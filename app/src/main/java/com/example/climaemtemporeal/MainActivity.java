package com.example.climaemtemporeal;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLOutput;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText etCidade;
    TextView tvResultado;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String id = "daef2b6ebc1f80a11a194a397ce30583";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCidade = findViewById(R.id.etCidade);
        tvResultado = findViewById(R.id.tvResultado);
    }

    public void buscarDetalhesClima(View view) {
        String tempUrl = "";
        String cidade = etCidade.getText().toString().trim();
        if(cidade.equals("")){
            tvResultado.setText("O campo cidade está vazio...");
        }else{
                tempUrl = url + "?q=" + cidade + "&appid=" + id;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15;
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");
                        String cityName = jsonResponse.getString("name");
                        tvResultado.setTextColor(Color.rgb(255,255,255));
                        output += "Clima atual de " + cityName + " (" + countryName + ")"
                                + "\n Temperatura: " + df.format(temp) + " °C"
                                + "\n Sensação: " + df.format(feelsLike) + " °C"
                                + "\n Umidade: " + humidity + "%"
                                + "\n Descrição: " + description
                                + "\n Vento: " + wind + "m/s (meters per second)"
                                + "\n Nebulosidade: " + clouds + "%"
                                + "\n Pressão do Ar: " + pressure + " hPa";
                        tvResultado.setText(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }