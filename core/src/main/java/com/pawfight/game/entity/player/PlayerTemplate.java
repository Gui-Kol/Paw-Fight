package com.pawfight.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.hud.Hud;
import com.pawfight.game.engine.hud.HudPause;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.design.particle.GerenciadorParticulas;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.engine.save.DadosSalvosJogador;
import com.pawfight.game.entity.Entidade;
import com.pawfight.game.entity.component.*;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.ArrayList;
import java.util.List;


public abstract class PlayerTemplate implements Entidade {

    protected final InputComponent input = new InputComponent();
    protected final MovimentoComponent movimento = new MovimentoComponent();
    protected final AnimacaoComponent animacao = new AnimacaoComponent();
    protected final AudioComponent audio = new AudioComponent();
    protected final CombateComponent combate = new CombateComponent();
    private final List<TirosTemplate> drawSnapshot = new ArrayList<>();
    protected final ColisaoComponent colisao = new ColisaoComponent();
    protected final SaveComponent save = new SaveComponent();
    protected final GerenciadorParticulas particulas = new GerenciadorParticulas();
    protected StatsComponent stats;          // inicializado no construtor (precisa de DadosPlayer)
    protected CameraComponent cameraComponent; // inicializado no construtor (precisa de params do mapa)

    // Estado espacial (usado por muitos componentes e classes externas)
    protected int dx, dy;
    protected final Rectangle hitBox;
    protected int TAMANHO_PX;
    protected int hitboxSize;
    protected int hitboxOffsetX;
    protected int hitboxOffsetY;
    protected boolean olhandoEsquerda = false;
    protected boolean moving = false;

    protected final Hud hud;
    protected HudPause hudPause;
    private boolean menuAberto = false;
    protected boolean pause = false;
    protected boolean drawHitBoxes = false;
    protected final Renderizar renderizar = Renderizar.INSTANCE;
    private boolean morteFinalizada = false;

    // Métodos abstratos (cada player define os seus)
    public abstract DadosPlayer dadosPlayer();
    public abstract void updateSpriteDefinitions();
    public abstract String getName();
    public abstract void ataqueBasico(float delta);
    public abstract void ataqueEspecial();
    public abstract void usarHabilidadeEspecial();
    protected abstract void definirAudios();
    protected abstract TirosTemplate modeloTiroExclusivo();

    public PlayerTemplate(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        this.dx = dx;
        this.dy = dy;

        // dadosPlayer é implementado pela subclasse
        DadosPlayer dados = dadosPlayer();

        stats = new StatsComponent(dados);

        audio.init(dados);

        TAMANHO_PX = dados.tamanho();
        hitboxSize = dados.hitboxSize();
        hitboxOffsetY = dados.hitboxOffsetY();
        hitboxOffsetX = dados.hitboxOffsetX();

        hitBox = new Rectangle(
            dx + (TAMANHO_PX - hitboxSize) / 2f + hitboxOffsetX,
            dy + hitboxOffsetY,
            hitboxSize,
            hitboxSize
        );

        cameraComponent = new CameraComponent(dx, dy, zoomCamera, tileWidth, numTilesX, tileHeight, numTilesY);

        animacao.initTextures(dados.idleSheet(), dados.walkSheet(), dados.deadSheet(), dados.hurtSheet());
        updateSpriteDefinitions();
        animacao.rebuildAnimations();

        hud = new Hud();
        hudPause = new HudPause(this);

        // Áudios extras da subclasse
        definirAudios();

        combate.addModeloTiro(modeloTiroExclusivo());
    }

    public void update(float delta) {
        if (input.isPauseToggle()) {
            if (pause && hudPause.isConfiguracoesAberta()) {
                hudPause.fecharConfiguracoes();
                return;
            }
            menuAberto = !menuAberto;
            pause = menuAberto;
            Gdx.app.log(getName(), pause ? "Jogo pausado." : "Jogo retomado.");
        }
        if (pause) return;

        if (!stats.isMorto()) {
            movimento.update(hitBox, stats.getVelocidade(), delta, input);
            moving = movimento.isMoving();
            olhandoEsquerda = movimento.isOlhandoEsquerda();

            ataqueBasico(delta);
            combate.processarTirosAutomaticos(this, delta);
            if (input.isAttackSpecial()) ataqueEspecial();
            if (input.isAbility()) usarHabilidadeEspecial();

            if (input.isDebugToggle()) {
                drawHitBoxes = !drawHitBoxes;
                Gdx.app.log("PlayerTemplate", "Exibir detalhes = " + drawHitBoxes);
                GameConfig.getInstance().setHitboxVisivel(drawHitBoxes);
            }

            cameraComponent.updateZoom();

            if (GameConfig.getInstance().isDebugMode() && input.isCheatToggle()) {
                xpUp(999999999);
                stats.setVida(999999999);
            }

            // Troca a direção no cache (sem rebuild)
            animacao.checkDirectionChange(olhandoEsquerda);

            int offsetX = olhandoEsquerda ? -(hitboxOffsetX) : hitboxOffsetX;
            hitBox.setPosition(
                dx + (TAMANHO_PX - hitboxSize) / 2f + offsetX,
                dy + hitboxOffsetY
            );
        }

        // Timers rodam mesmo com o player morto
        animacao.updateStateTime(delta);
        stats.updateTimers(delta);

        particulas.atualizar(delta);

        combate.updateTiros(delta);

        audio.updateAudio(moving);

        // Ajusta dx e dy conforme colisão
        colisao.checarColisao(this);

        cameraComponent.updateCamera(dx, dy);
    }

    public void draw(WorldTemplate world) {
        Batch batch = world.getBatch();
        ShapeRenderer shapeRenderer = world.getWorldRenderer().getShapeRenderer();
        OrthographicCamera cam = cameraComponent.getCamera();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        if (!morteFinalizada) {
            batch.draw(
                animacao.animaAtual(stats.isMorto(), stats.isHurt(), moving, stats.getHurtTime()),
                dx, dy, TAMANHO_PX, TAMANHO_PX
            );
        }
        particulas.desenhar(batch);
        batch.end();

        shapeRenderer.setProjectionMatrix(cam.combined);

        // 1 único begin/end para TODAS as hitboxes (colisão + player)
        if (GameConfig.getInstance().isHitboxVisivel()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            colisao.drawDebugHitboxesNoBatch(shapeRenderer);
            renderizar.hitboxDraw(shapeRenderer, hitBox);
            shapeRenderer.end();
        }
    }

    public void desenharTiros(WorldTemplate world) {
        if (combate.getTiros().isEmpty()) return;

        drawSnapshot.clear();
        drawSnapshot.addAll(combate.getTiros());

        Batch batch = world.getBatch();
        ShapeRenderer shapeRenderer = world.getWorldRenderer().getShapeRenderer();
        OrthographicCamera cam = cameraComponent.getCamera();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        for (TirosTemplate tiro : drawSnapshot) {
            tiro.desenhar(batch);
        }
        batch.end();

        // 1 único begin/end para TODAS as hitboxes de tiros
        if (GameConfig.getInstance().isHitboxVisivel()) {
            shapeRenderer.setProjectionMatrix(cam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            for (TirosTemplate tiro : drawSnapshot) {
                renderizar.hitboxRect(shapeRenderer, tiro.getHitBox(), com.badlogic.gdx.graphics.Color.RED);
            }
            shapeRenderer.end();
        }
    }

    public void drawHud(SpriteBatch batch, ShapeRenderer shapeRenderer, WorldTemplate world) {
        hud.draw(batch, this, shapeRenderer);
        hudPause.draw(world);
    }

    public void dano(int forca) {
        if (!stats.aplicarDano(forca)) return;
        if (stats.isMorto()) {
            animacao.resetStateTime();
            audio.playMorte();
            Gdx.app.log(getName(), "morreu — vida zerada (dano: " + forca + ")");
        } else {
            audio.playDano();
            particulas.spawnDano(dx + TAMANHO_PX / 2f, dy + TAMANHO_PX / 2f);
            Gdx.app.log(getName(), "Tomou " + forca + " de dano — vida " + stats.getVida() + "/" + stats.getVidaBase());
        }
    }

    public void curar(int pontos) {
        int vidaAntes = stats.getVida();
        stats.curar(pontos);
        particulas.spawnCura(dx + TAMANHO_PX / 2f, dy + TAMANHO_PX / 2f);
        Gdx.app.log(getName(), "Curou " + pontos + " — vida " + vidaAntes + " -> " + stats.getVida() + "/" + stats.getVidaBase());
    }

    public void xpUp(int xpGanho) {
        int levelAntes = stats.getLevel();
        int levels = stats.xpUp(xpGanho);
        for (int i = 0; i < levels; i++) {
            audio.playLevelUp();
            particulas.spawnLevelUp(dx + TAMANHO_PX / 2f, dy + TAMANHO_PX / 2f);
        }
        Gdx.app.log(getName(), "Ganhou " + xpGanho + " XP — xp " + stats.getXp() + "/" + stats.getXpNecessario()
            + (levels > 0 ? " — level " + levelAntes + " -> " + stats.getLevel() : ""));
    }

    public void moedaUp(int moedasGanha) {
        int moedasAntes = stats.getMoedas();
        stats.moedaUp(moedasGanha);
        Gdx.app.log(getName(), "Ganhou " + moedasGanha + " moedas — total " + moedasAntes + " -> " + stats.getMoedas());
    }

    public void saveData() {
        save.saveData(this);
    }

    public void loadSaveData() {
        save.loadSaveData(this);
    }

    public void adicionarColisao(List<Rectangle> colisores) {
        colisao.adicionarColisao(colisores);
    }

    public void adicionarColisaoPorLevel(List<Rectangle> colisores, int levelNecessario) {
        colisao.adicionarColisaoPorLevel(colisores, stats.getLevel(), levelNecessario);
    }

    public void clearList() {
        colisao.clearColisores();
        combate.clearTiros();
    }

    public void adicionarTiro(TirosTemplate tiro) {
        combate.adicionarTiro(tiro);
    }

    public void removerTiro(TirosTemplate tiro) {
        combate.removerTiro(tiro);
    }

    public void setLocal(float x, float y) {
        this.dx = (int) x;
        this.dy = (int) y;
        pause = false;
        menuAberto = false;

        int offsetX = olhandoEsquerda ? -(hitboxOffsetX) : hitboxOffsetX;
        hitBox.setPosition(
            dx + (TAMANHO_PX - hitboxSize) / 2f + offsetX,
            dy + hitboxOffsetY
        );
        cameraComponent.updateCamera(dx, dy);
    }

    public void dispose() {
        clearList();
        hudPause.dispose();
    }

    // Getters (mantém API pública compatível com código externo)
    public int getDx() { return dx; }
    public void setDx(int dx) { this.dx = dx; }
    public int getDy() { return dy; }
    public void setDy(int dy) { this.dy = dy; }
    public Rectangle getHitBox() { return hitBox; }
    public int getTamanho() { return TAMANHO_PX; }
    public int getHitboxSize() { return hitboxSize; }
    public int getHitboxOffsetX() { return hitboxOffsetX; }
    public int getHitboxOffsetY() { return hitboxOffsetY; }

    public float getNextX() { return movimento.getNextX(); }
    public float getNextY() { return movimento.getNextY(); }
    public boolean isOlhandoEsquerda() { return olhandoEsquerda; }

    // Delegam ao StatsComponent
    public int getVida() { return stats.getVida(); }
    public int getVidaBase() { return stats.getVidaBase(); }
    public int getForca() { return stats.getForca(); }
    public int getVelocidade() { return stats.getVelocidade(); }
    public int getLevel() { return stats.getLevel(); }
    public int getMoedas() { return stats.getMoedas(); }
    public int getPontosDisponiveis() { return stats.getPontosDisponiveis(); }
    public float getCadenciaTiro() { return stats.getCadenciaTiro(); }
    public float getDuracaoTiro() { return stats.getDuracaoTiro(); }
    public int getQuantidadeDeTiros() { return stats.getQuantidadeDeTiros(); }
    public boolean isMorto() { return stats.isMorto(); }

    public void vidaBaseUp(int pontosGastos) { stats.vidaBaseUp(pontosGastos); }
    public void forcaUp(int pontosGastos) { stats.forcaUp(pontosGastos); }
    public void velocidadeUp(int pontosGastos) { stats.velocidadeUp(pontosGastos); }
    public void gastouPontos(int pontosGastos) { stats.gastouPontos(pontosGastos); }

    public List<TirosTemplate> getTiros() { return combate.getTiros(); }
    public void setPodeAtacar(boolean podeAtacar) { combate.setPodeAtacar(podeAtacar); }

    // Injeta a lista de inimigos da sala atual (chamado pelo mundo).
    public void setFonteInimigos(List<EnemyTemplate> inimigos) { combate.setFonteInimigos(inimigos); }

    public OrthographicCamera getCamera() { return cameraComponent.getCamera(); }
    public CameraComponent getCameraComponent() { return cameraComponent; }
    public AnimacaoComponent getAnimacao() { return animacao; }

    public Hud getHud() { return hud; }
    public HudPause getHudPause() { return hudPause; }

    public boolean isPause() { return pause; }
    public void setPause(boolean pause) {
        this.pause = pause;
        this.menuAberto = pause;
    }

    // Morte finalizada (esconde o sprite após a animação tocar 1 vez)
    public boolean isMorteFinalizada() { return morteFinalizada; }
    public void setMorteFinalizada(boolean morteFinalizada) { this.morteFinalizada = morteFinalizada; }

    public StatsComponent getStats() {
        return stats;
    }
}
