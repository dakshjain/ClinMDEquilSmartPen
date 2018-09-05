package com.pnf.penequillaunch.logins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.pnf.penequillaunch.test.MainActivity.MyPREFERENCES;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    StringBuilder Pin = new StringBuilder("");
    TextInputEditText pinText;
    private String mVerificationId = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new ChangeBounds());
            getWindow().setExitTransition(new ChangeBounds());
            getWindow().setSharedElementEnterTransition(new ChangeBounds());
            getWindow().setSharedElementExitTransition(new ChangeBounds());
        }
        setContentView(R.layout.activity_otp);
        pinText = (TextInputEditText) findViewById(R.id.pintext);
        pinText.setCursorVisible(false);
        /*
        checkboxes.add((CheckBox) findViewById(R.id.pin1));
        checkboxes.add((CheckBox) findViewById(R.id.pin2));
        checkboxes.add((CheckBox) findViewById(R.id.pin3));
        checkboxes.add((CheckBox) findViewById(R.id.pin4));*/
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
        mAuth = FirebaseAuth.getInstance();
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d("OTPActivity", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d("OTPActivity", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("OTPActivity", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                Log.d("mVerificationId",mVerificationId);
                mResendToken = token;

                // ...
            }
        };
        Log.d("phone_otp","+91"+Doctor.phone);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+Doctor.phone,        // phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt("pageid",10000);
        editor.commit();
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("OTPActivity", "signInWithCredential:success");

                            pinText.setText(credential.getSmsCode());
                            startActivity(new Intent(OTPActivity.this, PenConnectActivity.class));
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("loggedInOnce", true);
                            editor.apply();
                            finishAffinity();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d("OTPActivity", "signInWithCredential:failure", task.getException());
                            try {
                                Exception e = task.getException();
                                if (e != null) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                                }
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_LONG).show();
                                    // The verification code entered was invalid
                                }
                                throw e;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
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
        if (Pin.length() < 6) {
            Pin.append(v.getText().toString());
            showHidePin();
        }
        if (Pin.length() == 6) {
            checkPin();
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

    private void checkPin() {
        Log.d("mVerificationId",mVerificationId);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, Pin.toString());
        signInWithPhoneAuthCredential(credential);
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
