package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private TextureAtlas backgroundAtlas;
    private TextureAtlas gameAtlas;
    private TextureRegion background;
    private TextureRegion logo;
    private TextureRegion buttonContainer;
    private Stage stage;
    private ImageButton gyroButton;
    private ImageButton touchpadButton;
    private BitmapFont font;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundAtlas = new TextureAtlas(Gdx.files.internal("atlas/background/background.atlas"));
        gameAtlas = new TextureAtlas(Gdx.files.internal("atlas/game/game.atlas"));
        background = backgroundAtlas.findRegion("backgroundScreenResize");
        logo = gameAtlas.findRegion("bastion_breaker_logo");
        buttonContainer = gameAtlas.findRegion("Button-Dummy@2x");

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        TextureRegionDrawable buttonContainerDrawable = new TextureRegionDrawable(buttonContainer);
        Image buttonContainerImage = new Image(buttonContainerDrawable);

        float containerScale = 2.7f;
        float containerWidth = buttonContainer.getRegionWidth() * containerScale;
        float containerHeight = buttonContainer.getRegionHeight() * containerScale;

        buttonContainerImage.setSize(containerWidth, containerHeight);

        float containerX = (Gdx.graphics.getWidth() - containerWidth) / 2;
        float containerY = ((Gdx.graphics.getHeight() - containerHeight) / 2) + 100;
        buttonContainerImage.setPosition(containerX, containerY);

        stage.addActor(buttonContainerImage);


        TextureRegionDrawable logoDrawable = new TextureRegionDrawable(gameAtlas.findRegion("bastion_breaker_logo"));
        Image logoImage = new Image(logoDrawable);

        float logoScale = 0.3f;
        float logoWidth = gameAtlas.findRegion("bastion_breaker_logo").getRegionWidth() * logoScale;
        float logoHeight = gameAtlas.findRegion("bastion_breaker_logo").getRegionHeight() * logoScale;

        logoImage.setSize(logoWidth, logoHeight);

        float logoX = (Gdx.graphics.getWidth() - logoWidth) / 2;
        float logoY = containerY + buttonContainer.getRegionHeight() + 60;

        logoImage.setPosition(logoX, logoY);
        stage.addActor(logoImage);


        TextureRegionDrawable gyroDrawable = new TextureRegionDrawable(gameAtlas.findRegion("Rect-Rect-Default"));
        ImageButton gyroButton = new ImageButton(gyroDrawable);
        float gyroX = containerX + 295;
        float gyroY = (containerY + buttonContainer.getRegionHeight() / 2) + 100;
        gyroButton.setPosition(gyroX, gyroY);
        stage.addActor(gyroButton);

        TextureRegionDrawable touchpadDrawable = new TextureRegionDrawable(gameAtlas.findRegion("Rect-Rect-Default"));
        ImageButton touchpadButton = new ImageButton(touchpadDrawable);
        float touchpadX = containerX + 295;
        float touchpadY = gyroY - 100;
        touchpadButton.setPosition(touchpadX, touchpadY);
        stage.addActor(touchpadButton);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(2);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label gyroLabel = new Label("Gyroscopie", labelStyle);
        gyroLabel.setPosition(gyroX + 8, gyroY + 35);
        stage.addActor(gyroLabel);

        Label touchpadLabel = new Label("TouchPad", labelStyle);
        touchpadLabel.setPosition(touchpadX + 10, touchpadY + 35);
        stage.addActor(touchpadLabel);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
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
