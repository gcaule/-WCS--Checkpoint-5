package com.wcs.germain.wcstravel;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;

    private static final String TAG = "FDB";

    private List<TravelModel> mTravelModelList = new ArrayList<>();

    boolean isDepartureOK = false;
    boolean isArrivalOK = false;

    boolean isDepartureDateOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase connection test
        //*
        //**
        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = mDatabase.getReference("checkpoint5/students/gcaule");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        //**
        //*


        final EditText departureDateValue = findViewById(R.id.searchDepartureDateValue);

        final Button searchFlight = findViewById(R.id.searchButton);

        final Calendar myCalendar = Calendar.getInstance();


        // Date Picker for departure
        final DatePickerDialog.OnDateSetListener departureDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                departureDateValue.setText(sdf.format(myCalendar.getTime()));
                isDepartureDateOK = true;
            }
        };

        departureDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog, departureDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // Populate the spinners for flight selection
        DatabaseReference airports = mDatabase.getReference("checkpoint5");
        airports.child("airports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> airports = new ArrayList<String>();

                for (DataSnapshot airportSnapshot : dataSnapshot.getChildren()) {
                    String airportName = airportSnapshot.child("label").getValue(String.class);
                    airports.add(airportName);
                }

                Spinner departureSpinner = findViewById(R.id.departureValue);
                ArrayAdapter<String> departuresAdapter = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.spinner_row, airports);
                departuresAdapter.setDropDownViewResource(R.layout.spinner_row);
                departureSpinner.setAdapter(new NothingSelectedSpinnerAdapter(departuresAdapter,
                        R.layout.departure_empty_spinner_row, MainActivity.this));

                Spinner arrivalSpinner = findViewById(R.id.arrivalValue);
                ArrayAdapter<String> arrivalsAdapter = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.spinner_row, airports);
                arrivalsAdapter.setDropDownViewResource(R.layout.spinner_row);
                arrivalSpinner.setAdapter(new NothingSelectedSpinnerAdapter(arrivalsAdapter,
                        R.layout.arrival_empty_spinner_row, MainActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        final Spinner departureSpinner = findViewById(R.id.departureValue);
        departureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                if (position != 0)
                    isDepartureOK = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final Spinner arrivalSpinner = findViewById(R.id.arrivalValue);
        arrivalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,int position, long id) {
                if (position != 0)
                    isArrivalOK = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        //Let's rock.
        searchFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTravelModelList.clear();

                if (!isDepartureOK) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.departure_error), Toast.LENGTH_SHORT).show();
                }


                if (!isArrivalOK) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.arrival_error), Toast.LENGTH_SHORT).show();
                }


                if (!isDepartureDateOK) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.departure_date_error), Toast.LENGTH_SHORT).show();
                }


                if (isDepartureOK && isArrivalOK && isDepartureDateOK) {

                    String departureBuffer = departureSpinner.getSelectedItem().toString();
                    departureBuffer = departureBuffer.substring(departureBuffer.length() - 5);
                    departureBuffer = departureBuffer.substring(1, departureBuffer.length() - 1);

                    String arrivalBuffer = arrivalSpinner.getSelectedItem().toString();
                    arrivalBuffer = arrivalBuffer.substring(arrivalBuffer.length() - 5);
                    arrivalBuffer = arrivalBuffer.substring(1, arrivalBuffer.length() - 1);

                    Resources resources = getResources();
                    String searchedFlight = resources.getString(R.string.searched_flight, departureBuffer, arrivalBuffer);

                    final String flightDate = departureDateValue.getText().toString();

                    DatabaseReference travels = mDatabase.getReference("checkpoint5/travels");
                    travels.orderByChild("travel").equalTo(searchedFlight)
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                for (DataSnapshot travelSnapshot : dataSnapshot.getChildren()) {
                                    TravelModel travel = travelSnapshot.getValue(TravelModel.class);

                                    if (travel != null && travel.getDeparture_date().equals(flightDate)) {

                                        RecyclerView recyclerView = findViewById(R.id.flightsList);

                                        RecyclerView.Adapter adapter = new FlightsAdapter(mTravelModelList, getApplicationContext());
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                        recyclerView.setLayoutManager(mLayoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(adapter);

                                        mTravelModelList.add(travel);
                                        adapter.notifyDataSetChanged();

                                    }
                                }

                            } else {
                                Toast.makeText(MainActivity.this,
                                        getString(R.string.search_no_flights), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {}
                    });

                }

            }
        });

    }
}
