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

public class MenuAdapter extends ArrayAdapter<Menu> {
    Context context;
    int layoutResourceId;
    Menu [] data = null;

    public MenuAdapter( Context context1, int layoutResourceId, Menu[] data) {
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
            menuHolder.imageView= (ImageView) row.findViewById(R.id.image);
            menuHolder.nombre=(TextView) row.findViewById(R.id.nombre);
            menuHolder.precio=(TextView) row.findViewById(R.id.precio);
            menuHolder.tipo=(TextView) row.findViewById(R.id.tipo);
            menuHolder.listado=(TextView) row.findViewById(R.id.listado);
            row.setTag(menuHolder);
        }else {
            menuHolder = (MenuHolder)row.getTag();
        }
        Menu menu= data[position];
        menuHolder.nombre.setText(menu.nombre);
        menuHolder.precio.setText("Precio: $"+menu.precio);
        menuHolder.tipo.setText(menu.tipo);
        menuHolder.listado.setText(menu.listado);
        menuHolder.imageView.setImageBitmap(menu.icon);
        return row;
    }
    static class MenuHolder{
        ImageView imageView;
        TextView nombre;
        TextView precio;
        TextView tipo;
        TextView listado;
    }
}
