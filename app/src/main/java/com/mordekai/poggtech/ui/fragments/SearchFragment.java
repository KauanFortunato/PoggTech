package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.adapter.HistoryAdapter;
import com.mordekai.poggtech.ui.activity.MainActivity;
import com.mordekai.poggtech.utils.SharedPrefHelper;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class SearchFragment extends Fragment {

    private SharedPrefHelper sharedPrefHelper;
    private HistoryAdapter historyAdapter;
    private RecyclerView rvHistory;
    private EditText searchProd;
    private ImageButton delHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_home, container, false);
        sharedPrefHelper = new SharedPrefHelper(requireContext());
        List<String> history = sharedPrefHelper.getSearchHistory();
        Log.d("History", history.toString());

        startComponents(view);

        historyAdapter = new HistoryAdapter(history, sharedPrefHelper, historyItem -> {
            searchProd.setText(historyItem);
            searchProd.setSelection(historyItem.length());
        });

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHistory.setAdapter(historyAdapter);


        HeaderFragment.HeaderListener listener = (HeaderFragment.HeaderListener) getActivity();

        if (listener != null) {
            listener.showBackButton();
        }
        ((MainActivity) requireActivity()).setForceBackToHome(true);

        view.setOnClickListener(v -> {
            getActivity().findViewById(R.id.searchProd).clearFocus();
            getActivity().findViewById(R.id.listSuggestions).setVisibility(View.GONE);
            Utils.hideKeyboard(this);
        });

        return view;
    }

    public void startComponents(View view) {
        rvHistory = view.findViewById(R.id.rvHistory);
        rvHistory.setAdapter(historyAdapter);
        delHistory = view.findViewById(R.id.delHistory);
        searchProd = getActivity().findViewById(R.id.searchProd);
        getActivity().findViewById(R.id.bottomNavigationView).setVisibility(View.GONE);

        delHistory.setOnClickListener(v -> {
            if (delHistory.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.CONFIRM);
            }

            sharedPrefHelper.clearSearchHistory();
            historyAdapter.notifyDataSetChanged();
        });
    }
}
