package com.dlsu.getbetter.getbetter;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.dlsu.getbetter.getbetter.adapters.SummaryPageDataAdapter;
import com.dlsu.getbetter.getbetter.database.DataAdapter;
import com.dlsu.getbetter.getbetter.objects.Attachment;
import com.dlsu.getbetter.getbetter.objects.CaseRecord;
import com.dlsu.getbetter.getbetter.objects.Patient;
import com.dlsu.getbetter.getbetter.sessionmanagers.SystemSessionManager;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ViewCaseRecordActivity extends AppCompatActivity implements MediaController.MediaPlayerControl, View.OnClickListener {

    private CaseRecord caseRecord;
    private Patient patientInfo;
    private ArrayList<Attachment> caseAttachments;

    SystemSessionManager systemSessionManager;
    DataAdapter getBetterDb;

    private MediaPlayer nMediaPlayer;
    private MediaController nMediaController;
    private Handler nHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_case_record);

        systemSessionManager = new SystemSessionManager(this);
        Bundle extras = getIntent().getExtras();
        int caseRecordId = extras.getInt("caseRecordId");
        long patientId = extras.getLong("patientId");

        HashMap<String, String> user = systemSessionManager.getUserDetails();
        HashMap<String, String> hc = systemSessionManager.getHealthCenter();
        int healthCenterId = Integer.parseInt(hc.get(SystemSessionManager.HEALTH_CENTER_ID));
        String midwifeName = user.get(SystemSessionManager.LOGIN_USER_NAME);

        TextView userLabel = (TextView)findViewById(R.id.user_label);
        TextView patientName = (TextView)findViewById(R.id.view_case_patient_name);
        TextView healthCenterName = (TextView)findViewById(R.id.view_case_health_center);
        TextView ageGender = (TextView)findViewById(R.id.view_case_age_gender);
        TextView chiefComplaint = (TextView)findViewById(R.id.view_case_chief_complaint);
        TextView controlNumber = (TextView)findViewById(R.id.view_case_control_number);
        RecyclerView attachmentList = (RecyclerView)findViewById(R.id.view_case_files_list);
        ImageView profilePic = (ImageView)findViewById(R.id.profile_picture_display);
        Button backBtn = (Button)findViewById(R.id.view_case_back_btn);
        Button updateCaseBtn = (Button)findViewById(R.id.update_case_record_btn);


        backBtn.setOnClickListener(this);
        updateCaseBtn.setOnClickListener(this);

        nMediaPlayer = new MediaPlayer();
        nMediaController = new MediaController(this) {
            @Override
            public void hide() {

            }
        };

        initializeDatabase();
        getCaseRecord(caseRecordId);
        getCaseAttachments(caseRecordId);
        getPatientInfo(patientId);

        SummaryPageDataAdapter fileAdapter = new SummaryPageDataAdapter(caseAttachments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        attachmentList.setHasFixedSize(true);
        attachmentList.setLayoutManager(layoutManager);
        attachmentList.setAdapter(fileAdapter);
        fileAdapter.SetOnItemClickListener(new SummaryPageDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ViewCaseRecordActivity.this, ViewImageActivity.class);
                intent.putExtra("imageUrl", caseAttachments.get(position).getAttachmentPath());
                intent.putExtra("imageTitle", caseAttachments.get(position).getAttachmentDescription());
                startActivity(intent);
            }
        });

        String recordedHpiOutputFile = getHpiOutputFile();
        String fullName = patientInfo.getFirstName() + " " + patientInfo.getMiddleName() + " " + patientInfo.getLastName();
        String gender = patientInfo.getGender();
        setPic(profilePic, patientInfo.getProfileImageBytes());

        int[] birthdateTemp = new int[3];
        String patientAgeGender = "";

        if(patientInfo.getBirthdate() != null) {

            StringTokenizer tok = new StringTokenizer(patientInfo.getBirthdate(), "-");
            int i = 0;
            while(tok.hasMoreTokens()) {

                birthdateTemp[i] = Integer.parseInt(tok.nextToken());
                i++;
            }

            LocalDate birthdate = new LocalDate(birthdateTemp[0], birthdateTemp[1], birthdateTemp[2]);
            LocalDate now = new LocalDate();

            Years age = Years.yearsBetween(birthdate, now);

            String patientAge = age.getYears() + "";
            patientAgeGender = patientAge + " yrs. old, " + gender;
        }

        ageGender.setText(patientAgeGender);

        chiefComplaint.setText(caseRecord.getCaseRecordComplaint());
        controlNumber.setText(caseRecord.getCaseRecordControlNumber());
        patientName.setText(fullName);
        userLabel.setText(midwifeName);
        healthCenterName.setText(getHealthCenterString(healthCenterId));

        nMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            nMediaPlayer.setDataSource(recordedHpiOutputFile);
            nMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nMediaController.setMediaPlayer(this);
        nMediaController.setAnchorView(findViewById(R.id.hpi_media_player));

        nMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                nHandler.post(new Runnable() {
                    public void run() {
                        nMediaController.show(0);
                        nMediaPlayer.start();
                    }
                });
            }
        });

    }

    public void initializeDatabase () {

        getBetterDb = new DataAdapter(this);

        try {
            getBetterDb.createDatabase();
        } catch(SQLException e ){
            e.printStackTrace();
        }
    }

    public void getCaseRecord(int caseRecordId) {

        try {
            getBetterDb.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        caseRecord = getBetterDb.getCaseRecord(caseRecordId);

        getBetterDb.closeDatabase();

    }

    public void getCaseAttachments(int caseRecordId) {

        try {
            getBetterDb.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        caseAttachments = new ArrayList<>();
        caseAttachments.addAll(getBetterDb.getCaseRecordAttachments(caseRecordId));

        getBetterDb.closeDatabase();

    }

    public void getPatientInfo(long patientId) {

        try {
            getBetterDb.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        patientInfo = getBetterDb.getPatient(patientId);

        getBetterDb.closeDatabase();
    }

    public String getHealthCenterString (int healthCenterId) {

        try {
            getBetterDb.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String healthCenterName = getBetterDb.getHealthCenterString(healthCenterId);

        getBetterDb.closeDatabase();

        return healthCenterName;

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.view_case_back_btn) {
            finish();

        } else if (id == R.id.update_case_record_btn) {

        }

    }

    private void setPic(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = 255;//mImageView.getWidth();
        int targetH = 200;// mImageView.getHeight();
        Log.e("width and height", targetW + targetH + "");

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    private String getHpiOutputFile() {

        String result = "";

        if(caseAttachments.isEmpty()) {
            Log.e("attachments is empty", "true");
        } else {
            for(int i = 0; i < caseAttachments.size(); i++) {

                if(caseAttachments.get(i).getAttachmentType() == 5) {
                    result = caseAttachments.get(i).getAttachmentPath();
                }
            }
        }
        return result;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {

        return (nMediaPlayer.getCurrentPosition() * 100) / nMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return nMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return nMediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return nMediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        if(nMediaPlayer.isPlaying())
            nMediaPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        nMediaPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        nMediaPlayer.start();
    }

}
