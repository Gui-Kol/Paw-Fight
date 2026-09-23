package com.pawfight.game.entity.tiro.blackcat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;

// "Unhada" grudada no inimigo mais próximo, dano contínuo; re-mira no vivo mais próximo se o alvo morrer.
public class TiroFantasmagorico extends TirosTemplate {

    // Multiplicador da área de dano em relação ao tamanho padrão dos tiros.
    private static final float FATOR_AREA = 1.4f;
    private static final float MULTIPLICADOR_DANO = 1.5f;

    private final Texture fantasma;

    // Inimigo ao qual a garra espectral está grudada.
    private EnemyTemplate alvo;

    private Pool<TiroFantasmagorico> pool;

    public TiroFantasmagorico(int tamanho, PlayerTemplate player) {
        super(player.getDx(), player.getDy(), calcularDano(player), tamanho, player);
        fantasma = Assets.get("entitys/player/ataques/arranhao.png", Texture.class);
        texture = fantasma;
        posicionarNoAlvo();

        // Cria o pool apenas no modelo
        pool = new Pool<TiroFantasmagorico>(8, 64) {
            @Override
            protected TiroFantasmagorico newObject() {
                return new TiroFantasmagorico();
            }
        };
    }

    private TiroFantasmagorico() {
        super(); // inicializa constantes (duracao, cadencia, tamanhoPadrao)
        fantasma = Assets.get("entitys/player/ataques/arranhao.png", Texture.class);
    }

    private static int calcularDano(PlayerTemplate player) {
        return Math.max(1, Math.round(player.getForca() * MULTIPLICADOR_DANO));
    }

    @Override
    protected Rectangle gerarHitBox() {
        float lado = tamanho * FATOR_AREA;
        return new Rectangle(xHitBox, yHitBox, lado, lado);
    }

    // Posiciona a garra sobre o alvo (ou sobre o player, se não houver alvos).
    private void posicionarNoAlvo() {
        Rectangle referencia = (alvo != null) ? alvo.getHitBox() : dono.getHitBox();
        sincronizarPosicao(
            referencia.x + referencia.width / 2f,
            referencia.y + referencia.height / 2f
        );
    }

    @Override
    protected TirosTemplate obterDoPool(PlayerTemplate player) {
        TiroFantasmagorico t = pool.obtain();

        // Reinicializa campos-base (tamanho menos o padrão, já somado pelo modelo)
        t.reiniciarBase(player.getDx(), player.getDy(), calcularDano(player), tamanho - tamanhoPadrao, player);
        t.inimigos = inimigos;
        float lado = t.tamanho * FATOR_AREA;
        t.hitBox.set(t.xHitBox, t.yHitBox, lado, lado);
        t.alvo = t.inimigoMaisProximo(); // nasce já atacando o mais próximo
        t.posicionarNoAlvo();
        t.texture = t.fantasma;
        t.ownerPool = pool;

        return t;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (!isAtivoParaColisao()) return;

        // Alvo morreu antes do fim: re-mira no vivo mais próximo
        if (alvo == null || alvo.isMorto()) {
            alvo = inimigoMaisProximo();
            if (alvo == null) return; // sala limpa: fica onde está até expirar
        }
        posicionarNoAlvo();
    }

    @Override
    public void reset() {
        super.reset();
        alvo = null;
    }

    @Override
    protected int definirTamanhoPadrao() {
        return 48;
    }

    @Override
    protected void definirTamanhoSprite() {
    }

    @Override
    protected int definirQuantidadeFrames() {
        return 13; // spritesheet com 13 frames
    }

    @Override
    protected int definirFramesPorSegundo() {
        return 12;
    }

    @Override
    protected int definirQuantidadeTirosPadrao() {
        return 1;
    }

    @Override
    protected float definirDuracao() {
        return 2f;
    }

    @Override
    protected float definirIntervalo() {
        return 0.9f;
    }

    @Override
    protected Texture randomTex() {
        return fantasma;
    }

    @Override
    protected Texture singleTex() {
        return fantasma;
    }
}
