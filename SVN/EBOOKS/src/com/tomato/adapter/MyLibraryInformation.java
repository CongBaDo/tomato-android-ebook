package com.tomato.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.os.Environment;

public class MyLibraryInformation {

	String ext;
	String sdpath=Environment.getExternalStorageDirectory().getAbsolutePath();

	public MyLibraryInformation(){
		ext=Environment.getExternalStorageState();
		//		if (ext.equals(Environment.MEDIA_MOUNTED)) {
		//			
		//		} else {
		//
		//		}
	}

	public void save(){

		File dir=new File(sdpath+"/dir");
		dir.mkdir();
		File file=new File(sdpath+"/dir/login.txt");
		try {
			FileOutputStream fos=new FileOutputStream(file);
			String str="nana";
			fos.write(str.getBytes());
			fos.close();
			

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (SecurityException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void dataload(){

		try {

			FileInputStream fis=new FileInputStream(sdpath+"/dir/login.txt");
			byte[] data=new byte[fis.available()];
			while (fis.read(data)!=-1) {
				;
			}
			fis.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			;
		}

	}


}
