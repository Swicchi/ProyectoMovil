package com.example.remigio.proyectofinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button scan_btn;
    String IP = "http://parra.chillan.ubiobio.cl:8070/remferna/ProyectoFinal";
    // Rutas de los Web Services
    String GET = IP + "/obtener_mesa.php";
    URL url;
    Intent intent;
    Context context;
    String resulta;
    ObtenerWebServiceMesa hiloconexion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        context = this;
        final Activity activity= this;
        scan_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                IntentIntegrator integrator= new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Coloque su dispositivo de forma horizontal para escanear el código ");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(context, "Se ha cancelado el escaner", Toast.LENGTH_LONG).show();
            }
            else {
                intent =  new Intent(this, Main2Activity.class );
                Toast.makeText(context, "Se ingresado el codigo",Toast.LENGTH_SHORT).show();
                resulta = result.getContents();
                GET = GET+"?codigo="+resulta;
                if(isNetDisponible()&&isOnlineNet()) {
                    hiloconexion = new ObtenerWebServiceMesa();
                    hiloconexion.execute(GET);
                }else{
                    Toast.makeText(context, "Se ha detectado problemas en la conexión",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public class ObtenerWebServiceMesa extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPostExecute(Boolean s) {
            //super.onPostExecute(s);
            if (s){
                startActivity(intent);
            }else{
                Toast.makeText(context, "No se encontro Mesa para el código, Reinicie la aplicación", Toast.LENGTH_LONG).show();
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
                    resultJSON = respuestaJSON.getBoolean("respuesta");
                    Log.d("Result2",resultJSON+" ");
                    if(resultJSON){
                        JSONObject mesaJSON = respuestaJSON.getJSONObject("mesa");
                        intent.putExtra("id_mesa",mesaJSON.getInt("id_mesa"));
                        intent.putExtra("numeroMesa",mesaJSON.getInt("numeroMesa"));
                        intent.putExtra("cantidad_asientos",mesaJSON.getInt("cantidad_asientos"));
                        intent.putExtra("id_garzon",mesaJSON.getInt("id_garzon"));
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
