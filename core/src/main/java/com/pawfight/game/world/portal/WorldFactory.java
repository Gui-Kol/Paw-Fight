package com.pawfight.game.world.portal;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.template.WorldTemplate;

// Fábrica funcional que cria o WorldTemplate de destino de um Portal, mantendo o sistema reutilizável
@FunctionalInterface
public interface WorldFactory {

    // Cria o mundo de destino com o contexto necessário (jogo, jogador, câmera e viewport)
    WorldTemplate criar(PawFight game, PlayerTemplate player, OrthographicCamera camera, Viewport viewport);
}
