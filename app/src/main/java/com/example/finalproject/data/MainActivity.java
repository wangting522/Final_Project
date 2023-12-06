package com.example.finalproject.data;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.finalproject.databinding.ActivityMainBinding;
/**
 * This activity represents the main screen of the application.
 * It contains buttons to navigate to various functionalities.
 */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set onClick listeners for each button using binding
        binding.sunButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SunActivity.class);
            startActivity(intent);
        });

        binding.dictionaryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictionaryActivity.class);
            startActivity(intent);
        });

        binding.songButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Deezer.class);
            startActivity(intent);
        });

        binding.recipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecipeMainActivity.class);
            startActivity(intent);
        });


        Log.w("MainActivity", "In onCreate() - Loading Widgets");
    }
    /**
     * Called when the activity is resumed from a paused state.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.w( "MainActivity", "In onResume() - The application is now responding to user input" );
    }
    /**
     * Called before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w( "MainActivity", "In onDestroy() - Any memory used by the application is freed" );
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.w( "MainActivity", "In onStop() - The application is no longer visible" );
    }
    /**
     * Called when the activity is paused and is no longer in the foreground.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.w( "MainActivity", "In onPause() - The application no longer responds to user input" );
    }
    /**
     * Called when the activity is becoming visible to the user.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.w( "MainActivity", "In onStart() - The application is now visible on screen" );
    }
}
