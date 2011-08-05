package com.tomato.ebook;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tomato.adapter.MyLibraryAdapter;
import com.tomato.sdcard.SDcard;
import com.tomato.communication.*;

public class MyLibrary extends Activity {
	public static ArrayList<Activity> bkList = new ArrayList<Activity>();
	TextView tv;
	Button readBtn,fileBtn;
	ImageView  store,list_book_detail,exit;
	final static int MAX = 100; 
	//	HashMap<String, String> hm;

	ArrayList<String> data_list=new ArrayList<String>();
	ArrayList<String> datadata=new ArrayList<String>();
	String[] bunri = null;
	SDcard sd=null;

	String userid=null,userId = null,book=null,title=null,writer=null,des=null,image_url=null,date=null,state=null;

	int book_key=1;	
	ConnectivityManager cManager;    
	NetworkInfo mobile;    
	NetworkInfo wifi;   
	File userData,userText;
	FileWriter[] save = new FileWriter[MAX];
	FileReader idCheck;

	//	String allSiori=null;
	//	String siori=null;


	String ext=Environment.getExternalStorageState();
	String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
	String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"\\tmt"+"\\";		
	File bookText;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylibrary);
		JptomatoLogoActivity.actList.add(this);
		tv=(TextView) findViewById(R.id.list_book_detail_text);
		readBtn=(Button) findViewById(R.id.list_book_read);	
		fileBtn=(Button)findViewById(R.id.list_file_read);
		exit=(ImageView)findViewById(R.id.exit);
		cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);    
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);    
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);   
		store=(ImageView)findViewById(R.id.store);	
		list_book_detail=(ImageView) findViewById(R.id.list_book_detail);
		sd=new SDcard();
		datadata=sd.tryToMyLibrary();//read login.txt

		//Gallery view
		Gallery mylibrarylist=(Gallery) findViewById(R.id.gallery);
		Log.e("g", "1");
		mylibrarylist.setAdapter(new MyLibraryAdapter(this));
		Log.e("g", "2");
		mylibrarylist.setOnItemClickListener(list_listener);
		Log.e("g", "3");


		readBtn.setOnClickListener(read_listener);
		fileBtn.setOnClickListener(read_listener);
		store.setOnClickListener(button_listener);
		exit.setOnClickListener(button_listener);

		Log.e("main", "main");


		Intent getFromLogin = getIntent();
		state = getFromLogin.getStringExtra("State");
		if((!mobile.isConnected() && !wifi.isConnected())||state.equals("not"))
		{
			store.setVisibility(View.GONE);
		}





	}

	String[] redata=null;

	public String[] datafor(String data){

		redata=data.split(",");

		return redata;
	}






	//		String theUrl = "http://ebookssongs3.appspot.com/ebookSelectList.jsp";
	//		Log.i(this.getLocalClassName(), theUrl);
	//		ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
	//		httpParams.add(new BasicNameValuePair("key","6002"));
	//
	//		cmsHTTP cmsHttp = new cmsHTTP();
	//		//cmsHttp.encoding = encoding;
	//		cmsHttp.act = MyLibrary.this;
	//		String tmpData = cmsHttp.sendPost(theUrl, httpParams);
	//		if (tmpData == null)
	//		{
	//			return;
	//		}
	//		else
	//		{
	//			hm = cmsutil.xml2HashMap(tmpData, cmsHttp.encoding);
	//			Log.v("nakami", tmpData);
	//			addResult(hm);
	//		}


	//	public void addResult(HashMap<String, String> hm) {
	//
	//		Log.e("in","in:");
	//		//		String msg = hm.get("msg[0]");
	////		for (int i = 0; i < hm.size(); i++) {
	////			id=hm.get("id["+i+"]");
	////			Log.e("hm", id);
	////			genre=hm.get("genre["+i+"]");
	////			title=hm.get("title["+i+"]");
	////			author=hm.get("author["+i+"]");
	////			decriptions=hm.get("decriptions["+i+"]");
	////			ebook=hm.get("ebook["+i+"]");
	////			count=hm.get("count["+i+"]");
	////			date=hm.get("date["+i+"]");
	////
	////			HashMap<String, String> data=new HashMap<String, String>();
	////			data.put("id", id);
	////			data.put("genre", genre);
	////			data.put("title", title);
	////			data.put("author", author);
	////			data.put("decriptions", decriptions);
	////			data.put("ebook", ebook);
	////			data.put("count", count);
	////			data.put("date", date);
	////			
	////			data_list.add(data);
	////
	////			
	////		}
	//
	//		tv.setText(hm.get("ebook[0]"));
	//		
	////		Toast.makeText(MyLibrary.this, hm.get("id[0]"), Toast.LENGTH_LONG).show();
	////
	////		Log.e("ebook", hm.get("book[0]"));
	//
	//	}

	private OnClickListener button_listener=new OnClickListener() {

		@Override
		public void onClick(View v) {


			switch (v.getId()) {

			case R.id.store:
				if(!mobile.isConnected() && !wifi.isConnected())
				{
					new AlertDialog.Builder(MyLibrary.this)
					.setTitle("Notification")
					.setMessage("今、ネットの問題が有って、利用ができません。\n後で利用して下さい。")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					})
					.show();
				}
				else
				{
					Intent intent=new Intent(MyLibrary.this, Genre_TabActivity.class);
					startActivity(intent);
				}
				break;


			case R.id.exit:
				new AlertDialog.Builder(MyLibrary.this)
				.setTitle("Notification")
				.setMessage("最初画面に戻ります。")
				.setNeutralButton("戻る", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						close();
						System.exit(1);
					}
				})
				.show();

				break;
			}
		}
	};



	private OnClickListener read_listener=new OnClickListener() {//read button

		@Override
		public void onClick(View v) {
			switch(v.getId())
			{

				case R.id.list_book_read:
				{
					Intent intent=new Intent(MyLibrary.this, CurlActivity.class);
					//			intent.putExtra("bookKey", bookey[book_key]);
					intent.putExtra("bookKey", book_key+"");
					intent.putExtra("color", "#000000");
					intent.putExtra("bgcolor", "#FFFFFF");
					//			intent.putExtra("siori", siori);
	
					startActivity(intent);
					break;
				}
				case R.id.list_file_read:
				{
					fileLoadUtil un = new fileLoadUtil();
					un.unCompress(filePath,"test.tmt");
					Toast.makeText(MyLibrary.this, "準備中です。", Toast.LENGTH_LONG).show();
					break;
				}
			}
		}
	};

	private OnItemClickListener list_listener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			Log.e("g", "g1");

			sd=new SDcard();

			userid=datadata.get(1);
			book=datadata.get(2);
			title=datadata.get(3);
			writer=datadata.get(4);
			des=datadata.get(5);
			image_url=datadata.get(6);
			date=datadata.get(7);


			for (int i = 0; i < datadata.size(); i++) {
				Log.e("datadata", datadata.get(i));
			}


			String[] booktitle=datafor(title);
			String[] bookwriter=datafor(writer);
			String[] bookdes=datafor(des);
			String[] bookimg=image_url.split(",");
			for(int k = 0; k < bookimg.length; k++){
				bookimg[k] = "/sdcard/ebook_"+ (k+1) + ".jpg";
			}

			String viewImage=bookimg[position];
			Bitmap bit = BitmapFactory.decodeFile(viewImage);
			list_book_detail.setImageBitmap(bit);
			tv.setText("タイトル＝"+booktitle[position]+"\n"+"作家＝"+bookwriter[position]+"\n"+"簡単説明＝"+bookdes[position]);

			book_key=position+1;




		}
	};

	public void close()  
	{  

		finish();  

		int nSDKVersion = Integer.parseInt(Build.VERSION.SDK);  

		if(nSDKVersion < 8)    //2.1이하  

		{  

			ActivityManager actMng = (ActivityManager)getSystemService(ACTIVITY_SERVICE);  

			actMng.restartPackage(getPackageName());  

		}  

		else  

		{  

			new Thread(new Runnable() {  

				public void run() {  

					ActivityManager actMng = (ActivityManager)getSystemService(ACTIVITY_SERVICE);  

					String strProcessName = getApplicationInfo().processName;  

					while(true)  

					{  

						List<RunningAppProcessInfo> list = actMng.getRunningAppProcesses();  

						for(RunningAppProcessInfo rap : list)  

						{  

							if(rap.processName.equals(strProcessName))  

							{  

								if(rap.importance >= RunningAppProcessInfo.IMPORTANCE_BACKGROUND)  

									actMng.restartPackage(getPackageName());  

								Thread.yield();  

								break;  

							}  

						}  

					}  

				}  

			}, "Process Killer").start();  

		}  
		System.exit(0);
	}  


}
