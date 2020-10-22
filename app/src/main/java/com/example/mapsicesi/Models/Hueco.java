package com.example.mapsicesi.Models;

public class Hueco {

    private String userView;
    private String uId;
    private float latitud;
    private float longitud;
    private boolean verificado;

    public Hueco(){

    }

    public Hueco(String userView, String uId, float latitud, float longitud, boolean verificado){
    this.userView = userView;
    this.uId = uId;
    this.latitud = latitud;
    this.longitud = longitud;
    this.verificado = verificado;
    }

    public String getUserView() {
        return userView;
    }

    public void setUserView(String userView) {
        this.userView = userView;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

}
