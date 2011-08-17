package com.tomato.ebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.tomato.pagecurl.CurlView;
import com.tomato.sdcard.SDcard;



/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
public class CurlActivity extends Activity {

//	static int siori;
	Configuration config;
	File sdPath=Environment.getExternalStorageDirectory();
	/*일단 제일위에 올려놓고*/

	private CurlView mCurlView;
	ArrayList<ArrayList<String>> book2=new ArrayList<ArrayList<String>>();
	ArrayList<String> page2=new ArrayList<String>();
	SDcard sd=new SDcard();
	String color=null;
	String bgcolor=null;
	String bookKey=null;
	String books="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		MyLibrary.bkList.add(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curlactivity);
		Intent intent=getIntent();
		bookKey=intent.getStringExtra("bookKey");
		color=intent.getStringExtra("color");
		bgcolor=intent.getStringExtra("bgcolor");
		//ページ再配置のためにメソッドで整理
		try {
			book2 = booksgo(bookKey);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		int index = 0;
		if (getLastNonConfigurationInstance() != null) {
			index = (Integer) getLastNonConfigurationInstance();
		}
		config = getResources().getConfiguration();
		ImageView image = (ImageView)findViewById(R.id.imageView1);
		//最初の場面
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
			image.setVisibility(View.VISIBLE);
			Bitmap orgImage = BitmapFactory.decodeResource(getResources(), R.drawable.ebookmain2);  
			Bitmap resize = Bitmap.createScaledBitmap(orgImage, 1350, 750, true);
			image.setAlpha(80);
			image.setImageBitmap(resize);
		}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
			image.setVisibility(View.VISIBLE);
			Bitmap orgImage = BitmapFactory.decodeResource(getResources(), R.drawable.ebookmain);  
			Bitmap resize = Bitmap.createScaledBitmap(orgImage, 340, 540, true);
			image.setAlpha(80);
			image.setImageBitmap(resize);
		}
		mCurlView = (CurlView) findViewById(R.id.curl);
		mCurlView.setVisibility(View.VISIBLE);
		mCurlView.setBitmapProvider(new BitmapProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);
		mCurlView.setBackgroundColor(0xFF202830);
	}
	@Override
	public void onPause() {
		super.onPause();
		mCurlView.onPause();
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
			Bitmap b = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			Drawable d = getResources().getDrawable(R.drawable.aaa);
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
			Paint p = new Paint();
			p.setColor(Color.BLACK);
			p.setTextAlign(Paint.Align.LEFT);
			p.setTextSize(30);
			p.setAntiAlias(true); 
			if (color!=null & bgcolor!=null) {
				c.drawColor(Color.parseColor(bgcolor));
				p.setColor(Color.parseColor(color));
			}else{
				c.drawRect(r, p);
				c.drawColor(Color.parseColor("#FFFFFF"));
			}
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;
			d.setBounds(r);
			int y=65;
			int x=90;
			page2 = book2.get(index);
			Log.e("page2",page2+"");
			Log.e("index",index+"");
			for (int j = 0; j < page2.size(); j++) {
				String line=String.valueOf( page2.get(j));
				if (line.equals("@")) {
					line="";
				}
				if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
					p.setTextSize(23);
					c.drawText(line, x , y, p);
					y+=35;
				}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
					c.drawText(line, x , y, p);
					y+=65;
				}
			} 
			d.draw(c);
			return b;
		}
		@Override//page count
		public int getBitmapCount() {
			return book2.size();
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
				Intent intent = new Intent(CurlActivity.this,ReadSetup.class);
				intent.putExtra("bookKey", bookKey);
				intent.putExtra("color", color);
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
	//ページの再配置のために
	public ArrayList<ArrayList<String>> booksgo(String bookKey) throws IOException{
		ArrayList<ArrayList<String>> book=new ArrayList<ArrayList<String>>();
		ArrayList<String> page=new ArrayList<String>();
		String book1="";
		String bookAdd="";
		FileReader fr = new FileReader("/sdcard/ebook_"+bookKey+".ebf");//bookKeyに該当する本を呼ぶ
		BufferedReader br=new BufferedReader(fr);
		int y=0;
		while((bookAdd=br.readLine())!=null){
			book1= book1+ bookAdd;
			if(y <=1){
				book1= book1+"@";//タイトルと著書を区別するため
			}
			y= y+1;
		}
		//タイトルと本文を区別するために＠をタイトルに残す
		StringBuffer sk = new StringBuffer(); 
		for(int k = 0; k < book1.length(); k++){
			if(book1.charAt(k)=='@'){
				if(k > 50){
					//50番目の列から＠をすべて削除
					sk.append("");
				}else{
					sk.append(book1.charAt(k));
				}
			}else{
				sk.append(book1.charAt(k));
			}
		}
		book1 = sk.toString();
		//本文のページの区別に入る
		String linere= "";
		//まずタイトルと本文の内容で区別する
		//タイトルだけ最初のページに追加して置く
		for(int k =0; k < book1.length(); k++){
			if(book1.charAt(k)=='@'){
				//booksgo メソッドで追加しておいた＠ぺーじを最初のページに追加
				page.add(linere);
				linere = "";
			}else{
				linere = linere + String.valueOf(book1.charAt(k));
			}
		}
		//タイトルページを本に追加
		book.add(page);
		//ぺーじをリセットする
		page = new ArrayList<String>();
		StringBuffer strBuf = new StringBuffer();
        char c = 0;
        int nSrcLength = linere.length();
        //まずは文字をすべて全角で変換
        for (int i = 0; i < nSrcLength; i++)
        {
            c = linere.charAt(i);
            //英語化特注文字の場合
            if (c >= 0x21 && c <= 0x7e)
            {
                c += 0xfee0;
            }
            //空白の場合
            else if (c == 0x20)
            {
                c = 0x3000;
            }
            //文字列　バッファーに変換した文字を入れる
            strBuf.append(c);
        }

        linere = strBuf.toString();
		Log.e("linere", linere.length()+"");
		//lineNumberはline, stringGetは文字の数
		int lineNumber=0, w=0,stringGet=14;
		String linere2= "";
		//本文の整列
		for(int k =0; k< linere.length(); k++){
			linere2 = linere2 + String.valueOf(linere.charAt(k));
			if(k == stringGet){
				//15文字ずつ
				stringGet += 15; 
				//lineの計算
				page.add(linere2);
				Log.e("page2", linere2+""+stringGet);
				//lineを空にする
				linere2 = "";
				//lineが何lineか
				lineNumber += 1;
			}
			//15列になったら
			else if(lineNumber==15){
				//本にページを追加
				book.add(page);
				//ページを空にする
				page=new ArrayList<String>();
				//列の計算初期化
				lineNumber=0;
			}
		}
		//forの中で追加できなかった最後のページを追加
		book.add(page);
		page=new ArrayList<String>();
		return book;
	}
}