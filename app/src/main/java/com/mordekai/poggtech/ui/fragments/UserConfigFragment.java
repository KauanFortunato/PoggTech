package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import com.mordekai.poggtech.data.remote.ApiService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConfigFragment extends Fragment {

    private TextView textEmail;
    private EditText editName, editSurname, editContact;
    private ImageButton btn_back;
    private AppCompatButton buttonEditPersonInfo, buttonEditEmail, buttonResetPass;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_config, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
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
            Call<ApiResponse> call = apiService.updateUser(user);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if (apiResponse.isSuccess() != null && apiResponse.isSuccess()) {
                            Log.d("API_SUCCESS", "Usuário atualizado com sucesso: " + apiResponse.getMessage());
                        } else {
                            Log.e("API_ERROR", "Erro na atualização: " + apiResponse.getMessage());
                        }
                    } else {
                        Log.e("API_ERROR", "Resposta não foi bem-sucedida. Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("API_FAILURE", "Falha ao comunicar com a API: " + t.getMessage());
                }
            });
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
        buttonEditEmail = view.findViewById(R.id.buttonEditEmail);
        buttonResetPass = view.findViewById(R.id.buttonResetPass);

        btn_back.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
