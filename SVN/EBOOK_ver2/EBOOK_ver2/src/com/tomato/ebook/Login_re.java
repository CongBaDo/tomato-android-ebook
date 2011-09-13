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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Login_re extends Activity {
	CheckBox Loginck;
	CheckUtil logTest;
	// サーバーからもらった"xml"データを保存する資料型
	HashMap<String, String> hm;

	ConnectivityManager cManager;
	NetworkInfo mobile;
	NetworkInfo wifi;

	Util cmsutil = new Util();
	Activity act = this;

	File userData, userText, userCheck;
	FileWriter[] save = new FileWriter[MAX];
	FileReader idCheck;

	static final int MAX = 100;
	static int bookCounter = 1;

	private String[] id = new String[MAX], pwd = new String[MAX],
			title = new String[MAX], author = new String[MAX],
			description = new String[MAX], image = new String[MAX],
			ebook = new String[MAX], date = new String[MAX],
			resId = new String[MAX], resTitle = new String[MAX],
			resAuthor = new String[MAX], resDescription = new String[MAX],
			resImage = new String[MAX], resEbook = new String[MAX],
			resDate = new String[MAX];

	String email, pass, bookId, bookTitle, bookAuthor, bookDescription,
			bookImage, bookEbook, bookDate, userId = null;

	/** Called when the activity is first created. */
	private Button btnServer, btnSyosai, btnTouroku;
	private EditText etId, etPw;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_re);

		etId = (EditText) findViewById(R.id.etId);
		etPw = (EditText) findViewById(R.id.etPw);

		btnServer = (Button) findViewById(R.id.btServer);
		btnSyosai = (Button) findViewById(R.id.btSyosai);
		btnTouroku = (Button) findViewById(R.id.btTouroku);

		etId = (EditText) findViewById(R.id.etId);
		etPw = (EditText) findViewById(R.id.etPw);

		Loginck = (CheckBox) findViewById(R.id.Login_checkBox);// 　「サーバーへ接続しない」チェックボックス

		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// 3GとWiFiの環境が出来ない場合
		if (!mobile.isConnected() && !wifi.isConnected()) {

			etId.setEnabled(false);
			etPw.setEnabled(false);
			Loginck.setChecked(true);
			btnServer.setEnabled(false);
		}

		// ////////////////////////////////////////////////
		//
		// 「サーバへ接続しない」チェックボックス押下時の処理
		//
		// ////////////////////////////////////////////////
		Loginck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (Loginck.isChecked()) {
					etId.setEnabled(false);
					etPw.setEnabled(false);
					btnServer.setEnabled(false);
				} else {
					etId.setEnabled(true);
					etPw.setEnabled(true);
					btnServer.setEnabled(true);
				}
			}
		});

		// ////////////////////////////////////////////////
		//
		// 「サーバへ接続」ボタン押下時の処理
		//
		// ////////////////////////////////////////////////
		btnServer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				Intent muyonggak = new Intent(Login_re.this,CustomDialog.class);
				startActivity(muyonggak);
				*/
//	i will be back キムセファデザイン訂正のためこっちを消す	
	// TODO Auto-generated method stub
				new AlertDialog.Builder(Login_re.this)
						.setTitle("Notification")
						.setMessage("サーバーへ接続します。\n少々お待ち下さい。")
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										tryToLogin(); // サーバーと通信します。
									}
								}).show();
								
			}
		});// LogBtn.setOnClickListener end

		// ////////////////////////////////////////////////
		//
		// 「書斎へ」ボタン押下時の処理
		//
		// ////////////////////////////////////////////////
		btnSyosai.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				email = etId.getText().toString();
				userCheck = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath(), "login.txt");
				// ユーザーのデータが存在しない場合
				if (!userCheck.exists() || userCheck.length() == 0
						|| !userCheck.canRead()) {
					new AlertDialog.Builder(Login_re.this)
							.setTitle("Notification")
							.setMessage("ユーザーのデータが存在しないです。\n ログインして下さい。")
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
										}
									}).show();
				}// if end
					// ---もしユーザーのデータが存在したら、ユーザーのデータを読み込み---//
				else {
					try {
						idCheck = new FileReader(userCheck);
						BufferedReader Br = new BufferedReader(idCheck);
						for (int i = 0; i < 2; i++) {
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
					new AlertDialog.Builder(Login_re.this)
							.setTitle("Notification")
							.setMessage(
									"ユーザーのデータが確認しました。\n書斎へ移動します。\n只、 ストアを利用しようとすれば、ログインがひつようです。")
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											Intent intent = new Intent(
													Login_re.this,
													Main_re.class);
											// Intent.putExtra("State", "not");
											startActivity(intent);

										}
									}).show();
				}// else end
			}// onClick end
		});// SyosaiBtn.setOnClickListener end

		// ////////////////////////////////////////////////
		//
		// 「登録」ボタン押下時の処理
		//
		// ////////////////////////////////////////////////
		btnTouroku.setOnClickListener(new View.OnClickListener() {
			@Override
			// TODO Auto-generated method stub
			public void onClick(View v) {

				Intent Intent = new Intent(Login_re.this, Join.class);
				startActivity(Intent);

			}
		});
	}// TorokuBtn.setOnClickListener end

	// ////////////////////////////////////////////////
	//
	// サーバーと通信して、データを引き出し
	//
	// ////////////////////////////////////////////////
	public void tryToLogin() {
		// ユーザーが入力したEmailと秘密番号を読み込む
		email = etId.getText().toString();
		pass = etPw.getText().toString();

		logTest = new CheckUtil(Login_re.this, email, pass);
		// Emailと秘密番号が全部入力した場合
		if (logTest.checkStart()) {
			// ログインを担当するサーバーの住所
			String theUrl = "http://ebookserverhjy5.appspot.com/android_login.jsp";

			// サーバーに要請するデータ目録を作成する
			ArrayList<NameValuePair> httpParams = new ArrayList<NameValuePair>();
			/*
			 * / ユーザーの状態ID。 / 本をダウンロードし無いなら"6"、ダウンロードした本が有ったら"1"、 /
			 * IDと秘密番号が間違ったら"3"です。
			 */
			httpParams.add(new BasicNameValuePair("rowid", email));

			// ユーザーのID
			httpParams.add(new BasicNameValuePair("email", email));

			// ユーザーの秘密番号
			httpParams.add(new BasicNameValuePair("pass", pass));

			// ダウンロードした本のID。登録した最初には"%x"と表示
			httpParams.add(new BasicNameValuePair("id", bookId));

			// ダウンロードした本の名前。登録した最初には"%x"と表示
			httpParams.add(new BasicNameValuePair("title", bookTitle));

			// ダウンロードした本の著者。登録した最初には"%x"と表示
			httpParams.add(new BasicNameValuePair("author", bookAuthor));

			// ダウンロードした本の説明。登録した最初には"%x"と表示
			httpParams.add(new BasicNameValuePair("descripstion",
					bookDescription));

			// ダウンロードした本のイメージ住所。登録した最初には"%x"と表示
			httpParams.add(new BasicNameValuePair("imageurl", bookImage));

			// ダウンロードした本の内容。登録した最初には"%x"と表示
			httpParams.add(new BasicNameValuePair("ebook", bookEbook));

			// ダウンロードした本がサーバーへ登録された日。登録した最初には"%x"と表示
			httpParams.add(new BasicNameValuePair("date", bookDate));

			cmsHTTP cmsHttp = new cmsHTTP();// 接続準備
			cmsHttp.act = Login_re.this;
			Log.e("sending", "sendpost");
			String tmpData = cmsHttp.sendPost(theUrl, httpParams);// サーバーへデータを要請
			Log.e("theUrl", theUrl + httpParams);

			// サーバーから戻り値がない場合
			if (tmpData == null) {
				return;
			}// if end

			// 戻り値が有る場合
			else {
				int rowid = 0;
				CheckUtil result = new CheckUtil();
				// サーバーへ要請したデータを保存
				hm = cmsutil.xml2HashMap(tmpData, cmsHttp.encoding);
				// 保存したデータの中で、ユーザーの状態データを読み込み
				rowid = Integer.valueOf(hm.get("rowid[0]"));
				// rowidの値が3の場合
				if (rowid == 3) {
					String msg = hm.get("msg[0]");
					result.CheckResult(this, rowid, msg, 1);
				}// if end
					// 3がない場合
				else {
					addResult(hm);
				}// else end
			}// else end
		}// if end
	}// tryToLogin() end

	// ////////////////////////////////////////////////
	//
	// サーバーへ要請したデータを使って、ファイルを作成
	//
	// ////////////////////////////////////////////////
	public void addResult(HashMap<String, String> hm) {
		int count = cmsutil.str2int(hm.get("count"));// サーバーからもらった本の数。"0"から始まる。
		int rowid = cmsutil.str2int(hm.get("rowid[0]"));
		String msg = hm.get("msg[0]");// rowidに対して、サーバーが送るメッセージ。

		// ユーザーがダウンロードした本が一巻の場合
		if (count == 0) {
			resId[0] = hm.get("id[0]");// 一番目の本のIDデータを引き出し
			resTitle[0] = hm.get("title[0]");// 一番目の本の名前データを引き出し
			resAuthor[0] = hm.get("author[0]");// 一番目の本の著者データを引き出し
			resDescription[0] = hm.get("description[0]");// 一番目の本の説目データを引き出し
			resImage[0] = hm.get("imageurl[0]");// 一番目の本のイメージ住所を引き出し
			resEbook[0] = hm.get("ebook[0]");// 一番目の本の内容データを引き出し
			resDate[0] = hm.get("date[0]");// 一番目の本本がサーバーへ登録された日を引き出し
		}// if end

		// ユーザーがダウンロードした本が一巻以上の場合
		else {
			// サーバーへ要請したデータを保存する変数"hm"からデータを引き出す構文を作る。
			for (int i = 0; i < count; i++) {
				id[i] = ("id[" + i + "]");
				Log.e("id", id[i].toString());
				title[i] = "title[" + i + "]";
				author[i] = "author[" + i + "]";
				description[i] = "description[" + i + "]";
				image[i] = "imageurl[" + i + "]";
				ebook[i] = "ebook[" + i + "]";
				date[i] = "date[" + i + "]";
			}// for i end

			// サーバーへ要請したデータを保存する変数"hm"からデータを引き出して各データ別に保存。
			for (int j = 0; j < count; j++) {
				Log.e("count_result", String.valueOf(j));
				resId[j] = hm.get(id[j]);
				resTitle[j] = hm.get(title[j]);
				resAuthor[j] = hm.get(author[j]);
				resDescription[j] = hm.get(description[j]);
				resImage[j] = hm.get(image[j]);
				resEbook[j] = hm.get(ebook[j]);
				resDate[j] = hm.get(date[j]);
			}// for end
		}// else end

		// rowidが"1"の場合
		if (rowid == 1) {
			try {
				// "login.txt"を作成します。
				saveFile(rowid, email, resId, resTitle, resAuthor,
						resDescription, resImage, resDate);

				// ユーザーがダウンロードした本が一巻の場合
				if (count == 0) {
					saveBook(resEbook, 0);// 一番目本の内容を保存
					SaveImg(resImage, 0);// 一番目本のイメージを保存
				}// if end

				// ユーザーがダウンロードした本が一巻以上の場合
				else {
					for (int i = 0; i < count; i++) {
						saveBook(resEbook, i);
						SaveImg(resImage, i);

					}
				}// else end
				bookCounter = 1;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// if end

		// rowidが"6"の場合
		else {

			try {
				// "login.txt"ファイルを作成
				saveFile(rowid, email, resId, resTitle, resAuthor,
						resDescription, resImage, resDate);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// else end

		CheckUtil result = new CheckUtil();
		result.CheckResult(this, rowid, msg, 1);// rowidに対して、サーバーが送メッセージを表示する。
	}// addResult(HashMap<String, String> hm) end

	// ////////////////////////////////////////////////
	//
	// "login.txt"ファイルを作成
	//
	// ////////////////////////////////////////////////
	public void saveFile(int rowid, String email, String[] id, String[] title,
			String[] author, String[] description, String[] image, String[] date)
			throws IOException {
		int count = Integer.valueOf(hm.get("count"));

		// 保存するファイルを開ける。
		userData = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath(), "login.txt");

		try {
			// rowidが"1"の場合
			if (rowid == 1) {
				save[0] = new FileWriter(userData);
				save[0].write(String.valueOf(rowid));
				save[0].write("\n");
				save[0].write(email);
				save[0].write("\n");

				// ユーザーがダウンロードした本が一巻の場合
				if (count == 0) {
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

				// ユーザーがダウンロードした本が一巻以上の場合
				else {
					for (int i = 0; i <= count; i++)// 本のIDを保存
					{
						if (i == count)
							save[0].write("\n");// if end
						else {
							if (i == (count - 1))
								save[0].write(id[i]);
							else {
								save[0].write(id[i]);
								save[0].write(",");
							}// else end
						}// else end
					}// for i end

					for (int i = 0; i <= count; i++)// 本の名前を保存
					{
						if (i == count)
							save[0].write("\n");
						else {
							if (i == (count - 1))
								save[0].write(title[i]);
							else {
								save[0].write(title[i]);
								save[0].write(",");
							}// else end

						}// else end
					}// for i end

					for (int i = 0; i <= count; i++)// 本の著者を保存
					{
						if (i == count)
							save[0].write("\n");
						else {
							if (i == (count - 1))
								save[0].write(author[i]);
							else {
								save[0].write(author[i]);
								save[0].write(",");
							}// else end

						}// else end
					}// for i end

					for (int i = 0; i <= count; i++)// 本の説明を保存
					{

						if (i == count)
							save[0].write("\n");
						else {
							if (i == (count - 1))
								save[0].write(description[i]);
							else {
								save[0].write(description[i]);
								save[0].write(",");
							}// else end

						}// else end
					}// for i end

					for (int i = 0; i <= count; i++)// 本のイメージ住所を保存
					{

						if (i == count)
							save[0].write("\n");
						else {
							if (i == (count - 1)) {
								save[0].write(image[i]);
							} else {
								save[0].write(image[i]);
								save[0].write(",");

							}// else end

						}// else end
					}// for i end

					for (int i = 0; i <= count; i++)// 本がサーバーに登録した時間を保存
					{

						if (i == count)
							save[0].write("\n");
						else {
							if (i == (count - 1))
								save[0].write(date[i]);
							else {
								save[0].write(date[i]);
								save[0].write(",");
							}// else

						}// else
					}// for i end
				}// else end
				save[0].close();// 保存するファイルを閉める。
			}// if end

			// rowidが"6"の場合
			else if (rowid == 6) {
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

			}// else if end
		}// try end
		catch (IOException e) {
			e.printStackTrace();
		}
	}/*
	 * saveFile(int rowid,String email, String[] id,String[] title,String[]
	 * author, String[] description,String[] image,String[] date) end
	 */

	// ////////////////////////////////////////////////
	//
	// Ebookファイルを作成
	//
	// ////////////////////////////////////////////////
	public void saveBook(String[] ebook, int i) throws IOException {

		userData = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath(), "ebook_" + (i + 1) + ".ebf");
		FileWriter tempSave = save[i + 1];
		tempSave = new FileWriter(userData);
		tempSave.write(ebook[i]);
		tempSave.close();
	}

	// ////////////////////////////////////////////////
	//
	// Ebookのイメージファイルを作成
	//
	// ///////////////////////////////////////////////
	void SaveImg(String[] ImgUrl, int i) throws IOException {

		try {
			// 保存したイメージの住所にイメージを要請するように住所を処理します。
			String tmpurlStr = "http://www." + ImgUrl[i];// 住所の前で、"http://www."を追加

			/*
			 * サーバーから端末までデータを送る課程で特殊文字が端末に届かない問題が発生。 それで、サーバーで"&"を"@amp;"で交代する。
			 * 下の構文はその"@amp;"を"&"で交代する。
			 */
			String imageUrl = tmpurlStr.replace("@amp;", "&");

			URL url = new URL(imageUrl);
			InputStream is = url.openStream();// 処理した住所でイメージ・データを引き出し

			// 保存するファイルを開ける
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath(), "ebook_" + (i + 1) + ".jpg");
			Bitmap bitmap = BitmapFactory.decodeStream(is);// データをイメージの形で保存
			OutputStream filestream = null;
			filestream = new FileOutputStream(file);
			// 保存したデータをイメージ形式で圧縮して、開けたファイルに保存
			bitmap.compress(CompressFormat.JPEG, 100, filestream);

			filestream.flush();
			filestream.close();// 保存するファイルを閉める。

		} catch (Exception e) {
			Log.e("URL", "error,in load Drawable\n" + e.toString());
		}
	}
}