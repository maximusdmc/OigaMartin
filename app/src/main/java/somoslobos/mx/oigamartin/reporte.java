package somoslobos.mx.oigamartin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;


import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;


import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * Created by MaXiMuS on 01/04/2017.
 */

public class reporte extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISO_READ_EXTERNAL = 2;
    private static String APP_DIRECTORY = "OigaMartin/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "Fotos";

    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;

    private ImageView muestra_foto;
    private ImageButton btn_enviar;
    private EditText ingresa_reporte;

    int PERMISO_FINE_LOCATION = 1;
    private String mPath, nombre_foto;
    LatLng marker = new LatLng(26.931876, -105.664486);
    Uri photoURI, filePath;
    String path, ruta_segun_eleccion = null, nombre, apellidos, telefono, correo, t_reporte;
    double lat_rep;
    double lng_rep;
    Editable reporte;
    SQLiteDatabase bd;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        t_reporte = getIntent().getStringExtra("tipo_reporte");


        mapFragment.getMapAsync(this);

        muestra_foto = (ImageView) findViewById(R.id.set_picture);
        btn_enviar = (ImageButton) findViewById(R.id.btn_enviar);
        ingresa_reporte = (EditText) findViewById(R.id.ingresa_reporte);

        muestra_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muestraOpciones();
            }
        });

        btn_enviar.setOnClickListener(view -> enviarReporte());
    }

    public void enviarReporte() {

        bd_usuario user =
                new bd_usuario(this, "bd_usuario", null, 1);
        bd = user.getWritableDatabase();
        Cursor cursor = bd.rawQuery("SELECT * FROM Usuario", null);
        if (cursor.moveToFirst()) {
            do {
                nombre = cursor.getString(0);
                apellidos = cursor.getString(1);
                correo = cursor.getString(2);
                telefono = cursor.getString(3);
            } while (cursor.moveToNext());
        }
        cursor.moveToFirst();
        cursor.close();
        reporte = ingresa_reporte.getText();

        try {
            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, uploadId, Constants.UPLOAD_URL)

                    .addFileToUpload(ruta_segun_eleccion, "image")
                    .addParameter("name", nombre_foto)
                    .addParameter("nombre", nombre)
                    .addParameter("apellidos", apellidos)
                    .addParameter("telefono", telefono)
                    .addParameter("correo", correo)
                    .addParameter("t_reporte", t_reporte)
                    .addParameter("reporte", reporte.toString())
                    .addParameter("lat", String.valueOf(lat_rep))
                    .addParameter("lng", String.valueOf(lng_rep))

                    .setNotificationConfig(new UploadNotificationConfig()
                            .setNotificationChannelId("TEST")
                            .setTitleForAllStatuses("OIGA MARTIN"))

                    .setUtf8Charset()
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Intent i = new Intent(reporte.this, principal.class);
                    startActivity(i);
                    finish();
                }
            };

            Timer timer = new Timer();
            timer.schedule(task, 500);
            Toast.makeText(this, "Enviando Reporte...", Toast.LENGTH_LONG).show();
        } catch (Exception exc) {
            Toast.makeText(this, "Agrega una imagen a tu reporte", Toast.LENGTH_SHORT).show();
        }
    }

    private void muestraOpciones() {
        final CharSequence[] option = {"Tomar foto", "Elegir de Galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(reporte.this);
        builder.setTitle("Elige Una Opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option[which] == "Tomar foto"){
                    abrirCamara();
                }else if(option[which] == "Elegir de Galeria"){
                    abrirGaleria();
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void abrirCamara() {



        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if(!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if(isDirectoryCreated){
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";
            nombre_foto = imageName;
            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            photoURI = FileProvider.getUriForFile(reporte.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    newFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, PHOTO_CODE);
            ruta_segun_eleccion = mPath;

        }
    }

    private void abrirGaleria(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISO_READ_EXTERNAL);
            }
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona foto"), SELECT_PICTURE);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    muestra_foto.setImageBitmap(bitmap);
                    break;
                case SELECT_PICTURE:
                    filePath = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        muestra_foto.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    path = getPath(filePath);
                    ruta_segun_eleccion = path.toString();
                    Random randomGenerator = new Random(6);
                    nombre_foto = randomGenerator.toString();

                    break;

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onMapReady(final GoogleMap googleMap) {

        googleMap.setMyLocationEnabled(false);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_FINE_LOCATION);
            }
        }
        //googleMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition camPos = new CameraPosition.Builder()
                .target(marker)
                .zoom(13)
                .bearing(45)
                .tilt(45)
                .build();

        CameraUpdate camUpd3 =
                CameraUpdateFactory.newCameraPosition(camPos);

        googleMap.animateCamera(camUpd3);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                lat_rep = latLng.latitude;
                lng_rep = latLng.longitude;

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng));

                CameraPosition camPos = new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(18)
                        .tilt(45)
                        .build();
                CameraUpdate camUpd3 =
                        CameraUpdateFactory.newCameraPosition(camPos);

                googleMap.animateCamera(camUpd3);

            }
        });

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


}