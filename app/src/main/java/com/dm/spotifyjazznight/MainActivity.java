package com.dm.spotifyjazznight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    Button btnChoisirArtiste;
    Button btnUniversJazz;
    ImageView btnPlay;
    ImageView btnNext;
    ImageView couvertureAlbum;
    TextView titreChanson;
    TextView nomArtiste;
    TextView nomAlbum;
    TextView dureeChanson;
    SeekBar seekBarDuree;
    DureeChanson threadDuree;
    Handler handlerDuree;
    int secondes = 0;
    int minutes = 0;
    String dureeFormattee = String.format("%02d", minutes) + ":" + String.format("%02d", secondes);
    String chansonCourante;
    ActivityResultLauncher<Intent> lanceur;

    private SpotifyDiffuseur spotifyDiffuseur;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        dureeChanson = findViewById(R.id.dureeChanson);
        seekBarDuree = findViewById(R.id.seekBarDuree);
        threadDuree = new DureeChanson();
        handlerDuree = new Handler();

        btnChoisirArtiste = findViewById(R.id.btnChoisirArtiste);
        btnUniversJazz = findViewById(R.id.btnUniversJazz);

        btnChoisirArtiste.setOnClickListener(v -> {
            lanceur.launch(new Intent(this, ArtisteActivity.class));
        });

        btnUniversJazz.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fr.wikipedia.org/wiki/Jazz"));
            startActivity(i);
        });

        btnPlay.setOnClickListener(v -> {
            if (spotifyDiffuseur.getLecteur() != null) {
                jouer();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (spotifyDiffuseur.getLecteur() != null) {
                spotifyDiffuseur.prochaineChanson();
                if (chansonCourante != null && !chansonCourante.equals(spotifyDiffuseur.getNomChanson())) {
                    chansonCourante = spotifyDiffuseur.getNomChanson();
                    reinitialiserDuree();
                    handlerDuree.removeCallbacks(threadDuree);
                    handlerDuree.post(threadDuree);
                }
            }
        });

        lanceur = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new CallBackChoixArtiste()
        );

        seekBarDuree.setMin(1);
        seekBarDuree.setMax(255);
        chansonCourante = spotifyDiffuseur.getNomChanson();
        Log.d("Jazz", "" + chansonCourante);
    }

    @Override
    protected void onStart() {
        super.onStart();
        reinitialiserDuree();
    }

    @Override
    protected void onStop() {
        super.onStop();
        spotifyDiffuseur.seDeconnecter();
    }

    private void jouer() {
        spotifyDiffuseur.jouer();
        chansonCourante = spotifyDiffuseur.getNomChanson();
        if (spotifyDiffuseur.isLecture()) {
            handlerDuree.post(threadDuree);
            btnPlay.setBackgroundResource(R.drawable.pause_button);
        }
        else {
            btnPlay.setBackgroundResource(R.drawable.play_button);
            handlerDuree.removeCallbacks(threadDuree);
        }
    }

    public void rafraichirAffichage(Track chanson, Bitmap couverture) {
        titreChanson.setText(chanson.name.toString());
        nomAlbum.setText(chanson.album.name.toString());
        nomArtiste.setText(chanson.artist.name.toString());
        couvertureAlbum.setImageBitmap(couverture);
    }

    public void reinitialiserDuree() {
        secondes = 0;
        minutes = 0;
        dureeFormattee = String.format("%02d", minutes) + ":" + String.format("%02d", secondes);
        dureeChanson.setText(dureeFormattee);
        seekBarDuree.setProgress(spotifyDiffuseur.getDureeChanson());
    }

    private class CallBackChoixArtiste implements ActivityResultCallback<ActivityResult> {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == 17) {
                handlerDuree.removeCallbacks(threadDuree);
                String artisteURI = result.getData().getStringExtra("artisteURI");
                spotifyDiffuseur.setlectureArtisteURI(artisteURI);
                btnPlay.setBackgroundResource(R.drawable.pause_button);
                spotifyDiffuseur.seConnecter();
                handlerDuree.post(threadDuree);
            }
        }
    }

    private class DureeChanson extends Thread {

        @Override
        public void run() {
            secondes++;
            if (secondes == 60) {
                minutes++;
                secondes = 0;
            }

            dureeFormattee = String.format("%02d", minutes) + ":" + String.format("%02d", secondes);
            dureeChanson.setText(dureeFormattee);
            seekBarDuree.setProgress((minutes * 60) + secondes);
            handlerDuree.postDelayed(threadDuree, 1000); // démarre le thread après le délai
        }
    }
}

