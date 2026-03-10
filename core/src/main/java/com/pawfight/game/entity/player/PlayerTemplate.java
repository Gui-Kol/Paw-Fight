package com.pawfight.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.commun.CommunVariable;
import com.pawfight.game.commun.Hud.Hud;
import com.pawfight.game.commun.animation.AnimationEngine;
import com.pawfight.game.commun.animation.SpriteDefinition;
import com.pawfight.game.commun.phisics.ChecarColisao;
import com.pawfight.game.commun.phisics.CreateHitBox;
import com.pawfight.game.commun.phisics.TilemapHitboxFactory;
import com.pawfight.game.commun.Hud.StatusMenu;

import java.util.ArrayList;
import java.util.List;

import static com.pawfight.game.commun.CommunVariable.HITBOX_ISVISIBLE;

public abstract class PlayerTemplate {
    // Atributos comuns
    protected int pontosDisponiveis;
    protected int xp;
    protected int xpNecessario;
    protected int vidaBase;
    protected int velocidade;
    protected int vida;
    protected int forca;
    protected int level;
    protected boolean morto = false;
    protected boolean hurt = false;
    protected boolean moving = false;
    protected boolean drawHitBoxes = false;

    protected float stateTime;
    protected float hurtTime = 0f;
    protected static final float HURT_DURATION = 0.5f;

    // Spritesheets e animações
    private boolean menuAberto;
    private boolean pause;
    protected StatusMenu statusMenu;
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
    protected static int TAMANHO_PX = 64;       // tamanho fixo do sprite
    // Configuração da hitbox (você pode alterar livremente)
    protected static int HITBOX_SIZE = 20;       // tamanho da hitbox (largura e altura)
    protected static int HITBOX_OFFSET_X = -10;   // deslocamento horizontal (esquerda/direita)
    protected static int HITBOX_OFFSET_Y = 0;  // deslocamento vertical (abaixar ou subir)

    // Posição e colisão
    protected List<Rectangle> listColisores;
    protected ChecarColisao checarColisao;
    protected int dx, dy;
    protected final Rectangle hitBox;
    protected final CreateHitBox createHitBox = new CreateHitBox();

    // Mundo e câmera
    protected final float mapWidth;
    protected final float mapHeight;
    protected final OrthographicCamera camera;
    protected final Hud hud;

    // Métodos abstratos (cada player define os seus)
    public abstract void texture();

    public abstract String getName();

    //Movimentacao
    float nextY;
    float nextX;
    float speed;


    public PlayerTemplate(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        this.dx = dx;
        this.dy = dy;

        pontosDisponiveis = 0;

        xp = 0;
        xpNecessario = 200;
        forca = 1;
        level = 1;

        menuAberto = false;
        pause = false;

        hud = new Hud();

        tilemapHitboxFactory = new TilemapHitboxFactory();
        listColisores = new ArrayList<>();
        statusMenu = new StatusMenu(this);

        // Hitbox inicial (quadrada e ajustável)
        hitBox = new Rectangle(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
            dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );

        stateTime = 0f;
        moving = false;

        // Configuração da câmera
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
    }

    // Atualização
    public void update(float delta) {
        if (pause){
            pauseControl();
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
        checarColisao();
        updateCamera();
        texture();
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

    //Controles Gerais
    public void entityControl(float delta) {
        moveEntityControl(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            if (drawHitBoxes) {
                drawHitBoxes = false;
            } else {
                drawHitBoxes = true;
            }
            Gdx.app.log("PlayerTemplate", "Exibir detalhes = " + HITBOX_ISVISIBLE);
            CommunVariable.setHitboxIsvisible(drawHitBoxes);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
                xpUp(2000);
            }
        }
        pauseControl();
    }

    //Controle para pausar
    public void pauseControl(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            statusMenu.criarMenu();
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

    public void checarColisao() {
        checarColisao.checarColisaoSeparadoEixo(listColisores, this);
    }


    public void adicionarColisaoPorLevel(List<Rectangle> colisores, int levelNecessario) {
        if (level < levelNecessario) {
            listColisores.addAll(colisores);
        }
    }

    public void adicionarColisao(List<Rectangle> colisores, ShapeRenderer shapeRenderer) {
        listColisores.addAll(colisores);
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
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
        batch.end();
        tilemapHitboxFactory.draw(shapeRenderer, camera, listColisores);
        createHitBox.drawHitBox(shapeRenderer, hitBox);
        if (menuAberto){
            statusMenu.draw(batch, hud.getHudCamera());
        }
        hud.draw(batch, this, shapeRenderer);
    }

    // Métodos comuns já implementados
    public void dano(int forca) {
        this.vida -= forca;
        if (vida <= 0) {
            morto = true;
            stateTime = 0f;
        } else {
            hurt = true;
            hurtTime = 0f;
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
        statusMenu.dispose();
    }

    public void clearList(){
        listColisores.clear();
    }


    public int getPontosDisponiveis() {
        return pontosDisponiveis;
    }

    public int getXp() {
        return xp;
    }

    public int getVidaBase() {
        return vidaBase;
    }

    public int getForca() {
        return forca;
    }

    public void setForca(int forca) {
        this.forca = forca;
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

    public int getXpNecessario() {
        return xpNecessario;
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

    public static int getTamanhoPx() {
        return TAMANHO_PX;
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

    private void gastouPontos(int pontosGastos) {
        pontosDisponiveis -= pontosGastos;
    }

    public void velocidadeUp(int pontosGastos) {
        velocidade += 20;
        gastouPontos(pontosGastos);
    }

    public void setLocal(float x, float y) {
        this.dx = (int)(x);
        this.dy = (int)(y);

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

    public Hud getHud() {
        return hud;
    }

    public boolean isMorto() {
        return morto;
    }
}
