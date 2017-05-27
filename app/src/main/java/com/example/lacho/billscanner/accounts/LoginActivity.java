package com.example.lacho.billscanner.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.example.lacho.billscanner.MainActivity;
import com.example.lacho.billscanner.R;

/**
 * Created by cepe6 on 27.05.17.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private void tryLogin() {
        EditText usernameEdit = (EditText)findViewById(R.id.login_username);
        String username = usernameEdit.getText().toString();
        EditText passwordEdit = (EditText)findViewById(R.id.login_password);
        String password = passwordEdit.getText().toString();


    }

    private void changeToRegister() {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }
}
