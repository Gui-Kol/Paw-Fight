package com.pawfight.game.engine.fisica;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChecarColisao")
class ChecarColisaoTest {

    private static final Rectangle PAREDE_DIREITA = new Rectangle(50, 0, 10, 100);

    // ── houveColisao ───────────────────────────────────────────

    @Test
    @DisplayName("Detecta sobreposição com parede")
    void detectaColisao() {
        Rectangle hitbox = new Rectangle(45, 10, 10, 10);
        assertTrue(ChecarColisao.houveColisao(hitbox, List.of(PAREDE_DIREITA)));
    }

    @Test
    @DisplayName("Sem sobreposição retorna false")
    void semColisao() {
        Rectangle hitbox = new Rectangle(0, 0, 10, 10);
        assertFalse(ChecarColisao.houveColisao(hitbox, List.of(PAREDE_DIREITA)));
    }

    @Test
    @DisplayName("Lista vazia nunca colide")
    void listaVazia() {
        assertFalse(ChecarColisao.houveColisao(new Rectangle(0, 0, 10, 10), List.of()));
    }

    // ── empurrarForaParedes ────────────────────────────────────

    @Test
    @DisplayName("Hitbox dentro da parede é empurrada para fora (eixo de menor overlap)")
    void empurraParaFora() {
        Rectangle hitbox = new Rectangle(45, 10, 10, 10); // overlap em X com a parede
        ChecarColisao.empurrarForaParedes(hitbox, List.of(PAREDE_DIREITA));
        assertFalse(hitbox.overlaps(PAREDE_DIREITA),
            "Hitbox deveria estar completamente fora da parede");
    }

    @Test
    @DisplayName("Hitbox mais perto da esquerda é empurrada para a esquerda")
    void empurraParaEsquerda() {
        Rectangle hitbox = new Rectangle(48, 10, 4, 10); // centro à esquerda do centro da parede
        ChecarColisao.empurrarForaParedes(hitbox, List.of(PAREDE_DIREITA));
        assertTrue(hitbox.x + hitbox.width <= PAREDE_DIREITA.x,
            "Deveria ser empurrada para a esquerda da parede");
    }

    @Test
    @DisplayName("Hitbox sem sobreposição permanece imóvel")
    void naoMoveQuandoSemSobreposicao() {
        Rectangle hitbox = new Rectangle(0, 0, 10, 10);
        ChecarColisao.empurrarForaParedes(hitbox, List.of(PAREDE_DIREITA));
        assertEquals(new Rectangle(0, 0, 10, 10), hitbox);
    }

    @Test
    @DisplayName("Resolve canto com múltiplas paredes")
    void cantoComDuasParedes() {
        Rectangle paredeHorizontal = new Rectangle(0, 50, 100, 10);
        Rectangle hitbox = new Rectangle(55, 45, 10, 10); // no canto entre as duas
        ChecarColisao.empurrarForaParedes(hitbox, List.of(PAREDE_DIREITA, paredeHorizontal));
        assertFalse(hitbox.overlaps(PAREDE_DIREITA));
        assertFalse(hitbox.overlaps(paredeHorizontal));
    }
}
