package com.mordekai.poggtech.presentation.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.RetrofitClient;
import com.mordekai.poggtech.data.repository.FirebaseUserRepository;
import com.mordekai.poggtech.data.repository.MySqlUserRepository;
import com.mordekai.poggtech.domain.FCMManager;
import com.mordekai.poggtech.domain.UserManager;
import com.mordekai.poggtech.presentation.ui.activity.LoginActivity;
import com.mordekai.poggtech.presentation.ui.activity.MainActivity;
import com.mordekai.poggtech.presentation.ui.bottomsheets.ChangeLanguageBottomSheet;
import com.mordekai.poggtech.presentation.ui.bottomsheets.ConfirmBottomSheet;
import com.mordekai.poggtech.utils.BottomNavVisibilityController;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.Utils;

public class SettingsFragment extends Fragment {

    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        startComponents(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void startComponents(View view) {
        AppCompatButton buttonLogout = view.findViewById(R.id.buttonLogout);
        AppCompatButton btnEditPerfil = view.findViewById(R.id.btnEditPerfil);
        AppCompatButton btnChangePass = view.findViewById(R.id.btnChangePass);
        AppCompatButton btnChangeTheme = view.findViewById(R.id.btnChangeTheme);
        AppCompatButton btnChangeLanguage = view.findViewById(R.id.btnChangeLanguage);
        ImageButton btn_back = view.findViewById(R.id.btn_back);

        btnEditPerfil.setOnClickListener(v -> {
            if (btnEditPerfil.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.navigate(R.id.action_settingsFragment_to_userConfigFragment);
        });

        buttonLogout.setOnClickListener(v -> {
            if (buttonLogout.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }

            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.logoutInfo));

            ConfirmBottomSheet confirmBottomSheet = new ConfirmBottomSheet(new ConfirmBottomSheet.OnClickConfirmed() {
                @Override
                public void onConfirmed() {
                    logOutUser();
                }
            });
            confirmBottomSheet.setArguments(bundle);
            confirmBottomSheet.show(requireActivity().getSupportFragmentManager(), confirmBottomSheet.getTag());

        });

        btnChangePass.setOnClickListener(v -> {
            if (btnChangePass.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.sendEmail));

            ConfirmBottomSheet confirmBottomSheet = new ConfirmBottomSheet(new ConfirmBottomSheet.OnClickConfirmed() {
                @Override
                public void onConfirmed() {
                    Utils utils = new Utils();
                    utils.resetUserPassword(user.getEmail(), getContext());
                }
            });
            confirmBottomSheet.setArguments(bundle);
            confirmBottomSheet.show(requireActivity().getSupportFragmentManager(), confirmBottomSheet.getTag());
        });

        btnChangeTheme.setOnClickListener(v -> {
            if (btnChangeTheme.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.navigate(R.id.action_settingsFragment_to_settingsAppearanceFragment);
        });

        btn_back.setOnClickListener(v -> {
            if (btn_back.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            NavController navController = ((MainActivity) requireActivity()).getCurrentNavController();
            navController.popBackStack();
        });

        btnChangeLanguage.setOnClickListener(v -> {
            if (btnChangeLanguage.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            ChangeLanguageBottomSheet bottomSheet = new ChangeLanguageBottomSheet();
            bottomSheet.show(requireActivity().getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    private void logOutUser() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        UserManager userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(apiService));
        FCMManager fcmManager = new FCMManager(apiService);

        fcmManager.removeToken(user.getUserId(), user.getToken(), new RepositoryCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse result) {
                Log.d("FCM_TOKEN", "Token removido com sucesso!");
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("FCM_TOKEN", "Erro ao remover token: " + t.getMessage());
            }
        });

        userManager.logoutUser();
        sharedPrefHelper.logOut();
        startActivity(new Intent(requireContext(), LoginActivity.class));
    }
}
