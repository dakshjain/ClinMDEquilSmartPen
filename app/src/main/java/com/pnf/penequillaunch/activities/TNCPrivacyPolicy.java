package com.pnf.penequillaunch.activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TNCPrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tncprivacy_policy);
        StorageReference TNCRef = FirebaseStorage.getInstance().getReference().child("TNCRef");
        Log.d("TNCPR",getIntent().getStringExtra("tncorPP"));
        TNCRef.child(getIntent().getStringExtra("tncorPP")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                WebView webView= (WebView) findViewById(R.id.webView);
                Log.d("TNCPR Path",uri.getPath());
                Log.d("TNCPR String",uri.toString());
                webView.loadUrl(uri.toString());
            }
        });

    }
}
