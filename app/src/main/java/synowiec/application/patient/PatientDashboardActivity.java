package synowiec.application.patient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.MainActivity;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.MyCabinetAdapter;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Patient;
import synowiec.application.model.Treatment;
import synowiec.application.physio.PhysioDashboardActivity;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.openActivity;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;

public class PatientDashboardActivity extends AppCompatActivity {

    private TextView name, email, surname;
    private String getId;
    private Button btn_logout, btn_photo_upload, btn_search_physio, btn_delete_user, btn_my_appointments;
    private HashMap<String, String> user;
    private ProgressBar mProgressBar;
    private List<Patient> currentPatient = new ArrayList<>();
    SessionManager sessionManager;
    private Menu action;
    private Bitmap bitmap = null;
    CircleImageView profile_image;
    private Context c = PatientDashboardActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        initializeWidgets();
        loadPatient("READ", getId);
        showData(user);


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.currentPatient = null;
                sessionManager.logout("patient");
            }
        });

        btn_photo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        btn_my_appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(c, PatientAppointmentsActivity.class);
            }
        });

        btn_search_physio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(surname.getText().toString().equals(""))
                    show(c, "Uzupełnij swoje dane by kontynuować!");
                else {
                    openActivity(PatientDashboardActivity.this, PatientSearchActivity.class);
                }
            }
        });

        btn_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyStandardDialog(c, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        //       .setTopColorRes(R.color.indigo)
                        //       .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Ostrzeżenie")
                        .setMessage("Czy na pewno chcesz usunąć konto? Zmiany będą nieodwracalne.")
                        .setPositiveButton("NIE", x -> {})
                        .setNegativeButton("TAK", x -> deleteUser())
                        .show();
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        sessionManager.logout("patient");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action, menu);

        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_edit:

                name.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);
                surname.setFocusableInTouchMode(true);
                btn_photo_upload.setVisibility(View.VISIBLE);
                btn_delete_user.setVisibility(View.VISIBLE);
                btn_my_appointments.setVisibility(View.GONE);
                btn_search_physio.setVisibility(View.GONE);
                btn_logout.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);

                return true;

            case R.id.menu_save:

                updateData();
                if(bitmap != null) UploadPictureRetrofit(getId, getStringImage(bitmap));

                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);
                btn_photo_upload.setVisibility(View.GONE);
                btn_delete_user.setVisibility(View.GONE);
                btn_my_appointments.setVisibility(View.VISIBLE);
                btn_search_physio.setVisibility(View.VISIBLE);
                btn_logout.setVisibility(View.VISIBLE);

                name.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                surname.setFocusableInTouchMode(false);
                name.setFocusable(false);
                email.setFocusable(false);
                surname.setFocusable(false);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    private void loadPatient(String action, String patientId) {
        if(user.containsValue(null) != true) {
            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> retrievedData;
            retrievedData = api.getPatientData(action, patientId);
            retrievedData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                        response) {
                    if (response == null || response.body() == null) {
                        showInfoDialog(PatientDashboardActivity.this, "ERROR", "Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }
                    if (response.isSuccessful() && response.body() != null) {
                        currentPatient = response.body().getPatients();
                        for (int i = 0; i < currentPatient.size(); i++) {
                            Utils.currentPatient = currentPatient.get(i);
                        }
                    } else if (!response.isSuccessful()) {
                        showInfoDialog(PatientDashboardActivity.this, "UNSUCCESSFUL",
                                "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE" +
                                        " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: " + " " +
                                        " \n 3. Most probably the problem is with your PHP Code.");
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    showInfoDialog(PatientDashboardActivity.this, "ERROR", t.getMessage());
                }
            });
        }
    }


    private void showData(HashMap<String, String> user){
        if(user.containsValue(null) != true){
            System.out.println(user);
            name.setText(user.get(sessionManager.NAME));
            email.setText(user.get(sessionManager.EMAIL));
            surname.setText(user.get(sessionManager.SURNAME));
            Picasso.get().load(user.get(sessionManager.PHOTO))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);
            System.out.println("Znaleziono uzytkownika");
        }else{
            System.out.println("Brak uzytkownika");
        }
    }

    private void showData2(Patient patient){
        if(user.containsValue(null) != true){
            System.out.println(patient.toString());
            name.setText(patient.getName());
            email.setText(patient.getEmail());
            surname.setText(patient.getSurname());
            Picasso.get().load(patient.getPhoto())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);
            System.out.println("Znaleziono uzytkownika");
        }else{
            System.out.println("Brak uzytkownika");
        }
    }

    private void updateData() {
        String sName, sEmail, sId, sSurname;
        sName = name.getText().toString();
        sEmail = email.getText().toString();
        sSurname = surname.getText().toString();
        sId = getId;


        showProgressBar(mProgressBar);
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> update = api.updatePatient("UPDATE", sId, sName, sEmail, sSurname);
        update.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PatientDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "Response: " + response.body().getResult());

                hideProgressBar(mProgressBar);
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    show(c, "Pomyślnie zaaktualizowano dane ");
                    System.out.println(response.body().getMessage());
                    //      receiveFromDatabase();

                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    showInfoDialog(PatientDashboardActivity.this, "UNSUCCESSFUL",
                            "Good Response From PHP,"+
                                    "WE ATTEMPTED UPDATING DATA BUT ENCOUNTERED ResponseCode: "+myResponseCode+
                                    " \n 3. Most probably the problem is with your PHP Code.");
                } else if (myResponseCode.equalsIgnoreCase("3")) {
                    showInfoDialog(PatientDashboardActivity.this, "NO MYSQL CONNECTION",
                            " Your PHP Code"+
                                    " is unable to connect to mysql database. Make sure you have supplied correct"+
                                    " database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR THROWN DURING UPDATE: " + t.getMessage());
                hideProgressBar(mProgressBar);
                showInfoDialog(PatientDashboardActivity.this, "FAILURE THROWN", "ERROR DURING UPDATE.Here"+
                        " is the Error: " + t.getMessage());
            }
        });
    }

    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz obraz"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);
                profile_image.clearFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadPictureRetrofit(final String id, final String photo){
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> upl = api.uploadImagePatient("UPLOAD_IMAGE", id, photo);

        upl.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PatientDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "UPLOAD IMAGE RESPONSE: " + response.body().toString());
                System.out.println(response.body().getMessage());
                show(c, "Pomyślnie zmieniono zdjęcie użytkownika");
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR THROWN DURING UPLOAD: " + t.getMessage());
                hideProgressBar(mProgressBar);
            }
        });
    }

    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }

    private void deleteUser() {
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> del = api.removePatient("DELETE", getId);

        showProgressBar(mProgressBar);
        del.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PatientDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }

                Log.d("RETROFIT", "DELETE RESPONSE: " + response.body());
                hideProgressBar(mProgressBar);
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    System.out.println(response.body().getMessage());
                    show(c, "Pomyślnie usunięto użytkownika");
                    sessionManager.logout("patient");
                    finish();
                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    showInfoDialog(PatientDashboardActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL"+
                                    " \n 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+
                                    myResponseCode+ " \n 3. Most probably the problem is with your PHP Code.");
                }else if (myResponseCode.equalsIgnoreCase("3")) {
                    showInfoDialog(PatientDashboardActivity.this, "NO MYSQL CONNECTION",
                            " Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                hideProgressBar(mProgressBar);
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PatientDashboardActivity.this, "FAILURE THROWN", "ERROR during DELETE attempt. Message: " + t.getMessage());
            }
        });
    }

    public void initializeWidgets(){
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin("patient");

        mProgressBar = findViewById(R.id.mProgressBarSave);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);

        name = findViewById(R.id.name);
        name.setFocusableInTouchMode(false);
        email = findViewById(R.id.email);
        email.setFocusableInTouchMode(false);
        surname = findViewById(R.id.surname);
        surname.setFocusableInTouchMode(false);

        btn_logout = findViewById(R.id.btn_logout);

        btn_photo_upload = findViewById(R.id.btn_photo);
        btn_photo_upload.setVisibility(View.GONE);

        profile_image = findViewById(R.id.profile_image);
        btn_my_appointments = findViewById(R.id.btn_my_appointments);
        btn_search_physio = findViewById(R.id.btn_search_physio);
        btn_delete_user = findViewById(R.id.btn_delete_user);

        user = sessionManager.getUserDetail("patient");
        getId = user.get(sessionManager.ID);
    }

}