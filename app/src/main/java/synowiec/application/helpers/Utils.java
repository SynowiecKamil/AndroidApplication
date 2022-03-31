package synowiec.application.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import synowiec.application.R;
import synowiec.application.model.Physiotherapist;

public class Utils {

    private  static  final String base_url = "http://192.168.21.17/android_register_login/";
    private static Retrofit retrofit;
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     return  Retrofit instance to initiate HTTP calls.
     */
    public static Retrofit getClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }

    /**
     show Toast messages throughout all activities
     */
    public static void show(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
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
     convert a string into a java.util.Date object and return it.
     */
    public static Date giveMeDate(String stringDate){
        try {
            SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);
            return sdf.parse(stringDate);
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
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

}
