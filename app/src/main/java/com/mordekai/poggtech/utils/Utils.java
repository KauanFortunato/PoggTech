package com.mordekai.poggtech.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
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

    public static void goToChat(FragmentActivity activity, Chat chat) {
        if (activity == null || chat == null) return;

        Bundle bundle = new Bundle();
        bundle.putInt("chat_with_id", chat.getChat_with());
        bundle.putInt("chat_id", chat.getChat_id());
        bundle.putString("chat_with_name", chat.getChat_with_name());
        bundle.putString("chat_with_last_name", chat.getChat_with_last_name());
        bundle.putInt("product_id", chat.getProduct_id());
        bundle.putString("product_title", chat.getProduct_title());
        bundle.putString("product_price", String.valueOf(chat.getProduct_price()));
        bundle.putString("image_product", chat.getCover_product());

        ChatDetailsFragment chatDetailsFragment = new ChatDetailsFragment();
        chatDetailsFragment.setArguments(bundle);


        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_bottom,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out_bottom
                )
                .replace(R.id.containerFrame, chatDetailsFragment)
                .addToBackStack(null)
                .commit();
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
}
