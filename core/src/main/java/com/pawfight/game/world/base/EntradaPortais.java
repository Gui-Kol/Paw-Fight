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
import com.pawfight.game.world.Home;
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
    public boolean entrarPortal(PlayerTemplate player, List<Rectangle> entradaPortalAreia, SpriteBatch batch){
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
        batch.setProjectionMatrix(hud.getHudCamera().combined);
        if (entrarPortal(player,entradaPortal,batch)){
            screenTransition.start(new Home(game));
            return true;
        }else {
            return false;
        }
    }
    public boolean entrarPortalNeve(PlayerTemplate player, List<Rectangle> entradaPortal, SpriteBatch batch, PawFight game) {
        batch.setProjectionMatrix(hud.getHudCamera().combined);
        if (entrarPortal(player,entradaPortal,batch)){
            screenTransition.start(new MundoAreia(game, player));
            return true;
        }else {
            return false;
        }
    }

    public void entrou(SpriteBatch batch){
        if (entrouPortal) {
            screenTransition.update(Gdx.graphics.getDeltaTime());
            screenTransition.render(batch);
        }
    }
}
