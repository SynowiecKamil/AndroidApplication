package synowiec.application.Controller.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.Helpers.Utils;
import synowiec.application.Model.Appointment;
import synowiec.application.Controller.PatientActivities.PatientDashboardActivity;

import static synowiec.application.Controller.Helpers.Utils.openActivity;
import static synowiec.application.Controller.Helpers.Utils.show;

public class ReservationConfirmFragment extends Fragment {

    LocalBroadcastManager localBroadcastManager;
    private SimpleDateFormat simpleDateFormat, displayDateFormat;
    Unbinder unbinder;
    private static final DecimalFormat df = new DecimalFormat("0.00");

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
    @BindView(R.id.txt_price)
    TextView txt_price;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    @OnClick(R.id.btn_confirm)
    public void confirmAppointment(){
        Appointment appointment = new Appointment();

        appointment.setPatientId(Utils.currentPatient.getId());
        appointment.setPhysioId(Utils.currentPhysio.getId());
        appointment.setTime(Integer.toString(Utils.currentTimeSlot));
        appointment.setDate(simpleDateFormat.format(Utils.currentDate.getTime()));
        appointment.setCity(Utils.currentPhysio.getCity());
        appointment.setTreatment(Utils.currentTreatment);
        appointment.setAddress(Utils.currentPhysio.getAddress());
        appointment.setPrice(Utils.currentPrice);

        insertAppointment(appointment);
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
        txt_place.setText(Utils.currentPhysio.getCity()+", "+Utils.currentPhysio.getAddress());
        txt_price.setText(df.format(Utils.currentPrice) + " zł");
    }

    private void insertAppointment(Appointment appointment){

        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> insertData = api.insertAppointment("INSERT", appointment.getPhysioId(), appointment.getPatientId(), appointment.getDate(), appointment.getTime(), appointment.getCity(), appointment.getTreatment(), appointment.getAddress(), appointment.getPrice());

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
        Utils.currentPrice = -1;
    }

    static ReservationConfirmFragment instance;
    public static ReservationConfirmFragment getInstance(){
        if(instance == null)
            instance = new ReservationConfirmFragment();
        return instance;
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern= Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Utils.KEY_CONFIRM_BOOKING));
        System.out.println(Utils.currentPrice);

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
