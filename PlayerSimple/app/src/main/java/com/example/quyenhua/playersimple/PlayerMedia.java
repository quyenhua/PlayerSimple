package com.example.quyenhua.playersimple;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.quyenhua.playersimple.Utility.Utility;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerMedia extends AppCompatActivity {

    private ImageButton imgPlay, imgBack, imgNext, imgPre;
    private TextView tvName, tvTimeStart, tvTimeEnd, tvSinger;
    private SeekBar sbTime;
    private ImageView imageView;
    private ProgressBar pbLoading;

    private ArrayList<String> arraySong;

    private int loadSeekBar = 0;
    private double timeStart = 0;
    private double timeEnd = 0;

    private String URL_SONG = "http://mp3.zing.vn/html5/song/";

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler myHandler = new Handler();
    private Handler handler = new Handler();
    private Runnable runnable;
    private Utility utility = new Utility();

    private boolean firstLauch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_media);

        initializeView();
        arraySong = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            arraySong = bundle.getStringArrayList("arrSong");
        }

        tvName.setText(arraySong.get(1));
        tvSinger.setText(arraySong.get(2));

        playMedia();
        setBack();
        setPlay();
        setNext();
        setPrevious();
        setSeekbar();
    }

    private void initializeView(){
        imgBack = (ImageButton)findViewById(R.id.imgBack);
        imgPlay = (ImageButton)findViewById(R.id.imgPlay);
        imgNext = (ImageButton)findViewById(R.id.imgNext);
        imgPre = (ImageButton)findViewById(R.id.imgPre);
        tvName = (TextView)findViewById(R.id.tvName);
        sbTime = (SeekBar)findViewById(R.id.sbTime);
        tvTimeStart = (TextView)findViewById(R.id.tvTimeCurrent);
        tvTimeEnd = (TextView)findViewById(R.id.tvTimeEnd);
        tvSinger = (TextView)findViewById(R.id.tvSinger);
        imageView = (ImageView)findViewById(R.id.imageView);
        pbLoading = (ProgressBar)findViewById(R.id.progressBar);
    }

    private void setBack(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retMain = new Intent(PlayerMedia.this, MainActivity.class);
                startActivity(retMain);
            }
        });
    }

    private void setPlay(){
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying() && mediaPlayer != null) {
                    mediaPlayer.pause();
                    imgPlay.setImageResource(R.drawable.play);
                }
                else{
                    if(firstLauch){
                        imgPlay.setImageResource(R.drawable.pause);
                        playMedia();
                    }
                    else{
                        imgPlay.setImageResource(R.drawable.pause);
                        mediaPlayer.start();
                        firstLauch = false;
                    }
                }
            }
        });
    }

    private void setNext(){
        imgNext.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                double timeDB = timeStart + 5000;
                if(timeDB <= timeEnd){
                    mediaPlayer.seekTo((int) timeDB);
                }
                else{
                    mediaPlayer.seekTo((int) timeEnd);
                }
                return false;
            }
        });
    }

    private void setPrevious(){
        imgPre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                double timeDB = timeStart - 5000;
                imgPlay.setImageResource(R.drawable.pause);
                if(timeDB >= 0){
                    mediaPlayer.seekTo((int) timeDB);
                }
                else{
                    mediaPlayer.seekTo(0);
                }
                return false;
            }
        });
    }

    private void setSeekbar(){
        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(loadSeekBar);
            }
        });
    }

    private void playMedia(){
        firstLauch = false;
        pbLoading.setVisibility(View.VISIBLE);
        try {
            mediaPlayer.setDataSource(arraySong.get(3));
        } catch (IOException e) {
            try {
                mediaPlayer.setDataSource(URL_SONG + arraySong.get(0));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //playCircle();
                pbLoading.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                imgPlay.setImageResource(R.drawable.pause);
                mediaPlayer.start();
                timeEnd = mediaPlayer.getDuration();
                if(loadSeekBar == 0){
                    sbTime.setMax((int) timeEnd);
                }
                myHandler.postDelayed(UpdateTime, 100);
                tvTimeEnd.setText(utility.convertDuration((long) timeEnd));
            }
        });
    }

    private Runnable UpdateTime = new Runnable() {
        @Override
        public void run() {
            timeStart = mediaPlayer.getCurrentPosition();
            tvTimeStart.setText(utility.convertDuration((long) timeStart));
            myHandler.postDelayed(this, 100);
            sbTime.setProgress((int) timeStart);
            if(utility.convertDuration((long) timeEnd) == utility.convertDuration((long) timeStart)){
                imgPlay.setImageResource(R.drawable.play);
            }
        }
    };

    /*private void playCircle(){
        if(mediaPlayer.isPlaying()){
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCircle();
                }
            };
            handler.postDelayed(runnable, 100);
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
        //playCircle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }
}
