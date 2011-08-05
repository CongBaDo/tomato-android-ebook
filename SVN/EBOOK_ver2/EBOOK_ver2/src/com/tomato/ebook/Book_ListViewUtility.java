package com.tomato.ebook;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Book_ListViewUtility extends Activity 
{
	   static class MyItem
	   {
		      
		   MyItem(String aIcon,String aName)
		   {
			   Icon = aIcon;
			   Name = aName;
		   }
		   
		   String Icon;
		   String Name;
	   }
	   static class MyListAdapter extends BaseAdapter
	   {
		    Context maincon;
		   LayoutInflater Inflater;
		   ArrayList<MyItem> arSrc;
		   int layout;
		   
		   public MyListAdapter(Context context,int alayout, ArrayList<MyItem>aarSrc)
		   {
			   maincon = context;
			   Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			   arSrc = aarSrc;
			   layout = alayout;
		   }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arSrc.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arSrc.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			// TODO Auto-generated method stub
			if(convertView==null)
			{
				convertView = Inflater.inflate(layout, parent, false);
		
			}


			ImageView img = (ImageView)convertView.findViewById(R.id.imageView1);
			Drawable draw = loadDrawable(arSrc.get(position).Icon);
			img.setImageDrawable(draw);
			
			
			
			TextView txt = (TextView)convertView.findViewById(R.id.textView1);
			txt.setText(arSrc.get(position).Name);

			return convertView;
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
	   }
}
