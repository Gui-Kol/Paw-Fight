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

/**
 * Sistema de portal reutilizável e abstrato.
 *
 * <p>Cada portal possui um nome único, está associado a um layer específico do
 * {@link TiledMap}, exibe uma mensagem ao jogador entrar na área de ativação e,
 * ao pressionar Enter, transporta o jogador para o {@link WorldTemplate} de
 * destino. Novos portais podem ser adicionados apenas instanciando a classe e
 * definindo a área de ativação, sem reescrever lógica.</p>
 */
public class Portal {

    // ── Atributos ─────────────────────────────────────────────
    private final String nome;
    private final String layer;
    private final String mensagem;
    private final WorldFactory destino;

    // ── Estado ────────────────────────────────────────────────
    private List<Rectangle> areaAtivacao;
    private boolean ativado = false;

    /**
     * Cria um portal.
     *
     * @param nome     identificação única do portal
     * @param layer    layer do TiledMap em que o portal atua
     * @param mensagem texto exibido quando o jogador entra na área do portal
     * @param destino  fábrica do mundo para o qual o jogador será enviado
     */
    public Portal(String nome, String layer, String mensagem, WorldFactory destino) {
        this.nome = nome;
        this.layer = layer;
        this.mensagem = mensagem;
        this.destino = destino;
    }

    // ── Métodos ───────────────────────────────────────────────

    /**
     * Interpreta os atributos e define a área de ativação do portal a partir
     * do layer configurado no TiledMap.
     *
     * @param map     mapa carregado do mundo atual
     * @param factory fábrica de hitboxes do tilemap (com cache)
     */
    public void definirAreaAtivacao(TiledMap map, TilemapHitboxFactory factory) {
        areaAtivacao = factory.createHitboxes(map, layer);
    }

    /**
     * Detecta a entrada do jogador na área de ativação do portal.
     *
     * @param player jogador a ser verificado
     * @return {@code true} se o jogador estiver sobre a área do portal
     */
    public boolean detectarEntrada(PlayerTemplate player) {
        return areaAtivacao != null && ChecarColisao.houveColisao(player.getHitBox(), areaAtivacao);
    }

    /**
     * Exibe a mensagem definida no atributo {@code mensagem} na parte inferior
     * da tela enquanto o jogador estiver na área de ativação.
     *
     * @param player       jogador a ser verificado
     * @param batch        batch de desenho
     * @param shapeRenderer renderer de formas usado pelo HUD
     */
    public void exibirMensagem(PlayerTemplate player, SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (detectarEntrada(player)) {
            player.getHud().mostrarMensagemEmBaixo(batch, shapeRenderer, mensagem);
        }
    }

    /**
     * Ativa a transição de mundo quando o jogador pressionar a tecla Enter
     * estando dentro da área de ativação. A transição é gerenciada pelo
     * {@link ScreenManager} (sem dar dispose na tela atual).
     *
     * @param player   jogador que atravessará o portal
     * @param game     jogo principal
     * @param camera   câmera do mundo atual
     * @param viewport viewport do mundo atual
     * @return {@code true} se a transição foi iniciada
     */
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

    /**
     * Restaura o estado do portal para permitir novos atravessamentos.
     */
    public void reset() {
        ativado = false;
    }

    // ── Getters ───────────────────────────────────────────────

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