package com.pnf.penequillaunch.mainActivity;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.pnf.penequillaunch.Utils.RecyclerItemClickListener;
import com.pnf.penequillaunch.others.DataToSVG;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.FirebaseDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.Pages;
import com.pnf.penequillaunch.others.Patient;
import com.pnf.penequillaunch.others.PatientDetailsDatabaseHelper;
import com.pnf.penequillaunch.test.MainActivity;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.Doctor;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.pnf.penequillaunch.mainActivity.ImageFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.pnf.penequillaunch.test.MainActivity.MyPREFERENCES;
import static com.pnf.penequillaunch.test.MainActivity.date;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    ActionBar actionBar;
    public ArrayList<String> arrayList;
    public PatientInfoRecyclerAdapter adapter;
    View v;
    TextView patientage , patientnumber , patientgender , patientvisits ;
    String firstName , lastName , mobileNumber , gender , age , visits , patientid ;
    FloatingActionButton add_document ;
    private String downloadfilename;
    public static final String PageId = "pageid";
    Uri imageUri;
    SharedPreferences sharedpreferences;

    ArrayList<String> multiselect_list = new ArrayList<>();
    ArrayList<Uri> files_uri = new ArrayList<>();
    boolean isMultiSelect = false;

    ActionMode mActionMode;

    Menu context_menu;
    public PatientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_patient, container, false);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        Log.d("PageFragment","inside recent ");
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        arrayList = new ArrayList<>();
        setHasOptionsMenu(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setRecycleChildrenOnDetach(false);
        add_document = (FloatingActionButton) findViewById(R.id.add_document_patient);
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
        adapter = new PatientInfoRecyclerAdapter(arrayList, (MainActivity) getActivity(),multiselect_list);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);
                else
                    Toast.makeText(getContext(), "Details Page", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<String>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        Log.d("inside","mactionmode null");
                        mActionMode =getActivity().startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);

            }
        }));
        Bundle arguments = getArguments();
        firstName = arguments.getString("firstName");
        lastName = arguments.getString("lastName");
        patientid  =arguments.getString("patientid");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(firstName +" "+lastName);

        mobileNumber = arguments.getString("mobileNumber");
        patientnumber = (TextView)findViewById(R.id.phone_txt);
        patientnumber.setText(mobileNumber);
        Cursor c   = PatientDetailsDatabaseHelper.getdatabasehelper().returngenderandage(mobileNumber);
        if(c.moveToFirst()){
            gender = c.getString(0);
            age  = c.getString(1);
        }
        patientgender = (TextView)findViewById(R.id.gender_txt);
        patientage = (TextView)findViewById(R.id.age_txt);
        patientvisits = ( TextView)findViewById(R.id.visits_txt);
        patientgender.setText(gender);
        patientage.setText(age);
        Cursor c1  =PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientdates(firstName,lastName,mobileNumber , patientid);
        if(c1.moveToFirst()){
            visits = String.valueOf(c1.getCount());
        }
        patientvisits.setText(visits);
        LoadData();
        findViewById(R.id.next_bundle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Doctor.currentBundle += 1;
            }
        });
        return v;
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(arrayList.get(position)))
                multiselect_list.remove(arrayList.get(position));
            else
                multiselect_list.add(arrayList.get(position));


            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }
    public void refreshAdapter()
    {
        adapter.selected_usersList=multiselect_list;
        adapter.pageslist=arrayList;
        adapter.notifyDataSetChanged();
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
            multiselect_list = new ArrayList<String>();
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
        for (String dates : multiselect_list){
            Log.d("dates",""+ dates.toString());
            Cursor c =PatientDetailsDatabaseHelper.getdatabasehelper().returnPageIdsinDate(dates,mobileNumber,firstName,lastName);
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

        String datetime = year + new MainActivity().getmonthinno(month) + date_ + hour + minutes ;

        SharedPreferences.Editor editor = sharedpreferences.edit();        SharedPreferences prefs = sharedpreferences;
        int count = prefs.getInt(PageId, 0);
        count = count + 1;
        editor.putInt(PageId, count);
        Log.d("count", "" + count);

        Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientPages(firstName,lastName,mobileNumber,patientid);
        if(c.moveToFirst()){
            String pageid = c.getString(0);
            Cursor c1 = PatientDetailsDatabaseHelper.getdatabasehelper().returnPageData(pageid);
            // String data = DatabaseUtils.dumpCursorToString(c1);
            if(c1.moveToFirst()) {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(),imageUri);
                bitmap = getResizedBitmap(bitmap,2000);
                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                DotDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count), datetime,"document" ,count);
                FirebaseDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count), datetime, "document",count);
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                // Create imageDir
                //File mypath=new File(directory,count+".png");
                //File mypath = new File(getContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir",count+".png");
                File mypath = new File(getContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir"+File.separator+count+".png");
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                  /*  DataToSVG dataToSVG = new DataToSVG(getContext());
                    dataToSVG.initFirebase();
                    dataToSVG.saveToFirebase(new Pages(c1.getString(0), c1.getString(1), count, c1.getString(3), (c1.getString(4)),
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

    public void LoadData( )  {
        Cursor c = PatientDetailsDatabaseHelper.getdatabasehelper().returnPatientdates(firstName,lastName,mobileNumber  ,patientid);

            if (c.moveToFirst()) {

                arrayList.clear();
                do {

                    String patientID = c.getString(0);

                    arrayList.add(patientID);


                    Log.d("retrieving page", "" + patientID);

                } while (c.moveToNext());


        }
            //publishProgress();



        if (adapter != null)
            adapter.notifyDataSetChanged();
        //new ShowSavedPages().execute();
    }

    private View findViewById(int id) {
        return v.findViewById(id);
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
   public class PatientInfoRecyclerAdapter extends RecyclerView.Adapter<PatientInfoRecyclerAdapter.MyViewHolder> {

       private List<String> pageslist;
       private final MainActivity activity;
       public ArrayList<String> selected_usersList=new ArrayList<>();



       public PatientInfoRecyclerAdapter(List<String> pageslist , MainActivity activity , ArrayList<String> selected_usersList) {
           this.pageslist = pageslist;
           this.activity  = activity;
           this.selected_usersList = selected_usersList;
       }

       @Override
       public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View itemView = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.item_patient_info, parent, false);

           return new MyViewHolder(itemView);
       }

       @Override
       public void onBindViewHolder(final MyViewHolder viewholder, int position) {

            if(selected_usersList.contains(pageslist.get(position)))
                viewholder.itemLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_selected_state));
            else
                viewholder.itemLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.list_item_normal_state));


           try{
                Cursor c =  PatientDetailsDatabaseHelper.getdatabasehelper().returnPagesinDate(pageslist.get(viewholder.getAdapterPosition()),mobileNumber , firstName , lastName , patientid);
                if(c.moveToFirst()){
                    if(c.getInt(0)==1){
                        viewholder.record.setText("1 record");
                    }
                    else{
                        viewholder.record.setText(c.getInt(0)+" records");
                    }
                }
               String date  = getdateforview(pageslist.get(viewholder.getAdapterPosition()));
               viewholder.name.setText(date);
               viewholder.item.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       ((MainActivity) getActivity()).replaceFragmenttoImageFragment(firstName,lastName,mobileNumber , viewholder.getAdapterPosition() , patientid);
                   }
               });
           }catch (Exception e){
               e.printStackTrace();
           }
       }
       private String getdateforview(String dateTime) {
           String newdate  = "" ;
           newdate  = dateTime.substring(6,8)+ " "+new MainActivity().getmonth(dateTime.substring(4,6))+" "+dateTime.substring(0,4);
           //String newtime = dateTime.substring(8,10) + ":"+dateTime.substring(10,12);
           return  newdate ;
       }

       @Override
       public int getItemCount() {
           return pageslist.size();
       }

       public  class MyViewHolder extends RecyclerView.ViewHolder {
           public final View itemLayout ;
           public final TextView name;
           public final View item;
           public final TextView record ;


           MyViewHolder(final View itemLayoutView) {
               super(itemLayoutView);
               item = itemLayoutView;
               name = (TextView) itemLayoutView.findViewById(R.id.pagetxt);
               record = (TextView)itemLayoutView.findViewById(R.id.records);
                itemLayout = itemLayoutView.findViewById(R.id.itemLayout);

           }

       }
   }

}


