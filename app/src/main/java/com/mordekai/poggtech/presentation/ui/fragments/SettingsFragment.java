package com.mordekai.poggtech.presentation.ui.fragments;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
        AppCompatButton termsUse = view.findViewById(R.id.termsUse);
        AppCompatButton privacyPolicy = view.findViewById(R.id.privacyPolicy);
        AppCompatButton appVersion = view.findViewById(R.id.appVersion);
        AppCompatButton deleAccount = view.findViewById(R.id.deleAccount);
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

        privacyPolicy.setOnClickListener(v -> {
            String privacyText = "Aqui vai o conteúdo da política de privacidade...";
            showInfoDialog("Política de Privacidade", privacyText, getContext());
        });

        termsUse.setOnClickListener(v -> {
            String termsText = "Aqui vão os termos de uso...";
            showInfoDialog("Termos de Uso", termsText, getContext());
        });

        appVersion.setOnClickListener(v -> {
            if (appVersion.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            try {
                String versionName = requireContext()
                        .getPackageManager()
                        .getPackageInfo(requireContext().getPackageName(), 0)
                        .versionName;

                Toast.makeText(getContext(), getString(R.string.appVersion) + ": " + versionName, Toast.LENGTH_SHORT).show();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        });

        deleAccount.setOnClickListener(v -> {
            if (deleAccount.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            Utils utils = new Utils();
            utils.showDialog(view.getContext(),
                    "Tem a certeza que quer deletar sua conta?",
                    "Isso irá apagar tudo da sua conta incluindo: Produtos, reviews e wallet",
                    "Confirmar",
                    vws -> {
                        deleteUser(user.getFireUid());
                    });

        });
    }

    private void deleteUser(String firebaseUserId) {
        UserManager userManager = new UserManager(new FirebaseUserRepository(), new MySqlUserRepository(RetrofitClient.getRetrofitInstance().create(ApiService.class)));
        userManager.deleteUser(firebaseUserId , new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Conta deletada com sucesso!", Toast.LENGTH_SHORT).show();
                logOutUser();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "Erro ao deletar conta", Toast.LENGTH_SHORT).show();
            }
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

    private void showInfoDialog(String title, String message, Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        // ScrollView para suportar textos longos
        ScrollView scrollView = new ScrollView(context);
        scrollView.setPadding(60, 30, 60, 30); // mesmo padding que o anterior

        TextView textView = new TextView(context);
        textView.setText(message);
        textView.setTextSize(14);
        textView.setTextColor(context.getResources().getColor(R.color.textTertiary));
        textView.setGravity(Gravity.START);
        textView.setLineSpacing(1.4f, 1.4f);
        scrollView.addView(textView);

        builder.setTitle(title);
        builder.setBackground(context.getResources().getDrawable(R.drawable.bg_card_product, null));
        builder.setView(scrollView);

        builder.setPositiveButton("Fechar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
