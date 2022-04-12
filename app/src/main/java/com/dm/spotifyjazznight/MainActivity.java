package com.dm.spotifyjazznight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.concurrent.atomic.AtomicReference;

// METTRE NOTRE PlayerApi dans une variable

// On va devoir partir un thread pour un SeekBar

public class MainActivity extends AppCompatActivity {

    Button btnChoisirArtiste;
    ImageView btnPlay;
    ImageView btnNext;
    ImageView couvertureAlbum;
    TextView titreChanson;
    TextView nomArtiste;
    TextView nomAlbum;

    private SpotifyDiffuseur spotifyDiffuseur;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spotifyDiffuseur = new SpotifyDiffuseur(this);

        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        couvertureAlbum = findViewById(R.id.couvertureAlbum);
        titreChanson = findViewById(R.id.titreChanson);
        nomArtiste = findViewById(R.id.nomArtiste);
        nomAlbum = findViewById(R.id.nomAlbum);

        btnChoisirArtiste = findViewById(R.id.btnChoisirArtiste);

        btnChoisirArtiste.setOnClickListener(v -> {
            Intent choixArtiste = new Intent(this, ArtisteActivity.class);
            startActivity(choixArtiste);
        });

        btnPlay.setOnClickListener(v -> {
            jouer();
        });

        btnNext.setOnClickListener(v -> {
            spotifyDiffuseur.prochaineChanson();
        });

        btnNext.setOnLongClickListener(v -> {
            spotifyDiffuseur.avanceRapide();
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        spotifyDiffuseur.seConnecter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        spotifyDiffuseur.seDeconnecter();
    }

    private void jouer() {
        spotifyDiffuseur.jouer();
        if (spotifyDiffuseur.isLecture()) {
            btnPlay.setBackgroundResource(R.drawable.pause_button);
        }
        else {
            btnPlay.setBackgroundResource(R.drawable.play_button);
        }
    }

    public void rafraichirAffichage(Track chanson, Bitmap couverture) {
        titreChanson.setText(chanson.name.toString());
        nomAlbum.setText(chanson.album.name.toString());
        nomArtiste.setText(chanson.artist.name.toString());
        couvertureAlbum.setImageBitmap(couverture);
    }

//    private void connected() {
//        // Play a playlist
//        mSpotifyAppRemote.getPlayerApi().play("spotify:artist:4iRZAbYvBqnxrbs6K25aJ7");   // .getPlayerApi() retourn un objet PlayerApi
//
////         Subscribe to PlayerState : une classe également
////         Appelé à chaque fois qu'on pèse sur player, stop, next --> quand le state est changé
//        mSpotifyAppRemote.getPlayerApi()
//                .subscribeToPlayerState()   // PlayerState : la toune qui est en train de jouer
//                .setEventCallback(playerState -> {  // Un callback qui indiquera si l'état de playerState a changé
//                    final Track track = playerState.track; // Objet Track qui existe (voir l'API pour tous les objets) -> album, artist, etc...
//                    if (track != null) {
//                        // On va changer des affichages de champs textes et d'images dans le TP
//                        Log.d("MainActivity", track.name + " by " + track.artist.name);
//                    }
//                });
//    }
}

