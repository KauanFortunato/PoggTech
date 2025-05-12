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
    private static final String SELECTED_TAB_KEY = "selected_tab";
    private boolean isCompraSelected = true;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        iniciarComponentes(view);

        fragmentManager = getChildFragmentManager();

        if (savedInstanceState != null) {
            isCompraSelected = savedInstanceState.getBoolean(SELECTED_TAB_KEY, true);
        }

        if(isCompraSelected){
            loadFragment(new ChatBuyFragment());
        } else {
            loadFragment(new ChatSellFragment());
        }

        updateButtonStyle(isCompraSelected);

        btnShop.setOnClickListener(v->{
            if(btnShop.isHapticFeedbackEnabled()){
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE);
            }

            updateButtonStyle(true);
            loadFragment(new ChatBuyFragment());
            isCompraSelected = true;
        });

        btnSell.setOnClickListener(v->{
            if(btnSell.isHapticFeedbackEnabled()){
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE);
            }

            isCompraSelected = false;
            updateButtonStyle(false);
            loadFragment(new ChatSellFragment());
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SELECTED_TAB_KEY, isCompraSelected);
    }

    private void loadFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
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
