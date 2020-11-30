package com.example.cps731_termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.example.cps731_termproject.utils.NetworkUtils;
import com.example.cps731_termproject.utils.NewsItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class InfoActivity extends BaseActivity {

    private final String TAG = "InfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    List<NewsItem> dataList = new ArrayList<>();
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;

    LocationManager locationManager;

    double latitude, longitude;

    private static final int REQUEST = 112;
    String [] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};



    // UI
    ImageView imageView;
    TextView currentTime;
    TextView locationText;
    TextView tempText;
    TextView descriptionText;
    Button btnSettings;
    Button weatherButton;

    String id;
    String countryName;
    Geocoder geocoder;

    class Information extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... address)
        {
            try{
                URL url =  new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept", "application/geo+json;version=1");
                connection.setRequestProperty("User-Agent", "http://newsapi.org/");
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
    public void onBackPressed(){
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Authenticate
        mAuth = getmAuth();
        user = mAuth.getCurrentUser();
        db = getDb();

        if (user == null){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        if (!NetworkUtils.isNetworkConnected(this)){
            startActivity(new Intent(this, NoInternetActivity.class));
        }

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // UI
        imageView = findViewById(R.id.weatherIcon);
        currentTime = findViewById(R.id.textCurrentTime);
        locationText = findViewById(R.id.textCityCountry);
        tempText = findViewById(R.id.temp);
        descriptionText = findViewById(R.id.textDescription);
        btnSettings = findViewById(R.id.btnSettings);
        weatherButton = findViewById(R.id.btnWeatherURL);



        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.i("TAG", "lat" + latitude + "long: " + longitude);

            }
        };

        // Default
        String countryName =  "CA";
        String searchLocationQuery = "";


        DocumentReference documentReference = db.collection("users").document(user.getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String location = document.getString("location");

                        String [] tempValues = getWeather("http://api.openweathermap.org/data/2.5/weather?q=" + location + "&units=metric&APPID=b6c15dcbe8b4a2a7df28700233152283");
                        getNews(tempValues[0]);
                        //mEmail.setText(document.getString("email"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        if (!hasPermissions(InfoActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) InfoActivity.this, PERMISSIONS, REQUEST );
        }

        // Init GPS Service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        recyclerView = findViewById(R.id.recycler_viewNews);
        newsAdapter = new NewsAdapter(InfoActivity.this, dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Dialog
                Dialog dialog = new Dialog(InfoActivity.this);
                //dialog.setContentView(R.layout.content_main);
                dialog.setContentView(R.layout.dialog_settings);

                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setLayout(width,height);
                dialog.show();

                //Init and assign variables
                EditText editText = dialog.findViewById(R.id.edit_textLocation);
                ImageButton btnGPS = dialog.findViewById(R.id.btnGPS);
                Button btnUpdate = dialog.findViewById(R.id.btn_updateLocation);

                btnGPS.setOnClickListener((new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            if (!hasPermissions(InfoActivity.this, PERMISSIONS)) {
                                ActivityCompat.requestPermissions((Activity) InfoActivity.this, PERMISSIONS, REQUEST );
                            }
                            else {

                                Location location = null;

                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, locationListener);
                                Criteria criteria = new Criteria();
                                String bestProvider = locationManager.getBestProvider(criteria, true);
                                if(bestProvider != null)
                                    location = locationManager.getLastKnownLocation(bestProvider);

                                if (location == null) {
                                    Toast.makeText(getApplicationContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();

                                } else {
                                    String [] tempValues = getWeather("http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&units=metric&APPID=b6c15dcbe8b4a2a7df28700233152283");
                                    getNews(tempValues[0]);

                                    if (tempValues != null) {

                                        DocumentReference documentReference = db.collection("users").document(user.getUid());
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("location", tempValues[1] + "," + tempValues[0]);
                                        documentReference.update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: User Profile is created for " + user.getUid());
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e.toString());
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(InfoActivity.this, "GPS no signal.", Toast.LENGTH_LONG).show();
                                    }
                                }
                                dialog.dismiss();
                            }


                        }
                        catch(SecurityException e){
                            e.printStackTrace();
                        }
                    }
                }));

                btnUpdate.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){

                        dialog.dismiss();
                        //Get Updated Text
                        String locationTxt = editText.getText().toString().trim();

                        String [] tempValues = getWeather("http://api.openweathermap.org/data/2.5/weather?q=" + locationTxt + "&units=metric&APPID=b6c15dcbe8b4a2a7df28700233152283");

                        try{
                            String country = "";
                            int findComma = locationTxt.lastIndexOf(",");
                            if (findComma != -1) {
                                country = locationTxt.substring(findComma + 1).trim();
                            }
                            else{
                                //Toast.makeText(InfoActivity.this, "Invalid Location", Toast.LENGTH_LONG).show();
                                country = "";
                            }
                            //String country = findComma != -1 ? locationTxt.substring(findComma + 1).trim() : "";

                            boolean dataIsFine = getNews(country);
                            if (dataIsFine){
                                DocumentReference documentReference = db.collection("users").document(user.getUid());
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("location", tempValues[1] + "," + tempValues[0]);
                                documentReference.update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: User Profile is created for " + user.getUid());
                                    }
                                }). addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.toString());
                                    }
                                });
                            }
                        }
                        catch(StringIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }


                        boolean[] temp = new boolean[] {false, false, false, false, false, false, false};


                    }
                });
            }
        });

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

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_info;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    public boolean getNews(String country){
        Information news = new Information();

        boolean returnValue = false;

        //news
        try {
            String content2;
            Log.i("asd :"  ,"dsakd;lasd");
            content2 = news.execute("http://newsapi.org/v2/top-headlines?country="+country+"&pagesize=5&apiKey=d10ca8f5633d47209bd5a4736d102516").get();
            Log.i("asd :"  ,content2);
            Log.i("test :"  ,"http://newsapi.org/v2/top-headlines?country="+country+"&pagesize=5&apiKey=d10ca8f5633d47209bd5a4736d102516");
            JSONObject jsonObject = new JSONObject(content2);
            String newsData = jsonObject.getString("articles");

            Log.i("newsData :"  ,newsData);
            JSONArray array = new JSONArray(newsData);

            String author = "";
            String source = "";
            String title = "";
            String description = "";
            String url = "";
            String urlToImage = "";
            String publishedAt = "";
            String contents = "";
            String totalResults = jsonObject.getString("totalResults");
            Log.i("totalResults"  ,totalResults);

            if (!totalResults.equals("0")) {
                Toast.makeText(InfoActivity.this, "Location Updated.", Toast.LENGTH_LONG).show();
                //Log.i("WWW"  ,totalResults);

                dataList.clear();
                returnValue = true;
            }
            else{
                Toast.makeText(InfoActivity.this, "Invalid Input.", Toast.LENGTH_LONG).show();
                //Log.i("WWW@"  ,totalResults);

            }

            for(int i = 0;i<array.length();i++)
            {
                JSONObject inArticle = array.getJSONObject(i);
                author = inArticle.getString("author");
                source = inArticle.getJSONObject("source").getString("name");
                title = inArticle.getString("title");
                description = inArticle.getString("description");
                url = inArticle.getString("url");
                urlToImage = inArticle.getString("urlToImage");
                publishedAt = inArticle.getString("publishedAt");
                contents = inArticle.getString("content");

                dataList.add(new NewsItem(title, description, author, source, url, urlToImage));

                Log.i("author"  , author);
                Log.i("source"  ,source);
                Log.i("title"  ,title);
                Log.i("description"  ,description);
                Log.i("url"  ,url);
                Log.i("urlToImage"  ,urlToImage);
                Log.i("publishedAt"  ,publishedAt);
                Log.i("content"  ,contents);

            }

            newsAdapter.notifyDataSetChanged();

            //ImageView imageView = findViewById(R.id.imageNews);
            //String imageUrl = urlToImage;
            //Picasso.get().load(imageUrl).into(imageView);

            //TextView temptext = findViewById(R.id.temp);
            //temptext.setText(temp+" C");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return returnValue;
    }

    public String [] getWeather(String url){
        Information weather = new Information();

        String [] tempValues = null;
        
        try {
            String content;
            String f = "imperial";
            String c = "metric";

            content = weather.execute(url).get();


            Log.i("asd :"  ,content);
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String mainTemp = jsonObject.getString("main");
            String wind = jsonObject.getString("wind");
            String sys = jsonObject.getString("sys");
            String name = jsonObject.getString("name");
            id = jsonObject.getString("id");

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
            //String country = "";

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
            countryName = sysPart.getString("country");

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
            Log.i("country"  , countryName);
            Log.i("cityName"  ,name);
            Log.i("id"  ,id);


            Date cal = Calendar.getInstance().getTime();

            String imageUrl = "http://openweathermap.org/img/wn/"+icon+"@2x.png";
            Picasso.get().load(imageUrl).into(imageView);

            currentTime.setText("Last updated: "+cal.toString());
            locationText.setText(name+", "+ countryName);
            tempText.setText(temp+"°C");
            descriptionText.setText("Feels like "+feelsLike+"°C. "+description+".");
            
            tempValues = new String[]{countryName, name};

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return tempValues;
    }

}
