package synowiec.application.Controller.Helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Model.Treatment;

public class TreatmentDialog extends AppCompatDialogFragment{
    private Spinner spinner;
    private EditText priceET;
    private DialogListener listener;
    private String id, treatmentName, myResponseCode ="0", action, dialogTitle = "Wybierz zabieg";
    private double treatmentPrice;
    private Treatment treatment;
    private ProgressBar mProgressBar;
    private Context c;

    public TreatmentDialog(String id, Context c, String action) {
        this.id = id;
        this.c = c;
        this.action = action;
    }

    public TreatmentDialog(String id, Context c, Treatment treatment, String action) {
        this.id = id;
        this.c = c;
        this.treatment = treatment;
        this.action = action;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.spearmint)));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_treatment_dialog, null);
        spinner = view.findViewById(R.id.spinner);
        if(action.equals("edit")) {
            dialogTitle = "Edytuj zabieg " + treatment.getName();
            spinner.setVisibility(View.GONE);}

        builder.setView(view)
                .setTitle(dialogTitle)
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
                        if(!priceET.getText().toString().equals("")) {
                            priceET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
                            treatmentPrice = Double.parseDouble(priceET.getText().toString());
                            if(action.equals("insert"))  insertTreatment(id, treatmentName, treatmentPrice);
                            else if(action.equals("edit")) {
                                spinner.setVisibility(View.GONE);
                                editTreatment(id,treatment.getName(),treatmentPrice);
                            }
                        } else Utils.show(getContext(), "Wprowadź cenę zabiegu!");
                    }
                });

        priceET = view.findViewById(R.id.price);
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

    private void insertTreatment(String sID, String sName, double sPrice) {

            RestApi api = Utils.getClient().create(RestApi.class);
            Call<ResponseModel> insertData = api.insertTreatment("INSERT_TREATMENT", sName, sID, sPrice);

            insertData.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {

                    if(response == null || response.body() == null || response.body().getCode()==null){
                        System.out.println("Response or Response Body is null. \n Recheck Your PHP code.");
                        return;
                    }

                    Log.d("RETROFIT", "response : " + response.body().toString());
                    myResponseCode = response.body().getCode();

                    if (myResponseCode.equals("1")) {
                        System.out.printf("0. SUCCESS: \n 1. Data inserted Successfully. \n 2. ResponseCode: "  +myResponseCode);
                        Utils.show(c, "Dodano zabieg!");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                listener.onCloseDialog();
                                Utils.hideProgressBar(mProgressBar);
                            }
                        }, 500);
                    } else if (myResponseCode.equalsIgnoreCase("2")) {
                        Utils.show(c, "Wybrany zabieg jest już na liście!");
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
                    Utils.showInfoDialog((AppCompatActivity) getActivity(), "FAILURE",
                            "FAILURE THROWN DURING INSERT."+
                                    " ERROR Message: " + t.getMessage());
                }
            });

    }

    private void editTreatment(String id, String name, double price) {
        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> update = api.updateTreatment("UPDATE_TREATMENT",name, id, price);
        update.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    Utils.show(c,"ERROR, Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "Response: " + response.body().getResult());

                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    Utils.show(c, "Pomyślnie zmieniono cenę zabiegu. ");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            listener.onCloseDialog();
                            Utils.hideProgressBar(mProgressBar);
                        }
                    }, 500);

                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    Utils.show(c, "UNSUCCESSFUL,  However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL"+
                            " \n 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+
                            myResponseCode+ " \n 3. Most probably the problem is with your PHP Code.");
                }else if (myResponseCode.equalsIgnoreCase("3")) {
                    Utils.show(c, "NO MYSQL CONNECTION, Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR THROWN DURING UPDATE: " + t.getMessage());
                Utils.show(c, "FAILURE THROWN, ERROR during DELETE attempt. Message: " + t.getMessage());
            }
        });
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern= Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }
}
