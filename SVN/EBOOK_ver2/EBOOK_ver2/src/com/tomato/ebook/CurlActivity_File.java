package com.tomato.ebook;

import java.io.File;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.tomato.pagecurl.CurlView;


/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */

public class CurlActivity_File extends Activity {


	Configuration config;													//横と縦の時を区別する
	
	private CurlView mCurlView;
	private ArrayList<ArrayList<String>> book2=new ArrayList<ArrayList<String>>();	//実際の本のデータ
	private ArrayList<String> page2=new ArrayList<String>();						//実際の本でページをつかみ出す
	private String filename=null;														
	private String bgcolor=null;
	private String bookKey=null;
	private Bitmap b= null;
	private Bitmap resize = null;
	private Paint p = new Paint();
	private int count = 0;
	private String position = null;
	private String[] name = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		MyLibrary.bkList.add(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curlactivity);
		Intent intent=getIntent();
		filename=intent.getStringExtra("FileName");
		position = intent.getStringExtra("Position");
		name = filename.split(".pdfImg");
		count = countPage();
		Log.e("filename",filename);
		
		//ページ再配置のためにメソッドで整理
		int index = 0;
		if(getLastNonConfigurationInstance() != null){
			index = (Integer) getLastNonConfigurationInstance();
		}
		config = getResources().getConfiguration();
		ImageView image = (ImageView)findViewById(R.id.imageView1);
		//場面の後ろの背景
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
			image.setVisibility(View.VISIBLE);
			Bitmap orgImage = BitmapFactory.decodeResource(getResources(), R.drawable.ebookmain2);  
			resize = Bitmap.createScaledBitmap(orgImage, 1350, 750, true);
			image.setAlpha(80);
			image.setImageBitmap(resize);
			orgImage.recycle();
			orgImage = null;
		}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
			image.setVisibility(View.VISIBLE);
			Bitmap orgImage = BitmapFactory.decodeResource(getResources(), R.drawable.ebookmain);  
			resize = Bitmap.createScaledBitmap(orgImage, 340, 540, true);
			image.setAlpha(80);
			image.setImageBitmap(resize);
			orgImage.recycle();
			orgImage = null;
		}
		
		mCurlView =(CurlView)findViewById(R.id.curl);
		mCurlView.setVisibility(View.VISIBLE);
		mCurlView.setBitmapProvider(new BitmapProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index,config);
		mCurlView.setBackgroundColor(0xFF202830);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mCurlView.onPause();
		resize.recycle();
		resize = null;
	}
	@Override
	public void onResume() {
		super.onResume();
		mCurlView.onResume();
	}
	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurlView.getCurrentIndex();
	}
	private class BitmapProvider implements CurlView.BitmapProvider {
		Configuration config = getResources().getConfiguration();
		@Override
		public Bitmap getBitmap(int width, int height, int index) {
			
			Log.e("DEBUG", "Heap Size : "+Long.toString(Debug.getNativeHeapAllocatedSize()));
			b= null;
			b = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			
			Drawable d = Drawable.createFromPath("/sdcard/Tomato/pdfimg/"+name[0]+"/"+name[0]+"_"+(index+1)+".png");
			int margin = 7;
			int border = 3;
			Rect r = new Rect(margin, margin, width - margin, height - margin);
			int imageWidth = r.width() - (border * 2);
			int imageHeight = imageWidth * d.getIntrinsicHeight()
			/ d.getIntrinsicWidth();
			if (imageHeight > r.height() - (border * 2)) {
				imageHeight = r.height() - (border * 2);
				imageWidth = imageHeight * d.getIntrinsicWidth()
				/ d.getIntrinsicHeight();
			}
			r.left += ((r.width() - imageWidth) / 2) - border;
			r.right = r.left + imageWidth + border + border;
			r.top += ((r.height() - imageHeight) / 2)- border;
			r.bottom = r.top + imageHeight + border + border;
			p.setColor(Color.BLACK);
			p.setTextAlign(Paint.Align.LEFT);
			p.setTextSize(30);
			p.setAntiAlias(true); 
					
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;
			d.setBounds(r);
			int y=65;
			int x=90;
			//本のページを計算
			if(book2.size() > index){
				page2 = book2.get(index);
				
				Log.e("page2",page2+"");
				Log.e("index",index+"");
				for (int j = 0; j < page2.size(); j++) {
					//該当ページの内容を書く
					String line=String.valueOf( page2.get(j));
					if (line.equals("@")) {
						line="";
					}
					if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
						//文字のサイズを23に修正
						p.setTextSize(23);
						c.drawText(line, x , y, p);
						//横の場合文字の行の距離を35に修正
						y+=35;
					}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
						c.drawText(line, x , y, p);
						//縦の場合文字の行の距離を65に修正
						y+=65;
					}
				}
			}
			d.draw(c);
			d = null;
			p.reset();
			return b;
		}
		
		//本のページ数を返す
		@Override//page count
		public int getBitmapCount() {
			return count;
		}
	}
	
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
				mCurlView.setMargins(.07f, .05f, .07f, .05f);//left,top,right,bo
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				mCurlView.setMargins(.03f, .0228f, .053f, .028f);
			}
		} 
	}
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		menu.add(0,0,0,"環境設定");
		menu.add(0,1,0,"戻る");
		return super.onCreateOptionsMenu(menu);
		}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
			{
				Intent intent = new Intent(CurlActivity_File.this,ReadSetup.class);
				intent.putExtra("bookKey", bookKey);
				intent.putExtra("bgcolor", bgcolor);
				startActivity(intent);
				break;
			}
			case 1:
			{
				for(int i =0;i <MyLibrary.bkList.size();i++ )
				{
					MyLibrary.bkList.get(i).finish();
				}
				break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private int countPage(){
		int i = 1;
		while(true){
			File file = new File("/sdcard/Tomato/pdfimg/"+name[0]+"/"+name[0]+"_"+i+".png");
			if((file.exists())){
				i++;
			}
			else{
				break;
			}
		}
		Log.e("CountNumber",i+"");
		return i-1;
	}
}
//携帯料金請求システムはこの加入している顧客の携帯に請求情報を送るシステムです。
//私が勤めた部分は顧客の情報を管理する場面と料金を会計する部分と以前請求が完了したすべての請求
//情報を管理する場面を負かされました。

//書籍在庫管理システムは本の情報登録、削除、貸与、期限延長などを管理するシステムです。
//このシステムでわたしが勤めた部分は本の登録場面、検索場面、貸与期限が過ぎたら延長量料が会計する場面を
//勤めました。
//ショッピングガイド商品の価格を確認して買い物リストを生成して、検索するアプリです。
//ここで私が勤めた部分はすべての買い物リストをListViewに見せる場面及び機能を
//勤めました
//Onrain Meseengerはjavaで作ったプログラムでタイトル通りに一般的にしているMESEENGERと同じです。
//友達の追加、１：１、1；Nの、チャットができます。
//私が勤めた部分は友達を追加すればデータベースに登録される部分とLogin部分と接続するときにMESEENGERに
//接続した人の友達リストを見せる部分を勤めました
//Ebookはアンドロイドで作りましたがサーバはGOOGLEAPPENGINEを使いました。
//簡単にはさせばサーバに登録している本をダウンロードしてアンドロイドフォンで読めるシステムです。
//私が勤めた部分はサーバの政策、本の情報をサーバに登録、削除、修正　などの機能の追加
//android端末で勤めた部分は本物の本を読むようにOPENGLを使って本をめくる効果を与えました。


