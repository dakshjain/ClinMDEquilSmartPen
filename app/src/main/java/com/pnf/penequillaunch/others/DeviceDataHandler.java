package com.pnf.penequillaunch.others;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceDataHandler {
    private static final String timestamp = "timestamp";
    private static final String pageid = "pageid";
    private static final String filename = "filename";
    private static final String TableName  = "live_details";

    private static final String DBName = "status.db";
    private static final int DBVersion = 3;


    private static final String Table_Create = "create table if not exists live_details (" +
            "timestamp text not null," +
            "type text not null," +
            "x float not null," +
            "y float not null)";

    private final DataBaseHelper dbh ;
    Context context;
    private static SQLiteDatabase sql;


    public DeviceDataHandler(Context context) {
        this.context = context;
        dbh = new DataBaseHelper(context);
        sql = dbh.getWritableDatabase();
    }

    public void Transaction_Open() {
        if(sql==null) {
            Log.d("DotDetails","Transact_Open");
            sql.beginTransaction();
        }
        try {
            sql.execSQL(Table_Create);
            Log.d("DotDetails","Open");
        }catch (Exception e){e.printStackTrace();}
    }

    public void Transaction_Close() {
        if(sql.inTransaction()) {
            sql.setTransactionSuccessful();
            sql.endTransaction();
        }
        try {
            dbh.close();
        }catch (Exception e){e.printStackTrace();}
    }

    public static SQLiteDatabase getsql(){
        return sql;
    }

    public void insertData(String timestamp , String type,float x,
                           float y) {
        //pageId = pageId + ((int) (Doctor.CurrentBundle * 512));

        /*contentValues.put(PenTipType, penTipType);
        contentValues.put(TiltX, tiltX);
        contentValues.put(TiltY, tiltY);
        contentValues.put(Twist, twist);
        contentValues.put(Colour, color);*/
        sql.execSQL("INSERT INTO "+TableName+ " VALUES(" +
                timestamp + "," +
                type + "," +
                x + "," +
                y +
                ");");
        Log.d("inside","insertData_dot");


    }
    public Cursor returnData() {
        return sql.rawQuery("SELECT x , y , type from "+TableName+" order by timestamp",null);
    }
    public void deleteAll() {
        sql.execSQL("delete from "+TableName);
    }



    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context context) {
            super(context, DBName, null, DBVersion);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.e("I WAS HERE", "1");
                db.execSQL(Table_Create);
            } catch (SQLException e) {
                Log.e("I WAS HERE", "2");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop Table If Exists dots");
            onCreate(db);
        }
    }
}
