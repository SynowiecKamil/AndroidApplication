package synowiec.application.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.R;
import synowiec.application.SessionManager;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.patient.PatientDashboardActivity;
import synowiec.application.patient.PatientRegisterActivity;
import synowiec.application.physio.PhysioDashboardActivity;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;

public class TreatmentDialog extends AppCompatDialogFragment{
    private Spinner spinner;
    private DialogListener listener;
    private String id, treatmentName;
    private ProgressBar mProgressBar;

    public TreatmentDialog(String id) {
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Wybierz zabieg")
                .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onCloseDialog();
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        treatmentName = spinner.getSelectedItem().toString();
                        insertTreatment(id, treatmentName);
                        Utils.showProgressBar(mProgressBar);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                listener.onCloseDialog();
                                hideProgressBar(mProgressBar);
                            }
                        }, 500);

                    }
                });

        spinner = view.findViewById(R.id.spinner);
        mProgressBar = view.findViewById(R.id.mProgressBarSave);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement dialog listener");
        }
    }

    public interface DialogListener{
        void onCloseDialog();
    }

    private void insertTreatment(String sID, String sName) {

            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> insertData = api.insertTreatment("INSERT_TREATMENT", sName, sID);

            insertData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {

                    if(response == null || response.body() == null || response.body().getCode()==null){
                        System.out.println("Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }

                    Log.d("RETROFIT", "response : " + response.body().toString());
                    String myResponseCode = response.body().getCode();

                    if (myResponseCode.equals("1")) {
                        System.out.printf("0. SUCCESS: \n 1. Data inserted Successfully. \n 2. ResponseCode: "  +myResponseCode);
                        Utils.show(getActivity(), "Dodano zabieg!");
                    } else if (myResponseCode.equalsIgnoreCase("2")) {
                        System.out.println("UNSUCCESSFUL"+
                                "However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL \n 2. WE"+
                                        " ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+myResponseCode+
                                        " \n 3. Most probably the problem is with your PHP Code.");
                    }else if (myResponseCode.equalsIgnoreCase("3")) {
                        System.out.println("NO MYSQL CONNECTION"+" Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                    }
                }
                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.d("RETROFIT", "ERROR: " + t.getMessage());
                    showInfoDialog((AppCompatActivity) getActivity(), "FAILURE",
                            "FAILURE THROWN DURING INSERT."+
                                    " ERROR Message: " + t.getMessage());
                }
            });

    }
}
