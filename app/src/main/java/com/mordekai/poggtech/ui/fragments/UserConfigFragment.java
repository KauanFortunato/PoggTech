package com.mordekai.poggtech.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.User;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.Objects;

public class UserConfigFragment extends Fragment {

    private TextView textName, textSurname, textContact, textEmail;
    private ImageButton btn_back;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_config, container, false);

        sharedPrefHelper = new SharedPrefHelper(requireContext());
        user = sharedPrefHelper.getUser();

        startComponents(view);

        if (user != null) {
            textName.setText(user.getName());
            textSurname.setText(user.getLastName());
            Log.d("UserConfigFragment", "User: " + user.getPhone());

            if (!Objects.equals(user.getPhone(), "")) {
                textContact.setText(user.getPhone());
            }
            textEmail.setText(user.getEmail());
        }

        return view;
    }

    private void startComponents(View view) {
        textName = view.findViewById(R.id.textName);
        textSurname = view.findViewById(R.id.textSurname);
        textContact = view.findViewById(R.id.textContact);
        textEmail = view.findViewById(R.id.textEmail);
        btn_back = view.findViewById(R.id.btn_back);

        btn_back.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
