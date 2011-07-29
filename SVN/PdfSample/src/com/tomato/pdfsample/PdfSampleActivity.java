package com.tomato.pdfsample;

import java.io.File;
import java.io.IOException;


import com.qoppa.pdf.PDFException;
import com.qoppa.pdfImages.PDFImages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class PdfSampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
     // Get the pdf file to export 
        File readPDF = new File (Environment.getExternalStorageDirectory().getAbsolutePath(),"test.pdf");
        if(!readPDF.exists()||readPDF.length()==0||!readPDF.canRead())
        {
        	new AlertDialog.Builder(PdfSampleActivity.this)
        	.setTitle("Notification")
        	.setMessage("PDF fileÇ™Ç›Ç¶Ç‹ÇπÇÒÅB")
        	.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.show();
        }
        try
        {
        	String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"test.pdf";
        	PDFImages images= new PDFImages();
        	images = new PDFImages("/sdcard/test.pdf",null);
        	for(int count=0;count<images.getPageCount();++count)
        	{
        		File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"page"+count+".png");
        		images.savePageAsPNG(count, outFile.getAbsolutePath(), 144);
        	}
            // Show message
   //         JOptionPane.showMessageDialog(this, "Files were exported to:\n" + exportsDir.getAbsolutePath());
        }
        catch (PDFException pdfE)
        {
   //         JOptionPane.showMessageDialog (this, pdfE.getMessage());
        	Log.e("error",pdfE.getMessage());
        } catch (IOException e) {
			e.printStackTrace();
		}
    }
}