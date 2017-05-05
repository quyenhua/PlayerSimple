package com.example.quyenhua.playersimple.RecentPlay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quyenhua.playersimple.R;

public class FragmentLyric extends Fragment {

    private TextView tvLyric;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_lyric, container, false);
        initialView(view);
//        String lyric = getArguments().getString("lyric");
//        tvLyric.setText(lyric);
        return view;
    }

    private void initialView(View view) {
        tvLyric = (TextView) view.findViewById(R.id.tvLyric);
    }

}
