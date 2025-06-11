package com.mordekai.poggtech.presentation.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.data.repository.FirebaseUserRepository;
import com.mordekai.poggtech.data.repository.MySqlUserRepository;
import com.mordekai.poggtech.domain.UserManager;
import com.mordekai.poggtech.utils.SharedPrefHelper;

public class SignUpActivity extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageButton btnBack;
    private EditText inputEmail, inputPassword, inputName, inputLastName, inputConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Verificar se o usuário já está logado
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
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(view -> {
            if (btnBack.isHapticFeedbackEnabled()) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.buttonSignUp).setOnClickListener(view -> {

            String email = inputEmail.getText().toString();
            String password = inputPassword.getText().toString();
            String name = inputName.getText().toString();
            String lastName = inputLastName.getText().toString();
            String confirmPassword = inputConfirmPassword.getText().toString();

            User user = new User(name, lastName, email);

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

            CreateAccount(user, password);
        });
    }

    private void CreateAccount(User user, String password) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        UserManager userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(apiService));

        userManager.createUser(user, password, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User result) {
                Log.d("Sucesso", "Usuário criado com sucesso! Response: " + result.getName());

                SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(SignUpActivity.this);
                sharedPrefHelper.saveUser(result);

                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Erro", "Falha ao criar usuário", t);
                Toast.makeText(SignUpActivity.this, "Falha ao criar usuário", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
