package com.mordekai.poggtech.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.utils.SharedPrefHelper;

public class HomeFragment extends Fragment {

    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        iniciarComponentes(view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void iniciarComponentes(View view) {
        ImageButton perfilButton = view.findViewById(R.id.perfil);
        ImageButton menuLateralButton = view.findViewById(R.id.menuLateral);

        perfilButton.setOnTouchListener((v, event) -> {
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

        menuLateralButton.setOnTouchListener((v, event) -> {
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

        perfilButton.setOnClickListener(v -> {
            UserAccountFragment userAccountFragment = new UserAccountFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerFrame, userAccountFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

}
