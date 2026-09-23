package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.fisica.TilemapHitboxFactory;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.entity.bosses.BossTemplate;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GerarInimigos {
    private final List<EnemyTemplate> listaInimigosFortes;
    private final List<BossTemplate> listaBosses;
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
            return novosInimigos;
        }

        // Garante que qntMin <= qntMax para evitar bound negativo
        if (qntMin > qntMax) {
            int temp = qntMin;
            qntMin = qntMax;
            qntMax = temp;
        }

        int quantidade = qntMin + random.nextInt(qntMax - qntMin + 1);

        while (novosInimigos.size() < quantidade) {
            EnemyTemplate modelo = modelosDisponiveis.get(random.nextInt(modelosDisponiveis.size()));

            // Tamanho do sprite do inimigo (para não ultrapassar a área)
            int tamanho = modelo.getTamanho();

            Rectangle regiao = regioesSpawn.get(random.nextInt(regioesSpawn.size()));

            // Garante que a área de spawn é grande o suficiente para caber o inimigo
            float spawnWidth = Math.max(0, regiao.width - tamanho);
            float spawnHeight = Math.max(0, regiao.height - tamanho);

            int x = (int) (regiao.x + random.nextFloat() * spawnWidth);
            int y = (int) (regiao.y + random.nextFloat() * spawnHeight);

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
        Sala currentRoom = world.getRoomManager().getCurrentRoom();
        TiledMap map = world.getMap();
        TilemapHitboxFactory tilemapHitboxFactory = world.getWorldPhysics().getTilemapHitboxFactory();
        String nomeClasseOrigem = world.getWorldName();
        GerarInimigos gerarInimigos = world.getEnemyManager().getGerarInimigos();

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
                inimigosGerados.addAll(gerarInimigos.inimigos(listarModeloEnemy, false, 50, 10, regiaoSpawn));
            }
            case INIMIGOS_FORTES -> {
                inimigosGerados.addAll(gerarInimigos.inimigos(listarModeloEnemy, true, 7, 3, regiaoSpawn));
            }
            case BOSS -> {
                inimigosGerados.addAll(gerarInimigos.inimigos(world.getBossesModelo(), false, 1, 1, regiaoSpawn));
            }
        }
        return inimigosGerados;
    }

    public List<EnemyTemplate> getInimigosFortes() {
        return listaInimigosFortes;
    }

    public List<BossTemplate> getBosses() {
        return listaBosses;
    }

    private void clear() {
        listaInimigosFortes.clear();
        listaBosses.clear();
    }
}
