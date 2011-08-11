package com.tomato.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;

public class fileLoadUtil {
	Context complete;
	String[] tmtFiles=new String[100];
	int fileLength = 0;
	String tmtPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Tomato/pdfimg/";
	public fileLoadUtil()
	{
		
		
		File f = new File(tmtPath);
		if(!f.exists())
		{
			f.mkdirs();
		}
        String files[] = f.list();
        
        for(int i=0;i<files.length;i++)
        {
        	if(files[i].endsWith(".pdfImg"))
        	{
        		tmtFiles[fileLength] = files[i];
        		fileLength++;
        	}
        }
        
	}
	
	public String getPath()
	{
		return tmtPath;
	}
	public String[] getList()
	{
		return tmtFiles; 
	}
	
	public int getLength()
	{
		return fileLength; 
	}
	
	public void unCompress(Context context,String path,String name)
	{
		complete = context;

		 try {
	         final int BUFFER = 2048;
	         BufferedOutputStream dest = null;
	         File target = new File(path,name);
	         FileInputStream fis = new  FileInputStream(path+name);
	         CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());
	         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
	         ZipEntry entry;
	         while((entry = zis.getNextEntry()) != null) 
	         {
	            System.out.println("Extracting: " +entry);
	            int count;
	            byte data[] = new byte[BUFFER];
	            // write the files to the disk
	            File dataFolder = new File(path+(name.replace(".pdfImg", "/")));
	            if(!dataFolder.exists())
	            {
	            	dataFolder.mkdir();
	            }
	            FileOutputStream fos = new FileOutputStream(path+(name.replace(".pdfImg", "/")+"/")+entry.getName());
	            dest = new BufferedOutputStream(fos,BUFFER);
	            while ((count = zis.read(data, 0,BUFFER)) != -1) 
	            {
	               dest.write(data, 0, count);
	            }
	            dest.flush();
	            dest.close();
	         }
	         zis.close();
	         
	         new AlertDialog.Builder(complete)
	         	.setTitle("Notification")
				.setMessage("ファイル読み込みが完了しました。")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.show();
	         
	         System.out.println("Checksum: "+checksum.getChecksum().getValue());
	      } catch(Exception e) {
	         e.printStackTrace();
	      }

	}
}
