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
		
		tabHost.addTab(tabHost.newTabSpec("My Library").setIndicator("", getResources().getDrawable(R.drawable.mylib))
				.setContent(myLib));				
		tabHost.addTab(tabHost.newTabSpec("Book Store").setIndicator("", getResources().getDrawable(R.drawable.bookstore))
				.setContent(new Intent(this, Genre.class)));
		tabHost.addTab(tabHost.newTabSpec("Exit").setIndicator("", getResources().getDrawable(R.drawable.exit))
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
