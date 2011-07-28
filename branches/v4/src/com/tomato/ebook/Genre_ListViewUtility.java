package com.tomato.ebook;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Genre_ListViewUtility extends Activity 
{

	   static class MyItem
	   {
		   String Name;
		   int Icon;
		   
		   MyItem(String aName,int aIcon)
		   {
			   Name = aName;
			   Icon = aIcon;
		   }
		   
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
			
			TextView txt = (TextView)convertView.findViewById(R.id.Genre_Tiltle);
			txt.setText(arSrc.get(position).Name);
			
			ImageView img = (ImageView)convertView.findViewById(R.id.Genre_GoToBookBtn);
			img.setImageResource(arSrc.get(position).Icon);
			
			

			return convertView;
		}

	   }
}
