package com.upf.bastionbreaker.controller.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ShootingController {
    private Stage stage;
    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin skin;

    public ShootingController(float x, float y, float width, float height) {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin();
        skin.add("touchBackground", new Texture("images/touchBackground.png"));
        skin.add("touchKnob", new Texture("images/touchKnob.png"));
        touchpadStyle = new TouchpadStyle();
        Drawable background = skin.getDrawable("touchBackground");
        Drawable knob = skin.getDrawable("touchKnob");
        touchpadStyle.background = background;
        touchpadStyle.knob = knob;
        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(x, y, width, height);
        stage.addActor(touchpad);
        Gdx.input.setInputProcessor(stage);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public float getAimKnobX() {
        return touchpad.getKnobPercentX();
    }

    public float getAimKnobY() {
        return touchpad.getKnobPercentY();
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
