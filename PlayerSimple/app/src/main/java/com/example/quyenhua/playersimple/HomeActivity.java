package com.example.quyenhua.playersimple;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quyenhua.playersimple.Adapter.SlideAdapter;
import com.example.quyenhua.playersimple.Adapter.SongAdapter;
import com.example.quyenhua.playersimple.Baihat.Song;
import com.example.quyenhua.playersimple.RecentPlay.Player;
import com.example.quyenhua.playersimple.loadurl.XMLDOMParser1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    ViewPager myPager;
    SlideAdapter adapter;
    SongAdapter songAdapter;
    String url = "http://mp3.zing.vn/";
    private int imageArra[] ;
    ArrayList<String> imgArr;
    TextView tvShow;
    ListView lvHotSong;
    public final String URL_SONG = "http://mp3.zing.vn/html5/song/";

    private ArrayList<Song> arrayListHotSong;
    private SearchView svSearch;
    private ArrayList<String> arrayBaiHat;
    private ArrayList<String> arrayList;
    ArrayList<Song> hotSongList;

    private String searchName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        arrayListHotSong = new ArrayList<>();
        arrayList = new ArrayList<>();
        lvHotSong = (ListView)findViewById(R.id.listViewHotSong);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadXML().execute(url);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
        //Toast.makeText(MainActivity.this,"" + arrayList.size(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        searchName = newText;
        return true;
    }

    class ReadXML extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return docNoiDung_Tu_URL(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            XMLDOMParser1 parser = new XMLDOMParser1();
            org.jsoup.nodes.Document doc = Jsoup.parse(s);
            Elements e = doc.select("div.slide-scroll img");

            Elements hotSong = doc.select(".fn-song");
            imgArr = new ArrayList<String>();
            imgArr.add(e.get(0).attr("src"));

            hotSongList = new ArrayList<Song>();

            for (int i = 0; i < 10; i++){
                Element elementSong = hotSong.get(i);
                String data_code = elementSong.attr("data-code");
                String url = URL_SONG + data_code;

                arrayList.add(i,data_code);
                String title = elementSong.select(".txt-primary a").text();
                String artist = elementSong.select("span h4 a").text();
                String imgUrl = elementSong.select(".thumb img").attr("src");
                arrayListHotSong.add(new Song(title, artist,url, null, imgUrl, null,null));

            }

            songAdapter = new SongAdapter(HomeActivity.this, R.layout.item_list_song, arrayListHotSong);
            songAdapter.notifyDataSetChanged();
            lvHotSong.setAdapter(songAdapter);
            for(int i = 1; i < e.size(); i++ ){
                imgArr.add(e.get(i).attr("data-lazy"));
            }
            lvHotSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent player = new Intent(HomeActivity.this, Player.class);
                    arrayBaiHat = new ArrayList<>();
                    arrayBaiHat.add(arrayList.get(i));
                    arrayBaiHat.add(arrayListHotSong.get(i).getTitle());
                    arrayBaiHat.add(arrayListHotSong.get(i).getArtist());
                    arrayBaiHat.add(arrayListHotSong.get(i).getUrl());
                    arrayBaiHat.add(arrayListHotSong.get(i).getLiric());
                    arrayBaiHat.add(arrayListHotSong.get(i).getBgcover());
                    arrayBaiHat.add(arrayListHotSong.get(i).getMv());
                    arrayBaiHat.add(arrayListHotSong.get(i).getArtisturl());
                    player.putStringArrayListExtra("arrSong", arrayBaiHat);
                    startActivity(player);

                }
            });
            myPager = (ViewPager)findViewById(R.id.viewPagerSlider);
           adapter = new SlideAdapter(HomeActivity.this, imgArr);
            myPager.setAdapter(adapter);

        }
    }

    private static String docNoiDung_Tu_URL(String theUrl)
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
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return content.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        addMenuItem(menu);

        MenuItem itemSearch = menu.findItem(R.id.svSearch);
        svSearch = (SearchView) itemSearch.getActionView();
        //Toast.makeText(this, svSearch.toString(), Toast.LENGTH_SHORT).show();
        svSearch.setOnQueryTextListener(this);
        return true;
    }
    public boolean addMenuItem(Menu menu){
        //param 1: group item
        //param 2: id item
        //param 3: thứ tự xuất hiện của menu item
        //param 4: title item
        menu.add(0, 1, 1, "Quyen1");
        menu.add(0, 2, 2, "Quyen2");
        menu.add(0, 3, 3, "Quyen3");
        menu.add(0, 4, 4, "Quyen4");
        return true;
    }
}
