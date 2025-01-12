package com.mordekai.poggtech.ui.activity;

import com.mordekai.poggtech.R;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.repository.FirebaseUserRepository;
import com.mordekai.poggtech.data.repository.MySqlUserRepository;
import com.mordekai.poggtech.domain.UserManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private TextView textNaoTemConta, forgotPassword;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        AppCompatButton buttonLogin = findViewById(R.id.buttonLogin);
        ProgressBar buttonProgress = findViewById(R.id.buttonProgress);

        // Verificar se o usuário já está logado
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Usuário já está logado, redirecionar para a tela principal
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        IniciarComponentes();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        buttonLogin.setOnClickListener(view -> {
            if (buttonLogin.isHapticFeedbackEnabled()) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
            hideKeyboard();
            buttonLogin.setEnabled(false);
            buttonLogin.setText("");
            buttonProgress.setVisibility(View.VISIBLE);

            loginUser(buttonLogin, buttonProgress);
        });


    }

    private void loginUser(AppCompatButton buttonLogin, ProgressBar buttonProgress) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Digite o email");
            buttonProgress.setVisibility(View.GONE);
            buttonLogin.setText(R.string.entrar);
            buttonLogin.setEnabled(true);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Digite a senha");
            buttonProgress.setVisibility(View.GONE);
            buttonLogin.setText(R.string.entrar);
            buttonLogin.setEnabled(true);
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        UserManager userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(apiService));

        userManager.loginUser(email, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                buttonProgress.setVisibility(View.GONE);
                buttonLogin.setText(R.string.entrar);
                buttonLogin.setEnabled(true);

                Log.d("Sucesso", "Usuário logado com sucesso! Response: " + result.getName());
                SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(LoginActivity.this);
                sharedPrefHelper.saveUser(result);

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                buttonProgress.setVisibility(View.GONE);
                buttonLogin.setText(R.string.entrar);
                buttonLogin.setEnabled(true);

                SnackbarUtil.showErrorSnackbar(getWindow().getDecorView().getRootView(), "Senha ou email incorretos!", LoginActivity.this);
                Log.e("Erro", "Erro ao fazer login: " + t.getMessage());
            }
        });
    }

    private void IniciarComponentes() {
        textNaoTemConta = findViewById(R.id.textNaoTemConta);
        forgotPassword = findViewById(R.id.forgotPassword);

        textNaoTemConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
