package com.mordekai.poggtech.presentation.ui.fragments;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.NetworkUtil;
import com.mordekai.poggtech.utils.SnackbarUtil;

import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.NavController;

public class OfflineFragment extends Fragment {
    private AppCompatButton tryAgainButton;
    ProgressBar buttonProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline, container, false);

        inciarComponentes(view);

        tryAgainButton.setOnClickListener(v -> {
            if(tryAgainButton.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }
            tryAgainButton.setEnabled(false);
            tryAgainButton.setText("");
            buttonProgress.setVisibility(View.VISIBLE);

            NetworkUtil.isConnectedXampp(isConnected -> {
                if(isConnected) {
                    if (NetworkUtil.isConnected(requireContext())) {
                        NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
                        navController.navigate(R.id.homeFragment);
                    } else {
                        if(tryAgainButton.isHapticFeedbackEnabled()) {
                            v.performHapticFeedback(HapticFeedbackConstants.REJECT);
                        }
                        SnackbarUtil.showErrorSnackbar(requireActivity().getWindow().getDecorView().getRootView(), "Não foi possível conectar ao servidor", requireContext());
                        tryAgainButton.setEnabled(true);
                        tryAgainButton.setText(R.string.tentarNovamente);
                    }
                } else {
                    if(tryAgainButton.isHapticFeedbackEnabled()) {
                        v.performHapticFeedback(HapticFeedbackConstants.REJECT);
                    }
                    SnackbarUtil.showErrorSnackbar(requireActivity().getWindow().getDecorView().getRootView(), "Não foi possível conectar ao servidor", requireContext());
                    tryAgainButton.setEnabled(true);
                    tryAgainButton.setText(R.string.tentarNovamente);
                }
            });
        });

        return view;
    }

    private void inciarComponentes(View view){
        tryAgainButton = view.findViewById(R.id.tryAgainButton);
        buttonProgress = view.findViewById(R.id.buttonProgress);
    }
}
