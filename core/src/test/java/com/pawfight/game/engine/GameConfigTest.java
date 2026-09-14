package com.pawfight.game.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameConfig")
class GameConfigTest {

    private final GameConfig config = GameConfig.getInstance();

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
}
