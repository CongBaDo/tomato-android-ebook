package com.tomato.communication;


import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;


public class owllab extends Application {

	private String companyMapState;
	public HashMap<String,String> authHM = new HashMap<String, String>();
	public HttpClient httpClient = new DefaultHttpClient();

	public String getCompanyMapState() {
		return companyMapState;
	}

	public void setCompanyMapState(String s) {
		companyMapState = s;
	}
	
	public ProgressDialog loadingDialog;

	public void startLoading(Context ctx) {
		loadingDialog = ProgressDialog.show(ctx, "Loading...", "Please wait...",
				false, true);
		Log.v("owllab", "startLoading" + ctx.toString());
	}

	public void endLoading() {
		Log.v("owllab", "endLoading");
		endLoader endLoader = new endLoader();
		Timer timer = new Timer(false);
		timer.schedule(endLoader, 1000);
	}

	class endLoader extends TimerTask {
		endLoader() {}
		@Override
		public void run() {
			loadingDialog.dismiss();
		}
	}
	

	// if (act!=null) ((owllab) act.getApplication()).startLoading(act);
	// if (act!=null) ((owllab) act.getApplication()).endLoading();
}
