package synowiec.application.Controller.PhysioActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.helpers.Utils;
import synowiec.application.Model.Physiotherapist;

import static synowiec.application.Controller.helpers.Utils.showInfoDialog;

public class PhysioRegisterActivity extends AppCompatActivity {

    private EditText nameET, surnameET, emailET, passwordET, c_passwordET;
    private Button btnRegister, btnLogin;
    private List<Physiotherapist> physiotherapistList = null;
    private ProgressBar loading;
    private Context c = PhysioRegisterActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_register);
        this.initializeWidgets();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {insertData();}
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhysioRegisterActivity.this, PhysioLoginActivity.class));
            }
        });

    }

    /**
     insert data into database
     */
    private void insertData() {
        String sName, sSurname, sEmail, sPassword, sC_password;
        sName = nameET.getText().toString();
        sSurname = surnameET.getText().toString();
        sEmail = emailET.getText().toString();
        sPassword = passwordET.getText().toString();
        sC_password = c_passwordET.getText().toString();
        checkEmail(sEmail);
        if(sEmail.isEmpty() || sName.isEmpty() || sSurname.isEmpty() || sPassword.isEmpty() || sC_password.isEmpty()){
            showInfoDialog(PhysioRegisterActivity.this, "Błąd rejestracji! ","Przynajmniej jedno wymagane pole jest puste! ");
        }else if (physiotherapistList == null) {
            showInfoDialog(PhysioRegisterActivity.this, "Błąd rejestracji! ", "Podany email już istnieje! ");
            emailET.setError("Podany email już istnieje!");
        }
        else if(!sC_password.equals(sPassword)){
            showInfoDialog(PhysioRegisterActivity.this, "Błąd rejestracji! ","Wpisane hasła nie są takie same! ");
            passwordET.setError("Hasła nie są takie same!");
            c_passwordET.setError("Hasła nie są takie same!");
        }else if (!sEmail.isEmpty() && !sPassword.isEmpty() && !sName.isEmpty() && !sSurname.isEmpty() && sC_password.equals(sPassword)) {

            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> insertData = api.insertPhysioData("INSERT", sName, sSurname, sEmail, sPassword);

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
                        showInfoDialog(PhysioRegisterActivity.this,"Gratulacje", "Pomyślnie zarejestrowano użytkownika");
                        nameET.getText().clear();
                        emailET.getText().clear();
                        surnameET.getText().clear();
                        passwordET.getText().clear();
                        c_passwordET.getText().clear();
                       // getCurrentFocus().clearFocus();
                        btnRegister.setVisibility(View.GONE);
                        btnLogin.setVisibility(View.VISIBLE);

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
        }
    }

    private void checkEmail(String email){
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.checkPhysioEmail("CHECK_EMAIL", email);
        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if (response == null || response.body() == null) {
                    showInfoDialog(PhysioRegisterActivity.this, "ERROR", "Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    physiotherapistList = response.body().getResult();
                } else if (!response.isSuccessful()) {
                    physiotherapistList = response.body().getResult();
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PhysioRegisterActivity.this, "ERROR", t.getMessage());
            }
        });
    }

    private void initializeWidgets(){
        getSupportActionBar().hide();
        loading = findViewById(R.id.loading);
        nameET = findViewById(R.id.name);
        surnameET = findViewById(R.id.surname);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        c_passwordET = findViewById(R.id.c_password);
        btnRegister = findViewById(R.id.btn_regist);
        btnLogin = findViewById(R.id.btn_login);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}


