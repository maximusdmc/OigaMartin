package somoslobos.mx.oigamartin;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class principal extends Activity implements View.OnClickListener {

    ImageButton btn_usuario, btn_salir, btn_bache, btn_lampara, btn_basura, btn_jmas, btn_contacto, btn_comentarios;
    TextView muestra_nombre;
    SharedPreferences prefs = null;
    SQLiteDatabase bd;
    String nombre;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        btn_usuario = findViewById(R.id.btn_edit_usuario);
        btn_salir = findViewById(R.id.btn_salir);
        btn_bache = findViewById(R.id.btn_bacheo);
        btn_lampara = findViewById(R.id.btn_lampara);
        btn_basura = findViewById(R.id.btn_basura);
        btn_jmas = findViewById(R.id.btn_fugas);
        btn_contacto = findViewById(R.id.btn_contacto);
        btn_comentarios = findViewById(R.id.btn_comentarios);

        btn_usuario.setOnClickListener(this);
        btn_salir.setOnClickListener(this);
        btn_bache.setOnClickListener(this);
        btn_lampara.setOnClickListener(this);
        btn_basura.setOnClickListener(this);
        btn_jmas.setOnClickListener(this);
        btn_contacto.setOnClickListener(this);
        btn_comentarios.setOnClickListener(this);

        muestra_nombre = findViewById(R.id.muestra_nombre);

        bd_usuario user =
                new bd_usuario(this, "bd_usuario", null, 1);
        bd = user.getWritableDatabase();
        bd.execSQL("CREATE TABLE IF NOT EXISTS Usuario (nombre VARCHAR(50), ap_pat VARCHAR(50), ap_mat VARCHAR(50), correo VARCHAR(50), telefono VARCHAR(20))");
        prefs = getSharedPreferences("somoslobos.mx.oigamartin", MODE_PRIVATE);

        Cursor cursor = bd.rawQuery("SELECT * FROM Usuario", null);
        if (cursor.moveToFirst()) {
            do {
                nombre = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.moveToFirst();
        muestra_nombre.setText("Bienvenido  " +nombre );
        cursor.close();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            //prefs.edit().putBoolean("firstrun", false).commit();
            Intent i = new Intent(this, crear_usuario.class );
            i.putExtra( "primer_inicio", "si");
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_edit_usuario:
                prefs.edit().putBoolean("firstrun", true).apply();
                Intent act_crea_usr = new Intent(this, crear_usuario.class );
                act_crea_usr.putExtra("primer_inicio", "no");
                startActivity(act_crea_usr);
                finish();
                break;
            case R.id.btn_salir:
                finish();
                System.exit(0);
                break;

            case R.id.btn_lampara:
                Intent cfe = new Intent(this, pruebas.class);
                cfe.putExtra("tipo_reporte", "cfe");
                startActivity(cfe);
                break;
            case R.id.btn_bacheo:
                Intent bacheo = new Intent(this, pruebas.class );
                bacheo.putExtra("tipo_reporte", "ob_publicas");
                startActivity(bacheo);
                break;
            case R.id.btn_basura:
                Intent serv_pub = new Intent(this, pruebas.class);
                serv_pub.putExtra("tipo_reporte", "serv_pub");
                startActivity(serv_pub);
                break;
            case R.id.btn_fugas:
                Intent jmas = new Intent(this, pruebas.class);
                jmas.putExtra("tipo_reporte", "jmas");
                startActivity(jmas);
                break;
            case R.id.btn_contacto:
                Intent contacto = new Intent(this, contacto.class);
                startActivity(contacto);
                break;
            case R.id.btn_comentarios:
                Intent comentarios = new Intent(this, comentarios.class);
                startActivity(comentarios);
                break;
            default:
                break;
        }
    }

}