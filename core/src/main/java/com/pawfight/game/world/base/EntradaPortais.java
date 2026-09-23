package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.hud.Hud;
import com.pawfight.game.engine.ScreenManager;
import com.pawfight.game.engine.fisica.ChecarColisao;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.GerenciadorInput;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.MundoAreia;

import java.util.List;

public class EntradaPortais {
    private final Hud hud;
    private boolean entrouPortal = false;

    public EntradaPortais(Hud hud) {
        this.hud = hud;
    }

    public boolean entrarPortal(PlayerTemplate player, List<Rectangle> entradaPortalAreia, SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (ChecarColisao.houveColisao(player.getHitBox(), entradaPortalAreia)) {
            GerenciadorInput input = GerenciadorInput.getInstance();
            String mensagem = "Aperte ENTER para entrar";
            hud.mostrarMensagemEmBaixo(batch,shapeRenderer ,mensagem);

            if (input.isPressionadaAgora(GameAction.MENU_CONFIRM)) {
                entrouPortal = true;
            }
        }
        return entrouPortal;
    }

    public boolean entrarPortalAreia(PlayerTemplate player, List<Rectangle> entradaPortal, SpriteBatch batch, ShapeRenderer shapeRenderer, PawFight game, OrthographicCamera camera, Viewport viewport) {
        try {
            if (player == null || entradaPortal == null || batch == null || game == null) {
                Gdx.app.error("EntradaPortais", "Objeto null em entrarPortalAreia.");
                return false;
            }

            batch.setProjectionMatrix(hud.getHudCamera().combined);

            if (entrarPortal(player, entradaPortal, batch, shapeRenderer)) {
                player.clearList();

                MundoAreia mundoAreia = new MundoAreia(game, player, camera, viewport);

                ScreenManager.getInstance().fadeToScreen(mundoAreia, 2f, Color.BLACK, false);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Gdx.app.error("EntradaPortais", "Erro ao tentar entrar no portal de areia: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean entrarPortalNeve(PlayerTemplate player, List<Rectangle> entradaPortal, SpriteBatch batch, PawFight game) {
        return false; // Implementação futura para o portal de neve
    }
}
