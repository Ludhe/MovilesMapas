package sv.edu.uesocc2019.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "circulos_y_marcadores";
    private static final String CREAR_TABLA_MARCADORES = "";
    private static final String CREAR_TABLA_CIRCULOS = "";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String crearTablaCarreras = "CREATE TABLE carreras (codigo TEXT PRIMARY KEY, nombre TEXT)";
        String crearTablaAlumnos = "CREATE TABLE estudiantes (id INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, " +
                "sexo INTEGER, carnet TEXT, carrera TEXT, FOREIGN KEY (carrera) REFERENCES carreras(codigo))";
        db.execSQL(crearTablaCarreras);
        db.execSQL(crearTablaAlumnos);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
