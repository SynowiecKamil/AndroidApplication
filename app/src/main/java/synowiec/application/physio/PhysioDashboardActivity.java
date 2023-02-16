package synowiec.application.physio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.MyCabinetAdapter;
import synowiec.application.helpers.TreatmentDialog;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Treatment;
import synowiec.application.patient.PatientDashboardActivity;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;


public class PhysioDashboardActivity extends AppCompatActivity implements TreatmentDialog.DialogListener, MyCabinetAdapter.ClickListener{

    private TextView name, email, surname, cabinet, description, profession_number;
    private String id = null, getId, selectedTreatment;
    private Button btn_logout, btn_photo_upload, btn_delete_user, btn_add_treatment, btn_delete_treatment;
    private boolean editMode;
    private HashMap<String, String> user = null;
    SessionManager sessionManager;
    private ProgressBar mProgressBar;
    private Menu action;
    private Bitmap bitmap;
    private View previousView;
    private ListView treatmentLV;
    private ArrayList<String> treatmentNameList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    CircleImageView profile_image;
    private RecyclerView cabinetRecyclerView;
    private Context c = PhysioDashboardActivity.this;
    private MyCabinetAdapter myCabinetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_dashboard);
        initializeWidgets();
        if(user.containsValue(null) != true) {
            showData(user);
            retrieveTreatment("RETRIEVE_TREATMENT", getId);
            initializeCabinetAdapter();
        }

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

        btn_delete_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedTreatment != null) {
                    new LovelyStandardDialog(c, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                            .setMessage("Czy na pewno chcesz usunąć zabieg?")
                            .setPositiveButton("NIE", x -> {
                            })
                            .setNegativeButton("TAK", x -> {
                                deleteTreatment(selectedTreatment);
                                adapter.remove(selectedTreatment);
                                adapter.notifyDataSetChanged();
                                previousView.setBackgroundResource(0);
                                selectedTreatment = null;
                            })
                            .show();
                }else{
                    Toast.makeText(PhysioDashboardActivity.this, "Proszę wybrać zabieg do usunięcia",Toast.LENGTH_SHORT).show();
                }

            }
        });

        treatmentLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long arg3) {
                if(editMode){
                        if (previousView != null) {
                            previousView.setBackgroundResource(0);
                        }
                        if (view != previousView) {
                            selectedTreatment = (String) treatmentLV.getItemAtPosition(position);
                            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            previousView = view;
                            System.out.println(selectedTreatment);
                        } else {
                            view.setBackgroundResource(0);
                            selectedTreatment = null;
                            previousView = null;
                        }
                    }
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

                editMode=true;
                name.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);
                surname.setFocusableInTouchMode(true);
                profession_number.setFocusableInTouchMode(true);
                cabinet.setFocusableInTouchMode(true);
                description.setFocusableInTouchMode(true);
                btn_photo_upload.setVisibility(View.VISIBLE);
                btn_delete_user.setVisibility(View.VISIBLE);
                btn_logout.setVisibility(View.GONE);
                btn_add_treatment.setVisibility(View.VISIBLE);
                btn_delete_treatment.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);

                return true;

            case R.id.menu_save:

                editMode=false;
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
                btn_delete_user.setVisibility(View.GONE);
                btn_logout.setVisibility(View.VISIBLE);
                btn_add_treatment.setVisibility(View.GONE);
                btn_delete_treatment.setVisibility(View.GONE);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    //RECEIVE DATA FROM SESSION MANAGER AND FILL
    private void showData(HashMap<String, String> user){

        if(user.containsValue(null) != true){
            System.out.println(user);
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
        System.out.println("4. ShowTreatmentList");
    }

    private void retrieveTreatment(final String action, String userID) {

        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.searchTreatment(action, userID, "0", "10");
        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                   // showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Treatment> currentTreatment = response.body().getTreatments();
                    System.out.println(" 1 . currentTreatment: " + currentTreatment);
                    for (int i = 0; i < currentTreatment.size(); i++) {
                        treatmentNameList.add(currentTreatment.get(i).getName());
                    }
                    Log.d("2 . RETROFIT", "response : " + response.body().getTreatments());
                    System.out.println("3. treatmentNameList: " + treatmentNameList);
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
                Log.d("RETROFIT", "ERROR during retrieving Treatment: " + t.getMessage());
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

    private void deleteTreatment(String treatmentName) {
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> del = api.deleteTreatment("DELETE_TREATMENT", treatmentName, getId);
        System.out.println("ID: " + getId + " treatment: " + treatmentName);

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
                    show(c, "Pomyślnie usunięto zabieg");
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

    private void initializeCabinetAdapter(){
        cabinet.addTextChangedListener(myCabinetTextWatcher);
        myCabinetAdapter = new MyCabinetAdapter(PhysioDashboardActivity.this);
        cabinetRecyclerView.setLayoutManager(new LinearLayoutManager(PhysioDashboardActivity.this));
        myCabinetAdapter.setClickListener(this);
        cabinetRecyclerView.setAdapter(myCabinetAdapter);
        myCabinetAdapter.notifyDataSetChanged();
    }

    public TextWatcher myCabinetTextWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!editable.toString().equals("")){
                myCabinetAdapter.getFilter().filter(editable.toString());
                if(cabinetRecyclerView.getVisibility() == View.GONE){
                    cabinetRecyclerView.setVisibility(View.VISIBLE);
                }
            }else{
                if(cabinetRecyclerView.getVisibility() == View.VISIBLE){
                    cabinetRecyclerView.setVisibility(View.GONE);
                }
            }
        }
    };



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
        description.setVisibility(View.GONE);
        btn_logout = findViewById(R.id.btn_logout);

        btn_photo_upload = findViewById(R.id.btn_photo);
        btn_photo_upload.setVisibility(View.GONE);

        profile_image = findViewById(R.id.profile_image);
        btn_delete_user = findViewById(R.id.btn_delete_user);

        btn_add_treatment = findViewById(R.id.btn_add_treatment);
        btn_add_treatment.setVisibility(View.GONE);
        btn_delete_treatment = findViewById(R.id.btn_delete_treatment);
        btn_delete_treatment.setVisibility(View.GONE);
        treatmentLV = findViewById(R.id.treatmentLV);


        user = sessionManager.getUserDetail("physio");
        getId = user.get(sessionManager.ID);

        cabinetRecyclerView = findViewById(R.id.cabinet_recyclerview);
        Places.initialize(PhysioDashboardActivity.this, getResources().getString(R.string.googlemap_key));
    }


    @Override
    public void onCloseDialog() {
        treatmentNameList.clear();
        treatmentLV.setAdapter(null);
        if(user.containsValue(null) != true)
        retrieveTreatment("RETRIEVE_TREATMENT", getId);
    }

    @Override
    public void click(Place place) {
        String city = String.valueOf(place.getName());
        cabinet.setText(city);
        cabinetRecyclerView.setVisibility(View.GONE);
    }
}
