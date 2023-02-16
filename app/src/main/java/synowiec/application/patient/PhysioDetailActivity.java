package synowiec.application.patient;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Appointment;
import synowiec.application.model.PhysioTreatment;
import synowiec.application.model.Physiotherapist;
import synowiec.application.model.Treatment;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;

public class PhysioDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameET, emailET;
    private Button backBTN, appointmentBTN;
    private ListView treatmentLV;
    private ArrayAdapter<String> adapter;
    private Physiotherapist receivedPhysiotherapist;
    private ArrayList<String> treatmentNameList = new ArrayList<>();
    private List<Appointment> currentAppointments = new ArrayList<>();
    CircleImageView profile_image;
    LocalBroadcastManager localBroadcastManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_detail);

        initializeWidgets();
        receiveAndShowData();
        retrieveTreatment("RETRIEVE_TREATMENT", receivedPhysiotherapist.getId());
        retrieveAppointments("READ", receivedPhysiotherapist.getId());



        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(PhysioDetailActivity.this, PatientSearchActivity.class);
                finish();
            }
        });
        appointmentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PhysioDetailActivity.this, PatientReservationActivity.class);
                intent.putExtra("PHYSIOTHERAPIST_KEY", receivedPhysiotherapist);
                intent.putExtra("APPOINTMENT_KEY", (Serializable) currentAppointments);
                PhysioDetailActivity.this.startActivity(intent);

                finish();
            }
        });
    }

    private void receiveAndShowData(){
        receivedPhysiotherapist = Utils.receivePhysiotherapist(getIntent(),PhysioDetailActivity.this);
        if(receivedPhysiotherapist !=null){
            nameET.setText(receivedPhysiotherapist.getName());
            emailET.setText(receivedPhysiotherapist.getEmail());
            Picasso.get().load(receivedPhysiotherapist.getPhoto())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);

        }

//        receivedPhysioTreatment = Utils.receivePhysioTreatment(getIntent(),PhysioDetailActivity.this);
//        if(receivedPhysioTreatment !=null){
//
//
//        }
    }

    private void showTreatmentList(){
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, treatmentNameList);
        treatmentLV.setAdapter(adapter);
        treatmentLV.setFocusable(false);
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
                    showInfoDialog(PhysioDetailActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RETROFIT", "response : " + response.body().getTreatments());
                   List<Treatment> currentTreatment = response.body().getTreatments();
               for (int i = 0; i < currentTreatment.size(); i++) {
                   treatmentNameList.add(currentTreatment.get(i).getName());
                }
                  showTreatmentList();
                }else if (!response.isSuccessful()) {
                    showInfoDialog(PhysioDetailActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                    " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+" " +
                                    " \n 3. Most probably the problem is with your PHP Code.");
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PhysioDetailActivity.this, "ERROR", t.getMessage());
            }
        });
    }

    private void retrieveAppointments(final String action, String userID){
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.getAppointment(action, userID);
        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                    Log.d("ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RETROFIT", "response : " + response.body().getAppointments());
                    currentAppointments = response.body().getAppointments();
                    System.out.println("Retrieve appointments: "+currentAppointments);
                    for (int i = 0; i < currentAppointments.size(); i++) {
                    }
                }else if (!response.isSuccessful()) {
                    Log.d("UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                    " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+" " +
                                    " \n 3. Most probably the problem is with your PHP Code.");
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {
        Utils.openActivity(PhysioDetailActivity.this, PatientSearchActivity.class);
    }

//    private void loadTimeSlot(){
//
//        getIntent().putExtra("complexObject", receivedPhysiotherapist);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        ReservationCalendarFragment f1 = new ReservationCalendarFragment();
//        fragmentManager.beginTransaction()
//                .replace(R.id.frameLayout, f1).commit();
//    }

    private void initializeWidgets(){
        nameET = findViewById(R.id.name);
        nameET.setFocusableInTouchMode(false);
        emailET = findViewById(R.id.email);
        emailET.setFocusableInTouchMode(false);
        profile_image = findViewById(R.id.profile_image);
        backBTN = findViewById(R.id.btn_back);
        appointmentBTN = findViewById(R.id.btn_appointment);
        treatmentLV = findViewById(R.id.treatmentLV);
    }
}
