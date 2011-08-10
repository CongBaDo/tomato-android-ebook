package com.tomato.ebook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class Books_TabActivity extends android.app.TabActivity {
	final static int MAX = 100;
	TabHost tabHost;
	String[] fromGenreId = new String[MAX],fromGenreTitle= new String[MAX],fromGenreAuthor= new String[MAX],
	fromGenreDescription= new String[MAX],fromGenreImage= new String[MAX],fromGenreGenre= new String[MAX],fromGenreHit= new String[MAX],fromGenreStock= new String[MAX];
	
	String fromGenreCount;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JptomatoLogoActivity.actList.add(this);
        Log.e("inBooks_TabActivity", "savevalue");
        Intent bookIntent = getIntent();
        
        fromGenreId=bookIntent.getStringArrayExtra("book_id");
		fromGenreTitle=bookIntent.getStringArrayExtra("book_title");
		fromGenreAuthor=bookIntent.getStringArrayExtra("book_author");
		fromGenreDescription=bookIntent.getStringArrayExtra("book_description");
		fromGenreImage=bookIntent.getStringArrayExtra("book_image");
		fromGenreGenre=bookIntent.getStringArrayExtra("book_genre");
		fromGenreCount=bookIntent.getStringExtra("book_count");
		fromGenreHit=bookIntent.getStringArrayExtra("book_hit");
		fromGenreStock=bookIntent.getStringArrayExtra("book_stock");
		
        Intent toBook = new Intent(this,Books.class);
        toBook.putExtra("fromGenreId", fromGenreId);
        toBook.putExtra("fromGenreTitle", fromGenreTitle);
        toBook.putExtra("fromGenreAuthor", fromGenreAuthor);
        toBook.putExtra("fromGenreDescription", fromGenreDescription);
        toBook.putExtra("fromGenreImage", fromGenreImage);
        toBook.putExtra("fromGenreGenre", fromGenreGenre);
        toBook.putExtra("fromGenreCount", fromGenreCount);
        toBook.putExtra("fromGenreHit", fromGenreHit);
        toBook.putExtra("fromGenreStock", fromGenreStock);
		
        tabHost = getTabHost();
        
        tabHost.addTab(tabHost.newTabSpec("tab1")
        		.setIndicator("LogOut")
        		.setContent(new Intent(this,Join.class)));
        
        tabHost.addTab(tabHost.newTabSpec("tab2")
        		.setIndicator("Books")
        		.setContent(toBook));
        
        tabHost.addTab(tabHost.newTabSpec("tab3")
        		.setIndicator("My Library")
        		.setContent(new Intent(this,Login.class)));
     
        tabHost.setCurrentTab(1);
      
        Log.e("Book_TabActivity","after");
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {			
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
			switch(tabHost.getCurrentTab())
			{
				case 0:
				{
					tabHost.clearAllTabs();
					Intent Intent = new Intent(Books_TabActivity.this,EbookMain.class);
					startActivity(Intent);
					break;
				}
				
				case 2:
				{
					tabHost.clearAllTabs();
					Intent Intent = new Intent(Books_TabActivity.this,MyLibrary.class);
					Intent.putExtra("State", "OK");
					startActivity(Intent);
					break;
				}
			}
			
			}
		});
	}
	
}
