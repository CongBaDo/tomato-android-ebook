package com.tomato.communication;





import com.tomato.ebook.MyLibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;


public class CheckUtil {

	String ch_id,ch_pass,ch_check;
	Context ch_context,re_context;

	Boolean ch_sw=false;

	public CheckUtil(){}

	public CheckUtil (Context context,String id,String pass)
	{
		ch_context = context;
		ch_id = id;
		ch_pass = pass;
	}

	public CheckUtil (Context Context,String id,String pass,String check,Boolean toggle)
	{
		ch_context = Context;
		ch_id = id;
		ch_pass = pass;
		ch_check = check;
		ch_sw = toggle;
	}

	public Boolean checkStart()
	{
		if(ch_id.length()==0)
		{

			new AlertDialog.Builder(ch_context)
			.setTitle("Notification")
			.setMessage("IDを入れてください。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			})
			.show();
			return false;
		}
		else if(ch_pass.length()==0)
		{
			new AlertDialog.Builder(ch_context)
			.setTitle("Notification")
			.setMessage("Passを入れてください。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			})
			.show();
			return false;
		}
		else
		{
			if(ch_sw)
			{
				if(ch_check.length()==0)
				{
					new AlertDialog.Builder(ch_context)
					.setTitle("Notification")
					.setMessage("Passをもう一度入れてください。")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					})
					.show();
					return false;
				}

				else if(!ch_pass.equals(ch_check))
				{
					new AlertDialog.Builder(ch_context)
					.setTitle("Notification")
					.setMessage("Passが違います。もう一度入れてください。")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					})
					.show();
					return false;
				}
			}
		}
		return true;
	}

	public void CheckResult(Context context,int rowid,String msg,int sw)
	{
		int oneSwich=sw;
		re_context = context;
		
		Log.e("rowid",String.valueOf(rowid));
		if(rowid==1)
		{
			if(oneSwich==1)
			{
				new AlertDialog.Builder(re_context)
				.setTitle("Notification")
				.setMessage(msg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent re_Intent = new Intent(re_context,MyLibrary.class);
						re_Intent.putExtra("State", "OK");
						re_context.startActivity(re_Intent);
					}
				})
				.show();
			}
			

		}

		else if(rowid==2)
		{
			new AlertDialog.Builder(re_context)
			.setTitle("Notification")
			.setMessage("同じIDが有ります。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			})
			.show();
		}

		else if(rowid==3)
		{
			new AlertDialog.Builder(re_context)
			.setTitle("Notification")
			.setMessage("IDが違います。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			})
			.show();
		}
		else if(rowid==4)
		{
			new AlertDialog.Builder(re_context)
			.setTitle("Notification")
			.setMessage("秘密番号が違います。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			})
			.show();
		}
		else if(rowid==5)
		{
			Log.e("Equalbook","msg");
			new AlertDialog.Builder(re_context)
			.setTitle("Notification")
			.setMessage("同じ本があります。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			})
			.show();
		}
		else if(rowid==6)
		{
			new AlertDialog.Builder(re_context)
			.setTitle("Notification")
			.setMessage("登録しました。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(re_context, MyLibrary.class);
					intent.putExtra("State", "OK");
					re_context.startActivity(intent);
				}
			})
			.show();
		}
		else
		{
			new AlertDialog.Builder(re_context)
			.setTitle("Notification")
			.setMessage("サーバーに問題があります。/nもう一度Loginしてください。")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			})
			.show();
			return;
		}
	}


}
