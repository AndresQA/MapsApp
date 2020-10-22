package com.example.mapsicesi.Models;

public class Usuario {

    private String uId;
    private String nombre;
    public Usuario(){

}
   public Usuario(String uId , String nombre){

       this.uId = uId;
       this.nombre = nombre;


    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
