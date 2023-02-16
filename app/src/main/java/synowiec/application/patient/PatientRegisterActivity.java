package synowiec.application.patient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.MainActivity;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.physio.PhysioDashboardActivity;
import synowiec.application.physio.PhysioLoginActivity;

import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;

public class PatientRegisterActivity extends AppCompatActivity {

    private EditText nameET, emailET, passwordET, c_passwordET;
    private Button btn_regist, btn_login;
    private ProgressBar loading;
    private Context c = PatientRegisterActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_register);
        initializeWidgets();

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PatientRegisterActivity.this, PatientLoginActivity.class));
            }
        });
    }


    private void insertData() {
        String sName, sEmail, sPassword, sC_password;
        sName = nameET.getText().toString();
        sEmail = emailET.getText().toString();
        sPassword = passwordET.getText().toString();
        sC_password = c_passwordET.getText().toString();
        if (!sEmail.isEmpty() && !sPassword.isEmpty() && !sName.isEmpty() && sC_password.equals(sPassword)) {

            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> insertData = api.insertPatientData("INSERT", sName, sEmail, sPassword);

            //    Utils.showProgressBar(mProgressBar);

            insertData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {

                    if(response == null || response.body() == null || response.body().getCode()==null){
                        showInfoDialog(PatientRegisterActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }

                    Log.d("RETROFIT", "response : " + response.body().toString());
                    String myResponseCode = response.body().getCode();

                    if (myResponseCode.equals("1")) {
                        System.out.printf("SUCCESS: \n 1. Data inserted Successfully. \n 2. ResponseCode: "  +myResponseCode);
                        show(c, "Zarejestrowano pomyślnie!");
                        showInfoDialog(PatientRegisterActivity.this,"Gratulacje", "Pomyślnie zarejestrowano użytkownika");
                        nameET.getText().clear();
                        emailET.getText().clear();
                        passwordET.getText().clear();
                        c_passwordET.getText().clear();
                        getCurrentFocus().clearFocus();
                        btn_regist.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                        //       Utils.openActivity(c, ScientistsActivity.class);
                    } else if (myResponseCode.equalsIgnoreCase("2")) {
                        showInfoDialog(PatientRegisterActivity.this, "UNSUCCESSFUL",
                                "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                        " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+myResponseCode+
                                        " \n 3. Most probably the problem is with your PHP Code.");
                    }else if (myResponseCode.equalsIgnoreCase("3")) {
                        showInfoDialog(PatientRegisterActivity.this, "NO MYSQL CONNECTION","Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                    }
                    //       hideProgressBar(mProgressBar);
                }
                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    //     hideProgressBar(mProgressBar);
                    showInfoDialog(PatientRegisterActivity.this, "FAILURE",
                            "FAILURE THROWN DURING INSERT."+
                                    " ERROR Message: " + t.getMessage());
                }
            });
        }else if(sEmail.isEmpty() || sName.isEmpty() || sPassword.isEmpty() || sC_password.isEmpty()){
            showInfoDialog(PatientRegisterActivity.this, "Błąd rejestracji! ","Przynajmniej jedno wymagane pole jest puste! ");
        }else if(!sC_password.equals(sPassword)){
            showInfoDialog(PatientRegisterActivity.this, "Błąd rejestracji! ","Wpisane hasła nie są takie same! ");
        }
    }

    private void initializeWidgets(){
        loading = findViewById(R.id.loading);
        nameET = findViewById(R.id.name);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        c_passwordET = findViewById(R.id.c_password);
        btn_regist = findViewById(R.id.btn_regist);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setVisibility(View.GONE);
    }

}


