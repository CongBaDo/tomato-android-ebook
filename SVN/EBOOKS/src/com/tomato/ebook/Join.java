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
			httpParams.add(new BasicNameValuePair("book", Jbook));
			
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
		String msg = hm.get("msg[0]");
		String book = hm.get("book[0]");
		if(rowid==1)
		{
			try {
					saveFile(rowid,joinId, book);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CheckUtil jo_result = new CheckUtil();
		jo_result.CheckResult(this, rowid, msg,0);
	}

	public void saveFile(int rowid,String id,String book) throws IOException
	{
		
		joinUserData = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"login.txt");
		try
		{
			joinSave = new FileWriter(joinUserData);
			joinSave.write(String.valueOf(rowid));
			joinSave.write("\n");
			joinSave.write(id);
			joinSave.write("\n");
			joinSave.write(book);		
			joinSave.close();
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
