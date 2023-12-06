package com.example.finalproject.data;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.finalproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
/**
 * Activity displaying favorite recipes.
 */
public class RecipeFavActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Button filterButton;

    private EditText filterText;

    private ListView list;

    private ProgressBar progressBar;

    private ArrayList<RecipeEntry> recipes = new ArrayList<RecipeEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav);

        progressBar = findViewById(R.id.favProgressBar);

        toolbar = (Toolbar) findViewById(R.id.recipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //This is the onClickListener for my List
        list = findViewById(R.id.favRecipeListView);
        RecipeDatabaseHelper helper = new RecipeDatabaseHelper(this);
        recipes = RecipeDAO.listFavRecipes(helper, "");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, recipes);
        list.setAdapter(adapter);
        list.deferNotifyDataSetChanged();

        list.setOnItemClickListener((mlist, item, position, id) -> {
            Bundle bundle = new Bundle();
            RecipeEntry entry = recipes.get(position);
            bundle.putLong("id", entry.id);
            bundle.putString("title", entry.title);
            bundle.putString("imageUrl", entry.imageUrl);
            bundle.putString("details", entry.details);

            boolean isTablet = findViewById(R.id.recipeFragmentLocation) != null;
            if (isTablet) {
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipeFragmentLocation, fragment)
                        .commit();
            } else { //isPhone
                Intent goToDetail = new Intent(this, FavEmptyActivity.class);
                goToDetail.putExtras(bundle); //send data to next activity
                startActivity(goToDetail); //make the transition
            }
        });

        list.setOnItemLongClickListener((mlist, item, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            RecipeEntry recipe = recipes.get(position);
            alertDialogBuilder.setTitle("Do you want to delete this?")
                    .setMessage(recipe.title)
                    .setPositiveButton("Yes", (click, arg) -> {
                        recipes.remove(position);
                        RecipeDAO.deleteFavRecipe(helper, recipe.id);
                        list.setAdapter(new ArrayAdapter(this, R.layout.list_item, recipes));
                        list.deferNotifyDataSetChanged();
                    })
                    .setNegativeButton("No", (click, arg) -> {
                    })
                    .create().show();
            return true;
        });

        filterText = findViewById(R.id.favFilterText);
        filterButton = findViewById(R.id.favFilterButton);
        filterButton.setOnClickListener(click -> {
            String keyword = filterText.getText().toString();
            Log.e("keyword", keyword);
            recipes = RecipeDAO.listFavRecipes(helper, keyword);
            Log.e("recipes", Integer.toString(recipes.size()));
            list.setAdapter(new ArrayAdapter(this, R.layout.list_item, recipes));
            list.deferNotifyDataSetChanged();

            Toast.makeText(this, Integer.toString(recipes.size()) + getString(R.string.favSearchCount) , Toast.LENGTH_LONG).show();
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String lastFilter = pref.getString("last_filter", "");
        ConstraintLayout flayout = findViewById(R.id.fav_layout);
        if (!lastFilter.isEmpty()) {
            Snackbar.make(flayout, getString(R.string.favLastSearchPrompt) + lastFilter, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(flayout, getString(R.string.favFirstSearchPrompt), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("last_filter", filterText.getText().toString());
        editor.commit();
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
                Intent goHome = new Intent(this, RecipeMainActivity.class);
                startActivity(goHome);
                break;
            case R.id.toolbar_help:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.information))
                        .setMessage(getString(R.string.recipeVersion) + "\n" + getString(R.string.favHelp))
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.toolbar_search:
                Intent goToSearch = new Intent(this, RecipeSearchActivity.class);
                startActivity(goToSearch);
                break;
            case R.id.toolbar_about:
                Intent goToAbout = new Intent(this, AboutMeActivity.class);
                startActivity(goToAbout);
                break;
            case R.id.toolbar_fav:
                Toast.makeText(this, getString(R.string.favToast), Toast.LENGTH_LONG).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
