package com.pawfight.game.content.enemy;

import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;
import java.util.Random;

public record ContextoSpawn(
    PlayerTemplate player,
    int x,
    int y,
    boolean forte,
    int dificuldade,
    List<Rectangle> paredes,
    List<EnemyTemplate> entidades,
    Random random
) { }
