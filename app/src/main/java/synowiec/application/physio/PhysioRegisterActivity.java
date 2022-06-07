package synowiec.application.physio;

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
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.patient.PatientRegisterActivity;

import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;

public class PhysioRegisterActivity extends AppCompatActivity {

    private EditText nameET, emailET, passwordET, c_passwordET;
    private Button btn_regist, btn_login;
    private ProgressBar loading;
    private Context c = PhysioRegisterActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_register);
        this.initializeWidgets();

        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {insertData();}
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhysioRegisterActivity.this, PhysioLoginActivity.class));
            }
        });

    }

//    private void Regist(){
//        loading.setVisibility(View.VISIBLE);
//        btn_regist.setVisibility(View.GONE);
//        name = findViewById(R.id.name);
//        email = findViewById(R.id.email);
//        password = findViewById(R.id.password);
//        c_password = findViewById(R.id.c_password);
//        final String sName = this.name.getText().toString().trim();
//        final String sEmail = this.email.getText().toString().trim();
//        final String sPassword = this.password.getText().toString().trim();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try{
//                            JSONObject jsonObject = new JSONObject(response);
//                            String success = jsonObject.getString("success");
//
//                            if (success.equals("1")) {
//                                Toast.makeText(PhysioRegisterActivity.this, "Zarejestrowano pomyślnie!", Toast.LENGTH_SHORT).show();
//                                loading.setVisibility(View.GONE);
//                                btn_login.setVisibility(View.VISIBLE);
//                                btn_regist.setVisibility(View.GONE);
//                            }else if(success.equals("0")){
//                                loading.setVisibility(View.GONE);
//                                btn_regist.setVisibility(View.VISIBLE);
//                                name.setError(null);
//                                password.setError(null);
//                                c_password.setError(null);
//                                email.getText().clear();
//                                email.setError("Wprowadz poprawny email");
//                                Toast.makeText(PhysioRegisterActivity.this, "Błąd rejestracji, istnieje użytkownik o tym adresie email ", Toast.LENGTH_SHORT).show();
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(PhysioRegisterActivity.this, "Błąd rejestracji! " + e.toString(), Toast.LENGTH_SHORT).show();
//                            loading.setVisibility(View.GONE);
//                            btn_regist.setVisibility(View.VISIBLE);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(PhysioRegisterActivity.this, "Błąd rejestracji! " + error.toString(), Toast.LENGTH_SHORT).show();
//                        loading.setVisibility(View.GONE);
//                        btn_regist.setVisibility(View.VISIBLE);
//                    }
//                })
//
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("name", sName);
//                params.put("email", sEmail);
//                params.put("password", sPassword);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//
//
//    }

    /**
     insert data typed in this page into the database
     */
    private void insertData() {
        String sName, sEmail, sPassword, sC_password;
        sName = nameET.getText().toString();
        sEmail = emailET.getText().toString();
        sPassword = passwordET.getText().toString();
        sC_password = c_passwordET.getText().toString();
        if (!sEmail.isEmpty() && !sPassword.isEmpty() && !sName.isEmpty() && sC_password.equals(sPassword)) {

            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> insertData = api.insertPhysioData("INSERT", sName, sEmail, sPassword);

            //    Utils.showProgressBar(mProgressBar);

            insertData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {

                    if(response == null || response.body() == null || response.body().getCode()==null){
                        showInfoDialog(PhysioRegisterActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }

                    Log.d("RETROFIT", "response : " + response.body().toString());
                    String myResponseCode = response.body().getCode();

                    if (myResponseCode.equals("1")) {
                        System.out.printf("SUCCESS: \n 1. Data inserted Successfully. \n 2. ResponseCode: "  +myResponseCode);
                        show(c, "Zarejestrowano pomyślnie!");
                        btn_regist.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                        //       Utils.openActivity(c, ScientistsActivity.class);
                    } else if (myResponseCode.equalsIgnoreCase("2")) {
                        showInfoDialog(PhysioRegisterActivity.this, "UNSUCCESSFUL",
                                "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                        " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+myResponseCode+
                                        " \n 3. Most probably the problem is with your PHP Code.");
                    }else if (myResponseCode.equalsIgnoreCase("3")) {
                        showInfoDialog(PhysioRegisterActivity.this, "NO MYSQL CONNECTION","Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                    }
                    //       hideProgressBar(mProgressBar);
                }
                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    //     hideProgressBar(mProgressBar);
                    showInfoDialog(PhysioRegisterActivity.this, "FAILURE",
                            "FAILURE THROWN DURING INSERT."+
                                    " ERROR Message: " + t.getMessage());
                }
            });
        }else if(sEmail.isEmpty() || sName.isEmpty() || sPassword.isEmpty() || sC_password.isEmpty()){
            showInfoDialog(PhysioRegisterActivity.this, "Błąd rejestracji! ","Przynajmniej jedno wymagane pole jest puste! ");
        }else if(!sC_password.equals(sPassword)){
            showInfoDialog(PhysioRegisterActivity.this, "Błąd rejestracji! ","Wpisane hasła nie są takie same! ");
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


