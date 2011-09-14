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
	ArrayList<MyItem> GarItem;
	ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
	HashMap<String, String> hm;
	
	static final int MAX = 100;
	String reId, reCount, reDate,tmpData,ids;
	String[] resId = new String[MAX], 
			 resTitle = new String[MAX],
			 resAuthor = new String[MAX],
			 resDescription = new String[MAX],
			 resImage = new String[MAX],
			 resHit = new String[MAX],
			 resStock = new String[MAX], 
			 resGenre = new String[MAX];
	
	Util genutil = new Util();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JptomatoLogoActivity.actList.add(this);//"プログラムの終了"のため、Activityを追加
		setContentView(R.layout.genre_custom_list);

		GarItem = new ArrayList<MyItem>();
		MyItem mi;
		//リスト画面に追加
		mi = new MyItem("Best Seller", R.drawable.click);
		GarItem.add(mi);
		mi = new MyItem("New Books", R.drawable.click);
		GarItem.add(mi);

		MyListAdapter MyAdapter = new MyListAdapter(this, R.layout.genre,GarItem);
		ListView MyList;
		MyList = (ListView) findViewById(R.id.Genre_List);
		MyList.setAdapter(MyAdapter);
		//各目録の中で、一つを選んだ時、発生するイベントを処理
		MyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				// TODO Auto-generated method stub
		
				switch (position) {
				//"Best Seller"を選択した場合
				case 0: 
				{
					//ジャンル部分の表示を担当するサーバーの住所
					String theUrl = "http://ebookserverhjy5.appspot.com/genre.jsp";
					String count = "count";
					
					//"Best Seller"の要請コード`count`を要請目録に追加			
					httpParams.add(new BasicNameValuePair("condition", count));
					cmsHTTP cmsHttp = new cmsHTTP();//接続準備
					// cmsHttp.encoding = encoding;
					cmsHttp.act = Genre.this;
					tmpData = cmsHttp.sendPost(theUrl, httpParams);//サーバーへデータを要請
					
					//サーバーから戻り値がない場合
					if (tmpData == null) 
					{
						return;
					}//if end 
					
					//戻り値が有る場合
					else 
					{
						//サーバーへ要請したデータを保存
						hm = genutil.xml2HashMap(tmpData, cmsHttp.encoding);
						
						addResult(hm);//保存したデータを各変数に保存
						httpParams.clear();
						theUrl = null;
						tmpData=null;
					}
					Intent Intent = new Intent(Genre.this,Books_TabActivity.class);
					
					//インデントで本の選択する画面に遷移する時、データを持って、遷移する。
					Intent.putExtra("book_id", resId);//本のID
					Intent.putExtra("book_title", resTitle);//本の名前
					Intent.putExtra("book_author", resAuthor);//本の著者
					Intent.putExtra("book_description", resDescription);//本の説明
					Intent.putExtra("book_image", resImage);//本のイメージの住所
					Intent.putExtra("book_genre", resGenre);//本のジャンル
					Intent.putExtra("book_hit", resHit);//ユーザーがダウンロードした数
					Intent.putExtra("book_stock", resStock);//本がサーバーへ登録された日
					Intent.putExtra("book_count", hm.get("count"));//サーバーからもらった本の数
					
					count = null;
					
					startActivity(Intent);//インデント開始
					return;
				}

				//"New Books"を選択した場合
				case 1: 
				{
					String theUrl = "http://ebookserverhjy5.appspot.com/genre.jsp";
					String date = "date";
					//"New Books"の要請コード`date`を要請目録に追加			
					httpParams.add(new BasicNameValuePair("condition", date));
					cmsHTTP cmsHttp = new cmsHTTP();;//接続準備
					// cmsHttp.encoding = encoding;
					cmsHttp.act = Genre.this;
					
					tmpData = cmsHttp.sendPost(theUrl, httpParams);//サーバーへデータを要請
					
					//サーバーから戻り値がない場合
					if (tmpData == null) 
					{
						return;
					}//if end
					
					//戻り値が有る場合
					else 
					{
						//サーバーへ要請したデータを保存
						hm = genutil.xml2HashMap(tmpData, cmsHttp.encoding);
						
						addResult(hm);//保存したデータを各変数に保存
						httpParams.clear();
						theUrl = null;
						tmpData = null;
					}
						Intent Intent = new Intent(Genre.this,Books_TabActivity.class);
					
						Intent.putExtra("book_id", resId);//本のID
						Intent.putExtra("book_title", resTitle);//本の名前
						Intent.putExtra("book_author", resAuthor);//本の著者
						Intent.putExtra("book_description", resDescription);//本の説明
						Intent.putExtra("book_image", resImage);//本のイメージの住所
						Intent.putExtra("book_genre", resGenre);//本のジャンル
						Intent.putExtra("book_hit", resHit);//ユーザーがダウンロードした数
						Intent.putExtra("book_stock", resStock);//本がサーバーへ登録された日
						Intent.putExtra("book_count", hm.get("count"));//サーバーからもらった本の数
						
						date = null;
						startActivity(Intent);//インデント開始
					}
					
					return;
				}
			}
		});
	}

	//////////////////////////////////////////////////
	//
	// サーバーへ要請したデータを各変数に保存
	//
	//////////////////////////////////////////////////
	public void addResult(HashMap<String, String> hm) {
		int count = Integer.valueOf(hm.get("count"));
		String[] id = new String[MAX], 
				title = new String[MAX], 
				author = new String[MAX], 
				description = new String[MAX], 
				image = new String[MAX], 
				genre = new String[MAX],
				stock = new String[MAX], 
				hit = new String[MAX];	

		//サーバーへ要請したデータを保存する変数"hm"からデータを引き出す構文を作る。
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
		}//for i end

		//サーバーへ要請したデータを保存する変数"hm"からデータを引き出して各データ別に保存。
		for (int j = 0; j < count; j++) 
		{
			resId[j] = hm.get(id[j]);
			resGenre[j] = hm.get(genre[j]);
			resTitle[j] = hm.get(title[j]);
			resAuthor[j] = hm.get(author[j]);
			resDescription[j] = hm.get(description[j]);
			resImage[j] = hm.get(image[j]);
			resHit[j]=hm.get(hit[j]);
			resStock[j] = hm.get(stock[j]);
		}//for j end
			
	}
}