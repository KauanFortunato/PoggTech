package com.mordekai.poggtech.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.ui.activity.LoginActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.data.model.User;

public class UserAccountFragment extends Fragment {
    private TextView helloUser, numberAccount;
    private ImageButton buttonConfig, buttonMyPurchases, buttonMyAds;
    private FirebaseUser currentUser;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return null;
        }

        StartComponents(view);
        helloUser.setText("Olá, " + user.getName());
        numberAccount.setText("Número Da Conta: " + user.getUserId());
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void StartComponents(View view) {
        helloUser = view.findViewById(R.id.helloUser);
        numberAccount = view.findViewById(R.id.numberAccount);

        buttonConfig = view.findViewById(R.id.buttonConfig);
        buttonMyPurchases = view.findViewById(R.id.buttonMyPurchases);
        buttonMyAds = view.findViewById(R.id.buttonMyAds);

        buttonConfig.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        buttonMyPurchases.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        buttonMyAds.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(50).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });

        buttonConfig.setOnClickListener(v -> {
            UserConfigFragment userConfigFragment = new UserConfigFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerFrame, userConfigFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}
