package com.tomato.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;

import com.tomato.sdcard.SDcard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class MyLibraryAdapter extends BaseAdapter{

	private SDcard sd=new SDcard();
	private Context mContext;
//	private int[] list_image={
//			R.drawable.list1,R.drawable.list2,R.drawable.list3,
//			R.drawable.list4,R.drawable.list5,R.drawable.list6,
//			R.drawable.list7};
	private String[] list_image=sd.imageCount();

	
	

	public MyLibraryAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		
		return list_image.length;
	}

	@Override
	public Object getItem(int position) {
		return list_image[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView imageview;
		File in;
		FileReader fl;
		BufferedReader buf;
		if (convertView==null) {
			imageview=new ImageView(mContext);
		} else {
			imageview=(ImageView) convertView;
			
		}
		try {
			in = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"ebook_1.jpg");
			fl = new FileReader(in);
			buf = new BufferedReader(fl);
			
			
		} catch (Exception e) {
		Log.e("adapter", "getview-error");
		}
		
		
		
		
//		imageview.setImageResource(list_image[position]);
		imageview.setScaleType(ImageView.ScaleType.FIT_XY);
		imageview.setLayoutParams(new Gallery.LayoutParams(189, 268));	
		
		
		
	return imageview;
	}

}

