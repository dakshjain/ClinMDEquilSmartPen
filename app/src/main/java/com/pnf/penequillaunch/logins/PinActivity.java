package com.pnf.penequillaunch.logins;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pnf.penequillaunch.test.BaseActivity;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.others.Mail;
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

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;


public class PinActivity extends AppCompatActivity implements View.OnClickListener {

    StringBuilder Pin = new StringBuilder("");
    TextInputEditText pinText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Doctor.loadFromSharedPreferences(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        setContentView(R.layout.activity_pin);
        pinText = (TextInputEditText) findViewById(R.id.pintext);
        pinText.setCursorVisible(false);
        loadDocDetails(Doctor.phone);
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

        ((TextView) findViewById(R.id.doctor_name)).setText("Dr. " + Doctor.lastName + "!");
        findViewById(R.id.forgot_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sending mail", Toast.LENGTH_LONG).show();
                sendMail();
            }
        });
    }

    private void sendMail() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] recipients = {Doctor.email};
        EmailAsyncTask email = new EmailAsyncTask();
        email.m = new Mail();
        email.m.set_from("info@clinmd.com");
        email.m.set_subject("Your Clinmd app pin");
        email.m.set_to(recipients);
        email.m.setBody("Hello Dr." + Doctor.lastName +
                ",\n\nYour pin is " + preferences.getString("Pin", "") +
                "\n\nThank you for using our services" +
                "\n For further help call us on +91-9535482864 or reply to this mail" +
                "\n\n Regards," +
                "\nTeam ClinMD");
        email.execute();
    }

    @Override
    public void onClick(View v) {
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

    private void loadDocDetails(final String enteredPhoneNumber) {
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Doctors");
        Query queryRef = myRef.orderByChild("registrationID").equalTo(new Doctor().getRegistrationID());
        queryRef.addChildEventListener(new ChildEventListener() {
            void load(DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                if (doctor != null) {
                    Log.d(" KEY", new Doctor().getRegistrationID());
                    if (doctor.getPhone().equalsIgnoreCase(enteredPhoneNumber)) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.phone), enteredPhoneNumber);
                        editor.apply();
                        Doctor.saveToSharedPreferences(getApplicationContext());
                        Log.d("KEYNAME", "NAME:" + new Doctor().toString());
                    }
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
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("prescription.png");

        savePrescription(reference);
    }

    private void checkPin() {


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (Pin.length() != 4)
            ((TextInputLayout) findViewById(R.id.pintextL)).setError("Pin should be 4 characters long");
        else if (Pin.toString().equalsIgnoreCase(preferences.getString("Pin", ""))) {
            startActivity(new Intent(PinActivity.this, BaseActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            ((TextInputLayout) findViewById(R.id.pintextL)).setError("Incorrect Pin");
            ((TextInputEditText) findViewById(R.id.pintext)).setText(null);
            Pin = new StringBuilder("");
            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_right);
            animation2.setDuration(250);
            animation2.setInterpolator(new BounceInterpolator());
            findViewById(R.id.numpad).startAnimation(animation2);

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
        if (pin.length() == 4) checkPin();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    void savePrescription(StorageReference reference) {
        Log.d("inside","save prescription");
        final File localfile = getBaseContext().getFileStreamPath("prescription.png");
        if (!localfile.exists()) {
            Log.d("inside","if");
            reference.getFile(localfile).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (localfile.exists())
                        localfile.delete();
                }
            });
        }
    }

    class EmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
        Mail m;

        EmailAsyncTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.d("EmailPin", m.get_from());
                Log.d("EmailPin", m.get_host());
                Log.d("EmailPin", m.get_user());
                Log.d("EmailPin", m.get_pass());
                Log.d("EmailPin", "Length" + m.get_to().length + "");
                Log.d("EmailPin", m.get_subject());
                Log.d("EmailPin", m.getBody());
                /*!_user.equals("") && !_pass.equals("") && _to.length > 0
                && !_from.equals("") && !_subject.equals("")
                && !_body.equals(""))*/
                if (m.send()) {
                    Log.d("EmailPin", "Check your email");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Check your email", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Log.d("EmailPin", "Failed");
                }
                return true;
            } catch (AuthenticationFailedException e) {
                e.printStackTrace();
                Log.d("EmailPin", "Authentication Failed");
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                Log.d("EmailPin", "Failed to send");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("EmailPin", "Unexpected Error Failed");
                return true;
            }
        }
    }


}
