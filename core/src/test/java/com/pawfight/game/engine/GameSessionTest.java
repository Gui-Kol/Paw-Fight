package com.pawfight.game.engine;

import com.pawfight.game.entity.player.PlayerTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("GameSession — ownership da run")
class GameSessionTest {

    @Test
    @DisplayName("Player compartilhado é descartado exatamente uma vez ao encerrar a sessão")
    void descartaPlayerUmaVez() {
        GameSession session = new GameSession(123L);
        PlayerTemplate player = mock(PlayerTemplate.class);
        session.definirPlayer(player);

        session.encerrar();
        session.encerrar();

        verify(player, times(1)).dispose();
        assertNull(session.getPlayer());
    }

    @Test
    @DisplayName("Trocar o Player descarta o anterior sem descartar o novo")
    void substituiPlayerComOwnershipExplicito() {
        GameSession session = new GameSession(123L);
        PlayerTemplate anterior = mock(PlayerTemplate.class);
        PlayerTemplate novo = mock(PlayerTemplate.class);
        session.definirPlayer(anterior);

        session.definirPlayer(novo);

        verify(anterior, times(1)).dispose();
        verify(novo, times(0)).dispose();
    }
}
