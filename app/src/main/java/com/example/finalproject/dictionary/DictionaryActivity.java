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


public class DictionaryActivity extends AppCompatActivity {
    ActivityDictionaryBinding binding;
    ArrayList<SearchWord> messages = null;
    SearchViewModel chatModel ;
    SearchWordDAO mDAO;
    RecyclerView.Adapter<MyRowHolder> myAdapter = null;
    RequestQueue queue = null;
    private SearchWord tempSearchWord;



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

        binding.recyclerView.setAdapter(
                myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                SearchMessageBinding binding = SearchMessageBinding.inflate(getLayoutInflater());

                return new MyRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                SearchWord searchWord = messages.get(position);
                holder.messageText.setText(searchWord.getWord());

                holder.definitionText.setText(searchWord.getDefinition());
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dic_menu, menu);
        return true;
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
                                Snackbar.make(binding.getRoot(), "Message deleted", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", clk2 -> {
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
                        .setNegativeButton("No", (dialog, cl) -> {})
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
                            Snackbar.make(binding.getRoot(), "Word saved to your word list: " + addWord.getWord(), Snackbar.LENGTH_LONG).show();
                        });
                    });
                } else {
                    Toast.makeText(this, "No word selected to save", Toast.LENGTH_SHORT).show();
                }
                break;


//                if (tempSearchWord != null) {
//                    Executor thread = Executors.newSingleThreadExecutor();
//                    thread.execute(() -> {
//                        long wordId = mDAO.insertSearchWord(tempSearchWord);
//                        tempSearchWord.setId(wordId);
//
//                        // Switching to UI thread for Toast
//                        runOnUiThread(() -> {
//                            Toast.makeText(DictionaryActivity.this, "Word saved to your word list", Toast.LENGTH_SHORT).show();
//                        });
//                    });
//                } else {
//                    Toast.makeText(this, "No word selected to save", Toast.LENGTH_SHORT).show();
//                }
//                break;

            case R.id.about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("How to Use")
                        .setMessage("To use this dictionary app, simply enter a word in the search " +
                                "bar at the top and press the 'Search' button. The app will display " +
                                "definitions fetched from the online dictionary. \n\nIf you wish to " +
                                "save a word and its definitions for later review, you can click on " +
                                "the 'Save' button after searching for a word. Your saved words can " +
                                "be accessed in the 'Word List' section.")
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show();
                break;

            case R.id.wordList:
            Intent intent = new Intent(DictionaryActivity.this, SavedWords.class);
            startActivity(intent);
            break;

    }return true;
    }

    class MyRowHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView definitionText;

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