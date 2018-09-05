package com.pnf.penequillaunch.others;

import android.content.Context;

/**
 * Created by 9 on 12/28/2017.
 */

public class DotDetailsDatabaseHelper extends DotDetailsDatabaseHandler {
    public static DotDetailsDatabaseHandler dotDetailsDatabaseHandler ;
    public DotDetailsDatabaseHelper(Context context){
        super(context);
        dotDetailsDatabaseHandler = new DotDetailsDatabaseHandler(context);
    }
    public void open(){


        dotDetailsDatabaseHandler.Transaction_Open();
    }
    public void close(){
        dotDetailsDatabaseHandler.Transaction_Close();
    }
    public static DotDetailsDatabaseHandler getdatabasehelper(){
        return dotDetailsDatabaseHandler;
    }
}
