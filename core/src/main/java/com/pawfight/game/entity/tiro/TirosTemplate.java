package com.pawfight.game.entity.tiro;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.design.animation.MotorAnimacao;
import com.pawfight.game.engine.render.Renderizar;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.List;

public abstract class TirosTemplate implements Pool.Poolable {

    // Duração padrão entre frames da animação do tiro (segundos por frame).
    public static final float DURACAO_FRAME_PADRAO = 0.08f;

    protected int x, y;
    protected Rectangle hitBox;
    protected int dano;
    protected float duracao, cadencia, intervalo;
    protected float tempoVida = 0f;
    protected Texture texture;
    protected int tamanhoDraw, tamanho, tamanhoPadrao;
    protected Renderizar renderizar = Renderizar.INSTANCE;
    protected int xHitBox, yHitBox;

    // Quantidade de frames horizontais da spritesheet do tiro (definida na criação).
    protected int quantidadeFrames;
    // Animação em loop construída a partir da spritesheet (preparada uma única vez).
    protected Animation<TextureRegion> animacao;
    // Tempo acumulado da animação (avança com delta, independente do movimento).
    protected float tempoAnimacao = 0f;
    // Textura a partir da qual a animação atual foi construída.
    private Texture texturaAnimacao;
    // Utilitário de animação reutilizado (divide a spritesheet e monta o loop).
    private final MotorAnimacao motorAnimacao = new MotorAnimacao();

    // Quem disparou — usado por efeitos de acerto (ex.: roubo de vida).
    protected PlayerTemplate dono;
    // Direção do olhar no momento do disparo (posicionamento direcional).
    protected boolean spawnEsquerda;
    // Inimigos vivos da sala atual (referência do mundo, injetada no disparo).
    protected List<EnemyTemplate> inimigos;
    // Direção unitária de deslocamento (tiros que se movem).
    protected float dirMovimentoX = 1f, dirMovimentoY = 0f;

    @SuppressWarnings("rawtypes")
    protected Pool ownerPool;

    public TirosTemplate(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        duracao = definirDuracao();
        cadencia = definirIntervalo();
        tamanhoPadrao = definirTamanhoPadrao();
        intervalo = 0;
        iniciarQuantidadeFrames();

        this.dono = player;
        this.spawnEsquerda = player.isOlhandoEsquerda();

        this.tamanho = tamanho + tamanhoPadrao;
        this.tamanhoDraw = tamanho + tamanhoPadrao;
        this.x = x;
        this.y = y;
        this.dano = dano;

        this.xHitBox = x;
        this.yHitBox = y;

        if (player.isOlhandoEsquerda()) {
            this.x += player.getTamanho();
            xHitBox += player.getTamanho();
        }

        texture = singleTex();
        hitBox = gerarHitBox();
    }

    protected TirosTemplate() {
        duracao = definirDuracao();
        cadencia = definirIntervalo();
        tamanhoPadrao = definirTamanhoPadrao();
        intervalo = 0;
        iniciarQuantidadeFrames();
        hitBox = new Rectangle();
    }

    // Quantidade de frames horizontais da spritesheet do tiro (deve ser maior que zero).
    protected abstract int definirQuantidadeFrames();

    // Valida e define a quantidade de frames da spritesheet informada pela subclasse.
    private void iniciarQuantidadeFrames() {
        int frames = definirQuantidadeFrames();
        if (frames <= 0) {
            throw new IllegalArgumentException(
                "A quantidade de frames do tiro precisa ser maior que zero. Valor recebido: " + frames);
        }
        this.quantidadeFrames = frames;
    }

    // Monta a animação em loop uma única vez por textura (largura do frame = largura da textura / frames).
    protected void prepararAnimacao(Texture spritesheet, int quantidadeFrames) {
        if (quantidadeFrames <= 0) {
            throw new IllegalArgumentException(
                "A quantidade de frames do tiro precisa ser maior que zero. Valor recebido: " + quantidadeFrames);
        }
        if (spritesheet == null) {
            return; // textura ainda não definida: a animação será preparada em garantirAnimacao()
        }
        int larguraTextura = spritesheet.getWidth();
        if (larguraTextura % quantidadeFrames != 0) {
            throw new IllegalArgumentException(
                "Spritesheet do tiro inválida: a largura da textura (" + larguraTextura
                    + "px) não é divisível pela quantidade de frames (" + quantidadeFrames + ").");
        }
        this.quantidadeFrames = quantidadeFrames;
        this.texturaAnimacao = spritesheet;
        this.animacao = motorAnimacao.animar(
            new DefinirSprite(spritesheet, quantidadeFrames, DURACAO_FRAME_PADRAO, false, false));
    }

    // Constrói a animação uma única vez; reconstrói só se a textura mudar (ex.: textura aleatória por disparo).
    protected void garantirAnimacao() {
        if (texture == null) return;
        if (animacao == null || texturaAnimacao != texture) {
            prepararAnimacao(texture, quantidadeFrames);
        }
    }

    // Frame atual da animação do tiro (loop contínuo enquanto o tiro existir).
    public TextureRegion frameAtual() {
        if (animacao == null) return null;
        return animacao.getKeyFrame(tempoAnimacao, true);
    }

    protected void reiniciarBase(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        this.tempoVida = 0f;
        this.tempoAnimacao = 0f;
        this.dono = player;
        this.spawnEsquerda = player.isOlhandoEsquerda();
        this.tamanho = tamanho + tamanhoPadrao;
        this.tamanhoDraw = tamanho + tamanhoPadrao;
        this.x = x;
        this.y = y;
        this.dano = dano;
        this.xHitBox = x;
        this.yHitBox = y;
        if (player.isOlhandoEsquerda()) {
            this.x += player.getTamanho();
            xHitBox += player.getTamanho();
        }
    }

    // Devolve este projétil ao pool de onde veio (se houver).
    @SuppressWarnings("unchecked")
    public void liberar() {
        if (ownerPool != null) {
            Pool p = ownerPool;
            ownerPool = null;      // previne double-free
            p.free(this);
        }
    }

    @Override
    public void reset() {
        tempoVida = 0f;
        tempoAnimacao = 0f;
    }

    protected abstract int definirTamanhoPadrao();

    protected abstract float definirDuracao();

    public void desenhar(Batch batch) {
        garantirAnimacao();
        TextureRegion frame = frameAtual();
        if (frame == null) return;
        batch.draw(frame, x, y, tamanhoDraw, tamanhoDraw);
    }

    public void desenharHitbox(ShapeRenderer shapeRenderer) {
        if (!com.pawfight.game.engine.GameConfig.getInstance().isHitboxVisivel()) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        renderizar.hitboxDraw(shapeRenderer, hitBox);
        shapeRenderer.end();
    }

    public void draw(Batch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        desenhar(batch);
        batch.end();
        desenharHitbox(shapeRenderer);
    }

    public void update(float delta) {
        tempoVida += delta;
        tempoAnimacao += delta;
    }

    public boolean isExpirado() {
        return tempoVida >= duracao;
    }

    // Força a expiração (usado por tiros de alvo único após o acerto).
    public void expirar() {
        tempoVida = duracao;
    }

    // true quando o tiro some após atingir um único inimigo (DanoTiro o expira no primeiro acerto).
    public boolean isUnicoAlvo() {
        return false;
    }

    // Hook chamado por DanoTiro quando o dano é aplicado; subclasses sobrescrevem (queimadura, lentidão, roubo de vida).
    public void aoAcertar(EnemyTemplate inimigo) {
        // padrão: sem efeito adicional
    }

    // Injeta a lista de inimigos vivos da sala (referência compartilhada — nunca copiar nem modificar).
    public void setInimigos(List<EnemyTemplate> inimigos) {
        this.inimigos = inimigos;
    }

    // Inimigo vivo mais próximo do atirador (null se não houver nenhum).
    protected EnemyTemplate inimigoMaisProximo() {
        if (inimigos == null || inimigos.isEmpty() || dono == null) return null;
        EnemyTemplate maisProximo = null;
        float melhorDistancia = Float.MAX_VALUE;
        for (EnemyTemplate inimigo : inimigos) {
            if (inimigo == null || inimigo.isMorto()) continue;
            float ddx = inimigo.getDx() - dono.getDx();
            float ddy = inimigo.getDy() - dono.getDy();
            float distancia = ddx * ddx + ddy * ddy;
            if (distancia < melhorDistancia) {
                melhorDistancia = distancia;
                maisProximo = inimigo;
            }
        }
        return maisProximo;
    }

    // Move desenho e hitbox juntos, centrando a hitbox em (centroX, centroY).
    protected void sincronizarPosicao(float centroX, float centroY) {
        xHitBox = Math.round(centroX - hitBox.width / 2f);
        yHitBox = Math.round(centroY - hitBox.height / 2f);
        hitBox.setPosition(xHitBox, yHitBox);
        x = Math.round(centroX - tamanhoDraw / 2f);
        y = Math.round(centroY - tamanhoDraw / 2f);
    }

    // Aponta a direção para o centro do alvo; sem alvo, segue o olhar do player no momento do disparo.
    protected void definirDirecaoPara(EnemyTemplate alvo) {
        if (alvo != null) {
            Rectangle hbAlvo = alvo.getHitBox();
            float ddx = (hbAlvo.x + hbAlvo.width / 2f) - (xHitBox + hitBox.width / 2f);
            float ddy = (hbAlvo.y + hbAlvo.height / 2f) - (yHitBox + hitBox.height / 2f);
            float modulo = (float) Math.sqrt(ddx * ddx + ddy * ddy);
            if (modulo > 0f) {
                dirMovimentoX = ddx / modulo;
                dirMovimentoY = ddy / modulo;
                return;
            }
        }
        dirMovimentoX = spawnEsquerda ? -1f : 1f;
        dirMovimentoY = 0f;
    }

    // Desloca o tiro em linha reta na direção atual de movimento.
    protected void moverNaDirecao(float velocidade, float delta) {
        float centroX = xHitBox + hitBox.width / 2f + dirMovimentoX * velocidade * delta;
        float centroY = yHitBox + hitBox.height / 2f + dirMovimentoY * velocidade * delta;
        sincronizarPosicao(centroX, centroY);
    }

    // Rotaciona a direção de movimento (leque multi-tiro); sem efeito em tiros sem dirMovimento. Zero alocação.
    public void rotacionarDirecao(float graus) {
        if (graus == 0f) return;
        double rad = Math.toRadians(graus);
        double cos = Math.cos(rad);
        double sen = Math.sin(rad);
        float novoX = (float) (dirMovimentoX * cos - dirMovimentoY * sen);
        float novoY = (float) (dirMovimentoX * sen + dirMovimentoY * cos);
        dirMovimentoX = novoX;
        dirMovimentoY = novoY;
    }

    protected abstract Texture randomTex();

    protected abstract Rectangle gerarHitBox();


    protected abstract TirosTemplate obterDoPool(PlayerTemplate player);

    protected abstract Texture singleTex();

    protected abstract float definirIntervalo();

    public float getCadencia() {
        return cadencia;
    }

    public float getDuracao() {
        return duracao;
    }

    public int getDano() {
        return dano;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public float getIntervalo() {
        return intervalo;
    }

    public void setDuracao(float duracao) {
        this.duracao = duracao;
    }

    public void setIntervalo(float intervalo) {
        this.intervalo = intervalo;
    }

    public void dispose() {
        // Texturas são gerenciadas pelo AssetManager — NÃO dar dispose aqui
    }
}


