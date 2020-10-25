package com.example.mapsicesi;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsicesi.Conexion.Actions;
import com.example.mapsicesi.Models.AdminHueco;
import com.example.mapsicesi.Models.AdminUsuario;
import com.example.mapsicesi.Models.Hueco;
import com.example.mapsicesi.Models.Usuario;
import com.example.mapsicesi.Observers.OnReadHuecos;
import com.example.mapsicesi.Observers.OnReadUsuarios;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener,
        OnReadHuecos,
        OnReadUsuarios {

    private GoogleMap mMap;
    private String user;
    private LocationManager manager;
    private AdminUsuario me;
    private ArrayList<Marker> points;
    private Button addBtn;
    private ConstraintLayout avisar;
    private Button agregar_hueco_Btn;
    private Button cancelarBtn;
    private TextView coordenadas;
    private TextView direccion;
    private TextView distTxt;
    private Actions conexion;
    private float latitud;
    private float longitud;
    private Button confirmarBtn;
    private AdminHueco huecoMasCerca;
    private ArrayList<AdminHueco> huecos;
    private HashMap<String, AdminUsuario> usuarios;


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
        usuarios = new HashMap<>();
        addBtn = findViewById(R.id.addBtn);
        distTxt = findViewById(R.id.distTxt);
        avisar = findViewById(R.id.avisar);
        agregar_hueco_Btn = findViewById(R.id.agregar_hueco_Btn);
        cancelarBtn = findViewById(R.id.cancelarBtn);
        coordenadas = findViewById(R.id.coordenadas);
        direccion = findViewById(R.id.direccion);
        confirmarBtn = findViewById(R.id.confirmarBtn);

        agregar_hueco_Btn.setOnClickListener(this);
        cancelarBtn.setOnClickListener(this);
        confirmarBtn.setOnClickListener(this);

        this.conexion = new Actions();
        this.conexion.setObserverHuecos(this);
        this.conexion.setObserverUsuarios(this);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);




        this.me = new AdminUsuario();
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me.getUsuarioMarcador().getPosition(), 8));

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

        Toast.makeText(this, "mapa cargado", Toast.LENGTH_SHORT).show();

      //  this.conexion.getPrimeraposicion(this.me);
        String uId = UUID.randomUUID().toString();
        this.conexion.registerUserIfNotExists(new Usuario(uId, user, 0, 0));
        conexion.leerHuecos();
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
           // addBtn.setText("Ahi no esta el D1 care verga");
        }
    }

    public void updateMyLocation(Location location){
        this.updatePosMarcador(location.getLatitude(), location.getLongitude());
        /*
        LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());

        latitud = (float) location.getLatitude();
        longitud = (float) location.getLongitude();

        if (me.getUsuarioMarcador() == null){
            Marker yo = this.mMap.addMarker(new MarkerOptions().position(myPos).title("Aqui tas"));
            me.setUsuarioMarcador(yo);
        }else {
            me.getUsuarioMarcador().setPosition(myPos);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17));
        this.me.getDataBaseUsuarios().setLatitud(latitud);
        this.me.getDataBaseUsuarios().setLongitud(longitud);
        this.conexion.updateMyLocation(this.me.getDataBaseUsuarios());
        */


       // computeDistances();
    }

    private void updatePosMarcador(double latitud, double longitud){

        this.latitud = (float) latitud;
        this.longitud = (float) longitud;
        LatLng myPos = new LatLng(latitud, longitud);

        if (me.getUsuarioMarcador() == null){
            Marker yo = this.mMap.addMarker(new MarkerOptions().position(myPos).title("Aqui tas"));
            me.setUsuarioMarcador(yo);
        }else {
            me.getUsuarioMarcador().setPosition(myPos);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17));

        if(this.me.getDataBaseUsuarios() != null){
            this.me.getDataBaseUsuarios().setLatitud(latitud);
            this.me.getDataBaseUsuarios().setLongitud(longitud);
            this.conexion.updateMyLocation(this.me.getDataBaseUsuarios());
        }

    }

    public void computedDistancesHuecos(){

        double dismin = -1;
        int index = -1;

        for(int i = 0; i < this.huecos.size(); i++){
            AdminHueco hueco = this.huecos.get(i);

            if(hueco.getHueco().isVerificado() == false){
                LatLng huecoPos = hueco.getHuecoView().getCenter();
                LatLng meLoc = me.getUsuarioMarcador().getPosition();

                double meters = SphericalUtil.computeDistanceBetween(huecoPos, meLoc);

                if(dismin == -1 || meters < dismin){
                    dismin = meters;
                    index = i;
                }
            }
        }

        if(index != -1){
            this.distTxt.setText("Hueco a " + Math.round(dismin) + " M");
            if(dismin < 40){
                this.huecoMasCerca = this.huecos.get(index);
                this.confirmarBtn.setVisibility(View.VISIBLE);
            }else{
                this.huecoMasCerca = null;
                this.confirmarBtn.setVisibility(View.INVISIBLE);

            }
        }else{
            this.huecoMasCerca = null;
            this.confirmarBtn.setVisibility(View.INVISIBLE);
            this.distTxt.setText("No hay huecos");
        }
    }

    private void computeDistances() {
        for (int i=0 ; i<points.size() ; i++){
            Marker marker = points.get(i);
            LatLng markerLoc = marker.getPosition();
            LatLng meLoc = me.getUsuarioMarcador().getPosition();

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
                double meters = SphericalUtil.computeDistanceBetween(punto, me.getUsuarioMarcador().getPosition());

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
                this.mostrarPopUp();
                break;

            case R.id.cancelarBtn:
                this.ocultarPopUp();
                break;

            case R.id.agregar_hueco_Btn:
                // verificar que la posicion haya cambiado
                String uId = UUID.randomUUID().toString();
                Hueco hueco =  new Hueco(user, uId, latitud, longitud, false);
                conexion.createHueco(hueco);
                Circle marcadorHueco = mMap.addCircle(new CircleOptions().fillColor(Color.RED).center(new LatLng(hueco.getLatitud(), hueco.getLongitud())).radius(10));
                AdminHueco huecoObj = new AdminHueco(hueco, marcadorHueco);
                this.huecos.add(huecoObj);
                this.ocultarPopUp();
                break;

            case R.id.confirmarBtn:
                huecoMasCerca.getHueco().setVerificado(true);
                conexion.huecoValidar(huecoMasCerca);
                this.UpdateColorHueco(huecoMasCerca);
                break;
        }

    }

    @Override
    public void getAllData(ArrayList<Hueco> huecos) {

        runOnUiThread(()->{
            Toast.makeText(this, "obteniendo huecos", Toast.LENGTH_SHORT).show();
            for (int i = 0 ; i < huecos.size() ; i++){
                int index = -1;
                Hueco hueco = huecos.get(i);
                for (int j = 0 ; j < this.huecos.size() ; j++){
                    AdminHueco ref = this.huecos.get(j);
                    if (ref.getHueco().getuId() == hueco.getuId()){
                        index = j;
                        j = this.huecos.size();
                    }

                }
                if (index != -1){
                    AdminHueco ref = this.huecos.get(index);
                    ref.getHueco().setVerificado(hueco.isVerificado());
                    this.UpdateColorHueco(ref);
                }else{

                    Circle marcadorHueco = mMap.addCircle(new CircleOptions().fillColor(Color.RED).center(new LatLng(hueco.getLatitud(), hueco.getLongitud())).radius(10));
                    AdminHueco huecoObj = new AdminHueco(hueco, marcadorHueco);
                    this.UpdateColorHueco(huecoObj);
                    this.huecos.add(huecoObj);

                }
            }
            this.computedDistancesHuecos();

        });



    }

    public void UpdateColorHueco(AdminHueco ref){
        if (ref.getHueco().isVerificado()) {
            ref.getHuecoView().setFillColor(Color.GREEN);
        }else {
            ref.getHuecoView().setFillColor(Color.RED);
        }
    }


    public String getDireccion(double latitud, double longitud){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String direccion = "No hay una dirección especifica";
        try {
            List<Address> direcciones = geocoder.getFromLocation(latitud, longitud, 1);
            for (int i = 0; i < direcciones.size(); i++){
                if(direccion == "No hay una dirección especifica"){
                    Address dir = direcciones.get(i);
                    direccion = dir.getAddressLine(0).toString();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  direccion;
    }


    public void mostrarPopUp(){
        this.avisar.setVisibility(View.VISIBLE);
        String cordenadasseteadas = this.latitud+" , " + this.longitud;
        String direccionseteada = this.getDireccion(this.latitud, this.longitud);
        this.direccion.setText(direccionseteada);
        this.coordenadas.setText(cordenadasseteadas);
    }

    public void ocultarPopUp(){
        this.avisar.setVisibility(View.INVISIBLE);

    }

    //obtenemos los usuarios
    @Override
    public void getAllUsuarios(ArrayList<Usuario> usuarios) {

        runOnUiThread(()->{

            for (int i = 0 ; i<usuarios.size() ; i++){
                Usuario u = usuarios.get(i);
                if (this.usuarios.get(u.getuId()) == null){
                    Marker marcador = this.mMap.addMarker(new MarkerOptions().position(new LatLng(u.getLatitud(), u.getLongitud())));
                    AdminUsuario nuevoUsuario = new AdminUsuario(u, marcador);
                    this.usuarios.put(u.getuId(), nuevoUsuario);
                }else {
                    AdminUsuario obtenerUser = this.usuarios.get(u.getuId());
                    if (obtenerUser.getDataBaseUsuarios().getLongitud() != u.getLongitud() && obtenerUser.getDataBaseUsuarios().getLatitud() != u.getLatitud()){
                        obtenerUser.getUsuarioMarcador().setPosition(new LatLng(u.getLatitud(), u.getLongitud()));
                    }
                }

            }

        });

    }

    @Override
    public void getMyUbicacion(Usuario me) {
        runOnUiThread(()-> {
            this.me.setDataBaseUsuarios(me);
            this.updatePosMarcador(me.getLatitud(), me.getLongitud());
        });
    }
}