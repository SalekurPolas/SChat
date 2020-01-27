package com.salekur.schat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout InputEmail, InputPassword;
    private MaterialButton ButtonRegister;
    private TextView TextLogin, TextHelp;

    private FirebaseAuth mAuth;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        loading = new ProgressDialog(this);

        InputEmail = (TextInputLayout) findViewById(R.id.register_input_email);
        InputPassword = (TextInputLayout) findViewById(R.id.register_input_password);
        ButtonRegister = (MaterialButton) findViewById(R.id.register_button);
        TextLogin = (TextView) findViewById(R.id.register_text_login);
        TextHelp = (TextView) findViewById(R.id.register_text_help);

        ButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRegister();
            }
        });

        TextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        TextHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToHelpActivity();
            }
        });
    }

    private void UserRegister() {
        String email = InputEmail.getEditText().getText().toString();
        String password = InputPassword.getEditText().getText().toString();

        if (email.isEmpty()) {
            InputEmail.setError("Email can't be empty");
        } else if (!email.endsWith("@salekur.com")) {
            InputEmail.setError("Invalid Email Address");
        } else if (password.isEmpty()) {
            InputPassword.setError("Password can't be empty");
        } else {
            loading.setMessage("Registering...");
            loading.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loading.dismiss();
                        SendUserToNameActivity();
                        finish();
                    } else {
                        loading.dismiss();
                        ShowMessage(task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void ShowMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToHelpActivity() {
        Intent helpIntent = new Intent(this, HelpActivity.class);
        startActivity(helpIntent);
    }

    private void SendUserToNameActivity() {
        Intent nameIntent = new Intent(this, NameActivity.class);
        startActivity(nameIntent);
    }
}
