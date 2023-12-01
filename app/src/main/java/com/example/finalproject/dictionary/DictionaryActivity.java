package com.example.finalproject.dictionary;
import android.content.Context;
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

import com.example.finalproject.data.SearchViewModel;
import com.example.finalproject.R;
import com.example.finalproject.databinding.ActivityDictionaryBinding;
import com.example.finalproject.databinding.SearchMessageBinding;


import com.google.android.material.snackbar.Snackbar;

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

//    public void onBackPressed() {
//        chatModel.selectedMessage.postValue(null);
//        super.onBackPressed();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDictionaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "databaseFileOnPhone").build();
        mDAO = db.swDAO();

        chatModel = new ViewModelProvider(this).get(SearchViewModel.class);
        messages = chatModel.messages.getValue();
        if(messages == null)
        {
            chatModel.messages.postValue(messages = new ArrayList<SearchWord>());

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

        //SharedPreferences to save something about what was typed in the EditText for use the next time
        SharedPreferences prefs = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
        AtomicReference<EditText> searchText = new AtomicReference<>(binding.editTextSearch);

        binding.btnSearch.setOnClickListener(click -> {
            String newMessage = binding.editTextSearch.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            SearchWord thisMessage = new SearchWord(newMessage, currentDateandTime);
            messages.add(thisMessage);
            binding.editTextSearch.setText("");
            myAdapter.notifyDataSetChanged();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchText", searchText.get().getText().toString() );
            editor.apply();

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute( () -> {
                thisMessage.id = mDAO.insertSearchWord(thisMessage);
                Log.d("TAG", "The id created is: " + thisMessage.id);
            });


           // runOnUiThread(() ->{myAdapter.notifyItemInserted(messages.size() - 1);});

            binding.editTextSearch.setText("");
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
                holder.timeText.setText(searchWord.getTimeSent());
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
                builder.setMessage("Do you want to delete the selected message?");
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

                // Get the currently selected word
                SearchWord selectedWord = chatModel.selectedMessage.getValue();
                if (selectedWord != null) {
                    // Save the word to favorites
                    //saveWordToFavorites(selectedWord);
                } else {
                    // If no word is selected, inform the user
                    Toast.makeText(this, "No word selected to save", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.about:
                Toast.makeText(this,"Version 3.0, Created by Yuxuan Xie",Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }
  /*  private void saveWordToFavorites(SearchWord word) {
        Executor thread = Executors.newSingleThreadExecutor();
        thread.execute(() -> {
            // Assume mDAO has a method to check if a word is already marked as favorite
            boolean isFavorite = mDAO.isWordFavorite(wordString);
            if (!isFavorite) {
                // Assume mDAO has a method to insert a word into the favorites list
                mDAO.insertFavoriteWord(wordString);
                runOnUiThread(() -> {
                    // Inform the user with a Toast or Snackbar
                    Toast.makeText(DictionaryActivity.this, "Word saved to favorites", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    // Inform the user that the word is already in favorites
                    Toast.makeText(DictionaryActivity.this, "Word is already in favorites", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
*/
    class MyRowHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                int rowNum = getAbsoluteAdapterPosition();
                SearchWord toDelete = messages.get(rowNum);

                if (chatModel.selectedMessage.getValue() == null)
                chatModel.selectedMessage.postValue(toDelete);
            });
            messageText = itemView.findViewById(R.id.message);
            timeText =itemView.findViewById(R.id.time);
        }
    }
}