package com.mordekai.poggtech.presentation.ui.bottomsheets;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.Utils;

public class MoreActionsMyProduct extends BottomSheetDialogFragment {

    public interface OnEditConfirmedListener  {
        void onEditConfirmedListener();
    }

    public interface OnDeleteConfirmedListener  {
        void onDeleteConfirmedListener();
    }

    public interface OnMarkAsSoldConfirmedListener {
        void onMarkAsSoldConfirmedListener();
    }

    private final OnEditConfirmedListener editListener;
    private final OnDeleteConfirmedListener deleteListener;
    private final OnMarkAsSoldConfirmedListener markAsSoldListener;
    private final boolean isAvailable;

    public MoreActionsMyProduct(boolean isAvailable, OnEditConfirmedListener editListener,
                                OnDeleteConfirmedListener deleteListener,
                                OnMarkAsSoldConfirmedListener markAsSoldListener) {
        this.editListener = editListener;
        this.deleteListener = deleteListener;
        this.markAsSoldListener = markAsSoldListener;
        this.isAvailable = isAvailable;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_more_actions_my_ad, container, false);

        AppCompatButton buttonEdit = view.findViewById(R.id.buttonEdit);
        AppCompatButton buttonCheck = view.findViewById(R.id.buttonCheck);
        AppCompatButton buttonDelete = view.findViewById(R.id.buttonDelete);

        if (isAvailable) {
            buttonCheck.setVisibility(View.VISIBLE);
        } else {
            buttonCheck.setVisibility(View.GONE);
        }

        buttonEdit.setOnClickListener(v -> {
            if(buttonEdit.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if (editListener != null) {
                editListener.onEditConfirmedListener();
            }
            dismiss();
        });

        buttonCheck.setOnClickListener(v -> {
            if(buttonCheck.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if (markAsSoldListener != null) {
                markAsSoldListener.onMarkAsSoldConfirmedListener();
            }
            dismiss();
        });

        buttonDelete.setOnClickListener(v -> {
            if(buttonDelete.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if (deleteListener != null) {
                deleteListener.onDeleteConfirmedListener();
            }
            dismiss();
        });

        return view;
    }
}
