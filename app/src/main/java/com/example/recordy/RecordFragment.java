package com.example.recordy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.SimpleFormatter;


public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private ImageButton goToAudioListImg;
    private ImageButton startOrEndRecording;
    private Chronometer recordTimer;
    private TextView filenameTV;

    private boolean isRecording = false;

    MediaRecorder mediaRecorder;
    private String filename;

    public RecordFragment() {
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
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        goToAudioListImg = view.findViewById(R.id.go_to_record_list_btn);
        goToAudioListImg.setOnClickListener(this);

        startOrEndRecording = view.findViewById(R.id.start_recording_btn);
        startOrEndRecording.setOnClickListener(this);

        recordTimer = view.findViewById(R.id.record_timer);
        filenameTV = view.findViewById(R.id.file_name);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_to_record_list_btn:
                navController.navigate(R.id.action_recordFragment_to_listFragment);
                break;
            case R.id.start_recording_btn:
                if (isRecording){
                    //Stop recording
                    stopRecording();
                    startOrEndRecording.setImageResource(R.drawable.record_btn_stopped);
                    isRecording = false;
                }else {
                    //Start recording
                    if (checkPermission()){
                        startRecording();
                        startOrEndRecording.setImageResource(R.drawable.record_btn_recording);
                        isRecording = true;
                    }

                }
                break;
        }
    }


    private void startRecording() {
        recordTimer.setBase(SystemClock.elapsedRealtime());
        recordTimer.start();


        String recordPath = getActivity().getExternalFilesDir("/").getPath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss" , Locale.CANADA);
        Date now = new Date();
        filename = "Recordy_" + formatter.format(now) + ".3gp";

        filenameTV.setText("Recording started.. : "  + filename);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + filename);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private void stopRecording() {
        recordTimer.stop();
        filenameTV.setText("Recording stopped, file saved : " + filename);

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext() , Manifest.permission.RECORD_AUDIO ) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }else{
            ActivityCompat.requestPermissions(getActivity() , new String[]{Manifest.permission.RECORD_AUDIO},22 );
            return false;
        }
    }
}