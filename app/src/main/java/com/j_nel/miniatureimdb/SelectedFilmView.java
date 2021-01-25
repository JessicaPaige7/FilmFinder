package com.j_nel.miniatureimdb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedFilmView extends AppCompatActivity {
    //Global Variables
    Context context;
    Intent intent;
    protected String strFilmID, strBaseURL, strURL, strSavedStateSearchedTitle = "", strSavedStateUserChoice = "";
    protected ImageView ivFilmPoster;
    protected TextView tvFilmTitle, tvFilmDirector, tvFilmActors, tvFilmRuntime, tvFilmReleased, tvFilmPlot, tvFilmLanguage;
    protected URL url;
    protected ProgressBar pbLoadingList;
    protected View vDimBackground;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_film_view);
        context = SelectedFilmView.this;

        ivFilmPoster = findViewById(R.id.imgFilmPoster);
        tvFilmTitle = findViewById(R.id.tvFilmTitle);
        tvFilmDirector = findViewById(R.id.tvFilmDirector);
        tvFilmActors = findViewById(R.id.tvFilmActors);
        tvFilmRuntime = findViewById(R.id.tvFilmRuntime);
        tvFilmReleased = findViewById(R.id.tvFilmReleased);
        tvFilmPlot = findViewById(R.id.tvFilmPlot);
        tvFilmLanguage = findViewById(R.id.tvFilmLanguage);
        pbLoadingList = findViewById(R.id.pbLoadingList);
        vDimBackground = findViewById(R.id.vDimBackground);

        //Retrieve the film's imdbID from FilmContentList.class and make a new web service request to return the data for that movie.
        intent = getIntent();
        strFilmID = intent.getStringExtra("FilmID").trim();
        url = CompleteURL(strFilmID);

        if (url != null) {
            GetFilmContent getFilmContent = new GetFilmContent();
            getFilmContent.execute(url);
        } else {
            //Return user to List
            Toast.makeText(context, "The URL is malformed. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateSharedPreferences() {
        //Save the User/Instance State in SharedPreferences.
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences("SavedUserState", MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("View", "View");
        sharedPreferencesEditor.putString("FilmID", strFilmID);
        sharedPreferencesEditor.apply();
    }

    private URL CompleteURL(String FilmID) {
        //Complete the URL that we'll send to OMDb API, now that we have the selected film's ID.
        try {
            strBaseURL = getString(R.string.web_service_url);
            strURL = strBaseURL + "?i=" + FilmID + "&apikey=" + getString(R.string.api_key);
            return new URL(strURL); //Url has been formed.
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; //The URL is malformed.
    }

    private class GetFilmContent extends AsyncTask<URL, Void, JSONObject> {
        //Global Variables
        protected HttpURLConnection connection = null;
        protected int intResponse;
        protected StringBuilder stringBuilder;
        protected String strLine;
        protected JSONArray jsonArray;
        protected JSONObject jsonObject;
        protected List<Rating> lstRating;
        protected FilmContentModel filmContentModel;
        protected Map<String, Bitmap> bitmaps = new HashMap<>();

        @Override
        protected JSONObject doInBackground(URL... params) {
            //Makes the call to the REST web service, collects the film's data and then saves the data to the local HTML file.
            try {
                connection = (HttpURLConnection) params[0].openConnection();
                intResponse = connection.getResponseCode();

                if (intResponse == HttpURLConnection.HTTP_OK) {
                    stringBuilder = new StringBuilder();
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        while ((strLine = bufferedReader.readLine()) != null) {
                            stringBuilder.append(strLine);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new JSONObject(stringBuilder.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(JSONObject film) {
            //Process JSON response object.
            convertJSONtoModel(film);
            SetUIElements(filmContentModel);
            if (filmContentModel != null && !filmContentModel.getPoster().trim().toUpperCase().equals("N/A")) {
                new LoadPosterTask(ivFilmPoster).execute(filmContentModel.getPoster());
            }
            UpdateSharedPreferences();
            pbLoadingList.setVisibility(View.GONE);
            vDimBackground.setVisibility(View.GONE);
        }

        protected void convertJSONtoModel(JSONObject selectedFilm) {
            //Populate the FilmContentModel with the data retrieved from JSON Object.
            try {
                //Populate lstRating with the Ratings that are in the JSON Object.
                jsonArray = selectedFilm.getJSONArray("Ratings");
                if (jsonArray.length() > 0) {
                    lstRating = new ArrayList<>();
                    for (int x = 0; x < jsonArray.length(); x++) {
                        jsonObject = jsonArray.getJSONObject(x);
                        //LIST - only these 5 variables are passed back on the POST request.
                        lstRating.add(new Rating(
                                jsonObject.getString("Source"),
                                jsonObject.getString("Value")
                        ));
                    }
                }

                //FilmView JSON Object variables
                filmContentModel = new FilmContentModel(
                        selectedFilm.getString("Title"),
                        selectedFilm.getString("Rated"),
                        selectedFilm.getString("Released"),
                        selectedFilm.getString("Runtime"),
                        selectedFilm.getString("Genre"),
                        selectedFilm.getString("Director"),
                        selectedFilm.getString("Actors"),
                        selectedFilm.getString("Plot"),
                        selectedFilm.getString("Language"),
                        selectedFilm.getString("Poster"),
                        lstRating,
                        selectedFilm.getString("Type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class LoadPosterTask extends AsyncTask<String, Void, Bitmap> {
        //Retrieve image for each item on an AsyncTask.
        private final ImageView imageView;
        private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public LoadPosterTask(ImageView imageView) {
            //Store image
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            HttpURLConnection connection;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    additionalProperties.put(strings[0], bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void SetUIElements(FilmContentModel filmContentModel) {
        tvFilmTitle.setText(filmContentModel.getTitle());
        tvFilmDirector.setText(filmContentModel.getDirector());
        tvFilmActors.setText(filmContentModel.getActors());
        tvFilmRuntime.setText(filmContentModel.getRuntime());
        tvFilmLanguage.setText(filmContentModel.getLanguage());
        tvFilmPlot.setText(filmContentModel.getPlot());
        tvFilmReleased.setText(filmContentModel.getReleased());
    }

    @Override
    public void onBackPressed() {
        //Because of the SavedUserState SharedPreferences, we have to program the back button manually.
        super.onBackPressed();
        intent = new Intent(context, FilmContentList.class);

        //Reading from SharedPreferences to pass the correct variables into the intent when the user click back.
        SharedPreferences sharedPreferences = context.getSharedPreferences("SavedUserState", MODE_PRIVATE);
        strSavedStateUserChoice = sharedPreferences.getString("UserChoice", "Movie");
        strSavedStateSearchedTitle = sharedPreferences.getString("SearchedTitle", "");

        intent.putExtra("UserChoice", strSavedStateUserChoice);
        intent.putExtra("SearchedTitle", strSavedStateSearchedTitle);
        context.startActivity(intent);
    }
}
