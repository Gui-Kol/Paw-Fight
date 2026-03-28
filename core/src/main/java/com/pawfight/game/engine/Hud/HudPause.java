package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.design.animation.AnimationEngine;
import com.pawfight.game.engine.design.desenhar.DesenharTextura;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.WorldTemplate;

import static com.pawfight.game.engine.CommunVariable.GET_ALTURA_TELA_BASE;
import static com.pawfight.game.engine.CommunVariable.GET_LARGURA_TELA_BASE;

public class HudPause {
    private final HudStage hudStage;
    private final DesenharTextura desenharTextura;
    private final AnimationEngine animationEngine;
    private final CreateButton createButton;
    private final Texture fundo;


    private ImageButton quitButton;
    private ImageButton saveButton;
    private ImageButton resumeButton;
    private ImageButton settingsButton;

    private final Texture normalTextureQuit = new Texture("menu/button/quit/quit1.png");
    private final Texture hoverTextureQuit = new Texture("menu/button/quit/quit2.png");
    private final Texture pressedTextureQuit = new Texture("menu/button/quit/quit3.png");

    private final Texture normalTextureSave = new Texture("menu/button/save/save1.png");
    private final Texture hoverTextureSave = new Texture("menu/button/save/save2.png");
    private final Texture pressedTextureSave = new Texture("menu/button/save/save3.png");

    private final Texture normalTextureResume = new Texture("menu/button/resume/resume1.png");
    private final Texture hoverTextureResume = new Texture("menu/button/resume/resume2.png");
    private final Texture pressedTextureResume = new Texture("menu/button/resume/resume3.png");

    private final Texture normalTextureSettings = new Texture("menu/button/settings/settings1.png");
    private final Texture hoverTextureSettings = new Texture("menu/button/settings/settings2.png");
    private final Texture pressedTextureSettings = new Texture("menu/button/settings/settings3.png");

    public HudPause(PlayerTemplate player) {
        createButton = new CreateButton();
        desenharTextura = new DesenharTextura();
        animationEngine = new AnimationEngine();
        hudStage = new HudStage();
        fundo = new Texture("menu/pause/pauseFundo-Sheet.png");
        botoes(player);
        Gdx.app.log("HudPause", "Sendo carregado para ser desenhado...");
    }

    public void draw(WorldTemplate world) {
        Batch batch = world.getBatch();
        boolean jogoPausado = world.getPlayer().isPause();

        DefinirSprite animacao = new DefinirSprite(fundo, 6, 0.05f, false, false);
        boolean renderizaBotao = animationEngine.desenharFundo(
            batch, jogoPausado, animacao, 1024, hudStage.getStage().getCamera());
        if (jogoPausado) {
            hudStage.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            if (renderizaBotao) {
                hudStage.render();
            }
        }
    }

    public void botoes(PlayerTemplate player) {
        if (quitButton == null) {
            try {
                Stage stage = hudStage.getStage();

                resumeButton = createButton.create(stage,
                    GET_LARGURA_TELA_BASE() / 2,
                    GET_ALTURA_TELA_BASE() / 2 + 300,
                    200, 105,
                    normalTextureResume, hoverTextureResume, pressedTextureResume);

                resumeButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        player.setPause(false);
                    }
                });

                saveButton = createButton.create(stage,
                    GET_LARGURA_TELA_BASE() / 2,
                    GET_ALTURA_TELA_BASE() / 2 + 150,
                    200, 105,
                    normalTextureSave, hoverTextureSave, pressedTextureSave);

                saveButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        player.saveData();
                    }
                });

                settingsButton = createButton.create(stage,
                    GET_LARGURA_TELA_BASE() / 2,
                    GET_ALTURA_TELA_BASE() / 2 + 0,
                    200, 105,
                    normalTextureSettings, hoverTextureSettings, pressedTextureSettings);

                settingsButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        player.setPause(false);
                    }
                });

                quitButton = createButton.create(stage,
                    GET_LARGURA_TELA_BASE() / 2,
                    GET_ALTURA_TELA_BASE() / 2 - 150,
                    200, 102,
                    normalTextureQuit, hoverTextureQuit, pressedTextureQuit);

                quitButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Gdx.app.exit();
                    }
                });
            } catch (Exception e) {
                Gdx.app.error("HudPause", "Erro ao criar botão: " + e.getMessage(), e);
            }
        }
    }


}
