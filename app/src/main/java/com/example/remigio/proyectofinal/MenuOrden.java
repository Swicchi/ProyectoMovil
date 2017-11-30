package com.example.remigio.proyectofinal;

import android.graphics.Bitmap;

/**
 * Created by Remigio on 06-11-2017.
 */

public class MenuOrden {
    public String nombre;
    public int precio;
    public int cantidad;
    public MenuOrden(){
        super();
    }

    public MenuOrden( int precio, int cantidad, String nombre) {
        super();
        this.nombre=nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

   }
