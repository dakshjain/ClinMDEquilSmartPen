package com.pnf.penequillaunch.mainActivity;




import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pnf.penequillaunch.others.DataToSVG;
import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.others.FirebaseDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.FullScreenImageAdapter;
import com.pnf.penequillaunch.others.Pages;
import com.pnf.penequillaunch.others.PatientDetailsDatabaseHelper;
import com.pnf.penequillaunch.test.MainActivity;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.pnf.penequillaunch.test.MainActivity.MyPREFERENCES;
import static com.pnf.penequillaunch.test.MainActivity.date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment implements ViewPager.OnPageChangeListener {

    public ArrayList<String> arrayList;
    public ArrayList<String> dateList;
    public FullScreenImageAdapter adapter;
    View v;
    String firstName , lastName , mobileNumber , patientid;
    int position ;
    FloatingActionButton add_document ;
    Uri imageUri;
    public static final String PageId = "pageid";
    public static String downloadfilename  ;
    SharedPreferences sharedpreferences;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    private File output =  null;

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_image, container, false);

        final ViewPager recyclerView = (ViewPager) findViewById(R.id.recycler);
        Log.d("PageFragment","inside recent ");
        arrayList = new ArrayList<>();
        dateList = new ArrayList<>();
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        setHasOptionsMenu(false);
        add_document = (FloatingActionButton) findViewById(R.id.add_document_image);
        downloadfilename  = String.valueOf(System.currentTimeMillis()/1000);
        add_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),downloadfilename+".png"));

                takePicture.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,imageUri);

                startActivityForResult(takePicture, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setRecycleChildrenOnDetach(false);


        Bundle arguments = getArguments();
        firstName = arguments.getString("firstName");
        lastName = arguments.getString("lastName");
        mobileNumber = arguments.getString("mobileNumber");
        patientid = arguments.getString("patientid");
        position = arguments.getInt("position");
        LoadData();
      /*  findViewById(R.id.next_bundle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Doctor.currentBundle += 1;
            }
        });*/
        adapter = new FullScreenImageAdapter((MainActivity) getActivity(),arrayList , dateList);

        recyclerView.setAdapter(adapter);

        recyclerView.setCurrentItem(position);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setDrawingCacheEnabled(true);
        adapter.notifyDataSetChanged();
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

            try {
                savetimeindb(String.valueOf(System.currentTimeMillis()/1000));
                adapter.notifyDataSetChanged();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    public void savetimeindb(String online) throws ParseException, IOException {
        Long epoch = System.currentTimeMillis()/1000;
        Date currentTime = Calendar.getInstance().getTime();
        String date = currentTime.toString();
        String month  = date.substring(4,7);
        String date_ =date.substring(8,10);
        String hour =  date.substring(11,13);
        String minutes  = date.substring(14,16);
        String year = date.substring(30,34);

        String datetime = year + getmonthinno(month) + date_ + hour + minutes ;

        SharedPreferences.Editor editor = sharedpreferences.edit();
        SharedPreferences prefs = sharedpreferences;
        int count = prefs.getInt(PageId, 0);
        count = count + 1;
        editor.putInt(PageId, count);
        Log.d("count", "" + count);

        Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientPages(firstName,lastName,mobileNumber,patientid );
        if(c.moveToFirst()){
            String pageid = c.getString(0);
            Cursor c1 = PatientDetailsDatabaseHelper.getdatabasehelper().returnPageData(pageid);
           // String data = DatabaseUtils.dumpCursorToString(c1);
            if(c1.moveToFirst()) {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(),imageUri);
                bitmap = getResizedBitmap(bitmap,2000);
                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                DotDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count), datetime, "document",count);
                FirebaseDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count), datetime,"document" ,count);
                // Create imageDir
                //File mypath=new File(directory,count+".png");
                //File mypath = new File(getContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir",count+".png");
                File mypath = new File(getContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir"+File.separator+count+".png");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                   /* DataToSVG dataToSVG = new DataToSVG(getContext());
                    dataToSVG.initFirebase();
                    dataToSVG.saveToFirebase(new Pages(c1.getString(0), c1.getString(1),  count, c1.getString(3), (c1.getString(4)),
                            (datetime), c1.getString(6), c1.getString(7), c1.getString(8)
                            ,c1.getString(10)),"document");*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PatientDetailsDatabaseHelper.getdatabasehelper().insertData(c1.getString(0), c1.getString(1),  String.valueOf(count), c1.getString(3), (c1.getString(4)),
                        (datetime), c1.getString(6), c1.getString(7), c1.getString(8),
                        c1.getString(9),c1.getString(10),"document");

            }
            else {
                Log.d("patient ", "insert failed");
            }

        }

        editor.commit();
        adapter.notifyDataSetChanged();
    }
    public  Bitmap getResizedBitmap(Bitmap image ,  int maxize){
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapratio = (float) width/(float)height;
        if(bitmapratio>1){
            width = maxize ;
            height = (int) (width/bitmapratio);
        }else{
            height = maxize ;
            width = (int)(height *  bitmapratio);

        }
        return  Bitmap.createScaledBitmap(image,width,height,true);
    }



    public String getmonthinno(String month) {
        String newmonth ="";
        switch (month) {
            case "Jan":
                newmonth = "01";
                break;
            case "Feb":
                newmonth = "02";
                break;
            case "Mar":
                newmonth = "03";
                break;
            case "Apr":
                newmonth = "04";
                break;
            case "May":
                newmonth = "05";
                break;
            case "Jun":
                newmonth = "06";
                break;
            case "Jul":
                newmonth = "07";
                break;
            case "Aug":
                newmonth = "08";
                break;
            case "Sep":
                newmonth = "09";
                break;
            case "Oct":
                newmonth = "10";
                break;
            case "Nov":
                newmonth = "11";
                break;
            case "Dec":
                newmonth = "12";
                break;


        }
        return newmonth;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        /*MenuItem item_search = menu.findItem(R.id.search);
        MenuItem item_action_pen = menu.findItem(R.id.action_pen);
        if(item_search!=null)
            item_search.setVisible(false);
        if(item_action_pen!=null)
            item_action_pen.setVisible(false);*/
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }



    private View findViewById(int id) {
        return v.findViewById(id);
    }

    public void LoadData( )  {
        Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientPages(firstName,lastName,mobileNumber,patientid);

        if (c.moveToFirst()) {

            arrayList.clear();
            dateList.clear();
            do {
                Context ctxt  = getActivity().getApplicationContext();
                ContextWrapper cw= new ContextWrapper(ctxt);
                //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File directory = getContext().getFilesDir();
                String patientID = c.getString(0);
                final int pageid  = Integer.valueOf(patientID);
                Cursor c1  = DotDetailsDatabaseHelper.getdatabasehelper().returnPageNameAnddate(pageid);
                if(c1.moveToFirst()) {

                    String path  = directory.toString()+File.separator+"imageDir"+File.separator+c1.getString(0)+".png";
                    String date  = c1.getString(1);
                    File checkfile = new File(path);
                    if(!checkfile.exists()){
                        if (isNetworkAvailable()) {
                            StorageReference reference ;
                            if(c.getString(1).equals("document")){
                                Log.d("inside","document fetch");
                                 reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("Documents").child("Document"+pageid+".png");

                            }
                            else {
                                Log.d("inside","page fetch");
                                 reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("Original Pages").child(pageid + ".png");
                            }
                            //File f  =new File(new ContextWrapper(getContext()).getDir("imageDir", Context.MODE_PRIVATE),pageid + ".png");
                            File f = new File(getContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir"+File.separator+pageid+".png");
                            String test  = f.toString();
                            reference.getFile(f).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String test = "FUCKED!!";
                                    Toast.makeText(getContext(), "file unable to download", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                    adapter.notifyDataSetChanged();

                                }
                            });
                        }
                        else{
                            Toast.makeText(ctxt, "Internet Connection required", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //arrayList.add((new ContextWrapper(getActivity().getApplicationContext()).getDir("imageDir", Context.MODE_PRIVATE)).toString()+"/"+c1.getString(0));
                    arrayList.add(path);
                    dateList.add(date);
                    Log.d("datesadded1",""+date);

                }
                else{
                    String path  = directory.toString()+File.separator+"imageDir"+File.separator+pageid+".png";
                    File checkfile = new File(path);
                    if(!checkfile.exists()){
                        if (isNetworkAvailable()) {

                            StorageReference reference ;
                            if(c.getString(1).equals("document")){
                                Log.d("inside","document fetch");
                                reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("Documents").child("Document"+pageid+".png");

                            }
                            else {
                                Log.d("inside","page fetch");
                                reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("Original Pages").child(pageid + ".png");
                            }                            //File f  =new File(new ContextWrapper(getContext()).getDir("imageDir", Context.MODE_PRIVATE),pageid + ".png");
                            File f = new File(getContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir"+File.separator+pageid+".png");
                            String test  = f.toString();
                            reference.getFile(f).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    String test = "FUCKED!!";
                                    Toast.makeText(getContext(), "file unable to download1", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    //Toast.makeText(getContext(), "file downloaded", Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                        else{
                            Toast.makeText(ctxt, "Internet Connection required", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //arrayList.add((new ContextWrapper(getActivity().getApplicationContext()).getDir("imageDir", Context.MODE_PRIVATE)).toString()+"/"+c1.getString(0));
                    arrayList.add(path);
                    Cursor c_date = PatientDetailsDatabaseHelper.getdatabasehelper().returnPageDate(String.valueOf(pageid));
                    if(c_date.moveToFirst()){
                        dateList.add(c_date.getString(0));
                        Log.d("datesadded2",""+c_date.getString(0));
                    }
                }

                Log.d("retrieving page", "" + patientID);

            } while (c.moveToNext());


        }
        //publishProgress();



        if (adapter != null)
            adapter.notifyDataSetChanged();
        //new ShowSavedPages().execute();
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo  = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("inside","page selected");

        getActivity().getActionBar().setTitle(String.valueOf(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

   /* private class ShowSavedPages  extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... avoid) {
            Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientPages(firstName,lastName,mobileNumber);
            if (c.moveToFirst()) {

                arrayList.clear();
                do {

                    int patientID = c.getInt(0);

                    arrayList.add(patientID);

                    publishProgress();
                    Log.d("retrieving page", "" + patientID);

                } while (c.moveToNext());

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... avoid) {
            super.onProgressUpdate();

            adapter.notifyDataSetChanged();

        }


        @Override
        protected void onPostExecute(Void avpoid) {
            super.onPostExecute(avpoid);
            if (getContext() != null)
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            //  int  size=arrayList.size();
            //  arrayList.remove(size);
        }

    }*/
}
