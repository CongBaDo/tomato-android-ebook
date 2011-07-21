package com.tomato.pdf;

import com.tomato.pdfmathod.PDFmethod;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class PDFimage extends Activity {
	
//	String sdPath=Environment.getExternalStorageDirectory().getAbsolutePath();
	
	PDFmethod pdf=null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pdf=new PDFmethod();

        Log.e("sdcard",Environment.getExternalStorageDirectory().getAbsolutePath());
        
        pdf.convertPDF(Environment.getExternalStorageDirectory().getAbsolutePath(), "data.pdf");
        

        
    }
}