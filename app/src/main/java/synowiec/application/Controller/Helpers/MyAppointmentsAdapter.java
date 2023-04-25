package synowiec.application.Controller.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.R;
import synowiec.application.Controller.ResponseModel;
import synowiec.application.Controller.RestApi;
import synowiec.application.Model.Appointment;

import static synowiec.application.Controller.Helpers.Utils.show;

public class MyAppointmentsAdapter extends RecyclerView.Adapter<MyAppointmentsAdapter.ViewHolder> {

    private Context c, context;
    private List<Appointment> appointments;
    private String user;
    private int time;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy HH");

    public MyAppointmentsAdapter(List<Appointment> appointments, String user, Context context) {
        this.appointments = appointments;
        this.user = user;
        this.context = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView patient_text_view, physio_text_view, date_text_view, time_text_view, cabinet_text_view, treatment_text_view;
        private ImageView closeImageView;
        private LinearLayout patientLinearLayout, physioLinearLayout;
        private Button btn_cancel;
        private ItemClickListener itemClickListener;

        ViewHolder(View itemView) {
            super(itemView);
            patientLinearLayout = itemView.findViewById(R.id.patient_linear_layout);
            physioLinearLayout = itemView.findViewById(R.id.physio_linear_layout);
            patient_text_view = itemView.findViewById(R.id.patient_text_view);
            physio_text_view = itemView.findViewById(R.id.physio_text_view);
            treatment_text_view = itemView.findViewById(R.id.treatment_text_view);
            date_text_view = itemView.findViewById(R.id.date_text_view);
            time_text_view = itemView.findViewById(R.id.time_text_view);
            cabinet_text_view = itemView.findViewById(R.id.cabinet_text_view);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);
            closeImageView = itemView.findViewById(R.id.imageView_close);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
        //    this.itemClickListener.onItemClick(this.getLayoutPosition());
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }

    /**
     inflating model.xml, layout into a view object and set it's background color
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.c=parent.getContext();
        View view = LayoutInflater.from(c).inflate(R.layout.layout_appointment, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Appointment p = appointments.get(position);
        if(user.equals("patient")){
            holder.physioLinearLayout.setVisibility(View.VISIBLE);
            holder.patientLinearLayout.setVisibility(View.GONE);
            holder.btn_cancel.setText("SZCZEGÓŁY");
        }else if(user.equals("physio")){
            holder.physioLinearLayout.setVisibility(View.GONE);
            holder.patientLinearLayout.setVisibility(View.VISIBLE);
        }
        holder.physio_text_view.setText(p.getPhysioId());
        holder.patient_text_view.setText(p.getPatientId());
        holder.treatment_text_view.setText(p.getTreatment());
        String date = p.getDate().replace("_", "/");
        holder.date_text_view.setText(date);
        time = (Integer.parseInt(p.getTime()));
        holder.time_text_view.setText(Utils.convertTimeSlotToString(time));
        String address = p.getCity()+",  " + p.getAddress();
        holder.cabinet_text_view.setText(address);
       // holder.setItemClickListener(pos -> initializeDialogMap(getLatLng(address), p, position));

        holder.btn_cancel.setOnClickListener(pos -> {
            if (user.equals("patient")){
                initializeDialogMap(getLatLng(address, c), p, position);
            }
            else {
            new LovelyStandardDialog(c, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                    //       .setTopColorRes(R.color.indigo)
                    //       .setButtonsColorRes(R.color.darkDeepOrange)
                    .setMessage("Czy na pewno chcesz anulować wizytę?")
                    .setPositiveButton("NIE", x -> {})
                    .setButtonsColor(Color.BLACK)
                    .setNegativeButton("TAK", x -> {
                        removeAppointment(p.getId());
                        appointments.remove(appointments.get(position));
                        notifyDataSetChanged();
                    })
                    .show();
            }
            System.out.println("Button click listener");
        });

        Date strDate = null;
        try {
            strDate = simpleDateFormat.parse(p.getDate()+" "+Utils.convertTimeSlotToString(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (new Date().before(strDate)) {
            holder.btn_cancel.setVisibility(View.VISIBLE);
        }
        else if(new Date().after(strDate)){
            holder.btn_cancel.setVisibility(View.GONE);
        }
    }

    private void removeAppointment(String id) {

        RestApi api = Utils.getClient().create(RestApi.class);
        Call<ResponseModel> del = api.deleteAppointment("DELETE", id);

        del.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if(response == null || response.body() == null || response.body().getCode()==null){
                    show(c,"ERROR, Response or Response Body is null. \n Recheck Your PHP code.");
                    return;
                }
                Log.d("RETROFIT", "DELETE RESPONSE: " + response.body());
                String myResponseCode = response.body().getCode();

                if (myResponseCode.equalsIgnoreCase("1")) {
                    System.out.println(response.body().getMessage());
                    show(c, "Pomyślnie anulowano wizytę");

                } else if (myResponseCode.equalsIgnoreCase("2")) {
                    show(c, "UNSUCCESSFUL,  However Good Response. \n 1. CONNECTION TO SERVER WAS SUCCESSFUL"+
                                    " \n 2. WE ATTEMPTED POSTING DATA BUT ENCOUNTERED ResponseCode: "+
                                    myResponseCode+ " \n 3. Most probably the problem is with your PHP Code.");
                }else if (myResponseCode.equalsIgnoreCase("3")) {
                    show(c, "NO MYSQL CONNECTION, Your PHP Code is unable to connect to mysql database. Make sure you have supplied correct database credentials.");
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.d("RETROFIT", "ERROR: " + t.getMessage());
                show(c, "FAILURE THROWN, ERROR during DELETE attempt. Message: " + t.getMessage());
            }
        });
    }

    private void initializeDialogMap(LatLng pos, Appointment p, int position){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_appointment_details_dialog);////your custom content

        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(context);

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                googleMap.addMarker(new MarkerOptions().position(pos).title("Lokalizacja gabinetu"));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        });
        ImageView closeImageView = (ImageView) dialog.findViewById(R.id.imageView_close);

        closeImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        Button btnCancelVisit = (Button) dialog.findViewById(R.id.btn_cancel_visit);
        btnCancelVisit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new LovelyStandardDialog(c, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        //       .setTopColorRes(R.color.indigo)
                        //       .setButtonsColorRes(R.color.darkDeepOrange)
                        .setMessage("Czy na pewno chcesz anulować wizytę?")
                        .setPositiveButton("NIE", x -> {})
                        .setButtonsColor(Color.BLACK)
                        .setNegativeButton("TAK", x -> {
                            removeAppointment(p.getId());
                            appointments.remove(appointments.get(position));
                            notifyDataSetChanged();
                            dialog.dismiss();
                        })
                        .show();
            }
        });
        dialog.show();
    }


    public LatLng getLatLng(String myAddress, Context context){
        Geocoder geocoder = new Geocoder(context);
        LatLng latLng = null;
        try {
            List<Address> addressList = geocoder.getFromLocationName(myAddress, 1);
            if(addressList.size()>0){
                double lat = (double)Math.round(addressList.get(0).getLatitude() * 10000d) / 10000d;
                double lng = (double)Math.round(addressList.get(0).getLongitude() * 10000d) / 10000d;
                latLng = new LatLng(lat,lng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    @Override
    public int getItemCount() {
        return appointments.size();
    }
    interface ItemClickListener {
        void onItemClick(int pos);
    }
}
