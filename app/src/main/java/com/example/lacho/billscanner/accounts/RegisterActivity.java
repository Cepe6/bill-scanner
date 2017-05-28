package com.example.lacho.billscanner.accounts;

import android.content.Intent;
import android.media.MediaPlayer;
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

/**
 * Created by cepe6 on 27.05.17.
 */

public class RegisterActivity extends AppCompatActivity {
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
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.problem);
            final String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            MainActivity.mKinveyClient.user().create(username, password, new KinveyUserCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.v("Success", "Registered user with id " + user.getId());
                    CharSequence text = user.getUsername() + ", your account has been created.";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e("Failure", "Could not register! Error: " + throwable.getMessage());
                    mp.start();
                    CharSequence text = "Houston, we have a problem!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
