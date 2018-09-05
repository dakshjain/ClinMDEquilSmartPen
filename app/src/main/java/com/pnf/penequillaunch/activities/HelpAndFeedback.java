package com.pnf.penequillaunch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.daksh.clinmdequilsmartpenlaunch.R;

public class HelpAndFeedback extends AppCompatActivity {
    View shadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_feedback);
        shadow = findViewById(R.id.shadow);
        findViewById(R.id.contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelpAndFeedback.this, ContactUsActivity.class));
                if (shadow != null) shadow.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shadow != null) shadow.setVisibility(View.GONE);
    }
}
