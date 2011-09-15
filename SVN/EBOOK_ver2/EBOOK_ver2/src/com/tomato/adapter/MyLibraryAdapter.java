package com.tomato.adapter;

import org.apache.james.mime4j.codec.DecoderUtil;

import com.tomato.sdcard.SDcard;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SimpleAdapter.ViewBinder;

public class MyLibraryAdapter extends BaseAdapter{

	private SDcard sd=new SDcard();
	private Context mContext;
//	private int[] list_image={
//			R.drawable.list1,R.drawable.list2,R.drawable.list3,
//			R.drawable.list4,R.drawable.list5,R.drawable.list6,
//			R.drawable.list7};
	private String[] list_image= sd.imageCount();
	
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
		if (convertView==null) {
			imageview=new ImageView(mContext);
		} else {
			imageview=(ImageView) convertView;
		}
		
		String viewImage=list_image[position];
		
		Log.e("viewImage", viewImage);
		
		try {
			if(Integer.valueOf(sd.getRowId())==1)
			{
				// The gap we want between the reflection and the original image
				final int reflectionGap = 1;

				// Get you bit map from drawable folder
				Bitmap originalImage = BitmapFactory.decodeFile(viewImage);
				
				int width = originalImage.getWidth();
				int height = originalImage.getHeight();

				// This will not scale but will flip on the Y axis
				Matrix matrix = new Matrix();
				matrix.preScale(1, -1);

				// Create a Bitmap with the flip matix applied to it.
				// We only want the bottom half of the image
				Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
						height / 2, width, height / 2, matrix, false);

				// Create a new bitmap with same width but taller to fit reflection
				Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
						(height + height / 2), Config.ARGB_8888);

				// Create a new Canvas with the bitmap that's big enough for
				// the image plus gap plus reflection
				Canvas canvas = new Canvas(bitmapWithReflection);
				// Draw in the original image
				canvas.drawBitmap(originalImage, 0, 0, null);
				// Draw in the gap
				Paint deafaultPaint = new Paint();
				canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
				// Draw in the reflection
				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

				// Create a shader that is a linear gradient that covers the reflection
				Paint paint = new Paint();
				LinearGradient shader = new LinearGradient(0,
						originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
								+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
				// Set the paint to use this shader (linear gradient)
				paint.setShader(shader);
				// Set the Transfer mode to be porter duff and destination in
				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
				// Draw a rectangle using the paint with our linear gradient
				canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
						+ reflectionGap, paint);	
			
			imageview.setImageBitmap(bitmapWithReflection);
			//InputStream is=new URL(viewImage).openStream();		
			//Bitmap bit=BitmapFactory.decodeStream(is);
			//imageview.setImageBitmap(bit);
			//is.close();			
			}
			else if (Integer.valueOf(sd.getRowId())==6)
			{
				// The gap we want between the reflection and the original image
				final int reflectionGap = 1;
				
				// Get you bit map from drawable folder
				imageview.setImageResource(com.tomato.ebook.R.drawable.list_book);
				BitmapDrawable bd = (BitmapDrawable)imageview.getDrawable();
				Bitmap originalImage = bd.getBitmap();
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
				canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
				// Draw in the reflection
				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

				// Create a shader that is a linear gradient that covers the
				// reflection
				Paint paint = new Paint();
				LinearGradient shader = new LinearGradient(0,
						originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
								+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
				// Set the paint to use this shader (linear gradient)
				paint.setShader(shader);
				// Set the Transfer mode to be porter duff and destination in
				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
				// Draw a rectangle using the paint with our linear gradient
				canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
						+ reflectionGap, paint);

				imageview.setImageBitmap(bitmapWithReflection);
			}
		} catch (Exception e) {
		Log.e("adapter", "getview-error");
		}
		imageview.setScaleType(ImageView.ScaleType.FIT_XY);
		imageview.setLayoutParams(new Gallery.LayoutParams(110, 180));	
	return imageview;
	}
}

