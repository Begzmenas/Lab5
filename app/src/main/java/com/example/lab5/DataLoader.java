package com.example.lab5;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DataLoader {
    public interface DataLoaderCallback {
        void onDataLoaded(List<String> data);
    }

    private final DataLoaderCallback callback;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public DataLoader(DataLoaderCallback callback) {
        this.callback = callback;
    }

    public void loadData(String url) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                Log.d("DataLoader", "Connecting to URL: " + url);
                URL urlObject = new URL(url);
                connection = (HttpURLConnection) urlObject.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);  // Set timeout
                connection.setReadTimeout(10000);  // Set read timeout
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d("DataLoader", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream inputStream = connection.getInputStream();
                         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        Log.d("DataLoader", "Raw Response Length: " + response.length());

                        if (response.length() > 0) {
                            try {
                                Log.d("DataLoader", "Parsing XML data...");
                                List<String> result = Parser.parseXML(response.toString());
                                handler.post(() -> callback.onDataLoaded(result));
                            } catch (Exception e) {
                                Log.e("DataLoader", "Error parsing XML", e);
                                handler.post(() -> callback.onDataLoaded(null));
                            }
                        } else {
                            Log.e("DataLoader", "Empty response received.");
                            handler.post(() -> callback.onDataLoaded(null));
                        }
                    }
                } else {
                    Log.e("DataLoader", "Failed to fetch data. Response Code: " + responseCode);
                    handler.post(() -> callback.onDataLoaded(null));
                }
            } catch (Exception e) {
                Log.e("DataLoader", "Error during data loading", e);
                handler.post(() -> callback.onDataLoaded(null));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
}