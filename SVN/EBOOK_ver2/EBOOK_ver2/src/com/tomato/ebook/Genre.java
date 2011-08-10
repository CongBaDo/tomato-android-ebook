package com.tomato.ebook;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.tomato.communication.Util;
import com.tomato.communication.cmsHTTP;
import com.tomato.ebook.R;
import com.tomato.ebook.Genre_ListViewUtility.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Genre extends Activity {
	/** Called when the activity is first created. */
	static final int MAX = 100;
	ArrayList<MyItem> GarItem;
	String ids;
	String reId, reCount, reDate,tmpData;
	HashMap<String, String> hm;
	Util genutil = new Util();
	ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
	String[] resId = new String[MAX], resTitle = new String[MAX], resAuthor = new String[MAX], resDescription = new String[MAX], resImage = new String[MAX],
    resHit = new String[MAX],resStock = new String[MAX], resGenre = new String[MAX];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JptomatoLogoActivity.actList.add(this);
		setContentView(R.layout.genre_custom_list);

		GarItem = new ArrayList<MyItem>();
		MyItem mi;

		mi = new MyItem("Best Seller", R.drawable.enter);
		GarItem.add(mi);
		mi = new MyItem("New Boolks", R.drawable.enter);
		GarItem.add(mi);

		MyListAdapter MyAdapter = new MyListAdapter(this, R.layout.genre,
				GarItem);
		ListView MyList;
		MyList = (ListView) findViewById(R.id.Genre_List);
		MyList.setAdapter(MyAdapter);
		MyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
		
				switch (position) {

				case 0: 
				{
					String theUrl = "http://ebookserverhjy5.appspot.com/genre.jsp";
					String count = "count";
					httpParams.add(new BasicNameValuePair("condition", count));
					cmsHTTP cmsHttp = new cmsHTTP();
					// cmsHttp.encoding = encoding;
					cmsHttp.act = Genre.this;
					Log.e("Genre","in_SendPost");
					tmpData = cmsHttp.sendPost(theUrl, httpParams);
					Log.e("Genre","out_SendPost");					
					if (tmpData == null) 
					{
						return;
					} 
					else 
					{
						hm = genutil.xml2HashMap(tmpData, cmsHttp.encoding);
						addResult(hm,0);
						httpParams.clear();
						theUrl = null;
						tmpData=null;
					}
					Intent Intent = new Intent(Genre.this,Books_TabActivity.class);
					Intent.putExtra("book_id", resId);
					Intent.putExtra("book_title", resTitle);
					Intent.putExtra("book_author", resAuthor);
					Intent.putExtra("book_description", resDescription);
					Intent.putExtra("book_image", resImage);
					Intent.putExtra("book_genre", resGenre);
					Intent.putExtra("book_hit", resHit);
					Intent.putExtra("book_stock", resStock);
					Intent.putExtra("book_count", hm.get("count"));
					Log.e("countValueInGenre", hm.get("count"));
					count = null;
					startActivity(Intent);
					return;
				}

				case 1: 
				{
					String theUrl = "http://ebookserverhjy5.appspot.com/genre.jsp";
					String date = "date";
					httpParams.add(new BasicNameValuePair("condition", date));
					cmsHTTP cmsHttp = new cmsHTTP();
					// cmsHttp.encoding = encoding;
					cmsHttp.act = Genre.this;
					tmpData = cmsHttp.sendPost(theUrl, httpParams);
					if (tmpData == null) 
					{
						return;
					}
					else 
					{
						hm = genutil.xml2HashMap(tmpData, cmsHttp.encoding);
						addResult(hm,1);
						httpParams.clear();
						theUrl = null;
						tmpData = null;
					}
						Intent Intent = new Intent(Genre.this,Books_TabActivity.class);
						Intent.putExtra("book_id", resId);
						Intent.putExtra("book_title", resTitle);
						Intent.putExtra("book_author", resAuthor);
						Intent.putExtra("book_description", resDescription);
						Intent.putExtra("book_image", resImage);
						Intent.putExtra("book_genre", resGenre);
						Intent.putExtra("book_hit", resHit);
						Intent.putExtra("book_stock", resStock);
						Intent.putExtra("book_count", hm.get("count"));
						
						date = null;
						startActivity(Intent);
					}
					
					return;
				}
			}
		});
	}

	public void addResult(HashMap<String, String> hm,int caseResult) {
		int count = Integer.valueOf(hm.get("count"));
		String[] id = new String[MAX], title = new String[MAX], author = new String[MAX], description = new String[MAX], image = new String[MAX], genre = new String[MAX],
		stock = new String[MAX], hit = new String[MAX];	
		Log.e("result_inStrFor", "going");
		for (int i = 0; i < count; i++) 
		{
			id[i] = ("id[" + i + "]");
			Log.e("id", id[i].toString());
			genre[i] = "genre[" + i + "]";
			title[i] = "title[" + i + "]";
			author[i] = "author[" + i + "]";
			description[i] = "description[" + i + "]";
			image[i] = "imageurl[" + i + "]";
			hit[i]=String.valueOf("hit["+i+"]");
			stock[i] = "stock[" + i + "]";
		}
			for (int i = 0; i < count; i++) 
		{
			Log.e("count_result", String.valueOf(i));
			resId[i] = hm.get(id[i]);
			resGenre[i] = hm.get(genre[i]);
			resTitle[i] = hm.get(title[i]);
			resAuthor[i] = hm.get(author[i]);
			resDescription[i] = hm.get(description[i]);
			resImage[i] = hm.get(image[i]);
			resHit[i]=hm.get(hit[i]);
			resStock[i] = hm.get(stock[i]);
		}
			
	}
}