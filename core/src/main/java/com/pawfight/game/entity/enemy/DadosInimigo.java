package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

public record DadosInimigo(
    String nome,
    int vidaBase,
    int forca,
    int velocidade,
    int tamanho,
    int hitboxSize,
    int hitboxOffsetY,
    int hitboxOffsetX,
    Texture idleSheet,
    Texture walkSheet,
    Texture deadSheet,
    Texture hurtSheet,
    Texture atackSheet,
    Texture specialAtackSheet,
    float multiplicador,
    Music audioDano,
    Music audioMorte
) {
}
