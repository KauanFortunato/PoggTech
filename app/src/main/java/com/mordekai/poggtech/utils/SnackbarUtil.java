package com.mordekai.poggtech.utils;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mordekai.poggtech.R;

public class SnackbarUtil {

    public static void showSuccessSnackbar(View view, String message, Context context) {
        if (view.isHapticFeedbackEnabled()) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
        }
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.colorSuccess));
        customizeTextView(snackbar, context, R.color.colorOnSuccess); // Cor do texto branca
        snackbar.show();
    }

    public static void showErrorSnackbar(View view, String message, Context context) {
        if (view.isHapticFeedbackEnabled()) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT);
        }
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.colorError));
        customizeTextView(snackbar, context, R.color.colorOnError);
        snackbar.show();
    }

    public static void customizeTextView(Snackbar snackbar, Context context, int textColor) {
        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, textColor));
    }
}
