package synowiec.application.Controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import synowiec.application.R;
import synowiec.application.Controller.Helpers.Utils;
import synowiec.application.Controller.PatientActivities.PatientLoginActivity;
import synowiec.application.Controller.PhysioActivities.PhysioLoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_patient, btn_physio;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        btn_patient = findViewById(R.id.btn_patient);
        btn_physio = findViewById(R.id.btn_physio);
        sessionManager = new SessionManager(this);



        btn_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(MainActivity.this, PatientLoginActivity.class);            }
        });

        btn_physio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(MainActivity.this, PhysioLoginActivity.class);
            }
        });

    }
}