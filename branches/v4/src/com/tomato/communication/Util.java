package com.tomato.communication;

import com.tomato.communication.cmsHTTP;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.app.Activity;
import android.database.Cursor;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;


public class Util {

	public Util() {

	}

	public Activity act;
	public String TAG = "com.owl.app.cms.util";

	public Util(Activity tmpact) {
		act = tmpact;
	}
	
	
	
	public HashMap<String, String> cursor2HashMap(Cursor cursor) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("count", "0");
		cursor.getColumnNames();
		try {
			int i = 0;
			while (cursor.moveToNext()) {
				for (int j = 0; j < cursor.getColumnCount(); j++) {
					String fieldName = cursor.getColumnName(j);
					String fieldValue = cursor.getString(j);
					hm.put(fieldName + "[" + i + "]", fieldValue);
				}// for j
				i++;
			}// while
			hm.put("count", Integer.toString(i));
		} catch (Exception e) {
			Log.e("cursor2HashMap", e.getMessage());
		}
		return hm;
	}
	

	public HashMap<String, String> hmMyLocation = new HashMap<String, String>();

	public HashMap<String, String> getAdminInfo() {
		Log.v(TAG, "getAdminInfo:");
		HashMap<String, String> hmAdmin = new HashMap<String, String>();
		if (act == null)
			return hmAdmin;

		String theUrl = "http://www.owllab.com/android/admin_info.php";
		Log.i(act.getLocalClassName(), theUrl);
		ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
		httpParams.add(new BasicNameValuePair("mode", "admin_tel"));
		cmsHTTP cmsHttp = new cmsHTTP();
		// cmsHttp.encoding = encoding;
		cmsHttp.act = act;
		String tmpData = cmsHttp.sendPost(theUrl, httpParams);
		if (tmpData == null)
			return hmAdmin;
		hmAdmin = xml2HashMap(tmpData, cmsHttp.encoding);
		Log.v(act.getLocalClassName(), tmpData);
		return hmAdmin;
	}

	public String getAuthID(Activity act) {
		String tmp = "";
		HashMap<String, String> hm = ((owllab) act.getApplication()).authHM;
		tmp = null2empty(hm.get("id[0]"));
		return tmp;
	}

	public boolean getLoginState(Activity act) {
		boolean tmp = false;
		if (getAuthID(act).length() > 0)
			tmp = true;
		return tmp;
	}

	public int getAuthLevel(Activity act) {
		int tmp = -1;
		HashMap<String, String> hm = ((owllab) act.getApplication()).authHM;
		tmp = str2int(hm.get("level[0]"), -1);
		return tmp;
	}

	public HashMap<String, String> getAuthHM(Activity act) {
		HashMap<String, String> hm = ((owllab) act.getApplication()).authHM;
		return hm;
	}

	public void setAuthHM(Activity act, HashMap<String, String> hm) {
		((owllab) act.getApplication()).authHM = hm;
	}

	public InputFilter filterAlphaNum = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			}
			return null;
		}
	};

	public InputFilter filterJavaLetterOrDigit = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; i++) {
				if (!Character.isJavaLetterOrDigit(source.charAt(i))) {
					return "";
				}
			}
			return null;
		}
	};

	public InputFilter filterLetterNum = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

			// Pattern ps=Pattern.compile("[a-zA-Z0-9가-R]*");
			// if (!ps.matcher(source).matches()) {
			// return "";
			// }
			for (int i = start; i < end; i++) {
				if (!Character.isLetterOrDigit(source.charAt(i))) {
					return "";
				}
			}
			return null;
		}
	};

	public InputFilter filterLetter = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; i++) {
				if (!Character.isLetter(source.charAt(i))) {
					return "";
				}
			}
			return null;
		}
	};

	public InputFilter filterNum = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int i = start; i < end; i++) {
				if (!Character.isDigit(source.charAt(i))) {
					return "";
				}
			}
			return null;
		}
	};

	String[] tagList = {"rowid","email","pwd","ebook","date","description","title","id","author","image","level","msg"};
	public HashMap<String, String> xml2HashMap(String tmpData, String encoding) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("count", "0");
		try {
			DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
			DocumentBuilder docB = docBF.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(tmpData.getBytes("UTF-8"));
			Document doc = docB.parse(is);
			Element lists = doc.getDocumentElement();
			NodeList dataList = lists.getElementsByTagName("data");
			int c = 0;
			for (int i = 0; i < dataList.getLength(); i++) 
			{
				NodeList dataNodeList = dataList.item(i).getChildNodes();
				Log.e("length",String.valueOf(dataNodeList.getLength()));
				for (int j = 0; j < dataNodeList.getLength(); j++) 
				{	
					//;
					Node itemNode = dataNodeList.item(j);
					if (itemNode.getFirstChild() != null) 
					{
						String nodeName = itemNode.getNodeName();
						String nodeValue = itemNode.getFirstChild()
								.getNodeValue();
						hm.put(nodeName + "[" + i + "]", nodeValue);
						Log.e("name",nodeName+i);
						Log.e("value",nodeValue);	
					}
				}// for j
				c++;
			}// for i
			Log.e("hm_data0",hm.get("title[0]"));
			Log.e("hm_data1",hm.get("title[1]"));
			hm.put("count", Integer.toString(c));
			Log.e("count",Integer.toString(c));
		} catch (Exception e) {
			Log.e("com.cms.app.util.xml2HashMap", e.getMessage());
		}
		return hm;
	}

	public int str2int(String txt, int mydefault) {
		int num = 0;
		if (txt == null || "".equals(txt)) {
			num = mydefault;
		} else {
			try {
				num = Integer.parseInt(txt);
			} catch (NumberFormatException e) {
				Log.e(TAG,e.toString());
			}
		}
		return num;
	}

	public int str2int(String txt) {
		int num = 0;
		if (txt == null || "".equals(txt)) {
		} 
		else 
		{
			num = double2int(txt);
		}
		return num;
	}

	public int double2int(double val) {
		int tmp = 0;
		Double d = new Double(val);
		tmp = d.intValue();
		return tmp;
	}

	public int double2int(String val) {
		int tmp = 0;
		Double d = new Double(val);
		tmp = d.intValue();
		return tmp;
	}

	public double str2double(String txt) {
		double num = 0;
		if (txt == null || "".equals(txt)) {
			num = 0.0;
		} else {
			num = Double.valueOf(txt).doubleValue();
		}
		return num;
	}

	public long str2long(String txt) {
		long num = 0;
		if (txt == null || "".equals(txt)) {
			num = 0;
		} else {
			num = Long.valueOf(txt).longValue();
		}
		return num;
	}

	public String str_replace(String src, String des, String org) {
		int fromindex = 0;
		int toindex = 0;
		String replaced = "";
		int i = 0;
		if ("".equals(src) || src == null) {
			replaced = org;
		} else {
			while (fromindex >= 0) {
				if (i == 0) {
					toindex = org.indexOf(src, 0);
					if (toindex < 0) {
						replaced = org.substring(0, org.length());
						break;
					} else {
						replaced = org.substring(0, toindex);
						replaced += des;
					}
				} else {
					toindex = org.indexOf(src, fromindex + src.length());
					if (toindex < 0) {
						replaced += org.substring(fromindex + src.length(), org
								.length());
						break;
					} else {
						replaced += org.substring(fromindex + src.length(),
								toindex);
						replaced += des;
					}
				}
				fromindex = toindex;
				i++;
			}
		}// if
		return replaced;
	}

	public String str_replace_i(String src, String des, String org) {

		String org_upper = org.toUpperCase();
		String src_upper = src.toUpperCase();
		int fromindex = 0;
		int toindex = 0;
		String replaced = "";
		int i = 0;
		if ("".equals(src) || src == null) {
			replaced = org;
		} else {
			while (fromindex >= 0) {
				if (i == 0) {
					toindex = org_upper.indexOf(src_upper, 0);
					if (toindex < 0) {
						replaced = org.substring(0, org_upper.length());
						break;
					} else {
						replaced = org.substring(0, toindex);
						replaced += des;
					}
				} else {
					toindex = org_upper.indexOf(src_upper, fromindex
							+ src.length());
					if (toindex < 0) {
						replaced += org.substring(fromindex + src.length(), org
								.length());
						break;
					} else {
						replaced += org.substring(fromindex + src.length(),
								toindex);
						replaced += des;
					}
				}
				fromindex = toindex;
				i++;
			}
		}
		return replaced;

	}

	public String null2empty(String str) {
		if (str == null)
			str = "";
		return str;
	}
	
	public String[] explode_trim(String src,String org) {
		
		String[] tmpa = explode(src,org);
		String[] tmpb;
		if (tmpa!=null) {
			tmpb = new String[tmpa.length];
			for (int i=0;i<tmpb.length;i++) {
				tmpb[i] = tmpa[i].trim();
			}//for i
			return tmpb;
		}
		return tmpa;
	}
	
	public String[] explode_trim(String src,String org,int limit) {
		
		String[] tmpa = explode(src,org);
		String[] tmpb = new String[limit];
		for (int i=0;i<limit;i++) {
			if (i>=tmpa.length) {
				tmpb[i] = "";
			} else {
				tmpb[i] = tmpa[i].trim();
			}//if
		}//for i
		return tmpb;
	}

	public String[] explode_trim(String src,String org,int limit,int didx) {
		
		String[] tmpa = explode(src,org);
		String[] tmpb = new String[limit];
		for (int i=0;i<limit;i++) {
			if (i>=tmpa.length) {
				tmpb[i] = tmpa[didx];
			} else {
				tmpb[i] = tmpa[i].trim();
			}//if
		}//for i
		return tmpb;
	}

	public String[] explode(String src,String org,int limit) {
		
		String[] tmpa = explode(src,org);
		String[] tmpb = new String[limit];
		for (int i=0;i<limit;i++) {
			if (i>=tmpa.length) {
				tmpb[i] = "";
			} else {
				tmpb[i] = tmpa[i];
			}//if
		}//for i
		return tmpb;
	}

	public String[] explode(String src,String org,int limit,int didx) {
		
		String[] tmpa = explode(src,org);
		String[] tmpb = new String[limit];
		for (int i=0;i<limit;i++) {
			if (i>=tmpa.length) {
				tmpb[i] = tmpa[didx];
			} else {
				tmpb[i] = tmpa[i];
			}//if
		}//for i
		return tmpb;	
	}

	public String[] explode(String src,String org){
		int fromindex = 0;
		int toindex = 0;
		int i=0;
		Vector<String> v = new Vector<String>();
		if ("".equals(src) || src==null) {
			v.addElement(new String(org));
		} else {
			while(fromindex >= 0){
				if (i==0){
					toindex = org.indexOf(src,0);
					if (toindex < 0){
						v.addElement(new String(org.substring(0,org.length())));
						break;
					} else {
						v.addElement(new String(org.substring(0,toindex)));
					}
				} else {
					toindex = org.indexOf(src, fromindex+src.length());
					if (toindex < 0){
						v.addElement(new String(org.substring(fromindex+src.length(),org.length())));
						break;
					} else {
						v.addElement(new String(org.substring(fromindex+src.length(),toindex)));
					}
				}
				fromindex = toindex;
			i++;}
		}
		//Object[] myarray = v.toArray();
		String[] myarray = new String[v.size()];
		for (i=0;i<myarray.length;i++) {
			myarray[i] = (String)v.elementAt(i);
		}//for i
		return myarray;
	}
}
