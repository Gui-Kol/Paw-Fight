package com.pawfight.game.world.template;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("WorldRenderer — somente apresentação")
class WorldRendererTest {

    @Test
    @DisplayName("Renderizar inimigos não atualiza IA nem física")
    void renderizarInimigosNaoModificaGameplay() {
        ShapeRenderer shapeRenderer = mock(ShapeRenderer.class);
        Renderizar renderizar = mock(Renderizar.class);
        WorldRenderer renderer = new WorldRenderer(shapeRenderer, mock(Texture.class), renderizar);
        WorldTemplate world = mock(WorldTemplate.class);
        EnemyManager enemyManager = mock(EnemyManager.class);
        WorldPhysics worldPhysics = mock(WorldPhysics.class);

        when(world.getEnemyManager()).thenReturn(enemyManager);
        when(world.getWorldPhysics()).thenReturn(worldPhysics);
        when(enemyManager.getListaInimigos()).thenReturn(List.of(mock(EnemyTemplate.class)));

        renderer.renderizarInimigos(world);

        verify(renderizar).renderizarInimigos(world);
        verify(enemyManager, never()).atualizarInimigos(org.mockito.ArgumentMatchers.anyFloat());
        verify(worldPhysics, never()).resolverColisoes(org.mockito.ArgumentMatchers.anyList());
    }
}
