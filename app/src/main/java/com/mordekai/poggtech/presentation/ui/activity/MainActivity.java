package com.mordekai.poggtech.presentation.ui.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.presentation.ui.fragments.HeaderFragment;
import com.mordekai.poggtech.utils.AppConfig;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.MessageNotifier;
import com.mordekai.poggtech.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity implements HeaderFragment.HeaderListener, BottomNavVisibilityController {

    private HeaderFragment headerFragment;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private boolean forceBackToHome = false;

    public void setForceBackToHome(boolean force) {
        this.forceBackToHome = force;
    }

    public boolean shouldForceBackToHome() {
        return forceBackToHome;
    }

    public void showHeader() {
        findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
    }

    public void hideHeader() {
        findViewById(R.id.headerContainer).setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        AppConfig.initialize(this);

        // Setup Header
        headerFragment = new HeaderFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.headerContainer, headerFragment)
                .commit();

        // Setup Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.home, R.id.account, R.id.save, R.id.chat
        ).build();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.chat) {
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                prefs.edit().putBoolean("has_new_message", false).apply();
                clearChatBadge();
            }

            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

            // Controla a visibilidade do header manualmente
            if (itemId == R.id.home) {
                showHeader();
            } else {
                hideHeader();
            }

            return handled;
        });

        // Verifica conexão com XAMPP
        NetworkUtil.isConnectedXampp(isConnected -> {
            boolean isOffline = !isConnected || !NetworkUtil.isConnected(getApplicationContext());
            if (isOffline) {
                navController.navigate(R.id.offlineFragment);
                hideHeader();
                hideBottomNav();
            } else {
                showBottomNav();
                showHeader();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean hasNew = prefs.getBoolean("has_new_message", false);

        if (hasNew) {
            showChatBadge();
        }

        MessageNotifier.setListener(this::showChatBadge);
    }

    private void showChatBadge() {
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.chat);
        badge.setVisible(true);
        badge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
    }

    private void clearChatBadge() {
        bottomNavigationView.removeBadge(R.id.chat);
    }

    @Override
    public void showBackButton() {
        if (headerFragment != null) {
            headerFragment.showButtonBack();
        }
    }

    @Override
    public void hideBackButton() {
        if (headerFragment != null) {
            headerFragment.hideButtonBack();
        }
    }

    @Override
    public void closeSearchProd() {
        if (headerFragment != null) {
            headerFragment.closeSearchProd();
        }
    }

    @Override
    public void showBottomNav() {
        findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBottomNav() {
        findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
    }
}
