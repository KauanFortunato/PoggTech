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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.ui.fragments.HeaderFragment;
import com.mordekai.poggtech.ui.fragments.HomeFragment;
import com.mordekai.poggtech.ui.fragments.OfflineFragment;
import com.mordekai.poggtech.ui.fragments.UserAccountFragment;
import com.mordekai.poggtech.utils.AppConfig;
import com.mordekai.poggtech.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        AppConfig.initialize(this);
        showIpInputDialog();

        loadFragmentBasedOnNetwork();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.headerContainer, new HeaderFragment())
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (bottomNavigationView.isHapticFeedbackEnabled()) {
                bottomNavigationView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

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