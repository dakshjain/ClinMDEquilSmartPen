package com.pnf.penequillaunch.logins;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.activities.ContactFormActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    String phoneNumber, enteredPhoneNumber;
    View v;
    TextInputEditText phone;
    TextInputLayout phoneL;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        v = inflater.inflate(R.layout.fragment_login, container, false);
        phone = (TextInputEditText) findViewById(R.id.phone_number);
        phoneL = (TextInputLayout) findViewById(R.id.phone_numberL);
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    phoneL.setError(null);
                }
            }
        });
        phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i) {
                    case KeyEvent.KEYCODE_NAVIGATE_NEXT:
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_NUMPAD_ENTER:
                        phoneCheck();
                }
                return false;
            }
        });
        phoneNumber = preferences.getString(getString(R.string.phone), null);
        phone.setText(phoneNumber);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_login);
        fab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredPhoneNumber = phone.getText().toString();
                phoneCheck();

            }
        });
        return v;
    }

    private boolean phoneCheck() {
        String enteredPhoneNumber = phone.getText().toString();
        if (enteredPhoneNumber.isEmpty()) {
            phoneL.setError("Please Enter your Mobile Number!");
            return false;
        } else if (!isValid(enteredPhoneNumber)) {
            phoneL.setError("Please Enter a Valid Mobile Number!");
            return false;
        } /*else if (!checkPhoneNumber(enteredPhoneNumber)) {
            phoneL.setError("Mobile Number is not Registered!");
            beginRegstration();
            return false;
        }*/

        savePhoneNumber();

        startActivityForResult(new Intent(getActivity(), LoggingInDialog.class).putExtra("Phno", enteredPhoneNumber), 1001);
/*
        startActivity(new Intent(getActivity(), BottomNavigationActivityBackup.class));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("FirstLaunch", false);
        editor.apply();
        getActivity().finish();*/
        return true;

    }

    private boolean isValid(String enteredPhoneNumber) {
        int i = Integer.parseInt(enteredPhoneNumber.charAt(0) + "");
        boolean valid = i >= 6;
        return (enteredPhoneNumber.length() == 10) && valid;
    }

    void savePhoneNumber() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.phone), phone.getText().toString());
        editor.apply();
    }
     void savePhoneNumber1() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.phone), "+91"+phone.getText().toString());
        editor.apply();
    }

    private void beginRegstration() {
        findViewById(R.id.registerPhone).setVisibility(View.VISIBLE);
        findViewById(R.id.registerPhone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePhoneNumber();
               startActivity(new Intent(getActivity(), ContactFormActivity.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1001) {

            if (data.getBooleanExtra("NumberExists", false)) {
                savePhoneNumber();
                startActivity(new Intent(getActivity(), OTPActivity.class).putExtra("isRegistration", false));
                getActivity().finish();
            } else {
                phoneL.setError("Mobile Number is not Registered!");
                beginRegstration();

            }
        }
    }

    View findViewById(int id) {
        return v.findViewById(id);
    }
}
