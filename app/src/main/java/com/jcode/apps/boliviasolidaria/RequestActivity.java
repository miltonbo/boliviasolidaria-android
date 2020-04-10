package com.jcode.apps.boliviasolidaria;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.jcode.apps.boliviasolidaria.utils.FilterImage;
import com.jcode.apps.boliviasolidaria.utils.ImageManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestActivity extends FragmentActivity {

    private static final int REQUEST_ATTACH_FILE_DIALOG = 0x0203;
    private static final int REQUEST_IMAGE_CAPTURE = 0x0204;

    private GoogleMap map;
    private LatLng position = new LatLng(0D, 0D);
    private ProgressDialog progress;

    private LinearLayout lyData1;
    private LinearLayout lyData2;

    private Button btnBack;
    private Button btnSaveRequest;
    private ImageView ivCI;

    private File photoFile;
    private String photo_doc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        progress = new ProgressDialog(this);
        progress.setTitle("Buscando ubicacion...");
        lyData1 = findViewById(R.id.lyData1);
        lyData2 = findViewById(R.id.lyData2);
        btnBack = findViewById(R.id.btnBack);
        ivCI = findViewById(R.id.ivCI);
        btnSaveRequest = findViewById(R.id.btnSaveRequest);
        loadMap();

        if (!isEnabledGPS(this)) {
            Toast.makeText(this, "Debe Activar el GPS", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickBack(View view) {
        btnBack.setVisibility(View.GONE);
        btnSaveRequest.setText("CONTINUAR");
        lyData1.setVisibility(View.VISIBLE);
        lyData2.setVisibility(View.GONE);
    }

    public void onClickSave(View view) {
        if (lyData1.getVisibility() == View.VISIBLE) {
            step2();
        } else {
            saveRequest();
        }
    }

    private void saveRequest() {

    }

    private void step2() {

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

        if (!isEnabledGPS(this)) {
            Toast.makeText(this, "Debe Activar el GPS", Toast.LENGTH_LONG).show();
            return;
        }
        lyData1.setVisibility(View.GONE);
        lyData2.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnSaveRequest.setText("SOLICITAR");
        if (map == null) {
            loadMap();
        } else {
            loadGpsMapLocation();
        }
    }

    private void loadMap() {
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setTiltGesturesEnabled(false);

                if (Build.VERSION.SDK_INT > 22)
                    map.setMyLocationEnabled(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
                else
                    map.setMyLocationEnabled(true);
                if (lyData2.getVisibility() == View.VISIBLE) {
                    loadGpsMapLocation();
                }
            }
        });
    }

    private void loadGpsMapLocation() {

        if (Build.VERSION.SDK_INT > 22)
            map.setMyLocationEnabled(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        else
            map.setMyLocationEnabled(true);

        if (isEnabledGPS(RequestActivity.this) && position.latitude == 0D) {
            progress.show();
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    progress.cancel();
                    LatLng GPS = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition camPos = new CameraPosition.Builder().target(GPS).zoom(15f).build();
                    CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
                    map.moveCamera(camUpd);
                    map.setOnMyLocationChangeListener(null);
                }
            });
            return;
        } else if (position.latitude != 0D) {
            CameraPosition camPos = new CameraPosition.Builder().target(position).zoom(15f).build();
            CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
            map.moveCamera(camUpd);
        }
    }

    public static boolean isEnabledGPS(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void onClickFromGallery(View view) {
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_ATTACH_FILE_DIALOG);
    }

    public void onClickFromCamera(View view) {
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                return;
            }
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                photoFile = null;
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", this.photoFile);
                } else {
                    uri = Uri.fromFile(this.photoFile);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            // Toast.makeText(this, "Ocurrio un problema con esta imágen.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                try {
                    ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());
                    File _file = saveImageOnDirectory(photoFile, exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                    photoFile.delete();
                    photo_doc = _file.getAbsolutePath();
                    Glide.with(RequestActivity.this).load(photo_doc).into(ivCI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;

            case REQUEST_ATTACH_FILE_DIALOG:
                try {
                    Uri photoUri = data.getData();
                    String path = getRealPathFromURI(photoUri);
                    ExifInterface exif = new ExifInterface(path);
                    File _file = saveImageOnDirectory(new File(path), exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                    photo_doc = _file.getAbsolutePath();
                    Glide.with(RequestActivity.this).load(photo_doc).into(ivCI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss_SSS").format(new Date());
        String imageFileName = timeStamp;
        File storageDir = App.getPhotoCache();
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        return image;
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private File saveImageOnDirectory(File fileImage, String orientation) {
        System.out.println(orientation);
        File root = App.getPhotoDir();
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
        File _file = new File(root, timeStamp + ".jpg");
        Bitmap bitmap = null;
        if (orientation.equals("" + ExifInterface.ORIENTATION_FLIP_VERTICAL) || orientation.equals("" + ExifInterface.ORIENTATION_ROTATE_90)) {
            bitmap = FilterImage.rotate(ImageManager.decodeSampledBitmapFromFile(fileImage.getAbsolutePath(), 640, 480), 90);
        } else {
            bitmap = ImageManager.decodeSampledBitmapFromFile(fileImage.getAbsolutePath(), 640, 480);
        }
        try {
            FileOutputStream _writer = new FileOutputStream(_file.getPath());
            _writer.write(toByteArray(bitmap, 80));
            _writer.close();
            bitmap.recycle();
            return _file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] toByteArray(Bitmap bitmap, int quality) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        return bos.toByteArray();
    }

}
