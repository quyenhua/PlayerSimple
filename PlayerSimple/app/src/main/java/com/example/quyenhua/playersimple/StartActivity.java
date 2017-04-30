package com.example.quyenhua.playersimple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Thread welcome =new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    Intent it = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(it);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        welcome.start();
    }
}
