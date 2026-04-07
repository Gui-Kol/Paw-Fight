package com.pawfight.game.world.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.hud.Hud;
import com.pawfight.game.engine.fisica.ChecarColisao;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public class Portoes {
    private final Hud hud;

    public Portoes(Hud hud) {
        this.hud = hud;
    }

    public void mensagemPortao(PlayerTemplate player, List<Rectangle> portao, SpriteBatch batch, ShapeRenderer shapeRenderer, String mensagem) {
        if (ChecarColisao.houveColisao(player.getHitBox(), portao)) {
            hud.mostrarMensagemEmBaixo(batch, shapeRenderer,mensagem);
        }
    }

}
