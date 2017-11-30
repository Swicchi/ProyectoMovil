package com.example.remigio.proyectofinal;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Remigio on 06-11-2017.
 */

public class Menu {
    public int id;
    public Bitmap icon;
    public String nombre;
    public int precio;
    public String tipo;
    public String listado;
    public Menu(){
        super();
    }

    public Menu(int id, Bitmap icon, int precio, String tipo, String listado,String nombre) {
        super();
        this.id=id;
        this.nombre=nombre;
        this.icon = icon;
        this.precio = precio;
        this.tipo = tipo;
        this.listado = listado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getListado() {
        return listado;
    }

    public void setListado(String listado) {
        this.listado = listado;
    }
}
