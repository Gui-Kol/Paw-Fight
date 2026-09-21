package com.pawfight.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Preferences;
import com.pawfight.game.engine.GameConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelaCheiaTest {
    private Application aplicativoAnterior;
    private Graphics graficosAnteriores;
    private boolean preferenciaAnterior;
    private Preferences preferencias;
    private final PawFight jogo = new PawFight();

    @BeforeEach
    void preparar() {
        aplicativoAnterior = Gdx.app;
        graficosAnteriores = Gdx.graphics;
        preferenciaAnterior = GameConfig.getInstance().isTelaCheia();
        Gdx.app = mock(Application.class);
        Gdx.graphics = mock(Graphics.class);
        preferencias = mock(Preferences.class);
        when(Gdx.app.getPreferences("pawfight-config")).thenReturn(preferencias);
    }

    @AfterEach
    void restaurar() {
        Gdx.app = aplicativoAnterior;
        Gdx.graphics = graficosAnteriores;
        GameConfig.getInstance().setTelaCheia(preferenciaAnterior);
    }

    @Test
    void estadoAtualNaoTrocaJanelaNovamente() {
        when(Gdx.graphics.isFullscreen()).thenReturn(true);
        jogo.definirTelaCheia(true);
        verify(Gdx.graphics, never()).setFullscreenMode(any());
        verify(Gdx.graphics, never()).setWindowedMode(anyInt(), anyInt());
        assertTrue(GameConfig.getInstance().isTelaCheia());
        verify(preferencias).putBoolean("telaCheia", true);
        verify(preferencias).flush();
    }

    @Test
    void alternanciaUsaEstadoRealEVoltaPara1280Por720() {
        GameConfig.getInstance().setTelaCheia(false);
        when(Gdx.graphics.isFullscreen()).thenReturn(true, true, false);
        jogo.toggleFullscreen();
        verify(Gdx.graphics).setUndecorated(false);
        verify(Gdx.graphics).setWindowedMode(1280, 720);
        assertFalse(GameConfig.getInstance().isTelaCheia());
        verify(preferencias).putBoolean("telaCheia", false);
    }

    @Test
    void falhaNaTrocaPreservaEstadoReal() {
        when(Gdx.graphics.isFullscreen()).thenReturn(false);
        jogo.definirTelaCheia(true);
        verify(Gdx.graphics).setFullscreenMode(null);
        assertFalse(GameConfig.getInstance().isTelaCheia());
        verify(preferencias).putBoolean("telaCheia", false);
    }
}
