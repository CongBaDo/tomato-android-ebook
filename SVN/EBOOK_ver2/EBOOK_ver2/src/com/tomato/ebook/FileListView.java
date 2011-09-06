package com.tomato.ebook;

import java.util.ArrayList;

import com.tomato.communication.fileLoadUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FileListView extends Activity{
	fileLoadUtil un = new fileLoadUtil();
	ArrayList<String> fileList = new ArrayList<String>();
	String[] tmtFiles = new String[un.getLength()];
	int savePosition;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_list);
		ListView list = (ListView)findViewById(R.id.FileList);
		tmtFiles = un.getList();
		for(int i = 0;i<un.getLength();i++)
		{
			fileList.add(tmtFiles[i]);
		}                 
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) {
				// TODO Auto-generated method stub
				
				un.unCompress(FileListView.this,un.getPath(),fileList.get(position));
				savePosition = position;
				new AlertDialog.Builder(FileListView.this)

				.setTitle("Notification")
				.setMessage("ファイル読み込みが完了しました。")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(FileListView.this,CurlActivity_File.class);
						intent.putExtra("FileName",fileList.get(savePosition));

						startActivity(intent);
						finish();
					}
				})
				.show();

			}
		});
	}
}
