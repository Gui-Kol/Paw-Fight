package com.pawfight.game.world.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.Hud.Hud;
import com.pawfight.game.engine.phisics.ChecarColisao;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class Portoes {
    private Hud hud;

    public Portoes() {
        hud = new Hud();
    }

    public void menssagemPortao(PlayerTemplate player, List<Rectangle> portao, SpriteBatch batch, String menssagem) {
        if (ChecarColisao.houveColisao(player.getHitBox(), portao)) {
            batch.setProjectionMatrix(hud.getHudCamera().combined);
            hud.mostrarMensagemEmBaixo(batch, menssagem);
        }
    }

}
