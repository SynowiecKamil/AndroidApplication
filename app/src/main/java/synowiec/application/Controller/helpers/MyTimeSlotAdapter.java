package synowiec.application.Controller.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import synowiec.application.R;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<String> timeSlotList;
    List<CardView> cardViewList;
    int firstHourPosition;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public MyTimeSlotAdapter(Context context, List<String> timeSlotList, int firstHourPosition) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        cardViewList = new ArrayList<>();
        this.localBroadcastManager = LocalBroadcastManager.getInstance(context);
        this.firstHourPosition = firstHourPosition;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.txt_time_slot.setText(new StringBuilder(Utils.convertTimeSlotToString(position + firstHourPosition)).toString());

            myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            myViewHolder.txt_time_slot_description.setText("Dostępne");
            myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));

            for (String slotValue : timeSlotList) {
                int slot = Integer.parseInt(slotValue);
                if (slot == position) {
                    myViewHolder.card_time_slot.setTag(Utils.DISABLE_TAG);
                    myViewHolder.card_time_slot.setClickable(false);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    myViewHolder.txt_time_slot_description.setText("Niedostępne");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
                }
            }
        if(!cardViewList.contains(myViewHolder.card_time_slot))
            cardViewList.add(myViewHolder.card_time_slot);


            myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int pos) {
                    for(CardView cardView:cardViewList){
                        if(cardView.getTag()== null) {
                            cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                        }
                    }
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(R.color.tealgreen));

                    Intent intent = new Intent(Utils.KEY_ENABLE_BUTTON_NEXT);
                    intent.putExtra(Utils.KEY_TIME_SLOT, pos);
                    intent.putExtra(Utils.KEY_STEP, 1);
                    localBroadcastManager.sendBroadcast(intent);
                    System.out.println(pos);
                }
            });


    }



    @Override
    public int getItemCount() {
        return Utils.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView txt_time_slot, txt_time_slot_description;
        CardView card_time_slot;



        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;
        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            card_time_slot = (CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView)itemView.findViewById(R.id.txt_time_slot_description);
            itemView.setOnClickListener(this);
        }
        @Override
         public void onClick(View view){
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
         }
    }


}
