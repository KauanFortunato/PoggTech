package com.mordekai.poggtech.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.ui.fragments.HeaderFragment;
import com.mordekai.poggtech.ui.fragments.HomeFragment;
import com.mordekai.poggtech.ui.fragments.OfflineFragment;
import com.mordekai.poggtech.ui.fragments.UserAccountFragment;
import com.mordekai.poggtech.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        loadFragmentBasedOnNetwork();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.headerContainer, new HeaderFragment())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (!NetworkUtil.isConnected(this)) {
                selectedFragment = new OfflineFragment();
            } else {
                if (item.getItemId() == R.id.home) {
                    selectedFragment = new HomeFragment();
                    findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
                } else if (item.getItemId() == R.id.account) {
                    selectedFragment = new UserAccountFragment();
                    findViewById(R.id.headerContainer).setVisibility(View.GONE);
                } else if (item.getItemId() == R.id.cart) {
                    // ToDo: Adicionar tela de Carrinho
                } else if (item.getItemId() == R.id.favorite) {
                    // ToDo: Adicionar tela de Favoritos
                } else if (item.getItemId() == R.id.chat) {
                    // ToDo: Adicionar tela de Chat
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.containerFrame, selectedFragment)
                        .commit();
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void loadFragmentBasedOnNetwork() {
        Fragment fragment;
        if (NetworkUtil.isConnected(this)) {
            fragment = new HomeFragment();
        } else {
            fragment = new OfflineFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerFrame, fragment)
                .commit();

    }
}