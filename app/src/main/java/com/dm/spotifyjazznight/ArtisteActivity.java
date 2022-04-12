package com.dm.spotifyjazznight;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;

public class ArtisteActivity extends AppCompatActivity {

    Vector<Hashtable<String, Object>> vecteurArtistes = new Vector<>();
    ListView listeArtiste;

    // Dans cette activité, on offrira à l'utilisateur la possibilité
    // de choisir une figure importante du jazz à écouter
    // Une activité boomerang (une fois le nom de l'artiste sélectionné,
    // on revient dans l'activité dédié au lecteur (MainActivity))

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artiste);

        remplirListeArtiste(vecteurArtistes);

        listeArtiste = findViewById(R.id.listeArtiste);
        int[] conteneurs = {R.id.portrait, R.id.artiste, R.id.date, R.id.instrument};

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                vecteurArtistes,
                R.layout.artisteitem,
                new String[]{"image", "artiste", "date", "instrument"},
                conteneurs);

        listeArtiste.setAdapter(simpleAdapter);
    }

    // Fonction permettant de remplir le Vector<Hashtable> passé en paramètres
    public void remplirListeArtiste(Vector<Hashtable<String, Object>> vecteur) {
        vecteur.add(creerArtiste(R.drawable.milesdavis, "Miles Davis", "1926-1991", "Trompettiste"));
        vecteur.add(creerArtiste(R.drawable.davebrubeck, "Dave Brubeck", "1920-2012", "Pianiste"));
        vecteur.add(creerArtiste(R.drawable.charlieparker, "Charlie Parker", "1920-1955", "Saxophoniste"));
        vecteur.add(creerArtiste(R.drawable.cannonballadderley, "Cannonball Adderley", "1928-1975", "Cornettiste"));
        vecteur.add(creerArtiste(R.drawable.sidneybechet, "Sidney Bechet", "1897-1959", "Clarinettiste"));
    }

    // Fonction utilitaire pour générer un artiste à insérer dans la liste
    public Hashtable<String, Object> creerArtiste (int adresseImage, String nom, String date, String instrument) {
        Hashtable<String, Object> artiste = new Hashtable<>(4);
        artiste.put("image", adresseImage);
        artiste.put("artiste", nom);
        artiste.put("date", date);
        artiste.put("instrument", instrument);
        return artiste;
    }
}