package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;

public class SearchFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_home, container, false);

        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();
        if (listener != null) {
            listener.showBackButton();
        }

        return view;
    }
}
