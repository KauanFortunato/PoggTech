package com.mordekai.poggtech.presentation.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.data.repository.FirebaseUserRepository;
import com.mordekai.poggtech.data.repository.MySqlUserRepository;
import com.mordekai.poggtech.domain.FCMManager;
import com.mordekai.poggtech.domain.UserManager;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.utils.SnackbarUtil;
import com.mordekai.poggtech.utils.Utils;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConfigFragment extends Fragment {

    private static final int AVATAR_PICK_CODE = 2000;
    private static final int AVATAR_PERMISSION_CODE = 2001;
    private Uri selectedAvatarUri;

    private TextView textEmail;
    private EditText editName, editContact;
    private ImageView providerLogin;
    private ImageButton btn_back;
    private AppCompatButton buttonEditPersonInfo, buttonCancelPersonInfo, buttonEditEmail;
    private LinearLayout containerEditImage;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    private UserManager userManager;
    private FCMManager fcmManager;
    private boolean isEditing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        fcmManager = new FCMManager(apiService);
        userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(apiService));

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        startComponents(view);

        if (user != null) {
            editName.setText(user.getName());
            Log.d("UserConfigFragment", "User: " + user.getPhone());

            if (!Objects.equals(user.getPhone(), "")) {
                editContact.setText(user.getPhone());
            }
            textEmail.setText(user.getEmail());
        }

        buttonEditPersonInfo.setOnClickListener(v -> {
            if (buttonEditPersonInfo.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if (!isEditing) {
                isEditing = true;
                buttonEditPersonInfo.setText(R.string.salvar);
                buttonCancelPersonInfo.setVisibility(View.VISIBLE);

                enableEditingMode(editName);
                enableEditingMode(editContact);

            } else {
                if(!editName.getText().toString().isEmpty()){

                    isEditing = false;
                    buttonEditPersonInfo.setText(R.string.edit);
                    buttonCancelPersonInfo.setVisibility(View.GONE);

                    resetToInitialState(editName);
                    resetToInitialState(editContact);

                    // Atualizar o objeto user
                    user.setName(editName.getText().toString());
                    user.setPhone(editContact.getText().toString());

                    // Salvar alterações no servidor ou localmente
                    saveUserChanges();
                } else {
                    if(editName.getText().toString().isEmpty()){
                        editName.setError("Campo obrigatório");
                    }
                }
            }
        });

        buttonCancelPersonInfo.setOnClickListener(v -> {
            if (buttonCancelPersonInfo.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            // Cancelar alterações
            isEditing = false;
            buttonEditPersonInfo.setText(R.string.edit);
            buttonCancelPersonInfo.setVisibility(View.GONE);

            resetToInitialState(editName);
            resetToInitialState(editContact);

            // Restaurar os dados antigos do utilizador
            if (user != null) {
                editName.setText(user.getName());
                editContact.setText(user.getPhone());
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AVATAR_PICK_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedAvatarUri = data.getData();

            // Mostrar preview
            ImageView avatarImageView = requireView().findViewById(R.id.userAvatar); // ou ShapeableImageView
            avatarImageView.setImageURI(selectedAvatarUri);

            // Enviar para o servidor
            uploadAvatarToServer(selectedAvatarUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AVATAR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickAvatarFromGallery();
            } else {
                SnackbarUtil.showErrorSnackbar(requireView(), "Permissão negada para acessar imagens", requireContext());
            }
        }
    }

    @SuppressLint("InlinedApi")
    private void startComponents(View view) {
        editName = view.findViewById(R.id.editName);
        editContact = view.findViewById(R.id.editContact);
        textEmail = view.findViewById(R.id.textEmail);
        providerLogin = view.findViewById(R.id.providerLogin);
        containerEditImage = view.findViewById(R.id.containerEditImage);
        AppCompatImageView userAvatar = view.findViewById(R.id.userAvatar);
        Utils.loadImageBasicAuth(userAvatar, user.getAvatar());

        btn_back = view.findViewById(R.id.btn_back);
        buttonEditPersonInfo = view.findViewById(R.id.buttonEditPersonInfo);
        buttonCancelPersonInfo = view.findViewById(R.id.buttonCancelPersonInfo);

        userAvatar.setOnClickListener(v -> {
            if (userAvatar.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if(isEditing) {
                openAvatarPicker();
            }
        });
        

        btn_back.setOnClickListener(v -> {
            if (btn_back.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
        });

        if(user.getIsGoogle()) {
            providerLogin.setImageResource(R.drawable.ic_google);
            providerLogin.setVisibility(View.VISIBLE);
        } else {
            providerLogin.setImageResource(R.drawable.nav_icon_persona);
            providerLogin.setVisibility(View.VISIBLE);
        }
    }

    private void enableEditingMode(EditText editText) {
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setClickable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        containerEditImage.setVisibility(View.VISIBLE);

        editText.setBackgroundResource(R.drawable.rounded_edittext_info_edit);
        editText.setTextSize(18);
        applyEditingStyle(editText);
    }

    private void resetToInitialState(EditText editText) {
        editText.setEnabled(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        containerEditImage.setVisibility(View.GONE);

        editText.setBackgroundResource(R.drawable.rounded_edittext_info);
        editText.setTextSize(18);
        applyStaticStyle(editText);
    }

    private void applyEditingStyle(EditText editText) {
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        editText.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_medium));
        editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }

    private void applyStaticStyle(EditText editText) {
        editText.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_black));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
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

    private void openAvatarPicker() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, AVATAR_PERMISSION_CODE);
        } else {
            pickAvatarFromGallery();
        }
    }

    private void pickAvatarFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selecionar Avatar"), AVATAR_PICK_CODE);
    }

    private void uploadAvatarToServer(Uri imageUri) {
        File file = Utils.getFileFromUri(requireContext(), imageUri);
        if (file == null) return;

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);
        RequestBody firebaseUid = RequestBody.create(MediaType.parse("text/plain"), user.getFireUid());

        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiResponse<String>> call = apiService.uploadUserAvatar(body, firebaseUid);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String avatarPath = response.body().getData();
                    user.setAvatar(avatarPath);
                    sharedPrefHelper.saveUser(user);
                    Utils.loadImageBasicAuth(requireView().findViewById(R.id.userAvatar), avatarPath);
                    SnackbarUtil.showSuccessSnackbar(requireView(), "Avatar atualizado com sucesso!", requireContext());
                } else {
                    SnackbarUtil.showErrorSnackbar(requireView(), "Erro ao enviar avatar", requireContext());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                SnackbarUtil.showErrorSnackbar(requireView(), "Falha ao comunicar com servidor", requireContext());
            }
        });
    }

}


