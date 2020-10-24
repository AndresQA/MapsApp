package com.example.mapsicesi.Models;

import com.google.android.gms.maps.model.Circle;

public class AdminHueco {

    private Hueco hueco;
    private Circle huecoView;

    public AdminHueco(Hueco hueco, Circle huecoView) {
        this.hueco = hueco;
        this.huecoView = huecoView;
    }

    public Hueco getHueco() {
        return hueco;
    }

    public void setHueco(Hueco hueco) {
        this.hueco = hueco;
    }

    public Circle getHuecoView() {
        return huecoView;
    }

    public void setHuecoView(Circle huecoView) {
        this.huecoView = huecoView;
    }
}
