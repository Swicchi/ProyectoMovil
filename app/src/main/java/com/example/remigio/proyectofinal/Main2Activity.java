package com.example.remigio.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    //TextView textView;
    ListView listView;
    String [] cadena = {"Elemento 1","Elemento 2","Elemento 3","Elemento 4" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //textView = (TextView) findViewById(R.id.texto);
        listView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            for (int i=0;i<cadena.length;i++) {
               // cadena[i]=bundle.get("codigo").toString()+" "+(i+1);
            }
            //textView.setText(bundle.get("codigo").toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,cadena);
        listView.setAdapter(adapter);
    }
}
