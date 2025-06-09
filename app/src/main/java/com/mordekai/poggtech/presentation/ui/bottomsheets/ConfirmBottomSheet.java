package com.mordekai.poggtech.presentation.ui.bottomsheets;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mordekai.poggtech.R;

public class ConfirmBottomSheet extends BottomSheetDialogFragment {

    public interface OnClickConfirmed  {
        void onConfirmed();
    }

    private ConfirmBottomSheet.OnClickConfirmed listener;

    public ConfirmBottomSheet(ConfirmBottomSheet.OnClickConfirmed listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_confirm, container, false);

        AppCompatButton buttonConfirm = view.findViewById(R.id.buttonConfirm);
        AppCompatButton buttonCancel = view.findViewById(R.id.buttonCancel);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);

        if(getArguments() != null) {
            String text = getArguments().getString("title");
            textViewTitle.setText(text);
        }

        buttonConfirm.setOnClickListener(v -> {
            if(buttonConfirm.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            if (listener != null) {
                listener.onConfirmed();
            }
            dismiss();
        });

        buttonCancel.setOnClickListener(v -> {
            if(buttonCancel.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            dismiss();
        });

        closeButton.setOnClickListener(v -> {
            if(closeButton.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            dismiss();
        });

        return view;
    }
}
