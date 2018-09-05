package com.pnf.penequillaunch.others;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by mbbbj on 25/05/2018.
 */

public class DataToSVG {

    private static Context context;
    private static Thread thread;
    private LinkedHashMap<Integer, Pages> pages = new LinkedHashMap<>();
    private LinkedHashMap<Integer, String> type = new LinkedHashMap<>();

    private ArrayList<Integer> pagelistarray = new ArrayList<>();
    private StorageReference mStorageRef;
    private static DatabaseReference myRef;
    private SharedPreferences preferences;
    private static float multiplier = 10f;
    private String regId = "";
    private static int counter = 0 ;
    public static int threshhold  = 0;
    int[] pageId;
    public DataToSVG(Context context){
        this.context = context;
    }
    public  DataToSVG(){}
    public Thread doInBackground(Context context) {
        Runnable r = new AnotherBar(context);
        thread = new Thread(r);
        thread.start();
        return thread;
    }


    public void initFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Doctors");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID());
    }


    private class AnotherBar implements Runnable {
        private int i = 0, j=0, k=0;
        private Context context;

        AnotherBar() {

        }

        AnotherBar(Context context) {
            this.context = context;
            DataToSVG.context = context;

        }


        @Override
        public void run() {
            do {
                Doctor.loadFromSharedPreferences(context);
                pages = new LinkedHashMap<>();
                type = new LinkedHashMap<>();
                regId = new Doctor().getRegistrationID();
                Cursor c1 = FirebaseDetailsDatabaseHelper.getdatabasehelper().returnPageList_asc();
                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                multiplier = preferences.getFloat("paper_scale", 10f);
                int i = 0;
                Log.i("multiplier",""+multiplier);
                if (c1.moveToFirst())
                    if(c1.getCount()>0) {
                        pageId = new int[c1.getCount()];
                        threshhold = c1.getCount();
                        do {

                            Log.d("pageId's:-", "" + c1.getInt(0));
                            //int pageId = 0;

                            pageId[i] = c1.getInt(0);

                            if (!pages.containsKey(pageId)) {
                                Log.d("DatatoSVG", "inside contains " + pageId);
                                pages.put(pageId[i], new Pages(pageId[i]));
                                type.put(pageId[i],c1.getString(1));
                            }
                            if (Thread.currentThread().isInterrupted())
                                break;


                            if (Thread.currentThread().isInterrupted())
                                break;

                            initFirebase();
                            // pages.get(pageId).doAdjustments(context);
                            Log.d("pageid", "" + pageId[i]);
                            i++;
                            //saveToFirebase(pages.get(pageId));
                        } while (c1.moveToNext());
                        if(type.get(pageId[counter]).equals("document")) {
                            saveToFirebase(pages.get(pageId[counter]),"document");
                        }
                        else{
                            saveToFirebase(pages.get(pageId[counter]));
                        }
                        if (Thread.currentThread().isInterrupted())
                            break;

                    }
            } while(false);
        }
    }

    public void saveToFirebase(final Pages page) {
        final DownloadedPages page1;
//        uploadDetail(page.age, page.pageID, "Age");
//        uploadDetail(page.mobile, page.pageID, "Mobile");
        final int filename = page.getPageID();
        page1 = new DownloadedPages();
        int counter ;
        myRef.child(new Doctor().getRegistrationID()).child("Page Counter").child("Counter").child("page").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String counter1 = dataSnapshot.getValue(String.class);
                final int counter  = Integer.valueOf(counter1);
                myRef.child(new Doctor().getRegistrationID()).child("Page Counter").child("Counter").child("page").setValue(String.valueOf(counter+1));

                int counter_update = counter+1;
                //page.setPageID(counter+1);
                Log.d("COUNTER",""+counter+1);
                page1.setPage(String.valueOf(counter));
                try {
                    // Cursor c = dotDetailsDatabaseHandler.returnPageDate(page.pageID);
                    int pageid= page.getPageID();
                    Log.d("pageid"," "+pageid);
                    Cursor c = DotDetailsDatabaseHelper.getdatabasehelper().returnPageDate(pageid);
                    if (c.moveToFirst()) {
                        String date = c.getString(0);
                        Log.d("DataToSVG date", "page id " + page.pageID + " " + date);
                        page1.setDate(date);

                    }
                    page1.setDoctor(new Doctor().getRegistrationID());
                    uploadOrignal(String.valueOf(filename), counter, page , page1);
                    // myRef.child(regId + "Page" + page.pageID).setValue(page1);




                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }
    private synchronized void uploadOrignal(String filename, final int pageID, final Pages page , final DownloadedPages page1) {


        Log.d("SyncService Uploading", "Started Page " + pageID);

        final StorageReference patientRef = mStorageRef.child("Original Pages").child( pageID + ".png");

            filename = filename+".png";

        File f = new File(context.getFilesDir().getAbsolutePath()+File.separator+"imageDir",filename);


        Uri file = Uri.fromFile(new File(context.getFilesDir().getAbsolutePath()+File.separator+"imageDir",filename));

            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/png")
                    .build();
            UploadTask task = patientRef.putFile(file, metadata);

            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("SyncService Uploading", "Done original");
                    FirebaseDetailsDatabaseHelper.getdatabasehelper().deleteData(page.pageID);
                    patientRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                            myRef.child(new Doctor().getRegistrationID()).child("Pages").child("Page"+pageID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.exists()) {
                                        Log.i("firebase call", "singlevalueevent " + page.pageID);
                                        Log.d("page info"," "+page.getDate());
                                        //  page1.setPage("Page" + page.pageID);
                                        //page1.setPage(String.valueOf(counter));
                                        myRef.child(new Doctor().getRegistrationID()).child("Pages").child("Page" + pageID).setValue(page1);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            myRef.child(new Doctor().getRegistrationID()).child("Page Downloads").child("Page"+pageID).child("download_link").setValue(downloadUri.toString());
                            myRef.child(new Doctor().getRegistrationID()).child("Page Downloads").child("Page"+pageID).child("page").setValue(String.valueOf(pageID));
                        }
                    });
                    counter = counter + 1;
                    if(counter<threshhold){
                        if(type!=null&&pageId!=null) {
                            if (type.get(pageId[counter]).equals("document")) {
                                saveToFirebase(pages.get(pageId[counter]), "document");
                            } else {
                                saveToFirebase(pages.get(pageId[counter]));
                            }
                        }

                    }


                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("SyncServiceUploading", "Failed original");
                    exception.printStackTrace();
                }
            });



    }
    String getExternalStoragePathDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public void saveToFirebase(final Pages page , String type) {

        final DownloadedPages page1;
//        uploadDetail(page.age, page.pageID, "Age");
//        uploadDetail(page.mobile, page.pageID, "Mobile");
        final int filename = page.getPageID();
        page1 = new DownloadedPages();
        int counter ;
        myRef.child(new Doctor().getRegistrationID()).child("Document Counter").child("Counter").child("document").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String counter1 = dataSnapshot.getValue(String.class);
                final int counter  = Integer.valueOf(counter1);
                myRef.child(new Doctor().getRegistrationID()).child("Document Counter").child("Counter").child("document").setValue(String.valueOf(counter+1));

                int counter_update = counter+1;
                //page.setPageID(counter+1);
                Log.d("COUNTER",""+counter);
                page1.setPage(String.valueOf(counter));
                try {
                    // Cursor c = dotDetailsDatabaseHandler.returnPageDate(page.pageID);
                    int pageid= page.getPageID();
                    Log.d("pageid"," "+pageid);
                    Cursor c = DotDetailsDatabaseHelper.getdatabasehelper().returnPageDate(pageid);
                    if (c.moveToFirst()) {
                        String date = c.getString(0);
                        Log.d("DataToSVG date", "page id " + page.pageID + " " + date);
                        page1.setDate(date);
                        page1.setAge(page.getAge());
                        page1.setDoctor(new Doctor().getRegistrationID());
                        page1.setFirstName(page.getName());
                        page1.setLastName(page.getLastName());
                        page1.setGender(page.getGender());
                        page1.setMobile(page.getMobile());
                        page1.setUgcid(page.getUGCId());
                    }
                    page1.setDoctor(new Doctor().getRegistrationID());
                    // myRef.child(regId + "Page" + page.pageID).setValue(page1);

                    uploadOrignal(String.valueOf(filename), counter, page , "document" , page1);



                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private synchronized void uploadOrignal(String filename, final int pageID, final Pages page , String type1 , final DownloadedPages page1) {


        Log.d("SyncService Uploading", "Started Page " + pageID);

        final StorageReference patientRef = mStorageRef.child("Documents").child( "Document"+pageID + ".png");

        filename = filename+".png";

        final File f = new File(context.getFilesDir().getAbsolutePath()+File.separator+"imageDir",filename);



        Uri file = Uri.fromFile(new File(context.getFilesDir().getAbsolutePath()+File.separator+"imageDir",filename));

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/png")
                .build();
        UploadTask task = patientRef.putFile(file, metadata);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("SyncService Uploading", "Done original");

                patientRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                        myRef.child(new Doctor().getRegistrationID()).child("Documents").child("Document"+pageID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {
                                    Log.i("firebase call", "singlevalueevent " + page.pageID);
                                    Log.d("page info"," "+page.getDate());
                                    //  page1.setPage("Page" + page.pageID);
                                    //page1.setPage(String.valueOf(counter));
                                    myRef.child(new Doctor().getRegistrationID()).child("Documents").child("Document" + pageID).setValue(page1);


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        myRef.child(new Doctor().getRegistrationID()).child("Document Downloads").child("Document"+pageID).child("download_link").setValue(downloadUri.toString());
                        myRef.child(new Doctor().getRegistrationID()).child("Document Downloads").child("Document"+pageID).child("document").setValue(String.valueOf(pageID));
                        Log.d("page values",""+page.getName()+ ".."+page.getLastName()+".."+page.getMobile()+".."+pageID+".."+page.getPageID());
                        PatientDetailsDatabaseHelper.getdatabasehelper().updatePageid(page.getName(),page.getLastName(),page.getMobile(),String.valueOf(pageID),String.valueOf(page.getPageID()));
                        FirebaseDetailsDatabaseHelper.getdatabasehelper().deleteData(page.pageID);
                        DotDetailsDatabaseHelper.getdatabasehelper().deleteData(page.pageID);
                        f.delete();

                    }
                });
                counter = counter + 1;
                if(counter<threshhold){

                    if(type.get(pageId[counter]).equals("document")) {
                        saveToFirebase(pages.get(pageId[counter]),"document");
                    }
                    else{
                        saveToFirebase(pages.get(pageId[counter]));
                    }

                }



            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("SyncServiceUploading", "Failed original");
                exception.printStackTrace();
            }
        });



    }

}
