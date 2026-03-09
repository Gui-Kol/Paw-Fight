package com.pawfight.game.commun.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.commun.font.FontFactory;
import com.pawfight.game.world.mundo_areia.Room;
import com.pawfight.game.world.mundo_areia.RoomType;

import java.util.Map;
import java.util.Set;

import static com.pawfight.game.commun.CommunVariable.HITBOX_ISVISIBLE;

public class DesenharMiniMapa {
    private static final int SEPARACAO = 30; // distância entre quadrados no minimapa
    private static final int AJUSTE = 15; // ajuste altura da linha
    private GlyphLayout layoutNumSalaAtual = new GlyphLayout();
    private GlyphLayout layoutTypeSalaAtual = new GlyphLayout();
    private BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);

    public void desenharSalaAtual(int atual, RoomType type, Batch batch) {
        if (HITBOX_ISVISIBLE) {
            batch.begin();
            layoutNumSalaAtual.setText(font, String.valueOf(atual));
            layoutTypeSalaAtual.setText(font, type.toString());

            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            float posX = screenWidth - layoutNumSalaAtual.width - 10;
            float posXType = screenWidth - layoutTypeSalaAtual.width - 10;
            float posY = screenHeight - 80; // um pouco abaixo das coordenadas

            font.draw(batch, layoutNumSalaAtual, posX, posY);
            font.draw(batch, layoutTypeSalaAtual, posXType, posY - 40);
            batch.end();
        }
    }
    public void desenharMiniMapa(OrthographicCamera hudCamera, Batch batch, ShapeRenderer shapeRenderer,
                                 Set<String> salasVisitadas, Room currentRoom, Map<String, Room> roomMap) {
        shapeRenderer.setProjectionMatrix(hudCamera.combined);

        int tamanho = 10; // tamanho de cada quadrado
        int offsetX = 150;
        int offsetY = 150;

        // calcula limites do minimapa
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

        for (String key : salasVisitadas) {
            String[] coords = key.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        int largura = (maxX - minX + 1) * SEPARACAO + 40;
        int altura = (maxY - minY + 1) * SEPARACAO + 40;

        float rectX = offsetX + minX * SEPARACAO - 20;
        float rectY = offsetY + minY * SEPARACAO - 20;

        // habilita blending para transparência
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // desenha fundo primeiro
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.3f)); // preto com 30% de opacidade
        shapeRenderer.rect(rectX, rectY, largura, altura);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (String key : salasVisitadas) {
            String[] coords = key.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);

            float posX = offsetX + x * SEPARACAO;
            float posY = offsetY + y * SEPARACAO;

            float centroX = posX + tamanho / 2f;
            float centroY = posY + tamanho / 2f;

            Room room = roomMap.get(key);
            if (room != null) {
                shapeRenderer.setColor(Color.WHITE);

                if (room.hasNorth()) {
                    String destinoKey = x + "," + (y + 1);
                    if (salasVisitadas.contains(destinoKey)) {
                        float destX = offsetX + x * SEPARACAO;
                        float destY = offsetY + (y + 1) * SEPARACAO;
                        float destCentroX = destX + tamanho / 2f;
                        float destCentroY = destY + tamanho / 2f;
                        shapeRenderer.line(centroX, centroY - AJUSTE, destCentroX, destCentroY - AJUSTE);
                    }
                }
                if (room.hasSouth()) {
                    String destinoKey = x + "," + (y - 1);
                    if (salasVisitadas.contains(destinoKey)) {
                        float destX = offsetX + x * SEPARACAO;
                        float destY = offsetY + (y - 1) * SEPARACAO;
                        float destCentroX = destX + tamanho / 2f;
                        float destCentroY = destY + tamanho / 2f;
                        shapeRenderer.line(centroX, centroY - AJUSTE, destCentroX, destCentroY - AJUSTE);
                    }
                }
                if (room.hasEast()) {
                    String destinoKey = (x + 1) + "," + y;
                    if (salasVisitadas.contains(destinoKey)) {
                        float destX = offsetX + (x + 1) * SEPARACAO;
                        float destY = offsetY + y * SEPARACAO;
                        float destCentroX = destX + tamanho / 2f;
                        float destCentroY = destY + tamanho / 2f;
                        shapeRenderer.line(centroX, centroY - AJUSTE, destCentroX, destCentroY - AJUSTE);
                    }
                }
                if (room.hasWest()) {
                    String destinoKey = (x - 1) + "," + y;
                    if (salasVisitadas.contains(destinoKey)) {
                        float destX = offsetX + (x - 1) * SEPARACAO;
                        float destY = offsetY + y * SEPARACAO;
                        float destCentroX = destX + tamanho / 2f;
                        float destCentroY = destY + tamanho / 2f;
                        shapeRenderer.line(centroX, centroY - AJUSTE, destCentroX, destCentroY - AJUSTE);
                    }
                }
            }
        }

        shapeRenderer.end();

        batch.begin();
        for (String key : salasVisitadas) {
            String[] coords = key.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);

            float posX = offsetX + x * SEPARACAO;
            float posY = offsetY + y * SEPARACAO;

            if (currentRoom.getX() == x && currentRoom.getY() == y) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.WHITE);
            }

            font.draw(batch, "■", posX, posY);
        }
        batch.end();
    }
}
