package com.pawfight.game.entity.tiro.blackbird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;

/**
 * Disparo congelante do Black Bird: ao nascer, aponta para o inimigo
 * mais próximo do player e se desloca em <b>alta velocidade</b>, em
 * linha reta (retilíneo mesmo se o alvo morrer no trajeto). Alcance
 * longo, dano moderado em um único alvo e chance de aplicar lentidão
 * (reduz velocidade de movimento e de ataque por um tempo).
 */
public class TiroGelo extends TirosTemplate {

    /** Velocidade de deslocamento do projétil (px/segundo). */
    private static final float VELOCIDADE_TIRO = 900f;

    // Lentidão — chance e valores configuráveis
    private static final float CHANCE_LENTIDAO = 0.5f;
    private static final float MULTIPLICADOR_LENTIDAO = 0.5f;
    private static final float DURACAO_LENTIDAO = 2.5f;

    private final Texture gelo;

    private Pool<TiroGelo> pool;

    public TiroGelo(int tamanho, PlayerTemplate player) {
        super(player.getDx(), player.getDy(), player.getForca(), tamanho, player);
        gelo = Assets.get("Hud/nuvemChao.png", Texture.class);
        texture = gelo;
        posicionarNoPlayer();
        definirDirecaoPara(inimigoMaisProximo());

        // Cria o pool apenas no modelo
        pool = new Pool<TiroGelo>(8, 64) {
            @Override
            protected TiroGelo newObject() {
                return new TiroGelo();
            }
        };
    }

    private TiroGelo() {
        super(); // inicializa constantes (duracao, cadencia, tamanhoPadrao)
        gelo = Assets.get("Hud/nuvemChao.png", Texture.class);
    }

    @Override
    protected Rectangle gerarHitBox() {
        return new Rectangle(xHitBox, yHitBox, tamanho, tamanho);
    }

    /** Nasce no centro do player (a trajetória é definida pela mira). */
    private void posicionarNoPlayer() {
        Rectangle pb = dono.getHitBox();
        sincronizarPosicao(pb.x + pb.width / 2f, pb.y + pb.height / 2f);
    }

    @Override
    protected TirosTemplate obterDoPool(PlayerTemplate player) {
        TiroGelo t = pool.obtain();

        // Reinicializa campos-base (tamanho menos o padrão, já somado pelo modelo)
        t.reiniciarBase(player.getDx(), player.getDy(), player.getForca(), tamanho - tamanhoPadrao, player);
        t.inimigos = inimigos;
        t.hitBox.setSize(t.tamanho, t.tamanho);
        t.posicionarNoPlayer();
        t.definirDirecaoPara(t.inimigoMaisProximo());
        t.texture = t.gelo;
        t.ownerPool = pool;

        return t;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        moverNaDirecao(VELOCIDADE_TIRO, delta); // retilíneo até expirar
    }

    @Override
    public boolean isUnicoAlvo() {
        return true; // disparo atinge um único alvo
    }

    @Override
    public void aoAcertar(EnemyTemplate inimigo) {
        // Efeito secundário: chance de lentidão
        inimigo.aplicarLentidao(MULTIPLICADOR_LENTIDAO, DURACAO_LENTIDAO, CHANCE_LENTIDAO);
    }

    @Override
    protected int definirTamanhoPadrao() {
        return 20;
    }

    @Override
    protected float definirDuracao() {
        return 2.5f; // alcance longo (distância = velocidade x duração)
    }

    @Override
    protected float definirIntervalo() {
        return 1.1f;
    }

    @Override
    protected Texture randomTex() {
        return gelo;
    }

    @Override
    protected Texture singleTex() {
        return gelo;
    }
}
