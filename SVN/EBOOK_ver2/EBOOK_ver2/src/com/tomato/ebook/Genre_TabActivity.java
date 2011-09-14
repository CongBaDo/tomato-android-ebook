package com.tomato.ebook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Genre_TabActivity extends android.app.TabActivity {
	TabHost tabHost;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JptomatoLogoActivity.actList.add(this);
		tabHost = getTabHost();
		
		Intent myLib = new Intent(this,MyLibrary.class);
		myLib.putExtra("State", "not");
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("My Library")
				.setContent(myLib));				
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Book Store")
				.setContent(new Intent(this, Genre.class)));
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Logout")
				.setContent(new Intent(this, Login_re.class)));
		
		tabHost.setCurrentTab(1);
		
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				switch (tabHost.getCurrentTab()) {
				case 2: {
					tabHost.clearAllTabs();
					Intent Intent = new Intent(Genre_TabActivity.this,
							Login_re.class);
					startActivity(Intent);
					break;
				}
				}
			}
		});
	}
}
