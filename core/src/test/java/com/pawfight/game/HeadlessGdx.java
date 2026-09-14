package com.pawfight.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import static org.mockito.Mockito.mock;

/**
 * Fornece um Gdx.app mínimo para os testes.
 * Necessário porque algumas classes chamam Gdx.app.log() (ex.: StatsComponent.xpUp).
 * Usa um mock de Application para evitar carregar nativos DLL (backend headless).
 * Chame ensureGdx() no @BeforeAll das classes de teste que precisam do Gdx.
 */
public final class HeadlessGdx {

    private HeadlessGdx() {
    }

    public static synchronized void ensureGdx() {
        if (Gdx.app == null) {
            Gdx.app = mock(Application.class);
        }
    }
}
