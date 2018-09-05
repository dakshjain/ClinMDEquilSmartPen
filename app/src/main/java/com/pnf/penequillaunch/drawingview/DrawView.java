package com.pnf.penequillaunch.drawingview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.pnf.penequillaunch.test.MainDefine;

import java.util.ArrayList;

public class DrawView extends View {
	Context mContext;
	private Bitmap mBitmap_img;
	Paint mPaint;
	public Bitmap mBitmap;
	Canvas mCanvas;
	RectF m_rectMain;
	Path mPath;
	
	Path rectPath;
	static int calibrated_width ;
	
	public Paint EraserPaint;
	
	int iDrawCnt = 0;
	
	PointF	previousPoint1 = new PointF();
	PointF	previousPoint2 = new PointF();
	PointF	currentPoint = new PointF();
	
	ArrayList<PointF> m_Stroke;

	int y_increment =0;
	
	public DrawView(Context c)
	{
		super(c);
		initView(c);
	}
	
	public DrawView(Context c, AttributeSet attrs) 
	{
		super(c, attrs);
		initView(c);
		
	}
	
	public void initView(Context c)
	{
		mContext = c;
		
		m_Stroke = new ArrayList<PointF>();
		
		mPath = new Path();
		rectPath = new Path();
		
		if(mPaint == null){
			mPaint = new Paint();
			
			mPaint.setStrokeWidth(1);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setColor(Color.rgb(0, 0, 0));
			mPaint.setAntiAlias(true);
			
		}
		
		if(EraserPaint == null){
			EraserPaint = new Paint();
			EraserPaint.setStyle(Paint.Style.FILL);
			EraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

		}
		
		m_rectMain = new RectF(0 , 0, MainDefine.iDisGetWidth, MainDefine.iDisGetHeight);
		Point changeSize = MainDefine.changeResolution();
		int _width = changeSize.x;
		int _height = changeSize.y;
		m_rectMain = new RectF(0 , 0, _width, _height);
		if(mBitmap == null){


			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inMutable = true;
			opt.inSampleSize=1;

			mBitmap_img  = BitmapFactory.decodeFile(getContext().getFileStreamPath("prescription.png").getAbsolutePath(),opt);
			//mBitmap = Bitmap.createBitmap(MainDefine.iDisGetWidth, MainDefine.iDisGetHeight, Bitmap.Config.ARGB_4444);
			Log.d("idisgetwidth"," "+MainDefine.iDisGetWidth);
			Log.d("idisgetheight"," "+MainDefine.iDisGetHeight);
			mBitmap = Bitmap.createScaledBitmap(mBitmap_img, MainDefine.iDisGetWidth, MainDefine.iDisGetHeight, true);

			mCanvas = new Canvas(mBitmap);

		}
	}
	
	void initData(){
		if(mPath != null){
			mPath.reset();
		}
		if(mPath != null){
			rectPath.reset();
		}
		
		if(mCanvas != null){
			mCanvas.drawPaint(EraserPaint);
			
		}
		
		invalidate();
	}
	
	public void changeDrawingSize(int _width ,int _height){
		if(m_rectMain == null || mBitmap == null || mBitmap.getWidth() != _width || mBitmap.getHeight() != _height){
			if(mBitmap != null){
				mBitmap.recycle();
				mBitmap = null;
		    }
		    
		    m_rectMain = new RectF(0 , 0, _width, _height);

			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inMutable = true;
			opt.inSampleSize=1;

			mBitmap_img  = BitmapFactory.decodeFile(getContext().getFileStreamPath("prescription.png").getAbsolutePath(),opt);
			//mBitmap = Bitmap.createBitmap(MainDefine.iDisGetWidth, MainDefine.iDisGetHeight, Bitmap.Config.ARGB_4444);
			mBitmap = Bitmap.createScaledBitmap(mBitmap_img, MainDefine.iDisGetWidth, MainDefine.iDisGetHeight, true);
			mCanvas = new Canvas(mBitmap);
		}
		
		btnEraser();
	}
	
	@Override
	protected void onDraw(Canvas c) {
		c.drawBitmap(mBitmap, 0, 0, null);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		float x = event.getX();
//		float y = event.getY();
//		RectF drawRect = null;
//		
//		if(isPenDraw){
//			return false;
//		}
//		
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			DoMouseDown(x ,y);
//			
//			break;
//		case MotionEvent.ACTION_MOVE:
//			DoMouseDragged(x ,y);
//			
//			drawRect = new RectF();
//			rectPath.computeBounds(drawRect, true);
//			invalidate(RectFtoRect(drawRect,10));
//			
//			rectPath.reset();
//			break;
//		case MotionEvent.ACTION_UP:
//			DoMouseUp(x ,y);
//			
//			drawRect = new RectF();
//			rectPath.computeBounds(drawRect, true);
//			invalidate(RectFtoRect(drawRect,10));
//			
//			rectPath.reset();
//			break;
//		}
		
		return true;
	}
	
	public void invalidatePath(){
		RectF drawRect = new RectF();
		rectPath.computeBounds(drawRect, true);
		invalidate(RectFtoRect(drawRect,10));
	}
	
	public void DoMouseDown(float x ,float y){
		/*Log.d("y real"," "+y);
		double y1  =(double) y*y ;
		double height  = 50*50;
		y = (float)Math.sqrt(y1+height);
		Log.d("y new "," "+y);*/
		iDrawCnt = 0;
		
		previousPoint1 = new PointF(x ,y);
		previousPoint2 = new PointF(x ,y);
		currentPoint = new PointF(x ,y);
	}
	
	public void DoMouseDragged(float x ,float y){
		/*Log.d("y real"," "+y);
		double y1  =(double) y*y ;
		double height  = 50*50;
		y = (float)Math.sqrt(y1+height);
		Log.d("y new "," "+y);*/

		PointF mid1 = new PointF();
		PointF mid2 = new PointF();
		
		if(iDrawCnt == 0 ){
			previousPoint2 = new PointF(previousPoint1.x ,previousPoint1.y);
			currentPoint = new PointF(x ,y);
		}else{
			previousPoint2 = new PointF(previousPoint1.x ,previousPoint1.y);
			previousPoint1 = new PointF(currentPoint.x ,currentPoint.y);
			currentPoint = new PointF(x ,y);
		}
		
		mid1 = BizMidPoint(previousPoint1, previousPoint2);
		mid2 = BizMidPoint(currentPoint, previousPoint1);
		
		mPath.moveTo(mid1.x, mid1.y);
		mPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
		
		mCanvas.drawPath(mPath, mPaint);
		iDrawCnt++;
		
		rectPath.moveTo(mid1.x, mid1.y);
		rectPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
		
		RectF drawRect = new RectF();
		rectPath.computeBounds(drawRect, true);
		invalidate(RectFtoRect(drawRect,40));
	}

	public void DoMouseUp(float x ,float y){
	/*	Log.d("y real"," "+y);
		double y1  =(double) y*y ;
		double height  = 50*50;
		y = (float)Math.sqrt(y1+height);
		Log.d("y new "," "+y);*/

		if(iDrawCnt == 0) return;
		iDrawCnt = 0;
		
		previousPoint2 = new PointF(previousPoint1.x ,previousPoint1.y);
		previousPoint1 = new PointF(currentPoint.x ,currentPoint.y);
		currentPoint = new PointF(x ,y);
		
		PointF mid1 = BizMidPoint(previousPoint1, previousPoint2);
		PointF mid2 = BizMidPoint(currentPoint, previousPoint1);

		mPath.moveTo(mid1.x, mid1.y);
		mPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
		mPath.lineTo(currentPoint.x, currentPoint.y);
		
		mCanvas.drawPath(mPath, mPaint);
		
		rectPath.moveTo(mid1.x, mid1.y);
		rectPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
		rectPath.moveTo(currentPoint.x, currentPoint.y);
		
		RectF drawRect = new RectF();
		rectPath.computeBounds(drawRect, true);
		invalidate(RectFtoRect(drawRect,40));
		
		mPath.reset();
		rectPath.reset();
	}
	
	public void setPenMode(int color){
		setPenColor(color);
	}
	
	public void setPenColor(int color){
		mPaint.setColor(color);
	}

	public void btnEraser()
	{
		//mCanvas.drawPaint(EraserPaint);
		invalidate();
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inMutable = true;
		opt.inSampleSize=1;

		mBitmap_img  = BitmapFactory.decodeFile(getContext().getFileStreamPath("prescription.png").getAbsolutePath(),opt);
		//mBitmap = Bitmap.createBitmap(MainDefine.iDisGetWidth, MainDefine.iDisGetHeight, Bitmap.Config.ARGB_4444);
		Log.d("idisgetwidth"," "+MainDefine.iDisGetWidth);
		Log.d("idisgetheight"," "+MainDefine.iDisGetHeight);
		mBitmap = Bitmap.createScaledBitmap(mBitmap_img, MainDefine.iDisGetWidth, MainDefine.iDisGetHeight, true);
		mCanvas = new Canvas(mBitmap);

	}
	public void clearPath(){
		mPath.reset();
	}
	public void clearAll(){
		invalidate();
		mBitmap=null;
		mCanvas=null;


	}
	
	PointF BizMidPoint(PointF pt,PointF pt2)
	{
		return new PointF((pt.x + pt2.x)/2, (pt.y + pt2.y)/2);
	}
	
	Rect RectFtoRect(RectF rectf,float thick)
	{

		Rect rect = new Rect((int)(rectf.left-thick) ,(int)(rectf.top-thick) ,(int)(rectf.right+thick) ,(int)(rectf.bottom+thick));
		return rect;
	}
}