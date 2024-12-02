package com.mordekai.poggtech.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.network.ApiService;
import com.mordekai.poggtech.network.RetrofitClient;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText inputEmail, inputPassword, inputName, inputLastName, inputConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Verificar se o usuário já está logado
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Usuário já está logado, redirecionar para a tela principal
            startActivity(new Intent(SignUpActivity.this, UserAccountActivity.class));
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
                    ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

                    // Conta criada com sucesso
                    FirebaseUser user = mAuth.getCurrentUser();

                    Log.d("Variaveis", "Nome: " + inputName.getText().toString()
                            + "\tLastName: " + inputLastName.getText().toString()
                            + "\tEmail: " + inputEmail.getText().toString()
                            + "\tPassword: " + password
                            + "\tUid: " + user.getUid());

                    apiService.insertUser(user.getUid(), inputName.getText().toString(),
                            inputLastName.getText().toString(), inputEmail.getText().toString()).enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {

                                // TODO: 02/12/2024 Fazer com que quando a pessoa cadastrar ser colocado sharedPred

                                Log.d("Sucesso", "Pessoa cadastrada com sucesso! Response: " + response.body());
                                startActivity(new Intent(SignUpActivity.this, UserAccountActivity.class));
                                finish();
                            } else {
                                Log.e("Erro", "Erro ao cadastrar: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("Erro", "Erro na requisição: " + t.getMessage());
                        }
                    });

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
