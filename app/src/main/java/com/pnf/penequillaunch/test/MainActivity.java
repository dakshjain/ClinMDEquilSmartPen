package com.pnf.penequillaunch.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pnf.bt.lib.PNFDefine;
import com.pnf.bt.lib.PNFPenController;
import com.pnf.bt.lib.PenDataClass;
import com.pnf.bt.lib.memory.DataImportFigure;
import com.pnf.bt.lib.memory.DataImportMakeFileThread;
import com.pnf.penequillaunch.activities.ParentActivity;
import com.pnf.penequillaunch.calibration.CalibrationPointActivity;
import com.pnf.penequillaunch.dataimport.DataImportActivity;
import com.pnf.penequillaunch.dataimport.DataImportPreView;
import com.pnf.penequillaunch.drawingview.DrawView;
import com.pnf.penequillaunch.drawingview.DrawViewActivity;
import com.pnf.penequillaunch.equilsdk.Const;
import com.pnf.penequillaunch.equilsdk.PenClientCtrl;
import com.pnf.penequillaunch.mainActivity.ImageFragment;
import com.pnf.penequillaunch.mainActivity.PageListFragment;
import com.pnf.penequillaunch.mainActivity.PatientFragment;
import com.pnf.penequillaunch.mainActivity.SettingsFragment;
import com.pnf.penequillaunch.mainActivity.TabFragment;
import com.pnf.penequillaunch.mainActivity.AnalyticsFragment;
import com.pnf.penequillaunch.others.DataToSVG;
import com.pnf.penequillaunch.others.DeviceDataHandler;
import com.pnf.penequillaunch.others.DeviceDataHelper;
import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.DownloadedPages;
import com.pnf.penequillaunch.others.FirebaseDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.Page;
import com.pnf.penequillaunch.others.Pages;
import com.pnf.penequillaunch.others.Patient;
import com.pnf.penequillaunch.others.PatientDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.ZoomImageView;


public class MainActivity extends ParentActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
	PowerManager.WakeLock mWakeLock;
	ActionBar actionBar;
	int back = 0;
	final byte SLEEPVIEW_SHOWPOPUP = 0x00;
	final byte SLEEPVIEW_CLOSEPOPUP = 0x01;
	final byte SLEEPVIEW_STARTPOPUP = 0x02;

	final byte REQUEST_DRAWVIEW = 0x00;
	final byte REQUEST_CALIBRATIONVIEW = 0x01;
	final byte REQUEST_TIMETESTVIEW = 0x02;
	final byte REQUEST_THRESHOLDVIEW = 0x03;

	final byte ALERTVIEW_FAIL_LISTENING = 0x00;
	final byte ALERTVIEW_APP_EXIT = 0x01;

	final byte SHOWPOPUP_PEN_CONNECT = 0x00;
	final byte SHOWPOPUP_PEN_DISCONNECT = 0x01;
	final byte SHOWPOPUP_PEN_ALIVE = 0x02;
	FloatingActionButton fab;
	boolean lock = true;
	boolean isCheckSleepView = false;
	final int penSleepDelay = 600;
	int savePenSleepRemainingTime;
	int savePenAliveSec;
	int curPenAliveSec;
	int penCheckAliveCnt;
	int drawviewactivitycount = 0;
	Timer penAliveTimer = null;

	int pagecount = 0;
	static int filedownloadcount = 0;
	public static int diFileCount = 0;
	int penErrorCnt = 0;
	int packetCnt = 0;
	int downCnt = 0;
	int moveCnt = 0;
	int upCnt = 0;
	int errCntX = 0;
	int errCntY = 0;
	int beforeRawX = -1;
	int beforeRawY = -1;

	boolean isHasFocus = false;

	DataImportPreView preView;
	ProgressDialog mProgress;
	String downLoadFileName;
	List<PenDataClass> downLoadPenDataList;
	List<PenDataClass> downLoadAccPenDataList;

	boolean isFileDownLoading = false;
	boolean isFileDeleting = false;
	int mDownLoadPageCount = 0;

	int deleteItemIndex = 0;
	private boolean workisdone = false;

	private FirebaseJobDispatcher jobDispatcher;
	static List<String> dataImportFileNameList;

	static String filename = "";
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String PageId = "pageid";
	SharedPreferences sharedpreferences;
	private boolean pendidata = false;
	BottomNavigationView navigation;
	NotificationCompat.Builder mBuilder ;
	NotificationManager mNotificationManager ;
	View penSleepView;
	int id  =1;
	YourAsyncTask yourAsyncTask ;
	public static boolean activityopened = false;

	 ImageView drawViewBGImgView;

	FrameLayout drawNoteLayer;
	 DrawView drawView;
	 public static Thread t ;
	 private  BluetoothAdapter BA ;

	 public static String firstname , lastname , followup , ugcid  , mobile , gender , patient_id  , page , diagnose , age , timestamp,date;
	public static String firstname1 , lastname1 , followup1 , ugcid1 , mobile1 , gender1 , patient_id1  , page1 , diagnose1 , age1 , timestamp1,date1;

	private static final int REQUEST_ENABLE_BT = 9;
	private boolean writing = false;
	private boolean force_stop = false;

	@Override
	protected void onResume() {
		super.onResume();
		drawviewactivitycount = 0;
		IntentFilter bluetoothfilter = new IntentFilter();
		bluetoothfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(Bluetoothhandler,bluetoothfilter);
		if(MainDefine.penController!=null) {
			MainDefine.penController.SetRetObj(penHandler);
			MainDefine.penController.SetRetObjForEnv(PenHandlerEnv);
			MainDefine.penController.SetRetObjForMsg(messageHandler);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d("inside","onPause");
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onDestroy() {
		super.onDestroy();

		stopPenAliveTimer();
		Log.d("Destroying","now");
		if(writing){
			Log.d("mBitmap","not null");
		/*	Long epoch = System.currentTimeMillis()/1000;
			try {
				savetimeindb(drawView.mBitmap,epoch.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}*/
		//new LiveSave(drawView.mBitmap).execute();
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		startJob();
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		/*if((t!=null && t.isAlive()) || (anotherBarThread!=null && anotherBarThread.isAlive())) {
			stopJob();
			Log.d("Bottom","Stop");
		}*/
	}
	public MainActivity getActivity(){
		return this;
	}

	private void getDetailsFromFirebase(){
		final FirebaseDatabase database   = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child("Patient Id");
		myRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener(){
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for(DataSnapshot dataSnapshots:dataSnapshot.getChildren()){
					String test =  dataSnapshots.getKey().toString();
					FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							String patientid  = dataSnapshot.getKey();
							Log.d("patientid",""+patientid.toString());
							Patient value= dataSnapshot.getValue(Patient.class);
							age  = value.getAge();
							patient_id=value.getID();
							firstname= value.getfirstName();
							followup = value.getfollowUp();
							lastname = value.getlastName();
							mobile = value.getmobile();
							gender = value.getGender();
							ugcid= value.getUgcID();

							Log.d("firstname",""+id);

						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
					FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Pages").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							for(DataSnapshot dataSnapshots : dataSnapshot.getChildren()){
								String test = dataSnapshots.getKey().toString();
								Page value  = dataSnapshots.getValue(Page.class);
								 diagnose = value.getDiagnose();
								 page = value.getPage();
								 timestamp = value.getTimestamp();
								Log.d("value",""+dataSnapshots.getKey());

								addDebugText(test);
								try{
									PatientDetailsDatabaseHelper.getdatabasehelper().insertData(firstname,lastname,page,mobile,age,timestamp,gender,followup,diagnose,patient_id,ugcid,"page");

								}catch(SQLException e ){
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
					Log.d("id",test);

					FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Documents").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							for(DataSnapshot dataSnapshots : dataSnapshot.getChildren()){
								String test = dataSnapshots.getKey().toString();
								Page value  = dataSnapshots.getValue(Page.class);
								diagnose = value.getDiagnose();
								page = value.getPage();
								timestamp = value.getTimestamp();
								Log.d("value",""+dataSnapshots.getKey());

								addDebugText(test);
								try{
									PatientDetailsDatabaseHelper.getdatabasehelper().insertData(firstname,lastname,page,mobile,age,timestamp,gender,followup,"NA",patient_id,ugcid,"document");
									PatientDetailsDatabaseHelper.getdatabasehelper().updatePatientId(firstname,lastname,mobile,page,patient_id);

								}catch(SQLException e ){
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});



				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}
	/*public void getdetailsfromfirebase_documents(){
		final FirebaseDatabase database   = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child("Patient Id");
		myRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener(){
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				for(DataSnapshot dataSnapshots:dataSnapshot.getChildren()){
					String test =  dataSnapshots.getKey().toString();
					FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							String patientid  = dataSnapshot.getKey();
							Log.d("patientid",""+dataSnapshot.getValue().toString());
							Patient value= dataSnapshot.getValue(Patient.class);
							age1  = value.getAge();
							patient_id1=value.getID();
							firstname1= value.getfirstName();
							followup1 = value.getfollowUp();
							lastname1 = value.getlastName();
							mobile1 = value.getmobile();
							gender1 = value.getGender();
							ugcid1= value.getUgcID();
						}
						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
					FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Documents").addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							for(DataSnapshot dataSnapshots : dataSnapshot.getChildren()){
								String test = dataSnapshots.getKey().toString();
								Page value  = dataSnapshots.getValue(Page.class);
								diagnose1 = value.getDiagnose();

								page1 = value.getPage();

								timestamp1= value.getTimestamp();
								Log.d("value",""+dataSnapshots.getKey()+", "+firstname1 );
								try{
									PatientDetailsDatabaseHelper.getdatabasehelper().insertData(firstname1,lastname1,page1,mobile1,age1,timestamp1,gender1,"NA","NA",patient_id1,ugcid1,"document");
								}catch(SQLException e ){
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
					Log.d("id",test);
				}
			}
			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}*/
	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		isHasFocus = hasFocus;
	}

	@Override
	public void onBackPressed() {
		boolean changevisibility  = true; ;
		int count  = getSupportFragmentManager().getBackStackEntryCount();
		List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
		int size  = fragments.size();
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
		    if(size!=1)
			    getSupportFragmentManager().popBackStack();
			if(count>2) {
				changevisibility=  false;
			}


		} else {
			this.finish();
		}
		if (actionBar != null) {
			actionBar.setTitle(R.string.title_home);
			actionBar.setSubtitle(null);
		}
		View v = findViewById(R.id.main_frame);
		try{
			findViewById(R.id.fragment_patient).setVisibility(View.GONE);
		}catch (Exception e){
			e.printStackTrace();
		}
		if (v.getVisibility() == View.INVISIBLE | v.getVisibility() == View.GONE) {
			//clearFullViewAndScale();
			if(changevisibility) {
				v.setVisibility(View.VISIBLE);
				Log.d("home button", "back pressed");
				refreshList();
			}
		} else {
			if (back == 0)
				finishAffinity();
			else {
				navigation.setSelectedItemId(R.id.navigation_home);
				tabFragment.isPenConnected();
			}
		}

	}

	public void refreshList() {
		if (pageListFragment.isAdded()) {
			pageListFragment.LoadData();

			pageListFragment.adapter.notifyDataSetChanged();
		}
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo  = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo !=null && activeNetworkInfo.isConnected();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode,resultCode,data);
		boolean isResultOK = resultCode == Activity.RESULT_OK ? true : false;
		String debugStr = "";

		switch (requestCode) {
			case REQUEST_DRAWVIEW:
				if (isResultOK) {
					debugStr = data.getExtras().getString("debug");
					addDebugText(debugStr);
				}
				break;
			case REQUEST_CALIBRATIONVIEW:
				if (isResultOK) {
					debugStr = data.getExtras().getString("debug");
					addDebugText(debugStr);
				}
				break;
			case REQUEST_TIMETESTVIEW:
				break;
			case REQUEST_THRESHOLDVIEW:
				break;
			case REQUEST_ENABLE_BT :
				checkbluetoothstate();
				
		}
	}

	private void checkbluetoothstate() {
		if (BA == null) {
			Toast.makeText(penClientCtrl, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
		} else {
			if (BA.isEnabled()) {
				if (BA.startDiscovery()) {
					tabFragment.animatePen();
				}
				else{
					PenClientCtrl.isconnected=true;
					tabFragment.isPenConnected();
				}
			}
			else{
				//Toast.makeText(penClientCtrl, "Failed to connect", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@SuppressLint("Wakelock")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bottom_navigation);
		File mypath = new File(getFilesDir().getAbsolutePath()+File.separator+"imageDir");
		Log.d("mypath",""+mypath.toString());
		if(!mypath.exists()){
			mypath.mkdirs();
			Log.d("create ",""+mypath.toString());
		}
		/*RectF rectFT_8X5 = new RectF(
				1804, 411,
				5363, 5125);*/
		/*RectF rectFT_8X5 = new RectF(
				1850, 435,
				5155, 5285);*/
		RectF rectFT_8X5 = new RectF(
				1868, 434,
				5226, 5254);
		WindowManager windowManager = getWindowManager();
        Display display =  windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int heightpixels = displayMetrics.heightPixels;
        Log.d("heightpixels"," "+heightpixels);

		Point size =  new Point();
		display.getSize(size);
		int heigth  = size.y;
		Log.d("height",""+heigth);
		double density = getResources().getDisplayMetrics().density;
		Log.d("density",""+density);
		PointF[] calScreenPoint = new PointF[4];
		PointF[] calResultPoint = new PointF[4];
		calResultPoint[0] = new PointF(rectFT_8X5.left, rectFT_8X5.top);
		calResultPoint[1] = new PointF(rectFT_8X5.right, rectFT_8X5.top);
		calResultPoint[2] = new PointF(rectFT_8X5.right, rectFT_8X5.bottom);
		calResultPoint[3] = new PointF(rectFT_8X5.left, rectFT_8X5.bottom);

		calScreenPoint[0] = new PointF(0.0f, 0.0f);
		calScreenPoint[1] = new PointF(MainDefine.iDisGetWidth, 0.0f);
		calScreenPoint[2] = new PointF(MainDefine.iDisGetWidth, MainDefine.iDisGetHeight);
		calScreenPoint[3] = new PointF(0.0f, MainDefine.iDisGetHeight);

		MainDefine.penController.setCalibrationData(calScreenPoint, 0, calResultPoint);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),REQUEST_ENABLE_BT);
		preView = new DataImportPreView(getApplicationContext(),getActivity());
		pageListFragment = new PageListFragment();
		tabFragment = TabFragment.getTabFragment();
		BA  =BluetoothAdapter.getDefaultAdapter();
		IntentFilter bluetoothfilter = new IntentFilter();
		bluetoothfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		registerReceiver(Bluetoothhandler,bluetoothfilter);
		BA.startDiscovery();
		if (tabFragment != null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.main_frame, tabFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		DataImportPreView fullPageView  = new DataImportPreView(this ,getActivity());
		FrameLayout.LayoutParams para = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		para.gravity = Gravity.CENTER;
		penSleepView = ((ViewStub) findViewById(R.id.penSleepStub)).inflate();

		penSleepView.setVisibility(View.GONE);

		//((ZoomLayout) findViewById(R.id.imageView)).addView(fullPageView, 0, para);
		navigation = (BottomNavigationView) findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(this);
		jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
		actionBar = getSupportActionBar();
		sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag");
		mWakeLock.acquire();
		drawViewBGImgView= (ImageView)findViewById(R.id.imageView);
		drawView = (DrawView)findViewById(R.id.drawView_main);
		Runnable r = new Bar(getApplicationContext(),getBaseContext());
		t = new Thread(r);
		t.start();
		ViewTreeObserver treeObserver = fullPageView.getViewTreeObserver();
		treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Log.d("fullpage init height", drawView.getHeight() + "");
				Log.d("fullpage init width", drawView.getWidth() + "");
				FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) drawView.getLayoutParams();
				layoutParams.width = (int) (drawView.getHeight() / (1.4));
				drawView.setLayoutParams(layoutParams);
				Log.d("fullpage height", drawView.getHeight() + "");
				Log.d("fullpage width", drawView.getWidth() + "");

				drawView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				float f1 = (drawView.getHeight() / (float) Const.Coordinates.pageVerticalPixels);
				DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
				float f;
				float density = displayMetrics.density * 160f;
				if (density < 350) f = displayMetrics.density * 160f * 0.0225f;
				else f = displayMetrics.density * 160f * 0.025f;
				Log.d("Float f",""+f);

				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

				SharedPreferences.Editor editor = preferences.edit();
				editor.putFloat("paper_scale", f+3.8f);
				editor.commit();
				editor.apply();
			}
		});
		getDetailsFromFirebase();
		//getdetailsfromfirebase_documents();
		startJob();
		savedevicedata();


	}
	private void savedevicedata(){
		Cursor c  = DeviceDataHelper.getdatabasehelper().returnData();
		int count  = c.getCount();
		Log.d("count",""+count);
		int count2 =  0  ;
		Log.d("count2",""+count2);
		if(c.moveToFirst()){

			do {
				Log.d("counter",""+count2);

				count2++;
				String type  = c.getString(2);
				if(type.equals("down")){

					Log.d("inside ","down");
					drawView.DoMouseDown(c.getFloat(0) ,c.getFloat(1));
					if(count2==count){
						try {
							Log.d("inside","saving1");
							savetimeindb(drawView.mBitmap,"device data");
							drawView.clearPath();

						} catch (ParseException e) {
							e.printStackTrace();
						}

					}
				}
				else{
					Log.d("inside","move");
					drawView.DoMouseDragged(c.getFloat(0) ,c.getFloat(1));
					drawView.invalidatePath();
					if(count2==count){
						try {
							Log.d("inside","saving");
							savetimeindb(drawView.mBitmap,"device data");
							drawView.btnEraser();
							drawView.clearPath();
							/*drawView.invalidate();
							drawView.btnEraser();*/

						} catch (ParseException e) {
							e.printStackTrace();
						}

					}
				}
			}while (c.moveToNext());

		}
	}
	public void replaceFragment(String firstName , String lastName , String mobileNumber  , String patientid){
		Log.d("patient","replace fragment");
		findViewById(R.id.drawView_main).setVisibility(View.GONE);
		findViewById(R.id.imageView).setVisibility(View.GONE);
		Bundle arguments = new Bundle();
		arguments.putString("firstName",firstName);
		arguments.putString("lastName",lastName);
		arguments.putString("mobileNumber",mobileNumber);
		arguments.putString("patientid",patientid);
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		PatientFragment mFrag = new PatientFragment();
		mFrag.setArguments(arguments);
		t.replace(R.id.activity_main, mFrag);
		t.addToBackStack(null);
		t.commit();
	}
	public void replaceFragmenttoImageFragment(String firstName , String lastName , String mobileNumber , int position , String patientid){
		Log.d("patient","replace fragment");
		/*findViewById(R.id.drawView_main).setVisibility(View.GONE);
		findViewById(R.id.imageView).setVisibility(View.GONE);*/
		Bundle arguments = new Bundle();
		arguments.putString("firstName",firstName);
		arguments.putString("lastName",lastName);
		arguments.putString("mobileNumber",mobileNumber);
		arguments.putString("patientid",patientid);
		arguments.putInt("position",position);
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		ImageFragment mFrag = new ImageFragment();
		mFrag.setArguments(arguments);
		t.replace(R.id.activity_main, mFrag);
		t.addToBackStack(new PatientFragment().getClass().getName());
		t.commit();
	}

	/*
     * top 1 menu button
     */
	public void drawingBtnClicked(View v) {
		Intent intent = new Intent(MainActivity.this, DrawViewActivity.class);
		startActivityForResult(intent, REQUEST_DRAWVIEW);
	}

	public void calibrationBtnClicked(View v) {

		if (MainDefine.penController.getExistCaliData()) {
			addDebugText("calibration data exist");
		} else {
			addDebugText("calibration data not exist");
		}

		if (MainDefine.penController.getModelCode() == 0) {
			addDebugText("smart pen");
		} else if (MainDefine.penController.getModelCode() == 1) {
			addDebugText("lollol pen");
		} else if (MainDefine.penController.getModelCode() == 2) {
			addDebugText("Equil SmartPen");
		} else if (MainDefine.penController.getModelCode() == 3) {
			addDebugText("Equil SmartPen2");
		} else if (MainDefine.penController.getModelCode() == 4) {
			addDebugText("Smart Maker");
		}

		Intent intent = new Intent(MainActivity.this, CalibrationPointActivity.class);
		startActivityForResult(intent, REQUEST_CALIBRATIONVIEW);
	}
/*
	public void stopPenBtnClicked(View v) {
		addDebugText("stop pen");
		MainDefine.penController.stopPen();
	}*/

	public void stopPenBtnClicked(View v) {
		addDebugText("inside stop pen");
		if(BA.isEnabled()){
			Log.d("pen message","ba enabled");
			BA.disable();
		}
		Log.d("pen message","disconnected");
		MainDefine.penController.stopPen();
	}
/*	public void startPenBtnClicked(View v) {
		MainDefine.penController.startPen();
	}

	public void startpen() {
		MainDefine.penController.startPen();
	}

	public void restartPenBtnClicked(View v) {
		MainDefine.penController.restartPen();
	}*/


	public void DIClicked(View v) {
		if (MainDefine.penController.isPenMode()) {
			if (diFileCount > 0) {
				Intent intent = new Intent(MainActivity.this, DataImportActivity.class);
				startActivity(intent);
			}
		}
	}

	public void packetCountClearClicked(View v) {
		packetCnt = 0;
		errCntX = 0;
		errCntY = 0;
		beforeRawX = -1;
		beforeRawY = -1;


	}

	public void countClearClicked(View v) {
		downCnt = 0;
		moveCnt = 0;
		upCnt = 0;


	}

	public void penSleepBtnClicked(View v) {
		penPopupHandler.sendEmptyMessage(SLEEPVIEW_CLOSEPOPUP);
	}

	void showAlertView(int alertTag) {
		AlertDialog.Builder builder = null;
		AlertDialog alert = null;

		builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(false);

		switch (alertTag) {
			case ALERTVIEW_FAIL_LISTENING:
				builder.setTitle(getResources().getString(R.string.FAIL_LISTENING_MSG));
				builder.setPositiveButton(getResources().getString(R.string.COMMON_OK), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.setNegativeButton(getResources().getString(R.string.COMMON_CANCEL), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
			case ALERTVIEW_APP_EXIT:
				builder.setTitle(getResources().getString(R.string.QUIL_APP));
				builder.setPositiveButton(getResources().getString(R.string.COMMON_OK), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						setResult(RESULT_OK, null);
						finish();
					}
				});
				builder.setNegativeButton(getResources().getString(R.string.COMMON_CANCEL), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				break;
		}

		alert = builder.create();
		alert.show();
	}


	@SuppressLint("HandlerLeak")
	Handler penHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onPenEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
		}
	};

	void onPenEvent(int penState, int RawX, int RawY, Object obj) {
		penPopupHandler.sendEmptyMessage(SLEEPVIEW_STARTPOPUP);

		if (MainDefine.penController == null) {
			addDebugText("PenController is not set");
			return;
		}

		PenDataClass penData = (PenDataClass) obj;
		PointF ptConv = MainDefine.penController.GetCoordinatePostionXY(RawX, RawY, penData.bRight);
		int penTemperature = penData.Pen_Temperature;
		addDebugText("pen temperature"+penTemperature);
		int penPressure = penData.Pen_Pressure;
		packetCnt++;

		/*
		 * MARKER PEN STATE
		 */

		switch (penState) {
			case PNFDefine.PEN_DOWN:
				addDebugText("Pen_down");

				inserdateindevicehndler("down",ptConv.x,ptConv.y);
				drawView.DoMouseDown(ptConv.x ,ptConv.y);
				downCnt++;
//			addDebugText("RawX = " + RawX + "	RawY = " + RawY);
				break;
			case PNFDefine.PEN_MOVE:
				writing  = true ;
				addDebugText("PEN_MOVE");
				findViewById(R.id.main_frame).setVisibility(View.GONE);

				drawView.setVisibility(View.VISIBLE);

				inserdateindevicehndler("move",ptConv.x,ptConv.y);
				drawView.DoMouseDragged(ptConv.x ,ptConv.y);
				drawView.invalidatePath();
				if(!activityopened) {
					activityopened = true ;
					//startActivityForResult(new Intent(this, DrawViewActivity.class), REQUEST_DRAWVIEW);
				}
				break;
			case PNFDefine.PEN_UP:
				addDebugText("PEN_UP");
				drawView.DoMouseUp(ptConv.x ,ptConv.y);
				drawView.invalidatePath();
				upCnt++;
				break;
			case PNFDefine.PNF_MSG_NEW_PAGE_BTN:
				addDebugText("PEN_PAGE_NEW_BTN1");
				break;
			case PNFDefine.PEN_PAGE_NEW:
				addDebugText("PEN_PAGE_NEW");
				break;
			case PNFDefine.PEN_PAGE_ADD:
				addDebugText("PEN_PAGE_ADD");
				break;
			case PNFDefine.PEN_PAGE_INSERT:
				addDebugText("PEN_PAGE_INSERT");
				break;

			case PNFDefine.PEN_HOVER:
				addDebugText("PEN_HOVER1");
				break;
			case PNFDefine.PEN_HOVER_DOWN:
				addDebugText("PEN_HOVER_DOWN");
				break;
			case PNFDefine.PEN_HOVER_MOVE:
				addDebugText("PEN_HOVER_MOVE");

				break;
		}
	}
	private void inserdateindevicehndler(String type, float x, float y){
		Date currentTime = Calendar.getInstance().getTime();
		String date = currentTime.toString();
		String month  = date.substring(4,7);
		String date_ =date.substring(8,10);
		String hour =  date.substring(11,13);
		String minutes  = date.substring(14,16);
		String year = date.substring(30,34);

		String datetime = year + getmonthinno(month) + date_ + hour + minutes ;
		DeviceDataHelper.getdatabasehelper().insertData(datetime,'"'+type+'"',x,y);
	}

	private void addDebugText(String s) {
		Log.d("message", s);
	}

	private final BroadcastReceiver Bluetoothhandler = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
													BluetoothAdapter.ERROR);
				switch(state){
					case BluetoothAdapter.STATE_CONNECTING:
						Log.d("Bluetooth state","connecting");
						break;
					case BluetoothAdapter.STATE_CONNECTED:
						Log.d("Bluetooth state","connected");
						break;

				}
			}
			else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
				Log.d("Bluetooth state","discovery started");
			}
		}
	};


	@SuppressLint("HandlerLeak")
	Handler PenHandlerEnv = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onPenEnvEvent(msg.what, msg.obj);
		}
	};

	void onPenEnvEvent(int what, Object obj) {
		Log.d("inside", "penmsgenvevnt what" + what);
		switch (what) {
			case PNFDefine.PEN_DI_DATA:
				Log.d("inside_main", "pen_di_data");

				if (obj == null) {
					fileDownBtnClicked(dataImportFileNameList.get(filedownloadcount));
					return;
				}
				pendidata = true;

				downLoadPenDataList.addAll((List<PenDataClass>) obj);

				DataImportMakeFileThread makeThread = new DataImportMakeFileThread(
						MainDefine.penController,
						MainDefine.iDisGetWidth,
						MainDefine.iDisGetHeight,
						downLoadPenDataList,
						false);
				makeThread.setOnDataListener(new DataImportMakeFileThread.OnDataListener() {
					@RequiresApi(api = Build.VERSION_CODES.KITKAT)
					@Override
					public void onResultFigureList(Point _drawCanvasSize, List<DataImportFigure> _figureList) {
						mDownLoadPageCount++;

						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(_drawCanvasSize.x, _drawCanvasSize.y);
						params.gravity = Gravity.CENTER;
						preView.setLayoutParams(params);
						preView.setAccMode(false);
						preView.setDrawingSize(_drawCanvasSize.x, _drawCanvasSize.y, false);
						preView.drawFigure(_figureList, false);
						//preView.drawimage(false);
						String saveDir = getExternalStoragePathDir();

						try {
							pagecount++;
							///saveToInternalStorage(preView.mBitmap,downLoadFileName);

							/*File saveFile = new File(saveDir, "" + downLoadFileName + ".jpg");
							FileOutputStream fOut = new FileOutputStream(saveFile, false);

							preView.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

							fOut.flush();
							fOut.close();
							addDebugText("save image path : " + saveFile);*/
							savetimeindb(preView.mBitmap);
							preView.invalidate();
							isFileDownLoading = true;
							if (filedownloadcount == MainActivity.diFileCount) {
								workisdone = true;
							}
						} catch (Exception e) {
							Log.e("error", "previewSaveBtnClicked Exception : " + e);
						}
					}
					@Override
					public void onResultFail() {
						isFileDownLoading = false;

					}
				});
				break;
			case PNFDefine.PEN_DI_ACC_DATA:
				Log.d("inside", "pen_di_acc_data");
				downLoadAccPenDataList.addAll((List<PenDataClass>) obj);

				DataImportMakeFileThread makeThread_acc = new DataImportMakeFileThread(
						MainDefine.penController,
						MainDefine.iDisGetWidth,
						MainDefine.iDisGetHeight,
						downLoadAccPenDataList,
						true);
				makeThread_acc.setOnDataListener(new DataImportMakeFileThread.OnDataListener() {
					@RequiresApi(api = Build.VERSION_CODES.KITKAT)
					@Override
					public void onResultFigureList(Point _drawCanvasSize, List<DataImportFigure> _figureList) {
						mDownLoadPageCount++;

						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(_drawCanvasSize.x, _drawCanvasSize.y);
						params.gravity = Gravity.CENTER;
						pendidata = true;
						preView.setLayoutParams(params);

						preView.setAccMode(true);
						preView.setDrawingSize(_drawCanvasSize.x, _drawCanvasSize.y, true);
						preView.drawFigure(_figureList, true);
						//preView.drawimage(true);
						String saveDir = getExternalStoragePathDir();

						try {
							pagecount++;
							//saveToInternalStorage(preView.accBitmap,downLoadFileName);
							/*File saveFile = new File(saveDir, "" + downLoadFileName + ".jpg");
							FileOutputStream fOut = new FileOutputStream(saveFile, false);

							preView.accBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
							fOut.flush();
							fOut.close();
							addDebugText("save acc image path : " + saveFile);*/
							savetimeindb(preView.accBitmap);
							preView.invalidate();
							isFileDownLoading = true;
							if (filedownloadcount == MainActivity.diFileCount) {
								workisdone = true;
							}
						} catch (Exception e) {
							Log.e("error", "acc Exception : " + e);
							preView.invalidate();
						}
					}

					@Override
					public void onResultFail() {
						isFileDownLoading = false;
					}
				});
				break;
			case PNFDefine.PNF_MSG_ENV_DATA:
				PenDataClass penData = (PenDataClass) obj;

				curPenAliveSec = penData.Pen_Alive;
			 station_battery = (int) penData.Pen_Station_Battery;
			addDebugText("new message stationi battery"+station_battery);
			 battery = (int) penData.Pen_Battery;
				addDebugText("new message battery"+battery);

				if (isCheckSleepView) {
					if (penAliveTimer == null) {
						penAliveTimer = new Timer();
						TimerTask penAliveTask = new TimerTask() {
							@Override
							public void run() {
								onTimerForPenAlive();
							}
						};
						penAliveTimer.schedule(penAliveTask, 1000, 1000);

						savePenSleepRemainingTime = (int) MainDefine.GetCurrentSec() + penSleepDelay;
						savePenAliveSec = penSleepDelay;
						curPenAliveSec = penSleepDelay;
					}
				}
				if (isFileDownLoading) {
					if (pendidata) {
						filedownloadcount++;
					}
					isFileDownLoading = false;
					pendidata = false;
					//filedownloadcount++;
					if (filedownloadcount < MainActivity.diFileCount) {

						yourAsyncTask.onProgressUpdate(Math.min(filedownloadcount,diFileCount));
						//MainDefine.penController.DeleteFileTobyte(downLoadFileName);
						fileDownBtnClicked(dataImportFileNameList.get(filedownloadcount));
					}
					else {

						yourAsyncTask.onPostExecute(null);
					}
					lock = true;
				}
				else{
					if(filedownloadcount<MainActivity.diFileCount){
						yourAsyncTask.onProgressUpdate(Math.min(filedownloadcount,diFileCount));
						fileDownBtnClicked(dataImportFileNameList.get(filedownloadcount));
					}
				}
				break;
			case PNFDefine.PEN_DI_TEMPLETE:
//			penDataArray = (ArrayList<PenDataClass>)obj;
				break;

			case PNFDefine.PEN_DI_DELETE:
				addDebugText("PEN_DI_DELETE");
//			penDataArray = (ArrayList<PenDataClass>)obj;
				break;

			default:
				break;
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadpage(int pngname, String title) throws FileNotFoundException {
		//webView.setVisibility(View.GONE);
		//previousPageNumber = currentPagenumber;
		String maketitle = title.substring(6);
		int actual_page = Integer.valueOf(maketitle)-10000;
		maketitle = title.substring(0,6)+String.valueOf(actual_page);
		if (actionBar != null) {
			actionBar.setTitle(maketitle);
		}
		//findViewById(R.id.fullpageview_frame).setVisibility(View.VISIBLE);
		findViewById(R.id.main_frame).setVisibility(View.GONE);
		if (pageListFragment.arrayList.contains(pngname)) {
			Log.d("Bottom", "change page");
			Cursor c = DotDetailsDatabaseHelper.getdatabasehelper().returnPageName(pngname);
			c.moveToFirst();
			String image = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + String.valueOf(c.getString(0)) + ".png";
			addDebugText("pngname"+c.getString(0));
			//File f  =new File(new ContextWrapper(getApplicationContext()).getDir("imageDir", Context.MODE_PRIVATE),c.getString(0) + ".png");
			File f = new File(getApplicationContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir",c.getString(0)+".png");
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable=true;
			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),null,options);
			drawView.setVisibility(View.GONE);
			ZoomImageView myImageView = (ZoomImageView) findViewById(R.id.imageView);
			myImageView.setImageBitmap(bitmap);


			myImageView.setVisibility(View.VISIBLE);
			/*String saveDir = "";
			File saveFile =  new File(saveDir, ""+downLoadFileName+".png");
			FileOutputStream fOut = new FileOutputStream(saveFile, false);*/
			//new LoadStrokesPage().doInBackground(pageNum);
		} else {
			refreshList();
		}

	}

	private void savetimeindb(Bitmap bitmapImage) throws ParseException {
		String year = downLoadFileName.substring(0, 4);
		String month = downLoadFileName.substring(4, 6);
		String day = downLoadFileName.substring(6, 8);
		String hour = downLoadFileName.substring(8, 10);
		String minutes = downLoadFileName.substring(10, 12);
		String seconds = downLoadFileName.substring(12, 14);
		String str = "" + getmonth(month) + " " + day + " " + year + " " + hour + ":" + minutes + ":" + seconds + ".454 IST";
		SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
		//Date date_ = df.parse(str);
		//String date = day + month +year;
		//long epoch = date_.getTime();
		//Log.d("Epoch", "" + epoch);
		/*String datetime = day + " " +month + " " + year + " " + hour + " " + minutes ;*/
		//String datetime = day + month + year +hour + minutes ;
		String datetime = year + month + day + hour + minutes ;
		addDebugText("datetime"+datetime);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		SharedPreferences prefs = sharedpreferences;
		int count = prefs.getInt(PageId, 0);
		count = count + 1;
		editor.putInt(PageId, count);
		Log.d("count", "" + count);
		DotDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count), datetime, "page",count);
		addDebugText("saving "+downLoadFileName);
		FirebaseDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count),datetime,"page",count);
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		// Create imageDir
		//File mypath=new File(directory,count+".png");
		File mypath = new File(getApplicationContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir"+File.separator+count+".png");

		//mypath  = new File(Environment.getExternalStorageDirectory()+"/"+count+".png");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			MainDefine.penController.DeleteFileTobyte(downLoadFileName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		editor.commit();
	}
	public void savetimeindb(Bitmap bitmapImage,String online) throws ParseException {

		Long epoch = System.currentTimeMillis()/1000;
		Date currentTime = Calendar.getInstance().getTime();
		String date = currentTime.toString();
		String month  = date.substring(4,7);
		String date_ =date.substring(8,10);
		String hour =  date.substring(11,13);
		String minutes  = date.substring(14,16);
		String year = date.substring(30,34);

		String datetime = year + getmonthinno(month) + date_ + hour + minutes ;
		addDebugText("date online"+date);
		addDebugText("datetime"+datetime);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		SharedPreferences prefs = sharedpreferences;
		int count = prefs.getInt(PageId, 0);
		count = count + 1;
		editor.putInt(PageId, count);

		Log.d("count", "" + count);
		String downLoadFileName = online;
		addDebugText("saving "+downLoadFileName);
		DotDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count), datetime, "page",count);
		FirebaseDetailsDatabaseHelper.getdatabasehelper().insertData(String.valueOf(count),datetime,"page", count);

		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		// Create imageDir
		//File mypath=new File(directory,count+".png");
		File mypath = new File(getApplicationContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir"+File.separator+count+".png");

		//mypath  = new File(Environment.getExternalStorageDirectory()+"/"+count+".png");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to the OutputStream

			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100
					, fos);
			DataToSVG dataToSVG = new DataToSVG(getApplicationContext());
			dataToSVG.initFirebase();
			dataToSVG.saveToFirebase(new Pages(count));
			DeviceDataHelper.getdatabasehelper().deleteAll();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		editor.commit();
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


	public String getmonth(String month) {
		String newmonth = "";
		switch (month) {
			case "01":
				newmonth = "Jan";
				break;
			case "02":
				newmonth = "Feb";
				break;
			case "03":
				newmonth = "Mar";
				break;
			case "04":
				newmonth = "Apr";
				break;
			case "05":
				newmonth = "May";
				break;
			case "06":
				newmonth = "Jun";
				break;
			case "07":
				newmonth = "Jul";
				break;
			case "08":
				newmonth = "Aug";
				break;
			case "09":
				newmonth = "Sep";
				break;
			case "10":
				newmonth = "Oct";
				break;
			case "11":
				newmonth = "Nov";
				break;
			case "12":
				newmonth = "Dec";
				break;


		}
		return newmonth;
	}

	void onTimerForPenAlive() {
		int curTime = (int) MainDefine.GetCurrentSec();

		boolean check = false;
		if (MainDefine.penController.getModelCode() == 2) {
			if (MainDefine.penController.getMCU1() >= 2 && MainDefine.penController.getMCU2() >= 2 && MainDefine.penController.getHWVersion() >= 2) {
				check = true;
			}
		} else if (MainDefine.penController.getModelCode() == 3) {
			if (MainDefine.penController.getMCU1() >= 1 && MainDefine.penController.getMCU2() >= 1 && MainDefine.penController.getHWVersion() >= 1) {
				check = true;
			}
		}

		if (check) {
			if (curPenAliveSec <= 0) {
				penPopupHandler.sendEmptyMessage(SLEEPVIEW_SHOWPOPUP);
				return;
			} else {
				penCheckAliveCnt = 0;
			}


			if (curPenAliveSec != 0) {
				if (savePenAliveSec != curPenAliveSec) {
					savePenAliveSec = curPenAliveSec;
					savePenSleepRemainingTime = (int) curTime + curPenAliveSec;
				}
			}
		}

		if (savePenSleepRemainingTime - curTime < 0) {
			penPopupHandler.sendEmptyMessage(SLEEPVIEW_SHOWPOPUP);
		} else {
			penCheckAliveCnt = 0;
		}
	}

	void stopPenAliveTimer() {
		isCheckSleepView = false;

		if (penAliveTimer != null) {
			penAliveTimer.cancel();
			penAliveTimer.purge();
			penAliveTimer = null;
		}
	}

	@SuppressLint("HandlerLeak")
	Handler penPopupHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case SLEEPVIEW_SHOWPOPUP://슬핍 팝업창 호출
					if(++penCheckAliveCnt > 10){
						if(penSleepView.getVisibility() == View.GONE){
							penSleepView.setVisibility(View.VISIBLE);
						}

						stopPenAliveTimer();
					}
					break;
				case SLEEPVIEW_CLOSEPOPUP://X버튼으로 강제 닫기
					if(penSleepView.getVisibility() == View.VISIBLE){
						penSleepView.setVisibility(View.GONE);
					}

					stopPenAliveTimer();
					break;
				case SLEEPVIEW_STARTPOPUP://펜 입력으로 닫기
					if(penSleepView.getVisibility() == View.VISIBLE){
						penSleepView.setVisibility(View.GONE);
					}

					isCheckSleepView = true;
					if(curPenAliveSec != penSleepDelay){
						savePenSleepRemainingTime = (int) MainDefine.GetCurrentSec() + penSleepDelay;
						savePenAliveSec = penSleepDelay;
						curPenAliveSec = penSleepDelay;
					}
					break;
			}
		}
	};

	@SuppressLint("HandlerLeak")
	Handler messageHandler = new Handler() {
		@RequiresApi(api = Build.VERSION_CODES.KITKAT)
		@Override
		public void handleMessage(Message msg) {
			onMessageEvent(msg.what, msg.obj);
		}
	};

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	void onMessageEvent(int what, Object obj) {
		tabFragment.isPenConnected();
		if(what==PNFDefine.PNF_MSG_CONNECTING){
			addDebugText("PNF_MSG_Connecting");
			Log.d("from","Main Activity");
			tabFragment.animatePen();
		}
		if (what==PNFDefine.PNF_MSG_CONNECTING_FAIL){
			addDebugText("PNF_MSG_CONNECTING_FAIL");
			tabFragment.isPenConnected();
		}
		if (what == PNFDefine.PNF_MSG_FAIL_LISTENING) {
//			showAlertView(ALERTVIEW_FAIL_LISTENING);
			addDebugText("PNF_MSG_FAIL_LISTENING");
			PenClientCtrl.isconnected = false;
			tabFragment.isPenConnected();
			savedevicedata();
			//tabFragment.pendisconnecticon();
			return;
		} else if (what == PNFDefine.PNF_MSG_CONNECTED) {
			addDebugText("PNF_MSG_CONNECTED");
			PenClientCtrl.isconnected = true;
			TabFragment.animating = false;
			tabFragment.isPenConnected();
			//tabFragment.penconnectedicon();
			diFileCount = 0;
			penErrorCnt = 0;
		}
		if (what == PNFDefine.PNF_MSG_INVALID_PROTOCOL) {
			return;
		} else if (what == PNFDefine.PNF_MSG_SESSION_CLOSED) {
			addDebugText("PNF_MSG_SESSION_CLOSED");
		} else if (what == PNFDefine.PNF_MSG_PEN_RMD_ERROR) {
			penErrorCnt++;
			if (penErrorCnt > 5) {
				Toast.makeText(
						getApplicationContext(),
						MainDefine.getLangString(getApplicationContext(), R.string.PEN_RMD_ERROR_MSG),
						Toast.LENGTH_SHORT)
						.show();
				penErrorCnt = 0;
			}
			return;
		} else if (what == PNFDefine.PNF_MSG_DI_TEMP_FILE_COMPLETE) {
			addDebugText("PNF_MSG_DI_TEMP_FILE_COMPLETE");
			MainDefine.penController.setDIState(1);
		} else if (what == PNFDefine.PNF_MSG_FIRST_DATA_RECV) {
			addDebugText("PNF_MSG_FIRST_DATA_RECV");

			if (MainDefine.penController.getModelCode() == 4) {
				RectF rectFT_8X5 = new RectF(
						1804, 411,
						5363, 5125);

				PointF[] calScreenPoint = new PointF[4];
				PointF[] calResultPoint = new PointF[4];
				calResultPoint[0] = new PointF(rectFT_8X5.left, rectFT_8X5.top);
				calResultPoint[1] = new PointF(rectFT_8X5.right, rectFT_8X5.top);
				calResultPoint[2] = new PointF(rectFT_8X5.right, rectFT_8X5.bottom);
				calResultPoint[3] = new PointF(rectFT_8X5.left, rectFT_8X5.bottom);

				calScreenPoint[0] = new PointF(0.0f, 0.0f);
				calScreenPoint[1] = new PointF(MainDefine.iDisGetWidth, 0.0f);
				calScreenPoint[2] = new PointF(MainDefine.iDisGetWidth, MainDefine.iDisGetHeight);
				calScreenPoint[3] = new PointF(0.0f, MainDefine.iDisGetHeight);

				MainDefine.penController.setCalibrationData(calScreenPoint, 0, calResultPoint);

			}
		} else if (what == PNFDefine.PNF_MSG_DI_START) {
			addDebugText("PNF_MSG_DI_START");
		} else if (what == PNFDefine.PNF_MSG_DI_STOP) {
			addDebugText("PNF_MSG_DI_STOP");
		} else if (what == PNFDefine.PNF_MSG_DI_OK) {
			addDebugText("PNF_MSG_DI_OK");
		} else if (what == PNFDefine.PNF_MSG_DI_FAIL) {
			addDebugText("PNF_MSG_DI_FAIL");
		} else if (what == PNFDefine.PNF_MSG_DI_FILE_LIST_COMPLETE) {
			addDebugText("PNF_MSG_DI_FILE_LIST_COMPLETE");

			diFileCount = 0;
			List<String> folderNameList = MainDefine.penController.getDIFolderName();
			if (folderNameList != null && folderNameList.size() > 0) {
				for (String folderName : folderNameList) {
					List<String> fileNameList = MainDefine.penController.getDIFileName(folderName);

					diFileCount += fileNameList.size();
				}
			}

			if (diFileCount > 0) {
				addDebugText("Station has " + String.valueOf(diFileCount) + " file(s).");


				sendNotification(preView);

			}
		} else if (what == PNFDefine.GESTURE_CIRCLE_CLOCKWISE) {
			addDebugText("GESTURE_CIRCLE_CLOCKWISE");
			return;
		} else if (what == PNFDefine.GESTURE_CIRCLE_COUNTERCLOCKWISE) {
			addDebugText("GESTURE_CIRCLE_COUNTERCLOCKWISE");
			return;
		} else if (what == PNFDefine.GESTURE_CLICK) {
			addDebugText("GESTURE_CLICK");
			return;
		} else if (what == PNFDefine.GESTURE_DOUBLECLICK) {
			addDebugText("GESTURE_DOUBLECLICK");
			return;
		}
		if(what== PNFDefine.PNF_MSG_NEW_PAGE_BTN) {
			writing = false ;
			findViewById(R.id.main_frame).setVisibility(View.GONE);

			//drawView.setVisibility(View.VISIBLE);
			//drawViewBGImgView.setVisibility(View.VISIBLE);
			drawViewBGImgView.setVisibility(View.GONE);

			//drawView.pathEraser();
		/*	Point changeSize = MainDefine.changeResolution();
			drawView.changeDrawingSize(changeSize.x ,changeSize.y);*/

			/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				drawView.setBackground(getResources().getDrawable(R.drawable.prescription,null));
			}
			else
				drawView.setBackground(getResources().getDrawable(R.drawable.prescription));*/

			String saveDir = getExternalStoragePathDir();
			try {
				Long epoch = System.currentTimeMillis()/1000;
				Date currentTime = Calendar.getInstance().getTime();
				String date = currentTime.toString();

				/*File saveFile = new File(saveDir, epoch.toString()+".jpg");
				FileOutputStream fOut = new FileOutputStream(saveFile, false);
				drawView.mBitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);



				fOut.flush();
				fOut.close();
				addDebugText("save image path : " + saveFile);*/
				//saveToInternalStorage(drawView.mBitmap,epoch.toString());

				savetimeindb(drawView.mBitmap,epoch.toString());
				drawView.btnEraser();


			} catch (Exception e){

			}

			addDebugText("PEN_PAGE_NEW_BTN");
			return;
		}
		if (what==PNFDefine.PEN_PAGE_NEW) {
				addDebugText("PEN_PAGE_NEW");
				return;
		}
		if (what==PNFDefine.PEN_PAGE_ADD) {
			addDebugText("PEN_PAGE_ADD");
			return;
		}
		if (what==PNFDefine.PEN_PAGE_INSERT) {
			addDebugText("PEN_PAGE_INSERT");
			return;
		}

		packetCnt++;

	}

	public void initData() {


		if (downLoadPenDataList == null) {
			downLoadPenDataList = new ArrayList<PenDataClass>();
		}

		if (downLoadAccPenDataList == null) {
			downLoadAccPenDataList = new ArrayList<PenDataClass>();
		}

		dataImportFileNameList = new ArrayList<String>();

		List<String> folderNameList = MainDefine.penController.getDIFolderName();
		if (folderNameList != null && folderNameList.size() > 0) {
			for (String folderName : folderNameList) {

				List<String> fileNameList = MainDefine.penController.getDIFileName(folderName);

				for (String fileName : fileNameList) {

					dataImportFileNameList.add(folderName + fileName);
					lock = false;
					Log.d("inside", "filenamelist loop ");
					Log.d("Name", "filename " + fileName + " folderName " + folderName);
					//fileDownBtnClicked(folderName + fileName);
				}
			}
			fileDownBtnClicked(dataImportFileNameList.get(0));
		}
	}

	public void fileDownBtnClicked(String fileName) {
		Log.d("inside", "filedownbtnclicked");
		/*downLoadFileName = diListViewAdapter.getFileName(tagIdx);*/
		downLoadFileName = fileName;

		MainDefine.penController.OpenFileTobyte(downLoadFileName);
		isFileDownLoading = true;

		if (downLoadPenDataList != null) {
			downLoadPenDataList.clear();
		}

		if (downLoadAccPenDataList != null) {
			downLoadAccPenDataList.clear();
		}

		mDownLoadPageCount = 0;

		//previewSaveBtnClicked(downLoadFileName);


	}

	public void previewSaveBtnClicked(String v) {
		String saveDir = getExternalStoragePathDir();

		try {
			//saveToInternalStorage(preView.mBitmap,"thunmnail.png");
			/*File saveFile = new File(saveDir, "thunmnail.png");
			FileOutputStream fOut = new FileOutputStream(saveFile, false);
			preView.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); //bm is the bitmap object
			fOut.flush();
			fOut.close();

			Toast.makeText(
					getApplicationContext(),
					"save image path : " + saveFile,
					Toast.LENGTH_LONG)
					.show();*/

		} catch (Exception e) {
			Log.e("error", "previewSaveBtnClicked Exception : " + e);
		}
	}

	String getExternalStoragePathDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}


	/*private String saveToInternalStorage(Bitmap bitmapImage,String downLoadFileName){
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		// Create imageDir
		File mypath=new File(directory,downLoadFileName+".jpg");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			MainDefine.penController.DeleteFileTobyte(downLoadFileName);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return directory.getAbsolutePath();
	}*/

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		if(findViewById(R.id.menu_blue_patient)!=null) {
			findViewById(R.id.menu_blue_patient).setVisibility(View.GONE);
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if(getSupportFragmentManager().getBackStackEntryCount()>2){
			//transaction.replace(R.id.layout_fullscreen, tabFragment);
			//transaction.replace(R.id.layout_fullscreen,new TabFragment());
			for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount()-1; ++i) {
				getSupportFragmentManager().popBackStack();
			}
            try {
				findViewById(R.id.layout_fullscreen).setVisibility(View.GONE);
				findViewById(R.id.imgDisplay).setVisibility(View.GONE);
				//new FullScreenImageAdapter().clearimagepaths();
			}catch (NullPointerException e){
            	e.printStackTrace();
			}
        }
		findViewById(R.id.main_frame).setVisibility(View.VISIBLE);
		try{
			findViewById(R.id.fragment_patient).setVisibility(View.GONE);

			//findViewById(R.id.layout_fullscreen).setVisibility(View.GONE);
		}catch (Exception e){
			e.printStackTrace();
		}
//		findViewById(R.id.drawView).setVisibility(View.GONE);
		//zoomOut();
		switch (item.getItemId()) {
			case R.id.navigation_home:
			    try {
			      /*  if(getSupportFragmentManager().findFragmentByTag("Tabfragment")!=null){
			            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("Tabfragment")).commit();
			            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("Tabfragment")).commit();
						transaction.add(tabFragment,"Tabfragment");
                    }
*/
                    transaction.replace(R.id.main_frame, tabFragment,"Tabfragment");
                    transaction.commit();
                    tabFragment.isPenConnected();
                    if (actionBar != null) {
                        actionBar.setTitle(R.string.title_home);
                        actionBar.setSubtitle(null);
                    }
                    //sethasrss();
                    invalidateOptionsMenu();

                    Log.d("Home button", "navigation home");
                    refreshList();
                    back = 0;
                }catch (Exception e){
			        e.printStackTrace();
                }
				return true;
			case R.id.analytics:
				back++;
				if (actionBar != null) actionBar.setTitle(R.string.analytics);
				transaction.replace(R.id.main_frame, new AnalyticsFragment());
				if(getSupportFragmentManager().findFragmentByTag("Tabfragment")==null)
					transaction.addToBackStack("TabFragment");
				transaction.commit();
				return true;
			case R.id.settings:
				back++;
				if (actionBar != null) actionBar.setTitle(R.string.setting);
				transaction.replace(R.id.main_frame, new SettingsFragment());
				if(getSupportFragmentManager().findFragmentByTag("Tabfragment")==null)
					transaction.addToBackStack("TabFragment");
				transaction.commit();
				return true;
			case R.id.calendar:
				back++;
				if (actionBar != null) actionBar.setTitle(R.string.calendar);
				transaction.replace(R.id.main_frame, new AnalyticsFragment());
				if(getSupportFragmentManager().findFragmentByTag("Tabfragment")==null)
					transaction.addToBackStack("TabFragment");
				transaction.commit();
				return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {

			case R.id.action_pen:
				doPenThingy();
				return true;
        /*    case R.id.action_sync:
                new DataToSVG().execute(getApplication());
                new SaveHTMLs(ParentActivity.this).execute();
                return true;*/
			default:
				return super.onOptionsItemSelected(item);
		}
	}



	private void doPenThingy() {
		if (BluetoothAdapter.getDefaultAdapter().getState() == BluetoothAdapter.STATE_TURNING_ON
				|| BluetoothAdapter.getDefaultAdapter().getState() == BluetoothAdapter.STATE_ON) {
			if (penClientCtrl.isConnected())
				openPenDialog();
           /* else if (Doctor.penId.isEmpty()) {
                startActivityForResult(new Intent(getApplicationContext(), DeviceListActivity.class), 4);
            }*/
			else {
				tabFragment.animatePen();
				MainDefine.penController = new PNFPenController(this);
				MainDefine.penController.setDefaultModelCode(PNFDefine.Equil);
				MainDefine.penController.setConnectDelay(false);
				MainDefine.penController.setProjectiveLevel(4);
				MainDefine.penController.fixStationPosition(PNFDefine.DIRECTION_TOP);
				MainDefine.penController.setCalibration(this);

				Log.d("inside", "dopenthing");
				MainDefine.penController.startPen();
				Log.d("equil","start pen main");
			}
			//penClientCtrl.connect(Doctor.penId);
		} else {
			startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),REQUEST_ENABLE_BT);
			
		}
	}

	private void openPenDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_pen_status);
		dialog.setCancelable(true);
		dialog.show();
		TextView battery = (TextView) dialog.findViewById(R.id.battery_pen);
		TextView battery_device = (TextView) dialog.findViewById(R.id.battery_device);

		Button disconnect = (Button) dialog.findViewById(R.id.disconnect_pen);
		Button ok = (Button) dialog.findViewById(R.id.ok);
		ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.battery_progress);
		ProgressBar progressBar_device = (ProgressBar) dialog.findViewById(R.id.battery_progress_device);
		disconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stopPenBtnClicked(view);
			}
		});

		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		if (penClientCtrl.isConnected()) {
			progressBar.setProgress(Math.round(ParentActivity.battery));
			progressBar_device.setProgress(Math.round(ParentActivity.station_battery));
			battery.setText(ParentActivity.battery + "% remaining");
			battery_device.setText(ParentActivity.station_battery + "% remaining");
			dialog.findViewById(R.id.battery_layout_pen).setVisibility(View.VISIBLE);
		} else {
			BaseActivity.battery = 0;
			battery.setText("N/A");
			battery_device.setText("N/A");
			dialog.findViewById(R.id.pen_not_found).setVisibility(View.VISIBLE);

			dialog.findViewById(R.id.battery_layout_pen).setVisibility(View.GONE);
		}
	}


	class YourAsyncTask extends AsyncTask<Void, Integer, Void> {
		private ProgressDialog dialog;

		public YourAsyncTask(MainActivity activity) {
			dialog = new ProgressDialog(activity);
		}

		@Override
		protected void onPreExecute() {
			mBuilder.setProgress(diFileCount, 0, false);
			mNotificationManager.notify(id, mBuilder.build());
			//dialog.show();
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			// Update progress
			mBuilder.setProgress(diFileCount, values[0], false)
					.setContentText(filedownloadcount+"/"+diFileCount);

			mNotificationManager.notify(id, mBuilder.build());
			super.onProgressUpdate(values);
		}

		protected Void doInBackground(Void... args) {
			// do background work here
			initData();
			return null;
		}

		protected void onPostExecute(Void result) {
			// do UI work here
			if (diFileCount==filedownloadcount) {
				mBuilder.setContentText("Download complete")
						.setOngoing(false);
				// Removes the progress bar
				mBuilder.setProgress(0, 0, false);
				mNotificationManager.notify(id, mBuilder.build());
			}
		}
	}
	class LiveSave extends AsyncTask<Void, Void, Void> {
		//private ProgressDialog dialog;
		Bitmap bitmap;
		public LiveSave(Bitmap bitmap) {
			//dialog = new ProgressDialog(activity);
			this.bitmap = bitmap;
		}


		protected Void doInBackground(Void... args) {
			// do background work here
			Long epoch = System.currentTimeMillis()/1000;
			try {
				savetimeindb(bitmap,epoch.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}


	}
	public void sendNotification(View view) {

		//Get an instance of NotificationManager//

		mBuilder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_sync)
						.setContentTitle("Downloading Prescriptions")
						.setContentText("0/"+diFileCount)
						.setDefaults(NotificationManager.IMPORTANCE_DEFAULT)
						.setPriority(Notification.PRIORITY_MAX)
						.setOnlyAlertOnce(true)
						.setOngoing(true)
						.setCategory(Notification.CATEGORY_PROGRESS);


		// Gets an instance of the NotificationManager service//

		mNotificationManager =

				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// When you issue multiple notifications about the same type of event,
		// it’s best practice for your app to try to update an existing notification
		// with this new information, rather than immediately creating a new notification.
		// If you want to update this notification at a later date, you need to assign it an ID.
		// You can then use this ID whenever you issue a subsequent notification.
		// If the previous notification is still visible, the system will update this existing notification,
		// rather than create a new one. In this example, the notification’s ID is 001//


		mNotificationManager.notify(001, mBuilder.build());
		yourAsyncTask = new YourAsyncTask(this);
		yourAsyncTask.execute();
	}

	public void startJob() {
		Job job = jobDispatcher.newJobBuilder()
				.setService(SyncService.class)
				.setLifetime(Lifetime.FOREVER)
				.setTag("clinmd-service")
				.setConstraints(Constraint.ON_ANY_NETWORK)
				.setRecurring(true)
				.setTrigger(Trigger.executionWindow(0,900))
				.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
				.setReplaceCurrent(false)
				.build();
		jobDispatcher.mustSchedule(job);
		Log.d("Service ", "Started");
	}

	public void stopJob() {
		Log.d("Service :- ", "Stopped");
		//DataToSVG.stopThread();
		//t.interrupt();
		new FirebaseJobDispatcher(new GooglePlayDriver(this)).cancel("clinmd-service");
		Toast.makeText(this, "job cancelled", Toast.LENGTH_SHORT).show();
	}

	public static class SyncService extends JobService {

		@Override
		public boolean onStartJob(JobParameters job) {
			Log.d("Thread ", " started");
			new DotDetailsDatabaseHelper(getApplicationContext()).open();
			new PatientDetailsDatabaseHelper(getApplicationContext()).open();
			new FirebaseDetailsDatabaseHelper(getApplicationContext()).open();
			new DeviceDataHelper(getApplicationContext()).open();
			Runnable r = new Bar(getApplicationContext(),getBaseContext(),job);
			t = new Thread(r);
			t.start();

			jobFinished(job,true);
			return true;
		}

		@Override
		public boolean onStopJob(JobParameters job) {
			Log.d("ServiceEnded", "SyncService");
			return false;
		}
	}

	private static class Bar implements Runnable {
		private int i = 0;
		int c = 1;
		private Context context;
		private Context basecontext;
		JobParameters  job ;


		private FirebaseAuth.AuthStateListener mAuthListener;
		FirebaseAuth mAuth;

		Bar() {
		}

		Bar(Context applicationContext, Context basecontext,JobParameters job) {
			this.context = applicationContext;
			this.basecontext=basecontext;
			this.job=job;
		}
		Bar(Context applicationContext, Context basecontext) {
			this.context = applicationContext;
			this.basecontext=basecontext;
		}
		@Override
		public void run() {
			do{
				mAuth  =FirebaseAuth.getInstance();
				mAuthListener = new FirebaseAuth.AuthStateListener() {
					@Override
					public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
						FirebaseUser user = firebaseAuth.getCurrentUser();
						if (user != null) {
							// User is signed in
							Log.d("SyncService", "onAuthStateChanged:signed_in:" + user.getUid());
						} else {
							// User is signed out
							Log.d("SyncService", "onAuthStateChanged:signed_out");
						}
					}
				};
				mAuth.addAuthStateListener(mAuthListener);
				Log.d("Context_Bottom",""+this);
				getDetailsFromFirebase();
				getdetailsfromfirebase_documents();

				Doctor.loadFromSharedPreferences(context);
				DataToSVG dataToSVG = new DataToSVG();
				dataToSVG.doInBackground(context);

			}while (false);

		}
		private void getDetailsFromFirebase(){
			final FirebaseDatabase database   = FirebaseDatabase.getInstance();
			DatabaseReference myRef = database.getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child("Patient Id");
			myRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener(){
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					for(DataSnapshot dataSnapshots:dataSnapshot.getChildren()){
						String test =  dataSnapshots.getKey().toString();
						FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								String patientid  = dataSnapshot.getKey();
								Log.d("patientid",""+dataSnapshot.getValue().toString());
								Patient value= dataSnapshot.getValue(Patient.class);
								age  = value.getAge();
								patient_id=value.getID();
								firstname= value.getfirstName();
								followup = value.getfollowUp();
								lastname = value.getlastName();
								mobile = value.getmobile();
								gender = value.getGender();
								ugcid= value.getUgcID();
							}
							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});
						FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Pages").addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								for(DataSnapshot dataSnapshots : dataSnapshot.getChildren()){
									String test = dataSnapshots.getKey().toString();
									Page value  = dataSnapshots.getValue(Page.class);
									diagnose = value.getDiagnose();
									page = value.getPage();
									timestamp = value.getTimestamp();
									Log.d("value",""+dataSnapshots.getKey());
									try{
										PatientDetailsDatabaseHelper.getdatabasehelper().insertData(firstname,lastname,page,mobile,age,timestamp,gender,followup,diagnose,patient_id,ugcid,"page");
									}catch(SQLException e ){
										e.printStackTrace();
									}
								}
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});
						Log.d("id",test);
					}
				}
				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}
		public void getdetailsfromfirebase_documents(){
			final FirebaseDatabase database   = FirebaseDatabase.getInstance();
			DatabaseReference myRef = database.getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child("Patient Id");
			myRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener(){
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					for(DataSnapshot dataSnapshots:dataSnapshot.getChildren()){
						String test =  dataSnapshots.getKey().toString();
						FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								String patientid  = dataSnapshot.getKey();
								Log.d("patientid",""+dataSnapshot.getValue().toString());
								Patient value= dataSnapshot.getValue(Patient.class);
								age  = value.getAge();
								patient_id=value.getID();
								firstname= value.getfirstName();
								followup = value.getfollowUp();
								lastname = value.getlastName();
								mobile = value.getmobile();
								gender = value.getGender();
								ugcid= value.getUgcID();
							}
							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});
						FirebaseDatabase.getInstance().getReference("Doctors").child(new Doctor().getRegistrationID()).child("Patients").child(dataSnapshots.getKey().toString()).child("Documents").addListenerForSingleValueEvent(new ValueEventListener() {
							@Override
							public void onDataChange(DataSnapshot dataSnapshot) {
								for(DataSnapshot dataSnapshots : dataSnapshot.getChildren()){
									String test = dataSnapshots.getKey().toString();
									Page value  = dataSnapshots.getValue(Page.class);
									diagnose = value.getDiagnose();
									page = value.getPage();
									timestamp = value.getTimestamp();
									Log.d("value",""+dataSnapshots.getKey());
									try{
										PatientDetailsDatabaseHelper.getdatabasehelper().insertData(firstname,lastname,page,mobile,age,timestamp,gender,"NA",diagnose,patient_id,ugcid,"document");
									}catch(SQLException e ){
										e.printStackTrace();
									}
								}
							}

							@Override
							public void onCancelled(DatabaseError databaseError) {

							}
						});
						Log.d("id",test);
					}
				}
				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		}
		public void savetofirebase(String pages){
			Log.d("Inside","savetofire");
			FirebaseDatabase database = FirebaseDatabase.getInstance();
			DatabaseReference myRef = database.getReference("Pages");
			StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
			String regId = new Doctor().getRegistrationID();
			//Cursor c =PatientDetailsDatabaseHelper.getdatabasehelper().returnALlPages();

			DownloadedPages page = new DownloadedPages(pages);
			try{
				Log.d("firebase_Page",""+pages);
				if(!page.getFirstName().equalsIgnoreCase(""))
					myRef.child(new Doctor().getRegistrationID()).child(regId + pages).setValue(page);
				else Log.d("firebase_page","not uploading"+pages);
			}catch (Exception e){e.printStackTrace();}



		}

	}
}