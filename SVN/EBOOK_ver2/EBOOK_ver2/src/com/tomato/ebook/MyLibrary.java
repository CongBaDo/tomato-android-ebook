package com.tomato.ebook;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomato.adapter.MyLibraryAdapter;
import com.tomato.sdcard.SDcard;

public class MyLibrary extends Activity {
	public static ArrayList<Activity> bkList = new ArrayList<Activity>();

	TextView titleName, authorName, description;

	ImageView list_book_detail;
	final static int MAX = 100;

	ArrayList<String> data_list = new ArrayList<String>();
	ArrayList<String> datadata = new ArrayList<String>();
	String[] bunri = null;
	SDcard sd = null;

	String userid = null, userId = null, book = null, title = null,
			writer = null, des = null, image_url = null, date = null;

	int book_key = 1;
	File userData, userText;
	FileWriter[] save = new FileWriter[MAX];
	FileReader idCheck;

	String ext = Environment.getExternalStorageState();
	String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	String filePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/tmt" + "/";
	File bookText;

	// SSong's 0913
	private Gallery mylibrarylist;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mylibrary_re);
		JptomatoLogoActivity.actList.add(this);

		titleName = (TextView) findViewById(R.id.MyLibrary_Story);
		authorName = (TextView) findViewById(R.id.MyLibrary_AuthorName);
		description = (TextView) findViewById(R.id.MyLibrary_Story);

		list_book_detail = (ImageView) findViewById(R.id.list_book_detail);

		sd = new SDcard();
		datadata = sd.tryToMyLibrary();// read login.txt

		// Gallery view
		mylibrarylist = (Gallery) findViewById(R.id.gallery);
		Log.e("g", "1");
		mylibrarylist.setAdapter(new MyLibraryAdapter(this));
		Log.e("g", "2");
		mylibrarylist.setOnItemClickListener(list_listener);
		Log.e("g", "3");
		mylibrarylist.setOnItemLongClickListener(veiw_listener);
	}

	String[] redata = null;

	public String[] datafor(String data) {
		redata = data.split(",");
		return redata;
	}

	private OnItemLongClickListener veiw_listener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			new AlertDialog.Builder(MyLibrary.this)
					.setTitle("お知らせ")
					.setMessage("ボタンを選んでください。")					
					.setPositiveButton("本を読む", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method
									Intent intent = new Intent(MyLibrary.this, CurlActivity.class);
									intent.putExtra("bookKey", book_key + "");
									intent.putExtra("color", "#000000");
									intent.putExtra("bgcolor", "#FFFFFF");
									startActivity(intent);
								}
							})
					.setNeutralButton("PDF", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method
							Intent intent = new Intent(MyLibrary.this, FileListView.class);
							startActivity(intent);
						}
					})		
					.setNegativeButton("preview", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method
							
						}
					}).show();
			return true;
		}
	};

	private OnItemClickListener list_listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Log.e("g", "g1");

			sd = new SDcard();

			userid = datadata.get(1);
			book = datadata.get(2);
			title = datadata.get(3);
			writer = datadata.get(4);
			des = datadata.get(5);
			image_url = datadata.get(6);
			date = datadata.get(7);

			if (Integer.valueOf(datadata.get(0)) == 1) {
				for (int i = 0; i < datadata.size(); i++) {
					Log.e("datadata", datadata.get(i));
				}
				String[] booktitle = datafor(title);
				String[] bookwriter = datafor(writer);
				String[] bookdes = datafor(des);
				String[] bookimg = image_url.split(",");
				for (int k = 0; k < bookimg.length; k++) {
					bookimg[k] = "/sdcard/ebook_" + (k + 1) + ".jpg";
				}
				String viewImage = bookimg[position];

				// 0912 SSong's
				Bitmap bitmapWithReflection = StrReflection(viewImage);

				list_book_detail.setImageBitmap(bitmapWithReflection);
				titleName.setText(booktitle[position]);
				authorName.setText(bookwriter[position]);
				description.setText(bookdes[position]);
				book_key = position + 1;
			} else if (Integer.valueOf(datadata.get(0)) == 6) {
				titleName.setText("未だダウンロードした本が有りません。");
			}
		}
	};

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(MyLibrary.this, Preview.class);
			intent.putExtra("bookKey", book_key + "");
			intent.putExtra("color", "#000000");
			intent.putExtra("bgcolor", "#FFFFFF");
			intent.putExtra("pageNum", 1);
			startActivity(intent);
			break;
		case 2:
			Intent intent2 = new Intent(MyLibrary.this, Preview.class);
			intent2.putExtra("bookKey", book_key + "");
			intent2.putExtra("color", "#000000");
			intent2.putExtra("bgcolor", "#FFFFFF");
			intent2.putExtra("pageNum", 2);
			startActivity(intent2);
			break;
		case 3:
			Intent intent3 = new Intent(MyLibrary.this, Preview.class);
			intent3.putExtra("bookKey", book_key + "");
			intent3.putExtra("color", "#000000");
			intent3.putExtra("bgcolor", "#FFFFFF");
			intent3.putExtra("pageNum", 3);
			startActivity(intent3);
			break;
		case 4:
			Intent intent4 = new Intent(MyLibrary.this, Preview.class);
			intent4.putExtra("bookKey", book_key + "");
			intent4.putExtra("color", "#000000");
			intent4.putExtra("bgcolor", "#FFFFFF");
			intent4.putExtra("pageNum", 4);
			startActivity(intent4);
			break;
		}
		return false;
	}

	// 0913 SSong's
	private Bitmap StrReflection(String bitmap) {
		// The gap we want between the reflection and the original image
		final int reflectionGap = 1;

		// Get you bit map from drawable folder
		Bitmap originalImage = BitmapFactory.decodeFile(bitmap);

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		// Create a Bitmap with the flip matix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		// Create a new bitmap with same width but taller to fit
		// reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 10), Config.ARGB_8888);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// Draw in the gap
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap,
				deafaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// Create a shader that is a linear gradient that covers the
		// reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff,
				TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}
}