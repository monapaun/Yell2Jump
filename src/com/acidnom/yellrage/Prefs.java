package com.acidnom.yellrage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class Prefs extends Activity implements OnClickListener {
	private int mode=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefs);
		
		View continueButton = findViewById(R.id.sound_button);
		continueButton.setOnClickListener(this);
		
		View newButton = findViewById(R.id.touch_button);
		newButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sound_button:
				YellMenu.mode=1;
				finish();
				break;
			case R.id.touch_button:
				YellMenu.mode=2;
				finish();
				break;
				// More buttons go here (if any) ...
			}
		}
}