package com.tomato.ebook;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class EbookMain extends Activity {
	/** Called when the activity is first created. */
	File userData;
	ConnectivityManager cManager;    
	NetworkInfo mobile;    
	NetworkInfo wifi; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);    
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);    
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);   
		if(!mobile.isConnected()&&!wifi.isConnected())
		{
			new AlertDialog.Builder(EbookMain.this)
			.setTitle("Notification")
			.setMessage("ネットがオフーラインです。\n書斎に移動します。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent dirIntent = new Intent(EbookMain.this,MyLibrary.class);
					startActivity(dirIntent);
				}
			})
			.show();
		}
		else
		{
			userData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
			if(!userData.exists()||userData.length()==0||!userData.canRead())
			{
				Intent intent=new Intent(this, Login.class);
				startActivity(intent);
			}

			else
			{
				new AlertDialog.Builder(EbookMain.this)
				.setTitle("Notification")
				.setMessage("書斎に移動します。")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent dirIntent = new Intent(EbookMain.this,MyLibrary.class);
						startActivity(dirIntent);
					}
				})
				.show();
			}
		}
		return super.onTouchEvent(event);
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			new AlertDialog.Builder(EbookMain.this)
			.setTitle("Notification")
			.setMessage("プログラムを終了しますか。")
			.setNeutralButton("戻る", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub


				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					close();
				}
			})
			.show();


		}
		return false;

	}
	public void close()  
	{  

		finish();  

		int nSDKVersion = Integer.parseInt(Build.VERSION.SDK);  

		if(nSDKVersion < 8)    //2.1이하  

		{  

			ActivityManager actMng = (ActivityManager)getSystemService(ACTIVITY_SERVICE);  

			actMng.restartPackage(getPackageName());  

		}  

		else  

		{  

			new Thread(new Runnable() {  

				public void run() {  

					ActivityManager actMng = (ActivityManager)getSystemService(ACTIVITY_SERVICE);  

					String strProcessName = getApplicationInfo().processName;  

					while(true)  

					{  

						List<RunningAppProcessInfo> list = actMng.getRunningAppProcesses();  

						for(RunningAppProcessInfo rap : list)  

						{  

							if(rap.processName.equals(strProcessName))  

							{  

								if(rap.importance >= RunningAppProcessInfo.IMPORTANCE_BACKGROUND)  

									actMng.restartPackage(getPackageName());  

								Thread.yield();  

								break;  

							}  

						}  

					}  

				}  

			}, "Process Killer").start();  

		}  
		System.exit(0);
	}  
}
