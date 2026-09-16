package com.pawfight.game.world.portal;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.template.WorldTemplate;

/**
 * Interface funcional responsável por criar o {@link WorldTemplate} de destino
 * de um {@link Portal}. Mantém o sistema abstrato e reutilizável: cada novo
 * mundo pode ser registrado sem reescrever a lógica do portal.
 */
@FunctionalInterface
public interface WorldFactory {

    /**
     * Cria o mundo de destino com o contexto necessário (jogo, jogador, câmera e viewport).
     *
     * @param game    jogo principal
     * @param player  jogador que será transportado
     * @param camera  câmera do mundo
     * @param viewport viewport do mundo
     * @return mundo de destino instanciado
     */
    WorldTemplate criar(PawFight game, PlayerTemplate player, OrthographicCamera camera, Viewport viewport);
}