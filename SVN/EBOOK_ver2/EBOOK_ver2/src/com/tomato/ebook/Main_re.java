package com.tomato.ebook;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TabHost;

public class Main_re extends TabActivity {
	/** Called when the activity is first created. */

	TabHost tab;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		tab = getTabHost();

		LayoutInflater inflater = LayoutInflater.from(this);
		inflater.inflate(R.layout.main_re, tab.getTabContentView(), true);
		
		Intent myLib = new Intent(this,MyLibrary.class);
		myLib.putExtra("State", "not");
		
		tab.addTab(tab.newTabSpec("Library").setIndicator("My Library")
				.setContent(myLib));
		tab.addTab(tab.newTabSpec("Store").setIndicator("Book Store")
				.setContent(new Intent(this,Genre.class)));
		tab.addTab(tab.newTabSpec("Logout").setIndicator("Logout")
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
						Intent Intent = new Intent(Main_re.this, Genre_TabActivity.class);
						Intent.putExtra("State", "OK");
						startActivity(Intent);
						break;
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