package synowiec.application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import synowiec.application.physio.PhysioDashboardActivity;
import synowiec.application.physio.PhysioLoginActivity;
import synowiec.application.patient.PatientDashboardActivity;
import synowiec.application.patient.PatientLoginActivity;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ID = "ID";
    public static final String PHOTO = "PHOTO";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String name, String email, String photo){

        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(ID, id);
        editor.putString(PHOTO, photo);
        editor.apply();

    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(String user){

        if(user == "patient"){
            if (!this.isLoggin()){
                Intent i = new Intent(context, PatientLoginActivity.class);
                context.startActivity(i);
                ((PatientDashboardActivity) context).finish();
            }
        }else{
            if (!this.isLoggin()){
                Intent i = new Intent(context, PhysioLoginActivity.class);
                context.startActivity(i);
                ((PhysioDashboardActivity) context).finish();
            }
        }

    }

    public HashMap<String, String> getUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(PHOTO, sharedPreferences.getString(PHOTO, null));

        return user;
    }

    public void logout(String user){

        if(user == "patient"){
            editor.clear();
            editor.commit();
            Intent i = new Intent(context, PatientLoginActivity.class);
            context.startActivity(i);
            ((PatientDashboardActivity) context).finish();
        }else{
            editor.clear();
            editor.commit();
            Intent i = new Intent(context, PhysioLoginActivity.class);
            context.startActivity(i);
            ((PhysioDashboardActivity) context).finish();
        }


    }

}