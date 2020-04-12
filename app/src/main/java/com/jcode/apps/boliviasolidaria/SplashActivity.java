package com.jcode.apps.boliviasolidaria;

//---------IMPORT---------------------------

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

//------------------------------------------

public class SplashActivity extends Activity {
	// ------DECLARE VARIABLE----------------
	private int SPLASH_DISPLAY_LENGTH = 2500;

	// ------ONCREATE------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		// --HANDLER FOR SPLASH--------------
		// initConfigurations();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (Build.VERSION.SDK_INT < 23) {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					askForPermissions();
				}
			};
		}, SPLASH_DISPLAY_LENGTH);

	}


	@TargetApi(23)
	public void askForPermissions() {
		//TODO: Comentar permiso de audio para Diplast
		if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
				this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
				this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			this.requestPermissions(new String[] {
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION}, 101);
		} else {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		boolean result = true;
		for (int i = 0; i < grantResults.length; i++) {
			if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
				result = false;
			}
		}

		if (result) {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(this, "Rechazo los permisos, saliendo de la aplicacion", Toast.LENGTH_SHORT).show();
		}
		finish();
	}
}