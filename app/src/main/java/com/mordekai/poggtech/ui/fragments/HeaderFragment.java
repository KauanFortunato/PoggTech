package com.mordekai.poggtech.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.Utils;

public class HeaderFragment extends Fragment {

    ImageButton btnBackHeader;
    EditText searchProd;
    private ConstraintLayout constraintLayout;
    private int larguraOriginalEditText = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);

        btnBackHeader = view.findViewById(R.id.btnBackHeader);
        searchProd = view.findViewById(R.id.searchProd);
        constraintLayout = (ConstraintLayout) searchProd.getParent();

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

        // Esconde o botão imediatamente
        btnBackHeader.setVisibility(View.GONE);
    }


    public interface HeaderListener {
        void showBackButton();
        void hideBackButton();
    }
}
