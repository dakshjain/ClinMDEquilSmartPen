package com.pnf.penequillaunch.logins;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.pnf.penequillaunch.test.BaseActivity;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.Doctor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class SetupPinActivity extends AppCompatActivity implements View.OnClickListener {

    StringBuilder Pin = new StringBuilder("");
    StringBuilder FirstPin = new StringBuilder("");
    TextInputEditText pinText;
    boolean firstTrial = true;
    void savePrescription(StorageReference reference) {
        final File localfile = getBaseContext().getFileStreamPath("prescription.png");
        if (!localfile.exists()) {
            reference.getFile(localfile).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (localfile.exists())
                        localfile.delete();
                }
            });
        }
    }

    private void loadDocDetails() {
        Doctor.loadFromSharedPreferences(getApplicationContext());
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Doctors").child("Doctor Id");
        String test_phone =  Doctor.phone;
        Query queryRef = myRef.orderByChild("phone").equalTo(Doctor.phone);
        queryRef.addChildEventListener(new ChildEventListener() {
            void load(DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                if (doctor != null) {
                    Doctor.saveToSharedPreferences(getApplicationContext());
                    Log.d(" KEY", new Doctor().getRegistrationID());
                    Log.d("KEYNAME", "NAME:" + doctor.toString());

                }
            }

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                load(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                load(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String test  = new Doctor().getRegistrationID();
        try {
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("Registration").child("prescription.png");
            savePrescription(reference);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        loadDocDetails();
        setContentView(R.layout.activity_setup_pin);
        pinText = (TextInputEditText) findViewById(R.id.pintext);
        pinText.setCursorVisible(false);
        findViewById(R.id.no0).setOnClickListener(this);
        findViewById(R.id.no1).setOnClickListener(this);
        findViewById(R.id.no2).setOnClickListener(this);
        findViewById(R.id.no3).setOnClickListener(this);
        findViewById(R.id.no4).setOnClickListener(this);
        findViewById(R.id.no5).setOnClickListener(this);
        findViewById(R.id.no6).setOnClickListener(this);
        findViewById(R.id.no7).setOnClickListener(this);
        findViewById(R.id.no8).setOnClickListener(this);
        findViewById(R.id.no9).setOnClickListener(this);
        findViewById(R.id.del).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        ((TextView) findViewById(R.id.doctor_name)).setText("Dr." + Doctor.lastName + "!");
    }

    @Override
    public void onClick(View v) {
        ((TextInputLayout) findViewById(R.id.pintextL)).setError(null);
        switch (v.getId()) {
            case R.id.del:
                delete();
                break;
            case R.id.next:
                checkPin();
                break;
            default:
                add((Button) v);
        }
    }


    private void add(Button v) {
        if (Pin.length() < 4) {
            Pin.append(v.getText().toString());
            showHidePin();
        }
        setText(Pin);
        Log.d("Pin", Pin.toString());
    }

    private void showHidePin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pinText.setTransformationMethod(new PasswordTransformationMethod());
            }
        }, 500);
        pinText.setTransformationMethod(new HideReturnsTransformationMethod());
    }

    void animateNumpad() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_to_left);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((TextView) findViewById(R.id.enter_pin)).setText("Please RE-ENTER your pin");
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_right);
                animation2.setDuration(250);
                findViewById(R.id.numpad).startAnimation(animation2);
                pinText.setText("");
                FirstPin = Pin;
                Pin = new StringBuilder("");
                firstTrial = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.numpad).startAnimation(animation);
    }

    private void checkPin() {
        if (Pin.length() != 4)
            ((TextInputLayout) findViewById(R.id.pintextL)).setError("Pin should be 4 characters long");
        else if (firstTrial)
            animateNumpad();
        else if (Pin.toString().equalsIgnoreCase(FirstPin.toString())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("PinIsSet", true);
            editor.putString("Pin", Pin.toString());
            editor.apply();
            startActivity(new Intent(SetupPinActivity.this, BaseActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();

        } else {
            ((TextInputLayout) findViewById(R.id.pintextL)).setError("Pins do not match");
            ((TextInputEditText) findViewById(R.id.pintext)).setText(null);
            Pin = new StringBuilder("");

        }
    }

    private void delete() {
        if (Pin.length() > 0) {
            Pin.deleteCharAt(Pin.length() - 1);
        }
        setText(Pin);
        Log.d("Pin", Pin.toString());
    }


    private void setText(StringBuilder pin) {
        pinText.setText(pin.toString());
    }
}
