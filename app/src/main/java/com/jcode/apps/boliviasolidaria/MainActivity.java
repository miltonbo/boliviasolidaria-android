package com.jcode.apps.boliviasolidaria;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > 22) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                this.requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                return;
            }
        }
    }

    public void onClickRequestHelp(View view) {
//        Intent intent = new Intent(this, RequestActivity.class);
        Intent intent = new Intent(this, ListHelRequestActivity.class);
        startActivity(intent);
    }

    public void onClickDonateProducts(View view) {
        Intent intent = new Intent(this, MapPointsActivity.class);
        startActivity(intent);
    }

    public void onClickDonateMoney(View view) {
        Intent intent = new Intent(this, MoneyHelpActivity.class);
        startActivity(intent);
    }
}
