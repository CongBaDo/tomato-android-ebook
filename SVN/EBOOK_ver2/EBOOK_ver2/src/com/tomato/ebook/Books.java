package com.tomato.ebook;

import java.util.ArrayList;

import com.tomato.ebook.R;
import com.tomato.ebook.Book_ListViewUtility.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Books extends Activity {
	/** Called when the activity is first created. */
	final int MAX = 200;
	ArrayList<MyItem> arItem;
	String[] toGenreId = new String[MAX],toGenreTitle= new String[MAX],toGenreAuthor= new String[MAX],toGenreDescription= new String[MAX],
	toGenreImage= new String[MAX],toGenreGenre= new String[MAX],toGenreHit= new String[MAX],toGenreStock,fixImage= new String[MAX];

	String toGenreCount;
	int intFromCount;

	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.books_custom_list);

		Intent fromGenre = getIntent();

		toGenreId=fromGenre.getStringArrayExtra("fromGenreId");
		toGenreTitle=fromGenre.getStringArrayExtra("fromGenreTitle");
		toGenreAuthor=fromGenre.getStringArrayExtra("fromGenreAuthor");
		toGenreDescription=fromGenre.getStringArrayExtra("fromGenreDescription");
		toGenreImage=fromGenre.getStringArrayExtra("fromGenreImage");
		toGenreGenre=fromGenre.getStringArrayExtra("fromGenreGenre");
		toGenreCount=fromGenre.getStringExtra("fromGenreCount");
		toGenreHit=fromGenre.getStringArrayExtra("fromGenreHit");
		toGenreStock=fromGenre.getStringArrayExtra("fromGenreStock");

		intFromCount = Integer.valueOf(toGenreCount);

		for(int i=0;i<intFromCount;i++)
		{

			fixImage[i]= "http://"+toGenreImage[i];
			fixImage[i]= fixImage[i].replaceAll("@amp;", "&");
		}

		arItem = new  ArrayList<MyItem>();
		MyItem[] mi = new MyItem[MAX];

		for(int i =0;i<intFromCount;i++)
		{
			mi[i]= new MyItem(fixImage[i],toGenreTitle[i]);
			arItem.add(mi[i]);
		}
		MyListAdapter MyAdapter = new MyListAdapter(this,R.layout.books,arItem);
		ListView MyList;
		MyList =(ListView)findViewById(R.id.listTest);
		MyList.setAdapter(MyAdapter);
		MyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) 
			{
				// TODO Auto-generated method stub
				int currentPosition = position;
				Intent Intent = new Intent(Books.this,Info.class);
				Log.e("ItentinBooks","start");
				Intent.putExtra("toBookId", toGenreId[currentPosition]);
				Intent.putExtra("toBookTitle", toGenreTitle[currentPosition]);
				Intent.putExtra("toBookAuthor", toGenreAuthor[currentPosition]);
				Intent.putExtra("toBookImage", toGenreImage[currentPosition]);
				Intent.putExtra("toBookGenre", toGenreGenre[currentPosition]);
				Intent.putExtra("toBookDescription", toGenreDescription[currentPosition]);
				Log.e("ItentinBooks","goIntent");
				startActivity(Intent);
			}
		});
	}

}