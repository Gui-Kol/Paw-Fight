package com.pawfight.game.engine.input;

/**
 * Tipo lógico de uma entrada de gamepad: um botão, ou o extremo
 * positivo/negativo de um eixo (analógico). A ausência de binding é
 * representada por {@code null}, não por um valor deste enum.
 */
public enum TipoEntradaGamepad {
    BOTAO,
    EIXO_POSITIVO,
    EIXO_NEGATIVO
}
