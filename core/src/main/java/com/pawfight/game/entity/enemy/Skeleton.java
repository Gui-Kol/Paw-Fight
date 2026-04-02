package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.player.PlayerTemplate;

public class Skeleton extends EnemyTemplate {
    private final MoverDirecaoPlayer moverDirecaoPlayer = new MoverDirecaoPlayer();

    public Skeleton(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
    }

    @Override
    protected DadosInimigo dadosInimigo() {
        return new DadosInimigo(
            "Esqueleto",
            50,
            1,
            300,
            64,
            20,
            0,
            -10,
            new Texture("entitys/enemy/Skeleton/Idle.png"),
            new Texture("entitys/enemy/Skeleton/Walk.png"),
            new Texture("entitys/enemy/Skeleton/Death.png"),
            null,
            null,
            null,
            1.8f,
            null,
            audioEngine.criarAudio("entitys/enemy/Skeleton/audioMorte.wav")
        );
    }

    @Override
    public void updateSpriteDefinitions() {
        idleDefinition = new DefinirSprite(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new DefinirSprite(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new DefinirSprite(deadSheet, 8, 0.1f, false, olhandoEsquerda);
        hurtDefinition = walkDefinition;
        atackDefinition = walkDefinition;
        specialAtackDefinition = walkDefinition;
    }

    @Override
    protected void aplicarStatsForte() {
        float multiplicador = forte ? 1.8f : 1;
        vidaBase = (int) (50 * multiplicador);
        vida = vidaBase;
        forca = (int) (1 * multiplicador);
        velocidade = (int) (300 * multiplicador);
    }

    @Override
    public EnemyTemplate cloneEnemy() {
        return new Skeleton(dx, dy, this.forte, player);
    }

    @Override
    public void andarIA(float delta) {
        moverDirecaoPlayer.mover(this);
    }

    @Override
    public int getTamanho() {
        return forte ? 128 : 64;
    }


    @Override
    protected int moedasMorte() {
        return 1;
    }

    @Override
    public void ataqueBasico() {
        if (hitBox.overlaps(player.getHitBox())) {
            player.dano(forca);
            atacando = true;
        }
    }


    @Override
    public void ataqueEspecial() {
        // Sem ataque especial
    }

    @Override
    public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
    }
}
