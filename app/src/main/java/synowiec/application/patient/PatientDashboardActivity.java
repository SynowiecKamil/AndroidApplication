package synowiec.application.patient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.physio.PhysioDashboardActivity;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;

public class PatientDashboardActivity extends AppCompatActivity {

    private static final String TAG = PatientDashboardActivity.class.getSimpleName(); //getting the info
    private TextView name, email;
    private Button btn_logout, btn_photo_upload, btn_delete_user;
    SessionManager sessionManager;
    String getId;
    private ProgressBar mProgressBar;
    private static String URL_READ = "http://192.168.21.17/android_application/readDetailPatient.php";
    private static String URL_EDIT = "http://192.168.21.17/android_application/editDetailPatient.php";
    private static String URL_UPLOAD = "http://192.168.21.17/android_application/uploadPatient.php";
    private Menu action;
    private Bitmap bitmap;
    CircleImageView profile_image;
    private Context c = PatientDashboardActivity.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin("patient");

        mProgressBar = findViewById(R.id.mProgressBarSave);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);

        name = findViewById(R.id.name);
        name.setFocusableInTouchMode(false);

        email = findViewById(R.id.email);
        email.setFocusableInTouchMode(false);

        btn_logout = findViewById(R.id.btn_logout);
        btn_photo_upload = findViewById(R.id.btn_photo);
        btn_delete_user = findViewById(R.id.btn_delete_user);
        btn_photo_upload.setVisibility(View.GONE);
        profile_image = findViewById(R.id.profile_image);

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout("patient");
            }
        });

        btn_photo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        btn_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyStandardDialog(c, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        //       .setTopColorRes(R.color.indigo)
                        //       .setButtonsColorRes(R.color.darkDeepOrange)
                        .setTitle("Ostrzeżenie")
                        .setMessage("Czy na pewno chcesz usunąć konto? Zmiany będą nieodwracalne.")
                        .setPositiveButton("NIE", x -> {})
                        .setNegativeButton("TAK", x -> deleteUser())
                        .show();
            }
        });

    }

    //getUserDetail
    private void getUserDetail(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        profile_image = findViewById(R.id.profile_image);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response.toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")){

                                for (int i =0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String strName = object.getString("name").trim();
                                    String strEmail = object.getString("email").trim();
                                    String strImage = object.getString("image").trim();

                                    name.setText(strName);
                                    email.setText(strEmail);
                                    Picasso.get().load(strImage)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .into(profile_image);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(PatientDashboardActivity.this, "Błąd odczytu danych "+e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(PatientDashboardActivity.this, "Błąd odczytu danych "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String > params = new HashMap<>();
                params.put("id", getId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action, menu);

        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_edit:

                name.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);
                btn_photo_upload.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);

                return true;

            case R.id.menu_save:

                SaveEditDetail();

                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);
                btn_photo_upload.setVisibility(View.GONE);

                name.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                name.setFocusable(false);
                email.setFocusable(false);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    //save
    private void SaveEditDetail() {

        final String name = this.name.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String id = getId;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                Toast.makeText(PatientDashboardActivity.this, "Zapisano pomyślnie!", Toast.LENGTH_SHORT).show();
                                sessionManager.createSession(name, email, id);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(PatientDashboardActivity.this, "Błąd "+ e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(PatientDashboardActivity.this, "Błąd "+ error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Wybierz obraz"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

            UploadPicture(getId, getStringImage(bitmap));

        }
    }

    private void UploadPicture(final String id, final String photo) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wgrywanie...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                Toast.makeText(PatientDashboardActivity.this, "Pomyślnie!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(PatientDashboardActivity.this, "Spróbuj ponownie!"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(PatientDashboardActivity.this, "Spróbuj ponownie" + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("photo", photo);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        getUserDetail();

    }

    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }

    private void deleteUser() {
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> del = api.removePatient("DELETE", getId);

        showProgressBar(mProgressBar);
        del.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PatientDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }

                Log.d("RETROFIT", "DELETE RESPONSE: " + response.body());
                hideProgressBar(mProgressBar);
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    System.out.println(response.body().getMessage());
                    show(c, "Pomyślnie usunięto użytkownika");
                    sessionManager.logout("patient");
                    finish();
                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    showInfoDialog(PatientDashboardActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL"+
                                    " \n 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+
                                    myResponseCode+ " \n 3. Most probably the problem is with your PHP Code.");
                }else if (myResponseCode.equalsIgnoreCase("3")) {
                    showInfoDialog(PatientDashboardActivity.this, "NO MYSQL CONNECTION",
                            " Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                hideProgressBar(mProgressBar);
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PatientDashboardActivity.this, "FAILURE THROWN", "ERROR during DELETE attempt. Message: " + t.getMessage());
            }
        });
    }
}