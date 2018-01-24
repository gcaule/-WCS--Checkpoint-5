package com.wcs.germain.wcstravel;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FDB";

    boolean isDepartureOK = false;
    boolean isArrivalOK = false;

    boolean isDepartureDateOK = false;
    boolean isArrivalDateOK = false;
    boolean isSpeedLessThan88MPH = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase connection test
        //*
        //**
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("checkpoint5/students/gcaule");

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
        final EditText arrivalDateValue = findViewById(R.id.searchReturnDateValue);

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
                String myFormat = "dd/MM/yyyy";
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


        // Date Picker for return
        final DatePickerDialog.OnDateSetListener returnDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                arrivalDateValue.setText(sdf.format(myCalendar.getTime()));
                isArrivalDateOK = true;
            }
        };

        arrivalDateValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog, returnDateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // Populate the spinners for flight selection
        DatabaseReference airports = database.getReference("checkpoint5");
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


        //Let's rock.
        searchFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Spinner departureSpinner = findViewById(R.id.departureValue);
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

                if (!isDepartureOK) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.departure_error), Toast.LENGTH_SHORT).show();
                }


                Spinner arrivalSpinner = findViewById(R.id.arrivalValue);
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

                if (!isArrivalOK) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.arrival_error), Toast.LENGTH_SHORT).show();
                }


                if (!isDepartureDateOK) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.departure_date_error), Toast.LENGTH_SHORT).show();
                }


                if (!isArrivalDateOK) {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.arrival_date_error), Toast.LENGTH_SHORT).show();
                }


                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
                try {
                    Date departureDate = sdf.parse(departureDateValue.getText().toString());
                    Date arrivalDate = sdf.parse(arrivalDateValue.getText().toString());

                    if (departureDate.after(arrivalDate)) {
                        Toast.makeText(MainActivity.this,
                                getString(R.string.slower_than_88_MPH), Toast.LENGTH_SHORT).show();
                    } else {
                        isSpeedLessThan88MPH = true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (isDepartureOK && isArrivalOK && isDepartureDateOK
                        && isArrivalDateOK && isSpeedLessThan88MPH) {
                    Toast.makeText(MainActivity.this,
                            "Todo match !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
