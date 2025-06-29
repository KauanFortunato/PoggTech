package com.mordekai.poggtech.presentation.ui.bottomsheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.data.remote.request.UpdateEmailRequest;
import com.mordekai.poggtech.data.repository.FirebaseUserRepository;
import com.mordekai.poggtech.data.repository.MySqlUserRepository;
import com.mordekai.poggtech.domain.FCMManager;
import com.mordekai.poggtech.domain.UserManager;
import com.mordekai.poggtech.presentation.ui.activity.LoginActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailBottomSheet extends BottomSheetDialogFragment {

    private EditText editTextNewEmail, editTextPassword;
    private AppCompatButton buttonSendVerification, buttonConfirm;
    private FirebaseUser user;
    private User userSharedPref;
    private SharedPrefHelper sharedPrefHelper;
    private boolean isPasswordVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_change_email, container, false);

        editTextNewEmail = view.findViewById(R.id.editTextNewEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonSendVerification = view.findViewById(R.id.buttonSendVerification);
        buttonConfirm = view.findViewById(R.id.buttonConfirm);

        user = FirebaseAuth.getInstance().getCurrentUser();

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        userSharedPref = sharedPrefHelper.getUser();

        setupPasswordVisibilityToggle();

        buttonSendVerification.setOnClickListener(v -> reauthenticateAndSendVerification(view));
        buttonConfirm.setOnClickListener(v -> confirmEmailChange(view));

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordVisibilityToggle() {
        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_eye_off, 0);
        } else {
            editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_eye_on, 0);
        }
        editTextPassword.setSelection(editTextPassword.getText().length());
        vibrate();
        isPasswordVisible = !isPasswordVisible;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    private void reauthenticateAndSendVerification(View view) {
        String newEmail = editTextNewEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newEmail) || !android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            editTextNewEmail.setError("Por favor, introduz um email válido.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Introduz a tua palavra-passe.");
            return;
        }

        user.reload().addOnCompleteListener(reloadTask -> {
            if (!reloadTask.isSuccessful()) {
                Toast.makeText(getContext(), "Erro ao atualizar estado do utilizador.", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                if (authTask.isSuccessful()) {
                    user.verifyBeforeUpdateEmail(newEmail)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Email de verificação enviado!", Toast.LENGTH_LONG).show();
                                    startVerificationCountdown();
                                } else {
                                    Toast.makeText(getContext(), "Erro ao enviar verificação: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Palavra-passe incorreta.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void startVerificationCountdown() {
        buttonSendVerification.setEnabled(false);

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                buttonSendVerification.setText("A aguardar... (" + seconds + "s)");
            }

            public void onFinish() {
                buttonSendVerification.setEnabled(true);
                buttonSendVerification.setText("Reenviar verificação");
            }
        }.start();
    }

    private void confirmEmailChange(View view) {
        String confirmedEmail = editTextNewEmail.getText().toString().trim();

        if (!user.isEmailVerified()) {
            Toast.makeText(getContext(), "Verifica o teu novo email antes de confirmar.", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.updateEmail(new UpdateEmailRequest(userSharedPref.getUserId(), confirmedEmail));
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    userSharedPref.setEmail(confirmedEmail);
                    sharedPrefHelper.saveUser(userSharedPref);

                    if (isAdded() && getView() != null) {
                        SnackbarUtil.showSuccessSnackbar(getView(), "Utilizador atualizado!", requireContext());
                    }

                    Log.d("API_SUCCESS", "Email atualizado com sucesso: " + response.body().getMessage());
                    dismiss();
                } else {
                    Log.e("API_ERROR", "Erro na atualização: " + (response.body() != null ? response.body().getMessage() : "Erro desconhecido"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("API_FAILURE", "Falha ao comunicar com a API: " + t.getMessage());
            }
        });
    }
}
