package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.entity.bosses.BossesTemplate;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GerarInimigos {
    private final List<EnemyTemplate> listaInimigosFortes;
    private final List<BossesTemplate> listaBosses;
    private final Random random;

    public GerarInimigos() {
        listaInimigosFortes = new ArrayList<>();
        listaBosses = new ArrayList<>();
        random = new Random();
    }

    private List<EnemyTemplate> inimigos(List<EnemyTemplate> modelosDisponiveis, boolean forte,
                                        int qntMax, int qntMin, List<Rectangle> regioesSpawn) {
        List<EnemyTemplate> novosInimigos = new ArrayList<>();

        if (modelosDisponiveis == null || modelosDisponiveis.isEmpty() || regioesSpawn == null || regioesSpawn.isEmpty()) {
            return novosInimigos; // retorna vazio se não há modelos ou regiões
        }

        int quantidade = qntMin + random.nextInt(qntMax - qntMin + 1);

        while (novosInimigos.size() < quantidade) {
            // Escolhe modelo aleatório
            EnemyTemplate modelo = modelosDisponiveis.get(random.nextInt(modelosDisponiveis.size()));

            // Escolhe região aleatória
            Rectangle regiao = regioesSpawn.get(random.nextInt(regioesSpawn.size()));
            int x = (int) (regiao.x + random.nextFloat() * regiao.width);
            int y = (int) (regiao.y + random.nextFloat() * regiao.height);

            // Clona inimigo base e posiciona
            EnemyTemplate novoInimigo = modelo.cloneEnemy();
            novoInimigo.setLocation(x, y);
            if (forte) {
                novoInimigo.setForte(true);
                listaInimigosFortes.add(novoInimigo);
            }

            novosInimigos.add(novoInimigo);
        }

        clear();
        return novosInimigos;
    }

    public List<EnemyTemplate> gerarInimigos(WorldTemplate world) {
        Sala currentRoom = world.getCurrentRoom();
        TiledMap map = world.getMap();
        TilemapHitboxFactory tilemapHitboxFactory = world.getTilemapHitboxFactory();
        String nomeClasseOrigem = world.getWorldName();
        GerarInimigos gerarInimigos = world.getGerarInimigos();

        if (currentRoom == null) {
            Gdx.app.error(nomeClasseOrigem, "gerarInimigos() chamado mas currentRoom é null!");
            return new ArrayList<>();
        }

        List<Rectangle> regiaoSpawn = tilemapHitboxFactory.createHitboxes(map, "Inimigos");
        if (regiaoSpawn == null) {
            Gdx.app.error(nomeClasseOrigem, "não existe região para gerar inimigos!");
            return null;
        }
        List<EnemyTemplate> inimigosGerados = new ArrayList<>();

        List<EnemyTemplate> listarModeloEnemy = world.getInimigosModelo();
        switch (currentRoom.getType()) {
            case INIMIGOS -> {
                inimigosGerados.addAll(gerarInimigos.inimigos(listarModeloEnemy, false, 10, 5, regiaoSpawn));
            }
            case INIMIGOS_FORTES -> {
                inimigosGerados.addAll(gerarInimigos.inimigos(listarModeloEnemy, true, 7, 3, regiaoSpawn));
            }
        }
        return inimigosGerados;
    }

    public List<EnemyTemplate> getInimigosFortes() {
        return listaInimigosFortes;
    }

    public List<BossesTemplate> getBosses() {
        return listaBosses;
    }

    private void clear() {
        listaInimigosFortes.clear();
        listaBosses.clear();
    }
}
