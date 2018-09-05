package com.pnf.penequillaunch.test;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.bt.lib.PNFDefine;
import com.pnf.bt.lib.PNFPenController;
import com.pnf.penequillaunch.equilsdk.Const;
import com.pnf.penequillaunch.equilsdk.PenClientCtrl;
import com.pnf.penequillaunch.mainActivity.PageListFragment;
import com.pnf.penequillaunch.mainActivity.TabFragment;
import com.pnf.penequillaunch.others.DeviceDataHelper;
import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.FirebaseDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.PatientDetailsDatabaseHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.pnf.penequillaunch.activities.ParentActivity.REQ_EXTERNAL_PERMISSION;

public class BaseActivity extends AppCompatActivity {
	final String SYSTEM_DIALOG_REASON_KEY = "reason";
	final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
	final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
	public static int battery;
	final byte REQUEST_MAIN = 0X00;
	public PenClientCtrl penClientCtrl;
	public BroadcastReceiver mBroadcastReceiver;
	public PageListFragment pageListFragment;
	public TabFragment tabFragment;
	boolean deviceConnected = false;

	private BluetoothAdapter BA ;
	public static DotDetailsDatabaseHelper dotDetailsDatabaseHelper;
	public static PatientDetailsDatabaseHelper patientDetailsDatabaseHelper;
	public static FirebaseDetailsDatabaseHelper firebasedetailsdatabasehelper;


	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(Const.Broadcast.ACTION_PEN_MESSAGE);
		filter.addAction(Const.Broadcast.ACTION_PEN_DOT);
		filter.addAction(Const.Broadcast.ACTION_OFFLINE_STROKES);
		filter.addAction(Const.Broadcast.ACTION_WRITE_PAGE_CHANGED);
		filter.addAction(Const.Broadcast.ACTION_SYMBOL_ACTION);

		String address = Doctor.penId;
		if (BluetoothAdapter.getDefaultAdapter().getState() == BluetoothAdapter.STATE_ON && address.length() > 0) {
			//penClientCtrl.connect(address);
			if (tabFragment != null) {
				Log.d("from", "baseactivity");
				tabFragment.animatePen();
			}
		}

		filter.addAction("firmware_update");

		registerReceiver(mBroadcastReceiver, filter);
        MainDefine.penController.SetRetObjForMsg(messageHandler);

	}
    @SuppressLint("HandlerLeak")
    Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onMessageEvent(msg.what, msg.obj);
        }
    };

	void onMessageEvent(int what ,  Object obj){
		if (what==PNFDefine.PNF_MSG_CONNECTING){
			Log.d("pen message","connecting");
		}
	    if(what==PNFDefine.PNF_MSG_CONNECTING_FAIL){
	        Log.d("pen message","disconnected");
            PenClientCtrl.isconnected = false;
	        tabFragment.isPenConnected();
        } if(what==PNFDefine.PNF_MSG_CONNECTING_FAIL){
	        Log.d("pen message","disconnected");
            PenClientCtrl.isconnected = false;
	        tabFragment.isPenConnected();
        }

    }

	@Override
	protected void onPause() {
		super.onPause();
		if (penClientCtrl.isConnected())
			penClientCtrl.disconnect();
		else
			PenClientCtrl.getInstance(getApplicationContext()).disconnect();
	}

	@Override
	protected void onStop() {
		Log.d("Parent activity ", " stopped");

		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(homeKeyReceiver);
		unregisterReceiver(keyGuardReceiver);
		unregisterReceiver(mReceiver);
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
	}

	@Override
	public void onBackPressed()
	{
		return;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		boolean isResultOK = resultCode == Activity.RESULT_OK?true:false;
		switch(requestCode)
		{
		case REQUEST_MAIN:
			if(isResultOK){
//				MainDefine.penController.DisconnectPen();
				
				new Thread(new Runnable() {
					public void run() {
						runOnUiThread(new Runnable(){
							@Override
							public void run() {
								
								System.exit(0);
							}
						});
					}
				}).start();
			}
			break;
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (Build.VERSION.SDK_INT >= 23) {
			final int writeExternalPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (writeExternalPermissionCheck == PackageManager.PERMISSION_DENIED) {
				chkPermissions();
			} else if (writeExternalPermissionCheck == PackageManager.PERMISSION_GRANTED) {
				// penClientCtrl.connect(Doctor.penId);
			}
		}
	}




	public void chkPermissions() {
		if (Build.VERSION.SDK_INT >= 23) {
			final int writeExternalPermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (writeExternalPermissionCheck == PackageManager.PERMISSION_DENIED) {
				ArrayList<String> permissions = new ArrayList<>();
				permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
				requestPermissions(permissions.toArray(new String[permissions.size()]), REQ_EXTERNAL_PERMISSION);
			}
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.baseview);

		penClientCtrl = new PenClientCtrl(getApplicationContext());
		penClientCtrl = PenClientCtrl.getInstance(getApplicationContext());
		
		/*
    	 * LCD 크기 셋팅
    	 */
		Point LCDSize = new Point();
		((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(LCDSize);
		MainDefine.iDisGetWidth = LCDSize.x;
		MainDefine.iDisGetHeight = LCDSize.y;
		
    	/*
    	 * 홈키 리시버 셋팅
    	 */
		IntentFilter filter = null;
		filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		registerReceiver(homeKeyReceiver, filter);
		
		/*
		 * 잠금 모드 리시버 셋팅
		 */
		filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction( Intent.ACTION_SCREEN_OFF );
		registerReceiver(keyGuardReceiver, filter);


		IntentFilter filter_bth = new IntentFilter();
		filter_bth.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter_bth.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter_bth.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter_bth.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		this.registerReceiver(mReceiver, filter_bth);
		filter_bth   =  new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		this.registerReceiver(mReceiver,filter_bth);
		connectpen();

		/*
    	 * PNFBtLib 셋팅
    	 */
		new DotDetailsDatabaseHelper(getApplicationContext()).open();
		new PatientDetailsDatabaseHelper(getApplicationContext()).open();
		new FirebaseDetailsDatabaseHelper(getApplicationContext()).open();
		new DeviceDataHelper(getApplicationContext()).open();
		Log.d("pen","connecting");
		Log.d("pencontroller",""+MainDefine.penController);

		
		BA = BluetoothAdapter.getDefaultAdapter();
    	
		Intent introIntent = new Intent(BaseActivity.this, MainActivity.class);
		startActivityForResult(introIntent, REQUEST_MAIN);
	}
	public void connectpen(){
		tabFragment = TabFragment.getTabFragment();
		tabFragment.animatePen();
		try {
			MainDefine.penController = new PNFPenController(this);
			MainDefine.penController.setDefaultModelCode(PNFDefine.Equil);
			MainDefine.penController.setConnectDelay(false);
			MainDefine.penController.setProjectiveLevel(4);
			MainDefine.penController.fixStationPosition(PNFDefine.DIRECTION_TOP);
			MainDefine.penController.setCalibration(this);

			Log.d("inside", "deviceconnected");
			MainDefine.penController.startPen();
			Log.d("equil", "start pen base");
		}catch (NullPointerException e){
			e.printStackTrace();
		}

	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            //Device found
			}
			else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
				Log.d("inside","bluetooth discovery started");

			}
			else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				Log.d("inside","bluetooth device connected");
				deviceConnected = true ;
            //Device is now connected
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				Log.d("inside","discovery finished");
				tabFragment.isPenConnected();
           //Done searching
			}
			else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            //Device is about to disconnect
			}
			else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
			}
			else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				//Device has disconnected
				final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						BluetoothAdapter.ERROR);
				switch(state){
					case BluetoothAdapter.STATE_OFF:
						Log.d("Bluetooth state","state off");
						break;
					case BluetoothAdapter.STATE_ON:
						Log.d("Bluetooth state","state on");
						break;

				}
			}
		}
	};

	private boolean isDeviceConnected() {
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
			public void onServiceConnected(int profile, BluetoothProfile proxy) {
				if (profile == BluetoothProfile.A2DP) {
					PenClientCtrl.isconnected = false;
					BluetoothA2dp btA2dp = (BluetoothA2dp) proxy;
					List<BluetoothDevice> a2dpConnectedDevices = btA2dp.getConnectedDevices();
					Log.d("size devices",""+a2dpConnectedDevices.size());
					if (a2dpConnectedDevices.size() != 0) {
						for (BluetoothDevice device : a2dpConnectedDevices) {
							Log.d("devices connected",""+device.getName());
							if (device.getName().contains(Doctor.penId)) {
								PenClientCtrl.isconnected = true;
								Toast.makeText(penClientCtrl, "device connected", Toast.LENGTH_SHORT).show();
							}
						}
					}
					if (!deviceConnected) {
						Toast.makeText(getApplicationContext(), "DEVICE NOT CONNECTED", Toast.LENGTH_SHORT).show();
					}
					mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, btA2dp);
				}
			}

			public void onServiceDisconnected(int profile) {
				// TODO
			}
		};
		mBluetoothAdapter.getProfileProxy(getApplicationContext(), mProfileListener, BluetoothProfile.A2DP);
		return deviceConnected;
	}

	private BroadcastReceiver homeKeyReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if(reason != null){
					if(reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)){//onHomePressed
//						MainDefine.penController.forceDisconnectPen();
					}else if(reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)){//onHomeLongPressed
//						MainDefine.penController.forceDisconnectPen();
					}
				}
			}
		}
	};
	
	private BroadcastReceiver keyGuardReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_SCREEN_ON)){
				
			}else if(action.equals(Intent.ACTION_SCREEN_OFF)){
//				MainDefine.penController.forceDisconnectPen();
			}
		}
	};
	
}
