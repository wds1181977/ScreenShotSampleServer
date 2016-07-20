package com.example.screenshotsample;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

/**
 * @author 老司机
 *
 */
public class ScreenshotUtil {

	private File mScreenshotDir;
	
	private String mImageFileName;
	private String mImageFilePath;
	private static String SCREENSHOTS_DIR_NAME = "Screenshots";
	private Context mContext;
	private Handler mHandler;

	public ScreenshotUtil(Context context, Handler h) {
		mContext = context;
		mHandler = h;
		// 截屏保存位置
		mScreenshotDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				SCREENSHOTS_DIR_NAME);
		mImageFilePath = mScreenshotDir.getAbsolutePath();
	}

	public String startScreenShot() {

		mHandler.post(mScreenshotRunnable);

		return mImageFilePath;
	}

	private final Runnable mScreenshotRunnable = new Runnable() {
		@Override
		public void run() {

			// 这里可以加上延时，等待用户跳转到其他页面

			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			takeScreenShot();
		}
	};

	final Object mScreenshotLock = new Object();
	ServiceConnection mScreenshotConnection = null;

	final Runnable mScreenshotTimeout = new Runnable() {
		@Override
		public void run() {
			synchronized (mScreenshotLock) {
				if (mScreenshotConnection != null) {
					mContext.unbindService(mScreenshotConnection);
					mScreenshotConnection = null;
				}
			}
		}
	};

	private void takeScreenShot() {

		synchronized (mScreenshotLock) {
			if (mScreenshotConnection != null) {
				return;
			}
			ComponentName cn = new ComponentName("com.android.systemui",
					"com.android.systemui.screenshot.TakeScreenshotService");
			Intent intent = new Intent();
			intent.setComponent(cn);
			ServiceConnection conn = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName name,
						IBinder service) {
					synchronized (mScreenshotLock) {
						if (mScreenshotConnection != this) {
							return;
						}
						Messenger messenger = new Messenger(service);
						Message msg = Message.obtain(null, 1);
						final ServiceConnection myConn = this;
						Handler h = new Handler(mHandler.getLooper()) {
							@Override
							public void handleMessage(Message msg) {
								synchronized (mScreenshotLock) {
									if (mScreenshotConnection == myConn) {
										mContext.unbindService(mScreenshotConnection);
										mScreenshotConnection = null;
										mHandler.removeCallbacks(mScreenshotTimeout);
									}
								}
							}
						};
						msg.replyTo = new Messenger(h);
						msg.arg1 = msg.arg2 = 0;
						// if (mStatusBar != null && mStatusBar.isVisibleLw())
						// msg.arg1 = 1;
						// if (mNavigationBar != null &&
						// mNavigationBar.isVisibleLw())
						// msg.arg2 = 1;
						try {
							messenger.send(msg);
						} catch (RemoteException e) {
						}
					}
				}

				@Override
				public void onServiceDisconnected(ComponentName name) {
				}
			};
			if (mContext.bindServiceAsUser(intent, conn,
					Context.BIND_AUTO_CREATE, UserHandle.CURRENT)) {
				mScreenshotConnection = conn;
				mHandler.postDelayed(mScreenshotTimeout, 10000);
			}
		}
	}
}
