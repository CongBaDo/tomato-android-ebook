package com.tomato.ebook;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class JptomatoLogoActivity extends Activity {
	public static ArrayList<Activity> actList = new ArrayList<Activity>();
	private final Class<?> nextActivity = EbookMain.class;
	private boolean isSkip;
	private boolean isCancled;
	
	private final Handler handler = new Handler();
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_tomato);
        
        startAnimation(findViewById(R.id.logo_droid_head), R.anim.logo_droid_head);
        startAnimation(findViewById(R.id.logo_text6_o), R.anim.logo_text6_o);
        startAnimation(findViewById(R.id.logo_text5_t), R.anim.logo_text5_t);
        startAnimation(findViewById(R.id.logo_text4_a), R.anim.logo_text4_a);
        startAnimation(findViewById(R.id.logo_text3_m), R.anim.logo_text3_m);
        startAnimation(findViewById(R.id.logo_text2_o), R.anim.logo_text2_o);
        startAnimation(findViewById(R.id.logo_text1_t), R.anim.logo_text1_t);
        startAnimation(findViewById(R.id.logo_tomatopic), R.anim.logo_tomatopic);
        startAnimation(findViewById(R.id.logo_droid_body), R.anim.logo_droid_body);
        startAnimation(findViewById(R.id.logo_skip), R.anim.logo_skip);
        
        handler.postDelayed(new Runnable() {
    	    @Override
    	    public void run() {
    	    	final ImageView logoTomatoIV = (ImageView) findViewById(R.id.logo_tomato);
    	    	logoTomatoIV.setVisibility(View.VISIBLE);
	            startAnimation(logoTomatoIV, R.anim.logo_tomato);
    	    }
    	}, 6900);
        
    	handler.postDelayed(new Runnable() {
    	    @Override
    	    public void run() {
    	    	if(!isSkip && !isCancled) {
    	    		moveActivity();
    	        	finish();
    	    	}
    	    }
    	}, 10000);
    	
    	final ImageView skipIV = (ImageView) findViewById(R.id.logo_skip);
    	skipIV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isSkip = true;
				moveActivity();
	        	finish();
			}
		});
    }
    
    
    public void startAnimation(View view, int resAnim) {
    	view.startAnimation(AnimationUtils.loadAnimation(this, resAnim));
    }
    
    
    public void moveActivity() {
    	final Intent intent = new Intent(this, nextActivity);
    	startActivity(intent);
    }
    

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			isSkip = true;
			moveActivity();
        	finish();
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_BACK) {
			isCancled = true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}