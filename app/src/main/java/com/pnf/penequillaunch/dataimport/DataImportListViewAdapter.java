package com.pnf.penequillaunch.dataimport;

import java.util.ArrayList;
import java.util.List;

import com.example.daksh.clinmdequilsmartpenlaunch.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DataImportListViewAdapter extends BaseAdapter {
	Context context;

	List<String> nameList = new ArrayList<String>();
	
	void dealloc(){
		if(nameList != null){
			nameList.clear();
		}
		
	}
	
	public DataImportListViewAdapter(
			Context con ,
			List<String> _nameList) {
		context = con;
		
		nameList.addAll(_nameList);
	}


	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view;
		
		if (convertView == null) {
			view = new View(context);
			view = inflater.inflate(R.layout.dataimportlistview_adapter, null);
		} else {
			view = (View) convertView;
		}
		
		TextView fileNameTextView = (TextView) view.findViewById(R.id.fileNameTextView);
		FrameLayout fileDownLayer = (FrameLayout) view.findViewById(R.id.fileDownLayer);
		FrameLayout fileDeleteLayer = (FrameLayout) view.findViewById(R.id.fileDeleteLayer);
		
		fileDownLayer.setTag(position);
		fileDeleteLayer.setTag(position);
		
		/*
		 * file Name
		 */
		fileNameTextView.setText(nameList.get(position));
		
		return view;
	}
	
	String getFileName(int position){
		return nameList.get(position);
	}
	
	void removeItem(int index){
		nameList.remove(index);
	}

	@Override
	public int getCount() {
		return nameList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}