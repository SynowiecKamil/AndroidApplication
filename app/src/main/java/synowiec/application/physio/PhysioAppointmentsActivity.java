package synowiec.application.physio;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.MyAppointmentsAdapter;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Appointment;

import static synowiec.application.helpers.Utils.showInfoDialog;

public class PhysioAppointmentsActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Button btnNext, btnPast;
    private MyAppointmentsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private LinearLayout linearLayout;
    private List<Appointment> appointmentsList = new ArrayList<>();
    private List<Appointment> appointmentsPastList = new ArrayList<>();
    private int time;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy HH");
    private Context c = PhysioAppointmentsActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_appointments);
        getSupportActionBar().hide();

        rv = findViewById(R.id.app_recyclerView);
        linearLayout = findViewById(R.id.physio_appointments_linear_layout);
        btnNext = findViewById(R.id.btn_next);
        btnPast = findViewById(R.id.btn_past);
        retrieveAndFillRecyclerView("RETRIEVE_PHYSIO_APPOINTMENTS", Utils.currentPhysio.getId());

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
        adapter = new MyAppointmentsAdapter(appointmentsList, "physio", c);
        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager);
    }

    private void retrieveAndFillRecyclerView(final String action, String physioId) {
      //  adapter.searchString = patientId;
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.getAppointment(action, physioId);

        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                    showInfoDialog(PhysioAppointmentsActivity.this,"ERROR","Response or Response Body is null. \n Recheck PHP code.");
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
                showInfoDialog(PhysioAppointmentsActivity.this, "ERROR", t.getMessage());
            }
        });
    }
}