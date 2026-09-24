package com.pawfight.game.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GameConfig")
class GameConfigTest {

    private final GameConfig config = GameConfig.getInstance();
    private Application aplicativoAnterior;
    private Preferences preferencias;
    private Map<String, Object> valores;

    @BeforeEach
    void prepararPreferencias() {
        aplicativoAnterior = Gdx.app;
        Gdx.app = mock(Application.class);
        preferencias = mock(Preferences.class);
        valores = new HashMap<>();
        when(Gdx.app.getPreferences("pawfight-config")).thenReturn(preferencias);
        when(preferencias.getFloat(anyString(), anyFloat())).thenAnswer(invocacao ->
            valores.getOrDefault(invocacao.getArgument(0), invocacao.getArgument(1)));
        when(preferencias.getBoolean(anyString(), anyBoolean())).thenAnswer(invocacao ->
            valores.getOrDefault(invocacao.getArgument(0), invocacao.getArgument(1)));
        when(preferencias.putFloat(anyString(), anyFloat())).thenAnswer(invocacao -> {
            valores.put(invocacao.getArgument(0), invocacao.getArgument(1));
            return preferencias;
        });
        when(preferencias.putBoolean(anyString(), anyBoolean())).thenAnswer(invocacao -> {
            valores.put(invocacao.getArgument(0), invocacao.getArgument(1));
            return preferencias;
        });
        config.carregarConfiguracoes();
    }

    @AfterEach
    void restaurarConfiguracoes() {
        valores.clear();
        config.carregarConfiguracoes();
        Gdx.app = aplicativoAnterior;
    }

    @Test
    void preferenciasAusentesUsamPadroes() {
        assertEquals(0.3f, config.getVolumeMusica());
        assertEquals(0.7f, config.getVolumeEfeitos());
        assertEquals(0.2f, config.getVolumePassos());
        assertTrue(config.isTelaCheia());
    }

    @Test
    void salvarECarregarPreservaConfiguracoes() {
        for (boolean telaCheia : new boolean[]{false, true}) {
            config.setVolumeMusicaPercent(15);
            config.setVolumeEfeitosPercent(85);
            config.setVolumePassosPercent(45);
            config.setTelaCheia(telaCheia);
            config.salvarConfiguracoes();
            config.setVolumeMusica(0);
            config.setVolumeEfeitos(0);
            config.setVolumePassos(0);
            config.setTelaCheia(!telaCheia);
            config.carregarConfiguracoes();
            assertEquals(0.15f, config.getVolumeMusica());
            assertEquals(0.85f, config.getVolumeEfeitos());
            assertEquals(0.45f, config.getVolumePassos());
            assertEquals(telaCheia, config.isTelaCheia());
        }
        verify(preferencias, times(2)).flush();
    }

    @Test
    void carregarLimitaVolumesPersistidos() {
        valores.put("volumeMusica", -2f);
        valores.put("volumeEfeitos", 4f);
        valores.put("volumePassos", -1f);
        config.carregarConfiguracoes();
        assertEquals(0f, config.getVolumeMusica());
        assertEquals(1f, config.getVolumeEfeitos());
        assertEquals(0f, config.getVolumePassos());
    }

    @Test
    @DisplayName("Singleton retorna sempre a mesma instância")
    void singleton() {
        assertSame(GameConfig.getInstance(), GameConfig.getInstance());
    }

    @Test
    @DisplayName("Volumes válidos são aceitos sem alteração")
    void volumeValido() {
        config.setVolumeMusica(0.5f);
        assertEquals(0.5f, config.getVolumeMusica());
    }

    @Test
    @DisplayName("Volume de música negativo é limitado a 0")
    void volumeMusicaNegativo() {
        config.setVolumeMusica(-0.5f);
        assertEquals(0f, config.getVolumeMusica());
    }

    @Test
    @DisplayName("Volume de música acima de 1 é limitado a 1")
    void volumeMusicaExcedente() {
        config.setVolumeMusica(2.5f);
        assertEquals(1f, config.getVolumeMusica());
    }

    @Test
    @DisplayName("Volume de efeitos respeita limites [0, 1]")
    void volumeEfeitosLimites() {
        config.setVolumeEfeitos(-1f);
        assertEquals(0f, config.getVolumeEfeitos());
        config.setVolumeEfeitos(5f);
        assertEquals(1f, config.getVolumeEfeitos());
    }

    @Test
    @DisplayName("Volume de passos respeita limites [0, 1]")
    void volumePassosLimites() {
        config.setVolumePassos(-0.1f);
        assertEquals(0f, config.getVolumePassos());
        config.setVolumePassos(1.1f);
        assertEquals(1f, config.getVolumePassos());
    }

    @Test
    @DisplayName("Setter em percent converte corretamente")
    void volumePercentual() {
        config.setVolumeMusicaPercent(75);
        assertEquals(0.75f, config.getVolumeMusica(), 0.001f);
    }

    @Test
    @DisplayName("Percentual fora de 0-100 também é limitado")
    void volumePercentualForaDoLimite() {
        config.setVolumeEfeitosPercent(150);
        assertEquals(1f, config.getVolumeEfeitos());
    }

    @Test
    @DisplayName("Debug mode desligado por padrão")
    void debugDesligadoPorPadrao() {
        // Assume que nenhum outro teste ligou o debug (singleton compartilhado)
        assertFalse(config.isDebugMode());
    }

    @Test
    @DisplayName("Constantes de resolução base estão corretas")
    void resolucaoBase() {
        assertEquals(1920, GameConfig.LARGURA_TELA_BASE);
        assertEquals(1080, GameConfig.ALTURA_TELA_BASE);
    }

    @Test
    @DisplayName("Escala de UI permanece positiva com janela minimizada")
    void escalaPositivaComDimensaoZero() {
        assertTrue(config.calcularEscala(0, 720) > 0f);
        assertTrue(config.calcularEscala(1280, 0) > 0f);
        assertTrue(config.calcularEscala(0, 0) > 0f);
    }

    @Test
    @DisplayName("Escala usa a menor proporção da janela")
    void escalaRespeitaProporcao() {
        assertEquals(0.5f, config.calcularEscala(960, 540), 0.0001f);
    }
}
