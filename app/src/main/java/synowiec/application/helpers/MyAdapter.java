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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import synowiec.application.R;
import synowiec.application.model.PhysioTreatment;
import synowiec.application.model.Physiotherapist;
import synowiec.application.patient.PhysioDetailActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context c;
    private int[] mMaterialColors;
    private List<Physiotherapist> physiotherapists;
    public String searchString = "";

    /**
     * 1. Hold all the widgets which will be recycled and reference them.
     * 2. Implement click event.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView nameTxt, mEmailTxt, galaxyTxt;
        CircleImageView profile_image;
        private MaterialLetterIcon mIcon;
        private ItemClickListener itemClickListener;
        /**
         reference widgets
         */
        ViewHolder(View itemView) {
            super(itemView);
        //    mIcon = itemView.findViewById(R.id.mMaterialLetterIcon);
            profile_image = itemView.findViewById(R.id.profile_image);
            nameTxt = itemView.findViewById(R.id.mNameTxt);
            mEmailTxt = itemView.findViewById(R.id.mEmailTxt);
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


    public MyAdapter(ArrayList<Physiotherapist> physiotherapists) {
        this.physiotherapists = physiotherapists;
    }
    /**
     inflating model.xml, layout into a view object and set it's background color
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.c=parent.getContext();
        //mMaterialColors = c.getResources().getIntArray(R.values.colors);
        View view = LayoutInflater.from(c).inflate(R.layout.model, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Physiotherapist p = physiotherapists.get(position);


        holder.nameTxt.setText(p.getName());
        holder.mEmailTxt.setText(p.getEmail());
        holder.galaxyTxt.setText(p.getPassword());
        Picasso.get().load(p.getPhoto())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(holder.profile_image);
 //       holder.profile_image.setImageBitmap(getBitmapFromURL(p.getPhoto()));
//        holder.mIcon.setInitials(true);
 //       holder.mIcon.setInitialsNumber(2);
//        holder.mIcon.setLetterSize(25);
//        holder.mIcon.setShapeColor(mMaterialColors[new Random().nextInt(
//            mMaterialColors.length)]);
        if(!p.getName().isEmpty() && p.getName().length()>1){
       //     holder.mIcon.setLetter(p.getName());

        }

        String name = p.getName().toLowerCase(Locale.getDefault());
        String email = p.getEmail().toLowerCase(Locale.getDefault());

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
        if (email.contains(searchString) && !(searchString.isEmpty())) {

            int startPos = email.indexOf(searchString);
            int endPos = startPos + searchString.length();

            Spannable spanString = Spannable.Factory.getInstance().
                    newSpannable(holder.mEmailTxt.getText());
            spanString.setSpan(new ForegroundColorSpan(Color.BLUE), startPos, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.mEmailTxt.setText(spanString);
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
