package com.tomato.ebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Info extends Activity {

	String toBookId=null,toBookTitle=null,toBookAuthor=null,toBookImage=null,toBookGenre=null,
	       toBookDescription=null,toFileUser=null,fixImage=null,userId=null;
	ImageView BookCorver;
	TextView BookName,BookAuthor,BookGenre,Story;
	Button DownBtn,OKBtn;
	File userText;
	FileReader idCheck;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JptomatoLogoActivity.actList.add(this);
	    setContentView(R.layout.info);
	    Intent toBook = getIntent();
	    Log.e("genIntentinInfo","start");
	    toBookId = toBook.getStringExtra("toBookId");
	    Log.e("valueIntoBookId",toBookId);
	    toBookTitle = toBook.getStringExtra("toBookTitle");
	    toBookAuthor = toBook.getStringExtra("toBookAuthor");
	    toBookImage = toBook.getStringExtra("toBookImage");
	    Log.e("ImageValueInInfo",toBookImage);
	    toBookGenre = toBook.getStringExtra("toBookGenre");
	    toBookDescription = toBook.getStringExtra("toBookDescription");
		  
	    
	    fixImage = "http://"+toBookImage;
		fixImage= fixImage.replaceAll("@amp;", "&");
	    
	    Log.e("setValueInInfo","title");
	    BookCorver = (ImageView)findViewById(R.id.Info_BookCover);
	    Drawable draw = loadDrawable(fixImage);
	    BookCorver.setImageDrawable(draw);
	    
	    BookName = (TextView)findViewById(R.id.Info_BookName);
	    BookName.setText(toBookTitle);
	    
	    BookAuthor = (TextView)findViewById(R.id.Info_AuthorName);
	    BookAuthor.setText(toBookAuthor);
	    
	    BookGenre = (TextView)findViewById(R.id.Info_GenreName);
	    BookGenre.setText(toBookGenre);
	    
	    Story = (TextView)findViewById(R.id.Info_Story);
	    Story.setText(toBookDescription);
	    
	   
	    
	    
	    DownBtn = (Button)findViewById(R.id.Info_DownBtn);
	    DownBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				userText = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
				try {
					idCheck = new FileReader(userText);
					BufferedReader Br = new BufferedReader(idCheck);
					for(int i=0;i<2;i++)
					{
						userId = Br.readLine();
					}
					Br.close();
					idCheck.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("ValueId",userId);
				Intent Intent = new Intent(Info.this,Down.class);
				Intent.putExtra("fromInfoImage", fixImage);
				Intent.putExtra("fromInfoBookId", toBookId);
				Intent.putExtra("fromFileUserId", userId);
				
				startActivity(Intent);
			}
		});
	    
	    OKBtn = (Button)findViewById(R.id.Info_OKBtn);
	    OKBtn.setOnClickListener(new View.OnClickListener() 
	    {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	    
	}
	public Drawable loadDrawable(String urlStr)
	{
		Drawable drawable = null;
		try
		{
			URL url = new URL(urlStr);
			InputStream is = url.openStream();
			drawable = Drawable.createFromStream(is, "none");
	
			
		}
		catch(Exception e)
		{
			Log.e("URL","error,in load Drawable\n"+e.toString());
		}
		return drawable;
		
	}
}
