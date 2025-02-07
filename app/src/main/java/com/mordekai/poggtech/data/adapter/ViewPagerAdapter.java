package com.mordekai.poggtech.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mordekai.poggtech.ui.fragments.FavoritesFragment;
import com.mordekai.poggtech.ui.fragments.ShoppingCartFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new ShoppingCartFragment();
        } else {
            return new FavoritesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Quantidade de abas
    }
}
