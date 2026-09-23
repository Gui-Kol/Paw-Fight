package com.pawfight.game.entity.enemy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.Entidade;
import com.pawfight.game.entity.component.AnimacaoComponent;
import com.pawfight.game.entity.component.AudioComponent;
import com.pawfight.game.entity.component.StatsComponent;
import com.pawfight.game.entity.component.ReferenciaEntidadeComponent;
import com.pawfight.game.entity.component.StatusComponent;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public abstract class EnemyTemplate implements Entidade {

    protected final AnimacaoComponent animacao = new AnimacaoComponent();
    protected final AudioComponent audio = new AudioComponent();
    protected final StatusComponent status = new StatusComponent();
    protected final StatsComponent stats;
    protected final Entity entidadeEcs = new Entity();

    protected PlayerTemplate player;
    protected List<EnemyTemplate> enemiesList;
    protected List<Rectangle> paredesColisores;

    protected float dx, dy;
    protected Rectangle hitBox;
    protected int TAMANHO_PX = 64;
    protected int HITBOX_SIZE = 20;
    protected int HITBOX_OFFSET_X = -10;
    protected int HITBOX_OFFSET_Y = 0;
    protected boolean olhandoEsquerda = false;
    protected boolean moving = false;

    protected String nome;
    protected boolean forte;
    protected boolean atacando = false;
    protected boolean atacandoEspecial = false;
    protected float ataqueTimer = 0f;
    protected final float ATAQUE_COOLDOWN = 1.5f;
    protected final float ATAQUE_DURATION = 0.5f;
    protected final float DISTANCIA_ATAQUE = 60f;


    public EnemyTemplate(int dx, int dy, boolean forte, PlayerTemplate player) {
        DadosInimigo dadosInimigo = dadosInimigo();
        float multiplicador = forte ? dadosInimigo.multiplicador() : 1;

        this.nome = dadosInimigo.nome();
        this.player = player;
        this.dx = dx;
        this.dy = dy;
        this.forte = forte;

        stats = new StatsComponent(
            dadosInimigo.vidaBase() * (int) multiplicador,
            dadosInimigo.forca() * (int) multiplicador,
            dadosInimigo.velocidade() * (int) multiplicador
        );
        entidadeEcs.add(stats);
        entidadeEcs.add(new ReferenciaEntidadeComponent(this));

        audio.initEnemy(dadosInimigo.audioDano(), dadosInimigo.audioMorte());

        animacao.initTextures(
            dadosInimigo.idleSheet(),
            dadosInimigo.walkSheet(),
            dadosInimigo.deadSheet(),
            dadosInimigo.hurtSheet(),
            dadosInimigo.atackSheet(),
            dadosInimigo.specialAtackSheet()
        );

        TAMANHO_PX = dadosInimigo.tamanho();
        HITBOX_SIZE = dadosInimigo.hitboxSize();
        HITBOX_OFFSET_X = dadosInimigo.hitboxOffsetX();
        HITBOX_OFFSET_Y = dadosInimigo.hitboxOffsetY();
        criarHitBox();
        updateSpriteDefinitions();
        animacao.rebuildAnimations();
    }

    protected abstract DadosInimigo dadosInimigo();
    public abstract void ataqueBasico();
    public abstract void ataqueEspecial();
    public abstract void updateSpriteDefinitions();
    public abstract EnemyTemplate cloneEnemy();
    public abstract void andarIA(float delta);
    public abstract int getTamanho();
    protected abstract int moedasMorte();
    public abstract void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer);

    protected int getHitboxOffsetXDirecional() {
        return olhandoEsquerda ? -HITBOX_OFFSET_X : HITBOX_OFFSET_X;
    }

    public void criarHitBox() {
        int offsetX = getHitboxOffsetXDirecional();
        this.hitBox = new Rectangle(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
            dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );
    }

    public void setLocation(int x, int y) {
        dx = x;
        dy = y;
        int offsetX = getHitboxOffsetXDirecional();
        hitBox.setPosition(dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX, dy + HITBOX_OFFSET_Y);
    }

    public void setEnemiesList(List<EnemyTemplate> enemiesList) {
        this.enemiesList = enemiesList;
    }

    public void setParedesColisores(List<Rectangle> paredes) {
        this.paredesColisores = paredes;
    }

    public List<Rectangle> getParedesColisores() {
        return paredesColisores;
    }

    public void update(float delta) {
        if (player != null && player.isPause()) {
            return;
        }
        TAMANHO_PX = getTamanho();

        if (!stats.isMorto()) {

            executarIA(delta);
            ataqueTimer += delta;

            // Troca só a direção no cache de animação (sem rebuild)
            animacao.checkDirectionChange(olhandoEsquerda);

            // Hitbox acompanha a direção esquerda/direita
            int offsetX = getHitboxOffsetXDirecional();
            hitBox.setPosition(
                dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
                dy + HITBOX_OFFSET_Y
            );

            if (atacando && animacao.getStateTime() >= ATAQUE_DURATION) {
                atacando = false;
            }
        }

        // Timers (sempre rodam, mesmo morto — para animação de morte e cooldowns)
        animacao.updateStateTime(delta);
        // Efeitos de status: queimadura aplica dano por tick apenas em vida
        int danoStatus = status.update(delta);
        if (danoStatus > 0 && !stats.isMorto()) {
            danoPorStatus(danoStatus);
        }
    }


    public void executarIA(float delta) {
        andarIA(delta);

        float distanciaAoPlayer = calcularDistanciaAoPlayer();

        // Lentidão também retarda a cadência de ataque (cooldown efetivo maior)
        float cooldownAtaque = ATAQUE_COOLDOWN / status.getMultiplicadorVelocidade();
        if (distanciaAoPlayer <= DISTANCIA_ATAQUE && ataqueTimer >= cooldownAtaque) {
            ataqueBasico();
            ataqueTimer = 0f;
            animacao.resetStateTime();
        }

        ataqueEspecial();
    }

    protected float calcularDistanciaAoPlayer() {
        if (player == null) return Float.MAX_VALUE;
        float ddx = this.dx - player.getDx();
        float ddy = this.dy - player.getDy();
        return (float) Math.sqrt(ddx * ddx + ddy * ddy);
    }

    @Override
    public void dano(int forca) {
        receberDano(forca);
    }

    // Dano de impacto (respeita invulnerabilidade); true se aplicado — usado por tiros p/ efeitos de acerto.
    public boolean receberDano(int forca) {
        if (!stats.aplicarDano(forca)) return false;
        onDanoRecebido(true);
        return true;
    }

    // Dano contínuo de status (DoT): ignora cooldown e animação de hurt, mas processa a morte.
    public void danoPorStatus(int forca) {
        if (!stats.aplicarDanoDireto(forca)) return;
        onDanoRecebido(false);
    }

    private void onDanoRecebido(boolean notificarDano) {
        if (stats.isMorto()) {
            animacao.resetStateTime();
            player.moedaUp(moedasMorte());
            audio.playMorte();
        } else if (notificarDano) {
            audio.playDano();
        }
    }

    public boolean aplicarQueimadura(int danoPorTick, float duracao, float chance) {
        if (stats.isMorto()) return false;
        boolean aplicou = status.aplicarQueimadura(danoPorTick, duracao, chance);
        if (aplicou) {
            Gdx.app.debug(nome, "Queimadura aplicada (" + danoPorTick + " dano por tick)");
        }
        return aplicou;
    }

    public boolean aplicarLentidao(float multiplicador, float duracao, float chance) {
        if (stats.isMorto()) return false;
        boolean aplicou = status.aplicarLentidao(multiplicador, duracao, chance);
        if (aplicou) {
            Gdx.app.debug(nome, "Lentidão aplicada (x" + multiplicador + " por " + duracao + "s)");
        }
        return aplicou;
    }

    public void sincronizarPosicaoComHitbox() {
        int offsetX = getHitboxOffsetXDirecional();
        this.dx = hitBox.x - (TAMANHO_PX - HITBOX_SIZE) / 2f - offsetX;
        this.dy = hitBox.y - HITBOX_OFFSET_Y;
    }

    protected TextureRegion animaAtual() {
        return animacao.animaAtual(
            stats.isMorto(), stats.isHurt(),
            atacando, atacandoEspecial,
            moving, stats.getHurtTime()
        );
    }

    public void drawSprite(SpriteBatch batch) {
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
    }


    public void setForte(boolean forte) {
        if (this.forte != forte) {
            this.forte = forte;
            aplicarStatsForte();
        }
    }

    protected void aplicarStatsForte() {
        // implementação padrão vazia — subclasses sobrescrevem
    }

    public int getDx()              { return Math.round(dx); }
    public int getDy()              { return Math.round(dy); }
    public Rectangle getHitBox()    { return hitBox; }
    public int getVida()            { return stats.getVida(); }
    public int getVidaBase()        { return stats.getVidaBase(); }
    public int getForca()           { return stats.getForca(); }
    public int getVelocidade()      { return Math.round(stats.getVelocidade() * status.getMultiplicadorVelocidade()); }
    public StatusComponent getStatus() { return status; }
    public boolean isMorto()        { return stats.isMorto(); }
    public boolean isOlhandoEsquerda() { return olhandoEsquerda; }
    public StatsComponent getStats() { return entidadeEcs.getComponent(StatsComponent.class); }
    public Entity getEntidadeEcs() { return entidadeEcs; }
}
