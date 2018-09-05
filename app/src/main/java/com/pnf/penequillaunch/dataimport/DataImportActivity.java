package com.pnf.penequillaunch.dataimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pnf.bt.lib.PNFDefine;
import com.pnf.bt.lib.PenDataClass;
import com.pnf.bt.lib.memory.DataImportFigure;
import com.pnf.bt.lib.memory.DataImportMakeFileThread;
import com.pnf.penequillaunch.test.MainDefine;
import com.example.daksh.clinmdequilsmartpenlaunch.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DataImportActivity extends Activity {
	final byte REQUEST_PREVIEW = 0x00;

	ListView fileListView;

	DataImportListViewAdapter diListViewAdapter;

	FrameLayout previewMainLayer;

	DataImportPreView preView;

	TextView previewNameTextView;
	Button previewModeBtn;

	ProgressDialog mProgress;
	String downLoadFileName;

	List<PenDataClass> downLoadPenDataList;
	List<PenDataClass> downLoadAccPenDataList;

	boolean isFileDownLoading = false;
	boolean isFileDeleting = false;
	int mDownLoadPageCount = 0;

	int deleteItemIndex = 0;

	boolean isHasFocus = false;

	@Override
	protected void onResume()
	{
		super.onResume();

		MainDefine.penController.SetRetObj(null);
		MainDefine.penController.SetRetObjForEnv(PenHandlerEnv);
		MainDefine.penController.SetRetObjForMsg(messageHandler);
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
	public void onBackPressed()
	{
		if(previewMainLayer.getVisibility() == View.VISIBLE){
			previewMainLayer.setVisibility(View.GONE);

			return;
		}

		setResult(RESULT_CANCELED ,null);
		finish();
		return;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		isHasFocus = hasFocus;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dataimportlistview);

		fileListView = (ListView) findViewById(R.id.fileListView);

		previewMainLayer = (FrameLayout) findViewById(R.id.previewMainLayer);

		preView = (DataImportPreView) findViewById(R.id.dataImportPreView);

		previewNameTextView = (TextView) findViewById(R.id.previewNameTextView);
		previewModeBtn = (Button) findViewById(R.id.previewModeBtn);

		initData();
	}

	void initData(){
		if(downLoadPenDataList == null){
			downLoadPenDataList = new ArrayList<PenDataClass>();
		}

		if(downLoadAccPenDataList == null){
			downLoadAccPenDataList = new ArrayList<PenDataClass>();
		}

		List<String> dataImportFileNameList = new ArrayList<String>();

		List<String> folderNameList = MainDefine.penController.getDIFolderName();
		if(folderNameList != null && folderNameList.size() > 0){
			for(String folderName : folderNameList){
				List<String> fileNameList = MainDefine.penController.getDIFileName(folderName);

				for(String fileName : fileNameList){
					dataImportFileNameList.add(folderName+fileName);
				}
			}
		}

		diListViewAdapter = new DataImportListViewAdapter(this ,dataImportFileNameList);
		fileListView.setAdapter(diListViewAdapter);
	}



	public void closeBtnClicked(View v){
		setResult(RESULT_CANCELED ,null);
		finish();
	}

	public void fileDownBtnClicked(View v){
		int tagIdx = (Integer) v.getTag();

		downLoadFileName = diListViewAdapter.getFileName(tagIdx);

		MainDefine.penController.OpenFileTobyte(downLoadFileName);

		if(downLoadPenDataList != null){
			downLoadPenDataList.clear();
		}

		if(downLoadAccPenDataList != null){
			downLoadAccPenDataList.clear();
		}

		mDownLoadPageCount = 0;
		previewModeBtn.setVisibility(View.GONE);
		previewNameTextView.setText(downLoadFileName);

		showProGress();
	}

	public void fileDeleteBtnClicked(View v){
		int tagIdx = (Integer) v.getTag();

		String deleteFileName = diListViewAdapter.getFileName(tagIdx);

		MainDefine.penController.DeleteFileTobyte(deleteFileName);

		deleteItemIndex = tagIdx;
		isFileDeleting = true;

		showProGress();
	}

	public void previewSaveBtnClicked(View v){
		String saveDir = getExternalStoragePathDir();

		try {
			if(preView.isAccMode){
				/*File saveFile = new File(saveDir ,"thunmnail_acc.png");
				FileOutputStream fOut = new FileOutputStream(saveFile ,false);

				preView.accBitmap.compress(CompressFormat.PNG, 100, fOut); //bm is the bitmap object
				fOut.flush();

				Toast.makeText(
						getApplicationContext(),
						"save acc image path : "+saveFile,
						Toast.LENGTH_LONG)
						.show();*/
				saveToInternalStorage(preView.accBitmap,"thunmnail_acc.png");
			}else{
				/*File saveFile = new File(saveDir ,"thunmnail.png");
				FileOutputStream fOut = new FileOutputStream(saveFile ,false);

				preView.mBitmap.compress(CompressFormat.PNG, 100, fOut); //bm is the bitmap object
				fOut.flush();

				Toast.makeText(
						getApplicationContext(),
						"save image path : "+saveFile,
						Toast.LENGTH_LONG)
						.show();*/
				saveToInternalStorage(preView.mBitmap,"thunmnail.png");
			}
		} catch (Exception e) {
			Log.e("error" ,"previewSaveBtnClicked Exception : "+e);
		}
	}

	public void previewChangeBtnClicked(View v){
		if(mDownLoadPageCount > 1){
			if(preView.isAccMode){
				preView.setAccMode(false);

				previewModeBtn.setText("1 / 2");
			}else{
				preView.setAccMode(true);

				previewModeBtn.setText("2 / 2");
			}

			preView.invalidate();
		}
	}

	public void previewCloseBtnClicked(View v){
		previewMainLayer.setVisibility(View.GONE);
	}

	void showProGress(){
		if(mProgress != null) return;

		mProgress = new ProgressDialog(DataImportActivity.this);
		mProgress.setMessage("file Downloading");
		mProgress.setCancelable(false);
		mProgress.show();
	}

	void stopProGress(){
		if(mProgress != null){
			mProgress.dismiss();
			mProgress = null;
		}
	}
	private String saveToInternalStorage(Bitmap bitmapImage, String downLoadFileName){
		ContextWrapper cw = new ContextWrapper(getApplicationContext());
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		// Create imageDir
		//File mypath=new File(directory,downLoadFileName+".png");
		File mypath = new File(getApplicationContext().getFilesDir().getAbsolutePath()+File.separator+"imageDir"+File.separator+downLoadFileName+".png");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			// Use the compress method on the BitMap object to write image to the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return directory.getAbsolutePath();
	}

	@SuppressLint("HandlerLeak")
	Handler PenHandlerEnv = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			onPenEnvEvent(msg.what ,msg.obj);
		}
	};


	@SuppressWarnings("unchecked")
	void onPenEnvEvent(int what, Object obj)
	{
		if(obj == null) return;

		if(what == PNFDefine.PEN_DI_DATA)
		{
			isFileDownLoading = true;

			downLoadPenDataList.addAll((List<PenDataClass>)obj);

			DataImportMakeFileThread makeThread = new DataImportMakeFileThread(
					MainDefine.penController ,
					MainDefine.iDisGetWidth ,
					MainDefine.iDisGetHeight ,
					downLoadPenDataList ,
					false);
			makeThread.setOnDataListener(new DataImportMakeFileThread.OnDataListener() {
				@Override
				public void onResultFigureList(Point _drawCanvasSize ,List<DataImportFigure> _figureList) {
					mDownLoadPageCount++;

					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(_drawCanvasSize.x ,_drawCanvasSize.y);
					params.gravity = Gravity.CENTER;

					preView.setLayoutParams(params);

					preView.setAccMode(false);
					preView.setDrawingSize(_drawCanvasSize.x ,_drawCanvasSize.y ,false);
					preView.drawFigure(_figureList ,false);

					preView.invalidate();
				}

				@Override
				public void onResultFail() {
					isFileDownLoading = false;
					stopProGress();
				}
			});
		}
		else if(what == PNFDefine.PEN_DI_ACC_DATA)
		{
			isFileDownLoading = true;

			downLoadAccPenDataList.addAll((List<PenDataClass>)obj);

			DataImportMakeFileThread makeThread = new DataImportMakeFileThread(
					MainDefine.penController ,
					MainDefine.iDisGetWidth ,
					MainDefine.iDisGetHeight ,
					downLoadAccPenDataList ,
					true);
			makeThread.setOnDataListener(new DataImportMakeFileThread.OnDataListener() {
				@Override
				public void onResultFigureList(Point _drawCanvasSize ,List<DataImportFigure> _figureList) {
					mDownLoadPageCount++;

					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(_drawCanvasSize.x ,_drawCanvasSize.y);
					params.gravity = Gravity.CENTER;

					preView.setLayoutParams(params);

					preView.setAccMode(true);
					preView.setDrawingSize(_drawCanvasSize.x ,_drawCanvasSize.y ,true);
					preView.drawFigure(_figureList ,true);

					preView.invalidate();
				}

				@Override
				public void onResultFail() {
					isFileDownLoading = false;
					stopProGress();
				}
			});
		}
		else if(what == PNFDefine.PNF_MSG_ENV_DATA){
			if(isFileDownLoading){
				previewMainLayer.setVisibility(View.VISIBLE);

				if(mDownLoadPageCount > 1){
					if(preView.isAccMode){
						previewModeBtn.setText("2 / 2");
					}else{
						previewModeBtn.setText("1 / 2");
					}
					previewModeBtn.setVisibility(View.VISIBLE);
				}

				isFileDownLoading = false;
				stopProGress();
			}
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

	void onMessageEvent(int what ,Object obj)
	{
		if(what == PNFDefine.PNF_MSG_FAIL_LISTENING){
			setResult(RESULT_CANCELED ,null);
			finish();
			return;
		}
		else if(what == PNFDefine.PNF_MSG_DI_OK){
			if(isFileDeleting){

				diListViewAdapter.removeItem(deleteItemIndex);
				diListViewAdapter.notifyDataSetChanged();

				isFileDeleting = false;
				stopProGress();
			}
		}
		else if(what == PNFDefine.PNF_MSG_DI_FAIL){
			if(isFileDownLoading || isFileDeleting){
				isFileDownLoading = false;
				isFileDeleting = false;

				stopProGress();
			}
		}
	}

	String getExternalStoragePathDir()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
}
