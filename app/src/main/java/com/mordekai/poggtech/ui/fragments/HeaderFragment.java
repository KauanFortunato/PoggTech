package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;

public class HeaderFragment extends Fragment {

    ImageButton btnBackHeader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);

        btnBackHeader = view.findViewById(R.id.btnBackHeader);

        btnBackHeader.setOnClickListener(v -> {
            if(btnBackHeader.isHapticFeedbackEnabled()){
                btnBackHeader.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            btnBackHeader.setVisibility(View.GONE);
            getParentFragmentManager().popBackStack();
        });

        return view;
    }
}
