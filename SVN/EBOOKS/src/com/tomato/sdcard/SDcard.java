package com.tomato.sdcard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.Environment;
import android.util.Log;

import com.tomato.communication.Util;
import com.tomato.communication.cmsHTTP;

public class SDcard {

	
//	String ext=Environment.getExternalStorageState();
	String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
	File userData;
	FileWriter save;
	String data=null;
	StringBuffer redata=null; 	
	Util cmsutil = new Util();
	ArrayList<ArrayList<String>> book=new ArrayList<ArrayList<String>>();
	ArrayList<String> page=new ArrayList<String>();
	ArrayList<String> data_list=new ArrayList<String>();
	String id=null,genre=null,title=null,author=null,description=null,ebook=null,count=null,date=null;
	HashMap<String, String> hm=new HashMap<String, String>();

	//////////////////////////////
	public ArrayList<String> tryToMyLibrary() {
		
		try {
			Log.e("tryToMyLibrary", "1");

			File file=new File(sdPath, "login.txt");
//			File file=new File(sdPath, "test.txt");
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);

			Log.e("tryToMyLibrary", "2");
			
			while ((data=br.readLine())!=null) {
				

				Log.e("br.readLine", data);
				data_list.add(data);
			
			}//end while
			Log.e("tryToMyLibrary", "3");
			
		} catch (Exception e) {
			Log.e("Login.txt", "loading error");
		}
		
		return data_list;
	}
	
	///////////////////////////////
	
	public String[] imageCount(){
		ArrayList<String> data=tryToMyLibrary();
		String imageTest=data.get(6);
		String imageUrl=imageTest.replace("@amp;", "&");
		String[] imageSplit=imageUrl.split(",");
		
	return imageSplit;
	}
	
	/////////////////////////////////
	public ArrayList<ArrayList<String>> dataload(String bookKey){
	
		Log.e("book", bookKey);
		try {
			File file=new File(sdPath, "ebook_"+bookKey+".ebf");
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			
			Log.e("dataload", "11");
				
			while((data=br.readLine())!=null){
				page.add(data);			
				if (data.equals("@")) {
					book.add(page);
					page=new ArrayList<String>();
				}
			}//end while

		} catch (Exception e) {
			Log.e("2222222222", "222222211111111");
		}		

		Log.e("book.size",book.size()+"");
		
		return book;
	}


	///////////////////////////////////////////////////

	public void readXML(String key) {

		String theUrl = "http://ebookssongs2.appspot.com/ebookSelectList.jsp";
		Log.e("readxml1", theUrl);
		ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
		httpParams.add(new BasicNameValuePair("key",key));

		Log.e("readxml2", theUrl);

		cmsHTTP cmsHttp = new cmsHTTP();
		//cmsHttp.encoding = encoding;
		//			cmsHttp.act = Login.this;
		String tmpData = cmsHttp.sendPost(theUrl, httpParams);
		if (tmpData == null)
		{
			Log.e("null", "null");
		}
		else
		{
			Log.e("notnull", "notnull");

			hm = cmsutil.xml2HashMap(tmpData, cmsHttp.encoding);
			//				Log.v(this.getLocalClassName(), tmpData);
			
			Log.e("notnull2", "notnull2");

			addResult();
		}
	}

	public void addResult() {

		//		int stock = cmsutil.str2int(hm.get("stock[0]"));
		
		id = hm.get("id[0]");
		genre = hm.get("genre[0]");
		title = hm.get("title[0]");
		author = hm.get("author[0]");
		description = hm.get("description[0]");
		ebook = hm.get("ebook[0]");
		count = hm.get("count[0]");
		date = hm.get("date[0]");		



		Log.e("add", genre);

		//		return hm;
	}

	

}
