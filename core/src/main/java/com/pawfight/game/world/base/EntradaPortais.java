package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.PawFight;
import com.pawfight.game.commun.Hud.Hud;
import com.pawfight.game.commun.animation.ScreenTransition;
import com.pawfight.game.commun.phisics.ChecarColisao;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.mundo_areia.MundoAreia;

import java.util.List;

public class EntradaPortais {
    private Hud hud;
    private boolean entrouPortal = false;

    // Referência para transição de tela
    private ScreenTransition screenTransition;

    public EntradaPortais(ScreenTransition screenTransition) {
        this.screenTransition = screenTransition;
        hud = new Hud();
    }

    public boolean entrarPortal(PlayerTemplate player, List<Rectangle> entradaPortalAreia, SpriteBatch batch) {
        if (ChecarColisao.houveColisao(player.getHitBox(), entradaPortalAreia)) {
            String mensagem = "Aperte ENTER para entrar";

            hud.mostrarMensagemEmBaixo(batch, mensagem);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                entrouPortal = true;
            }


        }
        return entrouPortal;
    }

    public boolean entrarPortalAreia(PlayerTemplate player, List<Rectangle> entradaPortal, SpriteBatch batch, PawFight game) {
        try {

            if (player == null || entradaPortal == null || batch == null || game == null || screenTransition == null) {
                Gdx.app.error("EntradaPortais", "Objeto null em entrarPortalAreia.");
                return false;
            }
            batch.setProjectionMatrix(hud.getHudCamera().combined);
            if (entrarPortal(player, entradaPortal, batch)) {
                MundoAreia mundoAreia = new MundoAreia(game, player);
                mundoAreia.preLoad();

                screenTransition.start(mundoAreia);
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            Gdx.app.error("EntradaPortais", "Erro ao tentar entrar no portal de areia: " + e.getMessage());
            return false;
        }
    }

    public boolean entrarPortalNeve(PlayerTemplate player, List<Rectangle> entradaPortal, SpriteBatch batch, PawFight game) {
        return false; // Implementação futura para o portal de neve
    }

    public void entrou(SpriteBatch batch) {
        if (entrouPortal) {
            screenTransition.update(Gdx.graphics.getDeltaTime());
            screenTransition.render(batch);
        }
    }
}
