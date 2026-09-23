package com.pawfight.game.engine.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntradaGamepadTest {

    @Test
    void serializacaoBotaoIdaEVolta() {
        EntradaGamepad original = EntradaGamepad.botao(EntradaGamepad.BOTAO_X);
        assertEquals("BOTAO:X", original.serializar());
        assertEquals(original, EntradaGamepad.desserializar(original.serializar()));
    }

    @Test
    void serializacaoEixosIdaEVolta() {
        EntradaGamepad positivo = EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X);
        EntradaGamepad negativo = EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y);
        assertEquals("EIXO+:ANALOGICO_ESQ_X", positivo.serializar());
        assertEquals("EIXO-:ANALOGICO_ESQ_Y", negativo.serializar());
        assertEquals(positivo, EntradaGamepad.desserializar(positivo.serializar()));
        assertEquals(negativo, EntradaGamepad.desserializar(negativo.serializar()));
    }

    @Test
    void desserializarInvalidosRetornaNull() {
        assertNull(EntradaGamepad.desserializar(null));
        assertNull(EntradaGamepad.desserializar(""));
        assertNull(EntradaGamepad.desserializar("SEM_SEPARADOR"));
        assertNull(EntradaGamepad.desserializar("TIPO_INVALIDO:X"));
        assertNull(EntradaGamepad.desserializar("BOTAO:"));
    }

    @Test
    void fallbackBrutoParaEntradasSemMapping() {
        EntradaGamepad botao = EntradaGamepad.botaoBruto(7);
        EntradaGamepad eixo = EntradaGamepad.eixoBruto(3, true);
        assertEquals("BOTAO:BOTAO_7", botao.serializar());
        assertEquals("EIXO+:EIXO_3", eixo.serializar());
        assertEquals(botao, EntradaGamepad.desserializar(botao.serializar()));
        assertEquals(eixo, EntradaGamepad.desserializar(eixo.serializar()));
    }

    @Test
    void igualdadePorTipoECodigo() {
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_A), EntradaGamepad.botao(EntradaGamepad.BOTAO_A));
        assertNotEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_A), EntradaGamepad.botao(EntradaGamepad.BOTAO_B));
        assertNotEquals(
            EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X),
            EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X));
    }
}
