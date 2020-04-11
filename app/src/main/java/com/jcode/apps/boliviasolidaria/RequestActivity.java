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
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.jcode.apps.boliviasolidaria.entity.HelpRequest;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private File photoFileToSend;

    private HelpRequest helpRequest = new HelpRequest();

    private EditText etDesc;
    private EditText etName;
    private EditText etPhone;
    private EditText etCI;
    private EditText etAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        progress = new ProgressDialog(this);
        progress.setTitle("Buscando ubicacion...");
        lyData1 = findViewById(R.id.lyData1);
        lyData2 = findViewById(R.id.lyData2);
        btnBack = findViewById(R.id.btnBack);
        etDesc = findViewById(R.id.etDesc);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etCI = findViewById(R.id.etCI);
        etAddress = findViewById(R.id.etAddress);

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

        String msgValid = "";
        ivCI.setBackgroundResource(R.drawable.edit_text);
        if (map == null) {
            msgValid += " - No se logro cargar la ubicación (Revise conexión o intente mas tarde).\n";
        }

        if (photoFileToSend == null) {
            ivCI.setBackgroundResource(R.drawable.edit_text_invalid);
            msgValid += " - Debe tomar una Foto del Carnet.\n";
        }

        if (!msgValid.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(msgValid)
                    .setTitle("Revise sus datos");
            builder.create().show();
            return;
        }

        position = map.getCameraPosition().target;
        helpRequest.setLat(position.latitude);
        helpRequest.setLng(position.longitude);

        ApiInterface api = Api.getService();
        Call<ServerResponse> call = api.sendHelpRequest(helpRequest);
        progress.setTitle("Enviando solicitud...");
        progress.show();
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.body().getId() > 0) {
                    helpRequest.setId(response.body().getId());
                    savePhoto();
                } else {
                    progress.cancel();
                    Toast.makeText(RequestActivity.this, "Fallo al enviar Solicitu.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.cancel();
                Toast.makeText(RequestActivity.this, "Fallo al enviar Solicitu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePhoto() {
        BodyFile bf = new BodyFile();
        bf.setFoto(readFileToByteArray(photoFileToSend));

        Call<String> call = Api.getService().sendPhotoCI2(helpRequest.getId(), bf);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progress.cancel();
                String r = response.body();
                if (r == null || r.isEmpty()) {
                    Toast.makeText(RequestActivity.this, "Guardado correctamente.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RequestActivity.this, "Fallo al enviar Imagen.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progress.cancel();
                Toast.makeText(RequestActivity.this, "Fallo al enviar Imagen.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method uses java.io.FileInputStream to read
     * file content into a byte array
     *
     * @param file
     * @return
     */
    private static byte[] readFileToByteArray(File file) {
        FileInputStream fis = null;
        // Creating a byte array using the length of the file
        // file.length returns long which is cast to int
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();

        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        }
        return bArray;
    }

    private MultipartBody.Part prepareFilePart(String partName, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", this.photoFile);
        } else {
            fileUri = Uri.fromFile(this.photoFile);
        }
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    private void step2() {

        String desc = etDesc.getText().toString().trim();
        String ci = etCI.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        etDesc.setBackgroundResource(R.drawable.edit_text);
        etCI.setBackgroundResource(R.drawable.edit_text);
        etName.setBackgroundResource(R.drawable.edit_text);
        etPhone.setBackgroundResource(R.drawable.edit_text);
        etAddress.setBackgroundResource(R.drawable.edit_text);

        String msgValid = "";

        if (desc.length() < 3) {
            etDesc.setBackgroundResource(R.drawable.edit_text_invalid);
            msgValid += " - Debe ingresar ¿Que ayuda necesita?.\n";
        }
        if (ci.length() < 6) {
            etCI.setBackgroundResource(R.drawable.edit_text_invalid);
            msgValid += " - CI o documento inválido (6 números).\n";
        }
        if (name.length() < 9) {
            etName.setBackgroundResource(R.drawable.edit_text_invalid);
            msgValid += " - Nombre inválido (Muy corto minimo 9 caracteres).\n";
        }
        if (phone.length() < 6) {
            etPhone.setBackgroundResource(R.drawable.edit_text_invalid);
            msgValid += " - Celular o teléfono inválido (6 números).\n";
        }

        if (address.length() < 6) {
            etAddress.setBackgroundResource(R.drawable.edit_text_invalid);
            msgValid += " - Dirección inválida (Muy corta minimo 10 caracteres).\n";
        }

        if (!msgValid.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(msgValid)
                    .setTitle("Revise sus datos");
            builder.create().show();
            return;
        }

        helpRequest.setId(0L);
        helpRequest.setNecesidad(desc);
        helpRequest.setCi(ci);
        helpRequest.setNombre(name);
        helpRequest.setContacto(phone);
        helpRequest.setDireccion(address);


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
                    photoFileToSend = saveImageOnDirectory(photoFile, exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                    photoFile.delete();
                    Glide.with(RequestActivity.this).load(photoFileToSend.getAbsolutePath()).into(ivCI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;

            case REQUEST_ATTACH_FILE_DIALOG:
                try {
                    Uri photoUri = data.getData();
                    String path = getRealPathFromURI(photoUri);
                    ExifInterface exif = new ExifInterface(path);
                    photoFileToSend = saveImageOnDirectory(new File(path), exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                    Glide.with(RequestActivity.this).load(photoFileToSend.getAbsolutePath()).into(ivCI);
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
