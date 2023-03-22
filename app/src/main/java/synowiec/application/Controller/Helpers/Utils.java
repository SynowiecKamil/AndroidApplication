package synowiec.application.Controller.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.util.Calendar;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import synowiec.application.R;
import synowiec.application.Model.Patient;
import synowiec.application.Model.Physiotherapist;

public class Utils {


    private  static  final String base_url = "http://192.168.21.17/android_application/";

    private static Retrofit retrofit;

    public static Patient currentPatient;
    public static Physiotherapist currentPhysio;
    public static String currentTreatment;
    public static double currentPrice;
    public static int currentTimeSlot=-1;
    public static Calendar currentDate = Calendar.getInstance();
    public static int TIME_SLOT_TOTAL;

    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String KEY_TREATMENT_SELECTED = "TREATMENT_SELECTED";
    public static final String KEY_PRICE_SELECTED = "PRICE_SELECTED";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";

    public static int step = 0; //init first step = 0
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

    public static void hideSoftKeyboard(Activity activty) {
        InputMethodManager inputManager = (InputMethodManager) activty.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        View focusedView = activty.getCurrentFocus();
        /*
         * If no view is focused, an NPE will be thrown
         *
         * Maxim Dmitriev
         */
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public static void showInfoDialog(final AppCompatActivity activity, String title,
                                      String message) {
        new LovelyStandardDialog(activity, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
   //             .setTopColorRes(R.color.indigo)
  //              .setButtonsColorRes(R.color.darkDeepOrange)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButtonColor(activity.getResources().getColor(R.color.teal))
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
                return "7:00-8:00";
            case 1:
                return "8:00-9:00";
            case 2:
                return "9:00-10:00";
            case 3:
                return "10:00-11:00";
            case 4:
                return "11:00-12:00";
            case 5:
                return "12:00-13:00";
            case 6:
                return "13:00-14:00";
            case 7:
                return "14:00-15:00";
            case 8:
                return "15:00-16:00";
            case 9:
                return "16:00-17:00";
            case 10:
                return "17:00-18:00";
            case 11:
                return "18:00-19:00";
            case 12:
                return "19:00-20:00";
            case 13:
                return "20:00-21:00";
            case 14:
                return "21:00-22:00";
            default:
                return "Niedostępne";
        }
    }

    public static int convertStringToTimeSlot(String time) {
        switch (time)
        {
            case "07:00":
                return 0;
            case "08:00":
                return 1;
            case "09:00":
                return 2;
            case "10:00":
                return 3;
            case "11:00":
                return 4;
            case "12:00":
                return 5;
            case "13:00":
                return 6;
            case "14:00":
                return 7;
            case "15:00":
                return 8;
            case "16:00":
                return 9;
            case "17:00":
                return 10;
            case "18:00":
                return 11;
            case "19:00":
                return 12;
            case "20:00":
                return 13;
            case "21:00":
                return 14;
            case "22:00":
                return 15;
            default:
                return -1;
        }
    }
}
