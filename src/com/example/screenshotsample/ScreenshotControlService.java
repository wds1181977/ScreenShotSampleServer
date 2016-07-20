package com.example.screenshotsample;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class ScreenshotControlService extends Service {
	
	private static final String BIND_SERVICE_ACTION = "com.example.screenshotsample.start";
	private static final String TAG = "ScreenshotControlService";
	 ScreenshotUtil mScreenshotUtil;
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			
			default:
				break;
                   }
               
		}
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "I have started hehe---");
		return Service.START_STICKY;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate........");
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		mScreenshotUtil=	new ScreenshotUtil(this,mHandler);
		

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy........");
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG,"bind action is "+ intent.getAction());
		if (BIND_SERVICE_ACTION.equals(intent.getAction())) {
			return mSBinder;
		}
		return null;
	}
	
	public IScreenshotControl.Stub mSBinder = new IScreenshotControl.Stub() {

		@Override
		public String takeScreenshot() throws RemoteException {
			// TODO Auto-generated method stub
			return mScreenshotUtil.startScreenShot();
		}


	};	
}
