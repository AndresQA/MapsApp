package com.example.mapsicesi.Models;

import com.google.android.gms.maps.model.Marker;

public class AdminUsuario {

    private Usuario dataBaseUsuarios;
    private Marker usuarioMarcador;

    public AdminUsuario(){

    }

    public AdminUsuario(Usuario dataBaseUsuarios, Marker usuarioMarcador) {
        this.dataBaseUsuarios = dataBaseUsuarios;
        this.usuarioMarcador = usuarioMarcador;
    }

    public Usuario getDataBaseUsuarios() {
        return dataBaseUsuarios;
    }

    public void setDataBaseUsuarios(Usuario dataBaseUsuarios) {
        this.dataBaseUsuarios = dataBaseUsuarios;
    }

    public Marker getUsuarioMarcador() {
        return usuarioMarcador;
    }

    public void setUsuarioMarcador(Marker usuarioMarcador) {
        this.usuarioMarcador = usuarioMarcador;
    }
}
