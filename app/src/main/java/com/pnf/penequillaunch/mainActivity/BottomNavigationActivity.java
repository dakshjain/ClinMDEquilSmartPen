package com.pnf.penequillaunch.mainActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.activities.ParentActivity;
import com.pnf.bt.lib.PNFDefine;

/**
 * Created by 9 on 3/16/2018.
 */

public class BottomNavigationActivity  extends ParentActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {
    ActionBar actionBar;
    int back = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

        setContentView(R.layout.activity_bottom_navigation);
        pageListFragment = new PageListFragment();
        tabFragment = TabFragment.getTabFragment();
        if(tabFragment!=null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame, tabFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        /*    try {
                ViewConfiguration config = ViewConfiguration.get(this);
                Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

                if (menuKeyField != null) {
                    menuKeyField.setAccessible(true);
                    menuKeyField.setBoolean(config, false);
                }
            } catch (Exception ex) {
                // Ignore
            }*/
        }


        actionBar = getSupportActionBar();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        findViewById(R.id.main_frame).setVisibility(View.VISIBLE);
        //zoomOut();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                transaction.replace(R.id.main_frame, tabFragment);
                transaction.commit();
                tabFragment.isPenConnected();

                if (actionBar != null){
                    actionBar.setTitle(R.string.title_home);
                    actionBar.setSubtitle(null);
                }

                //sethasrss();
                invalidateOptionsMenu();

                Log.d("Home button","navigation home");
                //refreshList();
                back = 0;
                return true;
            case R.id.analytics:
                back++;
                if (actionBar != null) actionBar.setTitle(R.string.analytics);
                transaction.replace(R.id.main_frame, new AnalyticsFragment());
                transaction.addToBackStack("TabFragment");
                transaction.commit();
                return true;
            case R.id.settings:
                back++;
                if (actionBar != null) actionBar.setTitle(R.string.setting);
                transaction.replace(R.id.main_frame, new AnalyticsFragment());
                transaction.addToBackStack("TabFragment");
                transaction.commit();
                return true;
            case R.id.calendar:
                back++;
                if (actionBar != null) actionBar.setTitle(R.string.calendar);
                transaction.replace(R.id.main_frame, new AnalyticsFragment());
                transaction.addToBackStack("TabFragment");
                transaction.commit();
                return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
   /*     PenClientCtrl penClientCtrl = new PenClientCtrl(getApplicationContext());
        penClientCtrl.penController.SetRetObjForMsg(messageHandler);*/

    }
    @SuppressLint("HandlerLeak")
    public Handler messageHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            onMessageEvent(msg.what ,msg.obj);
        }
    };
    void onMessageEvent(int what ,Object obj)
    {
        if(what == PNFDefine.PNF_MSG_FAIL_LISTENING){
//			showAlertView(ALERTVIEW_FAIL_LISTENING);
            addDebugText("PNF_MSG_FAIL_LISTENING");
            return;
        }else if(what == PNFDefine.PNF_MSG_CONNECTED){
            //isconnected = true;
            addDebugText("PNF_MSG_CONNECTED");
        }

    }


    void addDebugText(String text) {

        Log.d("pen message",text);

    }
}
