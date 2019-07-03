package sv.edu.uesocc2019.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    final SQLiteHelper sqlh = new SQLiteHelper(MapsActivity.this);
    SQLiteDatabase db;
    final ContentValues contenido = new ContentValues();
    final String[] tiposmapa =
            new String[]{"Normal", "Satelite", "Hibrido", "Terreno"};
    private Spinner cmbTiposMapa;
    private Button btnConfig;
    AlertDialog.Builder constructor;
    Context insideContext;
    SharedPreferences preferencias;
    Cursor circulos, marcadores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        db = sqlh.getWritableDatabase();
        circulos = db.query("circulos", null, null, null, null, null, null);
        marcadores = db.query("marcadores", null, null, null, null, null, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArrayAdapter<String> adaptador =
                new ArrayAdapter<String>(this,
                        android.R.layout.select_dialog_singlechoice, tiposmapa);

        cmbTiposMapa = (Spinner) findViewById(R.id.tipoMapa);
        btnConfig = findViewById(R.id.btnConfig);
        cmbTiposMapa.setAdapter(adaptador);

        cmbTiposMapa.setSelection(preferencias.getInt("tipo_mapa", 0));

        cmbTiposMapa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mMap != null) {
                    switch (i) {
                        case 0:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case 1:
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        case 2:
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                        case 3:
                            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento = new Intent(MapsActivity.this, Configuraciones.class);
                startActivity(intento);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        restaurarMarcadores();
        restaurarCirculos();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            } else {
                mMap.setMyLocationEnabled(preferencias.getBoolean("mi_ubicacion", true));
            }
        }
        mMap.setMinZoomPreference(1);
        mMap.setMaxZoomPreference(preferencias.getInt("zoom_level", 5));
        if (Double.valueOf(preferencias.getString("latitud_anterior", "0")) != 0) {
            LatLng posicionAnterior = new LatLng(Double.valueOf(
                    preferencias.getString("latitud_anterior", "0")), Double.valueOf(
                    preferencias.getString("longitud_anterior", "0")
            ));
            CameraPosition restaurarVista = new CameraPosition.Builder()
                    .target(posicionAnterior)
                    .zoom(preferencias.getFloat("zoom_anterior", 50))
                    .bearing(preferencias.getFloat("bearing", 90))
                    .tilt(preferencias.getFloat("tilt", 45))
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(restaurarVista));

        } else {

            LatLng ues = new LatLng(13.970546, -89.574738);
            mMap.addMarker(new MarkerOptions().position(ues).title("Universidad").draggable(true));
            CameraPosition moverues = new CameraPosition.Builder()
                    .target(ues)
                    .zoom(17)
                    .tilt(45)
                    .bearing(90)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(moverues));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this, "Coordenadas: \n" +
                        "Latitud: " + latLng.latitude + "\n" +
                        "Longitud:" + latLng.longitude, Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                armarDialog(latLng);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(MapsActivity.this,
                        "Coordenadas: \n" +
                                "LAtitud: " +
                                marker.getPosition().latitude + "\n" +
                                "Longitud:" +
                                marker.getPosition().longitude, Toast.LENGTH_LONG).show();
            }
        });

        toogleTools();

    }

    private void toogleTools() {
        mMap.getUiSettings().setScrollGesturesEnabled(preferencias.getBoolean("gestos_zoom", false));
        mMap.getUiSettings().setZoomControlsEnabled(preferencias.getBoolean("zoom", false));
        mMap.getUiSettings().setCompassEnabled(preferencias.getBoolean("brujula", false));
        mMap.getUiSettings().setMapToolbarEnabled(preferencias.getBoolean("barra_tareas", false));
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferencias.edit()
                .putInt("tipo_mapa", cmbTiposMapa.getSelectedItemPosition())
                .putFloat("zoom_anterior", mMap.getCameraPosition().zoom)
                .putString("longitud_anterior", (String.valueOf(mMap.getCameraPosition().target.longitude)))
                .putString("latitud_anterior", (String.valueOf(mMap.getCameraPosition().target.latitude)))
                .putFloat("tilt", mMap.getCameraPosition().tilt)
                .putFloat("bearing", mMap.getCameraPosition().bearing)
                .apply();
    }

    public void armarDialog(final LatLng latLng) {
        insideContext = MapsActivity.this;
        constructor = new AlertDialog.Builder(insideContext);
        constructor.setTitle("Agregar").setMessage("¿Agregar círculo o marcador?");
        constructor.setPositiveButton("Marcador", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                armarMarcador(latLng);
            }
        });
        constructor.setNegativeButton("Círculo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                agregarCirculo(latLng);
            }
        });

        AlertDialog dialogo = constructor.create();

        dialogo.show();
    }

    public void armarMarcador(final LatLng latLng) {
        constructor = new AlertDialog.Builder(insideContext);
        constructor.setTitle("Agregar Marcador").setMessage("Ingrese el título del marcador");
        constructor.setCancelable(false);
        //crea el input para el título
        final EditText input = new EditText(MapsActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        constructor.setView(input);

        constructor.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tituloMarcador = input.getText().toString();
                mMap.addMarker(new MarkerOptions().position(latLng).title(tituloMarcador).draggable(false));
                contenido.clear();
                contenido.put("longitud", latLng.longitude);
                contenido.put("latitud", latLng.latitude);
                contenido.put("titulo", tituloMarcador);
                contenido.put("arrastrable", false);
                db.insert("marcadores", null, contenido);
            }
        });
        constructor.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialogo = constructor.create();

        dialogo.show();
    }

    public void agregarCirculo(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(preferencias.getInt("radio_level", 50));
        circleOptions.strokeColor(Color.BLACK);
        circleOptions.fillColor(Color.BLUE);
        circleOptions.strokeWidth(1);
        mMap.addCircle(circleOptions);
        contenido.clear();
        contenido.put("longitud", latLng.longitude);
        contenido.put("latitud", latLng.latitude);
        contenido.put("radio", preferencias.getInt("radio_level", 50));
        contenido.put("color_borde", Color.BLACK);
        contenido.put("color_relleno", Color.BLUE);
        contenido.put("ancho_borde", 1);
        db.insert("circulos", null, contenido);
    }

    public void restaurarCirculos() {
        while (circulos.moveToNext()) {
            CircleOptions circleOptions = new CircleOptions();
            LatLng latLng = new LatLng(circulos.getDouble(circulos.getColumnIndex("latitud")),
                    circulos.getDouble(circulos.getColumnIndex("longitud")));
            circleOptions.center(latLng);
            circleOptions.radius(circulos.getInt(circulos.getColumnIndex("radio")));
            circleOptions.strokeColor(circulos.getInt(circulos.getColumnIndex("color_borde")));
            circleOptions.fillColor(circulos.getInt(circulos.getColumnIndex("color_relleno")));
            circleOptions.strokeWidth(circulos.getInt(circulos.getColumnIndex("ancho_borde")));
            mMap.addCircle(circleOptions);
        }
        circulos.close();
    }

    public void restaurarMarcadores() {

        while (marcadores.moveToNext()) {
            LatLng latLng = new LatLng(marcadores.getDouble(marcadores.getColumnIndex("latitud")),
                    marcadores.getDouble(marcadores.getColumnIndex("longitud")));
            mMap.addMarker(new MarkerOptions().position(latLng).
                    title(marcadores.getString(marcadores.getColumnIndex("titulo"))).
                    draggable(false));
        }
        marcadores.close();
    }
}
