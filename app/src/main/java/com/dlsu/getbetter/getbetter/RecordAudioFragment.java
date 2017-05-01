package com.dlsu.getbetter.getbetter;


import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordAudioFragment extends DialogFragment implements View.OnClickListener {

    TextInputEditText audioTitle;
    MediaRecorder mediaRecorder;
    MediaPlayer mp;
    TextView recordStatus;
    TextView minutesView;
    TextView secondsView;
    Button stopRecordButton;
    Button recordButton;
    Button playButton;
    Button cancelButton;
    Button doneButton;

    int seconds, minutes, recordTime, playTime;
    boolean isRecording;
    Handler handler;


    public RecordAudioFragment() {
        // Required empty public constructor
    }

    public static RecordAudioFragment newInstance() {
        RecordAudioFragment frag = new RecordAudioFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_audio, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        audioTitle = (TextInputEditText) view.findViewById(R.id.audio_file_name);
        minutesView = (TextView) view.findViewById(R.id.record_minutes);
        secondsView = (TextView) view.findViewById(R.id.record_seconds);
        recordStatus = (TextView) view.findViewById(R.id.recording_status);
        recordButton = (Button) view.findViewById(R.id.audio_record_btn);
        stopRecordButton = (Button) view.findViewById(R.id.audio_stop_record_btn);
        playButton = (Button) view.findViewById(R.id.audio_play_recorded_btn);
        cancelButton = (Button) view.findViewById(R.id.record_audio_cancel_btn);
        doneButton = (Button) view.findViewById(R.id.record_audio_done_btn);
        getDialog().setTitle("Record Audio");

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        recordButton.setOnClickListener(this);
        stopRecordButton.setOnClickListener(this);
        playButton.setOnClickListener(this);

        stopRecordButton.setEnabled(false);
        playButton.setEnabled(false);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.audio_record_btn) {

            if(audioTitle.getText().toString().matches("")) {
                Log.d("audiorecord", "onClick: edit text empty" );
            } else {
                mediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" +
                        audioTitle.getText().toString() + getTimeStamp() + ".3gp");

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    recordStatus.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                isRecording = true;
                stopRecordButton.setEnabled(true);
                handler.post(UpdateRecordTime);
            }
        } else if (id == R.id.audio_stop_record_btn) {

        } else if (id == R.id.audio_play_recorded_btn) {

        } else if (id == R.id.record_audio_done_btn) {

        } else if (id == R.id.record_audio_cancel_btn) {

        }
    }

    Runnable UpdateRecordTime = new Runnable() {
        @Override
        public void run() {
            if(isRecording) {
                if(seconds < 10) {
                    secondsView.setText("0" + seconds);
                }
                else {
                    secondsView.setText(String.valueOf(seconds));
                }

                recordTime += 1;
                seconds += 1;

                if(seconds > 60) {
                    seconds = 0;
                    minutes += 1;
                    minutesView.setText("0" + minutes);
                }
                handler.postDelayed(this, 1000);
            }
        }
    };

    Runnable UpdatePlayTime = new Runnable() {
        @Override
        public void run() {
            if(mp.isPlaying()) {

                if(seconds < 10) {
                    secondsView.setText("0" + seconds);
                }
                else {
                    secondsView.setText(String.valueOf(seconds));
                }

                seconds += 1;

                if(seconds > 60) {
                    seconds = 0;
                    minutes += 1;
                    minutesView.setText("0" + minutes);
                }
                handler.postDelayed(this, 1000);

            }
        }
    };

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }
}
