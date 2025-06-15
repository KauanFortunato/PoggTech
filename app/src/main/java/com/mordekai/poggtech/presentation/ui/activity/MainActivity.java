package com.mordekai.poggtech.presentation.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.presentation.ui.fragments.HeaderFragment;
import com.mordekai.poggtech.utils.AppConfig;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.MessageNotifier;
import com.mordekai.poggtech.utils.NetworkUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements HeaderFragment.HeaderListener, BottomNavVisibilityController {

    private HeaderFragment headerFragment;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private boolean forceBackToHome = false;

    private final Map<Integer, Integer> navHostMap = new HashMap<>();
    private final Map<Integer, NavController> navControllerMap = new HashMap<>();
    private int currentNavHostId = R.id.nav_host_home;

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
        SharedPreferences preferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        int savedTheme = preferences.getInt("selected_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);

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
        navHostMap.put(R.id.home, R.id.nav_host_home);
        navHostMap.put(R.id.save, R.id.nav_host_save);
        navHostMap.put(R.id.chat, R.id.nav_host_chat);
        navHostMap.put(R.id.account, R.id.nav_host_account);

        // Inicializa os NavControllers para cada aba
        navControllerMap.put(R.id.home, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_home)));

        navControllerMap.put(R.id.save, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_save)));

        navControllerMap.put(R.id.chat, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_chat)));

        navControllerMap.put(R.id.account, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_account)));


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.home, R.id.account, R.id.save, R.id.chat
        ).build();


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int tabId = item.getItemId();
            int targetFragmentId = navHostMap.get(tabId);

            if (tabId == R.id.chat) {
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                prefs.edit().putBoolean("has_new_message", false).apply();
                clearChatBadge();
            }

            // Oculta todos os hosts
            for (int hostId : navHostMap.values()) {
                findViewById(hostId).setVisibility(View.GONE);
            }

            // Mostra o host da aba selecionada
            findViewById(targetFragmentId).setVisibility(View.VISIBLE);
            currentNavHostId = targetFragmentId;

            NavController controller = navControllerMap.get(tabId);
            controller.popBackStack(controller.getGraph().getStartDestinationId(), false);

            if (tabId == R.id.home) {
                showHeader();
            } else {
                hideHeader();
            }

            return true;
        });

        // Verifica conexão com XAMPP
        NetworkUtil.isConnectedXampp(isConnected -> {
            boolean isOffline = !isConnected || !NetworkUtil.isConnected(getApplicationContext());

            NavController currentNav = getCurrentNavController();
            int currentDest = currentNav.getCurrentDestination() != null
                    ? currentNav.getCurrentDestination().getId()
                    : -1;

            if (isOffline) {
                if (currentDest != R.id.offlineFragment) {
                    currentNav.navigate(R.id.offlineFragment);
                }
                hideHeader();
                hideBottomNav();
            } else {
                showBottomNav();
                showHeader();
            }
        });


        MessageNotifier.setListener(this::showChatBadge);
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

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String language = prefs.getString("language", "pt");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(currentNavHostId);
        if (currentFragment != null) {
            NavController currentNav = NavHostFragment.findNavController(currentFragment);
            if (!currentNav.popBackStack()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showChatBadge() {
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.chat);
        badge.setVisible(true);
        badge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
    }

    private void clearChatBadge() {
        bottomNavigationView.removeBadge(R.id.chat);
    }

    public NavController getCurrentNavController() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(currentNavHostId);
        return NavHostFragment.findNavController(currentFragment);
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
