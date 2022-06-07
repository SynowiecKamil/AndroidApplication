package synowiec.application.physio;

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

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;


public class PhysioDashboardActivity extends AppCompatActivity{

    private TextView name, email;
    private String id = null, getId;
    private Button btn_logout, btn_photo_upload, btn_delete_user;
    private HashMap<String, String> user;
    SessionManager sessionManager;
    private ProgressBar mProgressBar;
    private Menu action;
    private Bitmap bitmap;
    CircleImageView profile_image;
    private Context c = PhysioDashboardActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_dashboard);
        initializeWidgets();
        showData(user);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { sessionManager.logout("physio"); }
        });

        btn_photo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { chooseFile(); }
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
    @Override
    public void onBackPressed() { sessionManager.logout("physiotherapist"); }

    @Override
    protected void onResume() { super.onResume(); }

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

                updateData();
                if(bitmap != null) UploadPictureRetrofit(getId, getStringImage(bitmap));

                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);

                name.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                name.setFocusable(false);
                email.setFocusable(false);
                btn_photo_upload.setVisibility(View.GONE);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    //RECEIVE DATA FROM SESSION MANAGER AND FILL
    private void showData(HashMap<String, String> user){
        if(user != null){
            name.setText(user.get(sessionManager.NAME));
            email.setText(user.get(sessionManager.EMAIL));
            Picasso.get().load(user.get(sessionManager.PHOTO))
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);
            System.out.println("Znaleziono uzytkownika");
        }else{
            System.out.println("Brak uzytkownika");
        }
    }

    //SELECT * FROM DB
    private void receiveFromDatabase(){
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<String> login = api.getPhysioData("READ", getId);

        login.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                if(response == null || response.body() == null ){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }

                Log.d("RETROFIT", "response : " + response.body().toString());
                String myResponse = response.body().toString();

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject object = jsonArray.getJSONObject(i);

                            String name = object.getString("name").trim();
                            String email = object.getString("email").trim();
                            String id = object.getString("id").trim();
                            String photo = object.getString("photo").trim();

                            //   receivedPhysio = new Physiotherapist(id, name, email, photo);
                            sessionManager.createSession(id, name, email, photo);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PhysioDashboardActivity.this, "Bląd!", Toast.LENGTH_SHORT).show();
                    }
                } else if (!response.isSuccessful()) {
                    showInfoDialog(PhysioDashboardActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                    " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+" " +
                                    " \n 3. Most probably the problem is with your PHP Code.");
                }
                //       hideProgressBar(mProgressBar);
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                //     hideProgressBar(mProgressBar);
                showInfoDialog(PhysioDashboardActivity.this, "FAILURE",
                        "FAILURE THROWN DURING INSERT."+
                                " ERROR Message: " + t.getMessage());
            }
        });
    }

    //UPDATE NAME, EMAIL IN DB
    private void updateData() {
        String sName, sEmail, sId;
        sName = name.getText().toString();
        sEmail = email.getText().toString();
        sId = getId;

        showProgressBar(mProgressBar);
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> update = api.updatePhysio("UPDATE", sId, sName, sEmail);
        update.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "Response: " + response.body().getResult());

                hideProgressBar(mProgressBar);
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    show(c, "Pomyślnie zaaktualizowano dane ");
                    System.out.println(response.body().getMessage());

                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    showInfoDialog(PhysioDashboardActivity.this, "UNSUCCESSFUL",
                            "Good Response From PHP,"+
                                    "WE ATTEMPTED UPDATING DATA BUT ENCOUNTERED ResponseCode: "+myResponseCode+
                                    " \n 3. Most probably the problem is with your PHP Code.");
                } else if (myResponseCode.equalsIgnoreCase("3")) {
                    showInfoDialog(PhysioDashboardActivity.this, "NO MYSQL CONNECTION",
                            " Your PHP Code"+
                                    " is unable to connect to mysql database. Make sure you have supplied correct"+
                                    " database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR THROWN DURING UPDATE: " + t.getMessage());
                hideProgressBar(mProgressBar);
                showInfoDialog(PhysioDashboardActivity.this, "FAILURE THROWN", "ERROR DURING UPDATE.Here"+
                        " is the Error: " + t.getMessage());
            }
        });
    }

    //CHOOSE FILE FOR PHOTO UPDATE
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
        }
    }

    //UPDATE PHOTO IN DB
    private void UploadPictureRetrofit(final String id, final String photo){
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> upl = api.uploadImagePhysio("UPLOAD_IMAGE", id, photo);

        upl.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "UPLOAD IMAGE RESPONSE: " + response.body().toString());
                System.out.println(response.body().getMessage());
                show(c, "Pomyślnie zmieniono zdjęcie użytkownika");
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR THROWN DURING UPLOAD: " + t.getMessage());
                hideProgressBar(mProgressBar);
            }
        });

    }

    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodedImage;
    }
    // DELETE USER IN DB
    private void deleteUser() {
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> del = api.removePhysio("DELETE", getId);

        showProgressBar(mProgressBar);
        del.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    showInfoDialog(PhysioDashboardActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }

                Log.d("RETROFIT", "DELETE RESPONSE: " + response.body());
                hideProgressBar(mProgressBar);
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    System.out.println(response.body().getMessage());
                    show(c, "Pomyślnie usunięto użytkownika");
                    sessionManager.logout("physio");
                    finish();
                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    showInfoDialog(PhysioDashboardActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL"+
                                    " \n 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+
                                    myResponseCode+ " \n 3. Most probably the problem is with your PHP Code.");
                }else if (myResponseCode.equalsIgnoreCase("3")) {
                    showInfoDialog(PhysioDashboardActivity.this, "NO MYSQL CONNECTION",
                            " Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                hideProgressBar(mProgressBar);
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PhysioDashboardActivity.this, "FAILURE THROWN", "ERROR during DELETE attempt. Message: " + t.getMessage());
            }
        });
    }

    public void initializeWidgets(){
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin("fizjoterapeuta");

        mProgressBar = findViewById(R.id.mProgressBarSave);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);

        name = findViewById(R.id.name);
        name.setFocusableInTouchMode(false);
        email = findViewById(R.id.email);
        email.setFocusableInTouchMode(false);
        btn_logout = findViewById(R.id.btn_logout);

        btn_photo_upload = findViewById(R.id.btn_photo);
        btn_photo_upload.setVisibility(View.GONE);

        profile_image = findViewById(R.id.profile_image);
        btn_delete_user = findViewById(R.id.btn_delete_user);


        user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
    }
}
