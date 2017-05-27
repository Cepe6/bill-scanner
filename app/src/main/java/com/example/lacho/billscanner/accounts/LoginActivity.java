package com.example.lacho.billscanner.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lacho.billscanner.MainActivity;
import com.example.lacho.billscanner.R;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity {

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
            final String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            MainActivity.mKinveyClient.user().login(username, password, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.i("Success", "Logged in with user " + user.getUsername());
                    CharSequence text = "Welcome back, " + user.getUsername() + ".";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.i("Failure", "Could not login");
                    CharSequence text = "Houston, we have a problem!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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
