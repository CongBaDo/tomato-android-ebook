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
	ArrayList<MyItem> arItem;
	
	final int MAX = 100;
	int intFromCount;
	String toGenreCount;
	String[] toGenreId = new String[MAX],
			toGenreTitle= new String[MAX],
			toGenreAuthor= new String[MAX],
			toGenreDescription= new String[MAX],
			toGenreImage = new String[MAX],
			toGenreGenre= new String[MAX],
			toGenreHit= new String[MAX],
			fixImage= new String[MAX],
			toGenreStock;

	@Override 
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		JptomatoLogoActivity.actList.add(this);
		setContentView(R.layout.books_custom_list);

		
		Intent fromGenre = getIntent();//"Genre.javaか"らインデントでもらったデータを引き出し
		
		toGenreId=fromGenre.getStringArrayExtra("fromGenreId");//本のID
		toGenreTitle=fromGenre.getStringArrayExtra("fromGenreTitle");//本の名前
		toGenreAuthor=fromGenre.getStringArrayExtra("fromGenreAuthor");//本の著者
		toGenreDescription=fromGenre.getStringArrayExtra("fromGenreDescription");//本の説明
		toGenreImage=fromGenre.getStringArrayExtra("fromGenreImage");//本のイメージの住所
		toGenreGenre=fromGenre.getStringArrayExtra("fromGenreGenre");//本のジャンル
		toGenreCount=fromGenre.getStringExtra("fromGenreCount");//サーバーからもらった本の数
		toGenreHit=fromGenre.getStringArrayExtra("fromGenreHit");//ユーザーがダウンロードした数。
		toGenreStock=fromGenre.getStringArrayExtra("fromGenreStock");//本がサーバーへ登録された日

		intFromCount = Integer.valueOf(toGenreCount);

		//各本のイメージ住所をサーバーに要請する形式で変更
		for(int i=0;i<intFromCount;i++)
		{

			fixImage[i]= "http://"+toGenreImage[i];
			fixImage[i]= fixImage[i].replaceAll("@amp;", "&");
		}

		arItem = new  ArrayList<MyItem>();
		MyItem[] mi = new MyItem[MAX];

		//リスト画面に本の目録追加
		for(int i =0;i<intFromCount;i++)
		{
			mi[i]= new MyItem(fixImage[i],toGenreTitle[i]);
			arItem.add(mi[i]);
		}
		
		MyListAdapter MyAdapter = new MyListAdapter(this,R.layout.books,arItem);
		ListView MyList;
		MyList =(ListView)findViewById(R.id.listTest);
		MyList.setAdapter(MyAdapter);
		
		//各目録の中で、一つを選んだ時、発生するイベントを処理
		MyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) 
			{
				// TODO Auto-generated method stub
				int currentPosition = position;
				Intent Intent = new Intent(Books.this,Info.class);
			
				//インデントで本の詳細情報画面に遷移する時、データを持って、遷移する。
				Intent.putExtra("toBookId", toGenreId[currentPosition]);//目録から選択した本のID
				Intent.putExtra("toBookTitle", toGenreTitle[currentPosition]);//目録から選択した本の名前
				Intent.putExtra("toBookAuthor", toGenreAuthor[currentPosition]);//目録から選択した本の著者
				Intent.putExtra("toBookImage", toGenreImage[currentPosition]);//目録から選択した本のイメージ住所
				Intent.putExtra("toBookGenre", toGenreGenre[currentPosition]);//目録から選択した本のジャンル
				Intent.putExtra("toBookDescription", toGenreDescription[currentPosition]);//目録から選択した本の説明
				
				startActivity(Intent);//インデント開始
			}
		});
	}
}