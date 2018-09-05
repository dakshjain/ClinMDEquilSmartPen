package com.pnf.penequillaunch.logins;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoggingInDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_logging_dialog);
            checkPhoneNumber(getIntent().getStringExtra("Phno"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPhoneNumber(final String enteredPhoneNumber) {
        Log.d("LoginActivity phone", enteredPhoneNumber);
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("DocPhones");
        myRef.orderByValue().equalTo(enteredPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getValue(String.class) != null) {
                        Log.d("LoginActivity phone", d.getValue().toString());
                        Log.d("KEY", dataSnapshot.getKey());
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoggingInDialog.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(getString(R.string.phone), enteredPhoneNumber);
                        Doctor.phone = enteredPhoneNumber;
                        editor.apply();
                        Doctor.saveToSharedPreferences(getApplicationContext());
                        Intent intent = new Intent();
                        intent.putExtra("NumberExists", true);
                        setResult(RESULT_OK, intent);
                        finish();

                    }
                }

                Intent intent = new Intent();
                intent.putExtra("NumberExists", false);
                setResult(RESULT_OK, intent);
                finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LoginActivity Cancelled",databaseError.getMessage());
                Intent intent = new Intent();
                intent.putExtra("NumberExists", false);
                setResult(RESULT_CANCELED, intent);
                finish();

            }
        });

/*
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("KEY",dataSnapshot.getKey());
                if(dataSnapshot.getKey().equalsIgnoreCase(enteredPhoneNumber))
                {
                    myRef.removeEventListener(this);
                    Intent intent=new Intent();
                    intent.putExtra("NumberExists",true);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Intent intent=new Intent();
                intent.putExtra("NumberExists",false);
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
*/
    }

}
