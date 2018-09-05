package com.pnf.penequillaunch.drawingview;

import com.pnf.bt.lib.PNFDefine;
import com.pnf.bt.lib.PenDataClass;
import com.pnf.penequillaunch.others.ZoomImageView;
import com.pnf.penequillaunch.test.MainActivity;
import com.pnf.penequillaunch.test.MainDefine;
import com.example.daksh.clinmdequilsmartpenlaunch.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class DrawViewActivity extends Activity{
	ZoomImageView drawViewBGImgView;
	
	FrameLayout drawNoteLayer;
	DrawView drawView;
	
	boolean isHasFocus = false;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		MainDefine.penController.SetRetObj(penHandler);
		MainDefine.penController.SetRetObjForEnv(penEnvHandler);
		MainDefine.penController.SetRetObjForMsg(messageHandler);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onStop(){
		super.onStop();
		MainActivity.activityopened = false ;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
	}
	
	@Override
    public void onWindowFocusChanged(boolean hasFocus) {
		isHasFocus = hasFocus;
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.drawview);
		drawViewBGImgView = (ZoomImageView) findViewById(R.id.drawViewBGImgView);
		
		drawNoteLayer = (FrameLayout) findViewById(R.id.drawNoteLay);
		drawView = (DrawView) findViewById(R.id.drawView);
		
		Point changeSize = MainDefine.changeResolution();
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(changeSize.x ,changeSize.y);
        params.gravity = Gravity.CENTER;
        drawNoteLayer.setLayoutParams(params);
    	
    	drawView.setLayoutParams(new FrameLayout.LayoutParams( LayoutParams.MATCH_PARENT ,LayoutParams.MATCH_PARENT));
		Log.d("x & y"," "+changeSize.x+" , "+changeSize.y);
		drawView.changeDrawingSize(changeSize.x ,changeSize.y);
		
		drawView.invalidate();
	}
	
	public void ClearAllBtnClicked(View v){
		drawView.btnEraser();
	}

	public void CloseBtnClicked(View v){
		Intent intent = new Intent();
		intent.putExtra("debug", debugStr);
		
		setResult(RESULT_OK,intent);
		finish();
	}
	
	@SuppressLint("HandlerLeak")
	Handler penHandler = new Handler() 
	{        
		@Override
		public void handleMessage(Message msg) 
		{
			onPenEvent(msg.what ,msg.arg1 ,msg.arg2 ,msg.obj);
		}
	};
	
	void onPenEvent(int penState, int RawX, int RawY ,Object obj)
	{
		if(!isHasFocus) return;
		
		PenDataClass penData = (PenDataClass)obj;
		PointF ptConv = MainDefine.penController.GetCoordinatePostionXY(RawX ,RawY ,penData.bRight);
		
		switch(penState)
		{
		case PNFDefine.PEN_DOWN:
			drawView.DoMouseDown(ptConv.x ,ptConv.y);
			break;
		case PNFDefine.PEN_MOVE:
			drawView.DoMouseDragged(ptConv.x ,ptConv.y);
			drawView.invalidatePath();
			break;
		case PNFDefine.PEN_UP:
			drawView.DoMouseUp(ptConv.x ,ptConv.y);
			drawView.invalidatePath();
			break;
			
			
		case PNFDefine.PEN_HOVER:
			break;
		case PNFDefine.PEN_HOVER_DOWN:
			break;
		case PNFDefine.PEN_HOVER_MOVE:
			break;
		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler penEnvHandler = new Handler() 
	{        
		@Override
		public void handleMessage(Message msg) 
		{
			onPenEnvEvent(msg.what ,msg.obj);
		}
	};
	
	void onPenEnvEvent(int what, Object obj)
	{
		if(what==PNFDefine.PNF_MSG_NEW_PAGE_BTN){
			addDebugText("PNF_MSG_NEW_PAGE_BTN");
			return;
		}
	}
	
	@SuppressLint("HandlerLeak")
	Handler messageHandler = new Handler() 
	{        
		@Override
		public void handleMessage(Message msg) 
		{
			onMessageEvent(msg.what ,msg.obj);
		}
	};
	
	void onMessageEvent(int what, Object obj)
	{
		if(!isHasFocus) return;
		
		if(what == PNFDefine.PNF_MSG_FAIL_LISTENING){
			return;
		}else if(what == PNFDefine.PNF_MSG_CONNECTED){
			addDebugText("PNF_MSG_CONNECTED");
		}
		else if(what == PNFDefine.PNF_MSG_INVALID_PROTOCOL){
			return;
		}
		
		else if(what == PNFDefine.PNF_MSG_SESSION_CLOSED){
			addDebugText("PNF_MSG_SESSION_CLOSED");
		}
		else if(what == PNFDefine.PNF_MSG_PEN_RMD_ERROR){
			Toast.makeText(
					getApplicationContext(),
					MainDefine.getLangString(getApplicationContext() ,R.string.PEN_RMD_ERROR_MSG),
					Toast.LENGTH_SHORT)
				.show();
			return;
		}
		else if(what == PNFDefine.PNF_MSG_FIRST_DATA_RECV){
			addDebugText("PNF_MSG_FIRST_DATA_RECV");
		}

		else if(what == PNFDefine.GESTURE_CIRCLE_CLOCKWISE){
			addDebugText("GESTURE_CIRCLE_CLOCKWISE");
			return;
		}
		else if(what == PNFDefine.GESTURE_CIRCLE_COUNTERCLOCKWISE){
			addDebugText("GESTURE_CIRCLE_COUNTERCLOCKWISE");
			return;
		}
		else if(what == PNFDefine.GESTURE_CLICK){
			addDebugText("GESTURE_CLICK");
			return;
		}
		else if(what == PNFDefine.GESTURE_DOUBLECLICK){
			addDebugText("GESTURE_DOUBLECLICK");
			return;
		}
		if(what == PNFDefine.PNF_MSG_NEW_PAGE_BTN){
			addDebugText("PNF_MSG_NEW_PAGE_BTN");
			return;
		}
	}
	
	String debugStr = "";
	void addDebugText(String text) {
    	String orgText = debugStr;
    	String inputText = "";
    	if(orgText.isEmpty()){
    		inputText = text;
    	}else{
    		inputText = orgText + "\n" + text;
    	}
    	debugStr = inputText;
    }
}
