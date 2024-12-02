package com.mordekai.poggtech.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.model.User;

public class UserAccountActivity extends AppCompatActivity {
    private TextView helloUser, numberAccount;
    private ImageButton buttonConfig, buttonMyPurchases, buttonMyAds;
    private FirebaseUser currentUser;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        sharedPrefHelper = new SharedPrefHelper(UserAccountActivity.this);

        user = sharedPrefHelper.getUser();

        // Verificar se o usuário não está logado
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Usuário não está logado, redirecionar para a tela de login
            startActivity(new Intent(UserAccountActivity.this, LoginActivity.class));
            finish();
            return;
        }

        StartComponents();

        helloUser.setText("Olá, " + user.getName());
        numberAccount.setText("Número Da Conta: " + user.getUserId());
    }

    private void StartComponents() {
        helloUser = findViewById(R.id.helloUser);
        numberAccount = findViewById(R.id.numberAccount);
        buttonConfig = findViewById(R.id.buttonConfig);
    }
}
