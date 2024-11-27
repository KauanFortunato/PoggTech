package com.mordekai.poggtech.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordekai.poggtech.R;

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
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
                        // Usuário logado com sucesso
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, " ID: " + user.getUid(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, " Email: " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
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
