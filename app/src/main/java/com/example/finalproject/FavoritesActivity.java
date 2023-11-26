package com.example.finalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.data.FavoritesViewModel;
import com.example.finalproject.data.RecordDetailsFragment;
import com.example.finalproject.databinding.ActivityFavoritesBinding;
import com.example.finalproject.databinding.ReceiveMessageBinding;
import com.example.finalproject.databinding.SentMessageBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
/**
 * The FavoritesActivity is an AppCompatActivity that manages a list of favorite records.
 * It displays the records in a RecyclerView and allows users to interact with them.
 * Users can view record details, and delete records from their favorites list.
 */
public class FavoritesActivity extends AppCompatActivity {
    MyRecordDAO mDao;
    private RequestQueue queue;
    ArrayList<MyRecord> favoriteLocations = null;
    FavoritesViewModel viewModel;
    RecyclerView.Adapter<MyRowHolder> myAdapter;
    ActivityFavoritesBinding binding;
    //private Executor backgroundExecutor = Executors.newSingleThreadExecutor();
    /**
     * Initializes the options menu for the activity.
     * @param menu The options menu in which items are placed.
     * @return true for the menu to be displayed; false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }
    /**
     * Handles action bar item clicks.
     * @param item The menu item that was selected.
     * @return false to allow normal menu processing to proceed, true to consume it here.
     */
    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1:
                MyRecord selectedRecordValue = viewModel.selectedRecord.getValue();
                if (selectedRecordValue != null) {
                    new AlertDialog.Builder(FavoritesActivity.this)
                            .setTitle("Question")
                            .setMessage("Do you want to delete this record? " + selectedRecordValue.getRecord())
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", (dialog, which) -> {
                                Executor thread1 = Executors.newSingleThreadExecutor();
                                thread1.execute(() -> {
                                    mDao.deleteRecord(selectedRecordValue);
                                    runOnUiThread(() -> {
                                        favoriteLocations.remove(selectedRecordValue);
                                        myAdapter.notifyDataSetChanged();

                                        // Snackbar with Undo option
                                        Snackbar.make(binding.getRoot(), "Record deleted", Snackbar.LENGTH_LONG)
                                                .setAction("Undo", undoView -> {
                                                    Executor thread2 = Executors.newSingleThreadExecutor();
                                                    thread2.execute(() -> {
                                                        mDao.insertRecord(selectedRecordValue);
                                                        runOnUiThread(() -> {
                                                            favoriteLocations.add(selectedRecordValue);
                                                            myAdapter.notifyDataSetChanged();
                                                        });
                                                    });
                                                })
                                                .show();
                                    });
                                });
                            })
                            .show();
                }
                return true;
            case R.id.item_2:
                new AlertDialog.Builder(this)
                        .setTitle("How to Use the interface")
                        .setMessage(
                                " Step 1: Input location, longitude and latitude and click Lookup button to get sunrise and sunset time.\n" +
                                        " Step 2: Click Save Location button and View Favorites button. \n" +
                                        " Step 3: Click saved location and get realtime information.")
                        .setPositiveButton("OK", null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = Volley.newRequestQueue(this);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.myToolbar);

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        favoriteLocations = viewModel.record.getValue();
        if (favoriteLocations == null) {
            favoriteLocations = new ArrayList<>();
            viewModel.record.postValue(favoriteLocations);
        }

        //load record from the database
        RecordDatabase db = Room.databaseBuilder(getApplicationContext(), RecordDatabase.class, "FileOnTingPhone").build();
        mDao = db.myRecordDAO();
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute( () -> {
            List<MyRecord> fromDatabase = mDao.getAllRecords();
            favoriteLocations.addAll(fromDatabase);
        });
        //end of loading from the database
/*
        binding.saveButton.setOnClickListener( cli -> {

            String userMessage = binding.userMessage.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            MyRecord thisMessage = new MyRecord(userMessage, currentDateandTime, true);
            favoriteLocations.add(thisMessage);
            binding.userMessage.setText("");
            myAdapter.notifyDataSetChanged();

            // add to database on another thread
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute( () -> {
                thisMessage.id = mDao.insertRecord(thisMessage);
            });

        });
        binding.deleteButton.setOnClickListener( cli -> {
            String userMessage = binding.userMessage.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            favoriteLocations.add(new MyRecord(userMessage, currentDateandTime, false));
            binding.userMessage.setText("");
            myAdapter.notifyDataSetChanged();
        });
*/
        viewModel.selectedRecord.observe(this,(newValue) -> {
            RecordDetailsFragment chatFragment = new RecordDetailsFragment(newValue);
            //chatFragment.displayMessage(newValue);
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("")
                    .replace(R.id.fragmentLocation, chatFragment)
                    .commit();
        });
        binding.myRecycler.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @Override
            public int getItemViewType(int position) {
                MyRecord myRecord = favoriteLocations.get(position);
                return myRecord.isSaveButton() ? 0 : 1;
            }
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //viewType will be either 0 or 1
                if (viewType == 0) {
                    SentMessageBinding binding = SentMessageBinding.inflate(getLayoutInflater(), parent, false);
                    return new MyRowHolder(binding.getRoot());
                }
                else {
                    ReceiveMessageBinding binding = ReceiveMessageBinding.inflate(getLayoutInflater(), parent, false);
                    return new MyRowHolder(binding.getRoot());
                }
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                MyRecord myRecord = favoriteLocations.get(position);
                holder.messageView.setText(myRecord.getRecord());
                holder.timeView.setText(myRecord.getTimeSave());
            }

            @Override
            public int getItemCount() {
                return favoriteLocations.size();
            }
        });
        binding.myRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * ViewHolder class for RecyclerView items.
     * This class holds references to the TextViews for displaying a MyRecord.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView messageView;
        public TextView timeView;
        /**
         * Constructor for MyRowHolder.
         * @param itemView The view of the RecyclerView item.
         */
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.record);
            timeView = itemView.findViewById(R.id.time);

            itemView.setOnClickListener( click -> {
                int position = getAbsoluteAdapterPosition();
                MyRecord selected = favoriteLocations.get(position);
                viewModel.selectedRecord.postValue(selected);
               fetchSunriseSunsetTimes(String.valueOf(selected.getLatitude()), String.valueOf(selected.getLongitude()));
            });

        }
    }
    /**
     * Fetches sunrise and sunset times based on latitude and longitude.
     * @param latitude Latitude for the query.
     * @param longitude Longitude for the query.
     */
    public void fetchSunriseSunsetTimes(String latitude, String longitude) {
        String url = "https://api.sunrisesunset.io/json?lat=" + latitude + "&lng=" + longitude + "&timezone=CA&date=today";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject results = response.getJSONObject("results");
                        String sunrise = results.getString("sunrise");
                        String sunset = results.getString("sunset");
                        runOnUiThread(() -> {
                           viewModel.selectedRecord.postValue(new MyRecord("","", true, sunrise, sunset));
                        });
                    } catch (JSONException e) {
                        Toast.makeText(FavoritesActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(FavoritesActivity.this, "Error with request", Toast.LENGTH_SHORT).show());
        queue.add(request);
    }
}


