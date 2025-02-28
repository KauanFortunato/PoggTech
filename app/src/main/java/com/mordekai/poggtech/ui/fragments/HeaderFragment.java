package com.mordekai.poggtech.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.Utils;

public class HeaderFragment extends Fragment {

    ImageButton btnBackHeader;
    EditText searchProd;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, container, false);

        btnBackHeader = view.findViewById(R.id.btnBackHeader);
        searchProd = view.findViewById(R.id.searchProd);

        btnBackHeader.setOnClickListener(v -> {
            if (btnBackHeader.isHapticFeedbackEnabled()) {
                btnBackHeader.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            btnBackHeader.setVisibility(View.GONE);

            // Evitar erro de null ao tentar esconder o teclado
            if (getActivity() != null) {
                Utils.hideKeyboard(getParentFragment());
            }

            getParentFragmentManager().popBackStack();
        });

        searchProd.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                btnBackHeader.setVisibility(View.VISIBLE);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFrame, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
