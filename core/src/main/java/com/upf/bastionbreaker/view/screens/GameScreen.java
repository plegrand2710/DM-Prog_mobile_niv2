package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private ParallaxBackground parallaxBackground;
    private SpriteBatch batch;

    @Override
    public void show() {
        System.out.println("✅ Initialisation de GameScreen...");

        TextureManager.load();

        try {
            // Charger la carte `.tmx`
            mapRenderer = new MapRenderer("map/bastion_breaker_map.tmx");
            System.out.println("✅ Map chargée avec succès !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger la carte !");
            e.printStackTrace();
        }

        try {
            // Charger le background parallaxe
            parallaxBackground = new ParallaxBackground();
            System.out.println("✅ Parallaxe chargé avec succès !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger le background parallaxe !");
            e.printStackTrace();
        }

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        // Nettoyer l'écran et définir une couleur de fond
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (parallaxBackground != null) {
            parallaxBackground.update(mapRenderer.getCamera().position.x);
            parallaxBackground.render(batch);
        } else {
            System.out.println("❌ ERREUR : `parallaxBackground` est NULL !");
        }

        batch.end();

        if (mapRenderer != null) {
            mapRenderer.update(delta);
            mapRenderer.render();
        } else {
            System.out.println("❌ ERREUR : `mapRenderer` est NULL !");
        }
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("🔄 Resize GameScreen : " + width + "x" + height);
        if (mapRenderer != null) {
            mapRenderer.getCamera().viewportWidth = width / 32f;
            mapRenderer.getCamera().viewportHeight = height / 32f;
            mapRenderer.getCamera().update();
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        System.out.println("🚀 Nettoyage de GameScreen...");
        if (mapRenderer != null) mapRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
