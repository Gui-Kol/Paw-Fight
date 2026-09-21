package com.pawfight.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import static org.mockito.Mockito.mock;

// Mock mínimo de Gdx.app (sem backend nativo) porque classes como StatsComponent chamam Gdx.app.log(); usar ensureGdx() no @BeforeAll.
public final class HeadlessGdx {

    private HeadlessGdx() {
    }

    public static synchronized void ensureGdx() {
        if (Gdx.app == null) {
            Gdx.app = mock(Application.class);
        }
    }
}
