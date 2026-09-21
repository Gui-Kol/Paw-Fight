package com.pawfight.game.world.portal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.ScreenManager;
import com.pawfight.game.engine.fisica.ChecarColisao;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.KeyBindings;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.List;

// Portal reutilizável: nome único, layer do TiledMap, mensagem ao entrar na área e transição ao mundo de destino com Enter
public class Portal {

    private final String nome;
    private final String layer;
    private final String mensagem;
    private final WorldFactory destino;

    private List<Rectangle> areaAtivacao;
    private boolean ativado = false;

    // Cria um portal: identificação única, layer do mapa, mensagem exibida na área e fábrica do mundo de destino
    public Portal(String nome, String layer, String mensagem, WorldFactory destino) {
        this.nome = nome;
        this.layer = layer;
        this.mensagem = mensagem;
        this.destino = destino;
    }

    // Define a área de ativação do portal a partir do layer configurado no TiledMap
    public void definirAreaAtivacao(TiledMap map, TilemapHitboxFactory factory) {
        areaAtivacao = factory.createHitboxes(map, layer);
    }

    // Retorna true se o jogador estiver sobre a área do portal
    public boolean detectarEntrada(PlayerTemplate player) {
        return areaAtivacao != null && ChecarColisao.houveColisao(player.getHitBox(), areaAtivacao);
    }

    // Exibe a mensagem do portal na parte inferior da tela enquanto o jogador estiver na área
    public void exibirMensagem(PlayerTemplate player, SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (detectarEntrada(player)) {
            player.getHud().mostrarMensagemEmBaixo(batch, shapeRenderer, mensagem);
        }
    }

    // Ativa a transição de mundo ao pressionar Enter na área; gerenciada pelo ScreenManager (sem dispose da tela atual)
    public boolean ativarTransicao(PlayerTemplate player, PawFight game, OrthographicCamera camera, Viewport viewport) {
        if (ativado) {
            return true;
        }
        if (detectarEntrada(player) && KeyBindings.getInstance().isActive(GameAction.MENU_CONFIRM)) {
            player.clearList();
            WorldTemplate mundo = destino.criar(game, player, camera, viewport);
            ScreenManager.getInstance().fadeToScreen(mundo, 2f, Color.BLACK, false);
            ativado = true;
            return true;
        }
        return false;
    }

    // Restaura o estado do portal para permitir novos atravessamentos
    public void reset() {
        ativado = false;
    }

    public String getNome() {
        return nome;
    }

    public String getLayer() {
        return layer;
    }

    public String getMensagem() {
        return mensagem;
    }

    public WorldFactory getDestino() {
        return destino;
    }
}
