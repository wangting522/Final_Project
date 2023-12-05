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

/**
 * SavedWords is an AppCompatActivity that handles the display and interaction with saved search words.
 * <p>
 * This activity provides functionality to view a list of saved search words, delete them, and view details about them.
 * It uses a RecyclerView to display the words, and integrates with a Room database to retrieve and manage the saved words.
 * <p>
 * Key functionalities include:
 * - Loading and displaying saved words from the Room database.
 * - Allowing the user to delete words and providing an undo option.
 * - Navigating to the details of a selected word.
 *
 * @see AppCompatActivity
 */
public class SavedWords extends AppCompatActivity {

    @NonNull DicSavedBinding binding;
    ArrayList<SearchWord> messages = null;
    SearchViewModel saveModel;
    SearchWordDAO mDAO;
    private RecyclerView.Adapter savedAdapter;
    Intent dictionarySavedPage;

    /**
     * Initializes the options menu for the activity.
     *
     * @param menu The options menu in which items are placed.
     * @return true for the menu to be displayed; false to not be shown.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dic_menu, menu);
        return true;
    }

    /**
     * Handles the activity's creation lifecycle event.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dic_saved);

        saveModel = new ViewModelProvider(this).get(SearchViewModel.class);
        // Initialize your Room database and DAO
        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "databaseFileOnPhone").build();
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

            /**
             * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
             *
             * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
             * @param viewType The view type of the new View.
             * @return A new ViewHolder that holds a View of the given view type.
             */
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

            /**
             * Called by RecyclerView to display the data at the specified position.
             *
             * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position
             *                 in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull myRowHolder holder, int position) {
                SearchWord obj = messages.get(position);
                holder.messageText.setText(obj.getWord());
                String text = obj.getDefinition();
                holder.definitionText.setText(text.substring(0,Math.min(text.length(),20))+"...");

            }

            /**
             * Returns the total number of items in the data set held by the adapter.
             *
             * @return The total number of items in this adapter.
             */
            @Override
            public int getItemCount() {
                return messages.size();
            }

        });

        binding.saveWordRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * ViewHolder class for managing views in each row of the RecyclerView.
     */
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

    /**
     * Handles the selection of an item in the options menu.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.deleteButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_confirmation);
                builder.setTitle(R.string.delete_title)
                        .setPositiveButton(R.string.positive_button_yes, (dialog, cl) -> {
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
                                Snackbar.make(binding.getRoot(), R.string.message_deleted, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.undo_action, clk2 -> {
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
                        .setNegativeButton(R.string.negative_button_no, (dialog, cl) -> {})
                        .create().show();
                break;
        }

        return true;
    }
}
