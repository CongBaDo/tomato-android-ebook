package com.tomato.ebook;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.tomato.pagecurl.CurlView;
import com.tomato.sdcard.SDcard;

/**
 * Simple Activity for curl testing.
 * 
 * @author harism
 */
public class CurlActivity extends Activity {

//	static int siori;

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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
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
		mCurlView = (CurlView) findViewById(R.id.curl);
		
		mCurlView.setBitmapProvider(new BitmapProvider());
		mCurlView.setSizeChangedObserver(new SizeChangedObserver());
		mCurlView.setCurrentIndex(index);
		mCurlView.setBackgroundColor(0xFF202830);
		
		Log.e("22", "22");
		
	}


	///////////////////////////////

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
			p.setTextSize(50);
			p.setAntiAlias(true); 

			
			
			Log.e("11111111111", "11111111111");

//			if (!color.equals(null) & !bgcolor.equals(null)) {
			if (color!=null & bgcolor!=null) {
				Log.e("22", "222");
//				int a2=Integer.parseInt(bgcolor);
//				int a1=Integer.parseInt(color);
				Log.e("curlco11111111111111", color);
				Log.e("curlbg111111111111111",bgcolor);
				c.drawColor(Color.parseColor(bgcolor));
				p.setColor(Color.parseColor(color));
			}else{
				Log.e("3333", "3333");
				
				c.drawRect(r, p);
				c.drawColor(Color.parseColor("#FFFFFF"));
			}

			
			
			Log.e("8", "8");
//			c.drawRect(r, p);
//			c.drawColor(Color.parseColor("#FFFFFF"));
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;

			d.setBounds(r);

			int y=65;
		
//			siori=index;/*책갈피를 위해 인덱스값을 강제로 스테틱에 전해주겠어 7.15 김세화님*/
			page=book.get(index);
			Log.e("list.size", page.size()+"");
			for (int j = 0; j < page.size(); j++) {
				String a=page.get(j)+"";
				
				if (a.equals("@")) {
					a=" ";
				}
				
				c.drawText(a, 55, y, p);
				y+=60;
			}//end for 

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
				//mCurlView.setMargins(.1f, .05f, .1f, .05f);
				mCurlView.setMargins(0, 0, 0, 0);
			} else {
				mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				//mCurlView.setMargins(.1f, .1f, .1f, .1f);
				mCurlView.setMargins(0, 0, 0, 0);
			}
		}
	}
	/*세화가 해피한 기분으로 만든 메뉴7.14*/
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		
		menu.add(0,0,0,"環境設定");//.setIcon(R.drawable.menu1);
		menu.add(0,1,0,"戻る");//.setIcon(R.drawable.menu2);
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
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//
//
//			break;
		case 1:
			Intent intent3 = new Intent(CurlActivity.this,MyLibrary.class);
			startActivity(intent3);
			break;
		}
		return super.onOptionsItemSelected(item);
	}




}