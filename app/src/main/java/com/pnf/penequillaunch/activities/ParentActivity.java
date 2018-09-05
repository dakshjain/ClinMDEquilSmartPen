package com.pnf.penequillaunch.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pnf.penequillaunch.mainActivity.PageListFragment;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.equilsdk.Const.Broadcast;
import com.pnf.penequillaunch.equilsdk.PenClientCtrl;
import com.pnf.penequillaunch.mainActivity.TabFragment;
import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.FirebaseDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.PatientDetailsDatabaseHelper;

import java.util.ArrayList;

public class ParentActivity extends AppCompatActivity {
    public static final int REQ_EXTERNAL_PERMISSION = 0x1002;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 4;
    public static float battery;
    public static float station_battery;


    public PenClientCtrl penClientCtrl;
    public BroadcastReceiver mBroadcastReceiver;
    public PageListFragment pageListFragment;
    public TabFragment tabFragment;

    public static DotDetailsDatabaseHelper dotDetailsDatabaseHelper;
    public static  PatientDetailsDatabaseHelper patientDetailsDatabaseHelper;
    public static FirebaseDetailsDatabaseHelper firebasedetailsdatabasehelper;



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
            }*/ else;
                //penClientCtrl.connect(Doctor.penId);
        } else {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("Parent activity ", " stopped");
        super.onStop();
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

                penClientCtrl.disconnect();
                dialog.dismiss();
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
            ParentActivity.battery = 0;
            battery.setText("N/A");
            battery_device.setText("N/A");
            dialog.findViewById(R.id.pen_not_found).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.battery_layout_pen).setVisibility(View.GONE);
        }
    }

    public void chkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            final int writeExternalPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalPermissionCheck == PackageManager.PERMISSION_DENIED) {
                ArrayList<String> permissions = new ArrayList<>();
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQ_EXTERNAL_PERMISSION);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (penClientCtrl.isConnected())
            penClientCtrl.disconnect();
        else
            PenClientCtrl.getInstance(getApplicationContext()).disconnect();
//        unregisterReceiver(mBroadcastReceiver);

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

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Broadcast.ACTION_PEN_MESSAGE);
        filter.addAction(Broadcast.ACTION_PEN_DOT);
        filter.addAction(Broadcast.ACTION_OFFLINE_STROKES);
        filter.addAction(Broadcast.ACTION_WRITE_PAGE_CHANGED);
        filter.addAction(Broadcast.ACTION_SYMBOL_ACTION);

        String address = Doctor.penId;
        if (BluetoothAdapter.getDefaultAdapter().getState() == BluetoothAdapter.STATE_ON && address.length() > 0) {
            //penClientCtrl.connect(address);
            if (tabFragment != null) {
                Log.d("from","parentActivity");
                tabFragment.animatePen();
            }
        }

        filter.addAction("firmware_update");

        registerReceiver(mBroadcastReceiver, filter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address;
                    if ((address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS)) != null) {
                        boolean isLe = data.getBooleanExtra(DeviceListActivity.EXTRA_IS_BLUETOOTH_LE, false);
                        penClientCtrl.setLeMode(isLe);
                        //penClientCtrl.connect(address);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        preferences.edit().putString("PenAddress", address).apply();
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        penClientCtrl = new PenClientCtrl(getApplicationContext());
        penClientCtrl = PenClientCtrl.getInstance(getApplicationContext());
        Log.d("ParentActivity", "SDK Version " + penClientCtrl.getSDKVerions());
        Intent oIntent = new Intent();
        oIntent.setClass(this, NeoPenService.class);
        startService(oIntent);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver( mBTDuplicateRemoveBroadcasterReceiver );
        Intent oIntent = new Intent();
        oIntent.setClass(this, NeoPenService.class);
        stopService(oIntent);
    }

    public String getExternalStoragePath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return Environment.MEDIA_UNMOUNTED;
        }
    }

}
