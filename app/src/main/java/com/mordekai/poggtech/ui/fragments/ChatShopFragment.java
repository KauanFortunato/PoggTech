package com.mordekai.poggtech.ui.fragments;
import com.mordekai.poggtech.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class ChatShopFragment extends Fragment {

    public ChatShopFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_shop, container, false);
    }
}
