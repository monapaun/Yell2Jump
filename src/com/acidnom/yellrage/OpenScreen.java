package com.acidnom.yellrage;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class OpenScreen extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	 @Override
	   public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.open_screen);
//	      // Lookup R.layout.main
//	      LinearLayout layout = (LinearLayout)findViewById(R.id.open_background);
//	        
//	      // Create the adView
//	      // Please replace MY_BANNER_UNIT_ID with your AdMob Publisher ID
//	      AdView adView = new AdView(this, AdSize.BANNER,"a14f2b28de5e665");
//	    
//	      // Add the adView to it
//	      layout.addView(adView);
//	       
//	      // Initiate a generic request to load it with an ad
//	      AdRequest request = new AdRequest();
//	      request.setTesting(true);
//	      //AdRequest.addTestDevice("16CE7F242F4024F398639F0EDEDD9CFA");
//
//	      adView.loadAd(request); 
	      Thread splashThread = new Thread() {
	         @Override
	         public void run() {
	            try {
	               int waited = 0;
	               while (waited < 2000) {
	                  sleep(100);
	                  waited += 100;
	               }
	            } catch (InterruptedException e) {
	               // do nothing
	            } finally {
	               finish();
	               Intent i = new Intent();
	               i.setClassName("com.acidnom.yellrage",
	                              "com.acidnom.yellrage.YellMenu");
	               startActivity(i);
	            }
	         }
	      };
	      splashThread.start();
	   }

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	}