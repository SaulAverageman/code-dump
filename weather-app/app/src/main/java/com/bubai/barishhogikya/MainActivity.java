package com.bubai.barishhogikya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView description,weatherTypeTextview;

    public class GetJson extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params){
            try{
                URL url=new URL(params[0]);
                HttpURLConnection con=(HttpURLConnection)url.openConnection();
                InputStream in=con.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                String result="";
                while(data!=-1){
                    char cc=(char)data;
                    result+=cc;
                    data=reader.read();
                }
                return result;
            }
            catch (Exception e){
                return e.getMessage().toString();
            }
        }
    }
    TextView cityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName =(TextView)findViewById(R.id.cityName);
        imageView =(ImageView)findViewById(R.id.imageView);
        description=(TextView)findViewById(R.id.description);
        weatherTypeTextview=(TextView)findViewById(R.id.weatherType);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    public void findWeather(View view){
        //hides keyboard
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //

        String city=cityName.getText().toString();
        if (city!=""){
            String url="https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=<>";

            GetJson rawjson=new GetJson();
            String rawdata="";
            try{
                rawdata=rawjson.execute(url).get();
                if (rawdata.equals(url)){
                    Toast.makeText(this, "Invalid city/state", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e){
                rawdata=e.getMessage().toString();
            }
            System.out.println(rawdata);
            weatherConditions(rawdata);
        }
    }
    public void weatherConditions(String json){
        weatherTypeTextview.setX(-2000);
        description.setX(-2000);
        imageView.setImageDrawable(null);


        String weatherType="";
        String temperatures="";
        try {
            JSONObject obj=new JSONObject(json);
            JSONArray weathers=new JSONArray(obj.getString("weather"));

            for(int i=0;i<weathers.length();i++){
                JSONObject parts=weathers.getJSONObject(i);
                weatherType=parts.getString("main");
            }

            temperatures=obj.getString("main");

            setWeatherText(weatherType);
            setWeatherImage(weatherType);
            setTemperatures(temperatures);
        }
        catch (Exception e){
            e.getMessage();
        }

    }
    public void animateimage(ImageView img){
        img.setScaleX(0);
        img.setScaleY(0);
        img.animate().scaleX(1)
                .scaleY(1)
                .setDuration(100);
    }
    public void setWeatherImage(String w){
        if (w=="")
            return;
        if (w!=""){
            animateimage(imageView);
            w=w.toLowerCase();
            ArrayList<String> mist=new ArrayList<String>();
            mist.add("mist");
            mist.add("haze");
            mist.add("fog");

            if (mist.contains(w))
                w="mist";

            int id=getResources().getIdentifier(w,"drawable",getPackageName());
            Drawable drawable=getResources().getDrawable(id);
            imageView.setImageResource(id);

        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void animatesmall(TextView v){
        v.animate()
                .translationXBy(2000)
                .setDuration(300);
    }
    public void setTemperatures(String temperatures){
        description.setText("");
        if (temperatures=="")
            return;
        String TEMP="Temp ", FEELS_LIKE="Feels like ",TEMP_MIN="min temp ",TEMP_MAX="max temp ";
        try{
            JSONObject JSONtemp=new JSONObject(temperatures);
            String temp=""+((int)Double.parseDouble(JSONtemp.getString("temp"))-273)+"℃";
            String feels_like=""+((int)Double.parseDouble(JSONtemp.getString("feels_like"))-273)+"℃";
            String temp_min=""+((int)Double.parseDouble(JSONtemp.getString("temp_min"))-273)+"℃";
            String temp_max=""+((int)Double.parseDouble(JSONtemp.getString("temp_max"))-273)+"℃";
            TEMP+=temp;
            FEELS_LIKE+=feels_like;
            TEMP_MIN+=temp_min;
            TEMP_MAX+=temp_max;
            String setDesc=TEMP+"\n"+FEELS_LIKE+"\n"+TEMP_MIN+"\n"+TEMP_MAX;

            description.setText(setDesc);
            animatesmall(description);
        }
        catch (Exception e){
            Log.e("last",e.getMessage().toString());
        }
    }
    /////////////////////////////////////
    /////////////////////////////////////
    public void animatebig(TextView v){
        v.animate()
                .translationXBy(2000)
                .setDuration(200);
    }
    public void setWeatherText(String w){

        if (w=="")
            return;
        weatherTypeTextview.setText(w);
        animatebig(weatherTypeTextview);
    }
    /////////////////////////////////////

}
