package com.dlsu.getbetter.getbetter;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NewPatientRecordActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_record);

//        if(savedInstanceState != null) {
//
//        }

        NewPatientFragment newPatientFragment = new NewPatientFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, newPatientFragment);
        fragmentTransaction.commit();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
//    }
}
