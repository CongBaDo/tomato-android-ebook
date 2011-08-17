package com.tomato.ebook;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;

import com.tomato.pagecurl.CurlPreview;
import com.tomato.pagecurl.CurlView;
import com.tomato.sdcard.SDcard;

/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
public class Preview extends Activity {

//	static int siori;

	File sdPath=Environment.getExternalStorageDirectory();
	//String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
	/*일단 제일위에 올려놓고*/
//	static int emulheight=0;/*전체를 보여주기위해*/
//	static int emulwidth=0;/*전체를 보여주기위해*/
	
	//private GestureDetector ges=null;
	private CurlView mCurlView2;
	ArrayList<ArrayList<String>> book = new ArrayList<ArrayList<String>>();
	ArrayList<String> page=new ArrayList<String>();

//	int cnt=0;
//	HashMap<String, String> hm=new HashMap<String, String>();
	SDcard sd=new SDcard();
	String color=null;
	String bgcolor=null;
	String bookKey=null;
//	String nowSiori=null;
	
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
		mCurlView2 = (CurlView) findViewById(R.id.curl);
		
		mCurlView2.setBitmapProvider(new BitmapProvider());
		mCurlView2.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView2.setCurrentIndex(index);
		mCurlView2.setBackgroundColor(0xFF202830);
		
		Log.e("22", "22");
		
	}


	///////////////////////////////

	@Override
	public void onPause() {
		super.onPause();
		mCurlView2.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurlView2.onResume();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return mCurlView2.getCurrentIndex();
	}

	/**
	 * Bitmap provider.
	 */

	private class BitmapProvider implements CurlView.BitmapProvider {

		@Override
		public Bitmap getBitmap(int width, int height, int index) {
			
			final int DIVID_SIZE = 2;
			final int TEXT_SIZE = 28 / DIVID_SIZE ;
			
			
			
			
			int height1 = getWindowManager().getDefaultDisplay().getHeight();
			int width1 = getWindowManager().getDefaultDisplay().getWidth();
			
			
			
			
			Bitmap b = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas canvas = new Canvas(b);


			Drawable drawable = getResources().getDrawable(R.drawable.aaa);

			int margin = 7;
			int border = 3;
			Rect rect = new Rect(margin, margin, width - margin, height - margin);

			int imageWidth  =  rect.width() - (border * 2);
			int imageHeight =  imageWidth 
								* drawable.getIntrinsicHeight()
								/ drawable.getIntrinsicWidth();
			
			if (imageHeight > rect.height() - (border * 2)) {
				imageHeight = rect.height() - (border * 2);
				imageWidth = 
							imageHeight 
							* drawable.getIntrinsicWidth()
							/ drawable.getIntrinsicHeight();
			}

			rect.left  += ((rect.width() - imageWidth) / 2) - border;
			rect.right  = rect.left + imageWidth + border + border;
			rect.top   += ((rect.height() - imageHeight) / 2)- border;
			rect.bottom = rect.top + imageHeight + border + border;

			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Paint.Align.LEFT);
			paint.setTextSize(TEXT_SIZE);
			paint.setAntiAlias(true); 

			
			if (color != null || bgcolor != null) {
				
				canvas.drawColor(Color.parseColor(bgcolor));
				paint.setColor(Color.parseColor(color));
			}else{
				
				canvas.drawRect(rect, paint);
				canvas.drawColor(Color.parseColor("#FFFFFF"));
			}

			
			rect.left   += border;
			rect.right  -= border;
			rect.top    += border;
			rect.bottom -= border;

			drawable.setBounds(rect);
			
			
			// [ 1 . 1 ] の座標
			
			int hTab = TEXT_SIZE;  // タイトルバーを含むサイズ
			int lTeb = 10;
			if(book.size() > index){
				page = book.get(index++);//順番通りでる
				
				Log.e("page",page+"");
				Log.e("index",index+"");
				
				for (int j = 0; j < page.size(); j++) {
					String line = String.valueOf( page.get(j) );
					
					if (line.equals("@")) {
						line = " ";
						
					}
					
					canvas.drawText(line, lTeb , hTab, paint);
					
					hTab += TEXT_SIZE + ( 10 / DIVID_SIZE ) ;
					
				}//end for 
				//mCurlView.addCurrentIndex();
			}
			// [ 1 . 2 ] の座標
			
			hTab = TEXT_SIZE;  // 上位の初期値
			
			if(book.size() > index){
				page = book.get(index++);
				Log.e("page",page+"");
				Log.e("index",index+"");
				
				if(page != null || page.size() != 0 ){
					
					for (int j = 0; j < page.size(); j++) {
						String line = String.valueOf( page.get(j) );
						
						if (line.equals("@")) {
							line = " ";
						}
						
						canvas.drawText(line, lTeb + width /2 , hTab, paint);
						hTab += TEXT_SIZE + ( 10 / DIVID_SIZE ) ;
						
					}//end for 
				}
			}
			// [ 2 . 1 ] の座標
			
			hTab = TEXT_SIZE + height / 2;  // 上位の初期値
			if(book.size() > index){
				page = book.get(index++);
				Log.e("page",page+"");
				Log.e("index",index+"");
				
				if(page != null || page.size() != 0 ){
					
					for (int j = 0; j < page.size(); j++) {
						String line = String.valueOf( page.get(j) );
						
						if (line.equals("@")) {
							line = " ";
						}
						
						canvas.drawText(line, lTeb , hTab, paint);
						hTab += TEXT_SIZE + ( 10 / DIVID_SIZE ) ;
						
					}//end for 
				}
			}
						
			hTab = TEXT_SIZE + height / 2;  // 上位の初期値
			lTeb = lTeb + width /2 ;        // 上位の初期値
			if(book.size() > index){
				page = book.get(index++);
				Log.e("page",page+"");
				Log.e("index",index+"");
		
				if(page != null || page.size() != 0 ){
					
					for (int j = 0; j < page.size(); j++) {
						String line = String.valueOf( page.get(j) );
						
						if (line.equals("@")) {
							line = " ";
						}
						
						canvas.drawText(line, lTeb , hTab, paint);
						hTab += TEXT_SIZE + ( 10 / DIVID_SIZE );
						
					}//end for 
					
					//mCurlView.addCurrentIndex();
				}
			}
			//mCurlView.addCurrentIndex(index);
			drawable.draw(canvas);
			return b;
		}

		@Override//page count
		public int getBitmapCount() {
			
			int e = (int) Math.ceil(book.size()/4)+1;
					Log.e("booksize/4", e + "hj");
			return e;
		}
	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView2.setViewMode(CurlPreview.SHOW_TWO_PAGES);
				//mCurlView.setMargins(.1f, .05f, .1f, .05f);
				mCurlView2.setMargins(0, 0, 0, 0);
			} else {
				mCurlView2.setViewMode(CurlPreview.SHOW_ONE_PAGE);
				//mCurlView.setMargins(.1f, .1f, .1f, .1f);
				mCurlView2.setMargins(0, 0, 0, 0);
			}
		}
	}
	/*세화가 해피한 기분으로 만든 메뉴7.14*/
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		
		menu.add(0,0,0,"環境設定");//.setIcon(R.drawable.menu1);
		menu.add(0,1,0,"戻る");//.setIcon(R.drawable.menu2);
		return super.onCreateOptionsMenu(menu);
		}
	//
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent intent = new Intent(Preview.this,ReadSetup.class);
			intent.putExtra("bookKey", bookKey);
			intent.putExtra("color", color);
			intent.putExtra("bgcolor", bgcolor);

			startActivity(intent);
			break;
//		case 1:
//			try {
//				
//				
//				Toast.makeText(CurlActivity.this, ":+:しおり設定完了:+:", Toast.LENGTH_LONG).show();
//				
////				AlertDialog.Builder bld = new AlertDialog.Builder(CurlActivity.this);
////				bld.setTitle(":+:しおり設定完了:+:");
////				bld.setMessage("しおりを保存しました");
////				bld.show();
//
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//
//
//			break;
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