package synowiec.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import synowiec.application.fizjoterapeuta.FizjoterapeutaDashboardActivity;
import synowiec.application.pacjent.PacjentDashboardActivity;

public class MainActivity extends AppCompatActivity {

    private Button btn_pacjent;
    private Button btn_fizjo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_pacjent = findViewById(R.id.btn_pacjent);
        btn_fizjo = findViewById(R.id.btn_fizjo);


        btn_pacjent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PacjentDashboardActivity.class));
            }
        });

        btn_fizjo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FizjoterapeutaDashboardActivity.class));
            }
        });

    }
}