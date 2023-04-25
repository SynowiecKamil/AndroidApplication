package synowiec.application.Controller.PatientActivities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.Controller.MainActivity;
import synowiec.application.Controller.PhysioActivities.PhysioDashboardActivity;
import synowiec.application.Controller.PhysioActivities.PhysioLoginActivity;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Model.Patient;
import synowiec.application.Model.Physiotherapist;
import synowiec.application.R;
import synowiec.application.Controller.SessionManager;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.Helpers.Utils;

import static synowiec.application.Controller.Helpers.Utils.showInfoDialog;

public class PatientLoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btn_login, btn_back;
    private TextView link_regist;
    private ProgressBar loading;
    private List<Patient> currentPatientList = new ArrayList<>();
    private Context c;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);
        initializeWidgets();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loginUser(); }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientLoginActivity.this, MainActivity.class));
            }
        });

        link_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientLoginActivity.this, PatientRegisterActivity.class));
            }
        });
    }

    private void loginUser(){
        loading.setVisibility(View.VISIBLE);
        String sEmail, sPassword;
        sEmail = email.getText().toString().trim();
        sPassword = password.getText().toString().trim();

        if (!sEmail.isEmpty() || !sPassword.isEmpty()) {
            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> retrievedData;
            retrievedData = api.getPatientLogin("LOGIN", sEmail, sPassword);
            retrievedData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                        response) {
                    if (response == null || response.body() == null) {
                        showInfoDialog(PatientLoginActivity.this, "ERROR", "Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }
                    String myResponseCode = response.body().getCode();
                    System.out.println("Response code: "+myResponseCode+ " message: " + response.body().getMessage());
                    if (myResponseCode.equals("1")) {
                        currentPatientList = response.body().getPatients();
                        for (int i = 0; i < currentPatientList.size(); i++) {
                            Utils.currentPatient = currentPatientList.get(i);
                            sessionManager.createSession(currentPatientList.get(i).getId(), currentPatientList.get(i).getName(),
                                    currentPatientList.get(i).getEmail(), currentPatientList.get(i).getPhoto(),
                                    currentPatientList.get(i).getSurname());
                            Utils.sendPatientToActivity(c, currentPatientList.get(i), PatientDashboardActivity.class);
                            finish();
                            loading.setVisibility(View.GONE);
                        }
                    } else if (myResponseCode.equalsIgnoreCase("2")) {
                        Toast.makeText(c, "Bląd! Niepoprawne hasło.", Toast.LENGTH_SHORT).show();
                        password.setError("Niepoprawne hasło");
                        password.requestFocus();
                    }else if (myResponseCode.equalsIgnoreCase("3")) {
                        Log.d("RETROFIT", "ERROR: " + "NO MYSQL CONNECTION, PHP Code is unable to connect to mysql database.");
                    }else if (myResponseCode.equalsIgnoreCase("0")) {
                        Toast.makeText(c, "Bląd! Użytkownik o podanym adresie email nie istnieje w systemie.", Toast.LENGTH_SHORT).show();
                        email.setError("Niepoprawny email");
                        email.requestFocus();
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    showInfoDialog(PatientLoginActivity.this, "ERROR", t.getMessage());
                }
            });
        } else {
            Toast.makeText(c, "Wprowadź email lub hasło!", Toast.LENGTH_SHORT).show();
            email.setError("Proszę wprowadź email");
            password.setError("Proszę wprowadź hasło");
        }
    }

    private void initializeWidgets(){
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        c = PatientLoginActivity.this;
        loading = findViewById(R.id.loading);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        link_regist = findViewById(R.id.link_regist);
        btn_back = findViewById(R.id.btn_back);
    }
}

