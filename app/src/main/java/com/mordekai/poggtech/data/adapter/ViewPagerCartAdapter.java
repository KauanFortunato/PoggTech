package com.mordekai.poggtech.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mordekai.poggtech.presentation.ui.fragments.SavedFragment;
import com.mordekai.poggtech.presentation.ui.fragments.CartFragment;

public class ViewPagerCartAdapter extends FragmentStateAdapter {
    public ViewPagerCartAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new CartFragment();
            case 1:
                return new SavedFragment();
            default:
                return new CartFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2; // Quantidade de abas
    }
}
