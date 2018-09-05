package com.pnf.penequillaunch.others;

import android.content.Context;

/**
 * Created by 9 on 12/28/2017.
 */



public class PatientDetailsDatabaseHelper extends PatientDetailsDatabaseHandler {
    public static PatientDetailsDatabaseHandler patientDetailsDatabaseHandler ;
    public PatientDetailsDatabaseHelper(Context context){
        super(context);
        patientDetailsDatabaseHandler = new PatientDetailsDatabaseHandler(context);
    }
    public void open(){
        patientDetailsDatabaseHandler.Open();
    }
    public void close(){
        patientDetailsDatabaseHandler.Close();
    }
    public static PatientDetailsDatabaseHandler getdatabasehelper(){
        return patientDetailsDatabaseHandler;
    }
}
