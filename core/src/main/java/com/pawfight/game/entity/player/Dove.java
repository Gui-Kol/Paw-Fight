package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.dove.TiroCoco;
import com.pawfight.game.entity.tiro.dove.TiroSolar;

public class Dove extends PlayerTemplate {

    public Dove(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);
    }

    @Override
    public DadosPlayer dadosPlayer() {
        return new DadosPlayer(
            1,      // forca
            1f,     // cadenciaTiro
            1f,     // duracaoTiro
            6,      // vidaBase
            600,    // velocidade
            32,     // tamanho
            0,      // tamanhoTiro
            15,     // hitboxSize
            0,      // hitboxOffsetY
            -5,     // hitboxOffsetX
            Assets.get("entitys/player/dove/Idle.png", Texture.class),
            Assets.get("entitys/player/dove/Walk.png", Texture.class),
            Assets.get("entitys/player/dove/Death.png", Texture.class),
            Assets.get("entitys/player/dove/Hurt.png", Texture.class),
            audio.getAudioEngine().criarAudio("entitys/player/audios/asas_passaro.wav")
        );
    }

    @Override
    protected void definirAudios() {
    }

    @Override
    protected void definirTamanhoSprite() {
    }

    @Override
    protected TirosTemplate modeloTiroExclusivo() {
        return new TiroSolar(stats.getTamanhoTiro(), this);
    }

    @Override
    public void updateSpriteDefinitions() {
        animacao.setDefinitions(
            new DefinirSprite(animacao.getIdleSheet(), 4, 0.1f, false, olhandoEsquerda),
            new DefinirSprite(animacao.getWalkSheet(), 4, 0.1f, false, olhandoEsquerda),
            new DefinirSprite(animacao.getDeadSheet(), 8, 0.1f, false, olhandoEsquerda),
            new DefinirSprite(animacao.getHurtSheet(), 3, 0.1f, false, olhandoEsquerda)
        );
    }

    @Override
    public String getName() {
        return "Dove";
    }

    @Override
    public void ataqueBasico(float delta) {
    }

    @Override
    public void ataqueEspecial() {
    }

    @Override
    public void usarHabilidadeEspecial() {
    }
}
