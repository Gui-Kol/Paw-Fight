package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.animation.SpriteDefinition;
import com.pawfight.game.entity.player.PlayerTemplate;

public class EnemySkeleton extends EnemyTemplate{
    public EnemySkeleton(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
        float multiplicador = forte ? 1.8f : 1;

        vidaBase = (int)(50 * multiplicador);
        vida = vidaBase;
        forca = (int)(1 * multiplicador);
        velocidade = (int)(300 * multiplicador);
    }

    @Override
    public void texture() {
        idleSheet = new Texture("entitys/enemy/Skeleton/Idle.png");
        walkSheet = new Texture("entitys/enemy/Skeleton/Walk.png");
        deadSheet = new Texture("entitys/enemy/Skeleton/Death.png");
        idleDefinition = new SpriteDefinition(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new SpriteDefinition(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new SpriteDefinition(deadSheet, 8, 0.1f, false, olhandoEsquerda);
        hurtDefinition = walkDefinition;
        atackDefinition = walkDefinition;
        specialAtackDefinition = walkDefinition;
    }

    @Override
    public EnemyTemplate cloneEnemy() {
        return new EnemySkeleton(dx, dy, forte, player);
    }

    @Override
    public void andarIA(float delta) {
        if (player != null && !player.isMorto()) {
            moverEmDirecaoAoPlayer(delta);
        }
    }

    @Override
    public int getTamanho() {
        return forte ? 128 : 64;
    }

    @Override
    public void ataqueBasico() {
        if (hitBox.overlaps(player.getHitBox())) {
            player.receberDano(forca);
            dano(player.getForca());
            atacando = true;
        }
    }

    @Override
    public void criarHitBox() {
        this.hitBox = new Rectangle(
            dx + (TAMANHO_PX - HITBOX_SIZE) / 2f + HITBOX_OFFSET_X,
            dy + HITBOX_OFFSET_Y,
            HITBOX_SIZE,
            HITBOX_SIZE
        );
    }

    @Override
    public void ataqueEspecial() {
        // Sem ataque especial
    }

    @Override
    public void draw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        var cameraCombined = player.getCamera().combined;
        shapeRenderer.setProjectionMatrix(cameraCombined);
        batch.setProjectionMatrix(cameraCombined);

        batch.begin();
        batch.draw(animaAtual(), dx, dy, TAMANHO_PX, TAMANHO_PX);
        batch.end();

        drawHitBox.draw(shapeRenderer, hitBox);
    }
}
