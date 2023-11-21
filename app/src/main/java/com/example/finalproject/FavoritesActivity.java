package com.example.finalproject;

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

import com.example.finalproject.data.FavoritesViewModel;
import com.example.finalproject.data.RecordDetailsFragment;
import com.example.finalproject.databinding.ActivityFavoritesBinding;
import com.example.finalproject.databinding.ReceiveMessageBinding;
import com.example.finalproject.databinding.SentMessageBinding;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {
    MyRecordDAO mDao;
    ArrayList<MyRecord> favoriteLocations = null;
    FavoritesViewModel viewModel;
    RecyclerView.Adapter<MyRowHolder> myAdapter;
    ActivityFavoritesBinding binding;
    private Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1:
                MyRecord selectedRecordValue = viewModel.selectedRecord.getValue(); // Get the selected record
                if (selectedRecordValue != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FavoritesActivity.this);
                    builder.setTitle("Question");
                    builder.setMessage("Do you want to delete this record? " + selectedRecordValue.getRecord()); // Use record text from the selected record
                    builder.setNegativeButton("No", (dialog, which) -> { });
                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        Executor thread1 = Executors.newSingleThreadExecutor();
                        thread1.execute(() -> {
                            mDao.deleteRecord(selectedRecordValue);
                            runOnUiThread(() -> {
                                favoriteLocations.remove(selectedRecordValue); // Remove from your list on the main thread
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
                    });
                    builder.create().show();
                }
                return true;
            case R.id.item_2:
                Toast.makeText(this, "Version 1.0, created by YourName", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView messageView;
        public TextView timeView;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.record);
            timeView = itemView.findViewById(R.id.time);

            itemView.setOnClickListener( click -> {
                int position = getAbsoluteAdapterPosition();
                MyRecord selected = favoriteLocations.get(position);
                viewModel.selectedRecord.postValue(selected);
 //               fetchSunriseSunsetTimes(String.valueOf(selected.getLatitude()), String.valueOf(selected.getLongitude()));
            });

        }
    }

}


