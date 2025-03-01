package com.mordekai.poggtech.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.Utils;

public class HeaderFragment extends Fragment {

    ImageButton btnBackHeader;
    EditText searchProd;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);

        btnBackHeader = view.findViewById(R.id.btnBackHeader);
        searchProd = view.findViewById(R.id.searchProd);

        btnBackHeader.setOnClickListener(v -> {
            if (btnBackHeader.isHapticFeedbackEnabled()) {
                btnBackHeader.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            hideButtonBack();

            if (getActivity() != null) {
                Utils.hideKeyboard(this);
                searchProd.clearFocus();
            }

            getParentFragmentManager().popBackStack();
        });

        searchProd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showButtonBack();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFrame, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    public void showButtonBack() {
        if (btnBackHeader.getVisibility() == View.VISIBLE) return;

        int originalWidth = searchProd.getWidth();
        int newWidth = originalWidth - btnBackHeader.getWidth() - 24;

        ObjectAnimator animacaoEditText = ObjectAnimator.ofInt(searchProd, "width", newWidth);
        animacaoEditText.setDuration(300);
        animacaoEditText.setInterpolator(new DecelerateInterpolator());

        btnBackHeader.setVisibility(View.VISIBLE);
        ObjectAnimator animacaoBotao = ObjectAnimator.ofFloat(btnBackHeader, "translationX", -100f, 0f);
        animacaoBotao.setDuration(300);
        animacaoBotao.setInterpolator(new DecelerateInterpolator());

        AnimatorSet animacaoSet = new AnimatorSet();
        animacaoSet.playTogether(animacaoEditText, animacaoBotao);
        animacaoSet.start();
    }

    public void hideButtonBack() {
        if (btnBackHeader.getVisibility() == View.GONE) return;

        int larguraOriginal = searchProd.getWidth() + btnBackHeader.getWidth() + 24;

        ObjectAnimator animacaoEditText = ObjectAnimator.ofInt(searchProd, "width", larguraOriginal);
        animacaoEditText.setDuration(300);
        animacaoEditText.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator animacaoBotao = ObjectAnimator.ofFloat(btnBackHeader, "translationX", 0f, -100f);
        animacaoBotao.setDuration(300);
        animacaoBotao.setInterpolator(new DecelerateInterpolator());

        animacaoBotao.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                btnBackHeader.setVisibility(View.GONE);
            }
        });

        AnimatorSet animacaoSet = new AnimatorSet();
        animacaoSet.playTogether(animacaoEditText, animacaoBotao);
        animacaoSet.start();
    }

    public interface HeaderListener {
        void showBackButton();
        void hideBackButton();
    }
}
