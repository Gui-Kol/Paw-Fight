package com.pawfight.game.engine.fisica;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ColisaoResolver")
class ColisaoResolverTest {

    private EnemyTemplate mockInimigo(Rectangle hitbox, boolean morto) {
        EnemyTemplate inimigo = mock(EnemyTemplate.class);
        when(inimigo.getHitBox()).thenReturn(hitbox);
        when(inimigo.isMorto()).thenReturn(morto);
        return inimigo;
    }

    @Test
    @DisplayName("Inimigos sobrepostos são separados")
    void separaInimigosSobrepostos() {
        Rectangle a = new Rectangle(10, 10, 10, 10);
        Rectangle b = new Rectangle(15, 10, 10, 10); // overlap de 5 em X
        EnemyTemplate ea = mockInimigo(a, false);
        EnemyTemplate eb = mockInimigo(b, false);

        new ColisaoResolver().resolver(List.of(ea, eb), List.of());

        assertFalse(a.overlaps(b), "Hitboxes deveriam estar separadas após resolver");
        verify(ea).sincronizarPosicaoComHitbox();
        verify(eb).sincronizarPosicaoComHitbox();
    }

    @Test
    @DisplayName("Inimigos separados permanecem intactos")
    void naoAlteraInimigosSeparados() {
        Rectangle a = new Rectangle(0, 0, 10, 10);
        Rectangle b = new Rectangle(100, 100, 10, 10);
        Rectangle aOriginal = new Rectangle(a);
        Rectangle bOriginal = new Rectangle(b);

        new ColisaoResolver().resolver(
            List.of(mockInimigo(a, false), mockInimigo(b, false)), List.of());

        assertEquals(aOriginal, a);
        assertEquals(bOriginal, b);
    }

    @Test
    @DisplayName("Inimigo morto não é movido nem considerado")
    void ignoraMortos() {
        Rectangle morto = new Rectangle(10, 10, 10, 10);
        Rectangle vivo = new Rectangle(15, 10, 10, 10);
        Rectangle mortoOriginal = new Rectangle(morto);

        new ColisaoResolver().resolver(
            List.of(mockInimigo(morto, true), mockInimigo(vivo, false)), List.of());

        assertEquals(mortoOriginal, morto, "Morto não deveria ser movido");
    }

    @Test
    @DisplayName("Inimigo dentro de parede é empurrado para fora")
    void empurraDeParede() {
        Rectangle parede = new Rectangle(50, 0, 10, 100);
        Rectangle inimigoRect = new Rectangle(48, 10, 8, 10);
        EnemyTemplate inimigo = mockInimigo(inimigoRect, false);

        new ColisaoResolver().resolver(List.of(inimigo), List.of(parede));

        assertFalse(inimigoRect.overlaps(parede),
            "Inimigo deveria estar fora da parede");
        verify(inimigo).sincronizarPosicaoComHitbox();
    }

    @Test
    @DisplayName("Lista de inimigos nula não lança exceção")
    void listaNula() {
        assertDoesNotThrow(() -> new ColisaoResolver().resolver(null, List.of()));
    }

    @Test
    @DisplayName("Lista de paredes nula resolve apenas inimigo-vs-inimigo")
    void paredesNulas() {
        Rectangle a = new Rectangle(10, 10, 10, 10);
        Rectangle b = new Rectangle(15, 10, 10, 10);
        assertDoesNotThrow(() -> new ColisaoResolver().resolver(
            List.of(mockInimigo(a, false), mockInimigo(b, false)), null));
        assertFalse(a.overlaps(b));
    }

    @Test
    @DisplayName("Cluster de inimigos em cadeia é resolvido dentro do limite de iterações")
    void clusterDeInimigos() {
        // O resolver faz no máx. 4 iterações; 3 inimigos com overlap < metade do tamanho é o caso realista garantido.
        List<EnemyTemplate> inimigos = new ArrayList<>();
        List<Rectangle> hitboxes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Rectangle hitbox = new Rectangle(10 + i * 7, 10, 10, 10); // overlap de apenas 3px por par
            hitboxes.add(hitbox);
            inimigos.add(mockInimigo(hitbox, false));
        }

        new ColisaoResolver().resolver(inimigos, List.of());

        for (int i = 0; i < hitboxes.size(); i++) {
            for (int j = i + 1; j < hitboxes.size(); j++) {
                assertFalse(hitboxes.get(i).overlaps(hitboxes.get(j)),
                    "Inimigos " + i + " e " + j + " deveriam estar separados");
            }
        }
    }
}
