package synowiec.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import synowiec.application.helpers.Utils;
import synowiec.application.physio.PhysioDashboardActivity;
import synowiec.application.patient.PatientDashboardActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_patient;
    private Button btn_physio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_patient = findViewById(R.id.btn_patient);
        btn_physio = findViewById(R.id.btn_physio);


        btn_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(MainActivity.this, PatientDashboardActivity.class);            }
        });

        btn_physio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(MainActivity.this, PhysioDashboardActivity.class);
            }
        });

    }
}