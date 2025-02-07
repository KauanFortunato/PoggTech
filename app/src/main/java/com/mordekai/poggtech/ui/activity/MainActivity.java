package com.mordekai.poggtech.ui.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.ui.fragments.CartFragment;
import com.mordekai.poggtech.ui.fragments.HeaderFragment;
import com.mordekai.poggtech.ui.fragments.HomeFragment;
import com.mordekai.poggtech.ui.fragments.OfflineFragment;
import com.mordekai.poggtech.ui.fragments.UserAccountFragment;
import com.mordekai.poggtech.utils.AppConfig;
import com.mordekai.poggtech.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment homeFragment, cartFragment, accountFragment, offlineFragment;
    private Fragment activeFragment;

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        AppConfig.initialize(this);
        showIpInputDialog();

        fragmentManager = getSupportFragmentManager();

        homeFragment = new HomeFragment();
        cartFragment = new CartFragment();
        accountFragment = new UserAccountFragment();
        offlineFragment = new OfflineFragment();

        if (NetworkUtil.isConnected(this)) {
            activeFragment = homeFragment;
        } else {
            activeFragment = offlineFragment;
        }

        fragmentManager.beginTransaction()
                .add(R.id.containerFrame, offlineFragment, "OFFLINE").hide(offlineFragment)
                .add(R.id.containerFrame, accountFragment, "ACCOUNT").hide(accountFragment)
                .add(R.id.containerFrame, cartFragment, "CART").hide(cartFragment)
                .add(R.id.containerFrame, homeFragment, "HOME")
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.headerContainer, new HeaderFragment())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (bottomNavigationView.isHapticFeedbackEnabled()) {
                bottomNavigationView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if(item.getItemId() == R.id.home){
                switchFragment(homeFragment);
                findViewById(R.id.headerContainer).setVisibility(View.VISIBLE);
            } else if (item.getItemId() == R.id.account){
                switchFragment(accountFragment);
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
            } else if (item.getItemId() == R.id.cart){
                switchFragment(cartFragment);
                findViewById(R.id.headerContainer).setVisibility(View.GONE);
            } else if (item.getItemId() == R.id.chat){
                // ToDo: Adicionar tela de Chat
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void switchFragment(Fragment newFragment) {
        if (newFragment != activeFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(activeFragment).show(newFragment).commit();
            activeFragment = newFragment;
        }
    }

    private void showIpInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar IP do Servidor");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(AppConfig.getBaseUrl().replace("http://", "").replace("/PoggTech-APIs/routes/", ""));
        builder.setView(input);

        builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    AppConfig.setBaseUrl(MainActivity.this, userInput);
                    RetrofitClient.resetRetrofit(); // Reinicia Retrofit para usar o novo IP
                    Toast.makeText(MainActivity.this, "IP atualizado para " + userInput, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
