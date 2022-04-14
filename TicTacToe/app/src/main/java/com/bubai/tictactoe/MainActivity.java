package com.bubai.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    /////////////////////INITIALIZE/////////////////
    int player=0;
    int[] status={-1,-1,-1,-1,-1,-1,-1,-1,-1};

    ImageView c0,c1,c2,c3,c4,c5,c6,c7,c8;
    ArrayList cards;
    //////////////////////ENDS HERE/////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////MY CODE//////////////////
        /////////////////////////////////////////

        c0=(ImageView)findViewById(R.id.c0);
        c1=(ImageView)findViewById(R.id.c1);
        c2=(ImageView)findViewById(R.id.c2);
        c3=(ImageView)findViewById(R.id.c3);
        c4=(ImageView)findViewById(R.id.c4);
        c5=(ImageView)findViewById(R.id.c5);
        c6=(ImageView)findViewById(R.id.c6);
        c7=(ImageView)findViewById(R.id.c7);
        c8=(ImageView)findViewById(R.id.c8);
        cards=new ArrayList();
        cards.add(c0);cards.add(c1);cards.add(c2);cards.add(c3);cards.add(c4);cards.add(c5);cards.add(c6);cards.add(c7);cards.add(c8);
        /////////////////////////////////////////
        ///////////////ENDS HERE/////////////////
    }

    public void setCross(ImageView view){
        view.setScaleY(0);
        view.setScaleX(0);
        view.setImageResource(R.drawable.cross);
        view.animate()
                .alpha(1)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100);

    }

    public void setZero(ImageView view){
        view.setScaleY(0);
        view.setScaleX(0);
        view.setImageResource(R.drawable.zero);
        view.animate()
                .alpha(1)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100);

    }




    public void playturn(View view) {
        ImageView img = (ImageView) view;
        int occupacy = status[Integer.parseInt(img.getTag().toString())];
        if (occupacy == -1) {
            if (player == 0) {
                setCross(img);
                status[Integer.parseInt(img.getTag().toString())]=0;
                player = 1;
            } else {
                setZero(img);
                status[Integer.parseInt(img.getTag().toString())]=1;
                player = 0;
            }
            result();
        }
    }

    public void result(){
        //horizontal
        for(int i=0; i<=6;i+=3) {
            if (status[i] == status[i+1] && status[i] == status[i+2]) {
                if (status[i] == 0) {
                    wincross();
                    end(0);
                    return;
                } else if (status[i]==1) {
                    winzero();
                    end(1);
                    return;
                }
            }
        }
        //horizontal ends

        //vertical
        for (int i=0;i<3;i++){
            if (status[i]==status[i+3] && status[i]==status[i+6]){
                if (status[i] == 0) {
                    wincross();
                    end(0);
                    return;
                } else if (status[i]==1) {
                    winzero();
                    end(1);
                    return;
                }
            }
        }
        //vertical ends

        //diagonal 1
        if(status[0]==status[4] &&status[0]==status[8]){
            if (status[0] == 0) {
                wincross();
                end(0);
                return;
            } else if (status[0]==1){
                winzero();
                end(1);
                return;
            }
        }
        //diagonal 1 ends

        //diagonal 2
        if(status[2]==status[4] &&status[2]==status[6]){
            if (status[2] == 0) {
                wincross();
                end(0);
                return;
            } else if (status[2]==1){
                winzero();
                end(1);
                return;
            }
        }
        //diagonal 2 ends

        //DRAW

        for(int i:status)
            if (i==-1)
                return;
        Toast.makeText(MainActivity.this,"y'all dumb",Toast.LENGTH_SHORT).show();
        end(-1);

        //DRAW ends
    }

    public void wincross(){
        TextView winner=(TextView)findViewById(R.id.textWinner);
        winner.setText("King Cross Wins!!");
    }

    public void winzero(){
        TextView winner=(TextView)findViewById(R.id.textWinner);
        winner.setText("Count Circle Wins!!");
    }

    public void end(int j){
        TableLayout T=(TableLayout)findViewById(R.id.badatable);
        T.setAlpha(0.6f);


        for(int i=0;i<9;i++)
            status[i]=0;
        LinearLayout menu=(LinearLayout)findViewById(R.id.menu);
        menu.animate()
                .translationX(0f)
                .setDuration(500);
        if (j==-1){
            TextView winner=(TextView)findViewById(R.id.textWinner);
            winner.setText("Stalemate, duh");
        }
    }
    public void playAgain(View view){
        for(int i=0;i<9;i++)
            status[i]=-1;

        TableLayout T=(TableLayout)findViewById(R.id.badatable);
        T.setAlpha(1);

        for (int i=0;i<3;i++){
            TableRow t=(TableRow)T.getChildAt(i);
            for (int j=0;j<3;j++){
                ImageView img=(ImageView)t.getVirtualChildAt(j);
                img.setImageResource(0);
            }
        }
        LinearLayout menu=(LinearLayout)findViewById(R.id.menu);
        menu.animate()
                .translationX(-1000f)
                .setDuration(100);

    }
}