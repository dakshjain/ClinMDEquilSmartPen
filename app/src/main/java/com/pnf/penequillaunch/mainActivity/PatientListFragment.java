package com.pnf.penequillaunch.mainActivity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daksh.clinmdequilsmartpenlaunch.BuildConfig;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import com.example.daksh.clinmdequilsmartpenlaunch.R;

import com.pnf.penequillaunch.logins.LoggingInDialog;
import com.pnf.penequillaunch.others.DataToSVG;
import com.pnf.penequillaunch.others.DeviceDataHelper;
import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.FirebaseDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.Pages;
import com.pnf.penequillaunch.others.Patient;
import com.pnf.penequillaunch.others.PatientDetailsDatabaseHelper;

import com.pnf.penequillaunch.others.ZoomImageView;
import com.pnf.penequillaunch.test.MainActivity;
import com.pnf.penequillaunch.Utils.RecyclerItemClickListener;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.pnf.penequillaunch.mainActivity.ImageFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.pnf.penequillaunch.test.MainActivity.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientListFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    public static final String servicecomplete = "com.clinmd.clinmd_app.mainActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static String searchString = "";
    public static String dateString = "";
    DatePickerDialog datePickerDialog;
    AlertDialog dialog ;
    PatientRecyclerAdapter patientRecyclerAdapter;
    View v;
    static Menu menu ;
    RecyclerView recyclerView;
    protected static boolean hasRss = false;
    static MenuItem next_page;
    static MenuItem prev_page;
    WebView webView;
    ArrayList<Patient> patients;
    LocalBroadcastManager bManager;
    com.github.clans.fab.FloatingActionButton fab , fab_share;
    private Uri imageUri;
    public static final String PageId = "pageid";
    private String firstName , lastName , mobileNumber , ugcid , age ,sex , diagnose  ,regId;
    String downloadfilename;
    Button submit  , share , cancel ,close;
    //EditText firstname,lastname,Ugcid,Age,Sex ,Diagnose ;
    TextInputLayout mobilenumber1 , firstnameL,lastnameL,UgcidL,AgeL,SexL ,DiagnoseL , regidL;
    TextInputEditText mobilenumber ,firstname,lastname,Ugcid,Age,Sex ,Diagnose , regid ;
    RadioGroup radiogrp ;
    RadioButton radiobtn;

    private boolean isfabvisible = true ;
    FloatingActionMenu floatingActionMenu;
    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(servicecomplete)) {
                Log.d("SyncSer", "onReceive();");
                patients = new ArrayList<>();


            }
        }
    };
    private SharedPreferences sharedpreferences;

    ArrayList<Patient> multiselect_list = new ArrayList<>();
    ArrayList<Uri> files_uri = new ArrayList<>();
    boolean isMultiSelect = false;
    ActionMode mActionMode;
    Menu context_menu;
    public static String sort_category  = "name";
    AlertDialog.Builder alert ;

    public PatientListFragment() {
        // Required empty public constructor
    }

    View findViewById(@IdRes int id) {
        return v.findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_patient_list, container, false);
        setHasOptionsMenu(true);
        bManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(servicecomplete);
        bManager.registerReceiver(bReceiver, intentFilter);
        patients = new ArrayList<>();
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        patientRecyclerAdapter = new PatientRecyclerAdapter(patients, ((MainActivity) getActivity()),multiselect_list,sort_category);
        try {
            if (getContext() != null) {
                new LoadPatients(getContext()).execute();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return v;
    }



    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(view ==null){
            Log.d("inside ","onviewcreated");
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        final android.support.v7.widget.GridLayoutManager linearLayoutManager = new android.support.v7.widget.GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(linearLayoutManager);
        Log.d("Position : ", new Doctor().getRegistrationID());
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setAdapter(patientRecyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);
                //else
                    //Toast.makeText(getContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Patient>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode =getActivity().startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));
        share = (Button)findViewById(R.id.share_button);
        cancel = (Button)findViewById(R.id.cancel);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePickerDialog = new DatePickerDialog(getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.add_document);
        //fab_share = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.share);
        floatingActionMenu =(FloatingActionMenu)findViewById(R.id.menu_blue);
        if(!isfabvisible){
            floatingActionMenu.setVisibility(View.GONE);
            share.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        /*fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new LoadPatients(getContext()).execute();
                PatientRecyclerAdapter.isradiovisible = true;
                floatingActionMenu.close(true);
                floatingActionMenu.setVisibility(View.GONE);
                isfabvisible=false;
                patientRecyclerAdapter.notifyDataSetChanged();
                share.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);

               *//* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Here is the share content body";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));*//*
            }
        });*/

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadPatients(getContext()).execute();
                PatientRecyclerAdapter.isradiovisible = false;
                floatingActionMenu.setVisibility(View.VISIBLE);
                isfabvisible=true;
                patientRecyclerAdapter.notifyDataSetChanged();
                share.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
            }
        });
    }
    protected void showDialog(){

        View view  = getActivity().getLayoutInflater().inflate(R.layout.dialog_patient_info, null);
        alert = new AlertDialog.Builder(getContext());

        alert.setView(view);
        dialog = alert.create();
        dialog.show();
        final boolean[] acceptno = {false};
        submit  =(Button) view.findViewById(R.id.submit);
        firstname  = (TextInputEditText) view.findViewById(R.id.firstname);
        lastname  = (TextInputEditText) view.findViewById(R.id.lastname);
        mobilenumber  = (TextInputEditText) view.findViewById(R.id.phone);
        mobilenumber1= (TextInputLayout) view.findViewById(R.id.phone_numberL);
        firstnameL = (TextInputLayout) view.findViewById(R.id.firstnameL);
        lastnameL = (TextInputLayout) view.findViewById(R.id.lastNameL);
        AgeL = (TextInputLayout) view.findViewById(R.id.ageL);
        DiagnoseL = (TextInputLayout) view.findViewById(R.id.diagnoseL);
        Age  = (TextInputEditText) view.findViewById(R.id.age);
        //Ugcid  = (EditText)view.findViewById(R.id.ugcid);
        radiogrp = (RadioGroup)view.findViewById(R.id.radioSex);
        Diagnose =(TextInputEditText) view.findViewById(R.id.diagnose);
        regidL = (TextInputLayout)view.findViewById(R.id.regidL);
        regid = (TextInputEditText)view.findViewById(R.id.regid);
         close = (Button)view.findViewById(R.id.close);
         close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alert!=null){
                    dialog.cancel();
                    }
            }
        });
        mobilenumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.hasFocus()) {
                    mobilenumber1.setError(null);
                }
            }
        });
        firstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                    firstnameL.setError(null);

            }
        });
        lastname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                firstnameL.setError(null);

            }
        });
        Age.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                firstnameL.setError(null);

            }
        });
        Diagnose.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                firstnameL.setError(null);

            }
        });

        mobilenumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i) {
                    case KeyEvent.KEYCODE_NAVIGATE_NEXT:
                    case KeyEvent.KEYCODE_ENTER:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_NUMPAD_ENTER:
                        acceptno[0] =phoneCheck();
                }
                return false;
            }
        });


        submit.setClickable(false);
        if(acceptno[0])
            submit.setClickable(true);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneCheck()) {

                    firstName = firstname.getText().toString();

                    lastName = lastname.getText().toString();

                    mobileNumber = mobilenumber.getText().toString();
                    //ugcid = Ugcid.getText().toString();

                    age = Age.getText().toString();
                    diagnose = Diagnose.getText().toString();
                    radiobtn = (RadioButton) radiogrp.findViewById(radiogrp.getCheckedRadioButtonId());
                    regId = regid.getText().toString();
                    if(regId.equals("")){
                        ugcid = "NA";
                    }else{
                        ugcid = regId;
                    }

                    if (radiobtn.getText().toString().equals("Male"))
                        sex = "M";
                    else
                        sex = "F";
                    downloadfilename = String.valueOf(System.currentTimeMillis() / 1000);

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), downloadfilename + ".png"));

                    takePicture.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);

                    startActivityForResult(takePicture, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                }
            }
        });

        //dialog.show();
    }
    private boolean phoneCheck() {
        String enteredPhoneNumber = mobilenumber.getText().toString();
        if (enteredPhoneNumber.isEmpty()) {
            //mobilenumber1.setError("Please Enter Mobile Number!");
            return true;
        } else if (!isValid(enteredPhoneNumber)) {
            mobilenumber1.setError("Please Enter a Valid Mobile Number!");
            return false;
        }
        return true;

    }
    private boolean valuesCheck(){

        if(firstname.getText().toString().isEmpty()){
            firstnameL.setError("Please Enter FirstName");
            return false;
        }
        else if(lastname.getText().toString().isEmpty()){
            lastnameL.setError("Please Enter LastName");
            return false;
        }
        else if(Age.getText().toString().isEmpty()){
            AgeL.setError("Please Enter Age");
            return false;
        }
        else if(Diagnose.getText().toString().isEmpty()){
            DiagnoseL.setError("Please Enter Diagnose");
            return false;
        }
        else if(mobilenumber.getText().toString().isEmpty()){
            return phoneCheck();

        }
        return true;
    }
    private boolean isValid(String enteredPhoneNumber) {
        int i = Integer.parseInt(enteredPhoneNumber.charAt(0) + "");
        boolean valid = i >= 7;
        return (enteredPhoneNumber.length() == 10) && valid;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        try {
            savetimeindb(String.valueOf(System.currentTimeMillis()/1000));
            patientRecyclerAdapter.notifyDataSetChanged();
            floatingActionMenu.close(true);

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(patients.get(position)))
                multiselect_list.remove(patients.get(position));
            else
                multiselect_list.add(patients.get(position));


            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }
    public void refreshAdapter()
    {
        patientRecyclerAdapter.selected_usersList=multiselect_list;
        patientRecyclerAdapter.itemsData=patients;
        patientRecyclerAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share:



                    requestPhotoPermissions();
                    //alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
                    return true;
                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Patient>();
            refreshAdapter();
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void requestPhotoPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(),
                        "Please grant permissions to change profile photo", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            getfiles();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void getfiles(){
        files_uri.clear();
        for (Patient patient : multiselect_list){
            Cursor c =PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientPages(patient.getfirstName(),patient.getlastName(),patient.getmobile() , patient.getID());
            if(c.moveToFirst()){
                do{
                    ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                    // path to /data/data/yourapp/app_data/imageDir
                    //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    // Create imageDir
                    File directory = getContext().getFilesDir();
                    File mypath=new File(directory+File.separator+"imageDir"+File.separator+c.getString(0)+".png");
                    Log.d("mypath",""+mypath.getAbsoluteFile().toString());
                    Uri photoUri  = FileProvider.getUriForFile(getContext() , "com.example.daksh.clinmdequilsmartpenlaunch.fileprovider",mypath.getAbsoluteFile());
                    Log.d("uri",""+photoUri.toString());
                    //files_uri.add( Uri.fromFile(mypath.getAbsoluteFile()));
                    files_uri.add(photoUri);

                }while (c.moveToNext());
            }
        }
        sharingintent(files_uri);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void sharingintent(ArrayList<Uri> files_uri){
        Intent intent  = new Intent();
        intent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY,getActivity().getComponentName());
        intent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE,getActivity().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setType("image/png");
        if(files_uri.size()==1){
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM,files_uri.get(0));
        }else{
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,files_uri);
        }

        startActivity(Intent.createChooser(intent,"share"));
    }


    /*@Override
    public void onPositiveClick(int from) {
        if(from==1)
        {
            if(multiselect_list.size()>0)
            {
                for(int i=0;i<multiselect_list.size();i++)
                    patients.remove(multiselect_list.get(i));

                patientRecyclerAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                Toast.makeText(getContext(), "Delete Click", Toast.LENGTH_SHORT).show();
            }
        }
        else if(from==2)
        {
            if (mActionMode != null) {
                mActionMode.finish();
            }

            SampleModel mSample = new SampleModel("Name"+user_list.size(),"Designation"+user_list.size());
            patients.add(mSample);
            patientRecyclerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
*/


    public void savetimeindb(String online) throws ParseException, IOException {
        Long epoch = System.currentTimeMillis()/1000;
        Date currentTime = Calendar.getInstance().getTime();
        String date = currentTime.toString();
        String month  = date.substring(4,7);
        String date_ =date.substring(8,10);
        String hour =  date.substring(11,13);
        String minutes  = date.substring(14,16);
        String year = date.substring(30,34);

        String datetime = year + new ImageFragment().getmonthinno(month) + date_ + hour + minutes ;

        SharedPreferences.Editor editor = sharedpreferences.edit();
        SharedPreferences prefs = sharedpreferences;
        int count = prefs.getInt(PageId, 0);
        count = count + 1;
        editor.putInt(PageId, count);
        Log.d("count", "" + count);
            // String data = DatabaseUtils.dumpCursorToString(c1);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(),imageUri);
                bitmap = new ImageFragment().getResizedBitmap(bitmap,2000);
                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());

                DotDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count), datetime, "document",count);
                FirebaseDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count) ,datetime,"document" ,count);
                // path to /data/data/yourapp/app_data/imageDir
                //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                File directory =  getContext().getFilesDir();
                File mypath=new File(directory+File.separator+"imageDir"+File.separator+count+".png");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    findViewById(R.id.downloading_progress).setVisibility(View.VISIBLE);
                   /* DataToSVG dataToSVG = new DataToSVG(getContext());
                    dataToSVG.initFirebase();
                    dataToSVG.saveToFirebase(new Pages(firstName,lastName,count,mobileNumber,(age),(datetime),sex,"NA",diagnose,ugcid),"document");*/
                    PatientDetailsDatabaseHelper.getdatabasehelper().insertData(firstName,lastName,String.valueOf(count),mobileNumber,(age),(datetime),sex,"NA",diagnose,"patient"+count,ugcid,"document");
                    findViewById(R.id.downloading_progress).setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

        editor.commit();
        new LoadPatients(getContext()).execute();
        dialog.cancel();
        floatingActionMenu.close(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.menu_search, menu);
        if(hasRss){

            if(getActivity().findViewById(R.id.search)!=null) getActivity().findViewById(R.id.search).setVisibility(View.GONE);
            menu.findItem(R.id.action_pen).setVisible(false);
            hasRss=false;
        }
        else {

            try {
                // Associate searchable configuration with the SearchView
                inflater.inflate(R.menu.menu_search, menu);
                SearchManager searchManager =
                        (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

                SearchView searchView =
                        (SearchView) menu.findItem(R.id.search).getActionView();

                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        searchString = "";
                        patientRecyclerAdapter.notifyDataSetChanged();
                        return false;
                    }
                });
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchString = query;
                        patientRecyclerAdapter.notifyDataSetChanged();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        searchString = newText;
                        patientRecyclerAdapter.notifyDataSetChanged();
                        return false;
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ((getActivity() instanceof MainActivity)) {
            ((MainActivity) getActivity()).tabFragment.isPenConnected();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.name:
                sort_category = "name";
                new LoadPatients(getContext()).execute();
                patientRecyclerAdapter.notifyDataSetChanged();
                return true;
            case R.id.mobile:
                sort_category = "mobile";
                new LoadPatients(getContext()).execute();
                patientRecyclerAdapter.notifyDataSetChanged();
                return true;
            case R.id.RegId:
                sort_category = "RegId";
                new LoadPatients(getContext()).execute();
                patientRecyclerAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    public void sethasrss(){

        hasRss=false;
    }

    @Override
    public void onDestroy() {
        bManager.unregisterReceiver(bReceiver);
        super.onDestroy();
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        next_page = menu.findItem(R.id.next_page);
        this.menu = menu;
    }

    public Menu getMenu()
    {
        return menu;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        GregorianCalendar calendarView = new GregorianCalendar(year, month, dayOfMonth);
        Date date = new Date(calendarView.getTimeInMillis());
        dateString = new SimpleDateFormat("dd MMM yy").format(date);
        patientRecyclerAdapter.notifyDataSetChanged();
    }

    private class LoadPatients extends AsyncTask<Void,Patient,Void> {

        Context context;

        LoadPatients(Context context) {
            this.context = context;
        }
        @Override
        protected Void doInBackground(Void... params) {

            patients.clear();
            Log.d("Patient List","inside loadpatients");

            Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnpatientIds();
            Log.d("Cursor count",""+c.getCount());
            if (c.moveToFirst()) {
                do {
                    String patientid = c.getString(0);
                    Cursor c1 = PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientInfo(patientid);
                    if(c1.moveToFirst()){
                        publishProgress(new Patient(c1.getString(0),c1.getString(1),c1.getString(2),c1.getString(3),c1.getString(4),c1.getString(5) , c1.getString(6)));
                    }
                } while (c.moveToNext());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Patient... values) {
            patients.add(values[0]);
            patientRecyclerAdapter.notifyDataSetChanged();
        }
    }

    static class PatientRecyclerAdapter extends RecyclerView.Adapter<PatientRecyclerAdapter.ViewHolder> {
        private  ArrayList<Patient> itemsData;
        private final MainActivity activity;
        private static String category ;
        static WebView webView;
        static MainActivity activity_2;
        static ArrayList<String> patient_pages;
        static String currentpage  = "";
        String newpage="";
        static boolean isradiovisible =  false  ;

        public ArrayList<Patient> selected_usersList=new ArrayList<>();


        PatientRecyclerAdapter(ArrayList<Patient> itemsData, MainActivity activity , ArrayList<Patient> selected_usersList , String category) {
            this.itemsData = itemsData;
            this.activity = activity;
            this.selected_usersList = selected_usersList;
            this.category = category ;
        }
        PatientRecyclerAdapter(MainActivity activity){
            this.activity = activity;
        }


        MainActivity getActivity() {
            return activity;
        }

        // Create new views (invoked by the layout_recycler manager)
        @Override
       public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

            // create a new view

            final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
            return new ViewHolder(itemLayoutView);
        }

        // Replace the contents of a view (invoked by the layout_recycler manager)


        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {


            setVisibility(true, viewHolder.itemLayout);

            if(selected_usersList.contains(itemsData.get(position)))
                viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_selected_state));
            else
                viewHolder.itemLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_normal_state));



            if (!searchString.equals("")) {
                if (!TextUtils.isEmpty(searchString)) {
                    setVisibility(false, viewHolder.itemLayout);
                    if (Pattern.compile(Pattern.quote(searchString), Pattern.CASE_INSENSITIVE).matcher(itemsData.get(position).getfirstName() + " " + itemsData.get(position).getlastName()).find()) {
                        setVisibility(true, viewHolder.itemLayout);
                    }
                    if (Pattern.compile(Pattern.quote(searchString), Pattern.CASE_INSENSITIVE).matcher(itemsData.get(position).getmobile()).find()) {
                        setVisibility(true, viewHolder.itemLayout);
                    }
                    if (Pattern.compile(Pattern.quote(searchString), Pattern.CASE_INSENSITIVE).matcher(itemsData.get(position).getUgcID()).find()) {
                        setVisibility(true, viewHolder.itemLayout);
                    }
                }
            }
            viewHolder.date.setText(getdateforview(itemsData.get(position).getDate()));
            //viewHolder.gender.setText(itemsData.get(position).getGender());
            //viewHolder.ugcid.setText(itemsData.get(position).getUgcID());
            if (itemsData.get(position).getDate().equals("0")) {

                Date date = new Date(itemsData.get(position).getDate());
                Log.d("right date ","page"+itemsData.get(position)+" "+date);
                //String dateStringText = new SimpleDateFormat("dd MMM yy").format(date);
                String dateStringText = getdateforview(date.toString());
                viewHolder.date.setText(dateStringText);
                if (!dateString.equals("")) {
                    if (!TextUtils.isEmpty(dateString)) {

                        setVisibility(false, viewHolder.itemLayout);
                        if (Pattern.compile(Pattern.quote(dateString), Pattern.CASE_INSENSITIVE).matcher(dateStringText).find()) {
                            setVisibility(true, viewHolder.itemLayout);
                        }
                    }
                }
            }
            if ((TextUtils.isEmpty(itemsData.get(position).getfirstName()) || TextUtils.isEmpty(itemsData.get(position).getlastName()))) {
               // Log.d("FromFirebase3", itemsData.get(position).getPage());
                setVisibility(false, viewHolder.itemLayout);
            } else {
                if(sort_category.equals("name")){
                    viewHolder.name.setText(itemsData.get(position).getfirstName() + " " + itemsData.get(position).getlastName());
                }else if(sort_category.equals("mobile")){
                    viewHolder.name.setText(itemsData.get(position).getmobile());
                }else if(sort_category.equals("RegId")){
                    viewHolder.name.setText(itemsData.get(position).getUgcID());
                }
                Log.d("Position : ", "" + position);

                LayoutTransition transition = viewHolder.trans.getLayoutTransition();
                try {
                    transition.setDuration(300);
                    transition.enableTransitionType(LayoutTransition.CHANGING);
                } catch (Exception e) {
                    Log.d("Error Here 2", e.toString());
                    e.printStackTrace();
                }
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Feature coming soon", Toast.LENGTH_LONG).show();
                    }
                };
                viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onClick(View v) {
                        patient_pages = new ArrayList<>();
                        hasRss=true;
                        getActivity().findViewById(R.id.main_frame).setVisibility(View.GONE);
                        ActionBar actionBar = (getActivity()).getSupportActionBar();
                        //currentpage=itemsData.get(position).getPage();
                        Log.d("current page"," "+currentpage);
                        getActivity().invalidateOptionsMenu();

                        ((MainActivity) getActivity()).replaceFragment(itemsData.get(position).getfirstName(),itemsData.get(position).getlastName(),itemsData.get(position).getmobile(),itemsData.get(position).getID());


                    }
                });


            }
        }


        public void setVisibility(boolean isVisible, View itemView) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
        private void loadWebPage(final String page, String doctor,int update) {
            Log.d("patient_pages","  "+patient_pages);
            Log.d("currentpage","  "+currentpage);
            try {
                String new_page = patient_pages.get(patient_pages.indexOf(currentpage) + update);
                currentpage = new_page;
                Cursor c =PatientDetailsDatabaseHelper.getdatabasehelper().returnPageDate(new_page);
                if(c.moveToNext()) {
                    String dateStringText = new SimpleDateFormat("dd MMM yy").format(new Date(c.getLong(0)));
                    Toast.makeText(activity_2, "" + dateStringText, Toast.LENGTH_SHORT).show();
                }

                // clearFullViewAndScale();
                // getSVGPage(page, doctor);
                //new SaveHTMLs(getContext()).saveSVGPage(page,doctor);
         /*       saveSVGPage(new_page, doctor);
                File localfile = activity_2.getBaseContext().getFileStreamPath(doctor + new_page + ".htm");
                webView.loadUrl("file://" + localfile.getAbsolutePath());*/
                File localfile = activity_2.getBaseContext().getFileStreamPath(doctor + new_page + ".htm");
                if (!localfile.exists()) {
                    //new SaveHTMLs(activity_2).saveSVGPage(new_page,new Doctor().getRegistrationID());
                    //saveSVGPage(new_page, doctor,activity_2);
                }else{

                    webView.loadUrl("file://" + localfile.getAbsolutePath());
                }


                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.setVisibility(View.VISIBLE);
                ZoomImageView fullpageview = (ZoomImageView) activity_2.findViewById(R.id.imageView);
                fullpageview.setVisibility(View.VISIBLE);


                FrameLayout main_frame = (FrameLayout) activity_2.findViewById(R.id.main_frame);
                main_frame.setVisibility(View.GONE);
            }catch (Exception e){
                Toast.makeText(activity_2, "No more records", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
/*
            webView.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>" + , "text/html", "UTF-8", null);
*/
            /*webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
*/            //webView.loadUrl("file:///android_asset/fullpage.htm");

        }
        private String getdateforview(String dateTime) {
            String newdate  = "" ;
            newdate  = dateTime.substring(6,8)+ " "+new MainActivity().getmonth(dateTime.substring(4,6))+" "+dateTime.substring(0,4);
            String newtime = dateTime.substring(8,10) + ":"+dateTime.substring(10,12);
            return  newdate ;
        }

        private void saveSVGPage(final String page, final String doctor,BottomNavigationActivity activity_2) {
            try {
                Log.d("SyncService Saving SVG ", doctor + page + ".htm");

                final File localfile = activity_2.getBaseContext().getFileStreamPath(doctor + page + ".htm");
                if (!localfile.exists()) {
                    localfile.createNewFile();
                }
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference pageRef = mStorageRef.child("Original Pages").child(new Doctor().getRegistrationID())
                        .child(doctor + page + ".htm");
                pageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("SyncService Saved SVG ", doctor + page + ".htm");
                        webView.loadUrl("file://" + localfile.getAbsolutePath());
                    }
                });


            } catch (Exception e) {
                Log.d(" Error SVG " + doctor + page + ".htm", e.toString());
                e.printStackTrace();
            }
        }


        // Return the size of your itemsData (invoked by the layout_recycler manager)
        @Override
        public int getItemCount() {
            return itemsData.size();
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final RelativeLayout trans;
            private View itemLayout;

            private TextView date;
            private TextView name;
            private TextView gender ;
            private TextView ugcid ;


            public ViewHolder(View view) {
                super(view);

                date = (TextView) view.findViewById(R.id.date);
                trans = (RelativeLayout) view.findViewById(R.id.transLayout);
                name = (TextView) view.findViewById(R.id.name);
                //gender = (TextView)view.findViewById(R.id.gender);
                //ugcid = (TextView)view.findViewById(R.id.ugcid);
                itemLayout = view.findViewById(R.id.itemLayout);



            }
        }
    }

}

