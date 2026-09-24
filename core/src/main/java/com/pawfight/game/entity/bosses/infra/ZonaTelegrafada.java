package com.pawfight.game.entity.bosses.infra;

import com.badlogic.gdx.math.Rectangle;

// Zona telegrafada: ponto capturado no início do aviso; a aplicação consulta a zona fixa, não o alvo atual.
public final class ZonaTelegrafada {

    private float x;
    private float y;

    // Fixa o ponto da zona (normalmente a posição do alvo no momento do aviso).
    public void marcar(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Distância euclidiana ao ponto marcado dentro do raio (comparação ao quadrado: sem raiz).
    public boolean contemPonto(float pontoX, float pontoY, float raio) {
        float deltaX = pontoX - x;
        float deltaY = pontoY - y;
        return deltaX * deltaX + deltaY * deltaY <= raio * raio;
    }

    // True quando o centro da hitbox está dentro do raio da zona.
    public boolean contemCentro(Rectangle hitbox, float raio) {
        return contemPonto(hitbox.x + hitbox.width / 2f, hitbox.y + hitbox.height / 2f, raio);
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
