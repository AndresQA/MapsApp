package com.example.mapsicesi.Observers;

import com.example.mapsicesi.Models.Hueco;
import com.example.mapsicesi.Models.Usuario;

import java.util.ArrayList;

public interface OnReadUsuarios {
    public void getAllUsuarios(ArrayList<Usuario> usuarios);
    public void getMyUbicacion(Usuario me);
}


