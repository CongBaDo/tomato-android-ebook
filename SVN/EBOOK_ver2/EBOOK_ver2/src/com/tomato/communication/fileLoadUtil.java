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

import android.os.Environment;

public class fileLoadUtil {
	String tmtFiles[] = new String[100];
	
	public fileLoadUtil()
	{
		
		int j = 0;
		File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/tmt/");
        String files[] = f.list();
        
        for(int i=0;i<files.length;i++)
        {
        	if(files[i].endsWith(".tmt"))
        	{
        		tmtFiles[j] = files[i];
        		j++;
        	}
        }
        
	}
	private String[] getList()
	{
		return tmtFiles; 
	}
	
	public void unCompress(String path,String name)
	{
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
	            FileOutputStream fos = new FileOutputStream(path+(entry.getName()));
	            dest = new BufferedOutputStream(fos,BUFFER);
	            while ((count = zis.read(data, 0,BUFFER)) != -1) 
	            {
	               dest.write(data, 0, count);
	            }
	            dest.flush();
	            dest.close();
	         }
	         zis.close();
	         target.delete();
	         System.out.println("Checksum: "+checksum.getChecksum().getValue());
	      } catch(Exception e) {
	         e.printStackTrace();
	      }

	}
}
