package synowiec.application.patient;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.model.PhysioTreatment;
import synowiec.application.model.Physiotherapist;
import synowiec.application.model.Treatment;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;
import static synowiec.application.helpers.Utils.showProgressBar;

public class PhysioDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameET, emailET;
    private Button backBTN;
    private ListView treatmentLV;
    private ArrayAdapter<String> adapter;
    private Physiotherapist receivedPhysiotherapist;
    private ArrayList<String> treatmentNameList = new ArrayList<>();
 //   private PhysioTreatment receivedPhysioTreatment;
    CircleImageView profile_image;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physio_detail);

        initializeWidgets();
        receiveAndShowData();
        retrieveTreatment("RETRIEVE_TREATMENT");

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openActivity(PhysioDetailActivity.this, PatientSearchActivity.class);
                finish();
            }
        });
    }

    private void receiveAndShowData(){
        receivedPhysiotherapist = Utils.receivePhysiotherapist(getIntent(),PhysioDetailActivity.this);
        if(receivedPhysiotherapist !=null){
            nameET.setText(receivedPhysiotherapist.getName());
            emailET.setText(receivedPhysiotherapist.getEmail());
            Picasso.get().load(receivedPhysiotherapist.getPhoto())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(profile_image);

        }

//        receivedPhysioTreatment = Utils.receivePhysioTreatment(getIntent(),PhysioDetailActivity.this);
//        if(receivedPhysioTreatment !=null){
//
//
//        }
    }

    private void showTreatmentList(){
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, treatmentNameList);
        treatmentLV.setAdapter(adapter);
    }

    private void retrieveTreatment(final String action) {

        String sQueryID = receivedPhysiotherapist.getId();
        int queryID = Integer.parseInt(sQueryID);
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.searchTreatment(action, queryID, "0", "10");
        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                    showInfoDialog(PhysioDetailActivity.this,"ERROR","Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RETROFIT", "response : " + response.body().getTreatments());
                   List<Treatment> currentTreatment = response.body().getTreatments();
                   System.out.println(currentTreatment);
               for (int i = 0; i < currentTreatment.size(); i++) {
                   treatmentNameList.add(currentTreatment.get(i).getName());
                }
                  showTreatmentList();
                }else if (!response.isSuccessful()) {
                    showInfoDialog(PhysioDetailActivity.this, "UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                    " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+" " +
                                    " \n 3. Most probably the problem is with your PHP Code.");
                }

            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                showInfoDialog(PhysioDetailActivity.this, "ERROR", t.getMessage());
            }
        });
    }


    @Override
    public void onClick(View v) {
        Utils.openActivity(PhysioDetailActivity.this, PatientSearchActivity.class);
    }

    private void initializeWidgets(){
        nameET = findViewById(R.id.name);
        emailET = findViewById(R.id.email);
        profile_image = findViewById(R.id.profile_image);
        backBTN = findViewById(R.id.btn_back);
        treatmentLV = findViewById(R.id.treatmentLV);
    }
}
