package com.pawfight.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.commun.animation.AnimationEngine;
import com.pawfight.game.commun.phisics.CreateHitBox;


public class Player {
    // Spritesheets
    private Texture idleSheet;
    private Texture walkSheet;

    // Animações
    private AnimationEngine animationEngine = new AnimationEngine();
    private boolean olhandoEsquerda = false;

    // Estado
    private float stateTime;
    private boolean moving;

    // Mundo
    private float mapWidth;
    private float mapHeight;
    private OrthographicCamera camera;
    private Rectangle hitBox;
    private static final float VELOCIDADE = 800; // pixels/segundo
    private static final int TAMANHO_PX = 64;    // tamanho fixo do sprite
    private CreateHitBox createHitBox = new CreateHitBox();
    private int dx, dy;

    public Player(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        this.dx = dx;
        this.dy = dy;

        hitBox = createHitBox.create(dx, dy, TAMANHO_PX / 3, TAMANHO_PX / 3);

        // Carrega spritesheets
        idleSheet = new Texture("entitys/player/black_cat/Idle.png");
        walkSheet = new Texture("entitys/player/black_cat/Walk.png");

        stateTime = 0f;
        moving = false;

        //Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = zoomCamera;
        // limites do mapa
        mapWidth = tileWidth * numTilesX;
        mapHeight = tileHeight * numTilesY;
        // posição inicial
        camera.position.set(dx, dy, 0);
        camera.position.x = MathUtils.clamp(camera.position.x, camera.viewportWidth / 2f, mapWidth - camera.viewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, camera.viewportHeight / 2f, mapHeight - camera.viewportHeight / 2f);
        camera.update();
    }

    public void update(float delta, int limiteX, int limiteY) {
        movePlayer(delta, limiteX, limiteY);
        hitBox.setPosition(dx + TAMANHO_PX / 4, dy);
        if (olhandoEsquerda) {
            hitBox.x = (int) (dx + TAMANHO_PX / 2.4);
        }

        stateTime += delta;
    }

    private TextureRegion animaAtual() {
        var idleAnimation = animationEngine.animar(idleSheet, 4, 0.12f, false, olhandoEsquerda);
        var walkAnimation = animationEngine.animar(walkSheet, 6, 0.1f, false, olhandoEsquerda);

        TextureRegion currentFrame;
        if (moving) {
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }
        return currentFrame;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
    }

    public void drawHitbox(ShapeRenderer shapeRenderer, Batch batch) {
        createHitBox.drawHitBox(shapeRenderer, hitBox);
    }

    public void dispose() {
        idleSheet.dispose();
        walkSheet.dispose();
    }

    private void movePlayer(float delta, int limiteX, int limiteY) {
        moving = false;
        float speed = VELOCIDADE * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (dx < limiteX - hitBox.width) {
                dx += speed;
                moving = true;
                olhandoEsquerda = false;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (dx > -hitBox.width) {
                dx -= speed;
                olhandoEsquerda = true;
                moving = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (dy < limiteY - hitBox.height) {
                dy += speed;
                moving = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (dy > 0) {
                dy -= speed;
                moving = true;
            }
        }
    }

    public void updateCamera() {
        camera.position.set(dx, dy, 0);
        camera.update();
    }

    public int getDy() {
        return dy;
    }

    public int getDx() {
        return dx;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void setPosition(int x, int y) {
        this.dx = x;
        this.dy = y;
        hitBox.setPosition(x, y);
    }

}
