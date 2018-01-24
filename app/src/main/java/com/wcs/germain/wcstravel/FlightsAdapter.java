package com.wcs.germain.wcstravel;

/**
 * Created by apprenti on 24/01/18.
 */

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FlightsAdapter extends RecyclerView.Adapter<FlightsAdapter.MyViewHolder> {

    private List<TravelModel> travelModelList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView airline, departureDate, arrivalDate, price;

        public MyViewHolder(View view) {
            super(view);
            airline = view.findViewById(R.id.airlineValue);
            departureDate = view.findViewById(R.id.resultDepartureDateValue);
            arrivalDate = view.findViewById(R.id.resultArrivalDateValue);
            price = view.findViewById(R.id.resultPriceValue);
        }
    }


    public FlightsAdapter(List<TravelModel> travelModelList) {
        this.travelModelList = travelModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TravelModel travelModel = travelModelList.get(position);
        holder.airline.setText(travelModel.getAirline());
        holder.departureDate.setText(travelModel.getDeparture_date());
        holder.arrivalDate.setText(travelModel.getReturn_date());

        //Resources resources = Resources.getSystem();
        //String priceValue = resources.getString(R.string.price_value_dollars, String.valueOf(travelModel.getPrice()));
        holder.price.setText(travelModel.getPrice());
    }

    @Override
    public int getItemCount() {
        return travelModelList.size();
    }
}
