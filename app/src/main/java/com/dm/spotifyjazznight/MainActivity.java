package com.dm.spotifyjazznight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    ActivityResultLauncher<Intent> lanceur;

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
            lanceur.launch(new Intent(this, ArtisteActivity.class));
        });

        btnPlay.setOnClickListener(v -> {
            jouer();
        });

        btnNext.setOnClickListener(v -> {
            spotifyDiffuseur.prochaineChanson();
        });

        lanceur = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new CallBackChoixArtiste()
        );
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

    private class CallBackChoixArtiste implements ActivityResultCallback<ActivityResult> {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == 17) {
                String artisteURI = result.getData().getStringExtra("artisteURI");
                spotifyDiffuseur.setlectureArtisteURI(artisteURI);
                btnPlay.setBackgroundResource(R.drawable.pause_button);
                Log.d("MainActivity", "J'EXISTE " + artisteURI);
            }
        }
    }
}

