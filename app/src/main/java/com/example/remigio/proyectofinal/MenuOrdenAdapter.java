package com.example.remigio.proyectofinal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Remigio on 06-11-2017.
 */

public class MenuOrdenAdapter extends ArrayAdapter<MenuOrden> {
    Context context;
    int layoutResourceId;
    MenuOrden [] data = null;

    public MenuOrdenAdapter(Context context1, int layoutResourceId, MenuOrden[] data) {
        super(context1,layoutResourceId,data);
        this.context = context1;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View row=convertView;
        MenuHolder menuHolder=null;
        if(row==null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row=inflater.inflate(layoutResourceId,parent,false);
            menuHolder = new MenuHolder();
            menuHolder.nombre=(TextView) row.findViewById(R.id.nombre);
            menuHolder.precio=(TextView) row.findViewById(R.id.precio);
            menuHolder.cantidad=(TextView) row.findViewById(R.id.cantidad);
            row.setTag(menuHolder);
        }else {
            menuHolder = (MenuHolder)row.getTag();
        }
        MenuOrden menu= data[position];
        menuHolder.nombre.setText(menu.nombre);
        menuHolder.precio.setText("$"+menu.precio*menu.cantidad);
        menuHolder.cantidad.setText(menu.cantidad+" ");
        return row;
    }
    static class MenuHolder{
        TextView nombre;
        TextView precio;
        TextView cantidad;
    }
}
