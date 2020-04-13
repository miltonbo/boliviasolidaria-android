package com.jcode.apps.boliviasolidaria;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jcode.apps.boliviasolidaria.entity.HelpRequest;
import com.jcode.apps.boliviasolidaria.entity.PointDistribution;
import com.jcode.apps.boliviasolidaria.entity.PointRequest;
import com.jcode.apps.boliviasolidaria.web.Api;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HelpRequestDetailActivity extends FragmentActivity {

    private GoogleMap map;
    private HelpRequest hr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_request_detail);

        hr = (HelpRequest) getIntent().getExtras().get("helpRequest");

        ((TextView) findViewById(R.id.tvName)).setText(hr.getNombre());
        ((TextView) findViewById(R.id.tvDesc)).setText(hr.getNecesidad());
        ((TextView) findViewById(R.id.tvPhone)).setText(hr.getContacto());
        ((TextView) findViewById(R.id.tvAddress)).setText(hr.getDireccion());

        loadMap();

        if (!isEnabledGPS(this)) {
            Toast.makeText(this, "Debe Activar el GPS", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickCI(View view) {

    }

    public void onClickCopyPhone(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto copiado", hr.getContacto());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Texto copiado", Toast.LENGTH_SHORT).show();
    }

    public void onClickCallPhone(View view) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + hr.getContacto()));
        startActivity(i);
    }

    public void onClickCopyAddress(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto copiado", hr.getDireccion());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Texto copiado", Toast.LENGTH_SHORT).show();
    }

    private void loadMap() {
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(googleMap -> {
            map = googleMap;
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setTiltGesturesEnabled(false);

            if (Build.VERSION.SDK_INT > 22)
                map.setMyLocationEnabled(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            else
                map.setMyLocationEnabled(true);

            LatLng position = new LatLng(hr.getLat(), hr.getLng());
            CameraPosition camPos = new CameraPosition.Builder().target(position).zoom(15f).build();
            CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
            map.moveCamera(camUpd);
            Bitmap bmMarker = BitmapFactory.decodeResource(getResources(), R.drawable.marker_red1);
            BitmapDescriptor bmd = BitmapDescriptorFactory.fromBitmap(bmMarker);
            map.addMarker(new MarkerOptions().position(new LatLng(hr.getLat(), hr.getLng())).title(hr.getNombre()).icon(bmd));
        });
    }

    public static boolean isEnabledGPS(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
