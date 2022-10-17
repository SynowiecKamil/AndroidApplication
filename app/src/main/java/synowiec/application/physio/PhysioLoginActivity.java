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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.MainActivity;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
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

//    private void Login(final String sEmail, final String sPassword) {
//
//        loading.setVisibility(View.VISIBLE);
//        btn_login.setVisibility(View.GONE);
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            String success = jsonObject.getString("success");
//                            JSONArray jsonArray = jsonObject.getJSONArray("login");
//
//                            if (success.equals("1")) {
//
//                                for (int i = 0; i < jsonArray.length(); i++) {
//
//                                    JSONObject object = jsonArray.getJSONObject(i);
//
//                                    String name = object.getString("name").trim();
//                                    String email = object.getString("email").trim();
//                                    String id = object.getString("id").trim();
//
//                                    sessionManager.createSession(name, email, id);
//
//                                    Intent intent = new Intent(PhysioLoginActivity.this, PhysioDashboardActivity.class);
//                                    intent.putExtra("name", name);
//                                    intent.putExtra("email", email);
//                                    startActivity(intent);
//                                    finish();
//
//                                    loading.setVisibility(View.GONE);
//
//
//                                }
//
//                            }else if(success.equals("0")){
//                                loading.setVisibility(View.GONE);
//                                btn_login.setVisibility(View.VISIBLE);
//                                Toast.makeText(PhysioLoginActivity.this, "Błąd logowania, błędne hasło ", Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            loading.setVisibility(View.GONE);
//                            btn_login.setVisibility(View.VISIBLE);
//                            Toast.makeText(PhysioLoginActivity.this, "Błąd logowania, błędny email " +e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        loading.setVisibility(View.GONE);
//                        btn_login.setVisibility(View.VISIBLE);
//                        Toast.makeText(PhysioLoginActivity.this, "Błąd logowania " +error.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("email", sEmail);
//                params.put("password", sPassword);
//                return params;
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//
//    }

    private void loginUser(){
        loading.setVisibility(View.VISIBLE);
        String sEmail, sPassword;
        sEmail = email.getText().toString().trim();
        sPassword = password.getText().toString().trim();

        if (!sEmail.isEmpty() || !sPassword.isEmpty()) {

            RestApi api = Utils.getClient().create(RestApi.class);
            Call<String> login = api.getPhysioLogin("LOGIN", sEmail, sPassword);

            //    Utils.showProgressBar(mProgressBar);

            login.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                    if(response == null || response.body() == null ){
                        showInfoDialog(PhysioLoginActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }

                    Log.d("RETROFIT", "response : " + response.body().toString());
                    String myResponse = response.body().toString();

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject object = jsonArray.getJSONObject(i);

                                String name = object.getString("name").trim();
                                String email = object.getString("email").trim();
                                String id = object.getString("id").trim();
                                String photo = object.getString("photo").trim();
                                String surname = object.getString("surname").trim();
                                String profession_number = object.getString("profession_number").trim();
                                String cabinet = object.getString("cabinet").trim();
                                String description = object.getString("description").trim();

                                sessionManager.createSession(id, name, email, photo, surname, profession_number, cabinet, description);
                                Intent intent = new Intent(PhysioLoginActivity.this, PhysioDashboardActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("photo", photo);
                                intent.putExtra("surname", surname);
                                intent.putExtra("profession_number", profession_number);
                                intent.putExtra("cabinet", cabinet);
                                intent.putExtra("description", description);
                                startActivity(intent);
                                finish();

                                loading.setVisibility(View.GONE);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(PhysioLoginActivity.this, "Bląd! Błędne dane logowania ", Toast.LENGTH_SHORT).show();
                        }
                    } else if (!response.isSuccessful()) {
                        showInfoDialog(PhysioLoginActivity.this, "UNSUCCESSFUL",
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
                    showInfoDialog(PhysioLoginActivity.this, "FAILURE",
                            "FAILURE THROWN DURING INSERT."+
                                    " ERROR Message: " + t.getMessage());
                }
            });
        } else {
            email.setError("Proszę wprowadź email");
            password.setError("Proszę wprowadź hasło");
        }

    }


    private void initializeWidgets(){
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

