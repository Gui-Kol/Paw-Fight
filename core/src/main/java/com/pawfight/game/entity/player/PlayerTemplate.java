package com.pawfight.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.CommunVariable;
import com.pawfight.game.engine.Hud.Hud;
import com.pawfight.game.engine.Hud.HudPause;
import com.pawfight.game.engine.design.SpriteDefinition;
import com.pawfight.game.engine.design.ZoomChanger;
import com.pawfight.game.engine.design.animation.AnimationEngine;
import com.pawfight.game.engine.phisics.ChecarColisao;
import com.pawfight.game.engine.phisics.TilemapHitboxFactory;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.tiro.Atirar;
import com.pawfight.game.entity.tiro.TirosTamplate;
import com.pawfight.game.engine.save.SaveDataPlayer;
import com.pawfight.game.world.WorldTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.SocketHandler;

import static com.pawfight.game.engine.CommunVariable.HITBOX_ISVISIBLE;

public abstract class PlayerTemplate {
    //Moedas
    protected int moedas;

    // Atributos comuns
    protected List<TirosTamplate> tirosModelos;
    protected float cadenciaTiro;
    protected float duracaoTiro;
    protected final List<TirosTamplate> tiros;
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


    protected float stateTime;
    protected float hurtTime = 0f;
    protected static final float HURT_DURATION = 0.5f;
    private float danoCooldown = 0f;

    // Spritesheets e animações
    private boolean menuAberto;
    protected HudPause hudPause;
    protected boolean pause;
    protected TilemapHitboxFactory tilemapHitboxFactory;
    protected SpriteDefinition idleDefinition;
    protected SpriteDefinition walkDefinition;
    protected SpriteDefinition deadDefinition;
    protected SpriteDefinition hurtDefinition;
    protected Texture idleSheet;
    protected Texture walkSheet;
    protected Texture deadSheet;
    protected Texture hurtSheet;
    protected Animation<TextureRegion> hurtAnimation;
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> deadAnimation;
    protected final AnimationEngine animationEngine = new AnimationEngine();
    protected boolean olhandoEsquerda = false;

    // Constantes
    protected int TAMANHO_PX;       // tamanho fixo do sprite
    // Configuração da hitbox (você pode alterar livremente)
    protected static int HITBOX_SIZE = 20;       // tamanho da hitbox (largura e altura)
    protected static int HITBOX_OFFSET_X = -10;   // deslocamento horizontal (esquerda/direita)
    protected static int HITBOX_OFFSET_Y = 0;  // deslocamento vertical (abaixar ou subir)

    // Posição e colisão
    protected List<Rectangle> listColisores;
    protected ChecarColisao checarColisao;
    protected int dx, dy;
    protected final Rectangle hitBox;

    // Mundo e camera
    protected final float mapWidth;
    protected final float mapHeight;
    protected final OrthographicCamera camera;
    protected final Hud hud;
    protected final ZoomChanger zoomChanger;
    protected final Renderizar renderizar;

    // Métodos abstratos (cada player define os seus)
    public abstract void texture();

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
        this.zoomChanger = new ZoomChanger();

        pontosDisponiveis = 0;

        xp = 0;
        xpNecessario = 200;
        level = 1;

        forca = definirForca();
        cadenciaTiro = definirCadenciaTiro();
        duracaoTiro = definirDuracaoTiro();
        vidaBase = definirVidaBase();
        velocidade = definirVelocidade();
        TAMANHO_PX = definirTamanho();
        tamanhoTiro = definirTamanhoTiro();

        HITBOX_SIZE = definirHitBoxSize();
        HITBOX_OFFSET_Y = definirHitBoxOffY();
        HITBOX_OFFSET_X = definirHitBoxOffX();

        vida = vidaBase;

        menuAberto = false;
        pause = false;

        tiros = new ArrayList<>();
        tirosModelos = new ArrayList<>();
        atirar = new Atirar();
        hud = new Hud();

        tilemapHitboxFactory = new TilemapHitboxFactory();
        listColisores = new ArrayList<>();
        renderizar = new Renderizar();
        hudPause = new HudPause();

        // Hitbox inicial (quadrada e ajustável)
        hitBox = new Rectangle(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
            dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
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
        texture();
        if (modeloTiroExclusivo() != null) {
            tirosModelos.add(modeloTiroExclusivo());
        }
    }

    protected abstract int definirTamanhoTiro();

    protected abstract int definirHitBoxOffY();

    protected abstract int definirHitBoxOffX();

    protected abstract int definirHitBoxSize();

    protected abstract int definirVelocidade();

    protected abstract int definirVidaBase();

    protected abstract float definirDuracaoTiro();

    protected abstract float definirCadenciaTiro();

    protected abstract int definirForca();

    protected abstract TirosTamplate modeloTiroExclusivo();

    public SaveDataPlayer saveData() {
        SaveDataPlayer data = new SaveDataPlayer();
        data.nomePersonagem = getName(); // cada player define o nome
        data.vidaBase = this.vidaBase;
        data.velocidade = this.velocidade;
        data.forca = this.forca;
        data.level = this.level;
        data.xp = this.xp;
        data.xpNecessario = this.xpNecessario;
        data.moedas = this.moedas;
        data.pontosDisponiveis = this.pontosDisponiveis;
        return data;
    }

    public void loadSaveData(SaveDataPlayer data) {
        this.vidaBase = data.vidaBase;
        this.vida = data.vidaBase;
        this.velocidade = data.velocidade;
        this.forca = data.forca;
        this.level = data.level;
        this.xp = data.xp;
        this.xpNecessario = data.xpNecessario;
        this.moedas = data.moedas;
        this.pontosDisponiveis = data.pontosDisponiveis;
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

            // Se estiver olhando para a esquerda, inverte o offset horizontal
            int offsetX = olhandoEsquerda ? -(HITBOX_OFFSET_X) : HITBOX_OFFSET_X;

            hitBox.setPosition(
                dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
                dy + HITBOX_OFFSET_Y
            );
            texture();
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
            if (danoCooldown >= 0.5f) {
                podeTomarDano = true;
                danoCooldown =  0f;
            }
        }
        checarColisao();
        updateCamera();
        texture();
    }

    protected abstract int definirTamanho();

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
            if (tirosModelos == null || tirosModelos.isEmpty()){return;}
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
            Gdx.app.log("PlayerTemplate", "Exibir detalhes = " + HITBOX_ISVISIBLE);
            CommunVariable.setHitboxIsvisible(drawHitBoxes);
        }
        camera.zoom = zoomChanger.changeZoom();

        controleTestes();
    }

    private void controleTestes() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
            xpUp(999999999);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F6)) {
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
    }

    public void moedaUp(int moedasGanha) {
        moedas += moedasGanha;
    }

    public void checarColisao() {
        checarColisao.checarColisaoSeparadoEixo(listColisores, this);
    }


    public void adicionarColisaoPorLevel(List<Rectangle> colisores, int levelNecessario) {
        if (level < levelNecessario) {
            listColisores.addAll(colisores);
        }
    }

    public void adicionarColisao(List<Rectangle> colisores) {
        listColisores.addAll(colisores);
    }

    public void adicionarTiro(TirosTamplate tiro) {
        tiros.add(tiro);
    }
    public void removerTiro(TirosTamplate tiro) {
        tiros.remove(tiro);
    }
    // Animação
    protected TextureRegion animaAtual() {
        idleAnimation = animationEngine.animar(idleDefinition);
        walkAnimation = animationEngine.animar(walkDefinition);
        hurtAnimation = animationEngine.animar(hurtDefinition);
        deadAnimation = animationEngine.animar(deadDefinition);
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
        hudPause.draw(world);
    }
    public void desenharTiros(WorldTemplate world){
        Batch batch = world.getBatch();
        ShapeRenderer shapeRenderer = world.getShapeRenderer();

        batch.setProjectionMatrix(camera.combined);
        for (TirosTamplate tiro : tiros) {
            tiro.draw(batch, shapeRenderer);
        }
    }

    public void drawHud(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        hud.draw(batch, this, shapeRenderer);
    }

    // Métodos comuns já implementados
    public void dano(int forca) {
        if (podeTomarDano) {
            podeTomarDano = false;
            this.vida -= forca;
            if (vida <= 0) {
                morto = true;
                stateTime = 0f;
            } else {
                hurt = true;
                hurtTime = 0f;
            }
            Gdx.app.log(getName(),"Tomou " + forca + " de dano!");
        }
    }

    public void updateCamera() {
        camera.position.set(dx, dy, 0);
        camera.update();
    }

    public void dispose() {
        idleSheet.dispose();
        walkSheet.dispose();
        deadSheet.dispose();
        hurtSheet.dispose();
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

    public static int getHitboxSize() {
        return HITBOX_SIZE;
    }

    public static int getHitboxOffsetX() {
        return HITBOX_OFFSET_X;
    }

    public static int getHitboxOffsetY() {
        return HITBOX_OFFSET_Y;
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

        int offsetX = olhandoEsquerda ? -(HITBOX_OFFSET_X) : HITBOX_OFFSET_X;
        hitBox.setPosition(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
            dy + HITBOX_OFFSET_Y
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

    public List<TirosTamplate> getTiros() {
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
}
