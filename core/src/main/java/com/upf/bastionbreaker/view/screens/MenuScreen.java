package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.upf.bastionbreaker.view.ui.PauseMenu;

public class MenuScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private TextureAtlas backgroundAtlas;
    private TextureAtlas gameAtlas;
    private TextureRegion background;
    private TextureRegion buttonContainer;
    private Stage stage;
    private ImageButton gyroButton;
    private ImageButton touchpadButton;
    private BitmapFont font;
    private PauseMenu pauseMenu;

    public MenuScreen(Game game) {
        this.game = game;
        this.pauseMenu = new PauseMenu();
    }




    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundAtlas = new TextureAtlas(Gdx.files.internal("atlas/background/background.atlas"));
        gameAtlas = new TextureAtlas(Gdx.files.internal("atlas/game/game.atlas"));
        background = backgroundAtlas.findRegion("backgroundScreenResize");
        buttonContainer = gameAtlas.findRegion("Button-Dummy@2x");

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        TextureRegionDrawable buttonContainerDrawable = new TextureRegionDrawable(buttonContainer);
        Image buttonContainerImage = new Image(buttonContainerDrawable);

        float containerScale = 2.7f;
        float containerWidth = buttonContainer.getRegionWidth() * containerScale;
        float containerHeight = buttonContainer.getRegionHeight() * containerScale;

        buttonContainerImage.setSize(containerWidth, containerHeight);
        float containerX = (screenWidth - containerWidth) / 2;
        float containerY = (0.3472f * screenHeight) - 200;

        Gdx.app.log("DEBUG_MENU", "screenHeight = " + Gdx.graphics.getHeight());

        buttonContainerImage.setPosition(containerX, containerY);
        stage.addActor(buttonContainerImage);

        TextureRegionDrawable logoDrawable = new TextureRegionDrawable(gameAtlas.findRegion("bastion_breaker_logo"));
        Image logoImage = new Image(logoDrawable);

        float logoScale = 0.3f;
        float logoWidth = gameAtlas.findRegion("bastion_breaker_logo").getRegionWidth() * logoScale;
        float logoHeight = gameAtlas.findRegion("bastion_breaker_logo").getRegionHeight() * logoScale;

        logoImage.setSize(logoWidth, logoHeight);
        float logoX = (screenWidth - logoWidth) / 2;
        float logoY = containerY + containerHeight * 0.45f;
        logoImage.setPosition(logoX, logoY);
        stage.addActor(logoImage);

        TextureRegionDrawable gyroDrawable = new TextureRegionDrawable(gameAtlas.findRegion("Rect-Rect-Default"));
        TextureRegionDrawable touchpadDrawable = new TextureRegionDrawable(gameAtlas.findRegion("Rect-Rect-Default"));

        gyroButton = new ImageButton(gyroDrawable);
        touchpadButton = new ImageButton(touchpadDrawable);

        float buttonWidth = containerWidth * 0.6f;
        float buttonHeight = containerHeight * 0.20f;
        float buttonX = containerX + (containerWidth - buttonWidth) / 2;
        float gyroY = containerY + containerHeight * 0.25f;
        float touchpadY = gyroY - buttonHeight * 0.8f;

        gyroButton.setSize(buttonWidth, buttonHeight);
        gyroButton.setPosition(buttonX, gyroY);
        touchpadButton.setSize(buttonWidth, buttonHeight);
        touchpadButton.setPosition(buttonX, touchpadY);

        stage.addActor(gyroButton);
        stage.addActor(touchpadButton);

        font = new BitmapFont();
        font.getData().setScale(2);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        Label gyroLabel = new Label("Gyroscopie", labelStyle);
        gyroLabel.setPosition(buttonX + buttonWidth * 0.33f, gyroY + buttonHeight * 0.4f);
        stage.addActor(gyroLabel);

        Label touchpadLabel = new Label("TouchPad", labelStyle);
        touchpadLabel.setPosition(buttonX + buttonWidth * 0.35f, touchpadY + buttonHeight * 0.4f);
        stage.addActor(touchpadLabel);

        gyroButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("DEBUG_MENU", "⚡ Changement d'écran vers PauseMenu");
                game.setScreen(new GameScreen("giroscopic"));
                //pauseMenu.togglePause();
            }
        });

        touchpadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("DEBUG_MENU", "⚡ Changement d'écran vers PauseMenu");
                game.setScreen(new GameScreen("touchpad"));

                //pauseMenu.togglePause();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
        pauseMenu.render(delta);

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundAtlas.dispose();
        gameAtlas.dispose();
        stage.dispose();
        font.dispose();
    }
}
