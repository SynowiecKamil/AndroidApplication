package synowiec.application.Controller.Helpers;

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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import synowiec.application.R;
import synowiec.application.Model.Physiotherapist;
import synowiec.application.Controller.PatientActivities.PhysioDetailActivity;

public class MyPhysiotherapistAdapter extends RecyclerView.Adapter<MyPhysiotherapistAdapter.ViewHolder> {

    private Context c;
    private List<Physiotherapist> physiotherapists;
    public String searchString = "";

    /**
     * 1. Hold all the widgets which will be recycled and reference them.
     * 2. Implement click event.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView nameTxt, surnameTxt, galaxyTxt;
        CircleImageView profile_image;
        private ItemClickListener itemClickListener;
        /**
         reference widgets
         */
        ViewHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            nameTxt = itemView.findViewById(R.id.mNameTxt);
            surnameTxt = itemView.findViewById(R.id.mSurnameTxt);
            galaxyTxt = itemView.findViewById(R.id.mGalaxyTxt);
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


    public MyPhysiotherapistAdapter(ArrayList<Physiotherapist> physiotherapists) {
        this.physiotherapists = physiotherapists;
    }
    /**
     inflating model.xml, layout into a view object and set it's background color
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.c=parent.getContext();
        View view = LayoutInflater.from(c).inflate(R.layout.layout_physio, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Physiotherapist p = physiotherapists.get(position);

        System.out.println(p.getId()+" "+p.getName()+" " + p.getSurname()+ " "+ p.getDescription());
        holder.nameTxt.setText(p.getName());
        holder.surnameTxt.setText(p.getSurname());
        holder.galaxyTxt.setText(p.getCity());
        Picasso.get().load(p.getPhoto())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.profile_image);
        if(!p.getName().isEmpty() && p.getName().length()>1){
       //     holder.mIcon.setLetter(p.getName());

        }

        String name = p.getName().toLowerCase(Locale.getDefault());
        String surname = p.getSurname().toLowerCase(Locale.getDefault());
        String cabinet = p.getCity().toLowerCase(Locale.getDefault());

        //highlight name text while searching
        if (name.contains(searchString) && !(searchString.isEmpty())) {
            int startPos = name.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().
                    newSpannable(holder.nameTxt.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.nameTxt.setText(spanString);
        }
        //      highlight email text while searching
        if (surname.contains(searchString) && !(searchString.isEmpty())) {

            int startPos = surname.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().
                    newSpannable(holder.surnameTxt.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.surnameTxt.setText(spanString);

        }
        //      highlight cabinet text while searching
        if (cabinet.contains(searchString) && !(searchString.isEmpty())) {

            int startPos = cabinet.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().
                    newSpannable(holder.galaxyTxt.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.RED), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.galaxyTxt.setText(spanString);

        }

        //open detail activity when clicked
        holder.setItemClickListener(pos -> Utils.sendPhysiotherapistToActivity(c, p,
                PhysioDetailActivity.class));
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
        return physiotherapists.size();
    }
    interface ItemClickListener {
        void onItemClick(int pos);
    }
}
