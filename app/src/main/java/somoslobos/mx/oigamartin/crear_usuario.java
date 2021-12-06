package somoslobos.mx.oigamartin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by MaXiMuS on 05/03/2017.
 */

public class crear_usuario extends Activity implements View.OnClickListener{

    EditText ingresa_nombre, ingresa_apellidos, ingresa_correo, ingresa_tel;
    Button btn_guardar, btn_salir;
    SQLiteDatabase bd;
    String nombre, apellidos, ap_mat, correo, telefono, falta_nombre , falta_apellidos , falta_correo , falta_telefono, primer_inicio ;
    SharedPreferences prefs;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_usuario);
        prefs = getSharedPreferences("somoslobos.mx.oigamartin", MODE_PRIVATE);
        prefs.edit().putBoolean("firstrun", false).apply();

        primer_inicio = getIntent().getStringExtra("primer_inicio");
        if (primer_inicio.equals("si")){
            falta_nombre = "si";
            falta_apellidos = "si";
            falta_telefono = "si";
            falta_correo = "si";
        }else {
            falta_nombre = "no";
            falta_apellidos = "no";
            falta_telefono = "no";
            falta_correo = "no";
        }

        btn_guardar    = findViewById(R.id.btn_guardar);
        btn_salir      = findViewById(R.id.btn_salir);
        ingresa_nombre = findViewById(R.id.ingresa_nombre);
        ingresa_apellidos = findViewById(R.id.ingresa_apellidos);
        ingresa_correo = findViewById(R.id.ingresa_correo);
        ingresa_tel    = findViewById(R.id.ingresa_tel);
        ingresa_tel.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10)});

        btn_guardar.setOnClickListener(this);
        btn_salir.setOnClickListener(this);

        //CONSULTAMOS BD Y LLENAMOS CAMPOS
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
        ingresa_nombre.setText(nombre );
        ingresa_apellidos.setText(apellidos);
        ingresa_correo.setText(correo);
        ingresa_tel.setText(telefono);
        cursor.close();

        ingresa_nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    falta_nombre = "si";
                }
                else{
                    falta_nombre = "no";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ingresa_apellidos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    falta_apellidos = "si";
                }
                else{
                    falta_apellidos = "no";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ingresa_correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    falta_correo = "si";
                }
                else{
                    falta_correo = "no";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        ingresa_tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    falta_telefono = "si";
                }
                else{
                    falta_telefono = "no";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_guardar:
                //VERIFICAMOS SI FALTA ALGUN DATO
                if (falta_nombre.equals("si") || falta_apellidos.equals("si") ||
                        falta_telefono.equals("si") || falta_correo.equals("si")){
                    Toast.makeText(this, "Rellena Todos Los Campos", Toast.LENGTH_LONG).show();

                 //SI NO FALTAN DATOS ENTONCES GUARDARA EL USUARIO
                }else {
                    //JALAMOS TODOS LOS DATOS
                    nombre = ingresa_nombre.getText().toString();
                    apellidos = ingresa_apellidos.getText().toString();
                    correo = ingresa_correo.getText().toString();
                    telefono = ingresa_tel.getText().toString();

                    //ABRIMOS LA BASE DE DATOS
                    bd_usuario user =
                            new bd_usuario(this, "bd_usuario", null, 1);
                    bd = user.getWritableDatabase();
                    bd.execSQL("DROP TABLE Usuario");
                    bd.execSQL("CREATE TABLE Usuario (nombre VARCHAR(50), apellidos VARCHAR(100), correo VARCHAR(50), telefono INT(20))");
                    bd.execSQL("INSERT INTO Usuario (nombre , apellidos , correo , telefono) VALUES ('" + nombre + "', '" +  apellidos+ "', '" + correo + "', '" + telefono + "' )");

                    //LANZAMOS LA ACTIVIDAD PRINCIPAL SI LOS DATOS SE GUARDARON CORRECTAMENTE
                    //Toast.makeText(this, "Usuario Guardado", Toast.LENGTH_LONG).show();
                    prefs.edit().putBoolean("firstrun", false).apply();

                    Intent i = new Intent(this, principal.class);
                    startActivity(i);
                    finish();
                }
                break;
            case R.id.btn_salir:
                finish();
                System.exit(0);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed (){

    }
}
