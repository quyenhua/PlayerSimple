package com.example.quyenhua.playersimple.RecentPlay;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.quyenhua.playersimple.Interface.ComunicationPlayer;
import com.example.quyenhua.playersimple.R;

public class FragmentSongList extends Fragment {

    private ComunicationPlayer comunicationPlayer;
    private Button button;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        initializeView(view);

        Bundle bundle = getArguments();
        String nhan = bundle.getString("dulieu");
        textView.setText(nhan);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                comunicationPlayer.changeText("Tôi vừa đổi text");
            }
        });
        return view;
    }

    private void initializeView(View view){
        button = (Button) view.findViewById(R.id.button);
        textView = (TextView) view.findViewById(R.id.textView2);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comunicationPlayer = (ComunicationPlayer) getActivity();
    }
}
