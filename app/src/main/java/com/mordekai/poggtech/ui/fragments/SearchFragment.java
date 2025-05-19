package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.CategoryAdapter;
import com.mordekai.poggtech.data.adapter.HistoryAdapter;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements CategoryAdapter.OnCategoryClickListerner, HeaderFragment.HeaderListener {

    private SharedPrefHelper sharedPrefHelper;
    private HistoryAdapter historyAdapter;
    private RecyclerView rvHistory, rvCategories;
    private TextView searchTitle;
    private EditText searchProd;
    private ImageView delHistory;
    private ProductManager productManager;
    private CategoryAdapter categoryAdapter;

    private HeaderFragment headerFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_home, container, false);
        headerFragment = (HeaderFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.headerContainer);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        List<String> history = sharedPrefHelper.getSearchHistory();
        Log.d("History", history.toString());

        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();

        startComponents(view);

        historyAdapter = new HistoryAdapter(history, sharedPrefHelper, historyItem -> {
            searchProd.setText(historyItem);
            searchProd.setSelection(historyItem.length());
        });

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHistory.setAdapter(historyAdapter);

        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();

        if (listener != null) {
            listener.showBackButton();
        }
        ((MainActivity) requireActivity()).setForceBackToHome(true);

        view.setOnClickListener(v -> {
            getActivity().findViewById(R.id.searchProd).clearFocus();
            getActivity().findViewById(R.id.listSuggestions).setVisibility(View.GONE);
            Utils.hideKeyboard(this);
        });

        if(history.isEmpty()){
            searchTitle.setVisibility(View.GONE);
            delHistory.setVisibility(View.GONE);
        }

        categoryAdapter = new CategoryAdapter(new ArrayList<>(), getContext(), this, R.layout.item_category);

        rvCategories.setLayoutManager(new GridLayoutManager(getContext(),4));
        rvCategories.setNestedScrollingEnabled(false);
        rvCategories.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvCategories.setAdapter(categoryAdapter);

        productManager = new ProductManager(RetrofitClient.getRetrofitInstance().create(ApiProduct.class));

        getCategories();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();
        historyAdapter.notifyDataSetChanged();
        ((MainActivity) requireActivity()).setForceBackToHome(true);
        showBackButton();

        Log.d("OnResume", "OnResume Called");
    }

    private void getCategories() {
        productManager.getAllCategories(new RepositoryCallback<List<Category>>() {

            @Override
            public void onSuccess(List<Category> result) {
                categoryAdapter.updateCategories(result);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_RESPONSE", "Erro ao buscar categorias", t);
            }
        });
    }

    private void startComponents(View view) {
        rvHistory = view.findViewById(R.id.rvHistory);
        delHistory = view.findViewById(R.id.delHistory);
        searchProd = getActivity().findViewById(R.id.searchProd);
        searchTitle = view.findViewById(R.id.searchTitle);
        rvCategories = view.findViewById(R.id.rvCategories);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        delHistory.setOnClickListener(v -> {
            if (delHistory.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            sharedPrefHelper.clearSearchHistory();
            historyAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        getActivity().findViewById(R.id.searchProd).clearFocus();
        getActivity().findViewById(R.id.listSuggestions).setVisibility(View.GONE);
        Utils.hideKeyboard(this);

        ((MainActivity) requireActivity()).setForceBackToHome(false);

        Bundle bundle = new Bundle();
        bundle.putParcelable("category", category);
        Fragment fragment = new CategoryFragment();
        fragment.setArguments(bundle);

        ((MainActivity) requireActivity()).setCurrentFragment(fragment);

        getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .replace(R.id.containerFrame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showBackButton() {
        Log.d("BackButton", "showBackButton called");
        if (headerFragment != null) {
            headerFragment.showButtonBack();
        }
    }

    @Override
    public void hideBackButton() {
        if (headerFragment != null) {
            headerFragment.hideButtonBack();
        }
    }

    @Override
    public void closeSearchProd() {
        if (headerFragment != null) {
            headerFragment.closeSearchProd();
        }
    }
}
