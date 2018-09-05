package com.pnf.penequillaunch.mainActivity;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pnf.penequillaunch.test.MainActivity;
import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.others.DotDetailsDatabaseHelper;
import com.pnf.penequillaunch.others.PageRecyclerAdapter;


import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PageListFragment extends Fragment {

    public ArrayList<Integer> arrayList;
    public PageRecyclerAdapter adapter;
    View v;


    public PageListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_page_list, container, false);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        Log.d("PageListFragment","inside recent ");


        arrayList = new ArrayList<>();
        setHasOptionsMenu(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setRecycleChildrenOnDetach(false);
        adapter = new PageRecyclerAdapter(arrayList, (MainActivity) getActivity());
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemViewCacheSize(500);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setDrawingCacheEnabled(true);
        LoadData();
        findViewById(R.id.next_bundle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Doctor.currentBundle += 1;
            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        if ((getActivity() instanceof MainActivity)) {
            ((MainActivity) getActivity()).tabFragment.isPenConnected();

        }
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    public void LoadData( )  {
/*
        Cursor c = DotDetailsDatabaseHelper.getdatabasehelper().returnPageList();

            if (c.moveToFirst()) {

                arrayList.clear();
                do {

                    int patientID = c.getInt(0);

                    arrayList.add(patientID);


                    Log.d("retrieving page", "" + patientID);

                } while (c.moveToNext());


        }
            //publishProgress();



        if (adapter != null)
            adapter.notifyDataSetChanged();*/
       new ShowSavedPages().execute();
    }

    private View findViewById(int id) {
        return v.findViewById(id);
    }

    private class ShowSavedPages  extends AsyncTask<Void,Void,Void> {


        @Override
        protected Void doInBackground(Void... avoid) {
            Cursor c = DotDetailsDatabaseHelper.getdatabasehelper().returnPageList();
            if (c.moveToFirst()) {

                arrayList.clear();
                do {

                    int patientID = c.getInt(0);

                    arrayList.add(patientID);

                    publishProgress();
                    Log.d("retrieving page", "" + patientID);

                } while (c.moveToNext());

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... avoid) {
            super.onProgressUpdate();

            adapter.notifyDataSetChanged();

        }


        @Override
        protected void onPostExecute(Void avpoid) {
            super.onPostExecute(avpoid);
            if (getContext() != null)
                if (adapter != null)
                    adapter.notifyDataSetChanged();
          //  int  size=arrayList.size();
          //  arrayList.remove(size);
        }

    }
}
