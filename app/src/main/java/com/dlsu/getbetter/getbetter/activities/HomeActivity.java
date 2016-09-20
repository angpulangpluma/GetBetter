package com.dlsu.getbetter.getbetter.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dlsu.getbetter.getbetter.AddInstructionsCaseFragment;
import com.dlsu.getbetter.getbetter.ClosedCaseFragment;
import com.dlsu.getbetter.getbetter.DetailsFragment;
import com.dlsu.getbetter.getbetter.DiagnosedCaseFragment;
import com.dlsu.getbetter.getbetter.DownloadContentActivity;
import com.dlsu.getbetter.getbetter.R;
import com.dlsu.getbetter.getbetter.UrgentCaseFragment;
import com.dlsu.getbetter.getbetter.sessionmanagers.SystemSessionManager;

import java.util.HashMap;



public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        DiagnosedCaseFragment.OnCaseRecordSelected, UrgentCaseFragment.OnCaseRecordSelected,
        ClosedCaseFragment.OnCaseRecordSelected {

    private SystemSessionManager systemSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        systemSessionManager = new SystemSessionManager(this);
        if(systemSessionManager.checkLogin())
            finish();

        HashMap<String, String> user = systemSessionManager.getUserDetails();
        HashMap<String, String> hc = systemSessionManager.getHealthCenter();

        String userNameLabel = user.get(SystemSessionManager.LOGIN_USER_NAME);
        String currentHealthCenter = hc.get(SystemSessionManager.HEALTH_CENTER_NAME);

        Button viewCreatePatientBtn = (Button)findViewById(R.id.view_create_patient_records_btn);
        Button downloadAdditionalContentBtn = (Button)findViewById(R.id.download_content_btn);
        Button logoutBtn = (Button)findViewById(R.id.logout_btn);
        Button changeHealthCenterBtn = (Button)findViewById(R.id.change_health_center_btn);

        TextView healthCenter = (TextView)findViewById(R.id.home_current_health_center);

        initializeUpdatedTabs();

//        FragmentTabHost fragmentTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
//        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
//
//        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("urgent").setIndicator("Urgent Cases"),
//                UrgentCaseFragment.class, null);
//
//        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("additional instructions").setIndicator("Cases w/ Additional Instructions"),
//                AddInstructionsCaseFragment.class, null);
//
//        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("diagnosed").setIndicator("Diagnosed Cases"),
//                DiagnosedCaseFragment.class, null);
//
//        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("closed").setIndicator("Closed Cases"),
//                ClosedCaseFragment.class, null);



        if (healthCenter != null) {
            healthCenter.setText(currentHealthCenter);
        }

        TextView userLabel = (TextView)findViewById(R.id.user_label);
        if (userLabel != null) {
            userLabel.setText(userNameLabel);
        }

        if (viewCreatePatientBtn != null) {
            viewCreatePatientBtn.setOnClickListener(this);
        }

        if (downloadAdditionalContentBtn != null) {
            downloadAdditionalContentBtn.setOnClickListener(this);
        }

        if (changeHealthCenterBtn != null) {
            changeHealthCenterBtn.setOnClickListener(this);
        }

        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.view_create_patient_records_btn) {

//            systemSessionManager.setHealthCenter(healthCenterName, String.valueOf(healthCenterId));
            Intent intent = new Intent(this, ExistingPatientActivity.class);

            startActivity(intent);


        } else if (id == R.id.download_content_btn) {

            if(getInternetConnectivityStatus()) {

                //            systemSessionManager.setHealthCenter(healthCenterName, String.valueOf(healthCenterId));
                Intent intent = new Intent(this, DownloadContentActivity.class);
                startActivity(intent);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Internet Connection Status");
                builder.setMessage("Cannot download data without Internet Connection, Please connect to the Internet first.");

                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }



        } else if (id == R.id.logout_btn) {

            systemSessionManager.logoutUser();

        } else if (id == R.id.change_health_center_btn) {

            Intent intent = new Intent(this, HealthCenterActivity.class);

            startActivity(intent);

            finish();
        }
    }

    @Override
    public void onCaseRecordSelected(int caseRecordId) {

        if(findViewById(R.id.case_detail) != null) {
            Log.d("choice", caseRecordId + "");
            Bundle arguments = new Bundle();
            arguments.putInt("case record id", caseRecordId);
            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.case_detail, fragment).commit();
        }
    }

    private boolean getInternetConnectivityStatus() {

        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    private void initializeUpdatedTabs() {

        FragmentTabHost fragmentTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("urgent").setIndicator("Urgent Cases"),
                UrgentCaseFragment.class, null);

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("additional instructions").setIndicator("Cases w/ Additional Instructions"),
                AddInstructionsCaseFragment.class, null);

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("diagnosed").setIndicator("Diagnosed Cases"),
                DiagnosedCaseFragment.class, null);

        fragmentTabHost.addTab(fragmentTabHost.newTabSpec("closed").setIndicator("Closed Cases"),
                ClosedCaseFragment.class, null);

    }
}
