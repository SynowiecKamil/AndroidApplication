package synowiec.application.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import synowiec.application.R;

public class MyCabinetAdapter extends RecyclerView.Adapter<MyCabinetAdapter.PredictionHolder> implements Filterable {

    private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();

    private Context mContext;
    private CharacterStyle STYLE_BOLD;
    private CharacterStyle STYLE_NORMAL;
    private final PlacesClient placesClient;
    private ClickListener clickListener;

    public MyCabinetAdapter(Context context) {
        mContext = context;
        STYLE_BOLD = new StyleSpan(Typeface.BOLD);
        STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
    }


    @Override
    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint){
                FilterResults results = new FilterResults();
                if(constraint != null){
                    mResultList = myPlaceData(constraint);
                    if(mResultList != null){
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results){
                if(results != null && results.count > 0){
                    notifyDataSetChanged();
                }
            }
        };
    }

    @NonNull
    @Override
    public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.place_recycler_item_layout, viewGroup, false);
        return new PredictionHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
    mPredictionHolder.address.setText(mResultList.get(i).address);
    mPredictionHolder.area.setText(mResultList.get(i).area);
    }

    private ArrayList<PlaceAutocomplete> myPlaceData(CharSequence constraint){
        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

        try{
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        }catch (ExecutionException | InterruptedException | TimeoutException e){
            e.printStackTrace();
        }

        if (autocompletePredictions.isSuccessful()){
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if(findAutocompletePredictionsResponse != null)
                for(AutocompletePrediction prediction: findAutocompletePredictionsResponse.getAutocompletePredictions()){
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                }
            return resultList;
        }else{
            return resultList;
        }

    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public PlaceAutocomplete getItem(int position){return mResultList.get(position);}

    public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView address, area;
        private LinearLayout mRow;

        PredictionHolder(View itemView){
            super(itemView);
            area = itemView.findViewById(R.id.place_area);
            address = itemView.findViewById(R.id.place_address);
            mRow = itemView.findViewById(R.id.place_item_view);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            PlaceAutocomplete item = mResultList.get(getAdapterPosition());
            if(v.getId()==R.id.place_item_view){
                String placeId = String.valueOf(item.placeId);

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse response) {
                        Place place = response.getPlace();
                        clickListener.click(place);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(e instanceof ApiException){
                            Toast.makeText(mContext, e.getMessage()+ "", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public class PlaceAutocomplete {
        public CharSequence placeId;
        public CharSequence address, area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address){
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @Override
        public String toString() { return area.toString(); }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener{
        void click(Place place);
    }
}
