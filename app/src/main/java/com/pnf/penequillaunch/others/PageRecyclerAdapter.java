package com.pnf.penequillaunch.others;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import com.pnf.penequillaunch.test.MainActivity;

import java.io.FileNotFoundException;
import java.util.ArrayList;


public class PageRecyclerAdapter extends RecyclerView.Adapter<PageRecyclerAdapter.ViewHolder> {
    private final ArrayList<Integer> itemsData;
    private final MainActivity activity;
    static boolean flag = false;
    String newdate = "" ;
    String newtime = "";

    public PageRecyclerAdapter(ArrayList<Integer> itemsData, MainActivity activity) {

        this.itemsData = itemsData;
        this.activity = activity;
    }

    // Create new views (invoked by the layout_recycler manager)
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        flag = !flag;

        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    // Replace the contents of a view (invoked by the layout_recycler manager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        try {

            int value  = Integer.valueOf(itemsData.get(viewHolder.getAdapterPosition()).toString()) - 10000;
            viewHolder.name.setText("Page: " + value);
            viewHolder.name.setTag(itemsData.get(viewHolder.getAdapterPosition()));
            viewHolder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        activity.loadpage(itemsData.get(viewHolder.getAdapterPosition()), "Page: " + itemsData.get(viewHolder.getAdapterPosition()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            viewHolder.dateTime.setText(getDateTime(itemsData.get(viewHolder.getAdapterPosition())));
            viewHolder.time.setText(newtime);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getDateTime(Integer integer) {
        String dateTime = "";

        Cursor c = DotDetailsDatabaseHelper.getdatabasehelper().returnPageDate(integer);
        if (c.moveToFirst()) {

            do {
                /*Date date = new Date(c.getLong(0));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                dateTime = dateFormat.format(date);*/
                dateTime  = c.getString(0);
                dateTime = getdateforview(dateTime);
            } while (c.moveToNext());
        }

        return dateTime;
    }

    private String getdateforview(String dateTime) {
        newdate  = dateTime.substring(6,8)+ " "+new MainActivity().getmonth(dateTime.substring(4,6))+" "+dateTime.substring(0,4);
         newtime = dateTime.substring(8,10) + ":"+dateTime.substring(10,12);
        return  newdate ;
    }

    // Return the size of your itemsData (invoked by the layout_recycler manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView name;
        public final View item;
        public final TextView dateTime;
        public final TextView time ;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            item = itemLayoutView;
            name = (TextView) itemLayoutView.findViewById(R.id.nametxt);
            dateTime = (TextView) itemLayoutView.findViewById(R.id.datetime);
            time = (TextView)itemLayoutView.findViewById(R.id.time);

        }

    }
}

/*
    public static class LogData {



        public static Context context;
        private final String RealNum;
        private final String FakeNum;
        private final String Name;
        private final String Time;
        private final String Duration;
        public static Activity activity;
        private final String imageID;

        private final byte[] photo;

        public LogData(String RealNum, String FakeNum, String Name, String imageID,String time, String duration,byte[] photo, Context context, Activity activity) {
            this.RealNum=RealNum;
            this.FakeNum=FakeNum;
            this.Name=Name;
            this.photo=photo;
            this.imageID=imageID;
            Time=time;
            Duration=duration;
            LogData.context=context;
            LogData.activity=activity;
        }

        public String getTime() {
            return Time;
        }

        public String getDuration() {
            return Duration;
        }

        public static Activity getActivity() {
            return activity;
        }

        public static Context getContext() {
            return context;
        }


        public String getRealNum() {
            return RealNum;
        }


        public String getFakeNum() {
            return FakeNum;
        }


        public String getName() {
            return Name;
        }


        public byte[] getPhoto() {
            return photo;
        }

        public String getImageID() {
            return imageID;
        }
    }
*/
/*
    public class LoadImage extends AsyncTask<Void ,Void ,Bitmap>{

        Map<String,Bitmap> photos=new HashMap<>();
        Set<String> numbers=new HashSet<>();
        @Override
        protected void onPostExecute(Bitmap photo) {
            if (photo!=null)
            viewHolder.contactImage.setImageBitmap(photo);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap photo;
            if(numbers.contains(itemsData.get(position).getRealNum()))
            {
                return photos.get(itemsData.get(position).getRealNum());
            }
            else
            {
                if(itemsData.get(position).getPhoto().length!=0&&itemsData.get(position).getPhoto()!=null)
                {
                    photo =BitmapFactory.decodeByteArray(itemsData.get(position).getPhoto(),0,itemsData.get(position).getPhoto().length);
                    photos.put(itemsData.get(position).getRealNum(),photo);
                    numbers.add(itemsData.get(position).getRealNum());
                    return  photo;
                }

            }
            return null;
        }
    }
*/
