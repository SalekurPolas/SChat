package com.salekur.schat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout InputUsername, InputPassword;
    private MaterialButton ButtonLogin;
    private TextView TextRegister, TextHelp;

    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        loading = new ProgressDialog(this);

        InputUsername = (TextInputLayout) findViewById(R.id.login_input_username);
        InputPassword = (TextInputLayout) findViewById(R.id.login_input_password);
        ButtonLogin = (MaterialButton) findViewById(R.id.login_button);
        TextRegister = (TextView) findViewById(R.id.login_text_register);
        TextHelp = (TextView) findViewById(R.id.login_text_help);

        ButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();
            }
        });

        TextRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        TextHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToHelpActivity();
            }
        });
    }

    private void UserLogin() {
        String username = InputUsername.getEditText().getText().toString();
        String password = InputPassword.getEditText().getText().toString();

        if (username.isEmpty()) {
            InputUsername.setError("Username can't be empty");
        } else if (password.isEmpty()) {
            InputPassword.setError("Password can't be empty");
        } else {
            String email = username + "@salekur.com";

            loading.setMessage("Logging...");
            loading.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loading.dismiss();
                        CurrentUser = mAuth.getCurrentUser();

                        if (!CurrentUser.isEmailVerified()) {
                            SendUserToVerifyActivity();
                        } else if (CurrentUser.getDisplayName() == null) {
                            SendUserToNameActivity();
                        } else if (CurrentUser.getPhotoUrl() == null ) {
                            SendUserToPhotoActivity();
                        } else {
                            SendUserToMainActivity();
                        }
                    } else {
                        loading.dismiss();
                        ShowMessage(task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToPhotoActivity() {
        Intent photoIntent = new Intent(this, PhotoActivity.class);
        startActivity(photoIntent);
    }

    private void SendUserToNameActivity() {
        Intent nameIntent = new Intent(this, NameActivity.class);
        startActivity(nameIntent);
    }

    private void SendUserToVerifyActivity() {
        Intent verifyIntent = new Intent(this, VerifyActivity.class);
        startActivity(verifyIntent);
    }

    private void SendUserToHelpActivity() {
        Intent helpIntent = new Intent(this, HelpActivity.class);
        startActivity(helpIntent);
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void ShowMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
