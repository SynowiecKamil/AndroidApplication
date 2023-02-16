package synowiec.application.patient;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.MyViewPagerAdapter;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Appointment;
import synowiec.application.model.Physiotherapist;

public class PatientReservationActivity extends AppCompatActivity {

    Physiotherapist receivedPhysiotherapist;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

//    @OnClick(R.id.btn_next_step)
//    void onClick(){
//        Toast.makeText(this, ""+Utils.currentTreatment, Toast.LENGTH_SHORT).show();
//    }

    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra(Utils.KEY_STEP, 0);
            if(step == 1) {
                Utils.currentTimeSlot = intent.getIntExtra(Utils.KEY_TIME_SLOT, -1);
                System.out.println("currentTimeSlot: "+ Utils.currentTimeSlot+"current date: "+Utils.currentDate.getTime()+" , currentPhysio: "+Utils.currentPhysio.getName());
                btn_next_step.setEnabled(true);
            }
            else if(step==2) {
                    Utils.currentTreatment = intent.getStringExtra(Utils.KEY_TREATMENT_SELECTED);
                    System.out.println("currentTreatment: "+ Utils.currentTreatment);
                    btn_next_step.setEnabled(true);
            }
            setColorButton();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_reservation);
        ButterKnife.bind(PatientReservationActivity.this);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Utils.KEY_ENABLE_BUTTON_NEXT));

        setupStepView();
        setColorButton();

        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                stepView.go(i, true);
                if(i == 0){
                    btn_previous_step.setEnabled(false);
                }else{
                    btn_previous_step.setEnabled(true);
                }
                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

        //event
    @OnClick(R.id.btn_previous_step)
    void previousStep(){
        if(Utils.step == 2 || Utils.step > 0)
        {
            Utils.step--;
            viewPager.setCurrentItem(Utils.step);
            if(Utils.step < 3)
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick(){
        if(Utils.step < 2 || Utils.step == 0)
        {
            Utils.step++;
            if(Utils.step == 1 ) // after choose date
            {
                if(Utils.currentTimeSlot != -1)
                loadTreatmentOfPhysio();
            }
            else if(Utils.step == 2)// after choose treatment
            {
                if(Utils.currentTreatment != null)
                confirmBooking();
            }
            viewPager.setCurrentItem(Utils.step);
        }
    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    private void setColorButton() {
        if(btn_next_step.isEnabled()){
            btn_next_step.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else
        {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if(btn_previous_step.isEnabled()){
            btn_previous_step.setBackgroundResource(R.color.colorPrimary);
        }
        else
        {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Wybierz termin");
        stepList.add("Wybierz zabieg");
        stepList.add("Podsumowanie");
        stepView.setSteps(stepList);
    }

    private void loadTimeSlotOfPhysio(){
        //load broadcast to fragment step 1
        System.out.println("Fragment 1");
        Intent intent = new Intent(Utils.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTreatmentOfPhysio(){
        // send broadcast to fragment step 2
        Intent intent = new Intent(Utils.KEY_ENABLE_BUTTON_NEXT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void confirmBooking(){
        //send broadcast to fragment step 3
        Intent intent = new Intent(Utils.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

}
