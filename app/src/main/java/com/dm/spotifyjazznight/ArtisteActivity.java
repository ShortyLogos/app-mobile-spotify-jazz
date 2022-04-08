package com.dm.spotifyjazznight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ArtisteActivity extends AppCompatActivity {

    // Dans cette activité, on offrira à l'utilisateur la possibilité
    // de choisir une figure importante du jazz à écouter
    // Une activité boomerang (une fois le nom de l'artiste sélectionné, on revient dans l'activité dédié au lecteur (MainActivity)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artiste);
    }
}