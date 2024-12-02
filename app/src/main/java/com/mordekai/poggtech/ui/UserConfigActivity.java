package com.mordekai.poggtech.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mordekai.poggtech.R;
import com.mordekai.poggtech.model.User;
import com.mordekai.poggtech.utils.SharedPrefHelper;

import java.util.Objects;

public class UserConfigActivity extends AppCompatActivity {

    private TextView textName, textSurname, textContact, textEmail;
    private SharedPrefHelper sharedPrefHelper;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);

        sharedPrefHelper = new SharedPrefHelper(UserConfigActivity.this);
        user = sharedPrefHelper.getUser();

        StartComponents();

        textName.setText(user.getName());
        textSurname.setText(user.getLastName());
        Log.d("UserConfigActivity", "User: " + user.getPhone());

        if (!Objects.equals(user.getPhone(), "")) {
            textContact.setText(user.getPhone());
        }

        textEmail.setText(user.getEmail());
    }

    private void StartComponents() {
        textName = findViewById(R.id.textName);
        textSurname = findViewById(R.id.textSurname);
        textContact = findViewById(R.id.textContact);
        textEmail = findViewById(R.id.textEmail);
    }
}
