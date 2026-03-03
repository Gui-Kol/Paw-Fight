package com.pawfight.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.commun.animation.AnimationEngine;
import com.pawfight.game.commun.phisics.ChecarColisao;
import com.pawfight.game.commun.phisics.CreateHitBox;

import java.util.List;

public class EntityTemplate {
    //Atributos da entidade
    private static final int VIDA = 10;   // Vida
    private static final int FORCA = 1;   // Forca
    private static final int VELOCIDADE = 800;   // pixels/segundo

    // Constantes
    private static final int TAMANHO_PX = 64;       // tamanho fixo do sprite
    // Configuração da hitbox (você pode alterar livremente)
    private static final int HITBOX_SIZE = 20;       // tamanho da hitbox (largura e altura)
    private static final int HITBOX_OFFSET_X = -10;   // deslocamento horizontal (esquerda/direita)
    private static final int HITBOX_OFFSET_Y = 0;  // deslocamento vertical (abaixar ou subir)


    // Spritesheets e animações
    private final Texture idleSheet;
    private final Texture walkSheet;
    private final AnimationEngine animationEngine = new AnimationEngine();
    private boolean olhandoEsquerda = false;

    // Estado do player
    private float stateTime;
    private boolean moving;

    // Mundo e câmera
    private final float mapWidth;
    private final float mapHeight;
    private final OrthographicCamera camera;

    // Colisão
    private final Rectangle hitBox;
    private final CreateHitBox createHitBox = new CreateHitBox();

    // Posição (sempre sincronizada com hitBox)
    private int dx, dy;

    // Construtor
    public EntityTemplate(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        this.dx = dx;
        this.dy = dy;

        // Hitbox inicial (quadrada e ajustável)
        hitBox = new Rectangle(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
            dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );


        // Spritesheets
        idleSheet = new Texture("entitys/player/black_cat/Idle.png");
        walkSheet = new Texture("entitys/player/black_cat/Walk.png");

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
        movePlayer(delta, paredes);

        // Se estiver olhando para a esquerda, inverte o offset horizontal
        int offsetX = olhandoEsquerda
            ? -(HITBOX_OFFSET_X)
            : HITBOX_OFFSET_X;

        hitBox.setPosition(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + offsetX,
            dy + HITBOX_OFFSET_Y
        );

        stateTime += delta;
    }


    // Movimento
    private void movePlayer(float delta, List<Rectangle> paredes) {
        moving = false;
        float speed = VELOCIDADE * delta;

        float nextX = hitBox.x;
        float nextY = hitBox.y;

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

        dx = (int)(hitBox.x - (TAMANHO_PX - HITBOX_SIZE) / 2f - offsetX);
        dy = (int)(hitBox.y - HITBOX_OFFSET_Y);
    }

    // Animação
    private TextureRegion animaAtual() {
        var idleAnimation = animationEngine.animar(idleSheet, 4, 0.12f, false, olhandoEsquerda);
        var walkAnimation = animationEngine.animar(walkSheet, 6, 0.1f, false, olhandoEsquerda);

        return moving ? walkAnimation.getKeyFrame(stateTime, true)
            : idleAnimation.getKeyFrame(stateTime, true);
    }

    // Renderização
    public void draw(SpriteBatch batch) {
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
    }

    public void drawHitbox(ShapeRenderer shapeRenderer) {
        createHitBox.drawHitBox(shapeRenderer, hitBox);
    }

    public void drawSpriteBounds(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
        shapeRenderer.rect(dx, dy, TAMANHO_PX, TAMANHO_PX);
    }

    // Utilitários
    public void dispose() {
        idleSheet.dispose();
        walkSheet.dispose();
    }

    public void updateCamera() {
        camera.position.set(dx, dy, 0);
        camera.update();
    }

    // Getters e Setters
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public OrthographicCamera getCamera() { return camera; }
    public Rectangle getHitBox() { return hitBox; }
    public boolean isOlhandoEsquerda() { return olhandoEsquerda; }

    public void setPosition(int x, int y) {
        this.dx = x;
        this.dy = y;
        hitBox.setPosition(
            x + (TAMANHO_PX - HITBOX_SIZE) / 2f,
            y + HITBOX_OFFSET_Y
        );
    }
}
