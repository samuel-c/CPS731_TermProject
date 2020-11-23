package com.example.cps731_termproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity {

    class Weather extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... address)
        {
            try{
                URL url =  new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int data = isr.read();
                String content = "";
                char ch;
                while(data!=-1)
                {
                    ch = (char)data;
                    content += ch;
                    data = isr.read();
                }
                //Log.i("qwe :"  ,content);
                return content;
            }
            catch(MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Weather weather = new Weather();
        try {
            String content;
            String f = "imperial";
            String c = "metric";
            String city = "toronto,ca";
            content = weather.execute("http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&APPID=b6c15dcbe8b4a2a7df28700233152283").get();
            Log.i("asd :"  ,content);
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemp = jsonObject.getString("main");
            String wind = jsonObject.getString("wind");
            String sys = jsonObject.getString("sys");
            String name = jsonObject.getString("name");
            String id = jsonObject.getString("id");

            Log.i("weatherData :"  ,weatherData);
            JSONArray array = new JSONArray(weatherData);

            String main = "";
            String description = "";
            String icon = "";
            String temp = "";
            String tempMin = "";
            String tempMax = "";
            String pressure = "";
            String humidity = "";
            String feelsLike = "";
            String windSpeed = "";
            String country = "";

            for(int i = 0;i<array.length();i++)
            {
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
                icon = weatherPart.getString("icon");
            }
            JSONObject mainPart = new JSONObject(mainTemp);
            JSONObject windPart = new JSONObject(wind);
            JSONObject sysPart = new JSONObject(sys);


            temp = mainPart.getString("temp");
            feelsLike = mainPart.getString("feels_like");
            tempMin = mainPart.getString("temp_min");
            tempMax = mainPart.getString("temp_max");
            pressure = mainPart.getString("pressure");
            humidity = mainPart.getString("humidity");
            windSpeed = windPart.getString("speed");
            country = sysPart.getString("country");

            Log.i("main"  ,main);
            Log.i("description"  ,description);
            Log.i("icon"  ,icon);
            Log.i("temp"  ,temp);
            Log.i("feels like"  ,feelsLike);
            Log.i("temp_min"  ,tempMin);
            Log.i("temp_max"  ,tempMax);
            Log.i("pressure"  ,pressure);
            Log.i("humidity"  ,humidity);
            Log.i("Wind speed"  ,windSpeed);
            Log.i("country"  ,country);
            Log.i("cityName"  ,name);
            Log.i("id"  ,id);

            ImageView imageView = findViewById(R.id.weatherIcon);
            String imageUrl = "http://openweathermap.org/img/wn/"+icon+"@2x.png";
            Picasso.get().load(imageUrl).into(imageView);
            Date cal = Calendar.getInstance().getTime();
            TextView currentTime = findViewById(R.id.textCurrentTime);
            TextView locationText = findViewById(R.id.textCityCountry);
            TextView tempText = findViewById(R.id.temp);
            TextView descriptionText = findViewById(R.id.textDescription);
            currentTime.setText("Last updated: "+cal.toString());
            locationText.setText(name+","+country);
            tempText.setText(temp+"°C");
            descriptionText.setText("Feels like "+feelsLike+"°C. "+description+".");

            Button weatherButton = findViewById(R.id.btnWeatherURL);
            weatherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("https://openweathermap.org/city/"+id));
                    startActivity(viewIntent);
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


    }
}