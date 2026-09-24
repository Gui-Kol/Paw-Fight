package com.pawfight.game.content.player;

public record ContextoCriacaoPlayer(
    int x,
    int y,
    int larguraTile,
    int quantidadeTilesX,
    int alturaTile,
    int quantidadeTilesY,
    float zoomCamera
) { }
