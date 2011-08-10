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

	EditText EditID,EditPass,EditCheckPass;
	Button OKBtn;
	CheckUtil joinTest;
	Util cmsutil = new Util();
	HashMap<String, String> hm;
	String joinId,pass,checkPass,Jbook;
	File joinUserData;
	FileWriter joinSave;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);
		JptomatoLogoActivity.actList.add(this);
		EditID = (EditText)findViewById(R.id.Join_EditID);
		EditPass = (EditText)findViewById(R.id.Join_EditPass);
		EditCheckPass = (EditText)findViewById(R.id.Join_EditCheckPass);
		OKBtn = (Button)findViewById(R.id.Join_OKBtn);

		OKBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				tryToLogin();

			}
		});

	}

	public void tryToLogin() 
	{
		
		joinId = EditID.getText().toString();
		pass = EditPass.getText().toString();
		checkPass = EditCheckPass.getText().toString();
		joinTest = new CheckUtil(Join.this,joinId,pass,checkPass,true);
		if (joinTest.checkStart())
		{
		//	String theUrl = "http://pairiserver.appspot.com/kaka/android_rejister.jsp";
			String theUrl = "http://ebookserverhjy5.appspot.com/android_rejister.jsp";
			Log.i(this.getLocalClassName(), theUrl);
			ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
			httpParams.add(new BasicNameValuePair("email",joinId));
			httpParams.add(new BasicNameValuePair("pass", pass));
			
			cmsHTTP cmsHttp = new cmsHTTP();
			//cmsHttp.encoding = encoding;
			cmsHttp.act = Join.this;
			String tmpData = cmsHttp.sendPost(theUrl, httpParams);
			if (tmpData == null)
			{
				return;
			}
			else
			{
				hm = cmsutil.xml2HashMap(tmpData, cmsHttp.encoding);
				Log.v(this.getLocalClassName(), tmpData);
				addResult();
			}
		}
	}
	public void addResult() {
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));
		String book = hm.get("id[0]");
		String title = hm.get("title[0]");
		String author = hm.get("author[0]");
		String description = hm.get("description[0]");
		String image = hm.get("imageurl[0]");
		String ebook = hm.get("ebook[0]");
		String date = hm.get("date[0]");
		String msg = hm.get("msg[0]");
		if(rowid==1||rowid==6)
		{
			try {
				Log.e("Join","savefile");
					saveFile(rowid,joinId, book,title,author,description,image,ebook,date);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.e("Join","savefinish");
		
		CheckUtil jo_result = new CheckUtil();
		jo_result.CheckResult(this, rowid, msg,0);
	}

	public void saveFile(int rowid,String usrID,String id,String title,String author,String description,String imgurl,String ebook,String date) throws IOException
	{
		
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
			joinSave.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
