package com.pnf.penequillaunch.mainActivity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pnf.penequillaunch.activities.HelpAndFeedback;
import com.pnf.penequillaunch.activities.HowToPenActivity;
import com.pnf.penequillaunch.activities.ProfileActivity;
import com.pnf.penequillaunch.activities.TNCPrivacyPolicy;
import com.pnf.penequillaunch.calibration.CalibrationPointActivity;
import com.pnf.penequillaunch.logins.SetupPinActivity;
import com.pnf.penequillaunch.others.Doctor;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.example.daksh.clinmdequilsmartpenlaunch.R;


import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    View v;
    private ImageView docPhoto;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_settings, container, false);
       /* findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OrderActivity.class));
            }
        });*/
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HelpAndFeedback.class));
            }
        });
      /*  findViewById(R.id.rate_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName()));
                i.addFlags(
                        Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                );
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        findViewById(R.id.change_pin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=Q5Ot_zz1llo")));*/
                startActivity(new Intent(getContext(), SetupPinActivity.class));

            }
        });
        findViewById(R.id.connect_pen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=Q5Ot_zz1llo")));*/
                startActivity(new Intent(getContext(), HowToPenActivity.class));

            }
        });

        findViewById(R.id.tnc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TNCPrivacyPolicy.class).putExtra("tncorPP","tnc.htm"));
            }
        });
        findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TNCPrivacyPolicy.class).putExtra("tncorPP","privacy_policy.htm"));
            }
        });
        View.OnClickListener a = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProfileActivity.class));
            }
        };
        findViewById(R.id.calibrate_pen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CalibrationPointActivity.class));
            }
        });

        TextView name = (TextView) findViewById(R.id.person_name);
        TextView speciality = (TextView) findViewById(R.id.speciality);
        name.setText("Dr." + Doctor.firstName + " " + Doctor.lastName + "(" + Doctor.degree + ")");
        speciality.setText(Doctor.speciality);
        docPhoto = (ImageView) findViewById(R.id.docPhoto);
        findViewById(R.id.account).setOnClickListener(a);
        findViewById(R.id.photo).setOnClickListener(a);
        getProfileImage();
        return v;
    }

    private void getProfileImage() {
        File localfile = getActivity().getBaseContext().getFileStreamPath("profile_pic.png");
        if (!localfile.exists()) {
            setProfileImage();
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
            docPhoto.setImageBitmap(bitmap);
        }
    }

    void saveDocPhoto(StorageReference reference) {
        File localfile = getActivity().getBaseContext().getFileStreamPath("profile_pic.png");
        reference.getFile(localfile);
    }

    private void setProfileImage() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = mStorageRef.child("Doctors")
                .child(new Doctor().getRegistrationID())
                .child("Registration")
                .child("Profile_Picture.jpg");
        Glide.with(getActivity().getApplicationContext()).using(new FirebaseImageLoader()).load(photoRef).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(docPhoto);
  saveDocPhoto(photoRef);
    }


    View findViewById(int id) {
        return v.findViewById(id);
    }
}