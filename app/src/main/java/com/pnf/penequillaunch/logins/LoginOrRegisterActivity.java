package com.pnf.penequillaunch.logins;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.activities.ContactFormActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class LoginOrRegisterActivity extends AppCompatActivity {
    FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if(isMyServiceRunning(SyncService.class)) {
            Log.d("Login","Stop");
           // new ServiceManager(this, getBaseContext()).stopDispatcherService();

        }*/
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        Log.d("LoginScreen", "getDynamicLink:onSuccess"+deepLink);

                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LoginScreen", "getDynamicLink:onFailure", e);
                    }
                });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getBoolean("loggedInOnce", false)) {
            Toast.makeText(this, "loggedInOnce", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), PenConnectActivity.class));
            finish();

        }
        setContentView(R.layout.activity_login_or_register);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        Log.d("HERE","1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,"PhoneID");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "NAME");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Log.d("HERE","2");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.login_container, new LoginFragment());
        transaction.addToBackStack(null);
        transaction.commit();
        findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.do_you_have).setVisibility(View.GONE);
            }
        });
        findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginOrRegisterActivity.this, "button clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ContactFormActivity.class));
                findViewById(R.id.shadow).setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.shadow).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finish();

        } else {
            super.onBackPressed();
        }
    }
}
