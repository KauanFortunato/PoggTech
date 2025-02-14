package com.mordekai.poggtech.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.data.repository.FirebaseUserRepository;
import com.mordekai.poggtech.data.repository.MySqlUserRepository;
import com.mordekai.poggtech.domain.UserManager;
import com.mordekai.poggtech.ui.activity.LoginActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.utils.SnackbarUtil;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConfigFragment extends Fragment {

    private TextView textEmail;
    private EditText editName, editSurname, editContact;
    private ImageButton btn_back;
    private AppCompatButton buttonEditPersonInfo, buttonCancelPersonInfo, buttonLogout, buttonEditEmail, buttonResetPass;
    private SharedPrefHelper sharedPrefHelper;
    private UserManager userManager;
    private User user;
    private boolean isEditing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_config, container, false);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(apiService));

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        startComponents(view);

        if (user != null) {
            editName.setText(user.getName());
            editSurname.setText(user.getLastName());
            Log.d("UserConfigFragment", "User: " + user.getPhone());

            if (!Objects.equals(user.getPhone(), "")) {
                editContact.setText(user.getPhone());
            }
            textEmail.setText(user.getEmail());
        }

        buttonEditPersonInfo.setOnClickListener(v -> {
            if (buttonEditPersonInfo.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            if (!isEditing) {
                isEditing = true;
                buttonEditPersonInfo.setText(R.string.salvar);
                buttonCancelPersonInfo.setVisibility(View.VISIBLE);

                enableEditingMode(editName);
                enableEditingMode(editSurname);
                enableEditingMode(editContact);
            } else {
                isEditing = false;
                buttonEditPersonInfo.setText(R.string.edit);
                buttonCancelPersonInfo.setVisibility(View.GONE);

                resetToInitialState(editName);
                resetToInitialState(editSurname);
                resetToInitialState(editContact);

                // Atualizar o objeto user
                user.setName(editName.getText().toString());
                user.setLastName(editSurname.getText().toString());
                user.setPhone(editContact.getText().toString());

                // Salvar alterações no servidor ou localmente
                saveUserChanges();
            }
        });

        buttonCancelPersonInfo.setOnClickListener(v -> {
            if (buttonCancelPersonInfo.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
            // Cancelar alterações
            isEditing = false;
            buttonEditPersonInfo.setText(R.string.edit);
            buttonCancelPersonInfo.setVisibility(View.GONE);

            resetToInitialState(editName);
            resetToInitialState(editSurname);
            resetToInitialState(editContact);

            // Restaurar os dados antigos do utilizador
            if (user != null) {
                editName.setText(user.getName());
                editSurname.setText(user.getLastName());
                editContact.setText(user.getPhone());
            }
        });

        return view;
    }

    private void startComponents(View view) {
        editName = view.findViewById(R.id.editName);
        editSurname = view.findViewById(R.id.editSurname);
        editContact = view.findViewById(R.id.editContact);
        textEmail = view.findViewById(R.id.textEmail);

        btn_back = view.findViewById(R.id.btn_back);
        buttonEditPersonInfo = view.findViewById(R.id.buttonEditPersonInfo);
        buttonCancelPersonInfo = view.findViewById(R.id.buttonCancelPersonInfo);
        buttonEditEmail = view.findViewById(R.id.buttonEditEmail);
        buttonResetPass = view.findViewById(R.id.buttonResetPass);

        buttonLogout = view.findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(v -> {
            if (buttonLogout.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
            userManager.logoutUser();
            sharedPrefHelper.clearUser();
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        btn_back.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void enableEditingMode(EditText editText) {
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);

        editText.setBackgroundResource(R.drawable.rounded_edittext_info_edit);
        applyEditingStyle(editText);
    }

    private void resetToInitialState(EditText editText) {
        editText.setEnabled(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);

        editText.setBackgroundResource(R.drawable.rounded_edittext_info);
        applyStaticStyle(editText);
    }

    private void applyEditingStyle(EditText editText) {
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_medium));
        editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }

    private void applyStaticStyle(EditText editText) {
        editText.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_black));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
    }

    private void saveUserChanges() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiResponse<Void>> call = apiService.updateUser(user);
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    sharedPrefHelper.saveUser(user);

                    SnackbarUtil.showSuccessSnackbar(requireView(), "Usuário atualizado!", requireContext());
                    Log.d("API_SUCCESS", "Usuário atualizado com sucesso: " + response.body().getMessage());
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


