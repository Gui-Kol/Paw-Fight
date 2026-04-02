package com.pawfight.game.engine.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.engine.design.desenhar.DesenharTexto;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.engine.procedural.sala.Sala;
import com.pawfight.game.engine.procedural.sala.TipoSala;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.world.WorldTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pawfight.game.engine.VariavelComum.*;

public class DesenharMiniMapa {
    private static final int SEPARACAO = 30; // distância entre quadrados no minimapa
    private static final int AJUSTE = 15; // ajuste altura da linha
    private final BitmapFont font = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 20);

    private void desenharSalaAtual(WorldTemplate world) {
        if (HITBOX_ISVISIBLE) {
            Sala currentRoom = world.getCurrentRoom();
            Batch batch = world.getBatch();
            int atual = world.getRooms().indexOf(currentRoom);

            batch.begin();
            Hud hud = world.getPlayer().getHud();
            String texto = "Sala Atual: " + atual + "\nTipo da sala: " + currentRoom.getType();
            GlyphLayout layout = new GlyphLayout(font, texto);
            Color corFundo = new Color();
            var xW = (GET_LARGURA_TELA_BASE - layout.width) - 10;
            var yH = (GET_ALTURA_TELA_BASE - layout.height) - 130;
            hud.getDesenharTexto().desenhar(batch, world.getShapeRenderer(), corFundo, texto, font, xW, yH, 0, false);
            batch.end();
        }
    }


    public void desenharMiniMapa(WorldTemplate world) {
        PlayerTemplate player = world.getPlayer();

        if (player.isPause()){return;}

        ShapeRenderer shapeRenderer = world.getShapeRenderer();
        Batch batch = world.getBatch();
        Map<String, Sala> roomMap = world.getRoomGenerator().getRoomMap();
        Set<String> salasVisitadas = world.getSalasVisitadas();
        OrthographicCamera camera = player.getHud().getHudCamera();
        shapeRenderer.setProjectionMatrix(camera.combined);
        Sala currentRoom = world.getCurrentRoom();

        desenharSalaAtual(world);

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
        int altura = (maxY - minY + 1) * SEPARACAO + 10;

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

            Sala Sala = roomMap.get(key);
            if (Sala != null) {
                shapeRenderer.setColor(Color.WHITE);

                if (Sala.hasNorth()) {
                    String destinoKey = x + "," + (y + 1);
                    if (salasVisitadas.contains(destinoKey)) {
                        float destX = offsetX + x * SEPARACAO;
                        float destY = offsetY + (y + 1) * SEPARACAO;
                        float destCentroX = destX + tamanho / 2f;
                        float destCentroY = destY + tamanho / 2f;
                        shapeRenderer.line(centroX, centroY - AJUSTE, destCentroX, destCentroY - AJUSTE);
                    }
                }
                if (Sala.hasSouth()) {
                    String destinoKey = x + "," + (y - 1);
                    if (salasVisitadas.contains(destinoKey)) {
                        float destX = offsetX + x * SEPARACAO;
                        float destY = offsetY + (y - 1) * SEPARACAO;
                        float destCentroX = destX + tamanho / 2f;
                        float destCentroY = destY + tamanho / 2f;
                        shapeRenderer.line(centroX, centroY - AJUSTE, destCentroX, destCentroY - AJUSTE);
                    }
                }
                if (Sala.hasEast()) {
                    String destinoKey = (x + 1) + "," + y;
                    if (salasVisitadas.contains(destinoKey)) {
                        float destX = offsetX + (x + 1) * SEPARACAO;
                        float destY = offsetY + y * SEPARACAO;
                        float destCentroX = destX + tamanho / 2f;
                        float destCentroY = destY + tamanho / 2f;
                        shapeRenderer.line(centroX, centroY - AJUSTE, destCentroX, destCentroY - AJUSTE);
                    }
                }
                if (Sala.hasWest()) {
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

        batch.setProjectionMatrix(camera.combined);
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

    public void dispose() {
        font.dispose();
    }
}
