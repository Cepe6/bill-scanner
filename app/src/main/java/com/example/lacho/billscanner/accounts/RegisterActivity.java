package com.example.lacho.billscanner.accounts;

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

/**
 * Created by cepe6 on 27.05.17.
 */

public class RegisterActivity extends AppCompatActivity {
    private Client mKinveyClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void createUser(View view) {
        EditText usernameEdit = (EditText)findViewById(R.id.register_username);
        EditText passwordEdit = (EditText)findViewById(R.id.register_password);
        if(usernameEdit == null || passwordEdit == null) {
            Log.e("ERROR", "Cannot register");
        } else {
            final String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            mKinveyClient.user().create(username, password, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.v("Success", "Registered user with id " + user.getId());
                    user.put("username", username);
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e("Failure", "Could not register! Error: " + throwable.getMessage());
                }
            });
        }
    }
}
