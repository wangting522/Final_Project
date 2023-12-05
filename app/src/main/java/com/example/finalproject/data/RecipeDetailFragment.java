package com.example.finalproject.data;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RecipeDetailFragment extends Fragment {


    private ImageButton faveButton;

    private ProgressBar progressBar;

    private ImageView imageView;

    private TextView titleTextView;

    private TextView detailTextView;

    private RecipeEntry recipe;

    private class FetchRecipeDetail extends AsyncTask<Long, Integer, String> {
        private final String urlTemplate = "https://api.spoonacular.com/recipes/%s/summary?apiKey=2311513282b7432684777caf629d344a";

        @Nullable
        @Override
        protected String doInBackground(Long... params) {
            Long recipeId = params[0];
            String rawUrl = String.format(urlTemplate, recipeId);
            try {
                URL url = new URL(rawUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                publishProgress(10);
                InputStream inStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                publishProgress(20);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                JSONObject r = new JSONObject(sb.toString());
                publishProgress(80);
                if (recipe != null) {
                    recipe.title = r.getString("title");
                    recipe.details = r.getString("summary");
                }
                publishProgress(100);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);
            titleTextView.setText(recipe.title);
            Spanned text = Html.fromHtml(recipe.details);
            detailTextView.setMovementMethod(LinkMovementMethod.getInstance());
            detailTextView.setText(text);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        recipe = new RecipeEntry();
        recipe.id = bundle.getLong("id");
        recipe.title = bundle.getString("title");
        recipe.imageUrl = bundle.getString("imageUrl");
        recipe.details = bundle.getString("details");
        new FetchRecipeDetail().execute(recipe.id);

        View detailLayout = inflater.inflate(R.layout.detail, container, false);

        faveButton = detailLayout.findViewById(R.id.faveButton);
        faveButton.setOnClickListener((View v) -> {
            RecipeDatabaseHelper helper = new RecipeDatabaseHelper(getActivity());

            switch (v.getId()) {
                case R.id.faveButton:
                    if (RecipeDAO.isExist(helper, recipe.id)) {
                        Snackbar.make(v, getString(R.string.favRemoved), Snackbar.LENGTH_LONG).show();
                        RecipeDAO.deleteFavRecipe(helper, recipe.id);
                    } else {
                        Snackbar.make(v, getString(R.string.favAdded), Snackbar.LENGTH_LONG).show();
                        RecipeDAO.addFavRecipe(helper, recipe);
                    }
                    updateIcon();
                    break;
                default:
                    break;
            }

        });
        updateIcon();

        imageView = detailLayout.findViewById(R.id.imageRecipe);
        Picasso.get().load(recipe.imageUrl).into(imageView);

        titleTextView = (TextView) detailLayout.findViewById(R.id.textTitle);
        titleTextView.setText(recipe.title);
        detailTextView = detailLayout.findViewById(R.id.textValue2);
        progressBar = detailLayout.findViewById(R.id.detailProgressBar);

        return detailLayout;
    }

    private void updateIcon() {
        RecipeDatabaseHelper helper = new RecipeDatabaseHelper(getActivity());
        if (RecipeDAO.isExist(helper, recipe.id)) {
            faveButton.setImageResource(R.drawable.star_filled);
        } else {
            faveButton.setImageResource(R.drawable.star_unfilled);
        }
    }


}