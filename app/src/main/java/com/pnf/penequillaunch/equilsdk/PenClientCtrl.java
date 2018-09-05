package com.pnf.penequillaunch.equilsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.pnf.bt.lib.PNFDefine;
import com.pnf.bt.lib.PNFPenController;
import com.pnf.penequillaunch.test.MainDefine;

/**
 * Created by 9 on 3/17/2018.
 */

public class PenClientCtrl extends Activity {
    public static PNFPenController penController = null;
    public static boolean isconnected = false;
    private Context context;
    public static PenClientCtrl myInstance;
    private boolean leMode;
    final byte REQUEST_MAIN = 0X00;
    private String SDKVreions;

    public PenClientCtrl(Context context) {

        this.context = context;
    }

    public boolean isConnected() {
        return isconnected;
    }

    public void connect(String penId) {
        Log.d("inside","connect_pen");
        MainDefine.penController = new PNFPenController(context);
        MainDefine.penController.setDefaultModelCode(PNFDefine.Equil);
        MainDefine.penController.setConnectDelay(false);
        MainDefine.penController.setProjectiveLevel(4);
        MainDefine.penController.fixStationPosition(PNFDefine.DIRECTION_TOP);
        MainDefine.penController.setCalibration(context);
        //MainDefine.penController.startPen();
        Log.d("equil","start pen penclient");
        if(MainDefine.penController.isPenMode())
            MainDefine.penController.SetRetObjForMsg(messageHandler);


    }

    public void disconnect() {
    }
    public static synchronized PenClientCtrl getInstance(Context context) {
        if (myInstance == null) {
            myInstance = new PenClientCtrl(context);
        }

        return myInstance;
    }

    @SuppressLint("HandlerLeak")
    public Handler messageHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Log.d("Handler","message event");
            onMessageEvent(msg.what ,msg.obj);
        }
    };
    void onMessageEvent(int what ,Object obj)
    {
        if(what == PNFDefine.PNF_MSG_FAIL_LISTENING){
//			showAlertView(ALERTVIEW_FAIL_LISTENING);
            Log.d("pen message","PNF_MSG_FAIL_LISTENING");
            return;
        }else if(what == PNFDefine.PNF_MSG_CONNECTED){
            isconnected = true;
            Log.d("pen message","PNF_MSG_Connected");
        }

    }

    void addDebugText(String text) {

        Log.d("pen message",text);

    }

    public boolean setLeMode(boolean leMode) {
        this.leMode = leMode;
        return this.leMode;
    }

    public String getSDKVerions() {
        return SDKVreions;
    }
}
