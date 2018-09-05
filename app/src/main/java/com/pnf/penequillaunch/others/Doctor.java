package com.pnf.penequillaunch.others;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Doctor {
    public static String registrationID = "";
    public static String registrationYear = "";
    public static String email = "";
    public static String firstName = "";
    public static String lastName = "";
    public static String phone = "";
    public static String speciality = "";
    public static String degree = "";
    public static String clinicAddress = "";
    public static String penId = "";
    public static String currentBundle = "";
    public static Context context;

    public Doctor() {
    }


    public static void clearDoc() {
        registrationID = "";
        registrationYear = "";
        email = "";
        firstName = "";
        lastName = "";
        phone = "";
        speciality = "";
        degree = "";
        clinicAddress = "";
        penId = "";
        currentBundle = "";
    }

    public static void fromBundle(Bundle bundle) {
        Doctor.registrationID = (bundle.getString("registrationID"));
        Doctor.registrationYear = (bundle.getString("registrationYear"));
        Doctor.email = (bundle.getString("email"));
        Doctor.firstName = (bundle.getString("firstName"));
        Doctor.lastName = (bundle.getString("lastName"));
        Doctor.phone = (bundle.getString("phone"));
        Doctor.speciality = (bundle.getString("speciality"));
        Doctor.degree = (bundle.getString("degree"));
        Doctor.clinicAddress = (bundle.getString("clinicAddress"));
        Doctor.currentBundle = (bundle.getString("currentBundle", ""));
    }

    public static void saveToSharedPreferences(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor bundle = preferences.edit();
        bundle.putString("registrationID", registrationID);
        bundle.putString("registrationYear", registrationYear);
        bundle.putString("email", email);
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("phone", phone);
        bundle.putString("speciality", speciality);
        bundle.putString("degree", degree);
        bundle.putString("clinicAddress", clinicAddress);
        bundle.putString("penId", penId);
        bundle.putString("currentBundle", currentBundle);
        bundle.apply();

    }

    public static void loadFromSharedPreferences(Context context) {
        Doctor.context = context;
        Log.d("Preference ", "" + PreferenceManager.getDefaultSharedPreferences(context));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Doctor.registrationID = (preferences.getString("registrationID", ""));
        Doctor.registrationYear = (preferences.getString("registrationYear", ""));
        Doctor.email = (preferences.getString("email", ""));
        Doctor.firstName = (preferences.getString("firstName", ""));
        Doctor.lastName = (preferences.getString("lastName", ""));
        Doctor.phone = (preferences.getString("phone", ""));
        Doctor.speciality = (preferences.getString("speciality", ""));
        Doctor.degree = (preferences.getString("degree", ""));
        Doctor.clinicAddress = (preferences.getString("clinicAddress", ""));
        Doctor.penId = (preferences.getString("penId", ""));
        Doctor.currentBundle = (preferences.getString("currentBundle", ""));
    }

    public String getFirstName() {
        return firstName;
    }

    public  void setFirstName(String firstName) {
        Doctor.firstName = firstName;
    }

    public String getCurrentBundle() {
        return currentBundle;
    }

    public void setCurrentBundle(String currentBundle) {
        Doctor.currentBundle = currentBundle;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        Doctor.lastName = lastName;
    }

    @Override
    public String toString() {
        return "RegID: " + registrationID +
                "\nRegYear: " + registrationYear +
                "\nemail: " + email +
                "\nFirstname: " + firstName +
                "\nlastName: " + lastName +
                "\nphone: " + phone +
                "\nspeciality: " + speciality +
                "\ndegree: " + degree +
                "\nAddress: " + clinicAddress +
                "\npenId: " + penId +
                "\nBundle: " + currentBundle;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("registrationID", registrationID);
        bundle.putString("registrationYear", registrationYear);
        bundle.putString("email", email);
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("phone", phone);
        bundle.putString("speciality", speciality);
        bundle.putString("degree", degree);
        bundle.putString("clinicAddress", clinicAddress);
        bundle.putString("penId", penId);
        bundle.putString("currentBundle", currentBundle);

        return bundle;
    }

    public String getRegistrationID() {
        if(registrationID==null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Doctor.registrationID = (preferences.getString("registrationID", ""));
            if(Doctor.registrationID==null){
                FirebaseDatabase.getInstance().getReference("Doctors").child("Doctor Id").orderByChild("phone").equalTo(Doctor.phone).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String registrationid = dataSnapshot.child("registrationID").getValue(String.class);
                        Doctor.registrationID  =registrationid;
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

                        SharedPreferences.Editor bundle = preferences.edit();
                        bundle.putString("registrationID", registrationid);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        Doctor.registrationID = registrationID;
    }

    public String getRegistrationYear() {
        return registrationYear;
    }

    public void setRegistrationYear(String registrationYear) {
        Doctor.registrationYear = registrationYear;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Doctor.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        Doctor.phone = phone;
    }

    public String getSpeciality() {
        return speciality;
    }

    private void setSpeciality(String speciality) {
        Doctor.speciality = speciality;
    }

    public String getDegree() {
        return degree;
    }

    private void setDegree(String degree) {
        Doctor.degree = degree;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    private void setClinicAddress(String clinicAddress) {
        Doctor.clinicAddress = clinicAddress;
    }

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        Doctor.penId = penId;
    }
}
