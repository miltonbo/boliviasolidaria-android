package com.jcode.apps.boliviasolidaria;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import com.jcode.apps.boliviasolidaria.utils.FilterImage;
import com.jcode.apps.boliviasolidaria.utils.ImageManager;
import com.jcode.apps.boliviasolidaria.web.Api;
import com.jcode.apps.boliviasolidaria.web.ApiInterface;
import com.jcode.apps.boliviasolidaria.web.BodyFile;
import com.jcode.apps.boliviasolidaria.web.ServerResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPointsActivity extends FragmentActivity {

    private GoogleMap map;
    private ProgressDialog progress;
    private ProgressDialog progressRequests;
    private ProgressDialog progresCenters;

    private HelpRequest helpRequest = new HelpRequest();

    private CheckBox cbRequest;
    private CheckBox cbCenters;

    private Marker markerSelected;
    private HashMap<Marker, PointRequest> hmMarkerRequest = new HashMap<>();
    private HashMap<Marker, PointDistribution> hmMarkerPointDist = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_points);
        progress = new ProgressDialog(this);
        progress.setTitle("Cargando...");
        cbRequest = findViewById(R.id.cbRequest);
        cbCenters = findViewById(R.id.cbCenters);

        cbRequest.setOnCheckedChangeListener(onCheck);
        cbCenters.setOnCheckedChangeListener(onCheck);

        loadMap();

        if (!isEnabledGPS(this)) {
            Toast.makeText(this, "Debe Activar el GPS", Toast.LENGTH_LONG).show();
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (map != null) {
                map.clear();
                hmMarkerPointDist.clear();
                hmMarkerRequest.clear();
                if (cbCenters.isChecked()) {
                    getCenters();
                }
                if (cbRequest.isChecked()) {
                    getRequest();
                }
            }
        }
    };


    private void getRequest() {
        progressRequests = new ProgressDialog(this);
        progressRequests.setTitle("Cargando Solicitudes ...");
        Call<List<PointRequest>> call = Api.getService().listPointRequests();
        call.enqueue(new Callback<List<PointRequest>>() {
            @Override
            public void onResponse(Call<List<PointRequest>> call, Response<List<PointRequest>> response) {
                progressRequests.cancel();
                if (response.body() != null) {
                    List<PointRequest> list = response.body();
                    hmMarkerRequest.clear();
                    BitmapDescriptor bmd = BitmapDescriptorFactory.fromBitmap(getBitmapMarker(R.drawable.marker_red1));
                    for (PointRequest pr : list) {
                        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(pr.getLat(), pr.getLng())).icon(bmd));
                        hmMarkerRequest.put(marker, pr);
                    }
                } else {
                    Toast.makeText(MapPointsActivity.this, "Fallo al descargar las solicitudes.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PointRequest>> call, Throwable t) {
                progressRequests.cancel();
                Toast.makeText(MapPointsActivity.this, "Fallo al descargar las solicitudes.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getCenters() {
        progresCenters = new ProgressDialog(this);
        progresCenters.setTitle("Cargando Centros de Distribución ...");
        Call<List<PointDistribution>> call = Api.getService().listPointCenters();
        call.enqueue(new Callback<List<PointDistribution>>() {
            @Override
            public void onResponse(Call<List<PointDistribution>> call, Response<List<PointDistribution>> response) {
                progresCenters.cancel();
                if (response.body() != null) {
                    List<PointDistribution> list = response.body();
                    hmMarkerPointDist.clear();
                    BitmapDescriptor bmd = BitmapDescriptorFactory.fromBitmap(getBitmapMarker(R.drawable.marker_blue1));
                    for (PointDistribution pd : list) {
                        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(pd.getLat(), pd.getLng())).title(pd.getTitulo()).icon(bmd));
                        hmMarkerPointDist.put(marker, pd);
                    }
                } else {
                    Toast.makeText(MapPointsActivity.this, "Fallo al descargar los centros de Distribución.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PointDistribution>> call, Throwable t) {
                progresCenters.cancel();
                Toast.makeText(MapPointsActivity.this, "Fallo al descargar los centros de Distribución.", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void loadMap() {
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(googleMap -> {
            map = googleMap;
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setTiltGesturesEnabled(false);

            map.setOnMarkerClickListener(marker -> {
                markerSelected = marker;
                return false;
            });

            getRequest();
            getCenters();

            if (Build.VERSION.SDK_INT > 22)
                map.setMyLocationEnabled(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            else
                map.setMyLocationEnabled(true);

            if (isEnabledGPS(MapPointsActivity.this)) {
                progress.show();
                map.setOnMyLocationChangeListener(location -> {
                    progress.cancel();
                    LatLng GPS = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition camPos = new CameraPosition.Builder().target(GPS).zoom(15f).build();
                    CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
                    map.moveCamera(camUpd);
                    map.setOnMyLocationChangeListener(null);
                });
                return;
            } else {
                LatLng position = new LatLng(-17.038417, -64.559780);
                CameraPosition camPos = new CameraPosition.Builder().target(position).zoom(8f).build();
                CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
                map.moveCamera(camUpd);
            }
        });
    }

    public static boolean isEnabledGPS(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    HashMap<Integer, Bitmap> hmBitmapMarkers = new HashMap<Integer, Bitmap>();

    private Bitmap getBitmapMarker(int markerId) {
        if (hmBitmapMarkers.get(markerId) == null) {
            Bitmap bmMarker = BitmapFactory.decodeResource(getResources(), markerId);
            hmBitmapMarkers.put(markerId, bmMarker);
        }
        return hmBitmapMarkers.get(markerId);
    }

}
