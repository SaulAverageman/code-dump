package com.bubai.footballnotsoccer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListView extends ArrayAdapter {
    private ArrayList<String> headlines_list;

    private ArrayList<Bitmap> imageid;
    private Activity context;
    public CustomListView(Activity context, ArrayList<String> headlines_list, ArrayList<Bitmap> imageid){
        super(context, R.layout.row_item, headlines_list);
        this.headlines_list=headlines_list;
        this.context=context;

        this.imageid=imageid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.row_item, null, true);
        TextView textViewCountry = (TextView) row.findViewById(R.id.headline);

        ImageView imageFlag = (ImageView) row.findViewById(R.id.main_image);

        textViewCountry.setText(headlines_list.get(position));
        try{
        imageFlag.setImageBitmap(imageid.get(position));}
        catch (Exception e){

            imageFlag.setImageBitmap(Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888));
        }
        return  row;
    }

}
