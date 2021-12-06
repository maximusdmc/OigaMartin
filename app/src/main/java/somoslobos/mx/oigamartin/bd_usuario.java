package somoslobos.mx.oigamartin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MaXiMuS on 11/03/2017.
 */

public class bd_usuario extends SQLiteOpenHelper {

    private String crea_bd_usuario = "CREATE TABLE Usuario (nombre VARCHAR(50), apellidos VARCHAR(50), correo VARCHAR(50), telefono INT(10))";
    private String borrar_bd = "DROP TABLE IF EXISTS Usuario";

    bd_usuario(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {
        //CREAR BD
        bd.execSQL(crea_bd_usuario);
    }

    @Override
    public void onUpgrade(SQLiteDatabase bd, int i, int i1) {
        //BORRAR Y VOLVER A HACER LA BD SI ACTUALIZA VERSION
        bd.execSQL(borrar_bd);
        bd.execSQL(crea_bd_usuario);
    }

}
