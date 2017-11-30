package com.example.remigio.proyectofinal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main2Activity extends AppCompatActivity {
    //TextView textView;
    private ListView listView;
    TextView monto;
    View viewmenu;
    int posicionmenu;
    static Orden orden;
    String[] cadena = {"Elemento 1", "Elemento 2", "Elemento 3", "Elemento 4"};
    private View mProgressView;
    Context context;
    Bitmap image = null;
    Menu[] menus = new Menu[]{
            new Menu(1,image ,1,"df","f","d")
    };
    // IP de mi Url
    String IP = "http://parra.chillan.ubiobio.cl:8070/remferna/ProyectoFinal";
    // Rutas de los Web Services
    String GET = IP + "/obtener_platos.php";
    URL url;
    ObtenerWebService hiloconexion;
    int numeroMesa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //textView = (TextView) findViewById(R.id.texto);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        numeroMesa = bundle.getInt("numeroMesa");
        monto = (TextView) findViewById(R.id.monto);
        monto.setText("Mesa: "+numeroMesa+", Monto total= $0");
        orden = new Orden(numeroMesa);

        Log.d("M","Progress");
        context = this;
        listView = (ListView) findViewById(R.id.listView);
        mProgressView = findViewById(R.id.login_progress);
        showProgress(true);
        hiloconexion = new ObtenerWebService();
        hiloconexion.execute(GET);
        Log.d("cant2",menus.length+"");

        if (bundle != null) {
            for (int i = 0; i < cadena.length; i++) {
                // cadena[i]=bundle.get("codigo").toString()+" "+(i+1);
            }
            //textView.setText(bundle.get("codigo").toString());
        }
        //Toast.makeText(getApplicationContext(), bundle.get("codigo").toString(), Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, cadena);
        //listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewmenu = view;
                posicionmenu=i;
                AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                alerta.setMessage("¿Esta segura/o de agregar producto?");
                alerta.setTitle("Sistema de Restaurante");
                alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Menu menu = menus[posicionmenu-1];
                        orden.addToCart(menu);
                        monto.setText("Mesa: "+(int)orden.getMesa()+", Monto Total= $"+(int)orden.getValue());
                        Toast.makeText(getApplicationContext(),"Se agrego 1: "+menu.nombre, Toast.LENGTH_SHORT).show();
                        TextView v = (TextView) viewmenu.findViewById(R.id.nombre);
                        //Toast.makeText(getApplicationContext(), v.getText(), Toast.LENGTH_SHORT).show();
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
    public void reset(View view){
        AlertDialog.Builder alerta = new AlertDialog.Builder(context);
        alerta.setMessage("¿Esta segura/o de realizar la acción?");
        alerta.setTitle("Sistema de Restaurante");
        alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                monto.setText("Mesa: "+numeroMesa+", Monto total= $0");
                orden.empty();
                Toast.makeText(getApplicationContext(),"Se vacio la orden", Toast.LENGTH_SHORT).show();
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
    public void verorden(View view){
        if(orden.getSize()>0) {
            Intent intent = new Intent(this, ViewCart.class);
            orden = orden;
            startActivity(intent);
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

    public class ObtenerWebService extends AsyncTask<String, Void, Menu[]> {
        @Override
        protected void onPostExecute(Menu[] s) {
            //super.onPostExecute(s);
            Log.d("cant",s.length+"");
            menus = s;
            Log.d("cant3",menus.length+"");
            MenuAdapter adapter = new MenuAdapter(context, R.layout.menu_row, menus);
            View header = (View) getLayoutInflater().inflate(R.layout.menu_header, null);
            listView.addHeaderView(header);
            listView.setAdapter(adapter);
            showProgress(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Menu[] s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Menu[] doInBackground(String... strings) {
            String cadena = strings[0];
            Menu[] menusl =new Menu[0];;
            Log.d("cadena",cadena);
            try{
                url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        "(Linux; Android 1.5; es-ES) Ejemplo HTTP");
                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();
                Log.d("Result",respuesta+" ");
                if(respuesta == HttpURLConnection.HTTP_OK){
                    InputStream in= new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line =  reader.readLine())!=null){
                        result.append(line);
                    }
                    JSONObject respuestaJSON = new JSONObject(result.toString());
                    boolean resultJSON = respuestaJSON.getBoolean("respuesta");
                    Log.d("Result2",resultJSON+" ");
                    if(resultJSON){
                        JSONArray platosJSON= respuestaJSON.getJSONArray("datos");
                        menusl = new Menu[platosJSON.length()];
                        URL imageUrl = null;
                        HttpURLConnection conn = null;
                        for(int i = 0;i< platosJSON.length();i++) {
                            imageUrl = new URL(platosJSON.getJSONObject(i).getString("srcIMG"));
                            conn = (HttpURLConnection) imageUrl.openConnection();
                            conn.connect();
                            Bitmap imagen = BitmapFactory.decodeStream(conn.getInputStream());
                            menusl[i] =new Menu(platosJSON.getJSONObject(i).getInt("id_plato"),imagen, platosJSON.getJSONObject(i).getInt("precio"), "Tipo: "+platosJSON.getJSONObject(i).getString("nombre_tipo"), "Detalle: "+platosJSON.getJSONObject(i).getString("ingredientes"), platosJSON.getJSONObject(i).getString("nombre"));
                        }
                    }
                }

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return menusl;
        }
    }
}


