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
import com.pawfight.game.entity.tiro.comportamento.ApresentacaoProjetil;
import com.pawfight.game.entity.tiro.comportamento.EfeitoImpacto;
import com.pawfight.game.entity.tiro.comportamento.MovimentoProjetil;

import java.util.List;

public abstract class TirosTemplate implements Pool.Poolable {

    public static final float INTERVALO_ENTRE_TIROS_RAJADA = 0.1f;

    protected int x, y;
    protected Rectangle hitBox;
    protected int dano;
    protected float duracao, cadencia, intervalo;
    private final EstadoProjetil estado = new EstadoProjetil();
    private MovimentoProjetil movimentoProjetil = MovimentoProjetil.PARADO;
    private EfeitoImpacto efeitoImpacto = EfeitoImpacto.NENHUM;
    private ApresentacaoProjetil apresentacaoProjetil;
    protected Texture texture;
    protected int tamanhoDraw, tamanho, tamanhoPadrao;
    protected int larguraSprite, alturaSprite;
    protected Renderizar renderizar = Renderizar.INSTANCE;
    protected int xHitBox, yHitBox;

    // Quantidade de frames horizontais da spritesheet do tiro (definida na criação).
    protected int quantidadeFrames;
    private final float duracaoFrame;
    private final int quantidadeTirosPadrao;
    private int totalTirosRajada;
    private int indiceProximoTiro;
    private float tempoAteProximoTiro;
    // Animação em loop construída a partir da spritesheet (preparada uma única vez).
    protected Animation<TextureRegion> animacao;
    // Tempo acumulado da animação (avança com delta, independente do movimento).
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
        duracaoFrame = calcularDuracaoFrame();
        quantidadeTirosPadrao = validarQuantidadeTirosPadrao();
        iniciarQuantidadeFrames();

        this.dono = player;
        this.spawnEsquerda = player.isOlhandoEsquerda();

        this.tamanho = tamanho + tamanhoPadrao;
        this.tamanhoDraw = tamanho + tamanhoPadrao;
        this.larguraSprite = tamanhoDraw;
        this.alturaSprite = tamanhoDraw;
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
        definirTamanhoSprite();
    }

    protected TirosTemplate() {
        duracao = definirDuracao();
        cadencia = definirIntervalo();
        tamanhoPadrao = definirTamanhoPadrao();
        intervalo = 0;
        duracaoFrame = calcularDuracaoFrame();
        quantidadeTirosPadrao = validarQuantidadeTirosPadrao();
        iniciarQuantidadeFrames();
        tamanho = tamanhoPadrao;
        tamanhoDraw = tamanhoPadrao;
        larguraSprite = tamanhoDraw;
        alturaSprite = tamanhoDraw;
        hitBox = new Rectangle();
        definirTamanhoSprite();
    }

    // Quantidade de frames horizontais da spritesheet do tiro (deve ser maior que zero).
    protected abstract int definirQuantidadeFrames();

    /** Quantidade de frames exibidos por segundo; deve ser maior que zero. */
    protected abstract int definirFramesPorSegundo();

    /** Quantidade base de projéteis criada a cada disparo; deve ser maior que zero. */
    protected abstract int definirQuantidadeTirosPadrao();

    private float calcularDuracaoFrame() {
        int framesPorSegundo = definirFramesPorSegundo();
        if (framesPorSegundo <= 0) {
            throw new IllegalArgumentException(
                "A quantidade de frames por segundo precisa ser maior que zero. Valor recebido: "
                    + framesPorSegundo);
        }
        return 1f / framesPorSegundo;
    }

    private int validarQuantidadeTirosPadrao() {
        int quantidade = definirQuantidadeTirosPadrao();
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                "A quantidade padrão de tiros precisa ser maior que zero. Valor recebido: " + quantidade);
        }
        return quantidade;
    }

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
            new DefinirSprite(spritesheet, quantidadeFrames, duracaoFrame, false, false));
    }

    // Constrói a animação uma única vez; reconstrói só se a textura mudar (ex.: textura aleatória por disparo).
    protected void garantirAnimacao() {
        if (texture == null) return;
        if (animacao == null || texturaAnimacao != texture) {
            prepararAnimacao(texture, quantidadeFrames);
        }
    }

    /** Troca spritesheet e contagem de quadros, reiniciando a nova animação no primeiro frame. */
    protected final void trocarAnimacao(Texture novaTextura, int novosFrames) {
        if (novosFrames <= 0) {
            throw new IllegalArgumentException(
                "A quantidade de frames do tiro precisa ser maior que zero. Valor recebido: " + novosFrames);
        }
        texture = novaTextura;
        quantidadeFrames = novosFrames;
        animacao = null;
        texturaAnimacao = null;
        estado.reiniciarAnimacao();
    }

    // Frame atual da animação do tiro (loop contínuo enquanto o tiro existir).
    public TextureRegion frameAtual() {
        if (animacao == null) return null;
        return animacao.getKeyFrame(estado.getTempoAnimacao(), true);
    }

    protected void reiniciarBase(int x, int y, int dano, int tamanho, PlayerTemplate player) {
        estado.reiniciar();
        this.dono = player;
        this.spawnEsquerda = player.isOlhandoEsquerda();
        this.tamanho = tamanho + tamanhoPadrao;
        this.tamanhoDraw = tamanho + tamanhoPadrao;
        this.larguraSprite = tamanhoDraw;
        this.alturaSprite = tamanhoDraw;
        definirTamanhoSprite();
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
        estado.reiniciar();
    }

    protected abstract int definirTamanhoPadrao();

    /** Gancho obrigatório: deixe vazio para usar o tamanho padrão quadrado. */
    protected abstract void definirTamanhoSprite();

    /** Define dimensões visuais independentes; valores não positivos usam o tamanho padrão atual. */
    protected final void definirTamanhoSprite(int largura, int altura) {
        larguraSprite = largura > 0 ? largura : tamanhoDraw;
        alturaSprite = altura > 0 ? altura : tamanhoDraw;
    }

    protected abstract float definirDuracao();

    public void desenhar(Batch batch) {
        if (apresentacaoProjetil != null) {
            apresentacaoProjetil.desenhar(batch);
            return;
        }
        desenharPadrao(batch);
    }

    protected final void desenharPadrao(Batch batch) {
        garantirAnimacao();
        TextureRegion frame = frameAtual();
        if (frame == null) return;
        batch.draw(frame, x, y, larguraSprite, alturaSprite);
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
        estado.atualizar(delta);
        if (estado.isAtivoParaColisao() && estado.getTempoVida() >= duracao) {
            gastar();
        }
        if (estado.isAtivoParaColisao()) movimentoProjetil.atualizar(delta);
    }

    public boolean isExpirado() {
        return estado.isExpirado();
    }

    // Consome a colisão imediatamente, mas preserva o sprite até concluir o ciclo de animação atual.
    public void expirar() {
        gastar();
    }

    private void gastar() {
        estado.desativar(quantidadeFrames, duracaoFrame);
    }

    // true quando o tiro some após atingir um único inimigo (DanoTiro o expira no primeiro acerto).
    public boolean isUnicoAlvo() {
        return false;
    }

    public boolean isAtivoParaColisao() { return estado.isAtivoParaColisao(); }

    // Hook chamado por DanoTiro quando o dano é aplicado; subclasses sobrescrevem (queimadura, lentidão, roubo de vida).
    public void aoAcertar(EnemyTemplate inimigo) {
        efeitoImpacto.aplicar(inimigo);
    }

    protected final void configurarMovimento(MovimentoProjetil movimento) {
        movimentoProjetil = movimento == null ? MovimentoProjetil.PARADO : movimento;
    }

    protected final void configurarEfeitoImpacto(EfeitoImpacto efeito) {
        efeitoImpacto = efeito == null ? EfeitoImpacto.NENHUM : efeito;
    }

    protected final void configurarApresentacao(ApresentacaoProjetil apresentacao) {
        apresentacaoProjetil = apresentacao;
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
        x = Math.round(centroX - larguraSprite / 2f);
        y = Math.round(centroY - alturaSprite / 2f);
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

    public int getLarguraSprite() { return larguraSprite; }
    public int getAlturaSprite() { return alturaSprite; }
    public float getDuracaoFrame() { return duracaoFrame; }
    public int getQuantidadeTirosPadrao() { return quantidadeTirosPadrao; }
    float getTempoAnimacao() { return estado.getTempoAnimacao(); }
    float getTempoVida() { return estado.getTempoVida(); }
    void setTempoAnimacao(float tempo) { estado.definirTempoAnimacao(tempo); }

    void iniciarRajada(int quantidade) {
        totalTirosRajada = Math.max(1, quantidade);
        indiceProximoTiro = 0;
        tempoAteProximoTiro = 0f;
    }

    boolean temTirosPendentesNaRajada() {
        return indiceProximoTiro < totalTirosRajada;
    }

    boolean atualizarEsperaDaRajada(float delta) {
        if (!temTirosPendentesNaRajada()) return false;
        tempoAteProximoTiro = Math.max(0f, tempoAteProximoTiro - delta);
        return tempoAteProximoTiro <= 0f;
    }

    int consumirProximoTiroDaRajada() {
        int indice = indiceProximoTiro++;
        tempoAteProximoTiro = INTERVALO_ENTRE_TIROS_RAJADA;
        return indice;
    }

    int getTotalTirosRajada() { return totalTirosRajada; }

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
