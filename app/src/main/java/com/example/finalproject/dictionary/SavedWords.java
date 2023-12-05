package com.example.finalproject.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.finalproject.R;
import com.example.finalproject.data.SearchViewModel;
import com.example.finalproject.databinding.SearchMessageBinding;
import com.example.finalproject.databinding.DicSavedBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SavedWords extends AppCompatActivity {

    @NonNull DicSavedBinding binding;
    ArrayList<SearchWord> messages = null;
    SearchViewModel saveModel;
    SearchWordDAO mDAO;
    private RecyclerView.Adapter savedAdapter;
    Intent dictionarySavedPage;

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dic_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dic_saved);

        saveModel = new ViewModelProvider(this).get(SearchViewModel.class);
        // Initialize your Room database and DAO
        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "Dictionary").build();
        mDAO = db.swDAO();
        messages = saveModel.messages.getValue();


        //get data from Database
        if(messages == null)
        {
            saveModel.messages.setValue(messages = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {

                messages.addAll(mDAO.getAllSearchWords());

                runOnUiThread(() -> {
                    savedAdapter.notifyDataSetChanged();
                    Log.d("SearchSaved", "Data set size: " + messages.size());
                    binding.saveWordRecyclerView.setAdapter(savedAdapter);
                });
            });
        }

        binding = DicSavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar( binding.saveWordToolbar);
        saveModel.selectedMessage.observe(this, (newMessageValue) -> {
            if (newMessageValue != null) {
                SearchDetailsFragment newFragment = new SearchDetailsFragment(newMessageValue);

                FragmentManager fMgr = getSupportFragmentManager();
                FragmentTransaction tx = fMgr.beginTransaction();
                tx.addToBackStack("");
                tx.replace(R.id.saveWordFrameLayout, newFragment);
                tx.commit();
            }
        });



        binding.saveWordRecyclerView.setAdapter(savedAdapter = new RecyclerView.Adapter<myRowHolder>() {

            @NonNull
            @Override
            public myRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                // 1. load a XML layout
                SearchMessageBinding binding =                            // parent is incase matchparent
                        SearchMessageBinding.inflate(getLayoutInflater(), parent, false);
                // Set layout parameters to wrap content
                binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                // 2. call our constructor below
                return new myRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside

            }

            @Override
            public void onBindViewHolder(@NonNull myRowHolder holder, int position) {
                SearchWord obj = messages.get(position);
                holder.messageText.setText(obj.getWord());

                holder.definitionText.setText(obj.getDefinition());

            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

        });

        binding.saveWordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    class myRowHolder extends RecyclerView.ViewHolder {

        TextView messageText;

        TextView definitionText;

        public myRowHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
            definitionText = itemView.findViewById(R.id.definition);
            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SearchWord toDelete = messages.get(position);

                saveModel.selectedMessage.postValue(toDelete);
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete this word?");
                builder.setTitle("Delete Message")
                        .setPositiveButton("Yes", (dialog, cl) -> {
                            // Get the selected message
                            SearchWord selectedMessage = saveModel.selectedMessage.getValue();
                            if (selectedMessage != null) {
                                // Delete the message from the database
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    mDAO.deleteMessage(selectedMessage);
                                });

                                // Remove the message from the list and update the RecyclerView
                                messages.remove(selectedMessage);
                                savedAdapter.notifyDataSetChanged();

                                // Show a Snackbar with an undo option
                                Snackbar.make(binding.getRoot(), "Message deleted", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", clk2 -> {
                                            // Insert the deleted message back to the database
                                            Executors.newSingleThreadExecutor().execute(() -> {
                                                long id = mDAO.insertSearchWord(selectedMessage);
                                                selectedMessage.id = id;
                                                runOnUiThread(() -> {
                                                    // Add the message back to the list and update the RecyclerView
                                                    messages.add(selectedMessage);
                                                    savedAdapter.notifyDataSetChanged();
                                                });
                                            });
                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .create().show();
                break;
        }

        return true;
    }
}
