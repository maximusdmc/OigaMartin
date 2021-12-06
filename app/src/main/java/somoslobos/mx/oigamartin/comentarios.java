package somoslobos.mx.oigamartin;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.xml.transform.URIResolver;

public class comentarios extends Activity {

    public static final String CHANNEL_ID = "1";
    public static final String CHANNEL_NAME = "Oiga Martin!";
    EditText sugerencia;
    ImageButton btn_enviar;
    SQLiteDatabase bd;
    String nombre;
    String apellidos;
    String correo;
    String telefono;
    String ruta_icono = null;
    Editable txt_sugerencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentarios);
        sugerencia = findViewById(R.id.ingresa_sugerencia);
        btn_enviar = findViewById(R.id.btn_enviar);

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
        txt_sugerencia = sugerencia.getText();

//        try {
//            String uploadId = UUID.randomUUID().toString();
//
//            new MultipartUploadRequest(this, uploadId, Constants.UPLOAD_URL)
//
//                    .addFileToUpload(ruta_icono, "image")
//                    .addParameter("name", "SUGERENCIA")
//                    .addParameter("nombre", nombre)
//                    .addParameter("apellidos", apellidos)
//                    .addParameter("telefono", telefono)
//                    .addParameter("correo", correo)
//                    .addParameter("t_reporte", "COMENTARIO")
//                    .addParameter("reporte", txt_sugerencia.toString())
//                    .addParameter("lat", "COMENTARIO")
//                    .addParameter("lng", "COMENTARIO")
//                    .setMaxRetries(2)
//                    .setUtf8Charset()
//
//                    .setNotificationConfig(new UploadNotificationConfig()
//                            .setTitle("Oiga Martin!")
//                            .setInProgressMessage("Enviando Reporte")
//                            .setCompletedMessage("Reporte Enviado")
//                            .setClearOnAction(true))
//                    .startUpload();
//
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    Intent i = new Intent(comentarios.this, principal.class);
//                    startActivity(i);
//                    finish();
//                }
//            };
//
//            Timer timer = new Timer();
//            timer.schedule(task, 500);
//            Toast.makeText(this, "Enviando Reporte...", Toast.LENGTH_LONG).show();
//        } catch (Exception exc) {
//            Toast.makeText(this, "Error al enviar tu comentario", Toast.LENGTH_SHORT).show();
//        }
        try {
            String PdfID = UUID.randomUUID().toString();

            MultipartUploadRequest uploadRequest = new MultipartUploadRequest(this, PdfID, Constants.UPLOAD_URL)
                    .addFileToUpload(ruta_icono, "image")
                    .addParameter("name", "SUGERENCIA")
                    .addParameter("nombre", nombre)
                    .addParameter("apellidos", apellidos)
                    .addParameter("telefono", telefono)
                    .addParameter("correo", correo)
                    .addParameter("t_reporte", "COMENTARIO")
                    .addParameter("reporte", txt_sugerencia.toString())
                    .addParameter("lat", "COMENTARIO")
                    .addParameter("lng", "COMENTARIO")
                    .setMaxRetries(5);

            // For Android > 8, we need to set an Channel to the UploadNotificationConfig.
            // So, here, we create the channel and set it to the MultipartUploadRequest
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("Upload", "Upload service", NotificationManager.IMPORTANCE_DEFAULT);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

                UploadNotificationConfig notificationConfig = new UploadNotificationConfig();

                notificationConfig.setNotificationChannelId("Upload");

                uploadRequest.setNotificationConfig(notificationConfig);
            } else {
                // If android < Oreo, just set a simple notification (or remove if you don't wanna any notification
                // Notification is mandatory for Android > 8
                uploadRequest.setNotificationConfig(new UploadNotificationConfig());
            }

            uploadRequest.startUpload();
            Toast.makeText(this, " Upload Successful ", Toast.LENGTH_SHORT).show();
            Log.d("Upload Procedure", "PdfUploadFunction:  -------SUCESS");
        }
        catch (Exception exception) {
            Log.d("Upload Procedure", "PdfUploadFunction: Fail");
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

}
