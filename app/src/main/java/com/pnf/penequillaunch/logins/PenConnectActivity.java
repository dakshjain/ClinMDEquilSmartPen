package com.pnf.penequillaunch.logins;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.equilsdk.Util;
import com.pnf.penequillaunch.others.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PenConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        loadDocDetails(preferences.getString(getString(R.string.phone), ""));
        if (preferences.getBoolean("PinIsSet", false)) {
            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
            finish();

        }
        setContentView(R.layout.activity_pen_connect);
        Doctor.loadFromSharedPreferences(getApplicationContext());

        findViewById(R.id.grant_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkPermissions();
            }
        });
        findViewById(R.id.turn_on_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int writeExternalPermissionCheck = ContextCompat.checkSelfPermission(PenConnectActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (writeExternalPermissionCheck == PackageManager.PERMISSION_DENIED) {
                        Util.showToast(getApplicationContext(), "Allow Storage Access to Proceed");
                    }else
                        startActivity(new Intent(PenConnectActivity.this, SetupPinActivity.class));
                } else
                    startActivity(new Intent(PenConnectActivity.this, SetupPinActivity.class));
            }
        });

    }

    public void chkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int writeExternalPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalPermissionCheck == PackageManager.PERMISSION_DENIED) {
                ArrayList<String> permissions = new ArrayList<>();
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1001);
            }
        }
    }
    private void loadDocDetails(final String enteredPhoneNumber) {
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Doctors").child("Doctor Id");
        myRef.orderByChild("phone").equalTo(enteredPhoneNumber).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    Doctor doctor = d.getValue(Doctor.class);
                    if (doctor != null) {
                        Log.d("KEY", doctor.getPhone());
                        if (doctor.getPhone().equalsIgnoreCase(enteredPhoneNumber)) {
                            myRef.removeEventListener(this);
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PenConnectActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(getString(R.string.phone), getIntent().getStringExtra("Phno"));
                            editor.apply();
                            Intent intent = new Intent();
                            intent.putExtra("NumberExists", true);
                            intent.putExtras(doctor.toBundle());
                            Doctor.saveToSharedPreferences(getApplicationContext());
                        }
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Intent intent = new Intent();
                intent.putExtra("NumberExists", false);

            }
        });
    }

}