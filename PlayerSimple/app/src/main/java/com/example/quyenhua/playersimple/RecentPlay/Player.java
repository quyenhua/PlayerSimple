package com.example.quyenhua.playersimple.RecentPlay;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.quyenhua.playersimple.Interface.ComunicationPlayer;
import com.example.quyenhua.playersimple.R;

public class Player extends Activity implements ComunicationPlayer{

    TextView textView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initializeView();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentSongList fragmentSongList = new FragmentSongList();
                Bundle bundle = new Bundle();
                bundle.putString("dulieu", "dữ liệu được truyền qua fragment");

                fragmentSongList.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //fragmentTransaction.add(R.id.layoutfragmentplayer, new FragmentPlayer());
                fragmentTransaction.add(R.id.layoutfragmentsonglist, fragmentSongList);
                fragmentTransaction.commit();
            }
        });
    }

    private void initializeView(){
        textView = (TextView) findViewById(R.id.text);
        button = (Button) findViewById(R.id.button2);
    }

    @Override
    public void changeText(String text) {
        textView.setText(text);
    }
}
