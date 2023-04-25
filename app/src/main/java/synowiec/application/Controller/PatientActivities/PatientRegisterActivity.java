package synowiec.application.Controller.PatientActivities;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.Helpers.Utils;

import static synowiec.application.Controller.Helpers.Utils.showInfoDialog;

public class PatientRegisterActivity extends AppCompatActivity {

    private EditText nameET, surnameET, emailET, passwordET, c_passwordET;
    private Button btn_regist, btn_login;
    private boolean isEmail;
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
        String sName, sSurname, sEmail, sPassword, sC_password;
        sName = nameET.getText().toString();
        sSurname = surnameET.getText().toString();
        sEmail = emailET.getText().toString();
        sPassword = passwordET.getText().toString();
        sC_password = c_passwordET.getText().toString();
        checkEmail(sEmail);
        if (isEmail) {
            showInfoDialog(PatientRegisterActivity.this, "Błąd rejestracji! ", "Podany email już istnieje! ");
            emailET.setError("Podany email już istnieje!");
        } else if(sEmail.isEmpty() || sName.isEmpty() || sSurname.isEmpty() || sPassword.isEmpty() || sC_password.isEmpty()) {
                showInfoDialog(PatientRegisterActivity.this, "Błąd rejestracji! ", "Przynajmniej jedno wymagane pole jest puste! ");
        } else if (!sC_password.equals(sPassword)) {
                showInfoDialog(PatientRegisterActivity.this, "Błąd rejestracji! ", "Wpisane hasła nie są takie same! ");
                passwordET.setError("Hasła nie są takie same!");
                c_passwordET.setError("Hasła nie są takie same!");
        } else if (!sEmail.isEmpty() && !sPassword.isEmpty() && !sName.isEmpty() && !sSurname.isEmpty() && sC_password.equals(sPassword)) {

                RestApi api = Utils.getClient().create(RestApi.class);
                Call<ResponseModel> insertData = api.insertPatientData("INSERT", sName, sSurname, sEmail, sPassword);

                insertData.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                        if (response == null || response.body() == null || response.body().getCode() == null) {
                            Log.d( "ERROR", "Response or Response Body is null. \n Recheck Your PHP code.");
                            return;
                        }
                        String myResponseCode = response.body().getCode();
                        if (myResponseCode.equals("1")) {
                            System.out.printf("SUCCESS: \n 1. Data inserted Successfully. \n 2. ResponseCode: " + myResponseCode);
                            showInfoDialog(PatientRegisterActivity.this, "Gratulacje", "Pomyślnie zarejestrowano użytkownika");
                            nameET.getText().clear();
                            emailET.getText().clear();
                            surnameET.getText().clear();
                            passwordET.getText().clear();
                            c_passwordET.getText().clear();
                            btn_regist.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);

                        } else if (myResponseCode.equalsIgnoreCase("2")) {
                            System.out.printf( "UNSUCCESSFUL. However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. " +
                                            " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: " + myResponseCode +
                                            " \n 3. Most probably the problem is with PHP Code.");
                        } else if (myResponseCode.equalsIgnoreCase("3")) {
                            Log.d( "NO MYSQL CONNECTION", "Your PHP Code is unable to connect to mysql database. Make sure the database credentials are correct.");
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.d("RETROFIT", "FAILURE THROWN DURING INSERT. ERROR Message: " + t.getMessage());
                    }
                });
            }
        }

    public boolean checkEmail(String email){
            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> retrievedData;
            retrievedData = api.checkPatientEmail("CHECK_EMAIL", email);
            retrievedData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                        response) {
                    String myResponseCode = response.body().getCode();
                    System.out.println("check email response code" + myResponseCode);
                    if (myResponseCode.equals("0")) isEmail =false;
                    if(myResponseCode.equals("1")) isEmail=true;
                    System.out.println(isEmail);
                }
                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    showInfoDialog(PatientRegisterActivity.this, "ERROR", t.getMessage());
                }
            });
            return isEmail;
    }

    private void initializeWidgets(){
        getSupportActionBar().hide();
        loading = findViewById(R.id.loading);
        nameET = findViewById(R.id.name);
        surnameET = findViewById(R.id.surname);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        c_passwordET = findViewById(R.id.c_password);
        btn_regist = findViewById(R.id.btn_regist);
        btn_login = findViewById(R.id.btn_login);
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


