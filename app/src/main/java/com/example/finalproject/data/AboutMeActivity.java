package com.example.finalproject.data;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.finalproject.R;

public class AboutMeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        Toolbar tBar = (Toolbar)findViewById(R.id.recipe_toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.accessible_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_home:

                Intent goHome = new Intent(AboutMeActivity.this, RecipeMainActivity.class);
                startActivity(goHome);
                break;
            case R.id.toolbar_help:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.information))
                        .setMessage(getString(R.string.recipeVersion) + "\n" + getString(R.string.aboutHelp))
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.toolbar_fav:
                Intent goToFav = new Intent(AboutMeActivity.this, RecipeFavActivity.class);
                startActivity(goToFav);
                break;
            case R.id.toolbar_search:
                Intent goToSearch = new Intent(AboutMeActivity.this, RecipeSearchActivity.class);
                startActivity(goToSearch);
                break;
            case R.id.toolbar_about:
                Toast.makeText(this, getString(R.string.aboutToast) , Toast.LENGTH_LONG).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}