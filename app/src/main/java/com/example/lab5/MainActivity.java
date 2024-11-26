package com.example.lab5;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ArrayAdapter<String> adapter;
    private final List<String> currencyList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonFetch = findViewById(R.id.buttonFetch);
        ListView listViewRates = findViewById(R.id.listViewRates);
        progressBar = findViewById(R.id.progressBar); // Assuming you have a ProgressBar

        // Set up the adapter for the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencyList);
        listViewRates.setAdapter(adapter);

        // Button click listener
        buttonFetch.setOnClickListener(v -> {
            Log.d(TAG, "Fetch button clicked.");
            fetchData();
        });
    }

    private void fetchData() {
        Log.d(TAG, "Fetching data...");
        progressBar.setVisibility(ProgressBar.VISIBLE); // Show the progress bar while loading

        DataLoader dataLoader = new DataLoader(data -> {
            // Ensure this is run on the main thread to update UI components
            runOnUiThread(() -> {
                currencyList.clear();
                if (data != null) {
                    Log.d(TAG, "Data loaded successfully.");
                    currencyList.addAll(data);
                } else {
                    Log.e(TAG, "Data loading failed or returned null.");
                    Toast.makeText(this, "Failed to fetch rates. Please try again.", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(ProgressBar.INVISIBLE); // Hide the progress bar after data is loaded
            });
        });

        // Load the data (this will fetch all the rates from the ECB XML feed)
        dataLoader.loadData("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
    }
}