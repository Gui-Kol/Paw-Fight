package com.pawfight.game.entity.bosses.infra;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ZonaTelegrafada")
class ZonaTelegrafadaTest {

    @Test
    @DisplayName("marcar fixa o ponto consultado pelas checagens")
    void marcarFixaPonto() {
        ZonaTelegrafada zona = new ZonaTelegrafada();

        zona.marcar(300f, 200f);

        assertEquals(300f, zona.getX());
        assertEquals(200f, zona.getY());
    }

    @Test
    @DisplayName("contemPonto respeita o raio inclusive na borda")
    void contemPontoRespeitaRaio() {
        ZonaTelegrafada zona = new ZonaTelegrafada();
        zona.marcar(100f, 100f);

        assertTrue(zona.contemPonto(100f, 100f, 50f));
        assertTrue(zona.contemPonto(130f, 140f, 50f));   // 30²+40² = 50² (borda)
        assertFalse(zona.contemPonto(131f, 140f, 50f));
    }

    @Test
    @DisplayName("contemCentro avalia o centro da hitbox")
    void contemCentroDaHitbox() {
        ZonaTelegrafada zona = new ZonaTelegrafada();
        zona.marcar(215f, 115f);

        assertTrue(zona.contemCentro(new Rectangle(200, 100, 30, 30), 150f));
        assertFalse(zona.contemCentro(new Rectangle(700, 700, 30, 30), 150f));
    }

    @Test
    @DisplayName("Nova marcação descarta o ponto anterior")
    void remarcarSubstitui() {
        ZonaTelegrafada zona = new ZonaTelegrafada();
        zona.marcar(0f, 0f);

        zona.marcar(500f, 500f);

        assertFalse(zona.contemPonto(0f, 0f, 10f));
        assertTrue(zona.contemPonto(500f, 500f, 10f));
    }
}
