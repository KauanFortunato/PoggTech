package com.mordekai.poggtech.ui.fragments;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.TabChatAdapter;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class ChatFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabChatAdapter tabChatAdapter;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        viewPager = view.findViewById(R.id.viewPager);



        return view;
    }
}
