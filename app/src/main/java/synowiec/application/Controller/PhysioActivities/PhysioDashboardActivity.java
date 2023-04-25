package synowiec.application.Controller.PhysioActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.Controller.SessionManager;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.Helpers.MyCabinetAdapter;
import synowiec.application.Controller.Helpers.MyTreatmentsAdapter;
import synowiec.application.Controller.Helpers.TreatmentDialog;
import synowiec.application.Controller.Helpers.Utils;
import synowiec.application.Model.Treatment;

import static synowiec.application.Controller.Helpers.Utils.hideProgressBar;
import static synowiec.application.Controller.Helpers.Utils.openActivity;
import static synowiec.application.Controller.Helpers.Utils.show;
import static synowiec.application.Controller.Helpers.Utils.showInfoDialog;
import static synowiec.application.Controller.Helpers.Utils.showProgressBar;


public class PhysioDashboardActivity extends AppCompatActivity implements TreatmentDialog.DialogListener, MyCabinetAdapter.ClickListener, AdapterView.OnItemSelectedListener {

    private EditText name, email, surname, cabinet, description, profession_number, cabinet_address;
    private TextView info;
    private CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7;
    private Spinner startHour, endHour;
    private String getId, selectedStartHour, selectedEndHour;
    private Button btn_logout, btn_photo_upload, btn_delete_user, btn_add_treatment, btn_delete_treatment, btn_edit_treatment, btn_my_appointments;
    private boolean editMode = false;
    private HashMap<String, String> user = null;
    SessionManager sessionManager;
    private ProgressBar mProgressBar;
    private Menu action;
    private Bitmap bitmap;
    private View previousView;
    private List<Treatment> currentTreatment;
    private Treatment selectedT;
    private List<CheckBox> checkBoxList;
    private MyTreatmentsAdapter treatmentsAdapter;
    private LinearLayoutManager layoutManager;
    CircleImageView profile_image;
    private RecyclerView cabinetRecyclerView, treatmentRecyclerView;
    private AutoCompleteTextView autocompleteCabinet;
    private Context c = PhysioDashboardActivity.this;
    private MyCabinetAdapter myCabinetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_physio_dashboard);
        initializeWidgets();
        if(user.containsValue(null) != true) {
            showData(user);
            retrieveTreatment("RETRIEVE_TREATMENT", getId);
            initializeCabinetAdapter();
        }
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout("physio");
                Utils.currentPhysio = null;
            }
        });

        btn_photo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseFile(); }
        });

        btn_my_appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(c, PhysioAppointmentsActivity.class);
            }
        });

        btn_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyStandardDialog(c, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                 //       .setTopColorRes(R.color.indigo)
                 //       .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Ostrzeżenie")
                        .setButtonsColor(Color.BLACK)
                        .setMessage("Czy na pewno chcesz usunąć konto? Zmiany będą nieodwracalne.")
                        .setPositiveButton("NIE", x -> {})
                        .setNegativeButton("TAK", x -> deleteUser())
                        .show();
            }
        });

        btn_add_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreatmentDialog treatmentDialog = new TreatmentDialog(getId, getApplicationContext(), "insert");
                treatmentDialog.show(getSupportFragmentManager(),"treatment dialog");
            }
        });

        btn_delete_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedT != null) {
                    new LovelyStandardDialog(c, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                            .setMessage("Czy na pewno chcesz usunąć zabieg?")
                            .setPositiveButtonColor(getResources().getColor(R.color.teal))
                            .setNegativeButtonColor(getResources().getColor(R.color.tealgreen))
                            .setPositiveButton("NIE", x -> {
                            })
                            .setNegativeButton("TAK", x -> {
                                deleteTreatment(selectedT);
                                currentTreatment.remove(selectedT);
                                treatmentsAdapter.notifyDataSetChanged();
                             //   previousView.setBackgroundResource(0);
                            })
                            .show();
                }else{
                    Toast.makeText(PhysioDashboardActivity.this, "Proszę wybrać zabieg do usunięcia",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_edit_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreatmentDialog treatmentDialog = new TreatmentDialog(getId, getApplicationContext(), selectedT, "edit");
                treatmentDialog.show(getSupportFragmentManager(),"treatment dialog");
            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        sessionManager.logout("physiotherapist");
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
                cabinet_address.setFocusableInTouchMode(true);
                btn_photo_upload.setVisibility(View.VISIBLE);
                btn_delete_user.setVisibility(View.VISIBLE);
                btn_logout.setVisibility(View.GONE);
                btn_my_appointments.setVisibility(View.GONE);
                btn_add_treatment.setVisibility(View.VISIBLE);
                btn_delete_treatment.setVisibility(View.VISIBLE);
                startHour.setEnabled(true);
                endHour.setEnabled(true);

                for (CheckBox checkBox : checkBoxList) {
                    checkBox.setClickable(true);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);

                return true;

            case R.id.menu_save:
                if(!getHours(selectedStartHour, selectedEndHour).equals("")) {
                    editMode = false;
                    updateData();
                    if (bitmap != null) uploadPictureRetrofit(getId, getStringImage(bitmap));

                    action.findItem(R.id.menu_edit).setVisible(true);
                    action.findItem(R.id.menu_save).setVisible(false);

                    name.setFocusableInTouchMode(false);
                    email.setFocusableInTouchMode(false);
                    surname.setFocusableInTouchMode(false);
                    profession_number.setFocusableInTouchMode(false);
                    cabinet.setFocusableInTouchMode(false);
                    description.setFocusableInTouchMode(false);
                    cabinet_address.setFocusableInTouchMode(false);
                    name.setFocusable(false);
                    email.setFocusable(false);
                    surname.setFocusable(false);
                    profession_number.setFocusable(false);
                    cabinet.setFocusable(false);
                    description.setFocusable(false);
                    cabinet_address.setFocusable(false);
                    btn_photo_upload.setVisibility(View.GONE);
                    btn_delete_user.setVisibility(View.GONE);
                    btn_logout.setVisibility(View.VISIBLE);
                    btn_my_appointments.setVisibility(View.VISIBLE);
                    btn_add_treatment.setVisibility(View.GONE);
                    btn_delete_treatment.setVisibility(View.GONE);
                    startHour.setEnabled(false);
                    endHour.setEnabled(false);

                    for (CheckBox checkBox : checkBoxList) {
                        checkBox.setClickable(false);
                    }
                    checkProfile();


                    return true;
                }else {
                    Toast.makeText(PhysioDashboardActivity.this, "Wybierz poprawne godziny przyjęć!", Toast.LENGTH_SHORT).show();
                    return false;
                }
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
            cabinet.setText(user.get(sessionManager.CITY));
            description.setText(user.get(sessionManager.DESCRIPTION));
            cabinet_address.setText(user.get(SessionManager.ADDRESS));
            Picasso.get().load(user.get(sessionManager.PHOTO))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);
            setDays(user.get(SessionManager.DAYS));
            setHours(user.get(SessionManager.HOURS));
            System.out.println("Znaleziono uzytkownika");
        }else{
            System.out.println("Brak uzytkownika");
        }
    }


    private void setupRecyclerView(List<Treatment> currentTreatment) {
        layoutManager = new LinearLayoutManager(this);

        treatmentsAdapter = new MyTreatmentsAdapter(currentTreatment, c, editMode, new MyTreatmentsAdapter.ItemClickListener() {
                @Override
                public void onItemClick (Treatment treatment,View view, int position){
                    if(editMode){
                        if (previousView != null) {
                            previousView.setBackgroundColor(getResources().getColor(R.color.colorTextBright));
                        }
                        if (view != previousView) {
                            selectedT = treatment;
                            view.setBackgroundColor(getResources().getColor(R.color.tealgreen));
                            previousView = view;
                            btn_edit_treatment.setVisibility(view.VISIBLE);
                            System.out.println(selectedT.getName().toString());
                        } else {
                            view.setBackgroundColor(getResources().getColor(R.color.colorTextBright));
                            btn_edit_treatment.setVisibility(view.GONE);
                            selectedT = null;
                            previousView = null;
                        }
                    }

            //    Utils.show(c, treatment.getId() + treatment.getName() + " clicked!");
            }
        });
        treatmentRecyclerView.setAdapter(treatmentsAdapter);
        treatmentRecyclerView.setLayoutManager(layoutManager);
    }

    private void retrieveTreatment(
            final String action, String userID) {

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
                    currentTreatment = response.body().getTreatments();
                    System.out.println(" 1 . currentTreatment: " + currentTreatment);
                    Log.d("2 . RETROFIT", "response : " + response.body().getTreatments());
                    setupRecyclerView(currentTreatment);
                    checkProfile();
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
        String sName, sEmail, sId, sSurname, sProfessionNumber, sCity, sDescription, sAddress, sDays, sHours;
        sName = name.getText().toString();
        sEmail = email.getText().toString();
        sSurname = surname.getText().toString();
        sProfessionNumber = profession_number.getText().toString();
        sCity = cabinet.getText().toString();
        sDescription = description.getText().toString();
        sAddress = cabinet_address.getText().toString();
        sDays = getDays();
        sHours = getHours(selectedStartHour, selectedEndHour);
        sId = getId;

        showProgressBar(mProgressBar);
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> update = api.updatePhysio("UPDATE", sId, sName, sEmail, sSurname, sProfessionNumber, sCity, sDescription, sAddress, sDays, sHours);
        update.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "Response: " + response.body().getPhysiotherapists());

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
    private void uploadPictureRetrofit(final String id, final String photo){
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

    private void deleteTreatment(Treatment treatment) {
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> del = api.deleteTreatment("DELETE_TREATMENT", treatment.getName(), getId);
        System.out.println("ID: " + getId + " treatment: " + treatment.getName());

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
        //autocompleteCabinet.setAdapter(myCabinetAdapter);
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

        info = findViewById(R.id.info_text_view);
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
        cabinet_address = findViewById(R.id.cabinet_address);
        cabinet_address.setFocusableInTouchMode(false);
        btn_logout = findViewById(R.id.btn_logout);
        btn_my_appointments = findViewById(R.id.btn_my_appointments);

        btn_photo_upload = findViewById(R.id.btn_photo);
        btn_photo_upload.setVisibility(View.GONE);

        profile_image = findViewById(R.id.profile_image);
        btn_delete_user = findViewById(R.id.btn_delete_user);

        btn_add_treatment = findViewById(R.id.btn_add_treatment);
        btn_add_treatment.setVisibility(View.GONE);
        btn_delete_treatment = findViewById(R.id.btn_delete_treatment);
        btn_delete_treatment.setVisibility(View.GONE);
        btn_edit_treatment = findViewById(R.id.btn_edit_treatment);
        btn_edit_treatment.setVisibility(View.GONE);
        treatmentRecyclerView = findViewById(R.id.treatment_recycler_view);

        cb1 = findViewById(R.id.pon_checkBox);
        cb2 = findViewById(R.id.wt_checkBox);
        cb3 = findViewById(R.id.sr_checkBox);
        cb4 = findViewById(R.id.czw_checkBox);
        cb5 = findViewById(R.id.pt_checkBox);
        cb6 = findViewById(R.id.sob_checkBox);
        cb7 = findViewById(R.id.ndz_checkBox);
        checkBoxList = Arrays.asList(cb1, cb2, cb3, cb4, cb5, cb6, cb7);

        startHour = findViewById(R.id.spinner_start_hour);
        startHour.setOnItemSelectedListener(this);
        endHour = findViewById(R.id.spinner_end_hour);
        endHour.setOnItemSelectedListener(this);
        startHour.setEnabled(false);
        endHour.setEnabled(false);

        user = sessionManager.getUserDetail("physio");
        getId = user.get(sessionManager.ID);

        cabinetRecyclerView = findViewById(R.id.cabinet_recyclerview);
        autocompleteCabinet = findViewById(R.id.autoComplete_cabinet);
        Places.initialize(PhysioDashboardActivity.this, getResources().getString(R.string.googlemap_key));
    }

    private String getDays(){
        StringBuilder daysSB = new StringBuilder(100);
        if(cb1.isChecked()) daysSB.append("0,");
        if(cb2.isChecked()) daysSB.append("1,");
        if(cb3.isChecked()) daysSB.append("2,");
        if(cb4.isChecked()) daysSB.append("3,");
        if(cb5.isChecked()) daysSB.append("4,");
        if(cb6.isChecked()) daysSB.append("5,");
        if(cb7.isChecked()) daysSB.append("6");
        return daysSB.toString();
    }

    private void setDays(String days){
        if(days.contains("0")) cb1.setChecked(true);
        if(days.contains("1")) cb2.setChecked(true);
        if(days.contains("2")) cb3.setChecked(true);
        if(days.contains("3")) cb4.setChecked(true);
        if(days.contains("4")) cb5.setChecked(true);
        if(days.contains("5")) cb6.setChecked(true);
        if(days.contains("6")) cb7.setChecked(true);
    }

    private String getHours(String startHour, String endHour){
        int startSlot = Utils.convertStringToTimeSlot(startHour);
        int endSlot = Utils.convertStringToTimeSlot(endHour);
        StringBuilder hoursSB = new StringBuilder(100);
        for(int i = startSlot; i < endSlot; i++){
            hoursSB.append(Integer.toString(i)+",");
        }
        System.out.println("get hours:" + hoursSB);
        return hoursSB.toString();
    }

    private void setHours(String hours){
        if(hours.equals("")) {
            startHour.setSelection(0);
            endHour.setSelection(7);
        }else{
            String st = hours.substring(0, hours.length() - 1);
            if (st.length() <= 2) {
                String firstSlot = st;
                startHour.setSelection(Integer.parseInt(firstSlot));
                endHour.setSelection(Integer.parseInt(firstSlot) + 1);
            } else {
                String firstSlot = st.substring(0, st.indexOf(","));
                String lastSlot = st.substring(st.lastIndexOf(',') + 1);
                System.out.println("hours: " + st + "first selection: " + firstSlot + " last selection: " + lastSlot);
                startHour.setSelection(Integer.parseInt(firstSlot));
                endHour.setSelection(Integer.parseInt(lastSlot) + 1);
            }
        }
    }

    private void checkProfile(){
        if(name.getText().toString().equals("") || surname.getText().toString().equals("") || email.getText().toString().equals("") ||
                profession_number.getText().toString().equals("") ||  cabinet.getText().toString().equals("") ||  cabinet_address.getText().toString().equals("")
         || (!cb1.isChecked() && !cb2.isChecked() && !cb3.isChecked() && !cb4.isChecked() && !cb5.isChecked() && !cb6.isChecked() && !cb7.isChecked()) || currentTreatment.isEmpty()){
            System.out.println("checkProfile, treatment list: " + currentTreatment.toString());
            info.setVisibility(View.VISIBLE);
        }else{
            info.setVisibility(View.GONE);
        }
    }


    @Override
    public void onCloseDialog() {
        System.out.println("OnCloseDialog");
        currentTreatment.clear();
        treatmentRecyclerView.setAdapter(null);
        if(user.containsValue(null) != true)
        retrieveTreatment("RETRIEVE_TREATMENT", getId);
        selectedT = null;
        btn_edit_treatment.setVisibility(View.GONE);
    }

    @Override
    public void click(Place place) {
        String city = String.valueOf(place.getName());
        cabinet.setText(city);
        cabinetRecyclerView.setVisibility(View.GONE);
    }

    //-----spinner listeners---//

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

        switch (parent.getId()) {
            case R.id.spinner_start_hour:
                selectedStartHour = parent.getSelectedItem().toString();
                System.out.println("startHour:" + selectedStartHour);
                break;
            case R.id.spinner_end_hour:
                selectedEndHour = parent.getSelectedItem().toString();
                System.out.println("getHours:" + getHours(selectedStartHour,selectedEndHour));

                break; 
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {    }

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
