package com.mordekai.poggtech.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.ui.activity.LoginActivity;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.NetworkUtil;
import com.mordekai.poggtech.utils.SnackbarUtil;

import androidx.appcompat.widget.AppCompatButton;

public class OfflineFragment extends Fragment {
    private AppCompatButton tryAgainButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline, container, false);

        inciarComponentes(view);

        tryAgainButton.setOnClickListener(v -> {
            NetworkUtil.isConnectedXampp(isConnected -> {
                if(isConnected) {
                    if (NetworkUtil.isConnected(requireContext())) {
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.containerFrame, new HomeFragment())
                                .commit();
                    }
                }
            });
        });

        return view;
    }

    private void inciarComponentes(View view){
        tryAgainButton = view.findViewById(R.id.tryAgainButton);
    }
}
