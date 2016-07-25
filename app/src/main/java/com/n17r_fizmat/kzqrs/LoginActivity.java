package com.n17r_fizmat.kzqrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextView signin, register;
    EditText username, password;
    ParseUser user;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            //TODO
            if (currentUser.get("name")==null || currentUser.getParseFile("avatar") == null) {
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(settingsIntent);
                finish();
            } else {
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                finish();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username = (EditText) findViewById(R.id.login_username);
        password  = (EditText) findViewById(R.id.login_password);
        signin = (TextView) findViewById(R.id.login_get_started);
        register = (TextView) findViewById(R.id.login_register);
        //set listeners
        signin.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.signInButton:
                ParseUser.logInInBackground(username.getText().toString(),
                        password.getText().toString(),
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {
                                    //startapp(v)
                                    Intent signinIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    signinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(signinIntent);
                                    finish();
                                } else {
                                    Log.d("ParseException", e.toString());
                                    Toast.makeText(LoginActivity.this,"Incorrect email or password. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                    //wrong email or password
                                }
                            }
                        });
                break;
            case R.id.signUpButton:
                user = new ParseUser();
                user.setUsername(username.getText().toString()); //email is username
                user.setPassword(password.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent registerIntent = new Intent(LoginActivity.this, SettingsActivity.class);
                            registerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(registerIntent);
                            finish();
                        } else {
                            Log.d("ParseException", e.toString());
                            Toast.makeText(LoginActivity.this, "This email is already registered!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}
