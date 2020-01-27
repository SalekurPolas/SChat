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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class NameActivity extends AppCompatActivity {
    private TextInputLayout InputName;
    private MaterialButton ButtonNext;
    private TextView TextBack;

    private FirebaseUser CurrentUser;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        InputName = (TextInputLayout) findViewById(R.id.name_input);
        ButtonNext = (MaterialButton) findViewById(R.id.name_button_next);
        TextBack = (TextView) findViewById(R.id.name_text_back);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        loading = new ProgressDialog(this);

        ButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserName();
            }
        });

        TextBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String name = CurrentUser.getDisplayName();

        if (name != null) {
            InputName.setHelperTextEnabled(true);
            InputName.setHelperText("Previous name: " + name);
        }
    }

    private void UpdateUserName() {
        String name = InputName.getEditText().getText().toString();

        if (name.isEmpty()) {
            InputName.setError("Name can't be Empty");
        } else {
            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            CurrentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loading.dismiss();
                        SendUserToPhotoActivity();
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

    private void SendUserToPhotoActivity() {
        Intent photoIntent = new Intent(this, PhotoActivity.class);
        startActivity(photoIntent);
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
