package com.pnf.penequillaunch.logins;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.example.daksh.clinmdequilsmartpenlaunch.R;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if(isMyServiceRunning(SyncService.class))
            new ServiceManager(getApplicationContext(), getBaseContext()).stopDispatcherService();*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                beginAnimation();
            }
        }, 1500);

    }

//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if(BottomNavigationActivity.serviceManager!=null)
            BottomNavigationActivity.serviceManager.stopDispatcherService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(BottomNavigationActivity.serviceManager!=null)
            BottomNavigationActivity.serviceManager.stopDispatcherService();
    }*/

    private void beginAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View logo = findViewById(R.id.clinmd_logo);
            View text = findViewById(R.id.clinmd_text);
            Pair<View, String> p1 = Pair.create(logo, "clinmd_logo");
            Pair<View, String> p2 = Pair.create(text, "clinmd_text");
            try {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreen.this, p1, p2);
                startActivity(new Intent(SplashScreen.this, PinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), options.toBundle());
            } catch (Exception e) {
                e.printStackTrace();
            }
//            finishAfterTransition();
        } else {
            startActivity(new Intent(SplashScreen.this, PinActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finishAffinity();

        }
    }
}
