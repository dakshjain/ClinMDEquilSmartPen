package com.pnf.penequillaunch.mainActivity;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.pnf.penequillaunch.test.MainActivity;
import com.example.daksh.clinmdequilsmartpenlaunch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {

    View v;
    MenuItem penIcon;
    FloatingActionButton fab;
    public static boolean animating = false ;
    int drawviewactivitycount = 0 ;
   

    public TabFragment() {
        // Required empty public constructor
    }

public static TabFragment getTabFragment()
{
    return new TabFragment();
}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_tabs, container, false);
        setHasOptionsMenu(true);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.tab_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        return v;
    }

    private View findViewById(int id) {
        return v.findViewById(id);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

        super.onCreateOptionsMenu(menu, inflater);
        penIcon = menu.findItem(R.id.action_pen);
    }

    public void animatePen() {
        if (penIcon != null) {
            penIcon.setIcon(R.drawable.pen_anim);
            animating = true;
            ((AnimationDrawable) penIcon.getIcon()).start();
            if ((getActivity() instanceof MainActivity)
                    && (((MainActivity) getActivity()).penClientCtrl.isConnected())) {
                penIcon.setIcon(R.drawable.ic_pen_connected);
            }            // penIcon.setIcon(R.drawable.ic_pen_connected);
        }

    }
    public void seticon(){
        if(penIcon!=null){
            penIcon.setIcon(R.drawable.ic_pen_disconnected);
        }
    }
    public void isPenConnected() {
        if(!animating) {
            if (penIcon != null) {
                if ((getActivity() instanceof MainActivity)
                        && (((MainActivity) getActivity()).penClientCtrl.isConnected())) {
                    penIcon.setIcon(R.drawable.ic_pen_connected);
                } else {
                    penIcon.setIcon(R.drawable.ic_pen_disconnected);
                }
            }
        }
        else{
            seticon();
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new PatientListFragment();

                default:

                    return ((MainActivity) getActivity()).pageListFragment;

            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Patients";
                case 1:
                    return "Recent";
            }
            return null;
        }
    }

}
