package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pawfight.game.engine.CommunVariable;

public class HudStage {
    private Stage stage;

    public HudStage() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, CommunVariable.GET_LARGURA_TELA_BASE(), CommunVariable.GET_ALTURA_TELA_BASE());

        FitViewport viewport = new FitViewport(
            CommunVariable.GET_LARGURA_TELA_BASE(),
            CommunVariable.GET_ALTURA_TELA_BASE(),
            camera
        );
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }

    public Stage getStage() {
        return stage;
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        Gdx.input.setInputProcessor(stage);
    }

    public void dispose() {
        stage.dispose();
    }
}
