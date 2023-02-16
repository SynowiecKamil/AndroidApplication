package synowiec.application.patient;


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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.MainActivity;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Patient;

import static synowiec.application.helpers.Utils.showInfoDialog;

public class PatientLoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btn_login, btn_back;
    private TextView link_regist;
    private ProgressBar loading;
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
        sEmail = email.getText().toString();
        sPassword = password.getText().toString();

        if (!sEmail.isEmpty() || !sPassword.isEmpty()) {

            RestApi api = Utils.getClient().create(RestApi.class);
            Call<String> login = api.getPatientLogin("LOGIN", sEmail, sPassword);

            //    Utils.showProgressBar(mProgressBar);

            login.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                    if(response == null || response.body() == null ){
                        showInfoDialog(PatientLoginActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }

                 //   Log.d("RETROFIT", "response : " + response.body().toString());
                    String myResponse = response.body().toString();

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject object = jsonArray.getJSONObject(i);

                                String id = object.getString("id").trim();
                                String name = object.getString("name").trim();
                                String email = object.getString("email").trim();
                                String photo = object.getString("photo").trim();
                                String surname = object.getString("surname").trim();

                                sessionManager.createSession(id, name, email, photo, surname);
                                Intent intent = new Intent(PatientLoginActivity.this, PatientDashboardActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("photo", photo);
                                intent.putExtra("surname", surname);
                                startActivity(intent);
                                finish();

                                loading.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(PatientLoginActivity.this, "Bląd! Błędne dane logowania ", Toast.LENGTH_SHORT).show();
                        }
                    } else if (!response.isSuccessful()) {
                        showInfoDialog(PatientLoginActivity.this, "UNSUCCESSFUL",
                                "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                        " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+" " +
                                        " \n 3. Most probably the problem is with your PHP Code.");
                    }
                    //       hideProgressBar(mProgressBar);
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    //     hideProgressBar(mProgressBar);
                    showInfoDialog(PatientLoginActivity.this, "FAILURE",
                            "FAILURE THROWN DURING INSERT."+
                                    " ERROR Message: " + t.getMessage());
                }
            });
        } else {
            email.setError("Proszę wprowadzić email");
            password.setError("Proszę wprowadzić hasło");
            loading.setVisibility(View.GONE);
        }
    }

    private void initializeWidgets(){
        sessionManager = new SessionManager(this);
        loading = findViewById(R.id.loading);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        link_regist = findViewById(R.id.link_regist);
        btn_back = findViewById(R.id.btn_back);
    }
}

