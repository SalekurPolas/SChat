package com.salekur.schat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhotoActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;

    private CircleImageView InputCircleImage;
    private MaterialButton ButtonChoose, ButtonNext;
    private TextView TextBack;

    private FirebaseUser CurrentUser;
    private ProgressDialog loading;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        InputCircleImage = (CircleImageView) findViewById(R.id.photo_image_profile);
        ButtonChoose = (MaterialButton) findViewById(R.id.photo_button_choose);
        ButtonNext = (MaterialButton) findViewById(R.id.photo_button_next);
        TextBack = (TextView) findViewById(R.id.photo_text_back);

        loading = new ProgressDialog(this);
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        ButtonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAccessAndPickProfileImage();
            }
        });

        ButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileImage();
            }
        });

        TextBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToNameActivity();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(PhotoActivity.this);
                } else {
                    ShowMessage("Required permission");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageUri = result.getUri();
                Picasso.get().load(ImageUri).placeholder(R.drawable.profile_image).into(InputCircleImage);
            }
        }
    }

    private void UpdateProfileImage() {
        if (ImageUri == null) {
            ShowMessage("Please pick an image");
        } else {
            loading.setMessage("Uploading Image...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(ImageUri).build();
            CurrentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void CheckAccessAndPickProfileImage() {
        if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else{
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(PhotoActivity.this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser.getPhotoUrl() != null) {
            Picasso.get().load(CurrentUser.getPhotoUrl()).placeholder(R.drawable.profile_image).into(InputCircleImage);
        }
    }

    private void SendUserToNameActivity() {
        Intent nameIntent = new Intent(this, NameActivity.class);
        startActivity(nameIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void ShowMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
