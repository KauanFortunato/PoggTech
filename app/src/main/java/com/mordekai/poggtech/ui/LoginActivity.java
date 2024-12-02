package com.mordekai.poggtech.ui;

import com.mordekai.poggtech.R;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.mordekai.poggtech.network.ApiService;
import com.mordekai.poggtech.network.RetrofitClient;
import com.mordekai.poggtech.model.User;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private  FirebaseAuth mAuth;
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
            startActivity(new Intent(LoginActivity.this, UserAccountActivity.class));
            finish();
            return;
        }

        IniciarComponentes();

        // Inicializar Firebase auth
        mAuth = FirebaseAuth.getInstance();

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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

                        String firebase_uid = mAuth.getCurrentUser().getUid();
                        Log.d("Variaveis", "Uid: " + firebase_uid);

                        Call<User> call = apiService.getUser(firebase_uid);
                        call.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    User user = response.body();

                                    if (user.getError() != null) {
                                        Toast.makeText(LoginActivity.this, user.getError(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Usuário encontrado com sucesso
                                        String name = user.getName();
                                        String email = user.getEmail();
                                        Log.d("Variaveis", "Nome: " + name + ", Email: " + email);

                                        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(LoginActivity.this);

                                        sharedPrefHelper.saveUser(user);

                                        startActivity(new Intent(LoginActivity.this, UserAccountActivity.class));
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Erro na resposta do Servidor", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Log.d("Erro", "erro ao fazer login: " + t.getMessage());
                            }
                        });

                    } else {
                        // Falha no login
                        Toast.makeText(LoginActivity.this, "Falha no login", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void IniciarComponentes() {
        textNaoTemConta = findViewById(R.id.textNaoTemConta);
    }
}
