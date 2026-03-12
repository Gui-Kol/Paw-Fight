package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GerarObjetos {
    public GerarObjetos() {
    }

    public List<ObjetoGerado> gerar(Texture textureObjeto,
                                    int qntMax, int qntMin, List<Rectangle> regioesSpawn,
                                    int ajusteAltura, int ajusteLargura,
                                    int ajusteX, int ajusteY) {
        List<ObjetoGerado> novosObjetos = new ArrayList<>();

        int quantidade = qntMin + (int) (Math.random() * (qntMax - qntMin + 1));

        while (novosObjetos.size() < quantidade) {
            // Escolhe uma região aleatória da lista
            Rectangle regiao = regioesSpawn.get((int) (Math.random() * regioesSpawn.size()));

            int x = (int)(regiao.x + Math.random() * regiao.width);
            int y = (int) (regiao.y + Math.random() * regiao.height);

            // Cria uma nova hitbox baseada na posição
            Rectangle novaHitbox = new Rectangle(x + ajusteX, y + ajusteY, textureObjeto.getWidth() + ajusteLargura, textureObjeto.getHeight() + ajusteAltura);

            ObjetoGerado obj = new ObjetoGerado(textureObjeto, novaHitbox, x, y);
            novosObjetos.add(obj);
        }

        return novosObjetos;
    }

}
