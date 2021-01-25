package com.j_nel.miniatureimdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //Global Variables
    protected Button btnSeries, btnMovies;
    Context context;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        //Save the User/Instance State in SharedPreferences.
        SharedPreferences.Editor sharedPreferencesEditor = getSharedPreferences("SavedUserState", MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("View", "Main");
        sharedPreferencesEditor.apply();

        btnSeries = findViewById(R.id.btnSeries);
        btnMovies = findViewById(R.id.btnMovie);

        btnSeries.setOnClickListener(view -> MoveToFilmList(getString(R.string.series)));
        btnMovies.setOnClickListener(view -> MoveToFilmList(getString(R.string.movie)));
    }

    public void MoveToFilmList(String userChoice) {
        //Move from MainActivity.class to FilmList.class, taking the user's choice as an extra variable.
        intent = new Intent(context, FilmContentList.class);
        intent.putExtra("UserChoice", userChoice);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Because of the SavedUserState SharedPreferences, we are not allowing the user to click the back button on the
        //MainActivity class as it will mess up the view stack.
    }
}