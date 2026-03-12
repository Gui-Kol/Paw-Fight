package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.entity.bosses.BossesTemplate;
import com.pawfight.game.entity.enemy.EnemyTemplate;

import java.util.ArrayList;
import java.util.List;

public class GerarInimigos {
    private List<EnemyTemplate> listaInimigosFortes;
    private List<BossesTemplate> listaBosses;

    public GerarInimigos() {
        listaInimigosFortes = new ArrayList<>();
        listaBosses = new ArrayList<>();
    }

    public List<EnemyTemplate> inimigos(EnemyTemplate inimigoBase, int qntMax, int qntMin, List<Rectangle> regioesSpawn) {
        List<EnemyTemplate> novosInimigos = new ArrayList<>();

        int quantidade = qntMin + (int)(Math.random() * (qntMax - qntMin + 1));

        while (novosInimigos.size() < quantidade) {
            // Escolhe uma região aleatória da lista
            Rectangle regiao = regioesSpawn.get((int)(Math.random() * regioesSpawn.size()));

            int x = (int)(regiao.x + Math.random() * regiao.width);
            int y = (int)(regiao.y + Math.random() * regiao.height);

            EnemyTemplate novoInimigo = inimigoBase.cloneEnemy();
            novoInimigo.setLocation(x, y);

            novosInimigos.add(novoInimigo);
        }


        clear();
        return novosInimigos;
    }


    public List<EnemyTemplate> inimigosFortes(){
        return listaInimigosFortes;
    }
    public List<BossesTemplate> bosses(){
        return listaBosses;
    }

    public void clear(){
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                listaInimigosFortes.clear();
                listaBosses.clear();
            }
        },1.5f);
    }

}
