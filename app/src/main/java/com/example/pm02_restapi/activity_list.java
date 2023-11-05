package com.example.pm02_restapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class activity_list extends AppCompatActivity {

    private ListView listview;
    private RequestQueue requestQueue;

    private ArrayAdapter<String> postAdapter;

    private String url = "https://jsonplaceholder.typicode.com/posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        /*Amarrando las variables con la interfaz de usuario*/
        listview = (ListView) findViewById(R.id.listPost);

        /*Inicializando el arreglo*/
        postAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listview.setAdapter(postAdapter);


        /*Lógica*/
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                /*Hacer un for para recorrer los elementos*/
                for (int i = 0; i < response.length(); i++) {

                    /*Try, Catch para poder */
                    try{
                        //Tomar el valor y colocarlo en el jsonObject
                        JSONObject jsonObject = response.getJSONObject(i);
                        String mensaje = jsonObject.getString("title");
                        postAdapter.add(mensaje);
                    }
                    catch(Exception ex){
                        ex.printStackTrace(); //Mostrar el error.
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonArrayRequest);
    }
}