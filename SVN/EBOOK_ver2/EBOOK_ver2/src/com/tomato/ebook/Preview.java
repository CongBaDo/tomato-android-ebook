package com.tomato.ebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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
	Configuration config;
	File sdPath=Environment.getExternalStorageDirectory();
	//String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
	/*일단 제일위에 올려놓고*/
//	static int emulheight=0;/*전체를 보여주기위해*/
//	static int emulwidth=0;/*전체를 보여주기위해*/
	
	//private GestureDetector ges=null;
	private CurlPreview mCurlView2;
	ArrayList<ArrayList<String>> book2 = new ArrayList<ArrayList<String>>();
	ArrayList<String> page2=new ArrayList<String>();

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
		mCurlView2 = (CurlPreview) findViewById(R.id.curlpre);
		mCurlView2.setVisibility(View.VISIBLE);
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

	private class BitmapProvider implements CurlPreview.BitmapProvider {

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
			if(book2.size() > index){
				page2 = book2.get(index++);//順番通りでる
				
				Log.e("index",index+"");
				
				for (int j = 0; j < page2.size(); j++) {
					String line = String.valueOf( page2.get(j) );
					
					if (line.equals("@")) {
						line = " ";
						
					}
					if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
						canvas.drawText(line, lTeb , hTab, paint);
						hTab += TEXT_SIZE + ( 10 / DIVID_SIZE );
					}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
						paint.setTextSize(TEXT_SIZE+7);
						canvas.drawText(line, lTeb , hTab+50, paint);
						hTab += TEXT_SIZE + ( 40 / DIVID_SIZE );
					}
				}//end for 
				//mCurlView.addCurrentIndex();
			}
			// [ 1 . 2 ] の座標
			
			hTab = TEXT_SIZE;  // 上位の初期値
			
			if(book2.size() > index){
				page2 = book2.get(index++);
				Log.e("index",index+"");
				
				if(page2 != null || page2.size() != 0 ){
					
					for (int j = 0; j < page2.size(); j++) {
						String line = String.valueOf( page2.get(j) );
						
						if (line.equals("@")) {
							line = " ";
						}
						if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
							canvas.drawText(line, lTeb + width /2 , hTab, paint);
							hTab += TEXT_SIZE + ( 10 / DIVID_SIZE );
						}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
							paint.setTextSize(TEXT_SIZE+7);
							canvas.drawText(line, lTeb + width /2 , hTab+50, paint);
							hTab += TEXT_SIZE + ( 40 / DIVID_SIZE );
						}
						
						
					}//end for 
				}
			}
			// [ 2 . 1 ] の座標
			
			hTab = TEXT_SIZE + height / 2;  // 上位の初期値
			if(book2.size() > index){
				page2 = book2.get(index++);
				Log.e("page",page2+"");
				Log.e("index",index+"");
				
				if(page2 != null || page2.size() != 0 ){
					
					for (int j = 0; j < page2.size(); j++) {
						String line = String.valueOf( page2.get(j) );
						
						if (line.equals("@")) {
							line = " ";
						}
						if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
							canvas.drawText(line, lTeb , hTab, paint);
							hTab += TEXT_SIZE + ( 10 / DIVID_SIZE );
						}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
							paint.setTextSize(TEXT_SIZE+7);
							canvas.drawText(line, lTeb , hTab, paint);
							hTab += TEXT_SIZE + ( 40 / DIVID_SIZE );
						}
					}//end for 
				}
			}
						
			hTab = TEXT_SIZE + height / 2;  // 上位の初期値
			lTeb = lTeb + width /2 ;        // 上位の初期値
			if(book2.size() > index){
				page2 = book2.get(index++);
				Log.e("index",index+"");
		
				if(page2 != null || page2.size() != 0 ){
					
					for (int j = 0; j < page2.size(); j++) {
						String line = String.valueOf( page2.get(j) );
						
						if (line.equals("@")) {
							line = " ";
						}
						if(config.orientation == Configuration.ORIENTATION_LANDSCAPE){
							
							canvas.drawText(line, lTeb , hTab, paint);
							hTab += TEXT_SIZE + ( 10 / DIVID_SIZE );
						}else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){
							paint.setTextSize(TEXT_SIZE+7);
							canvas.drawText(line, lTeb , hTab, paint);
							hTab += TEXT_SIZE + ( 40 / DIVID_SIZE );
						}
						
						
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
			
			int e = (int) Math.ceil(book2.size()/4)+1;
					Log.e("booksize/4", e + "hj");
			return e;
		}
	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlPreview.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				mCurlView2.setViewMode(CurlView.SHOW_TWO_PAGES);
				mCurlView2.setMargins(.07f, .05f, .07f, .05f);//left,top,right,bo
			} else {
				mCurlView2.setViewMode(CurlView.SHOW_ONE_PAGE);
				mCurlView2.setMargins(.03f, .0228f, .053f, .028f);
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