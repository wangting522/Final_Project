package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.databinding.ActivitySunBinding;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SunActivity extends AppCompatActivity {

    private ActivitySunBinding binding;
    private RequestQueue queue;
    private SharedPreferences sharedPreferences;
    private MyRecordDAO myRecordDAO;
    private Executor databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySunBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        queue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("SunAppPreferences", MODE_PRIVATE);

        // Initialize the database and DAO in the background
        databaseExecutor.execute(() -> {
            RecordDatabase db = RecordDatabase.getDatabase(getApplicationContext());
            myRecordDAO = db.myRecordDAO();
        });

        loadLastSearch();
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        binding.lookupButton.setOnClickListener(view -> {
            String location = binding.locationEditText.getText().toString();
            String latitude = binding.latitudeEditText.getText().toString();
            String longitude = binding.longitudeEditText.getText().toString();
            Log.d("SunActivity", "Location: " + location + ", Latitude: " + latitude + ", Longitude: " + longitude);
            if (isValidInput(latitude, longitude,location)) {
                saveLastSearch(latitude, longitude,location);
                fetchSunriseSunsetTimes(latitude, longitude);
            } else {
                Toast.makeText(SunActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        });

        binding.saveLocationButton.setOnClickListener(view -> {
            saveLocationToDatabase();
            Toast.makeText(SunActivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
        });

        binding.viewFavoritesButton.setOnClickListener(view -> {
            Intent intent = new Intent(SunActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }
    private void saveLocationToDatabase() {
        String location = binding.locationEditText.getText().toString();
        String latitude = binding.latitudeEditText.getText().toString();
        String longitude = binding.longitudeEditText.getText().toString();
        if (isValidInput(latitude, longitude,location)) {
            databaseExecutor.execute(() -> {
                MyRecord record = new MyRecord(location,
                        new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()),
                        true,latitude,longitude);
                myRecordDAO.insertRecord(record);
            });
        }
    }
    public void fetchSunriseSunsetTimes(String latitude, String longitude) {
        String url = "https://api.sunrisesunset.io/json?lat=" + latitude + "&lng=" + longitude + "&date=today";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject results = response.getJSONObject("results");
                        String sunrise = results.getString("sunrise");
                        String sunset = results.getString("sunset");
                        runOnUiThread(() -> {
                            binding.sunriseTextView.setText("Sunrise: " + sunrise);
                            binding.sunriseTextView.setVisibility(View.VISIBLE);
                            binding.sunsetTextView.setText("Sunset: " + sunset);
                            binding.sunsetTextView.setVisibility(View.VISIBLE);
                        });
                    } catch (JSONException e) {
                        Toast.makeText(SunActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(SunActivity.this, "Error with request", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }



    private boolean isValidInput(String latitude, String longitude, String location) {
        return !latitude.isEmpty() && !longitude.isEmpty();
    }

    private void saveLastSearch(String latitude, String longitude, String location) {
        sharedPreferences.edit()
                .putString("last_location", location)
                .putString("last_latitude", latitude)
                .putString("last_longitude", longitude)
                .apply();
    }

    private void loadLastSearch() {
        String lastLocation = sharedPreferences.getString("last_location", "");
        String lastLatitude = sharedPreferences.getString("last_latitude", "");
        String lastLongitude = sharedPreferences.getString("last_longitude", "");
        binding.locationEditText.setText(lastLocation);
        binding.latitudeEditText.setText(lastLatitude);
        binding.longitudeEditText.setText(lastLongitude);
    }
}


