package synowiec.application.helpers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import synowiec.application.R;
import synowiec.application.controller.ResponseModel;
import synowiec.application.controller.RestApi;
import synowiec.application.model.Appointment;
import synowiec.application.model.Treatment;
import synowiec.application.physio.PhysioDashboardActivity;

import static synowiec.application.helpers.Utils.hideProgressBar;
import static synowiec.application.helpers.Utils.show;
import static synowiec.application.helpers.Utils.showInfoDialog;

public class MyTreatmentsAdapter extends RecyclerView.Adapter<MyTreatmentsAdapter.ViewHolder>{

    private Context c, context;
    private int[] mMaterialColors;
    private List<Treatment> currentTreatment;
    private String user;
    private ItemClickListener itemClickListener;
    private boolean editMode;

    public MyTreatmentsAdapter(List<Treatment> currentTreatment, Context context,boolean editMode, ItemClickListener itemClickListener) {
        this.currentTreatment = currentTreatment;
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.editMode = editMode;
    }


    /**
     * 1. Hold all the widgets which will be recycled and reference them.
     * 2. Implement click event.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView  price_text_view, treatment_text_view;
        private EditText price_edit_text;
        private Button btn_edit_treatment;
        private ItemClickListener itemClickListener;
        private LinearLayout treatment_linear_layout;
        /**
         reference widgets
         */
        ViewHolder(View itemView) {
            super(itemView);
            treatment_text_view = itemView.findViewById(R.id.treatment_text_view);
            price_text_view = itemView.findViewById(R.id.price_text_view);
            treatment_linear_layout = itemView.findViewById(R.id.treatment_linear_layout);

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
        View view = LayoutInflater.from(c).inflate(R.layout.treatment_model, parent, false);
        MyTreatmentsAdapter.ViewHolder vh = new MyTreatmentsAdapter.ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull MyTreatmentsAdapter.ViewHolder holder, int position) {

        final Treatment p = currentTreatment.get(position);

        holder.treatment_text_view.setText(p.getName());
        holder.price_text_view.setText(p.getId());
        holder.itemView.setOnClickListener(view -> {
                itemClickListener.onItemClick(currentTreatment.get(position), view, position);
        });

    }


    @Override
    public int getItemCount() {
        return currentTreatment.size();
    }

    public interface ItemClickListener {
        void onItemClick(Treatment treatment, View var2, int var3);
    }

}