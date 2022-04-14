package com.bubai.footballnotsoccer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    ListView newsListView;
    ArrayList<String> newsList;
    ArrayList<Bitmap> imageList;
    CustomListView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsListView=(ListView)findViewById(R.id.newsListView);
        newsList=new ArrayList<String>();
        imageList=new ArrayList<Bitmap>();
        adapter=new CustomListView(this, newsList, imageList);
        //adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,newsList);

        updateNews();

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadWebPage(position);
            }
        });
    }

    public class GetBitMap extends AsyncTask<String,Void, Bitmap>{
        @Override
        public Bitmap doInBackground(String... site) {
            URL url;
            HttpsURLConnection con;
            Bitmap image = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
            try {
                url = new URL(site[0]);

                con = (HttpsURLConnection) url.openConnection();
                con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:221.0) Gecko/20100101 Firefox/31.0"); // add this line to your code
                con.connect();

                InputStream in = null;
                int status = con.getResponseCode();

                if (status != HttpURLConnection.HTTP_OK) {
                    Log.e("error", Integer.toString(status));
                    in = con.getErrorStream();
                } else {
                    Log.e("success", "success2");
                    in = con.getInputStream();
                }
                image = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("fail","fail1");
                System.out.println(e.getMessage().toString());
            } finally {
                return image;
            }
        }
    }

    public class GetNews extends AsyncTask<String, Void , String>{
        @Override
        public String doInBackground(String... site){
            String result="";
            URL url;
            HttpsURLConnection con;
            try{
                url=new URL(site[0]);

                con=(HttpsURLConnection)url.openConnection();
                con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:221.0) Gecko/20100101 Firefox/31.0"); // add this line to your code
                con.connect();

                InputStream in;
                int status = con.getResponseCode();

                if (status != HttpURLConnection.HTTP_OK)  {
                    Log.e("error",Integer.toString(status));
                    in = con.getErrorStream();
                }
                else  {
                    Log.e("success","success1");
                    in = con.getInputStream();
                }
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while (data!=-1){
                    result=result+(char) data;
                    data=reader.read();
                }
            }
            catch(Exception e){
                Log.e("fail","fail2");
                System.out.println(e.toString());
            }
            return result;
        }

    }

    public void updateNews(){
        GetNews getNews=new GetNews();

        String website="https://www.google.com";
        website="https://newsapi.org/v2/everything?q=Laliga&apiKey=66038627ca6c463cb45cfc6e8df5ebf7";
        try{
            String allData="";
            allData=getNews.execute(website).get();
            JSONObject obj=new JSONObject(allData);
            JSONArray articleArr=obj.getJSONArray("articles");
            addToDB(articleArr,articleArr.length());
        }
        catch (Exception e){
            Log.e("fail","fail3");
            System.out.println(e.toString());
        }
    }

    public void addToDB(JSONArray articles, int total){
        try{
            db=this.openOrCreateDatabase("newsDB",MODE_PRIVATE,null);
            db.execSQL("CREATE TABLE IF NOT EXISTS newsitems (id int(3),title varchar, url varchar, imageurl varchar)");
            db.execSQL("DELETE  FROM newsitems");
            int index=0;
            for (int i=0;i<total;i++){
                JSONObject obj=articles.getJSONObject(i);
                int ck=0;
                String query = "INSERT INTO newsitems values( "+Integer.toString(index)+",'" + handleString(obj.getString("title") )+ "', '" + obj.getString("url") + "', '" + obj.getString("urlToImage") + "' );";
                try {
                    db.execSQL(query);
                    index+=1;
                }
                catch (Exception e){
                    Log.e("skipped","skipped");
                }

            }
        }
        catch (Exception E){
            Log.e("fail","fail4");
            System.out.println(E.toString());
        }
        addToList();
    }
    public String handleString(String s){
        String out="";
        for (int i=0; i<s.length();i++){
            if (s.charAt(i)!="'".charAt(0))
                out+=s.charAt(i);
        }
        return out;
    }
    public void addToList(){
        try{
            Cursor c=db.rawQuery("SELECT * FROM newsitems", null);
            int titleIndex=c.getColumnIndex("title");
            int imageurlIndex=c.getColumnIndex("imageurl");
            c.moveToFirst();
            do {
                newsList.add(c.getString(titleIndex));
                addImageToList(c.getString(imageurlIndex));
                //System.out.println(c.getString(titleIndex));
            }while (c.moveToNext());
        }
        catch (Exception e){
            Log.e("fail","fail5");
            System.out.println(e.getMessage().toString());
        }
        newsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public void addImageToList(String website){
        GetBitMap getBitMap=new GetBitMap();
        Bitmap img=null;
        try{
            img=getBitMap.execute(website).get();

        }
        catch (Exception e){
            Log.e("fail7","fail7");
        }
        if (img==null){
            img=Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        }
        imageList.add(img);
    }
    public void loadWebPage(int index){
        try{
            Cursor c=db.rawQuery("SELECT * from newsitems WHERE id="+Integer.toString(index)+";",null);
            c.moveToFirst();
            int reqi=c.getColumnIndex("url");
            String url=c.getString(reqi);
            Log.e("url",url);
            Intent i=new Intent(getApplicationContext(),WebActivity.class);
            i.putExtra("URL",url);
            startActivity(i);
        }
        catch (Exception e){
            Log.e("fail","fail6");
            System.out.println(e.getMessage().toString());
        }

    }
}
