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

	static final int MAX = 100;
	String toInfoImage=null,
			toInfoBook=null,
			toInfoUserId=null,
			bookId=null,
			bookTitle=null,
			bookAuthor=null,
			msg=null,
			bookDescription=null,
			bookImage=null,
			bookEbook=null,
			bookDate=null,
			checkedBook=null;
	
	File bookText,userData;
	FileReader bookCheck;
	FileWriter[] save = new FileWriter[MAX];

	CheckUtil logTest;
	Util cmsutil = new Util();
	Activity act = this;
	HashMap<String, String> hm;
	
	EditText EditID,EditPass;
	Button LogBtn,TorokuBtn;
	Button OKBtn,CancelBtn;
	ImageView bookCover;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Activityの追加
		JptomatoLogoActivity.actList.add(this);
		setContentView(R.layout.down);
		
		Intent fromInfo = getIntent();//"Genre.javaか"らインデントでもらったデータを引き出し

		toInfoImage=fromInfo.getStringExtra("fromInfoImage");//本のイメージ住所
		toInfoBook=fromInfo.getStringExtra("fromInfoBookId");//本のID
		toInfoUserId=fromInfo.getStringExtra("fromFileUserId");//ユーザーのID

		bookCover = (ImageView)findViewById(R.id.DOWN_BookImage);
		Drawable draw = loadDrawable(toInfoImage);
		bookCover.setImageDrawable(draw);

		OKBtn = (Button)findViewById(R.id.DOWN_OKBtn);//「はい」ボタン
		CancelBtn = (Button)findViewById(R.id.DOWN_CancelBtn);//「いいえ」ボタン

		//////////////////////////////////////////////////
		//
		// 「はい」ボタン押下時の処理
		//
		//////////////////////////////////////////////////
		OKBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tryToLogin();//サーバーと通信します。
			}
		});

		//////////////////////////////////////////////////
		//
		// 「いいえ」ボタン押下時の処理
		//
		//////////////////////////////////////////////////
		CancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();//今の画面を消して、以前の本の詳細情報画面に戻ります
			}
		});   

	}
	
	//////////////////////////////////////////////////
	//
	// イメージの住所を使って、イメージ・データを読み込む
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

	//////////////////////////////////////////////////
	//
	// サーバーと通信して、データを引き出し
	//
	//////////////////////////////////////////////////
	public void tryToLogin() {

		//ダウンロードを担当するサーバーの住所
		String theUrl = "http://ebookserverhjy5.appspot.com/bookdownloder.jsp";
		Log.i(this.getLocalClassName(), theUrl);
		
		//サーバーに要請するデータ目録を作成する
		ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
		//ユーザーのID														
		httpParams.add(new BasicNameValuePair("email",toInfoUserId));
		//本のID
		httpParams.add(new BasicNameValuePair("bookid",toInfoBook));

		cmsHTTP cmsHttp = new cmsHTTP();//接続準備
		//cmsHttp.encoding = encoding;
		cmsHttp.act = Down.this;
		
		String tmpData = cmsHttp.sendPost(theUrl, httpParams);//サーバーへデータを要請

		//サーバーから戻り値がない場合
		if (tmpData == null)
		{
			return;
		}//if end
		
		//戻り値が有る場合
		else
		{
			//サーバーへ要請したデータを保存
			hm = cmsutil.xml2HashMap(tmpData, cmsHttp.encoding);
			Log.v(this.getLocalClassName(), tmpData);
			addResult(hm);
		}//else end

	}

	//////////////////////////////////////////////////
	//
	// サーバーへ要請したデータを使って、ファイルを作成
	//
	//////////////////////////////////////////////////
	public void addResult(HashMap<String, String> hm) {
		int count = Integer.valueOf(hm.get("count"));
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));

		String[] bookId=new String[MAX],
				title=new String[MAX],
				author=new String[MAX],
				description=new String[MAX],
				image=new String[MAX],
				stock=new String[MAX],
				resBookId=new String[MAX],
				resTitle=new String[MAX],
				resAuthor=new String[MAX],
				resDescription=new String[MAX],
				resImage=new String[MAX],
				resStock=new String[MAX];
		
		String ebook,resEbook,img,resImg;
		
		//ユーザーがダウンロードした本が一巻の場合
		if(count==0)
		{
			resBookId[0] = hm.get("id[0]");
			resTitle[0] = hm.get("title[0]");
			resAuthor[0] = hm.get("author[0]");
			resDescription[0] = hm.get("description[0]");
			resImage[0] =hm.get("imageurl[0]");
			resStock[0] =hm.get("stock[0]");
			resEbook=hm.get("ebook[0]");
			try {
				saveFile(rowid,toInfoUserId,resBookId,resTitle,resAuthor,resDescription,resImage,resStock);
				saveBook(resEbook,count+1);
				SaveImg(resImage[0],count+1);

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
		}//if end
		
		/*  ユーザーがダウンロードした本が一巻以上の場合		
		 *  以前のユーザー・データを更新して、新しい本を追加する
		 */
		else
		{
			//サーバーへ要請したデータを保存する変数"hm"からデータを引き出す構文を作る。
			for(int i=0;i<count;i++)
			{
				bookId[i]=("id["+i+"]");
				title[i]="title["+i+"]";
				author[i]="author["+i+"]";
				description[i]="description["+i+"]";
				image[i]="imageurl["+i+"]";
				stock[i]="stock["+i+"]";
			}//for i end
				ebook="ebook["+(count-1)+"]";
				img="imageurl["+(count-1)+"]";
				
			//サーバーへ要請したデータを保存する変数"hm"からデータを引き出して各データ別に保存。
			for(int j=0;j<count;j++)
			{
				resBookId[j] = hm.get(bookId[j]);
				resTitle[j] = hm.get(title[j]);
				resAuthor[j] = hm.get(author[j]);
				resDescription[j] = hm.get(description[j]);
				resImage[j] =hm.get(image[j]);
				resStock[j] = hm.get(stock[j]);
			}//for j end
			
			resEbook=hm.get(ebook);
			resImg=hm.get(img);

			//同じ本がいる場合
			if(checkBook())
			{
				CheckUtil result = new CheckUtil();	
				result.CheckResult(Down.this,5,msg,0);//rowidに対して、サーバーが送メッセージを表示する。
			}//if end
			
			//同じ本がイない場合
			else
			{
				try {
					//"login.txt"を作成します。
					saveFile(rowid,toInfoUserId,resBookId,resTitle,resAuthor,resDescription,resImage,resStock);
					
					saveBook(resEbook,count);//ダウンロードした本を保存。
					SaveImg(resImg,count);//本のイメージを保存

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
						finish();
						Intent intent = new Intent (Down.this,MyLibrary.class);
						intent.putExtra("State", "OK");
						startActivity(intent);//書斎画面へ戻ります。
					}
				})
				.show();
			}//else end
			
		}//else end
	}//addResult(HashMap<String, String> hm) end

	//////////////////////////////////////////////////
	//
	// "login.txt"ファイルを作成
	//
	//////////////////////////////////////////////////
	public void saveFile(int rowid,String userId, String[] bookId,String[] bookTitle
			,String[] author,String[] description,String[] image,String[] stock) throws IOException
	{
		int count = Integer.valueOf(hm.get("count"));

		//保存するファイルを開ける。
		userData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");

		try
		{

			save[0] = new FileWriter(userData);
			save[0].write(String.valueOf(rowid));
			save[0].write("\n");
			save[0].write(userId);
			save[0].write("\n");

			//ユーザーがダウンロードした本が一巻の場合		
			if(count==0)
			{

				save[0].write(bookId[0]);
				save[0].write("\n");
				save[0].write(bookTitle[0]);
				save[0].write("\n");
				save[0].write(author[0]);
				save[0].write("\n");
				save[0].write(description[0]);
				save[0].write("\n");
				save[0].write(image[0]);
				save[0].write("\n");
				save[0].write(stock[0]);
				save[0].write("\n");

			}//if end
			
			//ユーザーがダウンロードした本が一巻以上の場合		
			else
			{
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
						}//else end
						
					}//else end	
					
				}//for i end

				for(int j=0;j<=count;j++)
				{
					if(j==count)
						save[0].write("\n");
					
					else
					{
						if(j==(count-1))
							save[0].write(bookTitle[j]);
					
						else
						{
							save[0].write(bookTitle[j]);
							save[0].write(",");
						}//else end

					}//else end	
					
				}//for j end
				
				for(int k=0;k<=count;k++)
				{
					if(k==count)
						save[0].write("\n");
					
					else
					{
						if(k==(count-1))
							save[0].write(author[k]);
						
						else
						{
							save[0].write(author[k]);
							save[0].write(",");	
						}//else end

					}//else end
					
				}//for k end
				
				for(int l=0;l<=count;l++)
				{

					if(l==count)
						save[0].write("\n");
					
					else
					{
						if(l==(count-1))
							save[0].write(description[l]);
					
						else
						{
							save[0].write(description[l]);
							save[0].write(",");
						}//else end

					}//else end
					
				}//for l end
				
				for(int m=0;m<=count;m++)
				{

					if(m==count)
						save[0].write("\n");
					else
					{
						if(m==(count-1))
							save[0].write(image[m]);
						else
						{
							save[0].write(image[m]);
							save[0].write(",");
						}//else end

					}//else end
					
				}//for m end	
				
				for(int n=0;n<=count;n++)
				{

					if(n==count)
						save[0].write("\n");
					else
					{
						if(n==(count-1))
							save[0].write(stock[n]);
						else
						{
							save[0].write(stock[n]);
							save[0].write(",");
						}//else end

					}//else end
					
				}//for n end
				
			}//else end
			
			save[0].close();//保存するファイルを閉める。
			
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

