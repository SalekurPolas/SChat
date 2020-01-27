package com.salekur.schat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyActivity extends AppCompatActivity {
    private TextInputLayout InputEmail;
    private MaterialButton ButtonNext;

    private FirebaseUser CurrentUser;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        InputEmail = (TextInputLayout) findViewById(R.id.verify_input_email);
        ButtonNext = (MaterialButton) findViewById(R.id.verify_button_next);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        loading = new ProgressDialog(this);

        ButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerifyEmail();
            }
        });
    }

    private void SendVerifyEmail() {
        String email = InputEmail.getEditText().getText().toString();
        if (!email.equals(CurrentUser.getEmail())) {
            InputEmail.setError("Email didn't matched");
        } else {
            loading.setMessage("Sending email...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            CurrentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loading.dismiss();
                        SendUserToMainActivity();
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

    @Override
    protected void onStart() {
        super.onStart();

        if (!CurrentUser.getEmail().isEmpty()) {
            InputEmail.setHelperTextEnabled(true);
            InputEmail.setHelperText("Type: " + CurrentUser.getEmail());
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
