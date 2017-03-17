package com.example.quyenhua.playersimple;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    String Id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new LoadData().execute("http://mp3.zing.vn/bai-hat/Anh-Yeu-Em-Khac-Viet/ZW79BFD9.html");
            }
        });
    }

    class LoadData extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String url = LoadDataFromURL(strings[0]);
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();

            //String html = "<div><p>Lorem ipsum.</p>";
            Document doc = Jsoup.parse(s);
            Elements div = doc.select("div#zplayerjs-wrapper");
            String data_xml = div.attr("data-xml");
            int point = 0;
            for(int i = 0; i < data_xml.length(); i++){
                if(String.valueOf(data_xml.charAt(i)).equals("/")){
                    point = i;
                }
            }
            for(int point1 = point + 1; point1 < data_xml.length(); point1++){
                Id = Id + String.valueOf(data_xml.charAt(point1));
            }
            Toast.makeText(MainActivity.this, Id, Toast.LENGTH_LONG).show();
        }
    }

    private static String LoadDataFromURL(String theUrl)
    {
        StringBuilder content = new StringBuilder();

        try
        {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}
