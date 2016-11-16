package com.dlsu.getbetter.getbetter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dlsu.getbetter.getbetter.R;
import com.dlsu.getbetter.getbetter.sessionmanagers.NewPatientSessionManager;
import com.dlsu.getbetter.getbetter.sessionmanagers.SystemSessionManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Handler;

public class RecordHpiActivity extends AppCompatActivity implements View.OnClickListener {

    private static RecordHpiActivity recordHpiActivity;

    private Button recordHpi;
    private Button stopRecord;
    private Button playRecord;
    private Button backButton;
    private Button nextButton;
    private TextView minutesView;
    private TextView secondsView;
    private TextView recordingStatus;
    private boolean isRecording;

    private String outputFile;
    private String chiefComplaintName = "";
    private int seconds, minutes, recordTime, playTime;
    private MediaRecorder hpiRecorder;
    private MediaPlayer mp;
    private NewPatientSessionManager newPatientSessionManager;

    private android.os.Handler handler;

    public RecordHpiActivity() {
        //empty constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_hpi);

        SystemSessionManager systemSessionManager = new SystemSessionManager(this);
        if(systemSessionManager.checkLogin())
            finish();

        recordHpiActivity = this;

        newPatientSessionManager = new NewPatientSessionManager(this);
        handler = new android.os.Handler();

        bindViews(this);
        bindListeners(this);
        initializeMediaRecorder();
    }

    private void bindViews (RecordHpiActivity activity) {

        activity.recordHpi = (Button)activity.findViewById(R.id.hpi_record_btn);
        activity.stopRecord = (Button)activity.findViewById(R.id.hpi_stop_record_btn);
        activity.playRecord = (Button)activity.findViewById(R.id.hpi_play_recorded_btn);
        activity.backButton = (Button)activity.findViewById(R.id.hpi_back_btn);
        activity.nextButton = (Button)activity.findViewById(R.id.hpi_next_btn);
        activity.minutesView = (TextView)activity.findViewById(R.id.record_minutes);
        activity.secondsView = (TextView)activity.findViewById(R.id.record_seconds);
        activity.recordingStatus = (TextView)activity.findViewById(R.id.recording_status);

    }

    private void bindListeners (RecordHpiActivity activity) {

        activity.recordHpi.setOnClickListener(activity);
        activity.stopRecord.setOnClickListener(activity);
        activity.playRecord.setOnClickListener(activity);
        activity.backButton.setOnClickListener(activity);
        activity.nextButton.setOnClickListener(activity);

    }

    private void initializeMediaRecorder() {

        stopRecord.setEnabled(false);
        playRecord.setEnabled(false);
        outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" +
                "hpi_recording_" + getTimeStamp() + ".3gp";

        hpiRecorder = new MediaRecorder();
        hpiRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        hpiRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        hpiRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        hpiRecorder.setOutputFile(outputFile);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.hpi_next_btn) {

            newPatientSessionManager.setHPIRecord(outputFile, chiefComplaintName);
            Intent intent = new Intent(this, SummaryActivity.class);
            startActivity(intent);

        } else if (id == R.id.hpi_back_btn) {

            finish();

        } else if (id == R.id.hpi_record_btn) {

            minutesView.setText(R.string.recording_progress_zero);
            secondsView.setText(R.string.recording_progress_zero);

            try {
                hpiRecorder.prepare();
                hpiRecorder.start();
                recordingStatus.setVisibility(View.VISIBLE);

            } catch (IllegalStateException | IOException e) {

                e.printStackTrace();

            }

            isRecording = true;
            stopRecord.setEnabled(true);
            handler.post(UpdateRecordTime);

        } else if (id == R.id.hpi_stop_record_btn) {

            hpiRecorder.stop();
            hpiRecorder.release();
            hpiRecorder = null;

            isRecording = false;
            recordingStatus.setVisibility(View.GONE);
            stopRecord.setEnabled(false);
            playRecord.setEnabled(true);

            editImageTitle();

        } else if (id == R.id.hpi_play_recorded_btn) {

            mp = new MediaPlayer();

            seconds = 0;
            minutes = 0;

            secondsView.setText(R.string.recording_progress_zero);
            minutesView.setText(R.string.recording_progress_zero);

            try {

                mp.setDataSource(outputFile);
            } catch (IOException e ) {

                e.printStackTrace();

            }

            try {
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mp.start();
            handler.post(UpdatePlayTime);

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

    private void editImageTitle () {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chief Complaint");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                chiefComplaintName = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    public static RecordHpiActivity getInstance() {
        return recordHpiActivity;
    }
}
