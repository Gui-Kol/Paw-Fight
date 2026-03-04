package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.PawFight;
import com.pawfight.game.commun.Hud.Hud;
import com.pawfight.game.commun.animation.ScreenTransition;
import com.pawfight.game.commun.font.FontFactory;
import com.pawfight.game.commun.phisics.ChecarColisao;
import com.pawfight.game.entity.player.Player;
import com.pawfight.game.world.Home;

import java.util.List;

public class EntradaPortais {
    private Hud hud;
    private boolean entrarPortalAreia = false;

    // Referência para transição de tela
    private ScreenTransition screenTransition;

    public EntradaPortais(ScreenTransition screenTransition) {
        this.screenTransition = screenTransition;
        hud = new Hud();
    }

    public void entrarPortalAreia(Player player, List<Rectangle> entradaPortalAreia, Batch batch, PawFight game) {
        if (ChecarColisao.houveColisao(player.getHitBox(), entradaPortalAreia)) {
            String mensagem = "Aperte ENTER para entrar";
            hud.mostrarMensagemEmBaixo(batch, mensagem);

            // Se o jogador apertar ENTER, troca de fase
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                entrarPortalAreia = true;
                screenTransition.start(new Home(game));
            }

            if (entrarPortalAreia) {
                screenTransition.update(Gdx.graphics.getDeltaTime());
                screenTransition.render(new SpriteBatch());
            }
        }
    }


}
