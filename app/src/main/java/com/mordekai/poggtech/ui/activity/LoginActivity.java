package com.mordekai.poggtech.ui.activity;

import static com.mordekai.poggtech.utils.NetworkUtil.isConnectedXampp;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mordekai.poggtech.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// Google auth
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import androidx.appcompat.widget.AppCompatButton;
import androidx.credentials.CredentialManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.repository.FirebaseUserRepository;
import com.mordekai.poggtech.data.repository.MySqlUserRepository;
import com.mordekai.poggtech.domain.UserManager;
import com.mordekai.poggtech.utils.AppConfig;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private TextView textNaoTemConta, forgotPassword;
    private FirebaseUser currentUser;
    private boolean isPasswordVisible = false;
    private AppCompatButton buttonGoogle;
    private ProgressBar buttonProgressGoogle;

    // Google Auth
    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AppConfig.initialize(this);
        showIpInputDialog();

        credentialManager = CredentialManager.create(this);

        AppCompatButton buttonLogin = findViewById(R.id.buttonLogin);
        ProgressBar buttonProgress = findViewById(R.id.buttonProgress);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("Auth", "Usuário já autenticado: " + currentUser.getEmail());
        } else {
            Log.d("Auth", "Nenhum usuário autenticado");
        }

        buttonProgressGoogle = findViewById(R.id.buttonProgressGoogle);
        buttonGoogle = findViewById(R.id.button_login_google); // Certifica-te de que tens um botão no XML
        buttonGoogle.setOnClickListener(view -> {
            if (buttonGoogle.isHapticFeedbackEnabled()) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
            buttonGoogle.setText(" ");
            buttonProgressGoogle.setVisibility(View.VISIBLE);

            initiateGoogleLogin();
        });

        // Verificar se o usuário já está logado
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Usuário já está logado, redirecionar para a tela principal
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        IniciarComponentes();

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

        // Verifica se tem conexão com o servidor
        isConnectedXampp(isConnected -> {
            if (isConnected) {
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

                        SnackbarUtil.showErrorSnackbar(getWindow().getDecorView().getRootView(), "Erro: " . concat(t.getMessage()), LoginActivity.this);
                        Log.e("Erro", "Erro ao fazer login: " + t.getMessage());
                    }
                });
            } else {
                buttonProgress.setVisibility(View.GONE);
                buttonLogin.setText(R.string.entrar);
                buttonLogin.setEnabled(true);
                SnackbarUtil.showErrorSnackbar(getWindow().getDecorView().getRootView(), "Não foi possível conectar ao servidor", LoginActivity.this);
            }
        });
    }

    private void initiateGoogleLogin() {
        Log.d("Auth", "Iniciando login com Google...");

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setServerClientId(getString(R.string.default_web_client_id)) // Define o ID do cliente do servidor
                .setFilterByAuthorizedAccounts(false) // Mostrar todas as contas do Google
                .build();

        GetCredentialRequest credentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                credentialRequest,
                null,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // Lógica para sucesso
                        Log.d("Auth", "getCredentialAsync() foi bem-sucedido!");
                        processCredentialResponse(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        // Lógica para erro
                        buttonGoogle.setText(R.string.googleLogin);
                        buttonProgressGoogle.setVisibility(View.GONE);
                        SnackbarUtil.showErrorSnackbar(getWindow().getDecorView().getRootView(), getString(R.string.erroGoogleAuth), LoginActivity.this);
                        Log.e("Auth", getString(R.string.erroGoogleAuth), e);
                    }
                }
        );
    }

    private void processCredentialResponse(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;

            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                String idToken = googleIdTokenCredential.getIdToken();

                Log.d("Auth", "ID Token obtido: " + idToken);

                if (idToken == null || idToken.isEmpty()) {
                    Log.e("Auth", "Erro: ID Token é nulo ou vazio!");
                    SnackbarUtil.showErrorSnackbar(getWindow().getDecorView().getRootView(), "Erro ao obter ID Token", LoginActivity.this);
                    return;
                }

                authInFirebase(idToken);
            } else {
                Log.e("Auth", "Tipo de credencial inesperado.");
            }
        } else {
            Log.e("Auth", "Tipo de credencial inesperado.");
        }
    }

    private void authInFirebase(String idToken) {
        UserManager userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(RetrofitClient.getRetrofitInstance().create(ApiService.class)));

        userManager.googleLogin(idToken, new RepositoryCallback<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("Auth", "Login bem-sucedido! Nome: " + user.getName() + ", Email: " + user.getEmail());

                SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(LoginActivity.this);
                sharedPrefHelper.saveUser(user);

                buttonGoogle.setText(R.string.googleLogin);
                buttonProgressGoogle.setVisibility(View.GONE);

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Auth", "Falha no login", t);
                Toast.makeText(LoginActivity.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void IniciarComponentes() {
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        textNaoTemConta = findViewById(R.id.textNaoTemConta);
        forgotPassword = findViewById(R.id.forgotPassword);

        inputPassword.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                int drawableEndPosition = inputPassword.getRight() - inputPassword.getCompoundDrawables()[2].getBounds().width();
                if(event.getRawX() >= drawableEndPosition) {
                    togglePasswordVisibility();
                    if (inputPassword.isHapticFeedbackEnabled()) {
                        v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
                    }
                    return true;
                }
            }
            return false;
        });

        textNaoTemConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textNaoTemConta.isHapticFeedbackEnabled()) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
                }
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forgotPassword.isHapticFeedbackEnabled()) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
                }
                showForgotPasswordDialog();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_key, 0, R.drawable.ic_eye_off, 0);
        } else {
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            inputPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_key, 0, R.drawable.ic_eye_on, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        inputPassword.setSelection(inputPassword.getText().length());
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                    AppConfig.setBaseUrl(LoginActivity.this, userInput);
                    RetrofitClient.resetRetrofit(); // Reinicia Retrofit para usar o novo IP
                    Toast.makeText(LoginActivity.this, "IP atualizado para " + userInput, Toast.LENGTH_SHORT).show();
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

    private void showForgotPasswordDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 10, 60, 10); // Ajustar as margens conforme necessário

        final EditText inputEmail = new EditText(this);
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setHint(R.string.digiteEmail);
        layout.addView(inputEmail);

        TextView infoText = new TextView(this);
        infoText.setText(R.string.mensagemRecuperarSenha);
        infoText.setTextSize(14); // Tamanho do texto
        infoText.setTextColor(getResources().getColor(R.color.textTertiary));
        infoText.setPadding(0, 25, 0, 0);
        infoText.setGravity(Gravity.CENTER);
        layout.addView(infoText);

        builder.setTitle(R.string.recuperarSenha);
        builder.setBackground(getResources().getDrawable(R.drawable.card_product_background, null));
        builder.setView(layout);

        builder.setPositiveButton(R.string.enviar, (dialog, which) -> {
            String email = inputEmail.getText().toString();
            resetUserPassword(email);
        });
        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetUserPassword(String email) {
        if (!TextUtils.isEmpty(email)) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.checkEmail, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.falhaCheckEmail, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(LoginActivity.this, "Digite um e-mail válido.", Toast.LENGTH_SHORT).show();
        }
    }

}
