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
		//fileをloadするために使用
		File f = new File(tmtPath);
		if(!f.exists())
		{
			//Directoryがない場合に作る
			f.mkdirs();
		}
		//でもfile配列を作る
        String files[] = f.list();
        
        for(int i=0;i<files.length;i++)
        {
        	//fileの拡張子名がPDFIMG
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
	         //バッファの固定
			 final int BUFFER = 2048;
	         //まずNullに設定
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
	            //フォルダ内のfileの拡張子がを探す
	            File dataFolder = new File(path+(name.replace(".pdfImg", "/")));
	            if(!dataFolder.exists())
	            {	
	            	//フォルダがなかったら作る
	            	dataFolder.mkdir();
	            }
	            //敬老を探しpdfimgファイルを検索して作る
	            FileOutputStream fos = new FileOutputStream(path+(name.replace(".pdfImg", "/")+"/")+entry.getName());
	            //バッファを使ってファイルを整理する
	            dest = new BufferedOutputStream(fos,BUFFER);
	            //ファイルを一つずつ読む
	            while ((count = zis.read(data, 0,BUFFER)) != -1) 
	            {
	               dest.write(data, 0, count);
	            }
	            //バッファを空く
	            dest.flush();
	            //バッファを閉ざす
	            dest.close();
	         }
	         zis.close();
	         //ダイアルログを使う
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
