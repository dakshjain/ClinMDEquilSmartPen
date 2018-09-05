package com.pnf.penequillaunch.calibration;

import com.pnf.bt.lib.PNFDefine;
import com.pnf.penequillaunch.test.MainDefine;
import com.example.daksh.clinmdequilsmartpenlaunch.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;


public class CalibrationPointActivity extends Activity
{
	ImageView[] caliPointImgView;
	ImageView caliShowPopup;
	
	PointF m_posCoordinate[] = null;
	PointF m_posRestultPoint[] = null;
	PointF m_posDrawCoordinate[] = null;
	
	int m_nCoordinateCounter;
	int m_nCoordinateDrawPoint;
	int m_nCoordinateResuntPoint;
	
	Animation coordinateAni = null;
    Animation popupAni = null;
    boolean isAnimation = false;
    boolean isFirstActivitFocus = true;
    
	boolean isHasFocus = false;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		if(MainDefine.penController != null){
			MainDefine.penController.SetRetObj(penHandler);
			MainDefine.penController.SetRetObjForEnv(null);
		}
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
	}
	
	@Override
	protected void onStart(){
    	super.onStart();
    }
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
    protected void onDestroy()
    {
    	super.onDestroy();
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	isHasFocus = hasFocus;
    	if(m_posDrawCoordinate == null) initCoordinateData();
    	
    	if(isFirstActivitFocus){
    		isFirstActivitFocus = false;
    	}
    }
	
	@Override
	public void onBackPressed() 
	{
		if(isAnimation) return;
		if(!isHasFocus) return;
		if(isFirstActivitFocus) return;
		
		finish();
		return;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		m_nCoordinateCounter = 0;
		m_nCoordinateDrawPoint = 2;
		m_nCoordinateResuntPoint = 4;
		
		caliPointImgView = new ImageView[m_nCoordinateResuntPoint];
		
		setContentView(R.layout.calibration2point);
		
		for(int i=0;i<m_nCoordinateDrawPoint;i++){
			String imgStr = "caliPoint"+(i+1)+"ImgView";
			caliPointImgView[i] = (ImageView) findViewById(ResourcesIdNameToId(imgStr));
		}
		
		caliShowPopup = (ImageView)findViewById(R.id.caliShowPopup);
	}
	
	int ResourcesIdNameToId(String imgName){
		return getResources().getIdentifier(imgName, "id", getApplicationContext().getPackageName());
	}
	
	Rect getGlobalVisibleRect(View view){
		Rect r = new Rect();
		view.getGlobalVisibleRect(r);
		return r;
	}
	
	int ResourcesImgNameToId(String idName){
		if(idName.contains(".png")){
			idName = idName.substring(0, idName.lastIndexOf("."));
		}
		return getResources().getIdentifier(idName, "drawable", getApplicationContext().getPackageName());
	}
    
    void initCoordinateData(){
    	m_posDrawCoordinate = new PointF[m_nCoordinateDrawPoint];
    	for(int i=0;i<m_nCoordinateDrawPoint;i++){
    		Rect rectGlobal = getGlobalVisibleRect(caliPointImgView[i]);
    		m_posDrawCoordinate[i] = new PointF(rectGlobal.centerX(), rectGlobal.centerY());
    	}
    	
    	m_posRestultPoint = new PointF[m_nCoordinateResuntPoint];
    	for(int i=0;i<m_nCoordinateResuntPoint;i++){
    		m_posRestultPoint[i] = new PointF();
    	}
    	
    	m_posCoordinate = new PointF[m_nCoordinateResuntPoint];
    	m_posCoordinate[0] = new PointF(0, 0);
    	m_posCoordinate[1] = new PointF(MainDefine.iDisGetWidth, 0);
    	m_posCoordinate[2] = new PointF(MainDefine.iDisGetWidth, MainDefine.iDisGetHeight);
    	m_posCoordinate[3] = new PointF(0, MainDefine.iDisGetHeight);
    	
    	caliShowPopup.setVisibility(View.GONE);
    	reSetCalibration();
	}

    void reSetCalibration(){
    	m_nCoordinateCounter = 0;
    	for(int i=0;i<m_nCoordinateDrawPoint;i++){
    		if(i == m_nCoordinateCounter){
    			caliPointImgView[i].setSelected(false);
    			caliPointImgView[i].setVisibility(View.VISIBLE);
    		}else{
    			caliPointImgView[i].setVisibility(View.GONE);
    		}
    	}
    }
    
    public void reTryBtnClicked(View v){
    	if(isFirstActivitFocus) return;
    	if(isAnimation) return;
    	
    	reSetCalibration();
    }
    
    public void exitBtnClicked(View v){
    	if(isFirstActivitFocus) return;
    	if(isAnimation) return;
    	
    	finish();
    }
    
    void setCaliPoint(int position){
    	final int moveIdx = position;
    	if(moveIdx < m_nCoordinateDrawPoint){
	    	coordinateAni = new TranslateAnimation(
	    			0,
	    			m_posDrawCoordinate[moveIdx].x-m_posDrawCoordinate[moveIdx-1].x,
	    			0,
	    			m_posDrawCoordinate[moveIdx].y-m_posDrawCoordinate[moveIdx-1].y);
	
	    	coordinateAni.setDuration(300);
	    	coordinateAni.setFillAfter(false);
	
	    	caliPointImgView[moveIdx-1].startAnimation(coordinateAni);
	    	coordinateAni.setAnimationListener(new AnimationListener() {
	    		@Override
	    		public void onAnimationStart(Animation animation) {
	    			
	    		}
	
	    		@Override
	    		public void onAnimationRepeat(Animation animation) {
	    			
	    		}
	
	    		@Override
	    		public void onAnimationEnd(Animation animation) {
	    			
	    			coordinateAni.cancel();
	    			caliPointImgView[moveIdx-1].clearAnimation();
	    			
	    			caliPointImgView[moveIdx-1].setVisibility(View.GONE);
	    			
	    			caliPointImgView[moveIdx].setSelected(false);
	    			caliPointImgView[moveIdx].setVisibility(View.VISIBLE);
	    			
	    			
	    		}
	    	});
    	}else{
    		caliPointImgView[moveIdx-1].setVisibility(View.GONE);
    	}
    	
    	String imgStr = "e09_cali_check_0"+moveIdx;
    	
    	caliShowPopup.setVisibility(View.VISIBLE);
    	caliShowPopup.setImageResource(ResourcesImgNameToId(imgStr));
    	popupAni = AnimationUtils.loadAnimation(this, R.anim.zoom_out_alpha_fast);
		caliShowPopup.startAnimation(popupAni);
		popupAni.setAnimationListener(new AnimationListener() {
    		@Override
    		public void onAnimationStart(Animation animation) {
    			isAnimation = true;
    		}

    		@Override
    		public void onAnimationRepeat(Animation animation) {
    		}

    		@Override
    		public void onAnimationEnd(Animation animation) {
    			isAnimation = false;
    			
    			caliShowPopup.setVisibility(View.GONE);
    			
    			if(moveIdx == m_nCoordinateDrawPoint){
    				showAlertView();
    				
    			}
    		}
    	});
    }
    
    void showAlertView(){
    	m_posRestultPoint[1] = new PointF(m_posRestultPoint[2].x,m_posRestultPoint[0].y);
		m_posRestultPoint[3] = new PointF(m_posRestultPoint[0].x,m_posRestultPoint[2].y);

		MainDefine.penController.setCalibrationData(m_posCoordinate, 0, m_posRestultPoint);
		Log.d("calibration"," result :"+m_posRestultPoint[0].x+", "+m_posRestultPoint[0].y+", "+m_posRestultPoint[2].x+", "+m_posRestultPoint[2].y);
        Toast.makeText(this, " result :"+m_posRestultPoint[0].x+", "+m_posRestultPoint[0].y+", "+m_posRestultPoint[2].x+", "+m_posRestultPoint[2].y, Toast.LENGTH_LONG).show();
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
		
		if(isAnimation) return;
		
		if(penState == PNFDefine.PEN_DOWN || penState == PNFDefine.PEN_MOVE)
		{
			if(m_nCoordinateCounter < m_nCoordinateDrawPoint && !caliPointImgView[m_nCoordinateCounter].isSelected()){
				caliPointImgView[m_nCoordinateCounter].setSelected(true);
			}
			
		}else if(penState == PNFDefine.PEN_UP)
		{
			if(m_nCoordinateCounter < m_nCoordinateDrawPoint)							
			{
				caliPointImgView[m_nCoordinateCounter].setSelected(false);
				switch(m_nCoordinateCounter)
				{
				case 0:
					m_posRestultPoint[0] = new PointF(RawX ,RawY);
					break;
				case 1:
					m_posRestultPoint[2] = new PointF(RawX ,RawY);
					break;
				}
				
				m_nCoordinateCounter++;
				setCaliPoint(m_nCoordinateCounter);
			}
		}
	}
	
	boolean isCalibPointCheck(){
		boolean isRtn = true;
		if(m_posRestultPoint[2].x <= m_posRestultPoint[0].x || 
				m_posRestultPoint[2].y <= m_posRestultPoint[0].y || 
				m_posRestultPoint[2].x - m_posRestultPoint[0].x < 100 || 
				m_posRestultPoint[2].y - m_posRestultPoint[0].y < 100){
			isRtn = false;
		}
		
		return isRtn;
	}
}