package com.pawfight.game.engine.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.pawfight.game.engine.procedural.ObjetoGerado;

import java.util.List;

public class DrawList {

    public void drawObjects(List<ObjetoGerado> listaObjetos, SpriteBatch batch, Matrix4 cameraMatrix, int altura, int largura) {
        if (listaObjetos == null || listaObjetos.isEmpty()) {
            return;
        }
        batch.setProjectionMatrix(cameraMatrix);
        batch.begin();
        for (ObjetoGerado objeto : listaObjetos) {
            batch.draw(objeto.textura, objeto.x, objeto.y,
                altura, largura);
        }
        batch.end();
    }
}
