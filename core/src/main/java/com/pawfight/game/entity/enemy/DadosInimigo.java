package com.pawfight.game.entity.enemy;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;

public record DadosInimigo(
    String nome,
    int VidaBase,
    int Forca,
    int Velocidade,
    int Tamanho,
    int HitboxSize,
    int HitboxOffsetY,
    int HitboxOffsetX,
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
