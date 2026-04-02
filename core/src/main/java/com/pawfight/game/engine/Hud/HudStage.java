package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static com.pawfight.game.engine.VariavelComum.GET_ALTURA_TELA_BASE;
import static com.pawfight.game.engine.VariavelComum.GET_LARGURA_TELA_BASE;

public class HudStage {
    private Stage stage;

    public HudStage() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, GET_LARGURA_TELA_BASE, GET_ALTURA_TELA_BASE);

        FitViewport viewport = new FitViewport(
            GET_LARGURA_TELA_BASE,
            GET_ALTURA_TELA_BASE,
            camera
        );
        stage = new Stage(viewport);
        registerInputProcessor();
    }

    private void registerInputProcessor() {
        InputProcessor current = Gdx.input.getInputProcessor();
        if (current instanceof InputMultiplexer) {
            InputMultiplexer multiplexer = (InputMultiplexer) current;
            if (!multiplexer.getProcessors().contains(stage, true)) {
                multiplexer.addProcessor(0, stage); // HUD tem prioridade
            }
        } else {
            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(stage); // HUD tem prioridade
            if (current != null) {
                multiplexer.addProcessor(current);
            }
            Gdx.input.setInputProcessor(multiplexer);
        }
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
        // Não redefine o InputProcessor — o InputMultiplexer já está configurado.
    }

    public void dispose() {
        // Remove este stage do InputMultiplexer ao fazer dispose
        InputProcessor current = Gdx.input.getInputProcessor();
        if (current instanceof InputMultiplexer) {
            ((InputMultiplexer) current).removeProcessor(stage);
        }
        stage.dispose();
    }
}
