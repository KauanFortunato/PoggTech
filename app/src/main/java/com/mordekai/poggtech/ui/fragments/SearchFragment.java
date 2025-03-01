package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.utils.Utils;

public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_home, container, false);

        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();
        if (listener != null) {
            listener.showBackButton();
        }

        view.setOnClickListener(v -> {
            getActivity().findViewById(R.id.searchProd).clearFocus();
            getActivity().findViewById(R.id.listSuggestions).setVisibility(View.GONE);
            Utils.hideKeyboard(this);
        });

        return view;
    }

}
