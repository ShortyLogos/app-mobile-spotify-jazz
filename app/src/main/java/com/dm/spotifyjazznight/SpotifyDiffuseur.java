package com.dm.spotifyjazznight;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.concurrent.atomic.AtomicReference;

import static com.spotify.protocol.types.Image.Dimension.SMALL;
import static com.spotify.protocol.types.Image.Dimension.THUMBNAIL;

public class SpotifyDiffuseur {
    private static final String CLIENT_ID = "e67bfd48396d49068012ce28816b7076";
    private static final String REDIRECT_URI = "com.dm.spotifyjazznight://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private PlayerApi lecteur;
    private PlayerState etatLecteur;
    private Context contexte;
    private String lectureArtisteURI;
    private boolean lecture = false;
    Bitmap couverture = null;

    public SpotifyDiffuseur(Context contexte) {
        this.contexte = contexte;
    }

    public void seConnecter() {
        // On fera notre connection ici
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(contexte, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;   // Objet de base de l'API
                        Log.d("MainActivity", "Connected! Yay!");

                        lecteur = mSpotifyAppRemote.getPlayerApi();
                        // Now you can start interacting with App Remote
                        connecter();
                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                        mSpotifyAppRemote = null;
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    public void seDeconnecter() {
        SpotifyAppRemote.disconnect(getSpotifyAppRemote());
    }

    private void connecter() {
        lecteur.play(lectureArtisteURI);
        lecture = true;

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()   // PlayerState : la toune qui est en train de jouer
                .setEventCallback(new CallbackChangementChanson());
    }

    public SpotifyAppRemote getSpotifyAppRemote() {
        return mSpotifyAppRemote;
    }

    public void jouer() {
        if (lecture) {
            lecteur.pause();
        }
        else { lecteur.resume(); }

        lecture = !lecture;
    }

    public void prochaineChanson() {
        if (!lecture) { lecture = true; }
        lecteur.skipNext();
    }

    public String getlectureArtisteURI() {
        return lectureArtisteURI;
    }

    public void setlectureArtisteURI(String lectureArtisteURI) {
        this.lectureArtisteURI = lectureArtisteURI;
    }

    public boolean isLecture() {
        return lecture;
    }

    private class CallbackChangementChanson implements Subscription.EventCallback<PlayerState> {

        @Override
        public void onEvent(PlayerState data) {
            final Track track = data.track;
            if (track != null) {
                // Lors du changement de l'état de lecteur, comme lors d'un changement de chanson,
                // on rafraîchit l'affichage des informations pertinentes : titre, artiste, album, image de l'album...
                mSpotifyAppRemote.getImagesApi().getImage(track.imageUri, SMALL).setResultCallback(bitmap -> {
                    couverture = bitmap;
                });

                ((MainActivity)contexte).rafraichirAffichage(track, couverture);
                Log.d("MainActivity", track.name + " by " + track.artist.name);
            }
        }
    }
}
