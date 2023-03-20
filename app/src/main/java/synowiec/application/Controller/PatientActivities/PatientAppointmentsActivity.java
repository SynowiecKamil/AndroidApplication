package synowiec.application.Controller.PatientActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.helpers.MyAppointmentsAdapter;
import synowiec.application.Controller.helpers.Utils;
import synowiec.application.Model.Appointment;

import static synowiec.application.Controller.helpers.Utils.showInfoDialog;

public class PatientAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Button btnNext, btnPast, btnCancel;
    private MyAppointmentsAdapter adapter;
    private LinearLayout linearLayout;
    private LinearLayoutManager layoutManager;
    private List<Appointment> appointmentsList = new ArrayList<>();
    private List<Appointment> appointmentsPastList = new ArrayList<>();
    private int time;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy HH");
    private final Context c = PatientAppointmentsActivity.this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);
        getSupportActionBar().hide();

        rv = findViewById(R.id.app_recyclerView);
        linearLayout = findViewById(R.id.patient_appointments_linear_layout);
        btnNext = findViewById(R.id.btn_next);
        btnPast = findViewById(R.id.btn_past);
        btnCancel = findViewById(R.id.btn_cancel);
        retrieveAndFillRecyclerView("RETRIEVE_PATIENT_APPOINTMENTS", Utils.currentPatient.getId());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRecyclerView(appointmentsList);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.teal));
            }
        });
        btnPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRecyclerView(appointmentsPastList);
                linearLayout.setBackgroundColor(getResources().getColor(R.color.tealgreen));
            }
        });

    }

    private void setupRecyclerView(List<Appointment> appointmentsList) {
        layoutManager = new LinearLayoutManager(this);
        adapter = new MyAppointmentsAdapter(appointmentsList, "patient", c);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
    }

    private void retrieveAndFillRecyclerView(final String action, String patientId) {
      //  adapter.searchString = patientId;
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.getAppointment(action, patientId);

        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                    showInfoDialog(PatientAppointmentsActivity.this,"ERROR","Response or Response Body is null. \n Recheck PHP code.");
                    return;
                }
                Log.d("RETROFIT", "response : " + response.body().getAppointments().toString());
                List<Appointment> currentAppointments = new ArrayList<>();
                currentAppointments = response.body().getAppointments();
                for (Appointment appointment : currentAppointments) {
                    time = (Integer.parseInt(appointment.getTime()));
                    Date date = null;
                    try {
                        date = sdf.parse(appointment.getDate()+" "+Utils.convertTimeSlotToString(time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                   Date currentDate = new Date();
                    if(date.before(currentDate)){
                        appointmentsPastList.add(appointment);
                    }else if(date.after(currentDate)){
                        appointmentsList.add(appointment);
                    }
                }
               setupRecyclerView(appointmentsList);
            //    adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PatientAppointmentsActivity.this, "ERROR", t.getMessage());
            }
        });
    }
}