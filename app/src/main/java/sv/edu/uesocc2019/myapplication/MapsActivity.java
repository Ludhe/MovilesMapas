package sv.edu.uesocc2019.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
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

    final String[] tiposmapa =
            new String[]{"Normal", "Satelite", "Hibrido", "Terreno"};
    private Spinner cmbTiposMapa;
    private Button btnConfig;
    SharedPreferences preferencias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
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
        toogleTools();
       // mMap.setMinZoomPreference(10);
       // mMap.setMinZoomPreference(18);
        if (Double.valueOf(preferencias.getString("latitud_anterior", "0")) != 0) {
            LatLng posicionAnterior = new LatLng(Double.valueOf(
                    preferencias.getString("latitud_anterior", "0")), Double.valueOf(
                    preferencias.getString("longitud_anterior", "0")
            ));
            CameraPosition restaurarVista = new CameraPosition.Builder()
                    .target(posicionAnterior)
                    .zoom(preferencias.getFloat("zoom_anterior", 50))
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(restaurarVista));

        }else{
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
                        "LAtitud: " + latLng.latitude + "\n" +
                        "Longitud:" + latLng.longitude, Toast.LENGTH_LONG).show();
                ;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this, "Coordenadas: \n" +
                        "LAtitud: " + latLng.latitude + "\n" +
                        "Longitud:" + latLng.longitude, Toast.LENGTH_LONG).show();
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.radius(200);
                circleOptions.strokeColor(Color.BLACK);
                circleOptions.fillColor(Color.BLUE);
                circleOptions.strokeWidth(1);
                mMap.addCircle(circleOptions);

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

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

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
                .apply();
    }

}
