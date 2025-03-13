package com.upf.bastionbreaker.android;

import android.os.Bundle;
import android.util.Log;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.upf.bastionbreaker.MainPauline;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    private MainPauline mainGame; // Stocker l'instance du jeu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();

        // Mode immersif (plein écran)
        configuration.useImmersiveMode = true;

        // Initialisation du jeu
        mainGame = new MainPauline();
        initialize(mainGame, configuration);

        Log.d("AndroidLauncher", "✅ Application Android lancée avec succès !");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AndroidLauncher", "▶️ Application reprise");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("AndroidLauncher", "⏸️ Application en pause");
    }
}
