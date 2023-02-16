package synowiec.application.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Slide;

import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.model.Appointment;
import synowiec.application.model.Patient;
import synowiec.application.model.PhysioTreatment;
import synowiec.application.model.Physiotherapist;
import synowiec.application.model.Treatment;
import synowiec.application.physio.PhysioDashboardActivity;

public class Utils {


    private  static  final String base_url = "http://192.168.21.17/android_application/";

    private static Retrofit retrofit;

    public static Patient currentPatient;
    public static Physiotherapist currentPhysio;
    public static String currentTreatment;
    public static int currentTimeSlot=-1;
    public static Calendar currentDate = Calendar.getInstance();

    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String KEY_TREATMENT_SELECTED = "TREATMENT_SELECTED";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";

    public static int step = 0; //init first step = 0
    public static final int TIME_SLOT_TOTAL = 8;
    public static final Object DISABLE_TAG = "DISABLE";

    /**
     return  Retrofit instance to initiate HTTP calls.
     */
    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }

    /**
     show Toast messages throughout all activities
     */
    public static void show(Context c, String message){
        Toast toast = Toast.makeText(c, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    public static void showInfoDialog(final AppCompatActivity activity, String title,
                                      String message) {
        new LovelyStandardDialog(activity, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
   //             .setTopColorRes(R.color.indigo)
  //              .setButtonsColorRes(R.color.darkDeepOrange)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", v -> {})
                .show();
    }

    /**
     open any activity.
     */
    public static void openActivity(Context c,Class clazz){
        Intent intent = new Intent(c, clazz);
        c.startActivity(intent);
    }
    /**
     send a serialized Physiotherapist object to a specified activity
     */
    public static void sendPhysiotherapistToActivity(Context c, Physiotherapist physiotherapist,
                                                     Class clazz){
        Intent i=new Intent(c,clazz);
        i.putExtra("PHYSIOTHERAPIST_KEY", physiotherapist);
        c.startActivity(i);
    }


    /**
     receive a serialized Physio, deserialize it and return it,.
     */
    public  static Physiotherapist receivePhysiotherapist(Intent intent, Context c){
        try {
            return (Physiotherapist) intent.getSerializableExtra("PHYSIOTHERAPIST_KEY");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     show a progressbar
     */
    public static void showProgressBar(ProgressBar pb){
        pb.setVisibility(View.VISIBLE);
    }
    /**
     hide a progressbar
     */
    public static void hideProgressBar(ProgressBar pb){
        pb.setVisibility(View.GONE);
    }

    public static String convertTimeSlotToString(int slot) {
        switch (slot)
        {
            case 0:
                return "8:00-9:00";
            case 1:
                return "9:00-10:00";
            case 2:
                return "10:00-11:00";
            case 3:
                return "11:00-12:00";
            case 4:
                return "12:00-13:00";
            case 5:
                return "13:00-14:00";
            case 6:
                return "14:00-15:00";
            case 7:
                return "15:00-16:00";
            default:
                return "Niedostępne";
        }
    }
}
