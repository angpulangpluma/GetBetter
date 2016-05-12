package com.dlsu.getbetter.getbetter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dlsu.getbetter.getbetter.adapters.CaseRecordAdapter;
import com.dlsu.getbetter.getbetter.adapters.CaseRecordUploadAdapter;
import com.dlsu.getbetter.getbetter.database.DataAdapter;
import com.dlsu.getbetter.getbetter.objects.CaseRecord;
import com.dlsu.getbetter.getbetter.sessionmanagers.NewPatientSessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: 04/05/2016 fix upload case record php script
// TODO: 05/05/2016 add case record attachments and history

public class UploadCaseRecordToServerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String UPLOAD_URL = "http://128.199.205.226/get_better/upload_case_record.php";
    private static final String CASE_RECORD_ID_KEY = "caseRecordId";
    private static final String USER_ID_KEY = "userId";
    private static final String HEALTH_CENTER_ID_KEY = "healthCenterId";
    private static final String COMPLAINT_KEY = "complaint";
    private static final String CONTROL_NUMBER_KEY = "controlNumber";

    private ArrayList<CaseRecord> caseRecordsUpload;
    private long userId;
    private String uploadStatus = "";

    DataAdapter getBetterDb;
    CaseRecordUploadAdapter caseRecordUploadAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_case_record_to_server);

        Bundle extras = getIntent().getExtras();
        caseRecordsUpload = new ArrayList<>();

        userId = extras.getLong("patientId");

        Button backBtn = (Button)findViewById(R.id.upload_caserecord_back_btn);
        Button uploadBtn = (Button)findViewById(R.id.upload_caserecord_upload_btn);
        ListView caseRecordList = (ListView)findViewById(R.id.upload_page_case_record_list);

        uploadBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        initializeDatabase();
        new PopulateCaseRecordListTask().execute();

        caseRecordUploadAdapter = new CaseRecordUploadAdapter(this, R.layout.case_record_item_checkbox, caseRecordsUpload);
        caseRecordList.setAdapter(caseRecordUploadAdapter);
    }

    private void initializeDatabase () {

        getBetterDb = new DataAdapter(this);

        try {
            getBetterDb.createDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getCaseRecordsUpload () {

        try {
            getBetterDb.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        caseRecordsUpload.addAll(getBetterDb.getCaseRecordsUpload(userId));
        getBetterDb.closeDatabase();

    }

    private void removeCaseRecordsUpload (int caseRecordId) {

        try {
            getBetterDb.openDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.removeCaseRecordUpload(caseRecordId);
        getBetterDb.closeDatabase();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.upload_caserecord_back_btn) {

            finish();
        } else if(id == R.id.upload_caserecord_upload_btn) {

            ArrayList<CaseRecord> selectedCaseRecordsList = new ArrayList<>();

            for (int i = 0; i < caseRecordsUpload.size(); i++) {

                CaseRecord selectedCaseRecord = caseRecordsUpload.get(i);

                if (selectedCaseRecord.isChecked()) {
                    selectedCaseRecordsList.add(selectedCaseRecord);
                }
            }

            UploadCaseRecordsTask uploadCaseRecordsTask = new UploadCaseRecordsTask();
            uploadCaseRecordsTask.execute(selectedCaseRecordsList);
            if (uploadStatus.equalsIgnoreCase("Successfully Uploaded")) {
                for(int i = 0; i < selectedCaseRecordsList.size(); i++) {
                    removeCaseRecordsUpload(selectedCaseRecordsList.get(i).getCaseRecordId());
                }
            }
            finish();
        }
    }

    private class PopulateCaseRecordListTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog = new ProgressDialog(UploadCaseRecordToServerActivity.this);

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Populating Case Record List");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            getCaseRecordsUpload();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
            if(progressDialog.isShowing()) {
                progressDialog.hide();
                progressDialog.dismiss();
            }

        }

    }

    private class UploadCaseRecordsTask extends AsyncTask<ArrayList<CaseRecord>, Void, String > {

        //ProgressDialog progressDialog = new ProgressDialog(UploadCaseRecordToServerActivity.this);
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog.setMessage("Uploading Case Record/s..");
//            progressDialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<CaseRecord>... params) {

            ArrayList<CaseRecord> caseRecordsUpload = params[0];
            String result = "";
            String user = String.valueOf(userId);

            for(int i = 0; i < caseRecordsUpload.size(); i++) {

                HashMap<String, String> data = new HashMap<>();
                data.put(CASE_RECORD_ID_KEY, String.valueOf(caseRecordsUpload.get(i).getCaseRecordId()));
                data.put(USER_ID_KEY, user);
                data.put(HEALTH_CENTER_ID_KEY, "51");
                data.put(COMPLAINT_KEY, caseRecordsUpload.get(i).getCaseRecordComplaint());
                data.put(CONTROL_NUMBER_KEY, caseRecordsUpload.get(i).getCaseRecordControlNumber());
                result = rh.sendPostRequest(UPLOAD_URL, data);
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            if(progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }

            uploadStatus = s;
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }

    }


}