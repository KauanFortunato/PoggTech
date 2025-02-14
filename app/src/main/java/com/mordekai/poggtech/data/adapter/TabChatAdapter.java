package com.mordekai.poggtech.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.mordekai.poggtech.ui.fragments.ChatSellFragment;
import com.mordekai.poggtech.ui.fragments.ChatShopFragment;

public class TabChatAdapter extends FragmentStateAdapter {

    public TabChatAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ChatShopFragment();
            case 1:
                return new ChatSellFragment();
            default:
                return new ChatShopFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
