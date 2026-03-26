package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pawfight.game.engine.design.desenhar.DesenharTextura;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.WorldTemplate;

import static com.pawfight.game.engine.CommunVariable.GET_ALTURA_TELA_BASE;
import static com.pawfight.game.engine.CommunVariable.GET_LARGURA_TELA_BASE;

public class HudPause {
    private final DesenharTextura desenharTextura;
    private final CreateButton createButton;
    private final Texture fundo;

    public HudPause() {
        createButton = new CreateButton();
        desenharTextura = new DesenharTextura();
        fundo = new Texture("Hud/coin.png");
        Gdx.app.log("HudPause", "Sendo carregado para ser desenhado...");
    }

    public void draw(WorldTemplate world) {
        Batch batch = world.getBatch();
        Stage stage = world.getStage();
        PlayerTemplate player = world.getPlayer();
        if (player.isPause()) {
            batch.setProjectionMatrix(player.getHud().getHudCamera().combined);
            batch.begin();
            desenharFundo(batch);
            batch.end();
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void desenharFundo(Batch batch) {
        int tamanhoFundo = 512;
        int x = GET_LARGURA_TELA_BASE() / 2 - tamanhoFundo / 2;
        int y = GET_ALTURA_TELA_BASE() / 2 - tamanhoFundo / 2;


        desenharTextura.desenhar(batch, fundo, null, x, y, tamanhoFundo);
    }
}
