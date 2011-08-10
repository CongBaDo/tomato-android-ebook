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

import com.tomato.communication.CheckUtil;
import com.tomato.communication.Util;
import com.tomato.communication.cmsHTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class Down extends Activity {

	Button OKBtn,CancelBtn;
	ImageView bookCover;
	String toInfoImage=null,toInfoBook=null,toInfoUserId=null;

	static final int MAX = 100;
	EditText EditID,EditPass;
	Button LogBtn,TorokuBtn;
	CheckUtil logTest;
	HashMap<String, String> hm;
	Util cmsutil = new Util();
	Activity act = this;
	File userData;
	FileWriter[] save = new FileWriter[MAX];
	String bookId=null,bookTitle=null,bookAuthor=null,msg=null,
	bookDescription=null,bookImage=null,bookEbook=null,bookDate=null,checkedBook=null;
	File bookText;
	FileReader bookCheck;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		JptomatoLogoActivity.actList.add(this);
		setContentView(R.layout.down);
		Intent fromInfo = getIntent();

		toInfoImage=fromInfo.getStringExtra("fromInfoImage");
		toInfoBook=fromInfo.getStringExtra("fromInfoBookId");
		Log.e("InfoBookValue",toInfoBook);
		toInfoUserId=fromInfo.getStringExtra("fromFileUserId");

		bookCover = (ImageView)findViewById(R.id.DOWN_BookImage);
		Drawable draw = loadDrawable(toInfoImage);
		bookCover.setImageDrawable(draw);

		OKBtn = (Button)findViewById(R.id.DOWN_OKBtn);
		CancelBtn = (Button)findViewById(R.id.DOWN_CancelBtn);


		OKBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				tryToLogin();


			}
		});

		CancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});   

	}
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

	public void tryToLogin() {

		//String theUrl = "http://pairiserver.appspot.com/kaka/android_login.jsp";
		String theUrl = "http://ebookserverhjy5.appspot.com/bookdownloder.jsp";
		Log.i(this.getLocalClassName(), theUrl);
		ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();

		httpParams.add(new BasicNameValuePair("email",toInfoUserId));
		httpParams.add(new BasicNameValuePair("bookid",toInfoBook));

		cmsHTTP cmsHttp = new cmsHTTP();
		//cmsHttp.encoding = encoding;
		cmsHttp.act = Down.this;
		Log.e("sending","sendpost");
		String tmpData = cmsHttp.sendPost(theUrl, httpParams);
		//		Log.e("postout",tmpData);
		if (tmpData == null)
		{
			return;
		}
		else
		{
			hm = cmsutil.xml2HashMap(tmpData, cmsHttp.encoding);
			Log.e("go result","go!");
			Log.v(this.getLocalClassName(), tmpData);
			addResult(hm);
		}

	}

	public void addResult(HashMap<String, String> hm) {
		int count = Integer.valueOf(hm.get("count"));
		Log.e("result_countinDown",String.valueOf(count));
		String[] bookId=new String[MAX],title=new String[MAX],author=new String[MAX],description=new String[MAX],image=new String[MAX],stock=new String[MAX];
		String[] resBookId=new String[MAX],resTitle=new String[MAX],resAuthor=new String[MAX],resDescription=new String[MAX],resImage=new String[MAX],resStock=new String[MAX];	
		String ebook,resEbook,img,resImg;
		Log.e("result_inStrFor","going");
		for(int i=0;i<count;i++)
		{
			bookId[i]=("id["+i+"]");
			Log.e("id",bookId[i].toString());
			title[i]="title["+i+"]";
			author[i]="author["+i+"]";
			description[i]="description["+i+"]";
			image[i]="imageurl["+i+"]";
			stock[i]="stock["+i+"]";
		}

		ebook="ebook["+(count-1)+"]";
		img="imageurl["+(count-1)+"]";
		Log.e("Download",img);
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));

		for(int i=0;i<count;i++)
		{
			Log.e("count_result",String.valueOf(i));
			resBookId[i] = hm.get(bookId[i]);
			resTitle[i] = hm.get(title[i]);
			resAuthor[i] = hm.get(author[i]);
			resDescription[i] = hm.get(description[i]);
			resImage[i] =hm.get(image[i]);
			resStock[i] = hm.get(stock[i]);
		}
		resEbook=hm.get(ebook);
		resImg=hm.get(img);
		if(checkBook())
		{
			CheckUtil result = new CheckUtil();	
			result.CheckResult(Down.this,5,msg,0);
		}
		else
		{
			try {
				saveFile(rowid,toInfoUserId,resBookId,resTitle,resAuthor,resDescription,resImage,resStock);
				saveBook(resEbook,count);
				SaveImg(resImg,count);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			new AlertDialog.Builder(Down.this)
			.setTitle("Notification")
			.setMessage("ダウンロードが完了しました。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent (Down.this,MyLibrary.class);
					intent.putExtra("State", "OK");
					startActivity(intent);
				}
			})
			.show();
		}
	}

	public void saveFile(int rowid,String userId, String[] bookId,String[] bookTitle
			,String[] author,String[] description,String[] image,String[] stock) throws IOException
			{
		int count = Integer.valueOf(hm.get("count"));
		userData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
		try
		{
			save[0] = new FileWriter(userData);
			if(rowid==6){save[0].write("1");}
			save[0].write(String.valueOf(rowid));
			save[0].write("\n");
			save[0].write(userId);
			save[0].write("\n");
			for(int i=0;i<=count;i++)
			{	
				if(i==count)
					save[0].write("\n");
				else
				{
					if(i==(count-1))
						save[0].write(bookId[i]);
					else
					{
						save[0].write(bookId[i]);
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
						save[0].write(bookTitle[i]);
					else
					{
						save[0].write(bookTitle[i]);
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
						save[0].write(image[i]);
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
						save[0].write(stock[i]);
					else
					{
						save[0].write(stock[i]);
						save[0].write(",");
					}

				}
			}	
			save[0].close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
			}
	public void saveBook(String ebook,int i) throws IOException
	{
		userData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"ebook_"+i+".ebf");	
		FileWriter tempSave = save[i]; 
		tempSave = new FileWriter(userData);
		tempSave.write(ebook);
		tempSave.close();	

	}
	void  SaveImg(String ImgUrl,int i)throws IOException
	{
	
		try
		{	
			String tmpurlStr = "http://www."+ImgUrl;
			String imageUrl=tmpurlStr.replace("@amp;", "&");
			
			URL url = new URL(imageUrl);
			InputStream is = url.openStream();
			
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"ebook_"+i+".jpg");
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			OutputStream filestream = null;
			filestream = new FileOutputStream(file);
			Log.e("Downimage","img");
			bitmap.compress(CompressFormat.JPEG, 100, filestream);
			
			filestream.flush();
			filestream.close();
			
		}
		catch(Exception e)
		{
			Log.e("URL","error,in load Drawable\n"+e.toString());
		}


	}
	public Boolean checkBook()
	{
		Boolean swit = false;
		msg = hm.get("msg[0]");
		String[] bookIdList= new String[100];
		String ckBook = null;
		bookText = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
		try {
			bookCheck = new FileReader(bookText);
			BufferedReader Br = new BufferedReader(bookCheck);
			for(int i=0;i<3;i++)
			{
				ckBook = Br.readLine();
			}
			Br.close();
			bookCheck.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bookIdList = ckBook.split(",");
		for (int i = 0 ; i<bookIdList.length;i++)
		{
			Log.e("bookInList1",bookIdList[i]);
			Log.e("bookInList2",toInfoBook);
			if(bookIdList[i].equals(toInfoBook))
			{
				swit = true;
				
			}
		}
		return swit;
	}
}

