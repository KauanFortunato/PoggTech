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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
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
    private RecyclerView rvCategories;
    private TextView searchTitle;
    private EditText searchProd;
    private TextView delHistory;
    private ProductManager productManager;
    private CategoryAdapter categoryAdapter;
    private FlexboxLayout flexHistory;
    private LinearLayout containerHistory;


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

        refreshSearchHistory();

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

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
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

        if (!isVisible()) return;

        ((BottomNavVisibilityController) requireActivity()).hideBottomNav();

        refreshSearchHistory();

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
        containerHistory = view.findViewById(R.id.containerHistory);
        flexHistory = view.findViewById(R.id.flexHistory); // novo ID do layout
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
            refreshSearchHistory();
        });
    }

    private void refreshSearchHistory() {
        List<String> updatedHistory = sharedPrefHelper.getSearchHistory();
        flexHistory.removeAllViews();

        if (updatedHistory.isEmpty()) {
            containerHistory.setVisibility(View.GONE);
            return;
        } else {
            containerHistory.setVisibility(View.VISIBLE);
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String item : updatedHistory) {
            View chipView = inflater.inflate(R.layout.item_recently_searched, flexHistory, false);
            TextView chipText = chipView.findViewById(R.id.historyText);
            ImageButton delBtn = chipView.findViewById(R.id.dellSearch);

            chipText.setText(item);

            chipText.setOnClickListener(v -> {
                searchProd.setText(item);
                searchProd.setSelection(item.length());
            });

            delBtn.setOnClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                sharedPrefHelper.removeItemFromHistory(item);
                refreshSearchHistory();
            });

            flexHistory.addView(chipView);
        }
    }


    @Override
    public void onCategoryClick(Category category) {
        getActivity().findViewById(R.id.searchProd).clearFocus();
        getActivity().findViewById(R.id.listSuggestions).setVisibility(View.GONE);
        Utils.hideKeyboard(this);

        ((MainActivity) requireActivity()).setForceBackToHome(false);

        Bundle bundle = new Bundle();
        bundle.putParcelable("category", category);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.categoryFragment, bundle);
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
