package com.pawfight.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.AudioEngine;
import com.pawfight.game.engine.Hud.Hud;
import com.pawfight.game.engine.Hud.HudPause;
import com.pawfight.game.engine.VariavelComum;
import com.pawfight.game.engine.design.AlteradorZoom;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.design.animation.MotorAnimacao;
import com.pawfight.game.engine.fisica.ChecarColisao;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.engine.save.DadosSalvosJogador;
import com.pawfight.game.entity.Entidade;
import com.pawfight.game.entity.tiro.Atirar;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.pawfight.game.engine.VariavelComum.DEBUG_MODE;
import static com.pawfight.game.engine.VariavelComum.VOLUME_EFEITOS;


public abstract class PlayerTemplate implements Entidade {
    // Constantes
    private static final float HURT_DURATION = 0.5f;
    private static final float DANO_COOLDOWN_DURATION = 0.5f;

    //Moedas
    protected int moedas;

    // Atributos comuns
    protected List<TirosTemplate> tirosModelos;
    protected float cadenciaTiro;
    protected float duracaoTiro;
    protected final List<TirosTemplate> tiros;
    protected final Atirar atirar;
    protected int pontosDisponiveis;
    protected int xp;
    protected int xpNecessario;
    protected int vidaBase;
    protected int velocidade;
    protected int vida;
    protected int forca;
    protected int level;
    protected int tamanhoTiro;
    protected boolean morto = false;
    protected boolean hurt = false;
    protected boolean podeAtacar = false;
    protected boolean podeTomarDano = true;
    protected boolean moving;
    protected boolean drawHitBoxes = false;
    protected Music audioDano;
    protected Music audioMorte;
    protected Music audioLevelUp;
    protected Music audioMoving;


    protected float stateTime;
    protected float hurtTime = 0f;
    private float danoCooldown = 0f;

    // Spritesheets e animações
    private boolean menuAberto;
    protected HudPause hudPause;
    protected boolean pause;
    protected TilemapHitboxFactory tilemapHitboxFactory;
    protected DefinirSprite idleDefinition;
    protected DefinirSprite walkDefinition;
    protected DefinirSprite deadDefinition;
    protected DefinirSprite hurtDefinition;
    protected Texture idleSheet;
    protected Texture walkSheet;
    protected Texture deadSheet;
    protected Texture hurtSheet;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> deadAnimation;
    protected final MotorAnimacao MotorAnimacao = new MotorAnimacao();
    protected boolean olhandoEsquerda = false;
    private boolean lastOlhandoEsquerda = false;
    private boolean animationsDirty = true;

    // Configuração da hitbox (campos de INSTÂNCIA, não static)
    protected int TAMANHO_PX;
    protected int hitboxSize;
    protected int hitboxOffsetX;
    protected int hitboxOffsetY;

    // Posição e colisão
    protected List<Rectangle> listColisores;
    protected ChecarColisao checarColisao;
    protected int dx, dy;
    protected final Rectangle hitBox;

    // Mundo e camera
    protected final AudioEngine audioEngine = new AudioEngine();
    protected final float mapWidth;
    protected final float mapHeight;
    protected final OrthographicCamera camera;
    protected final Hud hud;
    protected final AlteradorZoom AlteradorZoom;
    protected final Renderizar renderizar = Renderizar.INSTANCE;

    // Métodos abstratos (cada player define os seus)

    public abstract DadosPlayer dadosPlayer();

    public abstract void updateSpriteDefinitions();

    public abstract String getName();

    public abstract void ataqueBasico(float delta);

    public abstract void ataqueEspecial();

    public abstract void usarHabilidadeEspecial();

    //Movimentação
    float nextY;
    float nextX;
    float speed;


    public PlayerTemplate(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        this.dx = dx;
        this.dy = dy;
        this.AlteradorZoom = new AlteradorZoom();
        DadosPlayer dadosPlayer = dadosPlayer();

        audioLevelUp = audioEngine.criarAudio("entitys/player/audios/power_up.wav");
        audioDano = audioEngine.criarAudio("entitys/player/audios/hurt.wav");
        audioMoving = dadosPlayer.audioMoving();

        pontosDisponiveis = 0;

        xp = 0;
        xpNecessario = 200;
        level = 1;

        forca = dadosPlayer.forca();
        cadenciaTiro = dadosPlayer.cadenciaTiro();
        duracaoTiro = dadosPlayer.duracaoTiro();
        vidaBase = dadosPlayer.vidaBase();
        velocidade = dadosPlayer.velocidade();
        TAMANHO_PX = dadosPlayer.tamanho();
        tamanhoTiro = dadosPlayer.tamanhoTiro();

        hitboxSize = dadosPlayer.hitboxSize();
        hitboxOffsetY = dadosPlayer.hitboxOffsetY();
        hitboxOffsetX = dadosPlayer.hitboxOffsetX();

        vida = vidaBase;

        menuAberto = false;
        pause = false;

        tiros = new ArrayList<>();
        tirosModelos = new ArrayList<>();
        atirar = new Atirar();
        hud = new Hud();

        tilemapHitboxFactory = new TilemapHitboxFactory();
        listColisores = new ArrayList<>();
        hudPause = new HudPause(this);

        // Hitbox inicial (quadrada e ajustável)
        hitBox = new Rectangle(
            dx + (TAMANHO_PX - hitboxSize) / 2f + hitboxOffsetX,
            dy + hitboxOffsetY,
            hitboxSize,
            hitboxSize
        );

        stateTime = 0f;
        moving = false;

        // Configuração da camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = zoomCamera;

        mapWidth = tileWidth * numTilesX;
        mapHeight = tileHeight * numTilesY;

        checarColisao = new ChecarColisao();

        camera.position.set(dx, dy, 0);
        camera.position.x = MathUtils.clamp(camera.position.x, camera.viewportWidth / 2f, mapWidth - camera.viewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, camera.viewportHeight / 2f, mapHeight - camera.viewportHeight / 2f);
        camera.update();

        // Carrega texturas UMA VEZ e cria definições iniciais
        idleSheet = dadosPlayer.idleSheet();
        walkSheet = dadosPlayer.walkSheet();
        deadSheet = dadosPlayer.deadSheet();
        hurtSheet = dadosPlayer.hurtSheet();
        updateSpriteDefinitions();
        rebuildAnimations();
        definirAudios();

        if (modeloTiroExclusivo() != null) {
            tirosModelos.add(modeloTiroExclusivo());
        }
    }

    protected abstract void definirAudios();

    protected abstract TirosTemplate modeloTiroExclusivo();

    private void rebuildAnimations() {
        idleAnimation = MotorAnimacao.animar(idleDefinition);
        walkAnimation = MotorAnimacao.animar(walkDefinition);
        hurtAnimation = MotorAnimacao.animar(hurtDefinition);
        deadAnimation = MotorAnimacao.animar(deadDefinition);
        animationsDirty = false;
    }

    public DadosSalvosJogador saveData() {
        DadosSalvosJogador data = new DadosSalvosJogador();
        data.setNomePersonagem(getName());
        data.setVidaBase(this.vidaBase);
        data.setVelocidade(this.velocidade);
        data.setForca(this.forca);
        data.setLevel(this.level);
        data.setXp(this.xp);
        data.setXpNecessario(this.xpNecessario);
        data.setMoedas(this.moedas);
        data.setPontosDisponiveis(this.pontosDisponiveis);
        return data;
    }

    public void loadSaveData(DadosSalvosJogador data) {
        this.vidaBase = data.getVidaBase();
        this.vida = data.getVidaBase();
        this.velocidade = data.getVelocidade();
        this.forca = data.getForca();
        this.level = data.getLevel();
        this.xp = data.getXp();
        this.xpNecessario = data.getXpNecessario();
        this.moedas = data.getMoedas();
        this.pontosDisponiveis = data.getPontosDisponiveis();
        Gdx.app.log("PlayerTemplate", "Save carregado para " + getName());
    }

    // Atualização
    public void update(float delta) {
        pauseControl();
        if (pause) {
            return;
        }

        if (!morto) {
            entityControl(delta);

            // Atualiza animações apenas quando a direção muda
            if (olhandoEsquerda != lastOlhandoEsquerda) {
                lastOlhandoEsquerda = olhandoEsquerda;
                updateSpriteDefinitions();
                animationsDirty = true;
            }

            // Se estiver olhando para a esquerda, inverte o offset horizontal
            int offsetX = olhandoEsquerda ? -(hitboxOffsetX) : hitboxOffsetX;

            hitBox.setPosition(
                dx + (TAMANHO_PX - hitboxSize) / 2f + offsetX,
                dy + hitboxOffsetY
            );
        }
        stateTime += delta;
        if (hurt) {
            hurtTime += delta;
            if (hurtTime >= HURT_DURATION) {
                hurt = false;
                hurtTime = 0f;
            }
        }
        if (!podeTomarDano) {
            danoCooldown += delta;
            if (danoCooldown >= DANO_COOLDOWN_DURATION) {
                podeTomarDano = true;
                danoCooldown = 0f;
            }
        }

        // Atualiza tiros e remove os expirados (via game loop, sem Timer)
        updateTiros(delta);
        updateAudio();

        checarColisao();
        updateCamera();
    }

    private void updateAudio() {
        if (moving) {
            audioEngine.passos(audioMoving);
        } else if (audioMoving != null && audioMoving.isPlaying()) {
            audioMoving.stop();
        }
    }



    private void updateTiros(float delta) {
        Iterator<TirosTemplate> it = tiros.iterator();
        while (it.hasNext()) {
            TirosTemplate tiro = it.next();
            tiro.update(delta);
            if (tiro.isExpirado()) {
                it.remove();
                tiro.dispose();
            }
        }
    }


    // Movimento
    protected void moveEntityControl(float delta) {
        moving = false;
        speed = velocidade * delta;

        nextX = hitBox.x;
        nextY = hitBox.y;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            nextX += speed;
            olhandoEsquerda = false;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            nextX -= speed;
            olhandoEsquerda = true;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            nextY += speed;
            moving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            nextY -= speed;
            moving = true;
        }
    }

    public void combatMoves(float delta) {
        // Controles de combate
        ataqueBasico(delta);
        if (podeAtacar) {
            if (tirosModelos == null || tirosModelos.isEmpty()) {
                return;
            }
            atirar.atira(tirosModelos, this, delta);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ataqueEspecial();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            usarHabilidadeEspecial();
        }
    }

    //Controles Gerais
    public void entityControl(float delta) {
        moveEntityControl(delta);
        combatMoves(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            drawHitBoxes = !drawHitBoxes;
            Gdx.app.log("PlayerTemplate", "Exibir detalhes = " + drawHitBoxes);
            VariavelComum.setHitboxIsvisible(drawHitBoxes);
        }
        camera.zoom = AlteradorZoom.changeZoom();

        if (DEBUG_MODE) {
            controleTestes();
        }
    }

    private void controleTestes() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            xpUp(999999999);
            vida = 999999999;
        }
    }

    //Controle para pausar
    public void pauseControl() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            menuAberto = !menuAberto;
            pause = menuAberto;
        }
    }

    public void xpUp(int xpGanho) {
        xp += xpGanho;
        while (xp >= level * xpNecessario) {
            xp -= level * xpNecessario;
            levelUp();

            if (level % 5 == 0) {
                xpNecessario += 200;
                Gdx.app.log("PlayerTemplate", "XP necessário para próximo nível: " + xpNecessario);
            }
        }
    }

    private void levelUp() {
        level += 1;
        pontosDisponiveis += 1;
        audioEngine.efeito(audioLevelUp);
    }

    public void moedaUp(int moedasGanha) {
        moedas += moedasGanha;
    }

    public void checarColisao() {
        checarColisao.checarColisaoSeparadoEixo(listColisores, this);
    }


    public void adicionarColisaoPorLevel(List<Rectangle> colisores, int levelNecessario) {
        if (colisores == null || colisores.isEmpty()) return;

        if (level < levelNecessario) {
            if (!listColisores.contains(colisores.get(0))) {
                listColisores.addAll(colisores);
            }
        } else {
            listColisores.removeAll(colisores);
        }
    }

    public void adicionarColisao(List<Rectangle> colisores) {
        listColisores.addAll(colisores);
    }

    public void adicionarTiro(TirosTemplate tiro) {
        tiros.add(tiro);
    }

    public void removerTiro(TirosTemplate tiro) {
        tiros.remove(tiro);
    }

    // Animação — usa cache, reconstrói apenas quando direção muda
    protected TextureRegion animaAtual() {
        if (animationsDirty) {
            rebuildAnimations();
        }
        if (morto) {
            if (deadAnimation.isAnimationFinished(stateTime)) {
                return deadAnimation.getKeyFrames()[deadAnimation.getKeyFrames().length - 1];
            } else {
                return deadAnimation.getKeyFrame(stateTime, false);
            }
        }
        if (hurt) {
            return hurtAnimation.getKeyFrame(hurtTime, false);
        }
        return moving ? walkAnimation.getKeyFrame(stateTime, true) : idleAnimation.getKeyFrame(stateTime, true);
    }

    // Renderização
    public void draw(WorldTemplate world) {
        desenharTiros(world);

        Batch batch = world.getBatch();
        ShapeRenderer shapeRenderer = world.getShapeRenderer();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        tilemapHitboxFactory.draw(shapeRenderer, camera, listColisores);
        renderizar.hitboxDraw(shapeRenderer, hitBox);
    }

    public void desenharTiros(WorldTemplate world) {
        if (tiros.isEmpty()) return;

        // Cópia defensiva — evita ConcurrentModificationException
        List<TirosTemplate> tirosSnapshot = new ArrayList<>(tiros);

        Batch batch = world.getBatch();
        ShapeRenderer shapeRenderer = world.getShapeRenderer();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (TirosTemplate tiro : tirosSnapshot) {
            tiro.desenhar(batch);
        }
        batch.end();

        // Hitboxes desenhados fora do batch
        shapeRenderer.setProjectionMatrix(camera.combined);
        for (TirosTemplate tiro : tirosSnapshot) {
            tiro.desenharHitbox(shapeRenderer);
        }
    }

    public void drawHud(SpriteBatch batch, ShapeRenderer shapeRenderer, WorldTemplate world) {
        hud.draw(batch, this, shapeRenderer);
        hudPause.draw(world);
    }

    // Métodos comuns já implementados
    public void dano(int forca) {
        if (podeTomarDano) {
            podeTomarDano = false;
            this.vida -= forca;
            if (vida <= 0) {
                morto = true;
                stateTime = 0f;
                audioEngine.efeito(audioMorte);
            } else {
                hurt = true;
                hurtTime = 0f;
                audioEngine.efeito(audioDano);
            }
            Gdx.app.log(getName(), "Tomou " + forca + " de dano!");
        }
    }

    public void updateCamera() {
        camera.position.set(dx, dy, 0);
        camera.update();
    }

    public void dispose() {
        if (idleSheet != null) idleSheet.dispose();
        if (walkSheet != null) walkSheet.dispose();
        if (deadSheet != null) deadSheet.dispose();
        if (hurtSheet != null) hurtSheet.dispose();
        clearList();
    }

    public void clearList() {
        listColisores.clear();
        tiros.clear();
    }


    public int getPontosDisponiveis() {
        return pontosDisponiveis;
    }

    public int getVidaBase() {
        return vidaBase;
    }

    public int getForca() {
        return forca;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public int getVida() {
        return vida;
    }

    public int getVelocidade() {
        return velocidade;
    }

    public int getLevel() {
        return level;
    }

    public boolean isOlhandoEsquerda() {
        return olhandoEsquerda;
    }

    public int getHitboxSize() {
        return hitboxSize;
    }

    public int getHitboxOffsetX() {
        return hitboxOffsetX;
    }

    public int getHitboxOffsetY() {
        return hitboxOffsetY;
    }

    public float getNextY() {
        return nextY;
    }

    public float getNextX() {
        return nextX;
    }

    public void vidaBaseUp(int pontosGastos) {
        vidaBase += 1;
        vida = vidaBase;
        gastouPontos(pontosGastos);
    }

    public void forcaUp(int pontosGastos) {
        forca += 1;
        gastouPontos(pontosGastos);
    }

    public void gastouPontos(int pontosGastos) {
        pontosDisponiveis -= pontosGastos;
    }

    public void velocidadeUp(int pontosGastos) {
        velocidade += 20;
        gastouPontos(pontosGastos);
    }


    public void setLocal(float x, float y) {
        this.dx = (int) (x);
        this.dy = (int) (y);

        // Reseta estado de pausa e menu
        pause = false;
        menuAberto = false;

        int offsetX = olhandoEsquerda ? -(hitboxOffsetX) : hitboxOffsetX;
        hitBox.setPosition(
            dx + (TAMANHO_PX - hitboxSize) / 2f + offsetX,
            dy + hitboxOffsetY
        );

        updateCamera();
    }

    public void setPodeAtacar(boolean podeAtacar) {
        this.podeAtacar = podeAtacar;
    }

    public Hud getHud() {
        return hud;
    }

    public boolean isMorto() {
        return morto;
    }

    public boolean isPause() {
        return pause;
    }

    public List<TirosTemplate> getTiros() {
        return tiros;
    }

    public int getMoedas() {
        return moedas;
    }

    public int getTamanho() {
        return TAMANHO_PX;
    }

    public float getCadenciaTiro() {
        return cadenciaTiro;
    }

    public float getDuracaoTiro() {
        return duracaoTiro;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
        this.menuAberto = pause;
    }
}
