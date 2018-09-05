package com.pnf.penequillaunch.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import com.clinmd.clinmd_app.others.PotentialUser;

public class ContactFormActivity extends AppCompatActivity {
    TextInputEditText mobileET, nameET, locationET;
    TextInputLayout mobileL, nameL, locationL;
    String mobile, name, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);

        mobileET = (TextInputEditText) findViewById(R.id.mobile);
        nameET = (TextInputEditText) findViewById(R.id.name);
        locationET = (TextInputEditText) findViewById(R.id.location);
        mobileL = (TextInputLayout) findViewById(R.id.mobileL);
        nameL = (TextInputLayout) findViewById(R.id.nameL);
        locationL = (TextInputLayout) findViewById(R.id.locationL);
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameET.getText().toString();
                mobile = mobileET.getText().toString();
                location = locationET.getText().toString();
                if (checkFields()) {
                    Toast.makeText(getApplicationContext(),"Thank You for your interest", Toast.LENGTH_LONG).show();
                    saveDetails();

                }
            }
        });
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();

            }
        });
    }

    private boolean checkFields() {
        boolean returnThis = true;

        mobileL.setError(null);
        nameL.setError(null);
        locationL.setError(null);
        if (!isValid(mobile)) {
            mobileL.setError("Please enter a valid phone number");
            returnThis = false;
        }

        if (TextUtils.isEmpty(mobile)) {
            mobileL.setError("Field can't be empty");
            returnThis = false;
        }

        if (name.length() < 3) {
            nameL.setError("Please enter a valid name");
            returnThis = false;
        }

        if (TextUtils.isEmpty(name)) {
            nameL.setError("Field can't be empty");
            returnThis = false;
        }

        if (TextUtils.isEmpty(location)) {
            nameL.setError("Field can't be empty");
            returnThis = false;
        }
        return returnThis;
    }

    private boolean isValid(String enteredPhoneNumber) {
        if (enteredPhoneNumber.length() > 0) {
            int i = Integer.parseInt(enteredPhoneNumber.charAt(0) + "");
            boolean valid = i >= 7;
            return (enteredPhoneNumber.length() == 10) && valid;
        } else return false;
    }

    void saveDetails() {

        DatabaseReference potentialReference = FirebaseDatabase.getInstance().getReference().child("Potential Users");
       /* PotentialUser user = new PotentialUser(name, mobile, location);
        potentialReference.push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ContactFormActivity.this.finish();
            }
        });*/
       finish();
    }

}
