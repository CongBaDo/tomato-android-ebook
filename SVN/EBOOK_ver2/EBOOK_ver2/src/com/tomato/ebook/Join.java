package com.tomato.ebook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.tomato.communication.CheckUtil;
import com.tomato.communication.Util;
import com.tomato.communication.cmsHTTP;

import android.util.Log;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Join extends Activity{

	HashMap<String, String> hm;
	String joinId,pass,checkPass,Jbook;

	File joinUserData;
	FileWriter joinSave;
	
	CheckUtil joinTest;
	Util cmsutil = new Util();
	
	EditText EditID,EditPass,EditCheckPass;
	Button OKBtn;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		JptomatoLogoActivity.actList.add(this);//"プログラムの終了"のため、Activityを追加
	
		EditID = (EditText)findViewById(R.id.Join_EditID);//「Email」エディトボックス
		EditPass = (EditText)findViewById(R.id.Join_EditPass);//「P/W」エディトボックス
		EditCheckPass = (EditText)findViewById(R.id.Join_EditCheckPass);//「一度P/Wを確認しｌます。」エディトボックス
		OKBtn = (Button)findViewById(R.id.Join_OKBtn);//「確認」ボタン
		
		//////////////////////////////////////////////////
		//
		// 「登録」ボタン押下時の処理
		//
		//////////////////////////////////////////////////
		OKBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				tryToLogin();//サーバーと通信します。

			}
		});

	}

	//////////////////////////////////////////////////
	//
	// サーバーと通信して、データを引き出し
	//
	//////////////////////////////////////////////////
	public void tryToLogin() 
	{
		//ユーザーが入力した各部分を読み込む		
		joinId = EditID.getText().toString();//Email
		pass = EditPass.getText().toString();//P/W
		checkPass = EditCheckPass.getText().toString();//P/W確認
	
		joinTest = new CheckUtil(Join.this,joinId,pass,checkPass,true);
		//Emailと秘密番号が全部入力した場合
		if (joinTest.checkStart())
		{
			//解任登録を担当するサーバーの住所
			String theUrl = "http://ebookserverhjy5.appspot.com/android_rejister.jsp";
			
			//サーバーに要請するデータ目録を作成する	
			ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
			
			//ユーザーのID
			httpParams.add(new BasicNameValuePair("email",joinId));
			//ユーザーの秘密番号
			httpParams.add(new BasicNameValuePair("pass", pass));
			
			cmsHTTP cmsHttp = new cmsHTTP();//接続準備
			cmsHttp.act = Join.this;
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
				addResult();
			}//else end
			
		}//if end
	}
	
	//////////////////////////////////////////////////
	//
	// サーバーへ要請したデータを使って、ファイルを作成
	//
	//////////////////////////////////////////////////
	public void addResult() {
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));//ユーザーの状態データを引き出し
		String book = hm.get("id[0]");//一番目の本のIDデータを引き出し
		String title = hm.get("title[0]");//一番目の本の名前データを引き出し
		String author = hm.get("author[0]");//一番目の本の著者データを引き出し
		String description = hm.get("description[0]");//一番目の本の説明データを引き出し
		String image = hm.get("imageurl[0]");//一番目の本のイメージの住所データを引き出し
		String ebook = hm.get("ebook[0]");//一番目の本の内容データを引き出し
		String date = hm.get("date[0]");//一番目の本本がサーバーへ登録された日を引き出し
		String msg = hm.get("msg[0]");//rowidに対して、サーバーが送るメッセージを引き出し

		try {
			//"login.txt"ファイルを作成
			saveFile(rowid,joinId, book,title,author,description,image,ebook,date);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CheckUtil jo_result = new CheckUtil();
		jo_result.CheckResult(this, rowid, msg,0);//rowidに対して、サーバーが送メッセージを表示する。
	}
	
	//////////////////////////////////////////////////
	//
	// "login.txt"ファイルを作成
	//
	//////////////////////////////////////////////////
	public void saveFile(int rowid,String usrID,String id,String title,String author,String description,String imgurl,String ebook,String date) throws IOException
	{
		//保存するファイルを開ける。
		joinUserData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
		try
		{
			joinSave = new FileWriter(joinUserData);
			joinSave.write(String.valueOf(rowid));
			joinSave.write("\n");
			joinSave.write(usrID);
			joinSave.write("\n");
			joinSave.write(id);
			joinSave.write("\n");
			joinSave.write(title);
			joinSave.write("\n");
			joinSave.write(author);
			joinSave.write("\n");
			joinSave.write(description);
			joinSave.write("\n");
			joinSave.write(imgurl);
			joinSave.write("\n");
			joinSave.write(ebook);
			joinSave.write("\n");
			joinSave.write(date);
			joinSave.close();//保存するファイルを閉める。
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
