package com.mordekai.poggtech.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mordekai.poggtech.ui.fragments.FavoritesFragment;
import com.mordekai.poggtech.ui.fragments.ShoppingCartFragment;

public class ViewPagerCartAdapter extends FragmentStateAdapter {
    public ViewPagerCartAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ShoppingCartFragment();
            case 1:
                return new FavoritesFragment();
            default:
                return new ShoppingCartFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Quantidade de abas
    }
}
