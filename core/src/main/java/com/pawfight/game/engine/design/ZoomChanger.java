package com.pawfight.game.engine.design;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class ZoomChanger {
    private float zoom = 0.5f;

    public float changeZoom() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            if (zoom < 0.7f) {
                zoom += 0.1f;
                Gdx.app.log("PawFight", "Zoom aumentado: " + zoom);
            } else {
                Gdx.app.log("PawFight", "Zoom máximo atingido: " + zoom);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            if (zoom > 0.3f) {
                zoom -= 0.1f;
                Gdx.app.log("PawFight", "Zoom diminuído: " + zoom);
            } else {
                Gdx.app.log("PawFight", "Zoom mínimo atingido: " + zoom);
            }
        }

        return zoom;
    }
}
