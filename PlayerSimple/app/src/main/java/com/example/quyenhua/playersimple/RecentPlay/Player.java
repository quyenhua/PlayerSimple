package com.example.quyenhua.playersimple.RecentPlay;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quyenhua.playersimple.Adapter.ViewPagerAdapter;
import com.example.quyenhua.playersimple.Baihat.Song;
import com.example.quyenhua.playersimple.MainActivity;
import com.example.quyenhua.playersimple.R;
import com.example.quyenhua.playersimple.Utility.Utility;
import com.example.quyenhua.playersimple.loadurl.XMLDOMParser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class Player extends FragmentActivity {

    private ViewPager vpPlayer;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;

    private ImageButton imgPlay, imgBack, imgNext, imgPre, imgInfo;
    private TextView tvName, tvTimeStart, tvTimeEnd, tvSinger;
    private SeekBar sbTime;

    private ArrayList<String> arraySong;
    private ArrayList<Song> arrItemSong;

    private int loadSeekBar = 0;
    private double timeStart = 0;
    private double timeEnd = 0;

    public final String URL_SONG = "http://mp3.zing.vn/html5/song/";
    public final String URL_SOURCE = "http://mp3.zing.vn/xml/song-xml/";
    public final String TITLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler myHandler = new Handler();
    private Utility utility = new Utility();

    private XMLDOMParser parser = new XMLDOMParser();

    private boolean firstLauch = true;

    private String fileName = "note.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initializeView();

        arrItemSong = new ArrayList<>();
        List<Fragment> fragmentList = new Vector<>();
        fragmentList.add(Fragment.instantiate(this, FragmentPlayer.class.getName()));
        fragmentList.add(Fragment.instantiate(this, FragmentSongList.class.getName()));
        fragmentList.add(Fragment.instantiate(this, FragmentLyric.class.getName()));

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        vpPlayer.setAdapter(viewPagerAdapter);

        //tabLayout.setupWithViewPager(vpPlayer);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            arraySong = bundle.getStringArrayList("arrSong");
        }

        tvName.setText(arraySong.get(1));
        tvSinger.setText(arraySong.get(2));

        playMedia();
        saveDataSong(arraySong.get(0));
        setEvent();

    }

    private void setEvent() {
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying() && mediaPlayer != null) {
                    mediaPlayer.pause();
                    imgPlay.setBackgroundResource(android.R.drawable.ic_media_play);
                }
                else{
                    if(firstLauch){
                        imgPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
                        playMedia();
                    }
                    else{
                        imgPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
                        mediaPlayer.start();
                        firstLauch = false;
                    }
                }
            }
        });

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

        imgPre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                double timeDB = timeStart - 5000;
                imgPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
                if(timeDB >= 0){
                    mediaPlayer.seekTo((int) timeDB);
                }
                else{
                    mediaPlayer.seekTo(0);
                }
                return false;
            }
        });

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

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(Player.this);
                dialog.setTitle("Infomation");
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_infomation);
                ImageView imgBackground = (ImageView) dialog.findViewById(R.id.imgBackground);
                TextView textName = (TextView) dialog.findViewById(R.id.tvName);
                TextView tvArtist = (TextView) dialog.findViewById(R.id.tvLinkArtist);
                TextView tvMv = (TextView) dialog.findViewById(R.id.tvLinkMv);
                Picasso.with(Player.this).load(arraySong.get(5)).placeholder(R.drawable.demo)
                        .error(R.drawable.demo).into(imgBackground, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }
                        });
                textName.setText(arraySong.get(1));
                tvArtist.setText(arraySong.get(2));

                tvArtist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Player.this, "link ca sĩ", Toast.LENGTH_SHORT).show();
                    }
                });

                tvMv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Player.this, "link MV", Toast.LENGTH_SHORT).show();
                    }
                });
                //tvMv.setText(arraySong.get(6));
                dialog.show();
            }
        });
    }

//    public void setArtist(View v){
//        Toast.makeText(this, "link ca sĩ", Toast.LENGTH_SHORT).show();
//    }
//
//    public void setMv(View v){
//        Toast.makeText(this, "link MV", Toast.LENGTH_SHORT).show();
//    }

    private void saveDataSong(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new LoadDataSong().execute(URL_SOURCE + arraySong.get(0));
            }
        });
    }

    class LoadDataSong extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            String url = LoadDataFromURL(strings[0]);
            return url;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            saveData(s);
            String news = readData();

            Document doc = parser.getDocument(news);

            NodeList item = doc.getElementsByTagName("item");
            for(int i = 0; i < item.getLength(); i++){
                Element title = (Element) item.item(i);
                String tsong = parser.getValueData(title, "title");
                String asong = parser.getValueData(title, "performer");
                String url = parser.getValueData(title, "source");
                String lsong = parser.getValueData(title, "lyric");
                String bsong = parser.getValueData(title, "backimage");
                String mvsong = parser.getValueData(title, "mv");
                String arsong = parser.getValueData(title, "link");
                arrItemSong.add(i, new Song(tsong, asong, url, lsong, bsong, mvsong, arsong));
            }
        }
    }

//    private void askPermissionAndWriteFile(String data) {
//        boolean canWrite = this.askPermission(REQUEST_ID_WRITE_PERMISSION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        //
//        if (canWrite) {
//            this.writeFile(data);
//        }
//    }
//
//    private void writeFile(String data) {
//        // Thư mục gốc của SD Card.
//        File extStore = Environment.getExternalStorageDirectory();
//        // ==> /storage/emulated/0/note.txt
//        String path = extStore.getAbsolutePath() + "/" + fileName;
//        Log.i("ExternalStorageDemo", "Save to: " + path);
//
//        try {
//            File myFile = new File(path);
//            myFile.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(myFile);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//            myOutWriter.append(data);
//            myOutWriter.close();
//            fOut.close();
//
//            Toast.makeText(getApplicationContext(), fileName + " saved", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void readDataFromExternalStorage() {
//
//        String data = askPermissionAndReadFile();
//        //Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
//        Document doc = parser.getDocument(data);
//
//    }
//
//    private String askPermissionAndReadFile() {
//        boolean canRead = this.askPermission(REQUEST_ID_READ_PERMISSION,
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//        String data = "";
//        if (canRead) {
//            data = this.readFile();
//        }
//        return data;
//    }
//
//    private String readFile() {
//        // Thư mục gốc của SD Card.
//        File extStore = Environment.getExternalStorageDirectory();
//        // ==> /storage/emulated/0/note.txt
//        String path = extStore.getAbsolutePath() + "/" + fileName;
//        Log.i("ExternalStorageDemo", "Read file: " + path);
//
//        String s = "";
//        String fileContent = "";
//        try {
//            File myFile = new File(path);
//            FileInputStream fIn = new FileInputStream(myFile);
//            BufferedReader myReader = new BufferedReader(
//                    new InputStreamReader(fIn));
//
//            while ((s = myReader.readLine()) != null) {
//                fileContent += s + "\n";
//            }
//            myReader.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return fileContent;
//    }
//
//    private boolean askPermission(int requestId, String permissionName) {
//        if (android.os.Build.VERSION.SDK_INT >= 23) {
//
//            // Kiểm tra quyền
//            int permission = ActivityCompat.checkSelfPermission(this, permissionName);
//
//
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//
//                // Nếu không có quyền, cần nhắc người dùng cho phép.
//                this.requestPermissions(
//                        new String[]{permissionName},
//                        requestId
//                );
//                return false;
//            }
//        }
//        return true;
//    }

    private String readData() {
        StringBuilder builder = new StringBuilder();
        String content = "";
        try {
            FileInputStream input = this.openFileInput(fileName);
            BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
            builder.append(TITLE + '\n' + "<data>" + '\n');
            while ((content = buffer.readLine()) != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if(!Objects.equals(content, TITLE) && !Objects.equals(content, "<data>") && !Objects.equals(content, "</data>")) {
                        builder.append(content).append('\n');
                    }
                }
            }
            builder.append("</data>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error1:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error2:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return builder.toString();
    }

    private void saveData(String s) {
//        org.w3c.dom.Document doc = parser.getDocument(s);
//
//        String title = parser.getValue(doc, "title");
//        String artist = parser.getValue(doc, "performer");
//        String url = parser.getValue(doc, "source");
//        String lyric = parser.getValue(doc, "lyric");
//        String backimage = parser.getValue(doc, "backimage");
//        if(backimage == "") {
//            backimage = "http://1.bp.blogspot.com/-3oZ7k46VeG4/Vk0d1nmNODI/AAAAAAAAC20/3B1E5A8BDC4/s1600/hinh-anh-dep-ve-dong-vat..jpg";
//        }
//        String mv = parser.getValue(doc, "mv");
//        String link = parser.getValue(doc, "link");
//
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        Document sdoc;
//        try {
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            sdoc = db.newDocument();
//            Node data = sdoc.createElement("data");
//            sdoc.appendChild(data);
//
//            Node item = sdoc.createAttribute("item");
//            data.appendChild(item);
//
//            Node ntitle = sdoc.createElement("title");
//            ntitle.setTextContent(title);
//            item.appendChild(ntitle);
//
//            Node nperformer = sdoc.createElement("performer");
//            nperformer.setTextContent(artist);
//            item.appendChild(nperformer);
//
//            Node nsource = sdoc.createElement("source");
//            nsource.setTextContent(url);
//            item.appendChild(nsource);
//
//            Node nlyric = sdoc.createElement("lyric");
//            nlyric.setTextContent(lyric);
//            item.appendChild(nlyric);
//
//            Node nbackground = sdoc.createElement("background");
//            nbackground.setTextContent(backimage);
//            item.appendChild(nbackground);
//
//            Node nmv = sdoc.createElement("mv");
//            nmv.setTextContent(mv);
//            item.appendChild(nmv);
//
//            Node nlink = sdoc.createElement("link");
//            nlink.setTextContent(link);
//            item.appendChild(nlink);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
        try {
            FileOutputStream output = this.openFileOutput(fileName, MODE_PRIVATE);
            output.write(s.getBytes());
            output.close();
            //Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error1:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error2:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeView(){
        vpPlayer = (ViewPager) findViewById(R.id.vpPlayer);
        imgBack = (ImageButton)findViewById(R.id.imgBack);
        imgPlay = (ImageButton)findViewById(R.id.imgPlay);
        imgNext = (ImageButton)findViewById(R.id.imgNext);
        imgPre = (ImageButton)findViewById(R.id.imgPre);
        imgInfo = (ImageButton) findViewById(R.id.imgInfo);
        tvName = (TextView)findViewById(R.id.tvName);
        sbTime = (SeekBar)findViewById(R.id.sbTime);
        tvTimeStart = (TextView)findViewById(R.id.tvTimeCurrent);
        tvTimeEnd = (TextView)findViewById(R.id.tvTimeEnd);
        tvSinger = (TextView)findViewById(R.id.tvSinger);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
    }

    private void setBack(){
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent retMain = new Intent(Player.this, MainActivity.class);
                startActivity(retMain);
            }
        });
    }

    private void playMedia(){
        firstLauch = false;
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
                imgPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
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
                imgPlay.setBackgroundResource(android.R.drawable.ic_media_play);
            }
        }
    };

//    private void playCircle(){
//        if(mediaPlayer.isPlaying()){
//            runnable = new Runnable() {
//                @Override
//                public void run() {
//                    playCircle();
//                }
//            };
//            handler.postDelayed(runnable, 100);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        //mediaPlayer.start();
        //playCircle();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mediaPlayer.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    public static String LoadDataFromURL(String theUrl) {
        StringBuilder content = new StringBuilder();

        try
        {
            // create a url object
            URL url = new URL(theUrl);

            Log.d("URL", String.valueOf(url));
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
