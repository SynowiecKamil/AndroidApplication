package synowiec.application.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import synowiec.application.R;
import synowiec.application.model.Appointment;
import synowiec.application.patient.PhysioDetailActivity;

public class MyAppointmentsAdapter extends RecyclerView.Adapter<MyAppointmentsAdapter.ViewHolder> {

    private Context c;
    private int[] mMaterialColors;
    private List<Appointment> appointments;
    public String searchString = "";
    private int time;
    private SimpleDateFormat displayDateFormat;

    /**
     * 1. Hold all the widgets which will be recycled and reference them.
     * 2. Implement click event.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView physio_text_view, date_text_view, time_text_view, cabinet_text_view, treatment_text_view;
        CircleImageView profile_image;
        private MaterialLetterIcon mIcon;
        private ItemClickListener itemClickListener;
        /**
         reference widgets
         */
        ViewHolder(View itemView) {
            super(itemView);
        //    mIcon = itemView.findViewById(R.id.mMaterialLetterIcon);
        //    profile_image = itemView.findViewById(R.id.profile_image);
            physio_text_view = itemView.findViewById(R.id.physio_text_view);
            treatment_text_view = itemView.findViewById(R.id.treatment_text_view);
            date_text_view = itemView.findViewById(R.id.date_text_view);
            time_text_view = itemView.findViewById(R.id.time_text_view);
            cabinet_text_view = itemView.findViewById(R.id.cabinet_text_view);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(this.getLayoutPosition());
        }

        void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
    }


    public MyAppointmentsAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }
    /**
     inflating model.xml, layout into a view object and set it's background color
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.c=parent.getContext();
        //mMaterialColors = c.getResources().getIntArray(R.values.colors);
        View view = LayoutInflater.from(c).inflate(R.layout.appointment_model, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Appointment p = appointments.get(position);

        holder.physio_text_view.setText(p.getPhysio_id());
        holder.treatment_text_view.setText(p.getTreatment());
        String date = p.getDate().replace("_", "/");
        holder.date_text_view.setText(date);
        time = (Integer.parseInt(p.getTime()));
        holder.time_text_view.setText(Utils.convertTimeSlotToString(time));
        holder.cabinet_text_view.setText(p.getPlace());
//        Picasso.get().load(p.getPhoto())
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .networkPolicy(NetworkPolicy.NO_CACHE)
//                .into(holder.profile_image);
 //       holder.profile_image.setImageBitmap(getBitmapFromURL(p.getPhoto()));
//        holder.mIcon.setInitials(true);
 //       holder.mIcon.setInitialsNumber(2);
//        holder.mIcon.setLetterSize(25);
//        holder.mIcon.setShapeColor(mMaterialColors[new Random().nextInt(
//            mMaterialColors.length)]);


//        String name = p.getName().toLowerCase(Locale.getDefault());
//        String surname = p.getSurname().toLowerCase(Locale.getDefault());
//        String cabinet = p.getCabinet().toLowerCase(Locale.getDefault());

//        //highlight name text while searching
//        if (name.contains(searchString) && !(searchString.isEmpty())) {
//            int startPos = name.indexOf(searchString);
//            int endPos = startPos + searchString.length();
//
//            Spannable spanString = Spannable.Factory.getInstance().
//                    newSpannable(holder.nameTxt.getText());
//            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            holder.nameTxt.setText(spanString);
//        }
//        //      highlight email text while searching
//        if (surname.contains(searchString) && !(searchString.isEmpty())) {
//
//            int startPos = surname.indexOf(searchString);
//            int endPos = startPos + searchString.length();
//
//            Spannable spanString = Spannable.Factory.getInstance().
//                    newSpannable(holder.surnameTxt.getText());
//            spanString.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            holder.surnameTxt.setText(spanString);
//
//        }
//        //      highlight cabinet text while searching
//        if (cabinet.contains(searchString) && !(searchString.isEmpty())) {
//
//            int startPos = cabinet.indexOf(searchString);
//            int endPos = startPos + searchString.length();
//
//            Spannable spanString = Spannable.Factory.getInstance().
//                    newSpannable(holder.galaxyTxt.getText());
//            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            holder.galaxyTxt.setText(spanString);
//
//        }

        //open detail activity when clicked
//        holder.setItemClickListener(pos -> Utils.sendPhysiotherapistToActivity(c, p,
//                PhysioDetailActivity.class));
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
