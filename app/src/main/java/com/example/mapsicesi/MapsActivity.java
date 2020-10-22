package com.example.mapsicesi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsicesi.Conexion.Actions;
import com.example.mapsicesi.Models.Hueco;
import com.example.mapsicesi.Models.Usuario;
import com.example.mapsicesi.Observers.OnReadHuecos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener,
        OnReadHuecos {

    private GoogleMap mMap;
    private String user;
    private LocationManager manager;
    private Marker me;
    private ArrayList<Marker> points;
    private Button addBtn;
    private TextView distTxt;
    private Actions conexion;
    private float latitud;
    private float longitud;
    private ArrayList<Circle> huecos;



    //lugares

    private Polygon d1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        user = getIntent().getExtras().getString("user");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        points = new ArrayList<>();
        huecos = new ArrayList<>();
        addBtn = findViewById(R.id.addBtn);
        distTxt = findViewById(R.id.distTxt);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String uId = UUID.randomUUID().toString();
        conexion.registerUserIfNotExists(new Usuario(uId, user));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);

        setInitialPos();
        addBtn.setOnClickListener(
                (v)->{
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me.getPosition(), 8));

                }
        );

        addBtn.setOnClickListener(this);
        d1 = mMap.addPolygon(
                new PolygonOptions()
                        .add(new LatLng(3.260096638907622, -76.54445674270391))
                        .add(new LatLng(3.260634220825442, -76.54392063617706))
                        .add(new LatLng(3.260096638907622, -76.54333926737309))
                        .add(new LatLng(3.2595299348687283, -76.54393807053566))
                        .add(new LatLng(3.260096638907622, -76.54445674270391))
                        .fillColor(Color.argb(10,255,0,0))
                        .strokeColor(Color.BLUE)
        );



        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);

        conexion = new Actions();
        conexion.setObserverHuecos(this);
            conexion.verHuecos();
    }

    public void setInitialPos(){
        @SuppressLint("MissingPermission") Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){
           updateMyLocation(location);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateMyLocation(location);
        boolean iamAtD1 = PolyUtil.containsLocation(new LatLng(location.getLatitude(), location.getLongitude()), d1.getPoints(), false);
        if (iamAtD1){
            addBtn.setText("Estas parao en el D1");
        }else{
            addBtn.setText("Ahi no esta el D1 care verga");
        }
    }

    public void updateMyLocation(Location location){
        LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());

        latitud = (float) location.getLatitude();
        longitud = (float) location.getLongitude();

        if (me == null){
           me = mMap.addMarker(new MarkerOptions().position(myPos).title("Aqui tas"));
        }else {
            me.setPosition(myPos);
        }
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17));

        computeDistances();
    }

    private void computeDistances() {
        for (int i=0 ; i<points.size() ; i++){
            Marker marker = points.get(i);
            LatLng markerLoc = marker.getPosition();
            LatLng meLoc = me.getPosition();

            double meters = SphericalUtil.computeDistanceBetween(markerLoc, meLoc);
            Log.e(">>>>", "Metros: a marcador " + i + ": " + meters + "m");
            if (meters < 50){
                addBtn.setText("usted esta pisando un marcador");
            }
        }

        if (d1 != null) {
            double distanceToD1 = 1000000000;
            for (int i = 0; i < d1.getPoints().size(); i++) {
                LatLng punto = d1.getPoints().get(i);
                double meters = SphericalUtil.computeDistanceBetween(punto, me.getPosition());

                distanceToD1 = Math.min(meters, distanceToD1);

            }

            distTxt.setText("Distancia al D1 " + distanceToD1);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
       Marker p = mMap.addMarker(new MarkerOptions().position(latLng).title("Marcador").snippet("Este es un marcador pa probar"));
       points.add(p);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, marker.getPosition().latitude + ", " + marker.getPosition().longitude, Toast.LENGTH_LONG).show();
        Log.e(">>>", marker.getPosition().latitude + ", " + marker.getPosition().longitude);
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                //String userView, String uId, float latitud, float longitud, boolean verificado)
                // verificar que la posicion haya cambiado
                String uId = UUID.randomUUID().toString();
               Hueco hueco =  new Hueco(user, uId, latitud, longitud, false);
               conexion.createHueco(hueco);
                break;
        }

    }

    @Override
    public void getAllData(ArrayList<Hueco> huecos) {

        runOnUiThread(()->{
            for (int i = 0 ; i < huecos.size() ; i++){
                Hueco hueco = huecos.get(i);
                mMap.addCircle(new CircleOptions().fillColor(Color.RED).center(new LatLng(hueco.getLatitud(), hueco.getLongitud())).radius(10));
            }
        });

    }
}