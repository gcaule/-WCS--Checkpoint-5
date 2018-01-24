package com.wcs.germain.wcstravel;

/**
 * Created by apprenti on 24/01/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.MyViewHolder> {

    private Context context;
    private List<TravelModel> travelModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView airline, departureDate, arrivalDate, price;
        public Button convertPrice;

        public MyViewHolder(View view) {
            super(view);
            airline = view.findViewById(R.id.airlineValue);
            departureDate = view.findViewById(R.id.resultDepartureDateValue);
            arrivalDate = view.findViewById(R.id.resultArrivalDateValue);
            price = view.findViewById(R.id.resultPriceValue);

            convertPrice = view.findViewById(R.id.convertPrice);

        }
    }


    public FlightsAdapter(List<TravelModel> travelModelList, Context context) {
        this.travelModelList = travelModelList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final TravelModel travelModel = travelModelList.get(position);
        holder.airline.setText(travelModel.getAirline());
        holder.departureDate.setText(travelModel.getDeparture_date());
        holder.arrivalDate.setText(travelModel.getReturn_date());
        holder.price.setText(context.getResources().getString(R.string.price_value_dollars,
                String.valueOf(travelModel.getPrice())));

        //Convert price on click
        holder.convertPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (context.getResources().getString(R.string.price_value_dollars,
                        String.valueOf(travelModel.getPrice())).equals(holder.price.getText())) {
                    Double dollarsPrice = Double.parseDouble(travelModel.getPrice());
                    Double eurosPrice = dollarsPrice * 0.717978173;

                    eurosPrice = Math.floor(eurosPrice * 100) / 100;

                    holder.price.setText(context.getResources().getString(R.string.price_value_euros,
                            String.valueOf(eurosPrice)));

                } else {

                    holder.price.setText(context.getResources().getString(R.string.price_value_dollars,
                            String.valueOf(travelModel.getPrice())));

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return travelModelList.size();
    }


}
