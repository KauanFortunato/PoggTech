package com.mordekai.poggtech.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.remote.ApiProduct;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.domain.ProductManager;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class HeaderFragment extends Fragment {

    private ImageButton btnBackHeader;
    private EditText searchProd;
    private SharedPrefHelper sharedPrefHelper;

    private ConstraintLayout constraintLayout;

    private ListView listSuggestions;
    private FrameLayout overlayContainer;
    private ArrayAdapter<String> adapter;
    private boolean isUpdatingText = false;

    private ProductManager productManager;
    private ApiProduct apiProduct;


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);
        sharedPrefHelper = new SharedPrefHelper(requireContext());

        apiProduct = RetrofitClient.getRetrofitInstance().create(ApiProduct.class);
        productManager = new ProductManager(apiProduct);

        listSuggestions = getActivity().findViewById(R.id.listSuggestions);
        overlayContainer = getActivity().findViewById(R.id.overlayContainer);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        listSuggestions.setAdapter(adapter);

        btnBackHeader = view.findViewById(R.id.btnBackHeader);
        searchProd = view.findViewById(R.id.searchProd);
        constraintLayout = (ConstraintLayout) searchProd.getParent();

        btnBackHeader.setOnClickListener(v -> {
            if (btnBackHeader.isHapticFeedbackEnabled()) {
                btnBackHeader.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            closeSearchProd();
            handleBackNavigation();
        });

        searchProd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (!searchProd.getText().toString().isEmpty()) {
                    overlayContainer.setVisibility(View.VISIBLE);
                    listSuggestions.setVisibility(View.VISIBLE);
                }

                showButtonBack();

                FragmentManager fragmentManager = getParentFragmentManager();
                Fragment existingFragment = fragmentManager.findFragmentByTag("SEARCH_FRAGMENT");

                if (existingFragment != null) {
                    fragmentManager.beginTransaction().remove(existingFragment).commit();
                }

                searchProd.postDelayed(() -> {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(
                                    R.anim.fade_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.containerFrame, new SearchFragment(), "SEARCH_FRAGMENT")
                            .addToBackStack("search_fragment")
                            .commit();
                }, 100);
            }
        });

        searchProd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdatingText) {
                    return;
                }

                if (s.length() > 0) {
                    getSuggestions(s.toString());
                    searchProd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lupa, 0, R.drawable.ic_close_small, 0);
                } else {
                    searchProd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lupa, 0, 0, 0);
                    overlayContainer.setVisibility(View.GONE);
                    listSuggestions.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        searchProd.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchProd.getText().toString().trim();

            if (!query.isEmpty()) {
                isUpdatingText = true;

                overlayContainer.setVisibility(View.GONE);
                listSuggestions.setVisibility(View.GONE);

                if (getActivity() != null) {
                    Utils.hideKeyboard(this);
                    searchProd.clearFocus();
                }

                FragmentManager fragmentManager = getParentFragmentManager();

                while (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStackImmediate();
                }

                sharedPrefHelper.addSearchHistory(query);

                fragmentManager.beginTransaction()
                        .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                        )
                        .replace(R.id.containerFrame, new SearchedProductsFragment())
                        .addToBackStack("searched_products")
                        .commit();

                searchProd.postDelayed(() -> isUpdatingText = false, 100);
            }

            return true;
        });

        searchProd.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Drawable[] drawables = searchProd.getCompoundDrawables();

                if(drawables[2] != null) {
                    int drawableEndPosition = searchProd.getRight() - drawables[2].getBounds().width();

                    if(event.getRawX() >= drawableEndPosition) {
                        if (searchProd.isHapticFeedbackEnabled()) {
                            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
                        }
                        searchProd.setText("");
                        searchProd.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lupa, 0, 0, 0);
                        return true;
                    }
                }
            }
            return false;
        });

        listSuggestions.setOnItemClickListener((parent, v, position, id) -> {
            String selectedItem = adapter.getItem(position);

            isUpdatingText = true;

            searchProd.setText(selectedItem);
            searchProd.setSelection(selectedItem.length());

            overlayContainer.setVisibility(View.GONE);
            listSuggestions.setVisibility(View.GONE);

            if (getActivity() != null) {
                Utils.hideKeyboard(this);
                searchProd.clearFocus();
            }

            FragmentManager fragmentManager = getParentFragmentManager();

            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }

            sharedPrefHelper.addSearchHistory(searchProd.getText().toString().trim());

            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.fade_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.fade_out
                    )
                    .replace(R.id.containerFrame, new SearchedProductsFragment())
                    .addToBackStack("searched_products")
                    .commit();

            searchProd.postDelayed(() -> isUpdatingText = false, 100);
        });

        return view;
    }

    private void handleBackNavigation() {
        FragmentManager fragmentManager = getParentFragmentManager();
        boolean forceBackHome = ((MainActivity) requireActivity()).shouldForceBackToHome();

        if (forceBackHome) {
            searchProd.setText("");

            // Aqui limpo toda a backstack
            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }

            // vai para a HomeFragment sendo froçado
            ((MainActivity) requireActivity()).switchToFragment("HOME");

            ((MainActivity) requireActivity()).setForceBackToHome(false);

        } else {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                ((MainActivity) requireActivity()).switchToFragment("HOME");
            }
        }
    }

    public void showButtonBack() {
        if (btnBackHeader.getVisibility() == View.VISIBLE) return;

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        int btnWidth = 110;

        int margemOriginal = ((ConstraintLayout.LayoutParams) searchProd.getLayoutParams()).getMarginStart();
        int novaMargem = margemOriginal + btnWidth;

        btnBackHeader.setTranslationX(-btnWidth);

        ValueAnimator anim = ValueAnimator.ofInt(margemOriginal, novaMargem);
        anim.setDuration(200);
        anim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            constraintSet.setMargin(searchProd.getId(), ConstraintSet.START, value);
            constraintSet.applyTo(constraintLayout);
        });

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                constraintSet.setMargin(searchProd.getId(), ConstraintSet.START, margemOriginal);
                constraintSet.applyTo(constraintLayout);

                btnBackHeader.setVisibility(View.VISIBLE);
            }
        });

        anim.start();
    }

    public void hideButtonBack() {
        if (btnBackHeader.getVisibility() == View.GONE) return;

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        // Obtém a margem original do EditText
        int margemOriginal = 10; // Margem inicial padrão antes da animação

        // Define a margem de volta ao normal sem animação
        constraintSet.setMargin(searchProd.getId(), ConstraintSet.START, margemOriginal);
        constraintSet.applyTo(constraintLayout);

        btnBackHeader.setVisibility(View.GONE);
    }

    public void closeSearchProd() {
        hideButtonBack();
        overlayContainer.setVisibility(View.GONE);
        listSuggestions.setVisibility(View.GONE);

        if (getActivity() != null) {
            Utils.hideKeyboard(this);
            searchProd.clearFocus();
        }
    }

    private void getSuggestions(String query) {
        productManager.getSuggestions(query, new RepositoryCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                if (result != null && !result.isEmpty()) {
                    adapter.clear();
                    adapter.addAll(result);
                    adapter.notifyDataSetChanged();

                    overlayContainer.setVisibility(View.VISIBLE);
                    listSuggestions.setVisibility(View.VISIBLE);
                    listSuggestions.bringToFront();
                } else {
                    Log.d("API_RESPONSE", "Suggestions: " + result);
                    listSuggestions.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_ERROR", "Erro ao buscar sugestões: " + t.getMessage(), t);
                overlayContainer.setVisibility(View.GONE);
                listSuggestions.setVisibility(View.GONE);
            }
        });
    }

    public interface HeaderListener {
        void showBackButton();
        void hideBackButton();
        void closeSearchProd();
    }
}
