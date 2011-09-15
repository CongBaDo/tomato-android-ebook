package com.tomato.ebook;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TabHost;

public class Main_re extends TabActivity {
	/** Called when the activity is first created. */
	TabHost tab;
	String state = null;

	ConnectivityManager cManager;
	NetworkInfo mobile;
	NetworkInfo wifi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		JptomatoLogoActivity.actList.add(this);
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.tabs);

		tab = getTabHost();

		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		inflater.inflate(R.layout.main_re, tab.getTabContentView(), true);
		
		Intent getFromLogin = getIntent();
		state = getFromLogin.getStringExtra("State");

		Intent myLib = new Intent(this, MyLibrary.class);

		tab.addTab(tab.newTabSpec("Library").setIndicator("My Library", getResources().getDrawable(R.drawable.icon))
				.setContent(myLib));

		tab.addTab(tab.newTabSpec("Store").setIndicator("Book Store", getResources().getDrawable(R.drawable.icon))
				.setContent(R.id.store));

		tab.addTab(tab.newTabSpec("Logout").setIndicator("Exit", getResources().getDrawable(R.drawable.icon))
				.setContent(new Intent(this, Login_re.class)));

		tab.setCurrentTab(0);
		
		Log.e("Book_TabActivity", "after");
		tab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				switch (tab.getCurrentTab()) {
				case 1: {
					tab.clearAllTabs();
					if (//(!mobile.isConnected() && !wifi.isConnected())
							state.equals("OK")) {
						Intent Intent = new Intent(Main_re.this,
								Genre_TabActivity.class);
						Intent.putExtra("State", "OK");
						startActivity(Intent);
						break;
					} else {
						new AlertDialog.Builder(Main_re.this)
								.setTitle("お知らせ")
								.setMessage("サーバーへ接続して下さい。")
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												Intent intent = new Intent(
														Main_re.this,
														Login_re.class);
												startActivity(intent);
											}
										}).show();
						break;
					}
				}
				case 2: {
					tab.clearAllTabs();
					Intent Intent = new Intent(Main_re.this, Login_re.class);
					startActivity(Intent);
					break;
				}
				}
			}
		});
	}
}