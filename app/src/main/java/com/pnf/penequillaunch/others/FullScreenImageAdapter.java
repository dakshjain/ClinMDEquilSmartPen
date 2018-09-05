package com.pnf.penequillaunch.others;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.mainActivity.ImageFragment;
import com.pnf.penequillaunch.test.MainActivity;


public class FullScreenImageAdapter extends PagerAdapter {

    private MainActivity _activity;
    private  ArrayList<String> _imagePaths;
    private ArrayList<String> datelist ;
    private LayoutInflater inflater;
    int count = 0 ;
    // constructor
    public FullScreenImageAdapter(MainActivity activity,
                                  ArrayList<String> imagePaths , ArrayList<String> datelist) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.datelist = datelist ;
    }
    public  FullScreenImageAdapter(){

    }
    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    public void clearimagepaths(){
        _imagePaths.clear();
        new ImageFragment().adapter.notifyDataSetChanged();
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageView imgDisplay;
        Button btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);


        imgDisplay = (ZoomImageView) viewLayout.findViewById(R.id.imgDisplay);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            String test  = _imagePaths.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
            if(bitmap==null)
                viewLayout.findViewById(R.id.downloading_progress).setVisibility(View.VISIBLE);

            imgDisplay.setImageBitmap(bitmap);


           /* String month = new MainActivity().getmonth(datelist.get(position).substring(4,6));
            String day = datelist.get(position).substring(6,8);
            Log.d("day",""+day);
            count = count+1;
            Log.d("count",""+position);
            String year = datelist.get(position).substring(0,4);
            _activity.getSupportActionBar().setTitle(day+" "+month+" "+year);*/



        }catch(Exception e){
            e.printStackTrace();
        }

        // close button click event

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

}