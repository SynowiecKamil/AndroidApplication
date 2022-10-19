package synowiec.application.physio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.TreatmentDialog;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Treatment;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;


public class PhysioDashboardActivity extends AppCompatActivity implements TreatmentDialog.DialogListener{

    private TextView name, email, surname, cabinet, description, profession_number;
    private String id = null, getId;
    private Button btn_logout, btn_photo_upload, btn_delete_user, btn_add_treatment;
    private HashMap<String, String> user;
    SessionManager sessionManager;
    private ProgressBar mProgressBar;
    private Menu action;
    private Bitmap bitmap;
    private ListView treatmentLV;
    private ArrayList<String> treatmentNameList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    CircleImageView profile_image;
    private Context c = PhysioDashboardActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_dashboard);
        initializeWidgets();
        showData(user);
        retrieveTreatment("RETRIEVE_TREATMENT");

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sessionManager.logout("physio"); }
        });

        btn_photo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseFile(); }
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

        btn_add_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreatmentDialog treatmentDialog = new TreatmentDialog(getId);
                treatmentDialog.show(getSupportFragmentManager(),"treatment dialog");
            }
        });
    }
    @Override
    public void onBackPressed() { sessionManager.logout("physiotherapist"); }

    @Override
    protected void onResume() { super.onResume(); }

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
                profession_number.setFocusableInTouchMode(true);
                cabinet.setFocusableInTouchMode(true);
                description.setFocusableInTouchMode(true);
                btn_photo_upload.setVisibility(View.VISIBLE);

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

                name.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                surname.setFocusableInTouchMode(false);
                profession_number.setFocusableInTouchMode(false);
                cabinet.setFocusableInTouchMode(false);
                description.setFocusableInTouchMode(false);
                name.setFocusable(false);
                email.setFocusable(false);
                surname.setFocusable(false);
                profession_number.setFocusable(false);
                cabinet.setFocusable(false);
                description.setFocusable(false);
                btn_photo_upload.setVisibility(View.GONE);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    //RECEIVE DATA FROM SESSION MANAGER AND FILL
    private void showData(HashMap<String, String> user){
        if(user != null){
            name.setText(user.get(sessionManager.NAME));
            email.setText(user.get(sessionManager.EMAIL));
            surname.setText(user.get(sessionManager.SURNAME));
            profession_number.setText(user.get(sessionManager.PROFESSION_NUMBER));
            cabinet.setText(user.get(sessionManager.CABINET));
            description.setText(user.get(sessionManager.DESCRIPTION));
            Picasso.get().load(user.get(sessionManager.PHOTO))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);
            System.out.println("Znaleziono uzytkownika");
        }else{
            System.out.println("Brak uzytkownika");
        }
    }

    private void showTreatmentList(){
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, treatmentNameList);
        treatmentLV.setAdapter(adapter);
    }

    private void retrieveTreatment(final String action) {

        int queryID = Integer.parseInt(getId);
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.searchTreatment(action, queryID, "0", "10");
        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RETROFIT", "response : " + response.body().getTreatments());
                    List<Treatment> currentTreatment = response.body().getTreatments();
                    System.out.println(currentTreatment);
                    for (int i = 0; i < currentTreatment.size(); i++) {
                        treatmentNameList.add(currentTreatment.get(i).getName());
                    }
                    showTreatmentList();
                }else if (!response.isSuccessful()) {
                    showInfoDialog(PhysioDashboardActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                    " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+" " +
                                    " \n 3. Most probably the problem is with your PHP Code.");
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PhysioDashboardActivity.this, "ERROR", t.getMessage());
            }
        });
    }

    //SELECT * FROM DB
    private void receiveFromDatabase(){
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<String> login = api.getPhysioData("READ", getId);

        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                if(response == null || response.body() == null ){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
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

                            //   receivedPhysio = new Physiotherapist(id, name, email, photo);
                            sessionManager.createSession(id, name, email, photo, surname, profession_number, cabinet, description);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PhysioDashboardActivity.this, "Bląd!", Toast.LENGTH_SHORT).show();
                    }
                } else if (!response.isSuccessful()) {
                    showInfoDialog(PhysioDashboardActivity.this, "UNSUCCESSFUL",
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
                showInfoDialog(PhysioDashboardActivity.this, "FAILURE",
                        "FAILURE THROWN DURING INSERT."+
                                " ERROR Message: " + t.getMessage());
            }
        });
    }

    //UPDATE NAME, EMAIL IN DB
    private void updateData() {
        String sName, sEmail, sId, sSurname, sProfessionNumber, sCabinet, sDescription;
        sName = name.getText().toString();
        sEmail = email.getText().toString();
        sSurname = surname.getText().toString();
        sProfessionNumber = profession_number.getText().toString();
        sCabinet = cabinet.getText().toString();
        sDescription = description.getText().toString();
        sId = getId;

        showProgressBar(mProgressBar);
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> update = api.updatePhysio("UPDATE", sId, sName, sEmail, sSurname, sProfessionNumber, sCabinet, sDescription);
        update.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "Response: " + response.body().getResult());

                hideProgressBar(mProgressBar);
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    show(c, "Pomyślnie zaaktualizowano dane ");
                    System.out.println(response.body().getMessage());

                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    showInfoDialog(PhysioDashboardActivity.this, "UNSUCCESSFUL",
                            "Good Response From PHP,"+
                                    "WE ATTEMPTED UPDATING DATA BUT ENCOUNTERED ResponseCode: "+myResponseCode+
                                    " \n 3. Most probably the problem is with your PHP Code.");
                } else if (myResponseCode.equalsIgnoreCase("3")) {
                    showInfoDialog(PhysioDashboardActivity.this, "NO MYSQL CONNECTION",
                            " Your PHP Code"+
                                    " is unable to connect to mysql database. Make sure you have supplied correct"+
                                    " database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR THROWN DURING UPDATE: " + t.getMessage());
                hideProgressBar(mProgressBar);
                showInfoDialog(PhysioDashboardActivity.this, "FAILURE THROWN", "ERROR DURING UPDATE.Here"+
                        " is the Error: " + t.getMessage());
            }
        });
    }

    //CHOOSE FILE FOR PHOTO UPDATE
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //UPDATE PHOTO IN DB
    private void UploadPictureRetrofit(final String id, final String photo){
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> upl = api.uploadImagePhysio("UPLOAD_IMAGE", id, photo);

        upl.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
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
    // DELETE USER IN DB
    private void deleteUser() {
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> del = api.removePhysio("DELETE", getId);

        showProgressBar(mProgressBar);
        del.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }

                Log.d("RETROFIT", "DELETE RESPONSE: " + response.body());
                hideProgressBar(mProgressBar);
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    System.out.println(response.body().getMessage());
                    show(c, "Pomyślnie usunięto użytkownika");
                    sessionManager.logout("physio");
                    finish();
                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    showInfoDialog(PhysioDashboardActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL"+
                                    " \n 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+
                                    myResponseCode+ " \n 3. Most probably the problem is with your PHP Code.");
                }else if (myResponseCode.equalsIgnoreCase("3")) {
                    showInfoDialog(PhysioDashboardActivity.this, "NO MYSQL CONNECTION",
                            " Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                hideProgressBar(mProgressBar);
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PhysioDashboardActivity.this, "FAILURE THROWN", "ERROR during DELETE attempt. Message: " + t.getMessage());
            }
        });
    }

    public void initializeWidgets(){
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin("fizjoterapeuta");

        mProgressBar = findViewById(R.id.mProgressBarSave);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);

        name = findViewById(R.id.name);
        name.setFocusableInTouchMode(false);
        email = findViewById(R.id.email);
        email.setFocusableInTouchMode(false);
        surname = findViewById(R.id.surname);
        surname.setFocusableInTouchMode(false);
        profession_number = findViewById(R.id.profession_number);
        profession_number.setFocusableInTouchMode(false);
        cabinet = findViewById(R.id.cabinet);
        cabinet.setFocusableInTouchMode(false);
        description = findViewById(R.id.description);
        description.setFocusableInTouchMode(false);
        btn_logout = findViewById(R.id.btn_logout);

        btn_photo_upload = findViewById(R.id.btn_photo);
        btn_photo_upload.setVisibility(View.GONE);

        profile_image = findViewById(R.id.profile_image);
        btn_delete_user = findViewById(R.id.btn_delete_user);

        btn_add_treatment = findViewById(R.id.btn_add_treatment);
        treatmentLV = findViewById(R.id.treatmentLV);


        user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
    }


    @Override
    public void onCloseDialog() {
        treatmentNameList.clear();
        treatmentLV.setAdapter(null);
        retrieveTreatment("RETRIEVE_TREATMENT");
    }
}
