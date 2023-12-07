package com.example.finalproject.data;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.finalproject.databinding.ActivityPlaylistBinding;
import com.example.finalproject.databinding.SongPlaylistBinding;

/**
 * The Playlist class represents an activity in the application that displays and manages
 * a playlist of songs. It utilizes the SongsAdapter to populate a RecyclerView with songs,
 * and allows users to perform actions such as searching for songs, deleting songs from the
 * playlist, and previewing song previews.
 */
public class Playlist extends AppCompatActivity {
    private RecyclerView.Adapter myAdapter;
    private RecyclerView recyclerView;
    private SongsViewModel songsViewModel;
    private ActivityPlaylistBinding binding;
    private SongsAdapter songsAdapter;
    private RequestQueue queue;
    private List<Songs> songsList = new ArrayList<>();
    MediaPlayer mediaPlayer;

    /**
     * Called when the activity is first created. Initializes the UI components, sets up the
     * toolbar, and handles various actions such as searching for songs, deleting songs, and
     * previewing song previews.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.playlist_toolbar);

        setSupportActionBar(binding.toolbar);

        queue = Volley.newRequestQueue(this);

        songsAdapter = new SongsAdapter(songsList);
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);

        binding.searchPageButton.setOnClickListener(click -> {
            Intent intent = new Intent(this, Deezer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        binding.playlistPageButton.setOnClickListener(click -> {
            startActivity(new Intent(this, Playlist.class));
        });

        SongsDatabase songsDatabase = Room.databaseBuilder(getApplicationContext(), SongsDatabase.class, "deezerDB").build();
        DeezerDAO dDao = songsDatabase.deezerDao();

        binding.searchButton.setOnClickListener(c -> {

            String searchedText = binding.searchTextPlaylist.getText().toString().trim();

            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(() -> {
                List<Songs> searchResults = dDao.searchSong(searchedText);
                runOnUiThread(() -> {
                    if (searchResults != null && !searchResults.isEmpty()) {
                        songsList.clear();
                        songsList.addAll(searchResults);
                        myAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Song not found: " + searchedText, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });


        if (songsList.isEmpty()) {
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                List<Songs> allSongs = dDao.getAllSongs();
                Log.d("Database", "Number of songs: " + allSongs.size());
                runOnUiThread(() -> {
                    songsList.addAll(allSongs);
                    if (myAdapter == null) {
                        myAdapter = new SongsAdapter(songsList);
                        recyclerView.setAdapter(myAdapter);
                    } else {
                        myAdapter.notifyDataSetChanged();
                    }
                });
            });
        }

        recyclerView = binding.favSongs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songsAdapter);

    }

    /**
     * ViewHolder class for representing individual items (songs) in the RecyclerView.
     * Provides a bind method to populate the views with song data.
     */
    public class SongsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        TextView durationTextView;
        ImageView albumCoverSP;

        androidx.appcompat.widget.Toolbar songsPlaylist;

        SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.songName);
            artistTextView = itemView.findViewById(R.id.artistName);
            durationTextView = itemView.findViewById(R.id.duration);
            albumCoverSP = itemView.findViewById(R.id.albumCoverSP);
            songsPlaylist = itemView.findViewById(R.id.songToolsP);

        }
        /**
         * Binds the song data to the views within the ViewHolder.
         *
         * @param songs The Songs object containing information about the song.
         */
        public void bind(Songs songs) {
            titleTextView.setText(songs.getTitle());
            artistTextView.setText(songs.getArtistName());
            durationTextView.setText(formatDuration(songs.getDuration()));


            String pathname = getFilesDir() + "/" + songs.getAlbumCover();
            File file = new File(pathname);

            if (file.exists()) {
                Bitmap albumCover = BitmapFactory.decodeFile(pathname);
                albumCoverSP.setImageBitmap(albumCover);
                albumCoverSP.setVisibility(View.VISIBLE);
            } else {
                ImageRequest imgReq = new ImageRequest(songs.getAlbumCover(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        albumCoverSP.setImageBitmap(bitmap);
                        albumCoverSP.setVisibility(View.VISIBLE);
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                    Playlist.this.openFileOutput(songs.getArtistName() + ".png", Activity.MODE_PRIVATE));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1024, 1024, ImageView.ScaleType.CENTER, null, error -> {
                    // Handle error in fetching the image
                    error.printStackTrace();
                });
                queue.add(imgReq);
            }
        }
        /**
         * Formats the duration of a song in minutes and seconds.
         *
         * @param duration The duration of the song in seconds.
         * @return A formatted string representing the duration (mm:ss).
         */
        private String formatDuration(int duration) {
            int minutes = duration / 60;
            int seconds = duration % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    // Inner class for SongsAdapter
    class SongsAdapter extends RecyclerView.Adapter<SongsViewHolder> {

        private List<Songs> songsList;

        /**
         * Adapter class for the RecyclerView, responsible for creating ViewHolders and binding data.
         */
        SongsAdapter(List<Songs> songsList) {
            this.songsList = songsList;
        }


        /**
         * Called when RecyclerView needs a new ViewHolder of the given type to represent
         * an item.
         *
         * @param parent   The ViewGroup into which the new View will be added.
         * @param viewType The view type of the new View.
         * @return A new SongsViewHolder that holds a View representing an item.
         */
        @NonNull
        @Override
        public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SongPlaylistBinding playlistBinding = SongPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SongsViewHolder(playlistBinding.getRoot());
        }

        /**
         * Called to bind the ViewHolder to a position in the RecyclerView.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents
         *                 of the item at the given position.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
            SongsDatabase songsDatabase = Room.databaseBuilder(getApplicationContext(), SongsDatabase.class, "deezerDB").build();
            DeezerDAO dDao = songsDatabase.deezerDao();
            Songs song = songsList.get(position);
            holder.bind(song);

            androidx.appcompat.widget.Toolbar toolbar = holder.songsPlaylist;
            toolbar.inflateMenu(R.menu.songs_playlist);

            toolbar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Playlist.this);
                        builder.setMessage("Do you want to delete this song from your playlist?")
                                .setTitle("Delete")
                                .setNegativeButton("No", (dialog, which) -> {
                                })
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    Songs songToDelete = songsList.get(position);
                                    songsList.remove(songToDelete);

                                    if (songToDelete != null) {
                                        songsAdapter.notifyItemChanged(position);
                                        Executor thread1 = Executors.newSingleThreadExecutor();
                                        thread1.execute(() -> {
                                            dDao.deleteSongFromPlayList(songToDelete);
                                        });
                                        Log.d("SongToDelete", "Song to Delete: " + songToDelete.toString());

                                        Snackbar.make(findViewById(android.R.id.content), "Song Deleted from playlist", Snackbar.LENGTH_LONG)
                                                .setAction("Undo", (btn) -> {
                                                    Executor thread2 = Executors.newSingleThreadExecutor();
                                                    thread2.execute(() -> {
                                                        // undo the addition from the database
                                                        dDao.insertSong(songToDelete);
                                                    });

                                                    songsList.remove(songToDelete);
                                                    songsAdapter.notifyItemChanged(position);

                                                })
                                                .show();
                                    }
                                });
                        builder.create().show();
                        break;
                    case R.id.preview:
                        try {
                            // Construct the URL for the Deezer API to get tracks of the selected album
                            String tracksURL = "https://api.deezer.com/track/" + song.getId();

                            // Make a GET request to the Deezer API to get tracks of the selected album
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tracksURL, null,
                                    response -> {
                                        try {
                                            // Get the array of songs from the response
                                            JSONArray songsArray = response.getJSONArray("data");

                                            // Iterate through the songsArray to find the matching song ID
                                            for (int i = 0; i < songsArray.length(); i++) {
                                                JSONObject currentSong = songsArray.getJSONObject(i);

                                                // Check if the current song has the same ID as the selected song
                                                int currentSongId = currentSong.getInt("id");
                                                if (currentSongId == song.getId()) {
                                                    // Get the preview URL of the matched song
                                                    String previewUrl = currentSong.getString("preview");

                                                    // Play the preview
                                                    playPreview(previewUrl);

                                                    // Exit the loop after finding the match
                                                    return;
                                                }
                                            }

                                            // If no match is found, show a Snackbar
                                            Snackbar.make(findViewById(android.R.id.content), "No preview available for this song", Snackbar.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> {
                                        // Handle error in fetching tracks
                                        error.printStackTrace();
                                    });
                            queue.add(request); // Add the tracks request
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }

                return false;
            });

        }
        private void playPreview(String previewUrl) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(previewUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Error playing preview", Snackbar.LENGTH_SHORT).show();
            }
        }

        /**
         * Gets the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return songsList.size();
        }
    }

    /**
     * Initializes the options menu in the app bar.
     *
     * @param menu The options menu in which you place your items.
     * @return true for the menu to be displayed; false for it to be hidden.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deezer_menu, menu);
        return true;
    }

    /**
     * Called when a menu item is selected. Handles actions based on the selected menu item.
     *
     * @param item The menu item that was selected.
     * @return true to consume the event here; false to allow normal menu processing to proceed.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sunrise:
                Intent sunIntent = new Intent(this, SunActivity.class);
                startActivity(sunIntent);
                break;
            case R.id.dictionary:
                startActivity(new Intent(this, DictionaryActivity.class));
                break;
            case R.id.recipe:
                startActivity(new Intent(this, RecipeMainActivity.class));
                break;
        }
        return true;
    }
}