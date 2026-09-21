package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.enemy.MoverDirecaoPlayer;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

// Rei Esqueleto: Fase 1 (>50% de vida) corpo a corpo; Fase 2 (≤50%) golpe mais forte + Grito de Fúria em área. Reutiliza assets do Skeleton.
public class ReiEsqueleto extends BossTemplate {

    private static final String TAG = "ReiEsqueleto";

    private final MoverDirecaoPlayer mover = new MoverDirecaoPlayer();

    public ReiEsqueleto(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
    }

    @Override
    protected DadosInimigo dadosInimigo() {
        return new DadosInimigo(
            "Rei Esqueleto",
            300,
            8,
            200,
            128,
            40,
            0,
            -20,
            Assets.get("entitys/enemy/Skeleton/Idle.png", Texture.class),
            Assets.get("entitys/enemy/Skeleton/Walk.png", Texture.class),
            Assets.get("entitys/enemy/Skeleton/Death.png", Texture.class),
            null,
            null,
            null,
            1f,
            null,
            audio.getAudioEngine().criarAudio("entitys/enemy/Skeleton/audioMorte.wav")
        );
    }

    @Override
    public void updateSpriteDefinitions() {
        DefinirSprite idle = new DefinirSprite(animacao.getIdleSheet(), 4, 0.1f, false, olhandoEsquerda);
        DefinirSprite walk = new DefinirSprite(animacao.getWalkSheet(), 6, 0.1f, false, olhandoEsquerda);
        DefinirSprite dead = new DefinirSprite(animacao.getDeadSheet(), 8, 0.1f, false, olhandoEsquerda);
        animacao.setDefinitions(idle, walk, dead, walk, walk, walk);
    }

    @Override
    protected void definirFases(List<FaseBoss> fases) {
        // Transição: o rei desmorona e se recompõe na nova fase (Death tocada uma vez)
        DefinirSprite defTransicao = new DefinirSprite(animacao.getDeadSheet(), 8, 0.08f, false, olhandoEsquerda);
        transicaoFase = animacao.getMotorAnimacao().criarAnimacao(defTransicao, false);

        Animation<TextureRegion> ataqueLento = animacao.getMotorAnimacao().animar(
            new DefinirSprite(animacao.getWalkSheet(), 6, 0.12f, false, olhandoEsquerda));
        Animation<TextureRegion> ataqueRapido = animacao.getMotorAnimacao().animar(
            new DefinirSprite(animacao.getWalkSheet(), 6, 0.06f, false, olhandoEsquerda));

        fases.add(new FasePosturaReal(ataqueLento));
        fases.add(new FaseFuria(ataqueRapido));
    }

    // Ciclo do boss: delega tudo à fase vigente

    @Override
    public void executarAtaqueNormal() {
        FaseBoss fase = faseVigente();
        if (fase != null) fase.executarAtaqueNormal(this);
    }

    @Override
    public void executarAtaqueEspecial() {
        FaseBoss fase = faseVigente();
        if (fase != null) fase.executarAtaqueEspecial(this);
    }

    @Override
    public void atualizarEstado() {
        verificarTransicaoFase();
    }

    @Override
    public void mudarFase(int novaFase) {
        if (novaFase < 0 || novaFase >= fases.size()) return;
        this.faseAtual = novaFase;
        FaseBoss fase = fases.get(novaFase);
        aplicarAnimacoesFase(fase);
        iniciarTransicao();
        Gdx.app.log(TAG, nome + " entrou na fase " + (novaFase + 1) + "/" + fases.size() + " — " + fase.getNome());
    }

    @Override
    public void andarIA(float delta) {
        mover.mover(this);
    }

    @Override
    public int getTamanho() {
        return 128;
    }

    @Override
    public EnemyTemplate cloneEnemy() {
        return new ReiEsqueleto(Math.round(dx), Math.round(dy), forte, player);
    }

    @Override
    protected int moedasMorte() {
        return 50;
    }

    @Override
    public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
    }

    // Fase 1 — golpe corpo a corpo simples, sem especial.
    private static class FasePosturaReal extends FaseBoss {

        FasePosturaReal(Animation<TextureRegion> animacaoAtaque) {
            super("Postura Real", 0.5f);
            this.animacaoAtaque = animacaoAtaque;
        }

        @Override
        public void executarAtaqueNormal(BossTemplate boss) {
            PlayerTemplate player = boss.getPlayer();
            if (player != null && boss.getHitBox().overlaps(player.getHitBox())) {
                player.dano(boss.getForca());
                boss.setAtacando(true);
            }
        }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) {
            // Sem especial nesta fase
        }
    }

    // Fase 2 — golpe mais forte + Grito de Fúria (dano em área, com cooldown).
    private static class FaseFuria extends FaseBoss {

        private static final float DISTANCIA_FURIA = 150f;
        private static final float COOLDOWN_FURIA = 2.5f;

        private float timerFuria = 0f;

        FaseFuria(Animation<TextureRegion> animacaoAtaque) {
            super("Fúria Óssea", 0f);
            this.animacaoAtaque = animacaoAtaque;
            this.animacaoMovimentacao = animacaoAtaque;
        }

        @Override
        public void executarAtaqueNormal(BossTemplate boss) {
            PlayerTemplate player = boss.getPlayer();
            if (player != null && boss.getHitBox().overlaps(player.getHitBox())) {
                player.dano(boss.getForca() + 1);
                boss.setAtacando(true);
            }
        }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) {
            timerFuria += Gdx.graphics.getDeltaTime();
            if (timerFuria < COOLDOWN_FURIA) return;

            PlayerTemplate player = boss.getPlayer();
            if (player == null) return;

            float distX = boss.getDx() - player.getDx();
            float distY = boss.getDy() - player.getDy();
            float distancia = (float) Math.sqrt(distX * distX + distY * distY);

            if (distancia <= DISTANCIA_FURIA) {
                player.dano(boss.getForca() / 2 + 1);
                boss.setAtacandoEspecial(true);
                timerFuria = 0f;
                Gdx.app.log(TAG, "Rei Esqueleto usou Grito de Fúria!");
            }
        }
    }
}
