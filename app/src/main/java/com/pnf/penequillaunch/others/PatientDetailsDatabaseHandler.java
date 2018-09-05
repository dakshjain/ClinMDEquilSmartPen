package com.pnf.penequillaunch.others;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class PatientDetailsDatabaseHandler {
    private static final String LastName = "lastName";
    private static final String FirstName = "firstName";
    private static final String PageId = "pageId";
    private static final String MobileNumber = "mobileNumber";
    private static final String Gender = "gender";
    private static final String Age = "age";
    private static final String Date = "date";
    private static final String FollowUp = "follow_up";
    private static final String Diagnose = "diagnose";
    private static final String NextPage = "nextPage";
    private static final String UGCId = "ugcid";
    private static final String PatientId = "patientid";
    private static final String Type = "type";
    private static final String TableName = "patient_details_new";
    private static final String DBName = "status.db";
    private static final int DBVersion = 3;
    private static final String Table_Create =
            "create table if not exists patient_details_new (" +
                    "lastName text not null," +
                    "firstName text not null," +
                    "gender text not null," +
                    "age text not null," +
                    "date text not null," +
                    "pageId text not null," +
                    "follow_up text not null," +
                    "diagnose text not null," +
                    "mobileNumber text not null," +
                    "patientid text not null," +
                    "ugcid text not null,"+
                    "type text not null,"+
                    "Primary Key(firstName,lastName,mobileNumber,pageId,type))";



    private final DataBaseHelper dbh;
    Context context;
    private SQLiteDatabase sql;

    public PatientDetailsDatabaseHandler(Context context) {
        dbh = new DataBaseHelper(context);
        this.context = context;
    }

    public void Open() {
        Log.d("PatientDetails","trying open");
        DotDetailsDatabaseHandler.getsql().execSQL(Table_Create);
    }

    public void Close() {
        dbh.close();
    }

    public void insertData(String firstName, String lastName, String pageId, String mobileNumber, String age, String date, String gender,String followUp,String diagnose,String patientId,String ugcid , String type) {

        if(firstName.isEmpty()){
            firstName = "NA";
        }
        if(lastName.isEmpty()){
            lastName = "NA";
        }if(mobileNumber.isEmpty()){
            mobileNumber = "NA";
        }if(age.isEmpty()){
            age = "NA";
        }if(date.isEmpty()){
            date = "NA";
        }if(gender.isEmpty()){
            gender = "NA";
        }if(followUp.isEmpty()){
            followUp = "NA";
        }if(diagnose.isEmpty()){
            diagnose = "NA";
        }if(patientId.isEmpty()){
            patientId = "NA";
        }if(ugcid.isEmpty()){
            ugcid = "NA";
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(FirstName, firstName);
        contentValues.put(LastName, lastName);
        contentValues.put(PageId, pageId);
        contentValues.put(MobileNumber, mobileNumber);
        contentValues.put(Age, age);
        contentValues.put(Date, date);
        contentValues.put(Gender, gender);
        contentValues.put(FollowUp,followUp);
        contentValues.put(Diagnose,diagnose);
        contentValues.put(PatientId,patientId);
        contentValues.put(UGCId,ugcid);
        contentValues.put(Type,type);
        try {
            DotDetailsDatabaseHandler.getsql().insertOrThrow(TableName, null, contentValues);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.i("patient","caught "+pageId);
            try {
                Log.i("patient ","trying update "+pageId);
                String[] args = new String[]{pageId, type};
                DotDetailsDatabaseHandler.getsql().update(TableName, contentValues, "mobileNumber = "+mobileNumber, null);
               // DotDetailsDatabaseHandler.getsql().execSQL("update "+TableName+" set patientid = '"+patientId+"' where firstName = '"+firstName+"' AND lastName = '"+lastName+"' AND mobileNumber = '"+mobileNumber+"' AND pageId = '"+pageId+"' AND type = 'document'");


            }
            catch (Exception e1) {
                Log.i("patient ","caugth 2 "+pageId);
                e.printStackTrace();
            }
        }

    }
    public void updatePageid(String firstName , String lastName  , String mobileNumber , String pageId , String oldpageId){
        DotDetailsDatabaseHandler.getsql().execSQL("update "+TableName+" set pageId = '"+pageId+"' where  pageId = '"+oldpageId+"' AND type = 'document'");
        Log.d("inside","update pageid");
        //DotDetailsDatabaseHandler.getsql().execSQL("update patient_details_new set pageId ='4' where firstName = '1' AND lastName = '1' AND mobileNumber = '9413097140' AND pageId = '10003' ");
    }
    public void updatePatientId(String firstName , String lastName  , String mobileNumber , String pageId ,String patientid ){
        DotDetailsDatabaseHandler.getsql().execSQL("update "+TableName+" set patientid = '"+patientid+"' where firstName = '"+firstName+"' AND lastName = '"+lastName+"' AND mobileNumber = '"+mobileNumber+"' AND pageId = '"+pageId+"' AND type = 'document'");
        Log.d("inside","update patientid");
        //DotDetailsDatabaseHandler.getsql().execSQL("update patient_details_new set pageId ='4' where firstName = '1' AND lastName = '1' AND mobileNumber = '9413097140' AND pageId = '10003' ");
    }
    public void deleteAll() {
        DotDetailsDatabaseHandler.getsql().execSQL("Drop Table If Exists dots");
    }
/*float x, float y, int pressure,
                               int dotType, long timestamp,
                               int sectionId, int ownerId, int noteId,
                               int pageId, int color,
                               int penTipType,
                               int tiltX, int tiltY, int twist*/

    public Cursor returnPageDate(String pageId) {
        Log.d("PageId rec", "" + pageId);
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT date from "+TableName+" where pageID = '"+pageId+"'" ,null);
    }
    public Cursor returnData() {
        Log.d("Inside", "return data");

        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT "+TableName+".firstName , "+TableName+".lastName , "+TableName+".pageId , "+TableName+".mobileNumber , "+TableName+".date from "+TableName+"  where "+TableName+".mobileNumber <> 'NA' group by "+TableName+".firstName , "+TableName+".lastName , "+TableName+".mobileNumber union all SELECT "+TableName+".firstName , "+TableName+".lastName , "+TableName+".pageId , "+TableName+".mobileNumber , "+TableName+".date from "+TableName+"  where "+TableName+".mobileNumber = 'NA'  order by "+TableName+".date desc",null);


    }
    public Cursor returngenderandage(String mobile){
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT "+TableName+".gender , "+TableName+".age  from "+TableName+" where "+TableName+".mobileNumber = '"+mobile+"'",null );
    }
    public Cursor returnpatientIds() {
        Log.d("Inside", "return data");

        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT distinct "+TableName+".patientid from "+TableName+"  order by "+TableName+".date desc",null);


    }
    public Cursor returnPatientInfo(String patientid){
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT firstName , lastName , max(date) , mobileNumber , gender , ugcid , patientid from "+TableName+" where patientid = '"+patientid+"'  order by date desc",null);
    }
    public Cursor returnNoOfPages() {

        return DotDetailsDatabaseHandler.getsql().rawQuery("select count(Distinct " + PageId + ") from " + TableName, null);
    }
    public Cursor returnPagesinDate(String date , String mobileNumber , String firstName , String lastName  , String patientid) {

        return DotDetailsDatabaseHandler.getsql().rawQuery("select count( distinct " + PageId + ") from " + TableName +" where date like '"+date+"%' AND mobileNumber = '"+mobileNumber+"' AND firstName = '"+firstName+"' AND lastName ='"+lastName+"' AND patientid = '"+patientid+"'", null);
    }
    public Cursor returnPageIdsinDate(String date , String mobileNumber , String firstName , String lastName ) {

        return DotDetailsDatabaseHandler.getsql().rawQuery("select distinct " + PageId + " from " + TableName +" where date like '"+date+"%' AND mobileNumber = '"+mobileNumber+"' AND firstName = '"+firstName+"' AND lastName ='"+lastName+"'", null);
    }
    public Cursor returnNoOfPatients() {

        return DotDetailsDatabaseHandler.getsql().rawQuery("select count(Distinct " + MobileNumber + ") from " + TableName, null);
    }
    public Cursor returndetailsofnextpage(String page){
        return  DotDetailsDatabaseHandler.getsql().rawQuery("SELECT firstName , lastName , pageId , mobileNumber , age , date , gender , follow_up , diagnose  , ugcid from " + TableName + " where pageId = '"+page+"'", null);
    }
    public Cursor returndetailsofugcid(String ugcid){
        return  DotDetailsDatabaseHandler.getsql().rawQuery("select firstName , lastName , pageId , mobileNumber  , age , date  , gender , follow_up , diagnose    from " + TableName + " where ugcid = '"+ugcid+"'", null);
    }
    public Cursor returnPatientPages(String firstName , String lastName  , String mobileNumber ,String patientid){
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT pageId , type from "+TableName+" where firstName = '"+firstName+"' AND lastName = '"+lastName+"' AND mobileNumber  = '"+mobileNumber+"' AND patientid = '"+patientid+"'   order by date desc" ,null);
    }
    public Cursor returnPatientdates(String firstName , String lastName  , String mobileNumber ,String patientid ){
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT distinct substr(date , 0, 9)  from "+TableName+" where patientid = '"+patientid+"' order by date desc" ,null);
    }
    public Cursor returnPatientPages1(String patientid){
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT pageId from "+TableName+" where patientid = '"+patientid+"'    order by date" ,null);
    }


    public void Another_Open() {
        sql.execSQL(Table_Create);
    }

    public Cursor returndetailsofnumber(String number,String firstName) {
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT firstName , lastName , pageId , mobileNumber , age , date , gender , follow_up , diagnose  , ugcid from "+TableName+" where mobileNumber = '"+number+"' AND firstName = '"+firstName+"'",null);
    }

    public Cursor returnALlPages() {
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT pageId from "+TableName,null);
    }

    public Cursor returnPageData(String pageId) {
        return DotDetailsDatabaseHandler.getsql().rawQuery("SELECT firstName , lastName , pageId , mobileNumber , age , date , gender , follow_up , diagnose , patientid ,ugcid from "+TableName+" where pageId = '"+pageId+"'",null);
    }

    public Cursor returnPageData1(String pageId){
        return  DotDetailsDatabaseHelper.getsql().rawQuery("SELECT "+TableName+".firstName  , "+TableName+".lastName , "+TableName+".mobileNumber from "+TableName+" where "+TableName+".pageId = '"+pageId+"' ",null);
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

