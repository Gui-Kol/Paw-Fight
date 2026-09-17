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
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.ArrayList;
import java.util.List;


public abstract class PlayerTemplate implements Entidade {

    // ── Componentes ────────────────────────────────────────────
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

    // ── Estado espacial (usado por muitos componentes e classes externas) ──
    protected int dx, dy;
    protected final Rectangle hitBox;
    protected int TAMANHO_PX;
    protected int hitboxSize;
    protected int hitboxOffsetX;
    protected int hitboxOffsetY;
    protected boolean olhandoEsquerda = false;
    protected boolean moving = false;

    // ── UI / HUD ───────────────────────────────────────────────
    protected final Hud hud;
    protected HudPause hudPause;
    private boolean menuAberto = false;
    protected boolean pause = false;
    protected boolean drawHitBoxes = false;
    protected final Renderizar renderizar = Renderizar.INSTANCE;

    // ── Métodos abstratos (cada player define os seus) ─────────
    public abstract DadosPlayer dadosPlayer();
    public abstract void updateSpriteDefinitions();
    public abstract String getName();
    public abstract void ataqueBasico(float delta);
    public abstract void ataqueEspecial();
    public abstract void usarHabilidadeEspecial();
    protected abstract void definirAudios();
    protected abstract TirosTemplate modeloTiroExclusivo();

    // ── Construtor ─────────────────────────────────────────────

    public PlayerTemplate(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        this.dx = dx;
        this.dy = dy;

        // 1. Dados do personagem (subclasse define)
        DadosPlayer dados = dadosPlayer();

        // 2. Stats
        stats = new StatsComponent(dados);

        // 3. Áudio
        audio.init(dados);

        // 4. Configuração de hitbox
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

        // 5. Câmera
        cameraComponent = new CameraComponent(dx, dy, zoomCamera, tileWidth, numTilesX, tileHeight, numTilesY);

        // 6. Animação
        animacao.initTextures(dados.idleSheet(), dados.walkSheet(), dados.deadSheet(), dados.hurtSheet());
        updateSpriteDefinitions();
        animacao.rebuildAnimations();

        // 7. HUD
        hud = new Hud();
        hudPause = new HudPause(this);

        // 8. Áudios extras da subclasse
        definirAudios();

        // 9. Modelo de tiro exclusivo
        combate.addModeloTiro(modeloTiroExclusivo());
    }

    // ══════════════════════════════════════════════════════════
    //  UPDATE (orquestra todos os componentes)
    // ══════════════════════════════════════════════════════════

    public void update(float delta) {
        // Pause
        if (input.isPauseToggle()) {
            menuAberto = !menuAberto;
            pause = menuAberto;
        }
        if (pause) return;

        if (!stats.isMorto()) {
            // Movimento
            movimento.update(hitBox, stats.getVelocidade(), delta, input);
            moving = movimento.isMoving();
            olhandoEsquerda = movimento.isOlhandoEsquerda();

            // Combate
            ataqueBasico(delta);
            combate.processarTirosAutomaticos(this, delta);
            if (input.isAttackSpecial()) ataqueEspecial();
            if (input.isAbility()) usarHabilidadeEspecial();

            // Debug
            if (input.isDebugToggle()) {
                drawHitBoxes = !drawHitBoxes;
                Gdx.app.log("PlayerTemplate", "Exibir detalhes = " + drawHitBoxes);
                GameConfig.getInstance().setHitboxVisivel(drawHitBoxes);
            }

            // Zoom
            cameraComponent.updateZoom();

            // Cheats
            if (GameConfig.getInstance().isDebugMode() && input.isCheatToggle()) {
                xpUp(999999999);
                stats.setVida(999999999);
            }

            // Animação — troca direção do cache (sem rebuild)
            animacao.checkDirectionChange(olhandoEsquerda);

            // Posição da hitbox
            int offsetX = olhandoEsquerda ? -(hitboxOffsetX) : hitboxOffsetX;
            hitBox.setPosition(
                dx + (TAMANHO_PX - hitboxSize) / 2f + offsetX,
                dy + hitboxOffsetY
            );
        }

        // Timers (sempre rodam, mesmo morto)
        animacao.updateStateTime(delta);
        stats.updateTimers(delta);

        // Partículas
        particulas.atualizar(delta);

        // Tiros
        combate.updateTiros(delta);

        // Áudio
        audio.updateAudio(moving);

        // Colisão → ajusta dx, dy
        colisao.checarColisao(this);

        // Câmera
        cameraComponent.updateCamera(dx, dy);
    }

    // ══════════════════════════════════════════════════════════
    //  DRAW (renderização)
    // ══════════════════════════════════════════════════════════

    public void draw(WorldTemplate world) {
        desenharTiros(world);

        Batch batch = world.getBatch();
        ShapeRenderer shapeRenderer = world.getWorldRenderer().getShapeRenderer();
        OrthographicCamera cam = cameraComponent.getCamera();

        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(
            animacao.animaAtual(stats.isMorto(), stats.isHurt(), moving, stats.getHurtTime()),
            dx, dy, TAMANHO_PX, TAMANHO_PX
        );
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

    // ══════════════════════════════════════════════════════════
    //  AÇÕES
    // ══════════════════════════════════════════════════════════

    public void dano(int forca) {
        if (!stats.aplicarDano(forca)) return;
        if (stats.isMorto()) {
            animacao.resetStateTime();
            audio.playMorte();
        } else {
            audio.playDano();
            particulas.spawnDano(dx + TAMANHO_PX / 2f, dy + TAMANHO_PX / 2f);
        }
        Gdx.app.log(getName(), "Tomou " + forca + " de dano!");
    }

    public void curar(int pontos) {
        stats.curar(pontos);
        particulas.spawnCura(dx + TAMANHO_PX / 2f, dy + TAMANHO_PX / 2f);
    }

    public void xpUp(int xpGanho) {
        int levels = stats.xpUp(xpGanho);
        for (int i = 0; i < levels; i++) {
            audio.playLevelUp();
            particulas.spawnLevelUp(dx + TAMANHO_PX / 2f, dy + TAMANHO_PX / 2f);
        }
    }

    public void moedaUp(int moedasGanha) {
        stats.moedaUp(moedasGanha);
    }

    // ══════════════════════════════════════════════════════════
    //  SAVE / LOAD
    // ══════════════════════════════════════════════════════════

    public void saveData() {
        save.saveData(this);
    }

    public void loadSaveData() {
        save.loadSaveData(this);
    }

    // ══════════════════════════════════════════════════════════
    //  COLISÃO (delegação)
    // ══════════════════════════════════════════════════════════

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

    // ══════════════════════════════════════════════════════════
    //  TIROS (delegação)
    // ══════════════════════════════════════════════════════════

    public void adicionarTiro(TirosTemplate tiro) {
        combate.adicionarTiro(tiro);
    }

    public void removerTiro(TirosTemplate tiro) {
        combate.removerTiro(tiro);
    }

    // ══════════════════════════════════════════════════════════
    //  POSIÇÃO / LOCAL
    // ══════════════════════════════════════════════════════════

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

    // ══════════════════════════════════════════════════════════
    //  DISPOSE
    // ══════════════════════════════════════════════════════════

    public void dispose() {
        clearList();
    }

    // ══════════════════════════════════════════════════════════
    //  GETTERS (mantém API pública compatível com código externo)
    // ══════════════════════════════════════════════════════════

    // Posição
    public int getDx() { return dx; }
    public void setDx(int dx) { this.dx = dx; }
    public int getDy() { return dy; }
    public void setDy(int dy) { this.dy = dy; }
    public Rectangle getHitBox() { return hitBox; }
    public int getTamanho() { return TAMANHO_PX; }
    public int getHitboxSize() { return hitboxSize; }
    public int getHitboxOffsetX() { return hitboxOffsetX; }
    public int getHitboxOffsetY() { return hitboxOffsetY; }

    // Movimento
    public float getNextX() { return movimento.getNextX(); }
    public float getNextY() { return movimento.getNextY(); }
    public boolean isOlhandoEsquerda() { return olhandoEsquerda; }

    // Stats (delegam ao StatsComponent)
    public int getVida() { return stats.getVida(); }
    public int getVidaBase() { return stats.getVidaBase(); }
    public int getForca() { return stats.getForca(); }
    public int getVelocidade() { return stats.getVelocidade(); }
    public int getLevel() { return stats.getLevel(); }
    public int getMoedas() { return stats.getMoedas(); }
    public int getPontosDisponiveis() { return stats.getPontosDisponiveis(); }
    public float getCadenciaTiro() { return stats.getCadenciaTiro(); }
    public float getDuracaoTiro() { return stats.getDuracaoTiro(); }
    public boolean isMorto() { return stats.isMorto(); }

    // Stats upgrade
    public void vidaBaseUp(int pontosGastos) { stats.vidaBaseUp(pontosGastos); }
    public void forcaUp(int pontosGastos) { stats.forcaUp(pontosGastos); }
    public void velocidadeUp(int pontosGastos) { stats.velocidadeUp(pontosGastos); }
    public void gastouPontos(int pontosGastos) { stats.gastouPontos(pontosGastos); }

    // Combate
    public List<TirosTemplate> getTiros() { return combate.getTiros(); }
    public void setPodeAtacar(boolean podeAtacar) { combate.setPodeAtacar(podeAtacar); }

    // Câmera
    public OrthographicCamera getCamera() { return cameraComponent.getCamera(); }
    public CameraComponent getCameraComponent() { return cameraComponent; }
    public AnimacaoComponent getAnimacao() { return animacao; }

    // HUD
    public Hud getHud() { return hud; }

    // Pause
    public boolean isPause() { return pause; }
    public void setPause(boolean pause) {
        this.pause = pause;
        this.menuAberto = pause;
    }

    public StatsComponent getStats() {
        return stats;
    }
}
