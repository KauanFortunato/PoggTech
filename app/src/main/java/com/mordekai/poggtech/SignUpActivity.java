package com.mordekai.poggtech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText inputEmail, inputPassword, inputName, inputLastName, inputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Usuário já está logado, redirecionar para a tela principal
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Inicializar o FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        inputName = findViewById(R.id.inputNome);
        inputLastName = findViewById(R.id.inputSobrenome);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);

        findViewById(R.id.buttonSignUp).setOnClickListener(view -> {
            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            String name = inputName.getText().toString();
            String lastName = inputLastName.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            if (name.isEmpty()) {
                inputName.setError("Digite o nome");
                inputName.requestFocus();
                return;
            }

            if (lastName.isEmpty()) {
                inputLastName.setError("Digite o sobrenome");
                inputLastName.requestFocus();
                return;
            }

            if (!confirmPassword.equals(password)) {
                inputConfirmPassword.setError("As senhas não coincidem");
                inputConfirmPassword.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                inputEmail.setError("Digite o email");
                inputEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                inputPassword.setError("Digite a senha");
                inputPassword.requestFocus();
                return;
            }

            if (password.length() < 6) {
                inputPassword.setError("A senha deve ter no mínimo 6 caracteres");
                inputPassword.requestFocus();
                return;
            }

            CreateAccount(email, password);
        });
    }

    private void CreateAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Conta criada com sucesso
                    FirebaseUser user = mAuth.getCurrentUser();

                    // Exibir mensagem de sucesso
                    Toast.makeText(SignUpActivity.this,
                            "Conta criada com sucesso", Toast.LENGTH_SHORT).show();

                    Toast.makeText(SignUpActivity.this,
                            " ID: " + user.getUid(), Toast.LENGTH_SHORT).show();

                    Toast.makeText(SignUpActivity.this,
                            " Email: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                } else {
                    if (task.getException() != null) {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(SignUpActivity.this, "Erro: " + errorMessage, Toast.LENGTH_SHORT).show();

                        if (errorMessage.contains("The email address is alredy in use")) {
                            Toast.makeText(SignUpActivity.this,
                                    "Este email já possui uma conta cadastrada.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    "Falha na criação da conta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Erro desconhecido ao criar a conta", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
