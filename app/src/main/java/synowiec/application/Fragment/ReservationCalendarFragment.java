package synowiec.application.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import synowiec.application.R;
import synowiec.application.helpers.ITimeSlotLoadListener;
import synowiec.application.helpers.MyTimeSlotAdapter;
import synowiec.application.helpers.SpacesItemDecoration;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Appointment;
import synowiec.application.model.Physiotherapist;

public class ReservationCalendarFragment extends Fragment implements ITimeSlotLoadListener {

    private Physiotherapist receivedPhysiotherapist;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    private AlertDialog dialog;
    private String receivedPhysioID;
    private List<Appointment> appointmentList;
    private MyTimeSlotAdapter adapter;
    private HorizontalCalendar horizontalCalendar;
    private SimpleDateFormat simpleDateFormat;
    Unbinder unbinder;
    Calendar choose_date, startDate;
    Calendar pickedDate = Calendar.getInstance();
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_time_slot)
    RecyclerView recycler_time_slot;
    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iTimeSlotLoadListener = this;

        // get Physiotherapist from activity
        Intent i = getActivity().getIntent();
        receivedPhysiotherapist = (Physiotherapist) i.getSerializableExtra("PHYSIOTHERAPIST_KEY");
        receivedPhysioID = receivedPhysiotherapist.getId();
        Utils.currentPhysio = receivedPhysiotherapist;
        // get appointmentList
        appointmentList = (List<Appointment>) i.getSerializableExtra("APPOINTMENT_KEY");
        System.out.println("Appointment list: " + appointmentList);

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
     }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DatePickerDialog.OnDateSetListener myDate =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                pickedDate.set(Calendar.YEAR, year);
                pickedDate.set(Calendar.MONTH,month);
                pickedDate.set(Calendar.DAY_OF_MONTH,day);
                horizontalCalendar.selectDate(pickedDate, true);
                horizontalCalendar.refresh();
            }
        };

        View itemView = inflater.inflate(R.layout.fragment_patient_calendar, container, false);
        unbinder = ButterKnife.bind(this, itemView);
        init(itemView, myDate);
        if(choose_date == null) horizontalCalendar.selectDate(startDate, true);
        else loadAvailableTimeSlotOfPhysio(simpleDateFormat.format(choose_date.getTime()));
        return itemView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadAvailableTimeSlotOfPhysio(String date){

            ArrayList<String> appointmentTimeList = new ArrayList<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE");
            if( dateFormat.format(choose_date.getTime()).equals("sob.") || dateFormat.format(choose_date.getTime()).equals("niedz.")){
                Collections.addAll(appointmentTimeList, "0","1", "2", "3", "4", "5", "6","7");
                System.out.println("appoi time list: "+ appointmentTimeList);
            }else {
                for (Appointment appointment : appointmentList) {
                    if (appointment.getDate().equals(date))
                        appointmentTimeList.add(appointment.getTime());
                }
            }
            if(appointmentTimeList != null) onTimeSlotLoadSuccess(appointmentTimeList);
            else onTimeSlotLoadEmpty();
    }

    static ReservationCalendarFragment instance;
    public static ReservationCalendarFragment getInstance(){
        if(instance == null)
            instance = new ReservationCalendarFragment();
        return instance;
    }

    private void init(View itemView, DatePickerDialog.OnDateSetListener myDate) {
        recycler_time_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recycler_time_slot.setLayoutManager(gridLayoutManager);
        recycler_time_slot.addItemDecoration(new SpacesItemDecoration(8));
        System.out.println("INIT");
        //Calendar
        startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 365);


        horizontalCalendar = new HorizontalCalendar.Builder(itemView, R.id.calendarView)
                .range(startDate,endDate)
                .datesNumberOnScreen(1)
                .configure()    // starts configuration.
                .formatTopText("MMMM")
                .end()
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                    choose_date = date;
                    Utils.currentDate = choose_date;
                    loadAvailableTimeSlotOfPhysio(simpleDateFormat.format(choose_date.getTime()));
            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                new DatePickerDialog(getContext(),myDate,pickedDate.get(Calendar.YEAR),pickedDate.get(Calendar.MONTH),pickedDate.get(Calendar.DAY_OF_MONTH)).show();
                System.out.println("onDateLongClicked");
                return super.onDateLongClicked(date, position);
            }

        });
    }


    @Override
    public void onTimeSlotLoadSuccess(List<String> timeSlotList) {
        adapter = new MyTimeSlotAdapter(getContext(), timeSlotList);
        recycler_time_slot.setAdapter(adapter);

    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        adapter = new MyTimeSlotAdapter(getContext());
        recycler_time_slot.setAdapter(adapter);
    }


}
