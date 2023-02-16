package synowiec.application.patient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.MyAdapter;
import synowiec.application.helpers.MyAppointmentsAdapter;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Appointment;
import synowiec.application.model.Physiotherapist;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;

public class PatientAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Button btnNext, btnPast;
    private MyAppointmentsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Appointment> appointmentsList = new ArrayList<>();
    private List<Appointment> appointmentsPastList = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);

        rv = findViewById(R.id.app_recyclerView);
        btnNext = findViewById(R.id.btn_next);
        btnPast = findViewById(R.id.btn_past);
        retrieveAndFillRecyclerView("RETRIEVE_PATIENT_APPOINTMENTS", Utils.currentPatient.getId());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRecyclerView(appointmentsList);
            }
        });
        btnPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRecyclerView(appointmentsPastList);
            }
        });

    }

    private void setupRecyclerView(List<Appointment> appointmentsList) {
        layoutManager = new LinearLayoutManager(this);
        adapter = new MyAppointmentsAdapter(appointmentsList);
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
                    Date date = null;
                    try {
                        date = sdf.parse(appointment.getDate());
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