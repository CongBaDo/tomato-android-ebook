package com.tomato.ebook;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;



public class ReadSetup extends Activity{

	Spinner sp1, sp2, sp3;
	ArrayAdapter<CharSequence> spin1,spin2, spin3;
	String title="選択してください。";
	TextView preview;
	ArrayList<CharSequence> data1, data2, data3;
	Button setup;
	//	int color, bgcolor=0;

	String color=null;
	String bgcolor=null;
	String bookKey=null;
	static int bg_Offset = 0,col_offset=0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readsetup);


		setup=(Button)findViewById(R.id.setup);
		sp1=(Spinner)findViewById(R.id.spinner1);
		sp2=(Spinner)findViewById(R.id.spinner2);
		sp3=(Spinner)findViewById(R.id.spinner3);
		preview=(TextView) findViewById(R.id.preview);

		sp1.setPrompt(title);
		sp2.setPrompt(title);
		sp3.setPrompt(title);

		data1=new ArrayList<CharSequence>();
		data1.add("普通");
		data1.add("3D");


		data2=new ArrayList<CharSequence>();
		data2.add("黒");
		data2.add("青");
		data2.add("茶色");

		data3=new ArrayList<CharSequence>();
		data3.add("白");
		data3.add("うすいピンク");
		data3.add("薄い青");
		data3.add("薄い緑");
		data3.add("薄い黄");		


		spin1=new ArrayAdapter<CharSequence>(this, 

				android.R.layout.simple_spinner_item, data1);
		sp1.setAdapter(spin1);
		spin2=new ArrayAdapter<CharSequence>(this, 

				android.R.layout.simple_spinner_item, data2);
		sp2.setAdapter(spin2);
		spin3=new ArrayAdapter<CharSequence>(this, 

				android.R.layout.simple_spinner_item, data3);
		sp3.setAdapter(spin3);


		sp1.setOnItemSelectedListener(click);
		sp2.setOnItemSelectedListener(click);
		sp3.setOnItemSelectedListener(click);
		setup.setOnClickListener(click_btn);

		Intent intent=getIntent();
		bookKey=intent.getStringExtra("bookKey");
		color=intent.getStringExtra("color");
		bgcolor=intent.getStringExtra("bgcolor");

	}

	private OnClickListener click_btn=new OnClickListener() {

		@Override
		public void onClick(View v) {

			new AlertDialog.Builder(ReadSetup.this)
			.setTitle("Notification")
			.setMessage("今までの設定でよろしいですか？")
			.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			})
			.setPositiveButton("はい", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(col_offset==0&&bg_Offset==0)
					{
						color="#000000";
						bgcolor="#FFFFFF";
						finish();
						Log.e("color-bg11111111111", color+"//"+bgcolor);
						Intent intent=new Intent(ReadSetup.this, CurlActivity.class);
						intent.putExtra("bookKey", bookKey);
						intent.putExtra("color",color);
						intent.putExtra("bgcolor",bgcolor);
						Log.e("color-bg222222222222222", color+"//"+bgcolor);		
						startActivity(intent);
					}
					else if(col_offset==1&&bg_Offset==0)
					{
						bgcolor="#FFFFFF";
						finish();
						Log.e("color-bg11111111111", color+"//"+bgcolor);
						Intent intent=new Intent(ReadSetup.this, CurlActivity.class);
						intent.putExtra("bookKey", bookKey);
						intent.putExtra("color",color);
						intent.putExtra("bgcolor",bgcolor);
						Log.e("color-bg222222222222222", color+"//"+bgcolor);		
						startActivity(intent);
					}
					else if(col_offset==0&&bg_Offset==1)
					{
						color="#000000";
						finish();
						Log.e("color-bg11111111111", color+"//"+bgcolor);
						Intent intent=new Intent(ReadSetup.this, CurlActivity.class);
						intent.putExtra("bookKey", bookKey);
						intent.putExtra("color",color);
						intent.putExtra("bgcolor",bgcolor);
						Log.e("color-bg222222222222222", color+"//"+bgcolor);		
						startActivity(intent);
					}
					else
					{
						finish();
						Log.e("color-bg11111111111", color+"//"+bgcolor);
						Intent intent=new Intent(ReadSetup.this, CurlActivity.class);
						intent.putExtra("bookKey", bookKey);
						intent.putExtra("color",color);
						intent.putExtra("bgcolor",bgcolor);
						Log.e("color-bg222222222222222", color+"//"+bgcolor);		
						startActivity(intent);
					}
				}
			})
			.show();
		}
	};


	private OnItemSelectedListener click=new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			switch (parent.getId()) 
			{

			case R.id.spinner1:{
				preview.setText(data1.get(position));
				break;}

			case R.id.spinner2:{
				preview.setText(data2.get(position));
				switch (position) {
				case 0:
					preview.setTextColor(Color.BLACK);
					color="#000000";
					break;
				case 1:
					col_offset = 1;
					preview.setTextColor(Color.BLUE);
					color="#1800ff";
					break;

				case 2:
					col_offset = 1;
					preview.setTextColor(Color.parseColor("#996903"));
					//					color=Color.parseColor("#996903");
					color="#996903";
					break;


				}

				break;
			}

			case R.id.spinner3:{
				String bg=(String) data3.get(position);
				preview.setText(bg);
				switch (position) {
				case 0:
					preview.setBackgroundColor(Color.WHITE);
					bgcolor="#FFFFFF";

					break;

				case 1:
					bg_Offset = 1;
					preview.setBackgroundColor(Color.parseColor("#ffe4e1"));
					//					bgcolor=Color.parseColor("#ffe4e1");
					bgcolor="#ffe4e1";
					break;

				case 2:
					bg_Offset = 1;
					preview.setBackgroundColor(Color.parseColor("#E1F0FD"));
					//					bgcolor=Color.parseColor("#E1F0FD");
					bgcolor="#E1F0FD";
					break;

				case 3:
					bg_Offset = 1;
					preview.setBackgroundColor(Color.parseColor("#BFFDBF"));
					//					bgcolor=Color.parseColor("#BFFDBF");
					bgcolor="#BFFDBF";
					break;

				case 4:
					bg_Offset = 1;
					preview.setBackgroundColor(Color.parseColor("#FFFFBF"));
					//					bgcolor=Color.parseColor("#FFFFBF");
					bgcolor="#FFFFBF";
					break;

				}

				break;
			}

			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};
}
