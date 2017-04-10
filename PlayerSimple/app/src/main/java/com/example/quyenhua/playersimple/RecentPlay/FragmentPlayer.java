package com.example.quyenhua.playersimple.RecentPlay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quyenhua.playersimple.R;

/**
 * Created by Quyen Hua on 4/3/2017.
 */

public class FragmentPlayer extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player, container, false);
        return view;
    }
}
