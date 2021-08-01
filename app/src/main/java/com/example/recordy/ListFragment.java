package com.example.recordy;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

public class ListFragment extends Fragment implements RecyclerViewAdapter.OnClickListener, View.OnClickListener {

    private RecyclerView allAudiosList;
    private ConstraintLayout constraintLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private File [] allAudios;

    private RecyclerViewAdapter adapter;

    private boolean isPlaying = false;
    private MediaPlayer mediaPlayer = null;

    int voiceMaxVal;
    int voiceCurrentVal;

    private File fileToPlay;

    //UI elements
    private ImageButton pauseAndPlay;
    private ImageButton playBack;
    private ImageButton playForward;
    private TextView fileNameTV;
    private TextView playingStatus;
    private SeekBar seekBar;
    private Chronometer AudioMaxValue;

    private Handler seekBarHandler;
    private Runnable seekBarRunnable;
    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pauseAndPlay = view.findViewById(R.id.player_pause);
        playBack = view.findViewById(R.id.player_back);
        playForward = view.findViewById(R.id.player_forward);
        fileNameTV = view.findViewById(R.id.file_name);
        playingStatus = view.findViewById(R.id.bottom_sheet_header_state);
        seekBar = view.findViewById(R.id.media_seekBar);
        AudioMaxValue = view.findViewById(R.id.audio_maxVal);

        String path = getActivity().getExternalFilesDir("/").getPath();
        File dir = new File(path);
        allAudios = dir.listFiles();

        constraintLayout = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged( View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            @Override
            public void onSlide( View bottomSheet, float slideOffset) {

            }

        });
        adapter = new RecyclerViewAdapter(allAudios , this);

        allAudiosList = view.findViewById(R.id.audio_list_view);
        allAudiosList.setHasFixedSize(true);
        allAudiosList.setLayoutManager(new LinearLayoutManager(getContext()));
        allAudiosList.setAdapter(adapter);


        pauseAndPlay.setOnClickListener(this);

    }

    @Override
    public void onItemClickListener(File itemFile, int itemPosition) {
        fileToPlay = itemFile;
        if (isPlaying){
            // file is playing
            stopPlayingAudioFile();
            isPlaying = false;
            playAudioFile(fileToPlay);
        }else{
            // file is not playing
            playAudioFile(fileToPlay);
        }
    }

    private void stopPlayingAudioFile() {
        mediaPlayer.stop();
        mediaPlayer.release();
        pauseAndPlay.setImageResource(R.drawable.player_play_btn);
        playingStatus.setText("Stopped");
    }

    private void playAudioFile(File fileToPlay) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileNameTV.setText(fileToPlay.getName());
        playingStatus.setText("playing");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pauseAndPlay.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn , null));
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                playingStatus.setText("Completed");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pauseAndPlay.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn , null));
                }
            }
        });

        voiceMaxVal = mediaPlayer.getDuration();
        seekBar.setMax(voiceMaxVal);

        AudioMaxValue.setText(String.valueOf(voiceMaxVal/1000));
        seekBarHandler = new Handler();
        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this , 500);
            }
        };
        seekBarHandler.postDelayed(seekBarRunnable , 0);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        isPlaying = true;
    }

    int currentPosition;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.player_pause:
                if (isPlaying){
                    mediaPlayer.pause();
                    currentPosition = mediaPlayer.getCurrentPosition();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        pauseAndPlay.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn , null));
                    }
                    playingStatus.setText("paused");
                    isPlaying = false;
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        pauseAndPlay.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn , null));
                    }
                    mediaPlayer.seekTo(currentPosition);
                    mediaPlayer.start();
                    playingStatus.setText("playing");
                    isPlaying = true;
                }
        }
    }
}