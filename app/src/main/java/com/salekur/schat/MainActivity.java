package com.salekur.schat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CircleImageView CircleProfileImage;
    private TextView ProfileName, ProfileEmail;
    private MaterialButton ButtonLogout;

    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();

        CircleProfileImage = (CircleImageView) findViewById(R.id.main_image_profile);
        ProfileName = (TextView) findViewById(R.id.main_text_name);
        ProfileEmail = (TextView) findViewById(R.id.main_text_email);
        ButtonLogout = (MaterialButton) findViewById(R.id.main_button_logout);

        ButtonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutUser();
            }
        });
    }

    private void LogoutUser() {
        mAuth.signOut();
        SendUserToLoginActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser != null) {
            if (!CurrentUser.isEmailVerified()) {
                SendUserToVerifyActivity();
            } else if (CurrentUser.getDisplayName().isEmpty()) {
                SendUserToNameActivity();
            } else if (CurrentUser.getPhotoUrl() == null) {
                SendUserToPhotoActivity();
            } else {
                UpdateUserInfo();
            }
        } else {
            SendUserToLoginActivity();
        }
    }

    private void UpdateUserInfo() {
        Picasso.get().load(CurrentUser.getPhotoUrl()).placeholder(R.drawable.profile_image).into(CircleProfileImage);
        ProfileName.setText(CurrentUser.getDisplayName());
        ProfileEmail.setText(CurrentUser.getEmail());
    }

    private void SendUserToVerifyActivity() {
        Intent verifyIntent = new Intent(this, LoginActivity.class);
        startActivity(verifyIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToNameActivity() {
        Intent nameIntent = new Intent(this, NameActivity.class);
        startActivity(nameIntent);
        finish();
    }

    private void SendUserToPhotoActivity() {
        Intent photoIntent = new Intent(this, PhotoActivity.class);
        startActivity(photoIntent);
        finish();
    }
}
