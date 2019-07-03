package sv.edu.uesocc2019.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "circulos_y_marcadores";
    private static final String CREAR_TABLA_MARCADORES = "CREATE TABLE marcadores (id INTEGER PRIMARY KEY," +
            " latitud DOUBLE, longitud DOUBLE, titulo TEXT, arrastrable INTEGER)";
    private static final String CREAR_TABLA_CIRCULOS = "CREATE TABLE circulos (id INTEGER PRIMARY KEY," +
            " latitud DOUBLE, longitud DOUBLE, radio INTEGER, color_borde INTEGER, color_relleno Integer," +
            " ancho_borde INTEGER)";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA_CIRCULOS);
        db.execSQL(CREAR_TABLA_MARCADORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS circulos");
        db.execSQL("DROP TABLE IF EXISTS marcadores");
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS circulos");
        db.execSQL("DROP TABLE IF EXISTS marcadores");
        onCreate(db);
    }
}
