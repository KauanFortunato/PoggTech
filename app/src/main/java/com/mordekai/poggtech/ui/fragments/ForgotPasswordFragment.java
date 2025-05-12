package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;

public class ForgotPasswordFragment extends Fragment {
    private EditText inputEmail;
    private Button buttonSend;
    private ProgressBar buttonProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        inicarComponentes(view);


        return view;
    }

    private void inicarComponentes(View view) {
        inputEmail = view.findViewById(R.id.inputEmail);
        buttonSend = view.findViewById(R.id.buttonSend);
        buttonProgress = view.findViewById(R.id.buttonProgress);
        buttonSend.setVisibility(View.GONE);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonProgress.setVisibility(View.VISIBLE);
                resetPassword();
            }
        });
    }

    private void resetPassword() {}
}
