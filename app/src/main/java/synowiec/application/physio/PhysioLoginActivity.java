package synowiec.application.physio;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.MainActivity;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Patient;
import synowiec.application.model.Physiotherapist;
import synowiec.application.patient.PatientDashboardActivity;
import synowiec.application.patient.PatientLoginActivity;
import synowiec.application.patient.PatientRegisterActivity;

import static synowiec.application.helpers.Utils.showInfoDialog;

public class PhysioLoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btn_login, btn_back;
    private TextView link_regist;
    private ProgressBar loading;
    private Context c;
    private List<Physiotherapist> currentPhysio = new ArrayList<>();
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_login);
        initializeWidgets();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {loginUser();}
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(c, MainActivity.class);
            }
        });

        link_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(c, PhysioRegisterActivity.class);
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
            retrievedData = api.getPhysioLogin("LOGIN", sEmail, sPassword);
            retrievedData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                        response) {
                    if (response == null || response.body() == null) {
                        showInfoDialog(PhysioLoginActivity.this, "ERROR", "Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        currentPhysio = response.body().getResult();
                        for (int i = 0; i < currentPhysio.size(); i++) {
                            Utils.currentPhysio = currentPhysio.get(i);
                                sessionManager.createSession(currentPhysio.get(i).getId(), currentPhysio.get(i).getName(), currentPhysio.get(i).getEmail(), currentPhysio.get(i).getPhoto(),
                                        currentPhysio.get(i).getSurname(), currentPhysio.get(i).getProfession_number(), currentPhysio.get(i).getCabinet(), currentPhysio.get(i).getDescription(),
                                        currentPhysio.get(i).getCabinet_address(), currentPhysio.get(i).getDays(), currentPhysio.get(i).getHours());
                                Intent intent = new Intent(PhysioLoginActivity.this, PhysioDashboardActivity.class);
                                intent.putExtra("name", currentPhysio.get(i).getName());
                                intent.putExtra("email", currentPhysio.get(i).getEmail());
                                intent.putExtra("id", currentPhysio.get(i).getId());
                                intent.putExtra("photo", currentPhysio.get(i).getPhoto());
                                intent.putExtra("surname", currentPhysio.get(i).getSurname());
                                intent.putExtra("profession_number", currentPhysio.get(i).getProfession_number());
                                intent.putExtra("cabinet", currentPhysio.get(i).getCabinet());
                                intent.putExtra("description", currentPhysio.get(i).getDescription());
                                intent.putExtra("cabinet_address", currentPhysio.get(i).getCabinet_address());
                                intent.putExtra("days", currentPhysio.get(i).getDays());
                                intent.putExtra("days", currentPhysio.get(i).getHours());
                                startActivity(intent);
                                finish();
                                loading.setVisibility(View.GONE);
                        }
                    } else if (!response.isSuccessful()) {
                        showInfoDialog(PhysioLoginActivity.this, "UNSUCCESSFUL",
                                "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE" +
                                        " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: " + " " +
                                        " \n 3. Most probably the problem is with your PHP Code.");
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    showInfoDialog(PhysioLoginActivity.this, "ERROR", t.getMessage());
                }
            });
        } else {
            email.setError("Proszę wprowadź email");
            password.setError("Proszę wprowadź hasło");
        }
    }


    private void initializeWidgets(){
        getSupportActionBar().hide();
        sessionManager = new SessionManager(this);
        c = PhysioLoginActivity.this;
        loading = findViewById(R.id.loading);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        btn_back = findViewById(R.id.btn_back);
        link_regist = findViewById(R.id.link_regist);
    }
}

