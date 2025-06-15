package com.mordekai.poggtech.ui.bottomsheets;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mordekai.poggtech.R;

public class DeleteProductBottomSheet extends BottomSheetDialogFragment {

    public interface OnDeleteConfirmedListener  {
        void onDeleteConfirmed();
    }

    private OnDeleteConfirmedListener listener;

    public DeleteProductBottomSheet(OnDeleteConfirmedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_delete_product, container, false);

        AppCompatButton buttonConfirm = view.findViewById(R.id.buttonConfirm);
        AppCompatButton buttonCancel = view.findViewById(R.id.buttonCancel);
        ImageButton closeButton = view.findViewById(R.id.closeButton);

        buttonConfirm.setOnClickListener(v -> {
            if(buttonConfirm.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            if (listener != null) {
                listener.onDeleteConfirmed();
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
