package com.j_nel.miniatureimdb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FilmContentList extends AppCompatActivity {
    //Global Variables
    protected TextView tvUserChoice, tvSearchMessage;
    protected Button btnSearchFilm;
    protected RecyclerView rvFilmContent;
    protected String strUserChoice, strSavedStateSearchedTitle;
    protected EditText txtSearchFilm;
    protected String strBaseURL, strURL;
    protected URL url;
    protected ProgressBar pbLoadingList;
    protected View vDimBackground;
    Context context;
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_content_list);
        context = FilmContentList.this;

        tvUserChoice = findViewById(R.id.tvUserChoice);
        rvFilmContent = findViewById(R.id.rvFilmContent);
        btnSearchFilm = findViewById(R.id.btnSearchFilm);
        txtSearchFilm = findViewById(R.id.txtSearchFilm);
        tvSearchMessage = findViewById(R.id.tvSearchMessage);
        pbLoadingList = findViewById(R.id.pbLoadingList);
        pbLoadingList.setVisibility(View.GONE);
        vDimBackground = findViewById(R.id.vDimBackground);
        vDimBackground.setVisibility(View.GONE);

        //Retrieve the user's choice from MainActivity.class and set it as the heading on the FilmList view.
        intent = getIntent();
        strSavedStateSearchedTitle = intent.getStringExtra("SearchedTitle");
        strUserChoice = intent.getStringExtra("UserChoice");
        tvUserChoice.setText(strUserChoice);
        UpdateSharedPreferences(); //We run an update early so that the view is correct, should the user close the app before running a search.

        //Add an if statement to accommodate the Search field functionality if SharedPreferences aren't null.
        if (strSavedStateSearchedTitle != null && !strSavedStateSearchedTitle.isEmpty()) {
            txtSearchFilm.setText(strSavedStateSearchedTitle);
            TriggerGetFilmContent();
        }

        //Configure btnSearchFilm to initiate web service request as well as hide the keyboard.
        btnSearchFilm.setOnClickListener(view -> {
            if (TextUtils.isEmpty(txtSearchFilm.getEditableText().toString())) {
                Toast.makeText(context, "Please enter in a film title.", Toast.LENGTH_SHORT).show();
            } else {
                TriggerGetFilmContent();
            }
        });
    }

    public void TriggerGetFilmContent() {
        //Because we have added the new SavedUserState SharedPreferences, this method has been created to assist
        //the Search functionality as it was originally only triggered by the onClick() event.
        tvSearchMessage.setVisibility(View.GONE);
        btnSearchFilm.setVisibility(View.INVISIBLE);
        pbLoadingList.setVisibility(View.VISIBLE);
        vDimBackground.setVisibility(View.VISIBLE);
        url = CreateURL(txtSearchFilm.getEditableText().toString().toLowerCase().trim(), strUserChoice);
        if (url != null) {
            DismissKeyboard(txtSearchFilm);
            GetFilmContent getFilmContent = new GetFilmContent();
            getFilmContent.execute(url);
        } else {
            Toast.makeText(context, "The URL is malformed. Please check your search title.", Toast.LENGTH_LONG).show();
        }
    }

    private void DismissKeyboard(View view) {
        //Dismiss the keyboard once the user has clicked the search button.
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private URL CreateURL(String searchItem, String UserChoice) {
        //Create the URL that we'll send to OMDb API.
        String strSearch = "";
        String strYear = "";
        if (searchItem.contains(",")) {
            strSearch = searchItem.substring(0, searchItem.indexOf(",")).trim();
            strYear = searchItem.substring(searchItem.indexOf(",") + 1).trim();
        }

        try {
            if (UserChoice.toLowerCase().equals("series")) {
                strBaseURL = getString(R.string.web_service_url_series);
            } else {
                strBaseURL = getString(R.string.web_service_url_movie);
            }

            if (!strYear.equals("") && !strSearch.equals("")) {
                strURL = strBaseURL + "&s=" + strSearch + "&y=" + strYear + "&apikey=" + getString(R.string.api_key);
            } else {
                strURL = strBaseURL + "&s=" + searchItem + "&apikey=" + getString(R.string.api_key);
            }
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
        protected FilmContentAdapter filmContentAdapter;
        protected ArrayList<FilmContentModel> filmContentList;

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
            //Process JSON response object and update the RecyclerView.
            convertJSONtoArrayList(film);
            pbLoadingList.setVisibility(View.GONE);
            vDimBackground.setVisibility(View.GONE);
            btnSearchFilm.setVisibility(View.VISIBLE);
            UpdateSharedPreferences(); //Update again so that SearchedTitle variable can be updated.

            //Bind filmContentList to rvFilmContent.
            filmContentAdapter = new FilmContentAdapter(filmContentList);
            if (filmContentAdapter.getItemCount() > 0) {
                rvFilmContent.setLayoutManager(new LinearLayoutManager(context));
                rvFilmContent.setItemAnimator(new DefaultItemAnimator());
                rvFilmContent.setAdapter(filmContentAdapter);
                filmContentAdapter.notifyDataSetChanged();
                rvFilmContent.smoothScrollToPosition(0);
            } else {
                rvFilmContent.setAdapter(filmContentAdapter);
                filmContentList.clear();
                filmContentAdapter.notifyDataSetChanged();
                tvSearchMessage.setVisibility(View.VISIBLE);
                tvSearchMessage.setText(R.string.no_data_returned);
            }
        }

        protected void convertJSONtoArrayList(JSONObject selectedFilm) {
            //Create film objects from the JSONObjects that we have retrieved.
            filmContentList = new ArrayList<>();
            try {
                jsonArray = selectedFilm.getJSONArray("Search");
                if (jsonArray.length() > 0) {
                    for (int x = 0; x < jsonArray.length(); x++) {
                        jsonObject = jsonArray.getJSONObject(x);
                        //List - only these 5 variables are passed back on the POST request.
                        filmContentList.add(new FilmContentModel(
                                jsonObject.getString("Title"),
                                jsonObject.getString("Year"),
                                jsonObject.getString("imdbID"),
                                jsonObject.getString("Type"),
                                jsonObject.getString("Poster")
                        ));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void UpdateSharedPreferences(){
        //Save the User/Instance State in SharedPreferences.
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences("SavedUserState", MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("View", "List");
        sharedPreferencesEditor.putString("SearchedTitle", txtSearchFilm.getEditableText().toString().toLowerCase().trim());
        sharedPreferencesEditor.putString("UserChoice", strUserChoice);
        sharedPreferencesEditor.apply();
    }

    @Override
    public void onBackPressed() {
        //Because of the SavedUserState SharedPreferences, we have to program the back button manually.
        super.onBackPressed();
        intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}