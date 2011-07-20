package com.tomato.ebook;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;

public class EbookMain extends Activity {
	/** Called when the activity is first created. */
	File userData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
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
		return super.onTouchEvent(event);
	}
}