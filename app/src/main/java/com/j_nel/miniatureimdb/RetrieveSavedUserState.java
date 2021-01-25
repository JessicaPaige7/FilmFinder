package com.j_nel.miniatureimdb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RetrieveSavedUserState extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveSavedUserState(RetrieveSavedUserState.this);
    }

    public void retrieveSavedUserState(Context context) {
        String strView, strUserChoice, strSearchedTitle, strFilmID;
        Intent intent;
        SharedPreferences sharedPreferences = context.getSharedPreferences("SavedUserState", MODE_PRIVATE);
        // Reading from SharedPreferences
        strView = sharedPreferences.getString("View", "Main");
        strUserChoice = sharedPreferences.getString("UserChoice", "Movie");
        strSearchedTitle = sharedPreferences.getString("SearchedTitle", "");
        strFilmID = sharedPreferences.getString("FilmID", "");

        if (strView.toLowerCase().equals("list")) {
            intent = new Intent(context, FilmContentList.class);
            intent.putExtra("UserChoice", strUserChoice);
            intent.putExtra("SearchedTitle", strSearchedTitle);
            context.startActivity(intent);
        } else if (strView.toLowerCase().equals("view")) {
            intent = new Intent(context, SelectedFilmView.class);
            intent.putExtra("FilmID", strFilmID);
            context.startActivity(intent);
        } else {
            intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }
}
