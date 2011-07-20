package com.tomato.ebook;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Genre_TabActivity extends android.app.TabActivity {
	TabHost tabHost;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabHost = getTabHost();
        
        tabHost.addTab(tabHost.newTabSpec("tab1")
        		.setIndicator("LogOut")
        		.setContent(new Intent(this,Join.class)));
        tabHost.addTab(tabHost.newTabSpec("tab2")
        		.setIndicator("Genre")
        		.setContent(new Intent(this,Genre.class)));
        tabHost.addTab(tabHost.newTabSpec("tab3")
        		.setIndicator("My Library")
        		.setContent(new Intent(this,Login.class)));
        tabHost.setCurrentTab(1);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
			switch(tabHost.getCurrentTab())
			{
				case 0:
				{
					tabHost.clearAllTabs();
					Intent Intent = new Intent(Genre_TabActivity.this,EbookMain.class);
					startActivity(Intent);
					break;
				}
				
				case 2:
				{
					tabHost.clearAllTabs();
					Intent Intent = new Intent(Genre_TabActivity.this,MyLibrary.class);
					startActivity(Intent);
					break;
				}
			}
			
			}
		});
	}
}
