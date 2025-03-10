package com.upf.bastionbreaker;

import com.badlogic.gdx.Game;
import com.upf.bastionbreaker.view.screens.GameScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen()); // Définit GameScreen comme écran principal
    }
}
