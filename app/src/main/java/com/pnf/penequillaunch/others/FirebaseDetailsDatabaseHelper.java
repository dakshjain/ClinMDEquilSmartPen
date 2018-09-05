package com.pnf.penequillaunch.others;

import android.content.Context;
import android.util.Log;

/**
 * Created by 9 on 12/28/2017.
 */


public class FirebaseDetailsDatabaseHelper extends FirebaseUploadDatabaseHandler {
    public static FirebaseUploadDatabaseHandler firebasedatabasehandler ;
    public static  boolean flag = true;
    public FirebaseDetailsDatabaseHelper(Context context){
        super(context);
        firebasedatabasehandler = new FirebaseUploadDatabaseHandler(context);
    }
    public void open(){

        firebasedatabasehandler.Transaction_Open();
    }
    public void close(){
        firebasedatabasehandler.Transaction_Close();
    }
    public static FirebaseUploadDatabaseHandler getdatabasehelper(){
        return firebasedatabasehandler;
    }
    public static boolean getflag(){
        return flag;
    }
    public static void changeflag(boolean val){
        Log.d("FirebaseChange","flag"+flag);
        flag = val;
    }
}
