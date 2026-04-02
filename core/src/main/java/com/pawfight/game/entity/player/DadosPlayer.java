package com.pawfight.game.entity.player;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

public record DadosPlayer(
    int forca,
    float cadenciaTiro,
    float duracaoTiro,
    int vidaBase,
    int velocidade,
    int tamanho,
    int tamanhoTiro,
    int hitboxSize,
    int hitboxOffsetY,
    int hitboxOffsetX,
    Texture idleSheet,
    Texture walkSheet,
    Texture deadSheet,
    Texture hurtSheet,
    Music audioMoving
) {
}
