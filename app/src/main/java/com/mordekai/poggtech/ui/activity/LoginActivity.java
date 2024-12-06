package com.mordekai.poggtech.ui.activity;

import com.mordekai.poggtech.R;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private TextView textNaoTemConta;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

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

        findViewById(R.id.buttonLogin).setOnClickListener(view -> loginUser());

        textNaoTemConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void loginUser() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Digite o email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Digite a senha");
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        UserManager userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(apiService));

        userManager.loginUser(email, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                Log.d("Sucesso", "Usuário logado com sucesso! Response: " + result.getName());

                SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(LoginActivity.this);
                sharedPrefHelper.saveUser(result);

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Erro", "Erro ao fazer login: " + t.getMessage());
            }
        });
    }

    private void IniciarComponentes() {
        textNaoTemConta = findViewById(R.id.textNaoTemConta);
    }
}
