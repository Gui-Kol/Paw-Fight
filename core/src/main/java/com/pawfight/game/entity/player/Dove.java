package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.dove.Coco;

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
            new Texture("entitys/player/dove/Idle.png"),
            new Texture("entitys/player/dove/Walk.png"),
            new Texture("entitys/player/dove/Death.png"),
            new Texture("entitys/player/dove/Hurt.png"),
            audioEngine.criarAudio("entitys/player/audios/asas_passaro.wav")
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
