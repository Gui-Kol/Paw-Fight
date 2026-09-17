package com.pawfight.game.entity.tiro.dove;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;

/**
 * Raio de sol da Dove: "velocidade instantânea" — nasce caindo
 * diretamente sobre o inimigo mais próximo do player e permanece por
 * apenas 0.5s. Dano elevado em um único alvo, com chance de causar
 * queimadura (dano adicional por tempo).
 */
public class TiroSolar extends TirosTemplate {

    // Queimadura (DoT) — chance e valores configuráveis
    private static final float CHANCE_QUEIMADURA = 0.35f;
    private static final float DURACAO_QUEIMADURA = 3f;

    private final Texture raio;

    private Pool<TiroSolar> pool;

    public TiroSolar(int tamanho, PlayerTemplate player) {
        super(player.getDx(), player.getDy(), calcularDano(player), tamanho, player);
        raio = Assets.get("Hud/raio.png", Texture.class);
        texture = raio;
        posicionarNoAlvo();

        // Cria o pool apenas no modelo
        pool = new Pool<TiroSolar>(8, 64) {
            @Override
            protected TiroSolar newObject() {
                return new TiroSolar();
            }
        };
    }

    private TiroSolar() {
        super(); // inicializa constantes (duracao, cadencia, tamanhoPadrao)
        raio = Assets.get("Hud/raio.png", Texture.class);
    }

    private static int calcularDano(PlayerTemplate player) {
        return Math.max(2, player.getForca() * 2);
    }

    @Override
    protected Rectangle gerarHitBox() {
        return new Rectangle(xHitBox, yHitBox, tamanho, tamanho);
    }

    /** Faz o raio cair sobre o alvo (ou sobre o player, se não houver alvos). */
    private void posicionarNoAlvo() {
        EnemyTemplate alvo = inimigoMaisProximo();
        Rectangle referencia = (alvo != null) ? alvo.getHitBox() : dono.getHitBox();
        sincronizarPosicao(
            referencia.x + referencia.width / 2f,
            referencia.y + referencia.height / 2f
        );
    }

    @Override
    protected TirosTemplate obterDoPool(PlayerTemplate player) {
        TiroSolar t = pool.obtain();

        // Reinicializa campos-base (tamanho menos o padrão, já somado pelo modelo)
        t.reiniciarBase(player.getDx(), player.getDy(), calcularDano(player), tamanho - tamanhoPadrao, player);
        t.inimigos = inimigos;
        t.hitBox.set(t.xHitBox, t.yHitBox, t.tamanho, t.tamanho);
        t.posicionarNoAlvo(); // sempre cai no inimigo mais próximo
        t.texture = t.raio;
        t.ownerPool = pool;

        return t;
    }

    @Override
    public boolean isUnicoAlvo() {
        return true; // dano elevado em um único alvo
    }

    @Override
    public void aoAcertar(EnemyTemplate inimigo) {
        // Efeito secundário: chance de queimadura (dano por tempo)
        int danoPorTick = Math.max(1, dano / 4);
        inimigo.aplicarQueimadura(danoPorTick, DURACAO_QUEIMADURA, CHANCE_QUEIMADURA);
    }

    @Override
    protected int definirTamanhoPadrao() {
        return 20;
    }

    @Override
    protected float definirDuracao() {
        return 0.5f; // raio rápido
    }

    @Override
    protected float definirIntervalo() {
        return 1.8f;
    }

    @Override
    protected Texture randomTex() {
        return raio;
    }

    @Override
    protected Texture singleTex() {
        return raio;
    }
}
