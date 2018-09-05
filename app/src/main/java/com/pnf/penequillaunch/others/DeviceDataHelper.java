package com.pnf.penequillaunch.others;

import android.content.Context;

public class DeviceDataHelper extends DeviceDataHandler {
    public static DeviceDataHandler deviceDataHandler ;
    public DeviceDataHelper(Context context){
        super(context);
        deviceDataHandler = new DeviceDataHandler(context);
    }
    public void open(){

        deviceDataHandler.Transaction_Open();
    }
    public void close(){
        deviceDataHandler.Transaction_Close();
    }
    public static DeviceDataHandler getdatabasehelper(){
        return deviceDataHandler;
    }
}