package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class DictionaryActivity extends AppCompatActivity {

    private EditText wordText;
    private Button search_button;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dictionary);

            // Find views by ID
            wordText = findViewById(R.id.wordText); // Replace with your actual EditText ID
            search_button = findViewById(R.id.search_button); // Replace with your actual Button ID

            // Set OnClickListener to the button
            search_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String word = wordText.getText().toString().trim();
                    if (word.isEmpty()) {
                        // Show toast message if the EditText is empty
                        Toast.makeText(DictionaryActivity.this, "Please enter a word", Toast.LENGTH_LONG).show();
                    } else {
                        // Perform your search operation here

                        // Example of showing a Snackbar
                        Snackbar.make(v, "Search performed", Snackbar.LENGTH_LONG).show();

                        // Example of showing an AlertDialog
                        new AlertDialog.Builder(DictionaryActivity.this)
                                .setTitle("Confirmation")
                                .setMessage("Are you sure you want to perform this action?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle the positive button action here
                                    }
                                })
                                .setNegativeButton("No", null) // null listener dismisses the dialog without doing anything
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            });
        }
    }

