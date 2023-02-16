package synowiec.application.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Appointment;
import synowiec.application.model.Physiotherapist;
import synowiec.application.patient.PatientDashboardActivity;
import synowiec.application.patient.PatientSearchActivity;
import synowiec.application.physio.PhysioLoginActivity;

import static synowiec.application.helpers.Utils.openActivity;
import static synowiec.application.helpers.Utils.show;

public class ReservationConfirmFragment extends Fragment {

    LocalBroadcastManager localBroadcastManager;
    private SimpleDateFormat simpleDateFormat, displayDateFormat;
    Unbinder unbinder;

    @BindView(R.id.txt_patient)
    TextView txt_patient;
    @BindView(R.id.txt_physio)
    TextView txt_physio;
    @BindView(R.id.txt_treatment)
    TextView txt_treatment;
    @BindView(R.id.txt_date)
    TextView txt_date;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_place)
    TextView txt_place;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    @OnClick(R.id.btn_confirm)
    public void conifirmAppointment(){
        Appointment appointment = new Appointment();

        appointment.setPatient_id(Utils.currentPatient.getId());
        appointment.setPhysio_id(Utils.currentPhysio.getId());
        appointment.setTime(Integer.toString(Utils.currentTimeSlot));
        appointment.setDate(simpleDateFormat.format(Utils.currentDate.getTime()));
        appointment.setPlace(Utils.currentPhysio.getCabinet());
        appointment.setTreatment(Utils.currentTreatment);

        // send data to DB
        insertAppointment(appointment.getPhysio_id(), appointment.getPatient_id(), appointment.getDate(), appointment.getTime(), appointment.getPlace(), appointment.getTreatment());
    }

    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {

        txt_patient.setText(Utils.currentPatient.getName() + " " + Utils.currentPatient.getSurname());
        txt_physio.setText(Utils.currentPhysio.getName() + " " + Utils.currentPhysio.getSurname());
        txt_treatment.setText(Utils.currentTreatment);
        txt_time.setText(new StringBuilder(Utils.convertTimeSlotToString(Utils.currentTimeSlot)));
        txt_date.setText(displayDateFormat.format(Utils.currentDate.getTime()));
        txt_place.setText(Utils.currentPhysio.getCabinet());
    }

    private void insertAppointment(String physioId, String patientId, String date, String time, String place, String treatment){

        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> insertData = api.insertAppointment("INSERT", physioId, patientId, date, time, place, treatment);

        //    Utils.showProgressBar(mProgressBar);

        insertData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {

                if(response == null || response.body() == null || response.body().getCode()==null){
                    System.out.println("Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }

                Log.d("RETROFIT", "response : " + response.body().toString());
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equals("1")) {
                    System.out.printf("0. SUCCESS: \n 1. Data inserted Successfully. \n 2. ResponseCode: "  +myResponseCode);
                           show(getContext(), "Pomyślnie zarezerwowano wizytę!");
                           resetStaticData();
                           getActivity().finish();
                           openActivity(getActivity(), PatientDashboardActivity.class);
                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    System.out.println("UNSUCCESSFUL"+
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                            " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+myResponseCode+
                            " \n 3. Most probably the problem is with your PHP Code.");
                }else if (myResponseCode.equalsIgnoreCase("3")) {
                    System.out.println("NO MYSQL CONNECTION"+" Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
//                    showInfoDialog(PatientRegisterActivity.this, "FAILURE",
//                            "FAILURE THROWN DURING INSERT."+
//                                    " ERROR Message: " + t.getMessage());
            }
        });
    }

    private void resetStaticData() {
        Utils.step = 0;
        Utils.currentDate.add(Calendar.DATE, 0);
        Utils.currentPhysio = null;
        Utils.currentTimeSlot = -1;
        Utils.currentTreatment = null;
    }

    static ReservationConfirmFragment instance;
    public static ReservationConfirmFragment getInstance(){
        if(instance == null)
            instance = new ReservationConfirmFragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Utils.KEY_CONFIRM_BOOKING));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_reservation_confirm, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        return itemView;
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }
}
