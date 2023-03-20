package synowiec.application.Controller.PatientActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.helpers.MyTreatmentsAdapter;
import synowiec.application.Controller.helpers.Utils;
import synowiec.application.Model.Appointment;
import synowiec.application.Model.Physiotherapist;
import synowiec.application.Model.Treatment;

import static synowiec.application.Controller.helpers.Utils.showInfoDialog;

public class PhysioDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView nameTextView , emailTextView , locationTextView ;
    private Button backBTN, appointmentBTN;
    private ListView treatmentLV;
    private ArrayAdapter<String> adapter;
    private Physiotherapist receivedPhysiotherapist;
    private ArrayList<String> treatmentNameList = new ArrayList<>();
    private List<Treatment> currentTreatment;
    private List<Appointment> currentAppointments = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private MyTreatmentsAdapter treatmentsAdapter;
    private RecyclerView treatmentRecyclerView;
    private Context c;
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
         //       Utils.openActivity(PhysioDetailActivity.this, PatientSearchActivity.class);
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
            nameTextView.setText(receivedPhysiotherapist.getName() +" "+ receivedPhysiotherapist.getSurname());
            emailTextView.setText("Email: " + receivedPhysiotherapist.getEmail());
            locationTextView.setText("Adres gabinetu: " + receivedPhysiotherapist.getCity() + ", " + receivedPhysiotherapist.getAddress());
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

    private void setupRecyclerView(List<Treatment> currentTreatment) {
        layoutManager = new LinearLayoutManager(this);

        treatmentsAdapter = new MyTreatmentsAdapter(currentTreatment, c, false, new MyTreatmentsAdapter.ItemClickListener() {
            @Override
            public void onItemClick (Treatment treatment,View view, int position){
            }
        });
        treatmentRecyclerView.setAdapter(treatmentsAdapter);
        treatmentRecyclerView.setLayoutManager(layoutManager);
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
                   currentTreatment = response.body().getTreatments();
               for (int i = 0; i < currentTreatment.size(); i++) {
                   treatmentNameList.add(currentTreatment.get(i).getName());
                }
                  showTreatmentList();
               setupRecyclerView(currentTreatment);
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
        getSupportActionBar().hide();
        nameTextView  = findViewById(R.id.name);
        nameTextView .setFocusableInTouchMode(false);
        emailTextView  = findViewById(R.id.email);
        emailTextView .setFocusableInTouchMode(false);
        locationTextView = findViewById(R.id.location);
        profile_image = findViewById(R.id.profile_image);
        backBTN = findViewById(R.id.btn_back);
        appointmentBTN = findViewById(R.id.btn_appointment);
        treatmentLV = findViewById(R.id.treatmentLV);
        treatmentRecyclerView = findViewById(R.id.treatment_recycler_view);
    }
}
