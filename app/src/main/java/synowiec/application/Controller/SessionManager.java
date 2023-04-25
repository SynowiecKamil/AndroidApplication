package synowiec.application.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import synowiec.application.Controller.PhysioActivities.PhysioDashboardActivity;
import synowiec.application.Controller.PhysioActivities.PhysioLoginActivity;
import synowiec.application.Controller.PatientActivities.PatientDashboardActivity;
import synowiec.application.Controller.PatientActivities.PatientLoginActivity;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ID = "ID";
    public static final String PHOTO = "PHOTO";
    public static final String SURNAME = "SURNAME";
    public static final String PROFESSION_NUMBER = "PROFESSION_NUMBER";
    public static final String CITY = "CITY";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String ADDRESS = "ADDRESS";
    public static final String DAYS = "DAYS";
    public static final String HOURS = "HOURS";

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String id, String name, String email, String photo, String surname, String professionNumber, String city, String description, String address, String days, String hours){

        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(ID, id);
        editor.putString(PHOTO, photo);
        editor.putString(SURNAME, surname);
        editor.putString(PROFESSION_NUMBER, professionNumber);
        editor.putString(CITY, city);
        editor.putString(DESCRIPTION, description);
        editor.putString(ADDRESS, address);
        editor.putString(DAYS, days);
        editor.putString(HOURS, hours);
        editor.apply();

    }

    public void createSession(String id, String name, String email, String photo, String surname){

        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(ID, id);
        editor.putString(PHOTO, photo);
        editor.putString(SURNAME, surname);
        editor.apply();

    }

    public boolean isLogged(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(String userType){

        if(userType == "patient"){
            if (!this.isLogged()){
                Intent i = new Intent(context, PatientLoginActivity.class);
                context.startActivity(i);
                ((PatientDashboardActivity) context).finish();
            }
        }else{
            if (!this.isLogged()){
                Intent i = new Intent(context, PhysioLoginActivity.class);
                context.startActivity(i);
                ((PhysioDashboardActivity) context).finish();
            }
        }

    }

    public HashMap<String, String> getUserDetail(String userType){

        HashMap<String, String> user = new HashMap<>();
        if(userType.equals("physio")) {
            user.put(ID, sharedPreferences.getString(ID, null));
            user.put(NAME, sharedPreferences.getString(NAME, null));
            user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
            user.put(PHOTO, sharedPreferences.getString(PHOTO, null));
            user.put(SURNAME, sharedPreferences.getString(SURNAME, null));
            user.put(PROFESSION_NUMBER, sharedPreferences.getString(PROFESSION_NUMBER, null));
            user.put(CITY, sharedPreferences.getString(CITY, null));
            user.put(DESCRIPTION, sharedPreferences.getString(DESCRIPTION, null));
            user.put(ADDRESS, sharedPreferences.getString(ADDRESS, null));
            user.put(DAYS, sharedPreferences.getString(DAYS, null));
            user.put(HOURS, sharedPreferences.getString(HOURS, null));
        }else if(userType.equals("patient")){
            user.put(ID, sharedPreferences.getString(ID, null));
            user.put(NAME, sharedPreferences.getString(NAME, null));
            user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
            user.put(PHOTO, sharedPreferences.getString(PHOTO, null));
            user.put(SURNAME, sharedPreferences.getString(SURNAME, null));
        }
        return user;
    }

    public void logout(String userType){

        if(userType == "patient"){
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