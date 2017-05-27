package com.example.lacho.billscanner.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.lacho.billscanner.R;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

import java.io.IOException;

/**
 * Created by cepe6 on 27.05.17.
 */

public class LoginActivity extends AppCompatActivity {
    private Client mKinveyClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void tryLogin(View v) throws IOException {
        EditText usernameEdit = (EditText)findViewById(R.id.login_username);
        EditText passwordEdit = (EditText)findViewById(R.id.login_password);
        if(usernameEdit == null || passwordEdit == null) {
            Log.e("ERROR", "Could not login");
        } else {
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            mKinveyClient.user().login(username, password, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.i("Success", "Logged in with user " + user.getUsername());
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i("Failure", "Could not login");
                }
            });
        }
    }

    public void changeToRegister(View view) {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
        finish();
    }
}
