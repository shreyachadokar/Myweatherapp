package com.example.myweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private RelativeLayout home;
    private ProgressBar progressbar;
    private ImageView background, search, tempicon;
    private TextView cityname, temperaturetv, tempcondition;
    private TextInputEditText EditTextCity;
    private RecyclerView rvweather;
    private ArrayList<WeatherRvModal> weatherRvModalArrayList;
    private WeatherRvAdapter weatherRvAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to make application full screen so that we dont see any status bar.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        home = findViewById(R.id.idhome);
        progressbar = findViewById(R.id.idprogressbar);
        background = findViewById(R.id.idbackground);
        search = findViewById(R.id.idsearch);
        tempicon = findViewById(R.id.idtempicon);
        cityname = findViewById(R.id.idcityname);
        temperaturetv = findViewById(R.id.idtemperature);
        tempcondition = findViewById(R.id.idtempcondition);
        rvweather = findViewById(R.id.idrvweather);
        EditTextCity = findViewById(R.id.idEdittextcity);
        weatherRvModalArrayList = new ArrayList<>();
        weatherRvAdapter = new WeatherRvAdapter(this, weatherRvModalArrayList);
        rvweather.setAdapter(weatherRvAdapter);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ;

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);


        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(), location.getLatitude());
        getWeatherInfo(cityName);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city=EditTextCity.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "please enter city name", Toast.LENGTH_SHORT).show();
                }else{
                    cityname.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0  && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude) {

    String cityName = "Not found";
    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
    try {
        List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

        for (Address adr : addresses) {
            if (adr != null) {
                String city = adr.getLocality();
                if (city != null && !city.equals("")) {
                    cityName = city;
                } else {
                    Log.d("TAG", "CITY NOT FOUND");
                    Toast.makeText(this, "User city not found", Toast.LENGTH_SHORT).show();

                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return cityName;
}

    private void getWeatherInfo(String cityName){
        String url="http://api.weatherapi.com/v1/forecast.json?key=87f29c938d064af298d45645231006&q="+cityName+ "&days=1&aqi=yes&alerts=yes";
        cityname.setText(cityName);
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressbar.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);
                weatherRvModalArrayList.clear();
                try {
                    String temperature=response.getJSONObject("current").getString("temp_c");
                    temperaturetv.setText(temperature+"°c");
                    int isDay= response.getJSONObject("current").getInt("is_day");
                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("url");
                    Picasso.get().load("http".concat(conditionIcon)).into(tempicon);
                    tempcondition.setText(condition);
                    if(isDay==1){
                        Picasso.get().load("https://www.google.com/search?q=clouds&tbm=isch&hl=en&chips=q:clouds,g_1:sky:TbufC0jRP-s%3D,g_1:wallpaper:15CqJ7Vsjr0%3D&sa=X&ved=2ahUKEwiuy6_k5rv_AhXKkNgFHbA6BjIQ4lYoAnoECAEQLg&biw=1349&bih=657#imgrc=mgyCtJtDv209lM").into(background);
                    }else{
                        Picasso.get().load("https://www.google.com/search?q=night+clouds+aesthetic&tbm=isch&ved=2ahUKEwjC4oKY6Lv_AhW0gtgFHVjQBsoQ2-cCegQIABAA&oq=night+clouds+aesthetic&gs_lcp=CgNpbWcQAzIFCAAQgAQyBQgAEIAEMgYIABAHEB4yBggAEAcQHjIICAAQBRAHEB4yCAgAEAUQBxAeMggIABAFEAcQHjIGCAAQCBAeMgYIABAIEB4yBggAEAgQHjoECCMQJzoICAAQCBAHEB5QvQtYlhlgjRxoAnAAeACAAYMCiAHUCJIBBTAuNS4xmAEAoAEBqgELZ3dzLXdpei1pbWfAAQE&sclient=img&ei=_Q2GZILwO7SF4t4P2KCb0Aw&bih=657&biw=1349&hl=en#imgrc=3D35mdAXaNicaM&imgdii=GEvLdVUyIcumxM").into(background);
                    }
                    JSONObject forecastObj=response.getJSONObject("forecast");
                    JSONObject forecastO=forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray=forecastO.getJSONArray("hour");
                    for(int i=0; i<hourArray.length();i++){
                        JSONObject hourObj=hourArray.getJSONObject(i);
                        String time=hourObj.getString("time");
                        String temper=hourObj.getString("temp_c");
                        String img=hourObj.getJSONObject("condition").getString("icon");
                        String wind=hourObj.getString("wind_kph");
                        weatherRvModalArrayList.add(new WeatherRvModal(time,temper,img,wind));
                    }
                    weatherRvAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }


}