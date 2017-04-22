package com.example.quyenhua.playersimple;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quyenhua.playersimple.Adapter.SongAdapter;
import com.example.quyenhua.playersimple.Baihat.Song;
import com.example.quyenhua.playersimple.RecentPlay.Player;
import com.example.quyenhua.playersimple.loadurl.XMLDOMParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.example.quyenhua.playersimple.loadurl.XMLDOMParser.LoadDataFromURL;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private ListView lvDanhsach;
    private ProgressBar pbListSong;
    private SongAdapter adapter;

    private SearchView svSearch;

    private ArrayList<String> arrayList;
    private ArrayList<Song> arraySong;
    private ArrayList<String> arrayBaiHat;

    private  XMLDOMParser parser = new XMLDOMParser();

    private String name = "";

    public final String URL_SEARCH = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q=";
    public final String URL_SOURCE = "http://mp3.zing.vn/xml/song-xml/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialView();
        arrayList = new ArrayList<>();
        arraySong = new ArrayList<>();
    }

    public void initialView(){
        lvDanhsach = (ListView)findViewById(R.id.lvDanhsachbaihat);
        pbListSong = (ProgressBar)findViewById(R.id.pbLoadingListSong);
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

            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

            Document doc = Jsoup.parse(s);
            Elements div = doc.select("div.item-song");

            for(int i = 0; i < div.size(); i++) {
                String data_code = div.get(i).attr("data-code");
                //Toast.makeText(MainActivity.this, data_code, Toast.LENGTH_SHORT).show();
                //arrayList.add(i, data_code);
                arrayList.add(i, data_code);
                new LoadXML().execute(URL_SOURCE + data_code);
            }
            pbListSong.setVisibility(View.INVISIBLE);
        }
    }

    class LoadXML extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String url = LoadDataFromURL(strings[0]);
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            org.w3c.dom.Document doc = parser.getDocument(s);

            String title = parser.getValue(doc, "title");
            String artist = parser.getValue(doc, "performer");
            String url = parser.getValue(doc, "source");
            String backimage = parser.getValue(doc, "backimage");
            if(backimage == "") {
                backimage = "http://1.bp.blogspot.com/-3oZ7k46VeG4/Vk0d1nmNODI/AAAAAAAAC20/3B1E5A8BDC4/s1600/hinh-anh-dep-ve-dong-vat..jpg";
            }
            String mv = parser.getValue(doc, "mv");
            String link = parser.getValue(doc, "link");

            arraySong.add(new Song(title, artist, url, backimage, mv, link));
            //Toast.makeText(MainActivity.this, "" + arraySong.size(), Toast.LENGTH_SHORT).show();
            adapter = new SongAdapter(MainActivity.this, R.layout.item_list_song, arraySong);
            adapter.notifyDataSetChanged();
            lvDanhsach.setAdapter(adapter);

            lvDanhsach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent player = new Intent(MainActivity.this, Player.class);
                    arrayBaiHat = new ArrayList<>();
                    arrayBaiHat.add(arrayList.get(i));
                    arrayBaiHat.add(arraySong.get(i).getTitle());
                    arrayBaiHat.add(arraySong.get(i).getArtist());
                    arrayBaiHat.add(arraySong.get(i).getUrl());
                    player.putStringArrayListExtra("arrSong", arrayBaiHat);
                    startActivity(player);
                    //Intent player = new Intent(MainActivity.this, Player.class);
                    //startActivity(player);
                }
            });
        }
    }

//    private static String LoadDataFromURL(String theUrl) {
//        StringBuilder content = new StringBuilder();
//
//        try
//        {
//            // create a url object
//            URL url = new URL(theUrl);
//
//            Log.d("URL", String.valueOf(url));
//            // create a urlconnection object
//            URLConnection urlConnection = url.openConnection();
//
//            // wrap the urlconnection in a bufferedreader
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//
//            String line;
//
//            // read from the urlconnection via the bufferedreader
//            while ((line = bufferedReader.readLine()) != null)
//            {
//                content.append(line + "\n");
//            }
//            bufferedReader.close();
//
//        }
//        catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        return content.toString();
//    }

    public static class StringUtils{
        public static String unAccent(String s) {
            String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d").replaceAll(" ", "+");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        addMenuItem(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getGroupId() == 0){
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(name != null){
            pbListSong.setVisibility(View.VISIBLE);
            arrayList = new ArrayList<>();
            arraySong = new ArrayList<>();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for(int i=1; i<= 10; i++) {
                        new LoadData().execute(URL_SEARCH + name + "&page=" + i);
                    }
                }
            });
        }
        //Toast.makeText(MainActivity.this,"" + arrayList.size(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        StringUtils utils = new StringUtils();
        name = utils.unAccent(newText);
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
