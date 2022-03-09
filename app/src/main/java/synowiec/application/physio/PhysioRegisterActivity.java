package synowiec.application.physio;

import android.content.Intent;
import android.os.Bundle;
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

import synowiec.application.R;
import synowiec.application.patient.PatientRegisterActivity;

public class PhysioRegisterActivity extends AppCompatActivity {

    private EditText name , email , password , c_password;
    private Button btn_regist, btn_login;
    private ProgressBar loading;
    private static String URL_REGIST = "http://192.168.21.17/android_application/registerPhysio.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_register);

        loading = findViewById(R.id.loading);
        btn_regist = findViewById(R.id.btn_regist);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setVisibility(View.GONE);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        c_password = findViewById(R.id.c_password);
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = name.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                String mPass = password.getText().toString().trim();
                String mCPass = c_password.getText().toString().trim();
                if (!mEmail.isEmpty() && !mPass.isEmpty() && !mName.isEmpty() && mCPass.equals(mPass)) {
                    Regist();
                } else {
                    name.setError("Proszę wpisać Imię");
                    email.setError("Proszę wpisać email");
                    password.setError("Proszę wpisać hasło");
                    c_password.setError("Wpisane hasła nie są takie same");
                }

            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhysioRegisterActivity.this, PhysioLoginActivity.class));
            }
        });

    }

    private void Regist(){
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        c_password = findViewById(R.id.c_password);
        final String sName = this.name.getText().toString().trim();
        final String sEmail = this.email.getText().toString().trim();
        final String sPassword = this.password.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                Toast.makeText(PhysioRegisterActivity.this, "Zarejestrowano pomyślnie!", Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                btn_login.setVisibility(View.VISIBLE);
                                btn_regist.setVisibility(View.GONE);
                            }else if(success.equals("0")){
                                loading.setVisibility(View.GONE);
                                btn_regist.setVisibility(View.VISIBLE);
                                name.setError(null);
                                password.setError(null);
                                c_password.setError(null);
                                email.getText().clear();
                                email.setError("Wprowadz poprawny email");
                                Toast.makeText(PhysioRegisterActivity.this, "Błąd rejestracji, istnieje użytkownik o tym adresie email ", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PhysioRegisterActivity.this, "Błąd rejestracji! " + e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PhysioRegisterActivity.this, "Błąd rejestracji! " + error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", sName);
                params.put("email", sEmail);
                params.put("password", sPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

}


