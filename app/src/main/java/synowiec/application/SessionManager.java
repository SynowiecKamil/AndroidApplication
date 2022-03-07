package synowiec.application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import synowiec.application.fizjoterapeuta.FizjoterapeutaDashboardActivity;
import synowiec.application.fizjoterapeuta.FizjoterapeutaLoginActivity;
import synowiec.application.pacjent.PacjentDashboardActivity;
import synowiec.application.pacjent.PacjentLoginActivity;

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

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name, String email, String id){

        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(ID, id);
        editor.apply();

    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }

    public void checkLogin(String user){

        if(user == "pacjent"){
            if (!this.isLoggin()){
                Intent i = new Intent(context, PacjentLoginActivity.class);
                context.startActivity(i);
                ((PacjentDashboardActivity) context).finish();
            }
        }else{
            if (!this.isLoggin()){
                Intent i = new Intent(context, FizjoterapeutaLoginActivity.class);
                context.startActivity(i);
                ((FizjoterapeutaDashboardActivity) context).finish();
            }
        }

    }

    public HashMap<String, String> getUserDetail(){

        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(ID, sharedPreferences.getString(ID, null));

        return user;
    }

    public void logout(String user){

        if(user == "pacjent"){
            editor.clear();
            editor.commit();
            Intent i = new Intent(context, PacjentLoginActivity.class);
            context.startActivity(i);
            ((PacjentDashboardActivity) context).finish();
        }else{
            editor.clear();
            editor.commit();
            Intent i = new Intent(context, FizjoterapeutaLoginActivity.class);
            context.startActivity(i);
            ((FizjoterapeutaDashboardActivity) context).finish();
        }


    }

}