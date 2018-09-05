package com.pnf.penequillaunch.popup;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.daksh.clinmdequilsmartpenlaunch.R;

public class PenSleepView extends Dialog implements android.view.View.OnClickListener {
	Button aliveDialogCompleteBtn;
	
	public PenSleepView(Context con){
		super(con);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.pensleepview);
		setCancelable(false);
		
		aliveDialogCompleteBtn = (Button) findViewById(R.id.aliveDialogCompleteBtn);
		aliveDialogCompleteBtn.setOnClickListener(this);
	}
	
	
	void start(){
		show();
	}
	
	void finish(){
		if(aliveDialogCompleteBtn != null){
			aliveDialogCompleteBtn = null;
		}
		dismiss();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.aliveDialogCompleteBtn:
			if(onDataListener != null){
				onDataListener.onResultFinish();
			}
			break;
			
		}
	}
	
	private OnDataListener onDataListener = null;
	public interface OnDataListener {
		public abstract void onResultFinish();
	}
	public void setOnDataListener(OnDataListener listener) {
		onDataListener = listener;
	}
}