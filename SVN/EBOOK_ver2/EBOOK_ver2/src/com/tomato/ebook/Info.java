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

	File userText;
	FileReader idCheck;
	
	String toBookId=null,
			toBookTitle=null,
			toBookAuthor=null,
			toBookImage=null,
			toBookGenre=null,
	       toBookDescription=null,
	       toFileUser=null,
	       fixImage=null,
	       userId=null;
	
	ImageView BookCorver;
	TextView BookName,BookAuthor,BookGenre,Story;
	Button DownBtn,OKBtn;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JptomatoLogoActivity.actList.add(this);//"プログラムの終了"のため、Activityを追加
	    setContentView(R.layout.info);
	    
	    Intent toBook = getIntent();//"Books.java"からインデントでもらったデータを引き出し

	    toBookId = toBook.getStringExtra("toBookId");//本のID
	    toBookTitle = toBook.getStringExtra("toBookTitle");//本の名前
	    toBookAuthor = toBook.getStringExtra("toBookAuthor");//本の著者
	    toBookImage = toBook.getStringExtra("toBookImage");//本のイメージの住所
	    toBookGenre = toBook.getStringExtra("toBookGenre");//本のジャンル
	    toBookDescription = toBook.getStringExtra("toBookDescription");//本の説明
		  
	  //本のイメージ住所をサーバーに要請する形式で変更
	    fixImage = "http://"+toBookImage;
		fixImage= fixImage.replaceAll("@amp;", "&");
	    
		//画面に本のイメージを表示
	    BookCorver = (ImageView)findViewById(R.id.Info_BookCover);
	    Drawable draw = loadDrawable(fixImage);
	    BookCorver.setImageDrawable(draw);
	    
	  //画面に本の名前を表示
	    BookName = (TextView)findViewById(R.id.Info_BookName);
	    BookName.setText(toBookTitle);
	    
	  //画面に本の著者を表示
	    BookAuthor = (TextView)findViewById(R.id.Info_AuthorName);
	    BookAuthor.setText(toBookAuthor);
	  
	  //画面に本のジャンルを表示
	    BookGenre = (TextView)findViewById(R.id.Info_GenreName);
	    BookGenre.setText(toBookGenre);
	    
	  //画面に本の説明を表示
	    Story = (TextView)findViewById(R.id.Info_Story);
	    Story.setText(toBookDescription);
	    
	    DownBtn = (Button)findViewById(R.id.Info_DownBtn);//「ダウンロード」ボタン
	    OKBtn = (Button)findViewById(R.id.Info_OKBtn);

		//////////////////////////////////////////////////
		//
		// 「ダウンロード」ボタン押下時の処理
		//
		//////////////////////////////////////////////////
	    DownBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				//ユーザーのIDを知るためにファイルを開ける。
				userText = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
				
				try {
					idCheck = new FileReader(userText);
					//データを読み込むバッファを開ける。
					BufferedReader Br = new BufferedReader(idCheck);
					
					//ユーザーのIDを読み込む。
					for(int i=0;i<2;i++)
					{
						userId = Br.readLine();
					}//for i end
					
					Br.close();//バッファを閉める。
					idCheck.close();//ファイルを閉める。
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent Intent = new Intent(Info.this,Down.class);
	
				//インデントで本のダウンロード画面に遷移する時、データを持って、遷移する。
				Intent.putExtra("fromInfoImage", fixImage);//サーバーに要請する形式で変更したイメージの住所
				Intent.putExtra("fromInfoBookId", toBookId);//本のID
				Intent.putExtra("fromFileUserId", userId);//ユーザーのID 
				
				startActivity(Intent);//インデント開始
			}
		});
	    
		//////////////////////////////////////////////////
		//
		// 「戻る」ボタン押下時の処理
		//
		//////////////////////////////////////////////////
	    OKBtn.setOnClickListener(new View.OnClickListener() 
	    {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();//今の画面を消して、以前の本を選択する画面に戻ります
			}
		});
	    
	}
	
	//////////////////////////////////////////////////
	//
	// 変更したイメージの住所を使って、イメージ・データを読み込む
	//
	//////////////////////////////////////////////////
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
