package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.ViewPagerCartAdapter;

public class ProductManageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_product, container, false);

        getActivity().findViewById(R.id.headerContainer).setVisibility(View.GONE);
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        ViewPagerCartAdapter adapter = new ViewPagerCartAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(getString(R.string.carrinho));
            } else {
                tab.setText(getString(R.string.salvos));
            }
        }).attach();

        return view;
    }
}
