package com.pnf.penequillaunch.others;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FirebaseUploadDatabaseHandler {
    private static final String timestamp = "timestamp";
    private static final String pageid = "pageid";
    private static final String filename = "filename";
    private static final String type = "type";

    private static final String TableName  = "upload_firebase_details";

    private static final String DBName = "status.db";
    private static final int DBVersion = 3;




    private static final String Table_Create = "create table if not exists upload_firebase_details (" +
            "timestamp text not null," +
            "filename text not null," +
            "type text not null," +
            "pageid integer not null)";

    private final DataBaseHelper dbh ;
    Context context;
    private static SQLiteDatabase sql;


    public FirebaseUploadDatabaseHandler(Context context) {
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

    public void insertData(String filename , String timestamp, String type ,
                           int pageId) {
        //pageId = pageId + ((int) (Doctor.CurrentBundle * 512));

        /*contentValues.put(PenTipType, penTipType);
        contentValues.put(TiltX, tiltX);
        contentValues.put(TiltY, tiltY);
        contentValues.put(Twist, twist);
        contentValues.put(Colour, color);*/
        sql.execSQL("INSERT INTO "+TableName+ " VALUES(" +
                timestamp + "," +
                filename + ", '" +
                type + "'," +
                pageId +
                ");");
        Log.d("inside","insertData_dot");


    }

    public void deleteData(int pageId) {
        sql.execSQL("Delete from " + TableName + " where pageid = '" + pageId + "'");
    }

    public void deleteAll() {
        sql.execSQL("Drop Table If Exists dots");
    }

    public Cursor returnPageDate(Integer pageid) {
        return sql.rawQuery("SELECT timestamp from "+TableName+" where pageid = "+pageid,null);
    }

    public Cursor returnPageList() {
        return sql.rawQuery("SELECT pageid from "+TableName+" order by pageid desc",null);
    }
    public Cursor returnPageList_asc(){
        return sql.rawQuery("SELECT pageid , type from "+TableName+" order by pageid asc",null);

    }

    public Cursor returnPageName(int pageid) {
        return sql.rawQuery("SELECT filename from "+TableName+" where pageid = "+pageid,null);
    }
/*float x, float y, int pressure,
                               int dotType, long timestamp,
                               int sectionId, int ownerId, int noteId,
                               int pageId, int color,
                               int penTipType,
                               int tiltX, int tiltY, int twist*/
/*


    public Cursor returnData() {
        return sql.query(TableName,
                new String[]{X, Y, Pressure, DotType, TimeStamp, SectionID, OwnerId, NoteId, PageId},
                null, null, null, null, null);
    }
    public Cursor returnDataXYPageDotType(int pageid) {
        return sql.rawQuery("SELECT x , y , dotType from "+TableName+" where pageid = "+pageid,null);
    }
*/
/*
    public Cursor returnPageListTimeStamp() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //sql.execSQL(FirebaseUploadDatabaseHandler.Table_Create);
        //return sql.rawQuery("Select "+ PageId + " from " + TableName + " where pageid in ( select distinct pageid from " + FirebaseUploadDatabaseHandler.TableName + " ) group by pageid ",null  );
        //return sql.rawQuery("Select "+ PageId + " from " +TableName + " join " + FirebaseUploadDatabaseHandler.TableName + " on " + PageId + " = " +new FirebaseUploadDatabaseHandler(context).returnPageId() , null);
       *//*
*/
/* return sql.rawQuery("Select " + PageId + " from " + TableName +
                " except " +
                "Select " + PageId + " from " + FirebaseUploadDatabaseHandler.TableName, null);*//*
*/
/*
        //return sql.query(true, TableName, new String[]{PageId}, null, null, null, null, TimeStamp+ " desc", null);

       *//*
*/
/* return sql.rawQuery("Select " + PageId + " from " + TableName +
                " except " +
                "Select " + PageId + " from " + FirebaseUploadDatabaseHandler.TableName, null);*//*
*/
/*
    }*//*



    public Cursor returnPage(int pageId) {
        Log.d("PageId rec", "" + pageId);
        return sql.query(TableName,
                new String[]{X, Y, Pressure, DotType, TimeStamp, SectionID, OwnerId, NoteId, PageId, Colour, PenTipType, TiltX, TiltY, Twist},
                " " + PageId + "='" + pageId + "'", null, null, null, null);
    }


    public Cursor returnPageDate(int pageId) {
        Log.d("PageId rec1", "" + pageId);
        return sql.query(TableName,
                new String[]{"MAX(timestamp)"},
                " " + PageId + "='" + pageId + "'", null, null, null, null, null);
    }

    public Cursor returnPageList() {
        // return sql.query( TableName, new String[]{PageId}, null, null, null, null, TimeStamp + " DESC");
        return sql.rawQuery("select "+ PageId + " from ( select distinct "+ PageId +" , max(" + TimeStamp +")  as date2 from " + TableName  + " group by " + PageId +" ) order by date2 desc limit 10", null);
        //return sql.rawQuery("select distinct " + PageId + " from " + TableName + " order by " +TimeStamp+ " desc",null);
    }
    public Cursor returnPageList_All() {
        // return sql.query( TableName, new String[]{PageId}, null, null, null, null, TimeStamp + " DESC");
        return sql.rawQuery("select "+ PageId + " from ( select distinct "+ PageId +" , max(" + TimeStamp +")  as date2 from " + TableName  + " group by " + PageId +" )", null);
        //return sql.rawQuery("select distinct " + PageId + " from " + TableName + " order by " +TimeStamp+ " desc",null);
    }

    public Cursor returnNoOfPages() {

        return sql.rawQuery("select count(Distinct " + PageId + ") from " + TableName, null);
    }

    public Cursor returnNoOfPagesToday() {
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy");
            Date date = new Date();
            String dateString = dateFormat2.format(date);

            date = dateFormat2.parse(dateString);

            Date date1 = new Date(date.getTime());
            Date date2 = new Date(date.getTime() + 86400000);
            String query = "select count(Distinct " + PageId + ") from " + TableName + " where timestamp > " + date1.getTime() + " and timestamp < " + date2.getTime();
            Log.d("Query ", query);
            return sql.rawQuery(query, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cursor returnDataTime() {
        return sql.query(true, TableName, new String[]{TimeStamp}, null, null, null, null, TimeStamp + " desc", null);
    }
*/

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
