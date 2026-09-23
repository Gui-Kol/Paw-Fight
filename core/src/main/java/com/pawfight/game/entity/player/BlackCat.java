package com.pawfight.game.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.entity.tiro.TirosTemplate;
import com.pawfight.game.entity.tiro.blackcat.TiroFantasmagorico;

public class BlackCat extends PlayerTemplate {
    public BlackCat(int dx, int dy, int tileWidth, int numTilesX, int tileHeight, int numTilesY, float zoomCamera) {
        super(dx, dy, tileWidth, numTilesX, tileHeight, numTilesY, zoomCamera);
    }

    @Override
    public DadosPlayer dadosPlayer() {
        return new DadosPlayer(
            2,      // forca
            1f,     // cadenciaTiro
            1f,     // duracaoTiro
            10,     // vidaBase
            350,    // velocidade
            64,     // tamanho
            0,      // tamanhoTiro
            25,     // hitboxSize
            0,      // hitboxOffsetY
            -5,     // hitboxOffsetX
            Assets.get("entitys/player/black_cat/Idle.png", Texture.class),
            Assets.get("entitys/player/black_cat/Walk.png", Texture.class),
            Assets.get("entitys/player/black_cat/Death.png", Texture.class),
            Assets.get("entitys/player/black_cat/Hurt.png", Texture.class),
            audio.getAudioEngine().criarAudio("entitys/player/audios/passos.wav")
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
        return new TiroFantasmagorico(stats.getTamanhoTiro(), this);
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
        return "Black Cat";
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
