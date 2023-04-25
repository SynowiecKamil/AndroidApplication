package synowiec.application.Controller.PhysioActivities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.Controller.MainActivity;
import synowiec.application.R;
import synowiec.application.Controller.SessionManager;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.Helpers.Utils;
import synowiec.application.Model.Physiotherapist;

import static synowiec.application.Controller.Helpers.Utils.showInfoDialog;

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
            public void onClick(View v) {
                loginUser();
            }
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
                    String myResponseCode = response.body().getCode();
                    if (myResponseCode.equals("1")) {
                        currentPhysio = response.body().getPhysiotherapists();
                        for (int i = 0; i < currentPhysio.size(); i++) {
                            Utils.currentPhysio = currentPhysio.get(i);
                            sessionManager.createSession(currentPhysio.get(i).getId(), currentPhysio.get(i).getName(), currentPhysio.get(i).getEmail(), currentPhysio.get(i).getPhoto(),
                                        currentPhysio.get(i).getSurname(), currentPhysio.get(i).getProfessionNumber(), currentPhysio.get(i).getCity(), currentPhysio.get(i).getDescription(),
                                        currentPhysio.get(i).getAddress(), currentPhysio.get(i).getDays(), currentPhysio.get(i).getHours());
                            Utils.openActivity(c, PhysioDashboardActivity.class);
                            finish();
                            loading.setVisibility(View.GONE);
                        }
                    } else if (myResponseCode.equalsIgnoreCase("2")) {
                        Toast.makeText(PhysioLoginActivity.this, "Bląd! Niepoprawne hasło.", Toast.LENGTH_SHORT).show();
                        password.setError("Niepoprawne hasło");
                        password.requestFocus();
                        loading.setVisibility(View.GONE);
                    }else if (myResponseCode.equalsIgnoreCase("3")) {
                        Log.d("RETROFIT", "ERROR: " + "NO MYSQL CONNECTION, PHP Code is unable to connect to mysql database.");
                    }else if (myResponseCode.equalsIgnoreCase("0")) {
                        Toast.makeText(PhysioLoginActivity.this, "Bląd! Użytkownik o podanym adresie email nie istnieje w systemie.", Toast.LENGTH_SHORT).show();
                        email.setError("Niepoprawny email");
                        password.setError("Niepoprawne hasło");
                        email.requestFocus();
                        loading.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    showInfoDialog(PhysioLoginActivity.this, "ERROR", t.getMessage());
                }
            });
        } else {
            Toast.makeText(PhysioLoginActivity.this, "Wprowadź email lub hasło!", Toast.LENGTH_SHORT).show();
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

