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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    View viewmenu;
    int posicionmenu;
    int i;
    int cont = 1;
    URL url;
    View header;
    Intent intent;
    Context context;
    String mensaje = "";
    private View mProgressView;
    private ListView listView;
    TextView titulo2;
    ObtenerWebServiceOrden hiloconexionOrden;
    ObtenerWebServiceProducto hiloconexionProducto;
    Orden cart = Main2Activity.orden;
    TextView monto = Main2Activity.monto;
    Menu[] menus ;
    protected void onCreate(Bundle savedInstanceState)
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 7200000);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_cart);
        TextView titulo = (TextView) findViewById(R.id.title);
        titulo.setText("Orden de Compra, Mesa "+(int)cart.getMesa());
        titulo2 = (TextView) findViewById(R.id.title2);
        titulo2.setText("Monto Total: $"+(int)cart.getValue());
        context = this;
        listView = (ListView) findViewById(R.id.listView);
        listar();

    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }
    private void listar(){
        menus = new Menu[cart.getSize()];
        Set<Menu> products = cart.getProducts();
        Log.d("sizecart",cart.getSize()+"");
        MenuOrden[] menuOrdens =new MenuOrden [cart.getSize()];
        Log.d("size",menuOrdens.length+"");
        Iterator iterator = products.iterator();
        i =0;
        while(iterator.hasNext())
        {
            Menu menu = (Menu) iterator.next();
            menus[i]= menu;
            menuOrdens[i] = new MenuOrden(menu.getPrecio(),cart.getQuantity(menu),menu.getNombre());
            i++;
        }
        MenuOrdenAdapter adapter = new MenuOrdenAdapter(this, R.layout.menuorden_row, menuOrdens);
        header = (View) getLayoutInflater().inflate(R.layout.menuorden_header, null);
        listView.addHeaderView(header);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewmenu = view;
                posicionmenu=i;
                AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                alerta.setMessage("¿Esta segura/o de quitar producto?");
                alerta.setTitle("Sistema de Restaurante");
                alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Menu menuOrden = menus[posicionmenu-1];
                        cart.removeToCart(menuOrden);
                        titulo2.setText("Monto Total: $"+(int)cart.getValue());
                        monto.setText("Mesa: "+(int)cart.getMesa()+", Monto total= $"+(int)cart.getValue());
                        listView.removeHeaderView(header);
                        Toast.makeText(getApplicationContext(),"Se quito 1: "+menuOrden.nombre, Toast.LENGTH_SHORT).show();
                        listar();
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
        });
    }
    public void solicitar(View view) {
        if(cart.getSize()>0) {
            if(isNetDisponible()&&isOnlineNet()) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(context);
        alerta.setMessage("¿Esta segura/o de realizar la acción?");
        alerta.setTitle("Sistema de Restaurante");
        alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listView = (ListView) findViewById(R.id.listView);
                mProgressView = findViewById(R.id.login_progress);
                showProgress(true);
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
            }else{
                Toast.makeText(context, "Se ha detectado problemas en la conexión",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Orden Vacia", Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            listView.setVisibility(show ? View.GONE : View.VISIBLE);
            listView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            listView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                showProgress(false);
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
                if(cont==cart.getSize()){
                    Toast.makeText(context, "Se Registro Orden",Toast.LENGTH_SHORT).show();
                    cont=0;
                    /*
                    cart.empty();
                    monto.setText("Mesa: "+(int)cart.getMesa()+", Monto total= $"+(int)cart.getValue());
                    listView.removeHeaderView(header);
                    titulo2.setText("Monto Total: $"+(int)cart.getValue());
                    listar();*/
                }
                cont++;
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


