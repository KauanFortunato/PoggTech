package com.mordekai.poggtech.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.fragments.HomeFragment;
import com.mordekai.poggtech.fragments.UserAccountFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.containerFrame, new HomeFragment())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.home) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFrame, new HomeFragment())
                        .addToBackStack(null)
                        .commit();
            } else if (item.getItemId() == R.id.cart) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFrame, new UserAccountFragment())
                        .addToBackStack(null)
                        .commit();
            } else if (item.getItemId() == R.id.favorite) {

            } else if (item.getItemId() == R.id.chat) {

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
}