package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.Assets;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.dove.Coco;

public class BlackBird extends PlayerTemplate {
    public BlackBird(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);
    }

    @Override
    public DadosPlayer dadosPlayer() {
        return new DadosPlayer(
            2,      // forca
            1f,     // cadenciaTiro
            1f,     // duracaoTiro
            8,      // vidaBase
            500,    // velocidade
            32,     // tamanho
            0,      // tamanhoTiro
            20,     // hitboxSize
            0,      // hitboxOffsetY
            -5,     // hitboxOffsetX
            Assets.get("entitys/player/black_bird/Idle.png", Texture.class),
            Assets.get("entitys/player/black_bird/Walk.png", Texture.class),
            Assets.get("entitys/player/black_bird/Death.png", Texture.class),
            Assets.get("entitys/player/black_bird/Hurt.png", Texture.class),
            audio.getAudioEngine().criarAudio("entitys/player/audios/asas_passaro.wav")
        );
    }

    @Override
    protected void definirAudios() {
    }

    @Override
    protected TirosTemplate modeloTiroExclusivo() {
        return new Coco(stats.getTamanhoTiro(), this);
    }

    @Override
    public void updateSpriteDefinitions() {
        animacao.setDefinitions(
            new DefinirSprite(animacao.getIdleSheet(), 4, 0.1f, false, olhandoEsquerda),
            new DefinirSprite(animacao.getWalkSheet(), 6, 0.1f, false, olhandoEsquerda),
            new DefinirSprite(animacao.getDeadSheet(), 4, 0.1f, false, olhandoEsquerda),
            new DefinirSprite(animacao.getHurtSheet(), 2, 0.1f, false, olhandoEsquerda)
        );
    }

    @Override
    public String getName() {
        return "Black Bird";
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
