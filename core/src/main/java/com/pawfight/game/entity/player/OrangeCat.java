package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.orangecat.TiroSangue;

public class OrangeCat extends PlayerTemplate {
    public OrangeCat(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);
    }

    @Override
    public DadosPlayer dadosPlayer() {
        return new DadosPlayer(
            1,      // forca
            1f,     // cadenciaTiro
            1f,     // duracaoTiro
            13,     // vidaBase
            300,    // velocidade
            56,     // tamanho
            0,      // tamanhoTiro
            25,     // hitboxSize
            0,      // hitboxOffsetY
            -5,     // hitboxOffsetX
            Assets.get("entitys/player/orange_cat/Idle.png", Texture.class),
            Assets.get("entitys/player/orange_cat/Walk.png", Texture.class),
            Assets.get("entitys/player/orange_cat/Death.png", Texture.class),
            Assets.get("entitys/player/orange_cat/Hurt.png", Texture.class),
            audio.getAudioEngine().criarAudio("entitys/player/audios/passos.wav")
        );
    }

    @Override
    protected void definirAudios() {
    }

    @Override
    protected TirosTemplate modeloTiroExclusivo() {
        return new TiroSangue(stats.getTamanhoTiro(), this);
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
        return "Orange Cat";
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
