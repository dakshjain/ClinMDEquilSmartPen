package com.pnf.penequillaunch.dataimport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pnf.bt.lib.memory.DataImportFigure;
import com.pnf.bt.lib.memory.DataImportStroke;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.pnf.penequillaunch.others.Doctor;
import com.pnf.penequillaunch.test.MainActivity;
import com.pnf.penequillaunch.test.MainDefine;

public class DataImportPreView extends View {
	Context mContext;

	Paint mPaint;

	public Bitmap mBitmap;
	Canvas mCanvas;

	public Bitmap accBitmap;
	Canvas accCanvas;
	MainActivity activity ;
	boolean isAccMode = false;

	PointF previousPoint1 = null;
	PointF previousPoint2 = null;
	PointF currentPoint = null;
	private ArrayList<Path> paths = new ArrayList<Path>();
	private Bitmap accBitmap_img;
	private Bitmap mBitmap_img;


	public DataImportPreView(Context c , MainActivity activity)
	{
		super(c);

		initView(c);
		this.activity = activity ;

	}

	public DataImportPreView(Context c, AttributeSet attrs)
	{
		super(c);


		initView(c);
	}


	void initView(Context c)
	{
		mContext = c;

		if(mPaint == null){
			mPaint = new Paint();//Paint.ANTI_ALIAS_FLAG

			mPaint.setStrokeWidth(1);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setColor(Color.rgb(0, 0, 0));
			mPaint.setAntiAlias(true);
			//mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN.SRC_OVER));
		}
	}

	public void setAccMode(boolean _isAcc){
		isAccMode = _isAcc;
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void setDrawingSize(int _width, int _height, boolean _isAcc){
	/*	BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inMutable = true;*/
		if(_isAcc){
			if(accBitmap != null){
				accBitmap.recycle();
				accBitmap = null;
		    }

			//Drawable d = getResources().getDrawable(R.drawable.prescription);
			//d.setBounds(0, 0, 0, 0);
			StorageReference reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("prescription.png");

			final File localfile = getContext().getFileStreamPath("prescription.png");
			if (!localfile.exists()) {
				reference.getFile(localfile).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						if (localfile.exists())
							localfile.delete();
					}
				});
			}
			definebitmap_acc();
			/*accBitmap = Bitmap.createBitmap(_width ,_height ,Bitmap.Config.ARGB_8888);
			accBitmap_img= BitmapFactory.decodeResource(getResources(),R.drawable.prescription,opt);
			accBitmap = Bitmap.createBitmap(accBitmap_img);
			//accCanvas = new Canvas(accBitmap.copy(Bitmap.Config.ARGB_8888,true));
			accCanvas = new Canvas(accBitmap);*/
			//accCanvas.drawBitmap(accBitmap,_width,_height,mPaint);
			//accCanvas.drawColor(Color.WHITE);
			//d.draw(accCanvas);

		}else{
			if(mBitmap != null){
				mBitmap.recycle();
				mBitmap = null;
		    }

			String regid= new Doctor().getRegistrationID();
			StorageReference reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("prescription.png");

 			final File localfile = getContext().getFileStreamPath("prescription.png");
			if (!localfile.exists()) {

				reference.getFile(localfile).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.d("inside","failure");
						if (localfile.exists())

							localfile.delete();
					}
				}).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
						Log.d("inside","success");
					}
				});
			}
			//ImageView iv = (ImageView)findViewById(R.id.imageView);
			definebitmap();
		/*	mBitmap = Bitmap.createBitmap(_width,_height ,Bitmap.Config.ARGB_8888);
			Log.d("name",""+localfile.getAbsolutePath());
			mBitmap_img  = BitmapFactory.decodeFile(getContext().getFileStreamPath("prescription.png").getAbsolutePath(),opt);
			//mBitmap_img = BitmapFactory.decodeResource(getResources(),R.drawable.prescription,opt);
			mBitmap = Bitmap.createBitmap(mBitmap_img);
			//mCanvas = new Canvas(mBitmap.copy(Bitmap.Config.ARGB_8888,true));
			mCanvas = new Canvas(mBitmap);*/
			//Drawable d = getResources().getDrawable(R.drawable.prescription);
			//d.setBounds(100, 100, 100, 100);
			//mCanvas.drawBitmap(mBitmap,_width,_height,mPaint);
			//mCanvas.drawColor(Color.WHITE);
			//d.draw(mCanvas);

		}
	}

	public void drawFigure(List<DataImportFigure> _figureList, boolean _isAcc){

		for(int i=0;i<_figureList.size();i++){
			DataImportFigure figure = _figureList.get(i);

			Path mPath = new Path();

			for(int j=0;j<figure.m_Stroke.size();j++){
				DataImportStroke stroke = figure.m_Stroke.get(j);

				if(j == 0){
					previousPoint1 = new PointF(stroke.pt.x ,stroke.pt.y);
					previousPoint2 = new PointF(stroke.pt.x ,stroke.pt.y);
					currentPoint = new PointF(stroke.pt.x ,stroke.pt.y);
				}else{
					PointF mid1 = new PointF();
					PointF mid2 = new PointF();

					previousPoint2 = new PointF(previousPoint1.x ,previousPoint1.y);
					previousPoint1 = new PointF(currentPoint.x ,currentPoint.y);
					currentPoint = new PointF(stroke.pt.x ,stroke.pt.y);

					mid1 = BizMidPoint(previousPoint1, previousPoint2);
					mid2 = BizMidPoint(currentPoint, previousPoint1);

					if(j == figure.m_Stroke.size()-1){
						mPath.moveTo(mid1.x, mid1.y);
						mPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);
						mPath.lineTo(currentPoint.x, currentPoint.y);

						if(_isAcc){

							accCanvas.drawPath(mPath, mPaint);
							mPath = new Path();
							paths.add(mPath);
						}else{
							mCanvas.drawPath(mPath, mPaint);
							mPath = new Path();
							paths.add(mPath);
						}
					}else{
						mPath.moveTo(mid1.x, mid1.y);
						mPath.quadTo(previousPoint1.x, previousPoint1.y, mid2.x, mid2.y);

						if(_isAcc){
							accCanvas.drawPath(mPath, mPaint); mPath = new Path();
							paths.add(mPath); mPath = new Path();
							paths.add(mPath);
						}else{
							mCanvas.drawPath(mPath, mPaint);
							mPath = new Path();
							paths.add(mPath);
						}
					}
				}
			}
		}
	}
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private void definebitmap(){

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inMutable = true;
		mBitmap_img  = BitmapFactory.decodeFile(getContext().getFileStreamPath("prescription.png").getAbsolutePath(),opt);
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		mBitmap = Bitmap.createScaledBitmap(mBitmap_img, MainDefine.iDisGetWidth,MainDefine.iDisGetHeight,true);
		//mBitmap = Bitmap.createBitmap(mBitmap_img);
		mBitmap.setConfig(Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	private void definebitmap_acc(){

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inMutable = true;
		accBitmap_img  = BitmapFactory.decodeFile(getContext().getFileStreamPath("prescription.png").getAbsolutePath(),opt);

		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		accBitmap =Bitmap.createScaledBitmap(accBitmap_img,size.x+150,size.y,true);
		accBitmap.setConfig(Bitmap.Config.ARGB_8888);
		//accBitmap = Bitmap.createBitmap(accBitmap_img);
		accCanvas= new Canvas(accBitmap);
	}
	PointF BizMidPoint(PointF pt,PointF pt2)
	{
		return new PointF((pt.x + pt2.x)/2, (pt.y + pt2.y)/2);
	}

	@Override
	public void onDraw(Canvas c){
		if(mBitmap!=null){
			c.drawBitmap(mBitmap,0,0,null);
			for (Path p : paths) {
				c.drawPath(p, mPaint);
			}
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void drawimage(boolean isacc) {
	/*	BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inMutable = true;*/
		StorageReference reference = FirebaseStorage.getInstance().getReference().child("Doctors").child(new Doctor().getRegistrationID()).child("prescription.png");

		final File localfile = getContext().getFileStreamPath("prescription.png");
		DisplayMetrics displayMetrics = new DisplayMetrics();
		//getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		if (!localfile.exists()) {
			reference.getFile(localfile).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					if (localfile.exists())
						localfile.delete();
				}
			});
		}
		if(isacc){
			/*accBitmap_img = BitmapFactory.decodeFile(localfile.getAbsolutePath(),opt);

			//accCanvas = new Canvas(accBitmap.copy(Bitmap.Config.ARGB_8888,true));
			accBitmap = Bitmap.createBitmap(accBitmap_img);
			accCanvas.drawBitmap(accBitmap,width,height,mPaint);*/
			definebitmap_acc();


		}else{
			/*mBitmap_img = BitmapFactory.decodeFile(localfile.getAbsolutePath(),opt);
			mBitmap = Bitmap.createBitmap(mBitmap_img);*/
			definebitmap();
			mBitmap.reconfigure(width,height, Bitmap.Config.ARGB_8888);
			mCanvas.drawBitmap(mBitmap,0,0,mPaint);
		}
	}
}