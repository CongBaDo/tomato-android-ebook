package com.tomato.pdfmathod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import net.sf.andpdf.refs.HardReference;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;

public class PDFmethod {

	FileChannel channel; 
	RandomAccessFile raf; 
	PDFFile pdffile; 
	int pageTotalcount;
	int pagecount;

	public void convertPDF(String path,String filename){    

		Log.e("1", "1");

		//path : /sdcard, filename : test.pdf     
		PDFImage.sShowImages = true;     
		PDFPaint.s_doAntiAlias = true;     
		HardReference.sKeepCaches = false;           

		Log.e("2", "2");
		File file  = new File(path+"/"+filename);     
		Log.e("3", "3");

		if(file.exists())     {         
			Log.i("file","exist!!");       
		}     else    {         
			Log.i("file","no exist!!");     
		}     
		try {         
			raf = new RandomAccessFile(file, "r");         
			channel = raf.getChannel();         
			net.sf.andpdf.nio.ByteBuffer buf = net.sf.andpdf.nio.ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));           
			pdffile = new PDFFile(buf);               
		}      catch (FileNotFoundException e)      {     

		}     catch (IOException ioe)      {         
			ioe.printStackTrace();     }           

		Log.e("4", "4");

		pageTotalcount = pdffile.getNumPages(); //PDF Total Page Count           
		for(int i=0; i<pageTotalcount; i++)     {         
			pagecount = i+1;                             
			PDFPage page = pdffile.getPage(i+1,true);   //1번부터 마지막 페이지까지 가져옴.                  
			int wi = (int) page.getWidth();         
			int hei = (int)page.getHeight();         
			int wantWidth = 987;        //원하는 가로사이즈를 넣으면  비율에 맞게 세로사이즈가 설정됨.         
			int wantHeight = wantWidth*hei/wi;                   
			RectF rect = new RectF(0, 0, (int)page.getWidth(), (int)page.getHeight());     

			Log.e("5", "5");

			Bitmap image = page.getImage(wantWidth,wantHeight,rect,true, true);                   
			Log.e("6", "6");
			String image_path = path+filename+"_"+Integer.toString(i+1)+".jpg";   
			// /sdcard/pdf/test_1.jpg 이런식으로 저장.         
			Log.e("7", "7");
			File imageFile = new File(image_path);
			Log.e("8", "8");


			try{             
				if(imageFile.exists())
				{ 
					imageFile.delete();               
				}             
				Log.e("9", "9");

				imageFile.createNewFile();    
				
				Log.e("10", "10");

				FileOutputStream fos = new FileOutputStream(imageFile);             
				image.compress(Bitmap.CompressFormat.JPEG, 100, fos);             
				fos.close();                       
				Log.e("11", "11");
				
			}catch (IOException ioe) {             
				ioe.printStackTrace();           

			} 
			} 
		
	} 
}

