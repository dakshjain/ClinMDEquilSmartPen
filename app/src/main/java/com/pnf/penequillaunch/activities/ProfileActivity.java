package com.pnf.penequillaunch.activities;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.pnf.penequillaunch.others.Doctor;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.PatientDetailsDatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private ImageView docPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        Doctor.loadFromSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_profile);
        docPhoto = (ImageView) findViewById(R.id.docPhoto);
        TextView personName = (TextView) findViewById(R.id.person_name);
        TextView speciality = (TextView) findViewById(R.id.speciality);
        TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
        TextView email = (TextView) findViewById(R.id.email);
        TextView regid = (TextView) findViewById(R.id.regid);
        TextView degree = (TextView) findViewById(R.id.degree);
        TextView address = (TextView) findViewById(R.id.address);
        personName.setText(Doctor.firstName + " " + Doctor.lastName);
        phoneNumber.setText(Doctor.phone);
        email.setText(Doctor.email);
        regid.setText(new Doctor().getRegistrationID() + "(" + Doctor.registrationYear + ")");
        address.setText(Doctor.clinicAddress);
        speciality.setText("(" + Doctor.speciality + ")");
        degree.setText("(" + Doctor.degree + ")");
        getProfileImage();

        Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnNoOfPatients();
        if (c.moveToFirst()) {
            ((TextView) findViewById(R.id.total_patients)).setText(c.getInt(0) + "");
        }
        c = DotDetailsDatabaseHelper.getdatabasehelper().returnNoOfPagesToday();
        if (c != null && c.moveToFirst()) {
            ((TextView) findViewById(R.id.daily_patients)).setText(c.getInt(0) + "");
        }

    }

    private void getProfileImage() {
        File localfile = getBaseContext().getFileStreamPath("profile_pic.png");
        if (!localfile.exists()) {
            setProfileImage();
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
            docPhoto.setImageBitmap(bitmap);
        }
    }

    private void setProfileImage() {

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = mStorageRef.child("Doctors")
                .child(new Doctor().getRegistrationID())
                .child("Registration")
                .child("Profile_Picture.jpg");
        Glide.with(this.getApplicationContext()).using(new FirebaseImageLoader()).load(photoRef).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(docPhoto);
        saveDocPhoto(photoRef);
                //Glide.with(getApplicationContext()).load(photoRef).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(docPhoto);
    }
    void saveDocPhoto(StorageReference reference) {
        File localfile = this.getBaseContext().getFileStreamPath("profile_pic.png");
        reference.getFile(localfile);
    }

}
