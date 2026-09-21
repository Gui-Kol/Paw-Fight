package com.pawfight.game.entity.tiro.orangecat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;

// Espadada do Orange Cat: golpe corpo a corpo à frente do player com roubo de vida (2% do dano, mínimo 1 HP).
public class TiroSangue extends TirosTemplate {

    // Extensão (largura) do golpe corpo a corpo.
    private static final float ALCANCE = 42f;
    // Distância do ponto de nascimento à frente do player.
    private static final float DISTANCIA_NASCIMENTO = 50f;
    private static final float MARGEM_VERTICAL = 6f;

    // Proporção do dano convertida em cura (roubo de vida reutilizável).
    public static final float PROPORCAO_ROUBO_VIDA = 0.02f;
    private static final int CURA_MINIMA = 1;

    private final Texture sangue;

    private Pool<TiroSangue> pool;

    public TiroSangue(int tamanho, PlayerTemplate player) {
        super(player.getDx(), player.getDy(), player.getForca(), tamanho, player);
        sangue = Assets.get("effects/sangue/sangue.png", Texture.class);
        texture = sangue;

        // Cria o pool apenas no modelo
        pool = new Pool<TiroSangue>(8, 64) {
            @Override
            protected TiroSangue newObject() {
                return new TiroSangue();
            }
        };
    }

    private TiroSangue() {
        super(); // inicializa constantes (duracao, cadencia, tamanhoPadrao)
        sangue = Assets.get("effects/sangue/sangue.png", Texture.class);
    }

    @Override
    protected Rectangle gerarHitBox() {
        Rectangle hb = new Rectangle();
        configurarHitBox(hb);
        return hb;
    }

    // Hitbox corpo a corpo: nasce ~50px à frente do player, na direção do olhar.
    private void configurarHitBox(Rectangle hb) {
        Rectangle pb = dono.getHitBox();
        float centroPlayerX = pb.x + pb.width / 2f;
        float centroGolpeX = spawnEsquerda
            ? centroPlayerX - DISTANCIA_NASCIMENTO
            : centroPlayerX + DISTANCIA_NASCIMENTO;
        hb.set(
            centroGolpeX - ALCANCE / 2f,
            pb.y - MARGEM_VERTICAL,
            ALCANCE,
            pb.height + 2 * MARGEM_VERTICAL
        );
    }

    @Override
    protected TirosTemplate obterDoPool(PlayerTemplate player) {
        TiroSangue t = pool.obtain();

        // Reinicializa campos-base (tamanho menos o padrão, já somado pelo modelo)
        t.reiniciarBase(player.getDx(), player.getDy(), player.getForca(), tamanho - tamanhoPadrao, player);
        t.configurarHitBox(t.hitBox);
        t.texture = t.sangue;
        t.ownerPool = pool;

        return t;
    }

    @Override
    public void aoAcertar(EnemyTemplate inimigo) {
        // Roubo de vida: cura proporcional ao dano efetivamente aplicado
        if (dono == null) return;
        int cura = Math.max(CURA_MINIMA, Math.round(dano * PROPORCAO_ROUBO_VIDA));
        dono.curar(cura);
        Gdx.app.debug("TiroSangue", "Roubo de vida: +" + cura + " HP");
    }

    @Override
    public void desenhar(Batch batch) {
        garantirAnimacao();
        TextureRegion frame = frameAtual();
        if (frame == null) return;
        // Efeito visual cobre exatamente a área do golpe
        batch.draw(frame, hitBox.x, hitBox.y, hitBox.width, hitBox.height);
    }

    @Override
    protected int definirQuantidadeFrames() {
        return 1; // textura estática (frame único)
    }

    @Override
    protected int definirTamanhoPadrao() {
        return 40;
    }

    @Override
    protected float definirDuracao() {
        return 0.35f;
    }

    @Override
    protected float definirIntervalo() {
        return 0.8f;
    }

    @Override
    protected Texture randomTex() {
        return sangue;
    }

    @Override
    protected Texture singleTex() {
        return sangue;
    }
}
