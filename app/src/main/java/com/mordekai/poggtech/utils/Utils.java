package com.mordekai.poggtech.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Chat;
import com.mordekai.poggtech.ui.fragments.ChatDetailsFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static void hideKeyboard(Fragment fragment) {
        View view = fragment.getView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) fragment.requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void goToChat(Fragment fragment, Chat chat) {
        if (fragment == null || chat == null) return;

        Bundle bundle = new Bundle();
        bundle.putInt("chat_with_id", chat.getChat_with());
        bundle.putInt("chat_id", chat.getChat_id());
        bundle.putString("chat_with_name", chat.getChat_with_name());
        bundle.putString("chat_with_last_name", chat.getChat_with_last_name());
        bundle.putInt("product_id", chat.getProduct_id());
        bundle.putString("product_title", chat.getProduct_title());
        bundle.putString("product_price", String.valueOf(chat.getProduct_price()));
        bundle.putString("image_product", chat.getCover_product());

        NavController navController = NavHostFragment.findNavController(fragment);
        navController.navigate(R.id.action_chatFragment_to_chatDetailsFragment, bundle);
    }

    public static File getFileFromUri(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("image", ".jpg", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadImageBasicAuth(ImageView imageView, String imageUrl) {
        String credentials = "admin" + ":" + "D7b@IRia";

        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                .addHeader("Authorization", auth)
                .build());

        Glide.with(imageView.getContext())
                .load(glideUrl)
                .placeholder(R.drawable.exemplo_ft3)
                .error(R.drawable.placeholder_image_error)
                .into(imageView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void showForgotPasswordDialog(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 10, 60, 10); // Ajustar as margens conforme necessário

        final EditText inputEmail = new EditText(context);
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setHint(R.string.digiteEmail);
        layout.addView(inputEmail);

        TextView infoText = new TextView(context);
        infoText.setText(R.string.mensagemRecuperarSenha);
        infoText.setTextSize(14); // Tamanho do texto
        infoText.setTextColor(context.getResources().getColor(R.color.textTertiary));
        infoText.setPadding(0, 25, 0, 0);
        infoText.setGravity(Gravity.CENTER);
        layout.addView(infoText);

        builder.setTitle(R.string.recuperarSenha);
        builder.setBackground(context.getResources().getDrawable(R.drawable.bg_card_product, null));
        builder.setView(layout);

        builder.setPositiveButton(R.string.enviar, (dialog, which) -> {
            String email = inputEmail.getText().toString();
            resetUserPassword(email, context);
        });
        builder.setNegativeButton(R.string.cancelar, (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void resetUserPassword(String email, Context context) {
        if (!TextUtils.isEmpty(email)) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, R.string.checkEmail, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, R.string.falhaCheckEmail, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(context, "Digite um e-mail válido.", Toast.LENGTH_SHORT).show();
        }
    }
}
