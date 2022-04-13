package com.dm.spotifyjazznight;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    String ArtisteURI;

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
                new String[]{"image", "artiste", "date", "instrument", "artisteURI"},
                conteneurs);

        listeArtiste.setAdapter(simpleAdapter);

        Ecouteur ec = new Ecouteur();
        listeArtiste.setOnItemClickListener(ec);
    }

    // Fonction permettant de remplir le Vector<Hashtable> passé en paramètres
    public void remplirListeArtiste(Vector<Hashtable<String, Object>> vecteur) {
        vecteur.add(creerArtiste(R.drawable.milesdavis, "Miles Davis", "1926-1991", "Trompettiste", "spotify:artist:0kbYTNQb4Pb1rPbbaF0pT4"));
        vecteur.add(creerArtiste(R.drawable.davebrubeck, "Dave Brubeck", "1920-2012", "Pianiste", "spotify:artist:4iRZAbYvBqnxrbs6K25aJ7"));
        vecteur.add(creerArtiste(R.drawable.charlieparker, "Charlie Parker", "1920-1955", "Saxophoniste", "spotify:artist:4Ww5mwS7BWYjoZTUIrMHfC"));
        vecteur.add(creerArtiste(R.drawable.cannonballadderley, "Cannonball Adderley", "1928-1975", "Cornettiste","spotify:artist:5v74mT11KGJqadf9sLw4dA"));
        vecteur.add(creerArtiste(R.drawable.sidneybechet, "Sidney Bechet", "1897-1959", "Clarinettiste", "spotify:artist:1RsmXc1ZqW3WBs9iwxiSwk"));
    }

    // Fonction utilitaire pour générer un artiste à insérer dans la liste
    public Hashtable<String, Object> creerArtiste (int adresseImage, String nom, String date, String instrument, String artisteURI) {
        Hashtable<String, Object> artiste = new Hashtable<>(5);
        artiste.put("image", adresseImage);
        artiste.put("artiste", nom);
        artiste.put("date", date);
        artiste.put("instrument", instrument);
        artiste.put("artisteURI", artisteURI);
        return artiste;
    }

    private class Ecouteur implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArtisteURI = vecteurArtistes.get(position).get("artisteURI").toString();

            Intent i = new Intent();
            i.putExtra("artisteURI", ArtisteURI);
            setResult(17, i);
            finish();
        }
    }
}