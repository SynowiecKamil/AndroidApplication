package synowiec.application.Fragment;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.helpers.Utils;
import synowiec.application.model.Physiotherapist;
import synowiec.application.model.Treatment;
import synowiec.application.patient.PhysioDetailActivity;

import static synowiec.application.helpers.Utils.showInfoDialog;

public class ReservationTreatmentFragment extends Fragment {

    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    Unbinder unbinder;
    private String treatment;
    private Physiotherapist receivedPhysiotherapist;
    private String receivedPhysioID;
    private ArrayList<String> treatmentNameList = new ArrayList<>();

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
                    List<Treatment> currentTreatment = response.body().getTreatments();
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