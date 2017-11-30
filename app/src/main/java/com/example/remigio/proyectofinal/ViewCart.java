package com.example.remigio.proyectofinal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

public class ViewCart extends AppCompatActivity
{
    String IP = "http://parra.chillan.ubiobio.cl:8070/remferna/ProyectoFinal";
    // Rutas de los Web Services
    String INSERT_ORDEN = IP + "/registrar_orden.php";
    String INSERT_PRODUCTO = IP + "/registrar_producto.php";
    String INSERT_BEBIDA= IP + "/registrar_bebida.php";
    URL url;
    Intent intent;
    Context context;
    String mensaje = "";
    ObtenerWebServiceOrden hiloconexionOrden;
    ObtenerWebServiceProducto hiloconexionProducto;
    Orden cart = Main2Activity.orden;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_cart);
        LinearLayout cartLayout = (LinearLayout) findViewById(R.id.cart);
        TextView titulo = (TextView) findViewById(R.id.title);
        titulo.setText("Orden de Compra, Mesa "+(int)cart.getMesa()+", Monto Total: $"+(int)cart.getValue());
        context = this;
        Set<Menu> products = cart.getProducts();
        MenuOrden[] menuOrdens =new MenuOrden [cart.getSize()];
        Iterator iterator = products.iterator();
        int i =0;
        ListView listView = (ListView) findViewById(R.id.listView);
        while(iterator.hasNext())
        {
            Menu menu = (Menu) iterator.next();
            menuOrdens[i] = new MenuOrden(menu.getPrecio(),cart.getQuantity(menu),menu.getNombre());

            i++;
        }
        MenuOrdenAdapter adapter = new MenuOrdenAdapter(this, R.layout.menuorden_row, menuOrdens);
        View header = (View) getLayoutInflater().inflate(R.layout.menuorden_header, null);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);
    }
    public void solicitar(View view) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(context);
        alerta.setMessage("¿Esta segura/o de realizar la acción?");
        alerta.setTitle("Sistema de Restaurante");
        alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hiloconexionOrden = new ObtenerWebServiceOrden();
                hiloconexionOrden.execute(INSERT_ORDEN, Double.toString(cart.getMesa()));
            }
        });
        alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Se cancelo la acción", Toast.LENGTH_SHORT).show();
            }
        });
        alerta.show();
    }
    public class ObtenerWebServiceOrden extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            Set<Menu> products = cart.getProducts();
            if(s.equals("")){
                Toast.makeText(context, mensaje,Toast.LENGTH_SHORT).show();
            }else {
                Iterator iterator = products.iterator();
                while (iterator.hasNext()) {
                    Menu menu = (Menu) iterator.next();
                    hiloconexionProducto = new ObtenerWebServiceProducto();

                    String tipo = "Tipo: Bebestible";
                    String tipo2 = menu.getTipo();
                    Log.d("tipo",tipo+" "+tipo2);
                    if(tipo.toUpperCase().equals(tipo2.toUpperCase())){
                        hiloconexionProducto.execute(INSERT_BEBIDA, Integer.toString(menu.getId()), s, Integer.toString(cart.getQuantity(menu)), menu.getTipo());
                    }else{
                        hiloconexionProducto.execute(INSERT_PRODUCTO, Integer.toString(menu.getId()), s, Integer.toString(cart.getQuantity(menu)), menu.getTipo());
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected String doInBackground(String... strings) {
            String cadena = strings[0];
            Log.d("cadena",cadena);
            boolean resultJSON = false;
            String id= "";
            try{
                HttpURLConnection urlConn;
                DataOutputStream printout;
                DataInputStream input;
                url = new URL(cadena);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setRequestProperty("Accept", "application/json");
                urlConn.connect();
                //Creo el Objeto JSON
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mesa", strings[1]);
                Log.d("mesa",strings[1]+" paso");
                // Envio los parámetros post.
                OutputStream os = urlConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();

                int respuesta = urlConn.getResponseCode();
                 StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        result.append(line);
                        //response+=line;
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    resultJSON = respuestaJSON.getBoolean("respuesta");
                    Log.d("Result2",resultJSON+" ");
                    if(resultJSON){
                        id = respuestaJSON.getString("orden");
                    }else {
                        mensaje = respuestaJSON.getString("mensaje");
                    }
                    Log.d("IDorden",id+" paso");
                }

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return id;
        }
    }

    public class ObtenerWebServiceProducto extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPostExecute(Boolean s) {
            //super.onPostExecute(s);
            if(s){
                Toast.makeText(context, "Se Registro Producto",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, mensaje,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Boolean s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            String cadena = strings[0];
            Log.d("cadena",cadena);
            boolean resultJSON = false;
            String id= "";
            try{
                HttpURLConnection urlConn;
                DataOutputStream printout;
                DataInputStream input;
                url = new URL(cadena);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("Content-Type", "application/json");
                urlConn.setRequestProperty("Accept", "application/json");
                urlConn.connect();
                //Creo el Objeto JSON
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("producto", strings[1]);
                jsonParam.put("orden", strings[2]);
                jsonParam.put("cantidad", strings[3]);
                jsonParam.put("tipo", strings[4]);
                Log.d("tipo",strings[4]+" ");
                // Envio los parámetros post.
                OutputStream os = urlConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();

                int respuesta = urlConn.getResponseCode();


                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK) {

                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        result.append(line);
                        //response+=line;
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    resultJSON = respuestaJSON.getBoolean("respuesta");
                    Log.d("Result2",resultJSON+" ");
                    if(resultJSON){

                    }else {
                        mensaje = respuestaJSON.getString("mensaje");
                    }
                }

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return resultJSON;
        }
    }
}


