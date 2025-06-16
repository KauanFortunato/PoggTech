package com.mordekai.poggtech.presentation.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.mordekai.poggtech.presentation.ui.fragments.ProductDetailsFragment;
import com.mordekai.poggtech.presentation.ui.fragments.ProductDetailsFragmentArgs;
import com.mordekai.poggtech.utils.AppConfig;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.MessageNotifier;
import com.mordekai.poggtech.utils.NetworkUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    private NavController.OnDestinationChangedListener currentNavListener;
    private NavController currentNavController;


    Set<Integer> fragmentsComHeaderEBottom = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.categoryFragment,
            R.id.searchedProducts
    ));

    Set<Integer> fragmentsComApenasBottom = new HashSet<>(Arrays.asList(
            R.id.accountFragment,
            R.id.productManageFragment,
            R.id.chatFragment
    ));

    Set<Integer> fragmentsComApenasHeader = new HashSet<>(Arrays.asList(
            R.id.productDetailsFragment,
            R.id.searchFragment
    ));

    Set<Integer> fragmentsSemHeaderENemBottom = new HashSet<>(Arrays.asList(
            R.id.chatDetailsFragment,
            R.id.offlineFragment,
            R.id.myAdsFragment,
            R.id.newAdFragment,
            R.id.orderDetailsFragment,
            R.id.ordersFragment,
            R.id.settingsFragment,
            R.id.settingsAppearanceFragment,
            R.id.userConfigFragment,
            R.id.walletFragment
    ));

    public void setForceBackToHome(boolean force) {
        this.forceBackToHome = force;
    }

    public boolean shouldForceBackToHome() {
        return forceBackToHome;
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
        Fragment existingHeader = getSupportFragmentManager().findFragmentById(R.id.headerContainer);
        if (existingHeader instanceof HeaderFragment) {
            headerFragment = (HeaderFragment) existingHeader;
        } else {
            headerFragment = new HeaderFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.headerContainer, headerFragment)
                    .commit();
        }

        // Setup Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navHostMap.put(R.id.homeFragment, R.id.nav_host_home);
        navHostMap.put(R.id.save, R.id.nav_host_save);
        navHostMap.put(R.id.chat, R.id.nav_host_chat);
        navHostMap.put(R.id.account, R.id.nav_host_account);

        // Inicializa os NavControllers para cada aba
        navControllerMap.put(R.id.homeFragment, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_home)));

        navControllerMap.put(R.id.save, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_save)));

        navControllerMap.put(R.id.chat, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_chat)));

        navControllerMap.put(R.id.account, NavHostFragment.findNavController(
                getSupportFragmentManager().findFragmentById(R.id.nav_host_account)));

        currentNavController = navControllerMap.get(R.id.homeFragment);
        currentNavListener = (navController, destination, arguments) -> {
            updateUiVisibilityBasedOnDestinationId(destination.getId());
        };
        currentNavController.addOnDestinationChangedListener(currentNavListener);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.account, R.id.save, R.id.chat
        ).build();


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int tabId = item.getItemId();
            int targetFragmentId = navHostMap.get(tabId);

            if (tabId == R.id.chat) {
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                prefs.edit().putBoolean("has_new_message", false).apply();
                clearChatBadge();
            }

            // Remove listener do NavController antigo para evitar chamadas concorrentes
            if (currentNavController != null && currentNavListener != null) {
                currentNavController.removeOnDestinationChangedListener(currentNavListener);
            }

            // Oculta todos os hosts
            for (int hostId : navHostMap.values()) {
                findViewById(hostId).setVisibility(View.GONE);
            }

            // Mostra o host da aba selecionada
            findViewById(targetFragmentId).setVisibility(View.VISIBLE);
            currentNavHostId = targetFragmentId;

            currentNavController = navControllerMap.get(tabId);

            // Adiciona listener para o NavController novo
            currentNavController.addOnDestinationChangedListener(currentNavListener);

            // Reseta pilha da aba
            currentNavController.popBackStack(currentNavController.getGraph().getStartDestinationId(), false);

            // Atualiza UI para o destino atual do NavController
            if (currentNavController.getCurrentDestination() != null) {
                updateUiVisibilityBasedOnDestinationId(currentNavController.getCurrentDestination().getId());
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
                updateUiVisibilityBasedOnCurrentDestination();
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

        updateUiVisibilityBasedOnCurrentDestination();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleDeepLink(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Também tratar aqui caso o app esteja sendo aberto pela primeira vez via notificação
        handleDeepLink(getIntent());
    }

    private void handleDeepLink(Intent intent) {
        if (intent == null || intent.getData() == null) return;

        Uri data = intent.getData();
        String host = data.getHost();
        String path = data.getPath();

        if (data != null) {
            if ("https".equals(data.getScheme())) {
                String correctedUrl = data.toString().replaceFirst("https", "http");
                data = Uri.parse(correctedUrl);
            }

            if ("poggers.ddns.net".equals(data.getHost())) {
                List<String> segments = data.getPathSegments();
                // ["PoggTech-APIs", "public", "product", "{id}"]
                if (segments.size() >= 4 && "product".equals(segments.get(2))) {
                    try {
                        int productId = Integer.parseInt(segments.get(3));
                        navController.navigate(
                                R.id.productDetailsFragment,
                                new ProductDetailsFragmentArgs.Builder(productId).build().toBundle()
                        );
                    } catch (NumberFormatException e) {
                        Log.e("DeepLink", "ID inválido: " + segments.get(3));
                    }
                }
            }
        }


        // Outros tipos de link
        String pathLast = data.getLastPathSegment();
        if (pathLast == null) return;

        switch (pathLast) {
            case "chat":
                bottomNavigationView.setSelectedItemId(R.id.chat);
                break;
            case "home":
                bottomNavigationView.setSelectedItemId(R.id.homeFragment);
                break;
            case "save":
                bottomNavigationView.setSelectedItemId(R.id.save);
                break;
            case "account":
                bottomNavigationView.setSelectedItemId(R.id.account);
                break;
        }

        setIntent(null); // evitar reprocessar
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

    private void updateUiVisibilityBasedOnCurrentDestination() {
        NavController nav = getCurrentNavController();
        if (nav.getCurrentDestination() == null) return;

        int currentDest = nav.getCurrentDestination().getId();

        if (fragmentsComHeaderEBottom.contains(currentDest)) {
            showHeader();
            showBottomNav();
        } else if (fragmentsComApenasBottom.contains(currentDest)) {
            hideHeader();
            showBottomNav();
        } else if (fragmentsComApenasHeader.contains(currentDest)) {
            showHeader();
            hideBottomNav();
        } else if (fragmentsSemHeaderENemBottom.contains(currentDest)) {
            hideHeader();
            hideBottomNav();
        } else {
            // fallback seguro
            showHeader();
            showBottomNav();
        }
    }

    private void updateUiVisibilityBasedOnDestinationId(int destId) {
        if (fragmentsComHeaderEBottom.contains(destId)) {
            showHeader();
            showBottomNav();
        } else if (fragmentsComApenasBottom.contains(destId)) {
            hideHeader();
            showBottomNav();
        } else if (fragmentsComApenasHeader.contains(destId)) {
            showHeader();
            hideBottomNav();
        } else if (fragmentsSemHeaderENemBottom.contains(destId)) {
            hideHeader();
            hideBottomNav();
        }
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

    public void showHeader() {
        findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
    }

    public void hideHeader() {
        View header = findViewById(R.id.headerContainer);
        header.setVisibility(View.GONE);
        header.postDelayed(() -> header.setVisibility(View.GONE), 50);
    }

    @Override
    public void showBottomNav() {
        findViewById(R.id.bottomNavigationView).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBottomNav() {
        View nav = findViewById(R.id.bottomNavigationView);
        nav.setVisibility(View.GONE);
    }
}
