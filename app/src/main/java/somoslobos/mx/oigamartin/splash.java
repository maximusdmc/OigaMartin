package somoslobos.mx.oigamartin;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by MaXiMuS on 02/04/2017.
 */

public class splash extends Activity {

    private static final long tiempo_splash = 1000;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.splash);
        revisarPermisos();
        GifImageView gifImageView = findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.spinner7);

    }

    public void lanzarApp() {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(splash.this, principal.class);
                startActivity(i);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, tiempo_splash);
    }
    private void revisarPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            lanzarApp();
            //Toast.makeText(this, "Esta version de android es inferior a la 6 " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
        } else {

            int permisoCamara = checkSelfPermission(Manifest.permission.CAMERA);
            int permisoStorage= checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int permisoLocation1 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int permisoLocation2 = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if ((permisoCamara != PackageManager.PERMISSION_GRANTED)
                    || (permisoStorage != PackageManager.PERMISSION_GRANTED)
                    || (permisoLocation1 != PackageManager.PERMISSION_GRANTED)
                    || (permisoLocation2 != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[] {Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);

            }else {
                lanzarApp();
                //Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lanzarApp();
                //Toast.makeText(this, "Gracias!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Necesitas otorgar los permisos necesarios" + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
                revisarPermisos();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
