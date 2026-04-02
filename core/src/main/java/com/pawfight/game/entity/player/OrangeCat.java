package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.dove.Coco;

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
            new Texture("entitys/player/orange_cat/Idle.png"),
            new Texture("entitys/player/orange_cat/Walk.png"),
            new Texture("entitys/player/orange_cat/Death.png"),
            new Texture("entitys/player/orange_cat/Hurt.png"),
            audioEngine.criarAudio("entitys/player/audios/passos.wav")
        );
    }

    @Override
    protected void definirAudios() {
    }

    @Override
    protected TirosTemplate modeloTiroExclusivo() {
        return new Coco(tamanhoTiro, this);
    }

    @Override
    public void updateSpriteDefinitions() {
        idleDefinition = new DefinirSprite(idleSheet, 4, 0.1f, false, olhandoEsquerda);
        walkDefinition = new DefinirSprite(walkSheet, 6, 0.1f, false, olhandoEsquerda);
        deadDefinition = new DefinirSprite(deadSheet, 4, 0.1f, false, olhandoEsquerda);
        hurtDefinition = new DefinirSprite(hurtSheet, 2, 0.1f, false, olhandoEsquerda);
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
