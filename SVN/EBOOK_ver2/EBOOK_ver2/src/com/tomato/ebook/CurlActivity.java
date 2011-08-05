package com.tomato.ebook;

import java.io.BufferedReader;
import java.io.File;
import java.math.BigDecimal;
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
	//String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
	/*일단 제일위에 올려놓고*/
//	static int emulheight=0;/*전체를 보여주기위해*/
//	static int emulwidth=0;/*전체를 보여주기위해*/
	//private GestureDetector ges=null;
	private CurlView mCurlView;
	ArrayList<ArrayList<String>> book=new ArrayList<ArrayList<String>>();
	ArrayList<String> page=new ArrayList<String>();
//	int cnt=0;
//	HashMap<String, String> hm=new HashMap<String, String>();
	SDcard sd=new SDcard();
	String color=null;
	String bgcolor=null;
	String bookKey=null;
//	String nowSiori=null;
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		config = getResources().getConfiguration();
		ImageView image = (ImageView)findViewById(R.id.imageView1);
		//if(index0){
			if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){//전화
				image.setVisibility(View.VISIBLE);
				Bitmap orgImage = BitmapFactory.decodeResource(getResources(), R.drawable.ebookmain2);  
				Bitmap resize = Bitmap.createScaledBitmap(orgImage, 1700, 780, true);  
				image.setAlpha(80);
				image.setImageBitmap(resize);
					
			}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
				image.setVisibility(View.VISIBLE);
				Bitmap orgImage = BitmapFactory.decodeResource(getResources(), R.drawable.ebookmain);  
				Bitmap resize = Bitmap.createScaledBitmap(orgImage, 340, 640, true);  
				image.setAlpha(80);
				image.setImageBitmap(resize);
			}
		}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		MyLibrary.bkList.add(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curlactivity);
		Log.e("ac", "ac");
		
		Intent intent=getIntent();
		bookKey=intent.getStringExtra("bookKey");
		color=intent.getStringExtra("color");
		bgcolor=intent.getStringExtra("bgcolor");

		Log.e("bookKeyCurl", bookKey);
		Log.e("colorCurl", color);
		Log.e("bgcolorCurl", bgcolor);
	
		Log.e("11", "11");
		
				
		Log.e("22", "22");
		
		
		book=sd.dataload(bookKey);
		int index = 0;
		if (getLastNonConfigurationInstance() != null) {
			index = (Integer) getLastNonConfigurationInstance();
		}	
		config = getResources().getConfiguration();
		ImageView image = (ImageView)findViewById(R.id.imageView1);
		if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){//처음띄웠을때의화면
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
		mCurlView.setBitmapProvider(new BitmapProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);
		mCurlView.setBackgroundColor(0xFF202830);
		Log.e("22", "22");
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
	/**
	 * Bitmap provider.
	 */
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
			p.setTextSize(32);
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
			int y=35;
			int x=0;
			final int DIVID_SIZE = 2;
			final int TEXT_SIZE = 30 / DIVID_SIZE ;
			
			int hTab = TEXT_SIZE;  // タイトルバーを含むサイズ
			int lTeb = 15;
			page=book.get(index);
			String page3 = book.toString();
			String[] page2 = page3.split("@");
			for(int m =0; m < page2.length; m++){
				Log.e("page2 : ", page2[m]);
			}
			Log.e("page",page+"");
			for (int j = 0; j < page.size(); j++) {
				String line=String.valueOf( page.get(j));
				if (line.equals("@")) {
					line="";
				}
				Log.e("page3 : ", page3);
			
				
				c.drawText(line, 90 , y, p);//한줄씩 붙여넣음
				
				if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){//처음띄웠을때의화면
					//hTab += TEXT_SIZE + ( 10 / DIVID_SIZE );
					y+=30;
				}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
					//hTab += TEXT_SIZE + ( 10 / DIVID_SIZE );
					y+=56;
				}
			} 
			d.draw(c);
			return b;
		}
		@Override//page count
		public int getBitmapCount() {
			Log.e("page", book.size()+"hj");
			return book.size();
		}
	}
	/**
	 * CurlView size changed observer.
	 */
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
	/*세화가 해피한 기분으로 만든 메뉴7.14*/
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		menu.add(0,0,0,"環境設定");
		menu.add(0,1,0,"戻る");
		return super.onCreateOptionsMenu(menu);
		}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent intent = new Intent(CurlActivity.this,ReadSetup.class);
			intent.putExtra("bookKey", bookKey);
			intent.putExtra("color", color);
			intent.putExtra("bgcolor", bgcolor);

			startActivity(intent);
			break;
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




}