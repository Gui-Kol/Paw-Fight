package com.pawfight.game.engine.design.particle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.design.animation.MotorAnimacao;
import com.pawfight.game.engine.loading.Assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GerenciadorParticulas {

    // ── Parâmetros ajustáveis: Dano ────────────────────────────
    public static final String SPRITE_DANO_PATH = "particles/dano.png";
    public static final int DANO_NUM_FRAME = 6;
    public static final float DANO_FRAME_DURATION = 0.3f;
    public static final float DANO_W = 64f;
    public static final float DANO_H = 64f;
    public static final float DANO_OFFSET_X = 0f;
    public static final float DANO_OFFSET_Y = 0f;
    public static final float DANO_TEMPO_VIDA = 0.4f;
    public static final boolean DANO_REVERSE = false;

    // ── Parâmetros ajustáveis: Cura ────────────────────────────
    public static final String SPRITE_CURA_PATH = "particles/cura.png";
    public static final int CURA_NUM_FRAME = 6;
    public static final float CURA_FRAME_DURATION = 0.3f;
    public static final float CURA_W = 64f;
    public static final float CURA_H = 64f;
    public static final float CURA_OFFSET_X = 0f;
    public static final float CURA_OFFSET_Y = 24f;
    public static final float CURA_TEMPO_VIDA = 0.6f;
    public static final boolean CURA_REVERSE = false;

    // ── Parâmetros ajustáveis: Level Up ────────────────────────
    public static final String SPRITE_LEVEL_UP_PATH = "particles/levelup.png";
    public static final int LEVEL_UP_NUM_FRAME = 5;
    public static final float LEVEL_UP_FRAME_DURATION = 0.16f;
    public static final float LEVEL_UP_W = 64f;
    public static final float LEVEL_UP_H = 64f;
    public static final float LEVEL_UP_OFFSET_X = 0f;
    public static final float LEVEL_UP_OFFSET_Y = 28f;
    public static final float LEVEL_UP_TEMPO_VIDA = 0.8f;
    public static final boolean LEVEL_UP_REVERSE = false;

    // ── Estado ─────────────────────────────────────────────────
    private final MotorAnimacao motorAnimacao = new MotorAnimacao();
    private final List<Particula> particulas = new ArrayList<>();
    private final Map<Texture, Animation<TextureRegion>> cacheAnimacoes = new HashMap<>();

    // ── Registro / Update / Render ─────────────────────────────
    public void registrarParticula(Particula particula) {
        particulas.add(particula);
    }

    public void atualizar(float delta) {
        Iterator<Particula> it = particulas.iterator();
        while (it.hasNext()) {
            Particula p = it.next();
            p.atualizar(delta);
            if (p.isAcabou()) {
                it.remove();
            }
        }
    }

    public void desenhar(Batch batch) {
        for (Particula p : particulas) {
            p.desenhar(batch);
        }
    }

    // ── Spawns de conveniência ─────────────────────────────────
    public void spawnDano(float x, float y) {
        Particula p = new Particula(
            animacaoDe(definirSpriteDano()), motorAnimacao,
            x, y, DANO_OFFSET_X, DANO_OFFSET_Y,
            DANO_W, DANO_H, DANO_TEMPO_VIDA, DANO_REVERSE
        );
        registrarParticula(p);
    }

    public void spawnCura(float x, float y) {
        Particula p = new Particula(
            animacaoDe(definirSpriteCura()), motorAnimacao,
            x, y, CURA_OFFSET_X, CURA_OFFSET_Y,
            CURA_W, CURA_H, CURA_TEMPO_VIDA, CURA_REVERSE
        );
        registrarParticula(p);
    }

    public void spawnLevelUp(float x, float y) {
        Particula p = new Particula(
            animacaoDe(definirSpriteLevelUp()), motorAnimacao,
            x, y, LEVEL_UP_OFFSET_X, LEVEL_UP_OFFSET_Y,
            LEVEL_UP_W, LEVEL_UP_H, LEVEL_UP_TEMPO_VIDA, LEVEL_UP_REVERSE
        );
        registrarParticula(p);
    }

    // ── Definições de sprite (placeholder via Assets) ──────────
    public DefinirSprite definirSpriteDano() {
        return new DefinirSprite(
            Assets.get(SPRITE_DANO_PATH, Texture.class),
            DANO_NUM_FRAME, DANO_FRAME_DURATION, DANO_REVERSE, false
        );
    }

    public DefinirSprite definirSpriteCura() {
        return new DefinirSprite(
            Assets.get(SPRITE_CURA_PATH, Texture.class),
            CURA_NUM_FRAME, CURA_FRAME_DURATION, CURA_REVERSE, false
        );
    }

    public DefinirSprite definirSpriteLevelUp() {
        return new DefinirSprite(
            Assets.get(SPRITE_LEVEL_UP_PATH, Texture.class),
            LEVEL_UP_NUM_FRAME, LEVEL_UP_FRAME_DURATION, LEVEL_UP_REVERSE, false
        );
    }

    // ── Cache de animações (evita rebuild do spritesheet) ─────
    private Animation<TextureRegion> animacaoDe(DefinirSprite def) {
        Animation<TextureRegion> anim = cacheAnimacoes.get(def.texture());
        if (anim == null) {
            anim = motorAnimacao.criarAnimacao(def, def.reverse());
            cacheAnimacoes.put(def.texture(), anim);
        }
        return anim;
    }

    public void limpar() {
        particulas.clear();
    }
}
