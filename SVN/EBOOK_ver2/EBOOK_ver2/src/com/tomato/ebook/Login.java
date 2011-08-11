package com.tomato.ebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.tomato.communication.CheckUtil;
import com.tomato.communication.Util;
import com.tomato.communication.cmsHTTP;


public class Login extends Activity {
	static final int MAX = 100;
	static int bookCounter=1;
	String[] id=new String[MAX],pwd = new String [MAX],title=new String[MAX],author=new String[MAX],description=new String[MAX],image=new String[MAX],ebook=new String[MAX],date=new String[MAX];
	String[] resId=new String[MAX],resTitle=new String[MAX],resAuthor=new String[MAX],resDescription=new String[MAX],resImage=new String[MAX],resEbook=new String[MAX],resDate=new String[MAX];	
	String email,pass,bookId,bookTitle,bookAuthor,bookDescription,bookImage,bookEbook,bookDate,userId=null;
	EditText EditID,EditPass;
	Button LogBtn,TorokuBtn,SyosaiBtn;
	CheckBox Loginck;
	CheckUtil logTest;
	HashMap<String, String> hm;
	Util cmsutil = new Util();
	Activity act = this;
	File userData,userText,userCheck;
	FileWriter[] save = new FileWriter[MAX];
	FileReader idCheck;
	ConnectivityManager cManager;    
	NetworkInfo mobile;    
	NetworkInfo wifi;    

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		JptomatoLogoActivity.actList.add(this);
		EditID = (EditText)findViewById(R.id.Login_EditID);
		EditPass = (EditText)findViewById(R.id.Login_EditPass);
		LogBtn = (Button)findViewById(R.id.Login_LogBtn);
		SyosaiBtn = (Button)findViewById(R.id.Login_syo);
		Loginck = (CheckBox)findViewById(R.id.Login_checkBox);
		TorokuBtn = (Button)findViewById(R.id.Login_TorokuBtn);
		cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);    
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);    
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);   

		if(!mobile.isConnected() && !wifi.isConnected())
		{
			//			new AlertDialog.Builder(Login.this)
			//			.setTitle("Notification")
			//			.setMessage("ログインの際、必ずWIFIや３Gに接続して下さい。")
			//			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			//
			//				@Override
			//				public void onClick(DialogInterface dialog, int which) {
			//					// TODO Auto-generated method stub
			//					
			//				}
			//			})
			//			.show();
			EditID.setEnabled(false);
			EditPass.setEnabled(false);
			Loginck.setChecked(true);
			LogBtn.setEnabled(false);
		}

		Loginck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(Loginck.isChecked())
				{
					EditID.setEnabled(false);
					EditPass.setEnabled(false);
					LogBtn.setEnabled(false);
				}
				else
				{
					EditID.setEnabled(true);
					EditPass.setEnabled(true);
					LogBtn.setEnabled(true);
				}
			}
		});

		LogBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Login.this)
				.setTitle("Notification")
				.setMessage("サーバーへ接続します。\n少々お待ち下さい。")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub			
						tryToLogin(); 
					}
				})
				.show();

			}
		});

		SyosaiBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				email = EditID.getText().toString();
				userCheck = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
				if(!userCheck.exists()||userCheck.length()==0||!userCheck.canRead())
				{
					new AlertDialog.Builder(Login.this)
					.setTitle("Notification")
					.setMessage("ユーザーのデータが存在しないです。\n ログインして下さい。")
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
					try {
						idCheck = new FileReader(userCheck);
						BufferedReader Br = new BufferedReader(idCheck);
						for(int i=0;i<2;i++)
						{
							userId = Br.readLine();
						}
						Br.close();
						idCheck.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					new AlertDialog.Builder(Login.this)
					.setTitle("Notification")
					.setMessage("ユーザーのデータが確認しました。\n書斎へ移動します。\n只、 ストアを利用しようとすれば、ログインがひつようです。")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub			
							Intent Intent = new Intent(Login.this,MyLibrary.class);
							Intent.putExtra("State", "not");
							startActivity(Intent);

						}
					})
					.show();
				}
			}
		});
		TorokuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			// TODO Auto-generated method stub
			public void onClick(View v) {

				Intent Intent = new Intent(Login.this,Join.class);
				startActivity(Intent);

			}	
		});
	}

	public void tryToLogin() {
		email = EditID.getText().toString();
		pass = EditPass.getText().toString();

		logTest = new CheckUtil(Login.this,email,pass);
		if (logTest.checkStart())
		{
			//String theUrl = "http://pairiserver.appspot.com/kaka/android_login.jsp";
			String theUrl = "http://ebookserverhjy5.appspot.com/android_login.jsp";
			Log.i(this.getLocalClassName(), theUrl);
			ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();

			httpParams.add(new BasicNameValuePair("rowid",email));
			httpParams.add(new BasicNameValuePair("email",email));
			httpParams.add(new BasicNameValuePair("pass", pass));
			httpParams.add(new BasicNameValuePair("id",bookId));
			httpParams.add(new BasicNameValuePair("title", bookTitle));
			httpParams.add(new BasicNameValuePair("author",bookAuthor));
			httpParams.add(new BasicNameValuePair("descripstion", bookDescription));
			httpParams.add(new BasicNameValuePair("imageurl",bookImage));
			httpParams.add(new BasicNameValuePair("ebook",bookEbook));
			httpParams.add(new BasicNameValuePair("date",bookDate));

			cmsHTTP cmsHttp = new cmsHTTP();
			//cmsHttp.encoding = encoding;
			cmsHttp.act = Login.this;
			Log.e("sending","sendpost");
			String tmpData = cmsHttp.sendPost(theUrl, httpParams);
			//		Log.e("postout",tmpData);
			if (tmpData == null)
			{
				return;
			}
			else
			{
				CheckUtil result = new CheckUtil();	
				int rowid = 0;
				hm = cmsutil.xml2HashMap(tmpData, cmsHttp.encoding);
				Log.e("go result","go!");
				Log.v(this.getLocalClassName(), tmpData);
				rowid = Integer.valueOf(hm.get("rowid[0]"));
				
				if(rowid==3){
				String msg = hm.get("msg[0]");
				result.CheckResult(this,rowid,msg,1);
				}
				else
				{
					addResult(hm);
				}
			}
		}
	}
	public void addResult(HashMap<String, String> hm) {
		int count = Integer.valueOf(hm.get("count"));
		Log.e("result_count",String.valueOf(count));
		Log.e("result_inStrFor","going");
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));
		String msg = hm.get("msg[0]");
		if(count==0)
		{
			resId[0] = hm.get("id[0]");
			resTitle[0] = hm.get("title[0]");
			resAuthor[0] = hm.get("author[0]");
			resDescription[0] = hm.get("description[0]");
			resImage[0] =hm.get("imageurl[0]");
			resEbook[0] =hm.get("ebook[0]");
			resDate[0] = hm.get("date[0]");
		}
		else
		{
			for(int i=0;i<count;i++)
			{
				id[i]=("id["+i+"]");
				Log.e("id",id[i].toString());
				title[i]="title["+i+"]";
				author[i]="author["+i+"]";
				description[i]="description["+i+"]";
				image[i]="imageurl["+i+"]";
				ebook[i]="ebook["+i+"]";
				date[i]="date["+i+"]";
			}
			for(int i=0;i<count;i++)
			{
				Log.e("count_result",String.valueOf(i));
				resId[i] = hm.get(id[i]);
				resTitle[i] = hm.get(title[i]);
				resAuthor[i] = hm.get(author[i]);
				resDescription[i] = hm.get(description[i]);
				resImage[i] =hm.get(image[i]);
				resEbook[i] =hm.get(ebook[i]);
				resDate[i] = hm.get(date[i]);
			}
		}
		Log.e("rowid in addReseult",rowid+"");
		//rowid = 1;
		if(rowid==1)
		{
			try {
				saveFile(rowid,email,resId,resTitle,resAuthor,resDescription,resImage,resDate);
				if(count==0)
				{
					saveBook(resEbook,0);
					SaveImg(resImage,0);
				}
				else
				{
					for(int i = 0;i<count;i++)
					{
						saveBook(resEbook,i);
						SaveImg(resImage,i);

					}
				}
				bookCounter = 1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(rowid==6)
		{

			try {
					saveFile(rowid,email,resId,resTitle,resAuthor,resDescription,resImage,resDate);
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CheckUtil result = new CheckUtil();	
		result.CheckResult(this,rowid,msg,1);
	}
	public void saveFile(int rowid,String email, String[] id,String[] title
			,String[] author,String[] description,String[] image,String[] date) throws IOException
			{
		int count = Integer.valueOf(hm.get("count"));
		userData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
		try
		{
			if(rowid==1)
			{
				save[0] = new FileWriter(userData);
				save[0].write(String.valueOf(rowid));
				save[0].write("\n");
				save[0].write(email);
				save[0].write("\n");
				if(count==0)
				{
					save[0].write(id[0]);
					save[0].write("\n");
					save[0].write(title[0]);
					save[0].write("\n");
					save[0].write(author[0]);
					save[0].write("\n");
					save[0].write(description[0]);
					save[0].write("\n");
					save[0].write(image[0]);
					save[0].write("\n");
					save[0].write(date[0]);
					save[0].write("\n");

				}
				else
				{
					for(int i=0;i<=count;i++)
					{	
						if(i==count)
							save[0].write("\n");
						else
						{
							if(i==(count-1))
								save[0].write(id[i]);
							else
							{
								save[0].write(id[i]);
								save[0].write(",");
							}
						}	
					}

					for(int i=0;i<=count;i++)
					{
						if(i==count)
							save[0].write("\n");
						else
						{
							if(i==(count-1))
								save[0].write(title[i]);
							else
							{
								save[0].write(title[i]);
								save[0].write(",");
							}

						}	
					}
					for(int i=0;i<=count;i++)
					{
						if(i==count)
							save[0].write("\n");
						else
						{
							if(i==(count-1))
								save[0].write(author[i]);
							else
							{
								save[0].write(author[i]);
								save[0].write(",");	
							}

						}	
					}
					for(int i=0;i<=count;i++)
					{

						if(i==count)
							save[0].write("\n");
						else
						{
							if(i==(count-1))
								save[0].write(description[i]);
							else
							{
								save[0].write(description[i]);
								save[0].write(",");
							}

						}
					}
					for(int i=0;i<=count;i++)
					{

						if(i==count)
							save[0].write("\n");
						else
						{
							if(i==(count-1))
							{
								save[0].write(image[i]);
							}
							else
							{
								save[0].write(image[i]);
								save[0].write(",");

							}

						}
					}	
					for(int i=0;i<=count;i++)
					{

						if(i==count)
							save[0].write("\n");
						else
						{
							if(i==(count-1))
								save[0].write(date[i]);
							else
							{
								save[0].write(date[i]);
								save[0].write(",");
							}

						}
					}
				}
				save[0].close();
			}

			else if(rowid==6)
			{
				save[0] = new FileWriter(userData);
				save[0].write(String.valueOf(rowid));
				save[0].write("\n");
				save[0].write(email);
				save[0].write("\n");
				save[0].write("%x");
				save[0].write("\n");
				save[0].write("%x");
				save[0].write("\n");
				save[0].write("%x");
				save[0].write("\n");
				save[0].write("%x");
				save[0].write("\n");
				save[0].write("%x");
				save[0].write("\n");
				save[0].write("%x");
				save[0].write("\n");
				save[0].close();

			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
			}
	public void saveBook(String[] ebook,int i) throws IOException
	{
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));
		if(rowid==1)
		{
			userData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"ebook_"+(i+1)+".ebf");	
			FileWriter tempSave = save[i+1]; 
			tempSave = new FileWriter(userData);
			tempSave.write(ebook[i]);
			tempSave.close();
		}
	}
	void  SaveImg(String[] ImgUrl,int i)throws IOException
	{
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));
		if(rowid==1)
		{
			try
			{	
				String tmpurlStr = "http://www."+ImgUrl[i];
				String imageUrl=tmpurlStr.replace("@amp;", "&");

				URL url = new URL(imageUrl);
				InputStream is = url.openStream();

				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"ebook_"+(i+1)+".jpg");
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				OutputStream filestream = null;
				filestream = new FileOutputStream(file);
				bitmap.compress(CompressFormat.JPEG, 100, filestream);

				filestream.flush();
				filestream.close();

			}
			catch(Exception e)
			{
				Log.e("URL","error,in load Drawable\n"+e.toString());
			}


		}
	}
}