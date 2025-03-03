package com.mordekai.poggtech.ui.fragments;
import android.view.HapticFeedbackConstants;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ChatFragment extends Fragment {

    private AppCompatButton btnShop, btnSell;
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        iniciarComponentes(view);
        fragmentManager = getChildFragmentManager();

        loadFragment(new ChatBuyFragment());

        btnShop.setOnClickListener(v->{
            if(btnShop.isHapticFeedbackEnabled()){
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            updateButtonStyle(true);
            loadFragment(new ChatBuyFragment());
        });

        btnSell.setOnClickListener(v->{
            if(btnSell.isHapticFeedbackEnabled()){
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            updateButtonStyle(false);
            loadFragment(new ChatSellFragment());
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void updateButtonStyle(boolean isCompraSelected) {
        if (isCompraSelected) {
            btnShop.setBackgroundResource(R.drawable.bg_button_selected);
            btnShop.setTextColor(getResources().getColor(R.color.colorOnPrimary));
            btnSell.setBackgroundResource(R.drawable.bg_button_unselected);
            btnSell.setTextColor(getResources().getColor(R.color.textPrimary));
        } else {
            btnSell.setBackgroundResource(R.drawable.bg_button_selected);
            btnSell.setTextColor(getResources().getColor(R.color.colorOnPrimary));
            btnShop.setBackgroundResource(R.drawable.bg_button_unselected);
            btnShop.setTextColor(getResources().getColor(R.color.textPrimary));
        }
    }

    private void iniciarComponentes(View view) {
        btnShop = view.findViewById(R.id.btnShop);
        btnSell = view.findViewById(R.id.btnSell);
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }
}
