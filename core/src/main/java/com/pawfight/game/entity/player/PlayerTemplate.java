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
import com.pawfight.game.commun.animation.SpriteDefinition;
import com.pawfight.game.commun.animation.AnimationEngine;
import com.pawfight.game.commun.phisics.ChecarColisao;
import com.pawfight.game.commun.phisics.CreateHitBox;

import java.util.List;

public abstract class PlayerTemplate {
    // Atributos comuns
    protected int vidaBase = 10;
    protected static final int VELOCIDADE = 800;
    protected int vida = vidaBase;
    protected int forca = 1;
    protected boolean morto = false;
    protected boolean hurt = false;
    protected boolean moving = false;

    protected float stateTime = 0f;
    protected float hurtTime = 0f;
    protected static final float HURT_DURATION = 0.5f;

    // Spritesheets e animações
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
    protected static final int TAMANHO_PX = 64;       // tamanho fixo do sprite
    // Configuração da hitbox (você pode alterar livremente)
    protected static final int HITBOX_SIZE = 20;       // tamanho da hitbox (largura e altura)
    protected static final int HITBOX_OFFSET_X = -10;   // deslocamento horizontal (esquerda/direita)
    protected static final int HITBOX_OFFSET_Y = 0;  // deslocamento vertical (abaixar ou subir)

    // Posição e colisão
    protected int dx, dy;
    protected final Rectangle hitBox;
    protected final CreateHitBox createHitBox = new CreateHitBox();

    // Mundo e câmera
    protected final float mapWidth;
    protected final float mapHeight;
    protected final OrthographicCamera camera;

    // Métodos abstratos (cada player define os seus)
    public abstract void dispose();

    //Movimentacao
    float nextY;
    float nextX;
    float speed;



    public PlayerTemplate(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        this.dx = dx;
        this.dy = dy;

        vida = vidaBase;

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

        camera.position.set(dx, dy, 0);
        camera.position.x = MathUtils.clamp(camera.position.x, camera.viewportWidth / 2f, mapWidth - camera.viewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, camera.viewportHeight / 2f, mapHeight - camera.viewportHeight / 2f);
        camera.update();
    }


    // Atualização
    public void update(float delta, List<Rectangle> paredes) {
        if (!isMorto()) {

            moveEntity(delta, paredes);

            // Se estiver olhando para a esquerda, inverte o offset horizontal
            int offsetX = olhandoEsquerda
                ? -(HITBOX_OFFSET_X)
                : HITBOX_OFFSET_X;

            hitBox.setPosition(
                dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
                dy + HITBOX_OFFSET_Y
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

    }

    // Movimento
    protected void moveEntity(float delta, List<Rectangle> paredes) {
        moving = false;
        speed = VELOCIDADE * delta;

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

        // Verifica colisão separada por eixo
        ChecarColisao.ajustarPosicaoSeBaterParede(hitBox, nextX, nextY, paredes);

        int offsetX = olhandoEsquerda
            ? -(HITBOX_OFFSET_X)
            : HITBOX_OFFSET_X;

        dx = (int) (hitBox.x - (TAMANHO_PX - HITBOX_SIZE) / 2f - offsetX);
        dy = (int) (hitBox.y - HITBOX_OFFSET_Y);
    }

    // Animação
    protected TextureRegion animaAtual() {
        idleAnimation = animationEngine.animar(idleDefinition);
        walkAnimation = animationEngine.animar(walkDefinition);
        hurtAnimation = animationEngine.animar(hurtDefinition);
        deadAnimation = animationEngine.animar(deadDefinition);
        if (isMorto()) {
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
        batch.begin();
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        createHitBox.drawHitBox(shapeRenderer, hitBox);
        shapeRenderer.end();
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

    public boolean isMorto() {
        return morto;
    }

    public boolean isHurt() {
        return hurt;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaBase() {
        return vidaBase;
    }

    public int getForca() {
        return forca;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setPosition(int x, int y) {
        this.dx = x;
        this.dy = y;
        hitBox.setPosition(x, y);
    }

    public boolean isOlhandoEsquerda() {
        return olhandoEsquerda;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public void setForca(int forca) {
        this.forca = forca;
    }

    public void setMorto(boolean morto) {
        this.morto = morto;
    }

    public void setHurt(boolean hurt) {
        this.hurt = hurt;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public float getHurtTime() {
        return hurtTime;
    }

    public void setHurtTime(float hurtTime) {
        this.hurtTime = hurtTime;
    }

    public SpriteDefinition getIdleDefinition() {
        return idleDefinition;
    }

    public void setIdleDefinition(SpriteDefinition idleDefinition) {
        this.idleDefinition = idleDefinition;
    }

    public SpriteDefinition getWalkDefinition() {
        return walkDefinition;
    }

    public void setWalkDefinition(SpriteDefinition walkDefinition) {
        this.walkDefinition = walkDefinition;
    }

    public SpriteDefinition getDeadDefinition() {
        return deadDefinition;
    }

    public void setDeadDefinition(SpriteDefinition deadDefinition) {
        this.deadDefinition = deadDefinition;
    }

    public SpriteDefinition getHurtDefinition() {
        return hurtDefinition;
    }

    public void setHurtDefinition(SpriteDefinition hurtDefinition) {
        this.hurtDefinition = hurtDefinition;
    }

    public Texture getIdleSheet() {
        return idleSheet;
    }

    public void setIdleSheet(Texture idleSheet) {
        this.idleSheet = idleSheet;
    }

    public Texture getWalkSheet() {
        return walkSheet;
    }

    public void setWalkSheet(Texture walkSheet) {
        this.walkSheet = walkSheet;
    }

    public Texture getDeadSheet() {
        return deadSheet;
    }

    public void setDeadSheet(Texture deadSheet) {
        this.deadSheet = deadSheet;
    }

    public Texture getHurtSheet() {
        return hurtSheet;
    }

    public void setHurtSheet(Texture hurtSheet) {
        this.hurtSheet = hurtSheet;
    }

    public Animation<TextureRegion> getHurtAnimation() {
        return hurtAnimation;
    }

    public void setHurtAnimation(Animation<TextureRegion> hurtAnimation) {
        this.hurtAnimation = hurtAnimation;
    }

    public Animation<TextureRegion> getIdleAnimation() {
        return idleAnimation;
    }

    public void setIdleAnimation(Animation<TextureRegion> idleAnimation) {
        this.idleAnimation = idleAnimation;
    }

    public Animation<TextureRegion> getWalkAnimation() {
        return walkAnimation;
    }

    public void setWalkAnimation(Animation<TextureRegion> walkAnimation) {
        this.walkAnimation = walkAnimation;
    }

    public Animation<TextureRegion> getDeadAnimation() {
        return deadAnimation;
    }

    public void setDeadAnimation(Animation<TextureRegion> deadAnimation) {
        this.deadAnimation = deadAnimation;
    }

    public AnimationEngine getAnimationEngine() {
        return animationEngine;
    }

    public void setOlhandoEsquerda(boolean olhandoEsquerda) {
        this.olhandoEsquerda = olhandoEsquerda;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public CreateHitBox getCreateHitBox() {
        return createHitBox;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }
}
