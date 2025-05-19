package com.mordekai.poggtech.ui.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.ui.fragments.ProductManageFragment;
import com.mordekai.poggtech.ui.fragments.ChatFragment;
import com.mordekai.poggtech.ui.fragments.HeaderFragment;
import com.mordekai.poggtech.ui.fragments.HomeFragment;
import com.mordekai.poggtech.ui.fragments.OfflineFragment;
import com.mordekai.poggtech.ui.fragments.UserAccountFragment;
import com.mordekai.poggtech.utils.AppConfig;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.MessageNotifier;
import com.mordekai.poggtech.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity implements HeaderFragment.HeaderListener, BottomNavVisibilityController {

    private HeaderFragment headerFragment;
    private BottomNavigationView bottomNavigationView;
    private String notificationDestination = null;
    private final HomeFragment homeFragment = new HomeFragment();
    private final UserAccountFragment accountFragment = new UserAccountFragment();
    private final ProductManageFragment saveFragment = new ProductManageFragment();
    private final ChatFragment chatFragment = new ChatFragment();
    private final OfflineFragment offlineFragment = new OfflineFragment();


    private Fragment currentFragment;

    private boolean forceBackToHome = false;

    // Uso essa funcao para setar a varivel e no HeaderFragment vejo se é true para voltar
    // diretamente para o Home
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

        headerFragment = new HeaderFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.headerContainer, headerFragment)
                .commit();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        AppConfig.initialize(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrame, homeFragment, "HOME")
                .hide(homeFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrame, accountFragment, "ACCOUNT")
                .hide(accountFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrame, saveFragment, "SAVE")
                .hide(saveFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrame, chatFragment, "CHAT")
                .hide(chatFragment)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrame, offlineFragment, "OFFLINE")
                .hide(offlineFragment)
                .commit();

        // Define listener de navegação
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment targetFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                targetFragment = homeFragment;
                findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
            } else if (itemId == R.id.account) {
                targetFragment = accountFragment;
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
            } else if (itemId == R.id.save) {
                targetFragment = saveFragment;
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
            } else if (itemId == R.id.chat) {
                targetFragment = chatFragment;
                findViewById(R.id.headerContainer).setVisibility(View.GONE);

                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                prefs.edit().putBoolean("has_new_message", false).apply();
                clearChatBadge();
            }

            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (fragment != homeFragment &&
                        fragment != accountFragment &&
                        fragment != saveFragment &&
                        fragment != chatFragment &&
                        fragment != offlineFragment &&
                        fragment != headerFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
                }
            }

            if (targetFragment != null && targetFragment != currentFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(currentFragment)
                        .show(targetFragment)
                        .commit();
                currentFragment = targetFragment;
            }

            return true;
        });

        NetworkUtil.isConnectedXampp(isConnected -> {
            boolean isOffline = !isConnected || !NetworkUtil.isConnected(getApplicationContext());
            Fragment initialFragment = isOffline ? offlineFragment : homeFragment;

            getSupportFragmentManager().beginTransaction()
                    .show(initialFragment)
                    .commit();

            currentFragment = initialFragment;

            findViewById(R.id.headerContainer).setVisibility(
                    isOffline ? View.GONE : View.VISIBLE
            );

            if (!isOffline) {
                bottomNavigationView.setSelectedItemId(R.id.home);
            }

        });

        notificationDestination = getIntent().getStringExtra("navigate_to");
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean hasNew = prefs.getBoolean("has_new_message", false);

        if (hasNew) {
            showChatBadge();
        }

        MessageNotifier.setListener(() -> showChatBadge());
    }

    private void showChatBadge() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.chat);
        badge.setVisible(true);
        badge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
    }

    private void clearChatBadge() {
        bottomNavigationView.removeBadge(R.id.chat);
    }

    public void switchToFragment(String tag) {
        Fragment targetFragment = null;
        boolean shouldShowBottomNav = false;

        switch (tag) {
            case "HOME":
                targetFragment = homeFragment;
                findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
                shouldShowBottomNav = true;
                break;
            case "ACCOUNT":
                targetFragment = accountFragment;
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
                shouldShowBottomNav = true;
                break;
            case "SAVE":
                targetFragment = saveFragment;
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
                shouldShowBottomNav = true;
                break;
            case "CHAT":
                targetFragment = chatFragment;
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
                shouldShowBottomNav = true;
                break;
            case "OFFLINE":
                targetFragment = offlineFragment;
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
                break;
        }

        if (targetFragment != null && targetFragment != currentFragment) {
            getSupportFragmentManager().beginTransaction()
                    .hide(currentFragment)
                    .show(targetFragment)
                    .commit();
            currentFragment = targetFragment;
        }

        if (shouldShowBottomNav) {
            findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
        }

        if (tag.equals("HOME")) bottomNavigationView.setSelectedItemId(R.id.home);
        else if (tag.equals("ACCOUNT")) bottomNavigationView.setSelectedItemId(R.id.account);
        else if (tag.equals("SAVE")) bottomNavigationView.setSelectedItemId(R.id.save);
        else if (tag.equals("CHAT")) bottomNavigationView.setSelectedItemId(R.id.chat);
    }

    public void setCurrentFragment(Fragment fragment) {
        this.currentFragment = fragment;
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
        Log.d("BottomNav", "BottomNav Shown");
    }

    @Override
    public void hideBottomNav() {
        findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);
    }
}
