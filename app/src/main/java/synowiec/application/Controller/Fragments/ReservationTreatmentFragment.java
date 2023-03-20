package synowiec.application.Controller.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Controller.helpers.Utils;
import synowiec.application.Model.Physiotherapist;
import synowiec.application.Model.Treatment;

public class ReservationTreatmentFragment extends Fragment {

    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    Unbinder unbinder;
    private String treatment;
    private Physiotherapist receivedPhysiotherapist;
    private String receivedPhysioID;
    private ArrayList<String> treatmentNameList = new ArrayList<>();
    private List<Treatment> currentTreatment;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;


    static ReservationTreatmentFragment instance;
    public static ReservationTreatmentFragment getInstance(){
        if(instance == null)
            instance = new ReservationTreatmentFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getActivity().getIntent();
        receivedPhysiotherapist = (Physiotherapist) i.getSerializableExtra("PHYSIOTHERAPIST_KEY");
        receivedPhysioID = receivedPhysiotherapist.getId();
        treatmentNameList.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_reservation_treatment, container, false);
        unbinder = ButterKnife.bind(this, itemView);


        retrieveTreatment( "RETRIEVE_TREATMENT",receivedPhysioID);


        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                    Intent intent = new Intent(Utils.KEY_ENABLE_BUTTON_NEXT);
                    intent.putExtra(Utils.KEY_TREATMENT_SELECTED, item);
                    for (int i = 0; i < currentTreatment.size(); i++) {
                    if(currentTreatment.get(i).getName().equals(item)){
                        intent.putExtra(Utils.KEY_PRICE_SELECTED, Double.parseDouble(currentTreatment.get(i).getId()));
                    }
                    }
                    intent.putExtra(Utils.KEY_STEP, 2);
                    localBroadcastManager.sendBroadcast(intent);
            }
        });
        return itemView;
    }

    private void retrieveTreatment(String action, String physioId) {

        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> retrievedData;
        retrievedData = api.searchTreatment(action, physioId, "0", "10");
        retrievedData.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel>
                    response) {
                if(response == null || response.body() == null){
                    Log.d("ERROR","Response or Response Body is null. \n Recheck Your PHP code.");                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RETROFIT", "response : " + response.body().getTreatments());
                    currentTreatment = response.body().getTreatments();
                    for (int i = 0; i < currentTreatment.size(); i++) {
                        treatmentNameList.add(currentTreatment.get(i).getName());
                    }
                    spinner.setItems(treatmentNameList);
                }else if (!response.isSuccessful()) {
                    Log.d("UNSUCCESSFUL",
                            "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                    " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+" " +
                                    " \n 3. Most probably the problem is with your PHP Code.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
            }
        });



    }

}