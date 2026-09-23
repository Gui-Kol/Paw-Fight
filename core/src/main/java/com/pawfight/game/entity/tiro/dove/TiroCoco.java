package com.pawfight.game.entity.tiro.dove;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;

public class TiroCoco extends TirosTemplate {
    private final Texture coco1, coco2, coco3;

    private Pool<TiroCoco> pool;

    public TiroCoco(int tamanho, PlayerTemplate player) {
        super(player.getDx() - tamanho / 2, player.getDy() - tamanho / 2, player.getForca(), tamanho, player);
        coco1 = Assets.get("entitys/player/dove/coco/2.png", Texture.class);
        coco2 = Assets.get("entitys/player/dove/coco/3.png", Texture.class);
        coco3 = Assets.get("entitys/player/dove/coco/1.png", Texture.class);
        texture = randomTex();

        // Cria o pool apenas no modelo
        pool = new Pool<TiroCoco>(8, 64) {
            @Override
            protected TiroCoco newObject() {
                return new TiroCoco();
            }
        };
    }

    private TiroCoco() {
        super(); // inicializa constantes (duracao, cadencia, tamanhoPadrao)
        coco1 = Assets.get("entitys/player/dove/coco/2.png", Texture.class);
        coco2 = Assets.get("entitys/player/dove/coco/3.png", Texture.class);
        coco3 = Assets.get("entitys/player/dove/coco/1.png", Texture.class);
    }

    @Override
    protected Rectangle gerarHitBox() {
        this.xHitBox += tamanho / 4;
        this.yHitBox += tamanho / 4;

        return new Rectangle(xHitBox, yHitBox, (float) tamanho / 2, (float) tamanho / 2);
    }

    @Override
    protected TirosTemplate obterDoPool(PlayerTemplate player) {
        TiroCoco c = pool.obtain();

        c.reiniciarBase(
            player.getDx() - tamanho / 2,
            player.getDy() - tamanho / 2,
            player.getForca(),
            tamanho,
            player
        );

        c.xHitBox += c.tamanho / 4;
        c.yHitBox += c.tamanho / 4;
        c.hitBox.set(c.xHitBox, c.yHitBox, (float) c.tamanho / 2, (float) c.tamanho / 2);
        c.texture = c.randomTex();
        c.ownerPool = pool;

        return c;
    }

    @Override
    protected int definirTamanhoPadrao() {
        return 16;
    }

    @Override
    protected void definirTamanhoSprite() {
    }

    @Override
    protected int definirQuantidadeFrames() {
        return 1; // cada textura aleatória é estática (frame único)
    }

    @Override
    protected int definirFramesPorSegundo() {
        return 12;
    }

    @Override
    protected float definirDuracao() {
        return 2;
    }

    @Override
    protected Texture randomTex() {
        int random = (int) (Math.random() * 3);
        return switch (random) {
            case 1 -> coco1;
            case 2 -> coco2;
            default -> coco3;
        };
    }

    @Override
    protected Texture singleTex() {
        return texture;
    }

    @Override
    protected float definirIntervalo() {
        return 1;
    }
}
