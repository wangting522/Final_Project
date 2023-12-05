package com.example.finalproject.dictionary;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.data.SearchViewModel;
import com.example.finalproject.R;
import com.example.finalproject.databinding.ActivityDictionaryBinding;
import com.example.finalproject.databinding.SearchMessageBinding;


import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main activity for the dictionary application, providing user interface to search for words,
 * display their definitions, save them to a local list, and manage saved entries.
 */
public class DictionaryActivity extends AppCompatActivity {
    /**
     * SearchRoomBinding object for data binding.
     */
    ActivityDictionaryBinding binding;
    /**
     * ArrayList to store SearchTerm objects representing search results.
     */
    ArrayList<SearchWord> messages = null;
    /**
     * ViewModel for managing data related to search.
     */
    SearchViewModel chatModel ;
    /**
     * Data Access Object for Room database.
     */
    SearchWordDAO mDAO;
    /**
     * Adapter for the RecyclerView displaying search results and saved terms.
     */
    RecyclerView.Adapter<MyRowHolder> myAdapter = null;
    /**
     * RequestQueue for managing Volley network requests.
     */
    RequestQueue queue = null;
    private SearchWord tempSearchWord;


    /**
     * Initializes the activity, sets up the user interface, and initializes components.
     * This method sets up the toolbar, RecyclerView, database connection, and ViewModel.
     * It also handles the restoration of saved instance state if applicable.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle),
     *                           which is null if no data was supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        queue = Volley.newRequestQueue(this);
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "databaseFileOnPhone").build();
        mDAO = db.swDAO();

        chatModel = new ViewModelProvider(this).get(SearchViewModel.class);
        messages = chatModel.messages.getValue();
        if(messages == null)
        {
            chatModel.messages.postValue(messages = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                List<SearchWord> fromDatabase= mDAO.getAllSearchWords();
                messages.addAll(fromDatabase); //Once you get the data from database

                runOnUiThread( () ->  binding.recyclerView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }

//        chatModel.selectedMessage.observe(this, selectedMessage -> {
//
//            if(selectedMessage != null) {
//                SearchDetailsFragment newFragment = new SearchDetailsFragment(selectedMessage);
//
//                FragmentManager fMgr = getSupportFragmentManager();
//                FragmentTransaction transaction = fMgr.beginTransaction();
//                transaction.addToBackStack("any string here");
//                transaction.add(R.id.abcframeLayout, newFragment);
//                //transaction.replace(R.id.frameLayout, newFragment);
//                transaction.commit();
//            }
//        });


        SharedPreferences prefs = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
        AtomicReference<EditText> searchText = new AtomicReference<>(binding.editSearchWord);

        binding.Searchbtn.setOnClickListener(click -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchText", searchText.get().getText().toString() );
            editor.apply();
            String wordToSearch = binding.editSearchWord.getText().toString();
                try {
                    String encodedWordName = URLEncoder.encode (wordToSearch, "UTF-8");

                    String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + encodedWordName;

                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                            (response) -> {   //this gets called if the server responded
                                try {
                                    StringBuilder definitionsBuilder = new StringBuilder();
                                    JSONObject results = response.getJSONObject(0);
                                    JSONArray meanings = results.getJSONArray("meanings");
                                    messages.clear();

                                    for (int i = 0; i < meanings.length(); i++) {
                                        JSONObject aMeaning = meanings.getJSONObject(i);
                                        JSONArray aDefinition = aMeaning.getJSONArray("definitions");
                                        for (int j = 0; j < aDefinition.length(); j++) {
                                            String def = aDefinition.getJSONObject(j).getString("definition");
                                            definitionsBuilder.append(def).append("\n");
                                            runOnUiThread( () -> {
                                                SearchWord thisMessage = new SearchWord(wordToSearch,def);
                                                // Update the RecyclerView with the new word and its definitions
                                                messages.add(thisMessage);
                                                myAdapter.notifyDataSetChanged();
                                            });
//                                            Executor thread = Executors.newSingleThreadExecutor();
//                                            thread.execute(() -> {
//                                                mDAO.insertSearchWord(new SearchWord(wordToSearch, def));
//                                            });
//
                                        }
//                                        myAdapter.notifyDataSetChanged();
                                    }
                                    String allDefinitions = definitionsBuilder.toString();
                                    tempSearchWord = new SearchWord(wordToSearch, allDefinitions);
                                    runOnUiThread(() -> {
                                    messages.clear(); // Clear previous results
                                    messages.add(tempSearchWord);
                                    myAdapter.notifyDataSetChanged();});
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("Reponse", "Received" + response.toString());

                            },
                            (error) ->{  });
                    queue.add(request);
                    // Clear the search input field
                    //binding.editSearchWord.setText("");

                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }



        });


        searchText.get().setText(prefs.getString("searchText",""));
        chatModel.selectedMessage.observe(this, (newMessageValue) -> {
            SearchDetailsFragment dictionaryFragment = new SearchDetailsFragment( newMessageValue );
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction transaction = fMgr.beginTransaction();
            transaction.addToBackStack("hehe");
            transaction.replace(R.id.fragmentLocation,dictionaryFragment);
            transaction.commit();
        });
        /**
         * Adapter for the RecyclerView displaying search results and saved terms in the
         * DictionaryActivity. The adapter binds the data to the RecyclerView and handles the creation of ViewHolder objects.
         */
        binding.recyclerView.setAdapter(
                myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            /**
            * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
            *
            * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
            * @param viewType The view type of the new View.
            * @return A new ViewHolder that holds a View of the given view type.
            */
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                SearchMessageBinding binding = SearchMessageBinding.inflate(getLayoutInflater());

                return new MyRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside
            }
            /**
            * Called by RecyclerView to display the data at the specified position.
            *
            * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position
            *                 in the data set.
            * @param position The position of the item within the adapter's data set.
            */
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                SearchWord searchWord = messages.get(position);
                holder.messageText.setText(searchWord.getWord());

                holder.definitionText.setText(searchWord.getDefinition());
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
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    /**
     * Inflates the menu for the activity to add items to the action bar.
     *
     * @param menu The options menu in which the items are placed.
     * @return True for the menu to be displayed, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dic_menu, menu);
        return true;
    }
    /**
     * Handles the selection of a menu item.
     * This method performs actions such as deleting a word or saving a word based on the menu item selected.
     *
     * @param item The menu item that was selected by the user.
     * @return True if the event was handled, false otherwise.
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
                            SearchWord selectedMessage = chatModel.selectedMessage.getValue();
                            if (selectedMessage != null) {
                                // Delete the message from the database
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    mDAO.deleteMessage(selectedMessage);
                                });

                                // Remove the message from the list and update the RecyclerView
                                messages.remove(selectedMessage);
                                myAdapter.notifyDataSetChanged();

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
                                                    myAdapter.notifyDataSetChanged();
                                                });
                                            });
                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton(R.string.negative_button_no, (dialog, cl) -> {})
                        .create().show();
                break;

            case R.id.saveButton:
                SearchWord addWord = chatModel.selectedMessage.getValue();
                if (addWord != null) { // Check that the selected word is not null
                    messages.add(addWord);
                    myAdapter.notifyDataSetChanged();
                    Executor addthread = Executors.newSingleThreadExecutor();
                    addthread.execute(() -> {
                        // Insert the word into the database on a background thread
                        long wordId = mDAO.insertSearchWord(addWord); // Use the correct type 'long'
                        addWord.setId(wordId); // Set the ID of the word with the correct type
                        Log.d("TAG", "The id created is:" + wordId);
                        runOnUiThread(() -> {
                            Snackbar.make(binding.getRoot(), R.string.word_saved_notification + addWord.getWord(), Snackbar.LENGTH_LONG).show();
                        });
                    });
                } else {
                    Toast.makeText(this, R.string.no_word_selected, Toast.LENGTH_SHORT).show();
                }
                break;



            case R.id.about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle(R.string.how_to_use_title)
                        .setMessage(R.string.how_to_use_message)
                        .setPositiveButton(R.string.ok_button, (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show();
                break;

            case R.id.wordList:
            Intent intent = new Intent(DictionaryActivity.this, SavedWords.class);
            startActivity(intent);
            break;

    }return true;
    }
    /**
     * ViewHolder class for RecyclerView to manage the views for each item in the list.
     * It holds references to all the UI components within the list item layout, such as TextViews.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView definitionText;
        /**
         * Constructor for the ViewHolder, with a click listener that handles item selection.
         *
         * @param itemView The view of the individual list item.
         */
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                int rowNum = getAbsoluteAdapterPosition();
                SearchWord toDelete = messages.get(rowNum);

                    //if (chatModel.selectedMessage.getValue() == null)
                    chatModel.selectedMessage.postValue(toDelete);
            });
            messageText = itemView.findViewById(R.id.message);
            definitionText =itemView.findViewById(R.id.definition);
        }
    }
}