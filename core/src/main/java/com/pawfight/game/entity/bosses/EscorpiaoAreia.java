package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.enemy.MoverDirecaoPlayer;
import com.pawfight.game.entity.enemy.Skeleton;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EscorpiaoAreia extends BossTemplate {

    public enum Estado {
        APROXIMACAO,
        AVISO,
        ATAQUE,
        RECUPERACAO,
        SALTO_AVISO,
        SALTO,
        ENTERRADO,
        TRANSICAO,
        MORTE
    }

    private static final int VIDA_BASE = 420;
    private static final int FORCA = 6;
    private static final int VELOCIDADE = 180;
    private static final int TAMANHO = 128;
    private static final int HITBOX = 54;
    // O Player ainda não possui StatusComponent; o veneno é representado por dano direto adicional.
    private static final int DANO_VENENO_DIRETO = 1;

    private static final float LIMIAR_FASE_2 = 0.65f;
    private static final float LIMIAR_FASE_3 = 0.30f;
    private static final float ALCANCE_FERRAO = 72f;
    private static final float ALCANCE_ATERRISSAGEM = 92f;
    private static final float ANTECIPACAO_FERRAO = 0.5f;
    private static final float ANTECIPACAO_DUPLO = 0.45f;
    private static final float INTERVALO_GOLPE_DUPLO = 0.35f;
    private static final float RECUPERACAO = 0.4f;
    private static final float COOLDOWN_ATAQUE = 1.4f;
    private static final float COOLDOWN_ATAQUE_FASE_3 = 0.7f;
    private static final float COOLDOWN_MINIMO = 0.6f;
    private static final float ANTECIPACAO_SALTO = 0.65f;
    private static final float COOLDOWN_SALTO = 6f;
    private static final float DURACAO_ENTERRADO = 1f;
    private static final float COOLDOWN_INVOCACAO = 12f;
    private static final int MAX_INVOCACOES = 3;

    private final MoverDirecaoPlayer mover = new MoverDirecaoPlayer();
    private final List<EnemyTemplate> invocacoesAtivas = new ArrayList<>(MAX_INVOCACOES);
    private final List<EnemyTemplate> invocacoesPendentes = new ArrayList<>(MAX_INVOCACOES);

    private Estado estado = Estado.APROXIMACAO;
    private float timerEstado;
    private float cooldownAtaque;
    private float cooldownSalto = COOLDOWN_SALTO;
    private float cooldownInvocacao = COOLDOWN_INVOCACAO;
    private float destinoSaltoX;
    private float destinoSaltoY;
    private int golpeAtual;
    private boolean danoAplicadoNaJanela;

    public EscorpiaoAreia(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
    }

    @Override
    protected DadosInimigo dadosInimigo() {
        return new DadosInimigo(
            "Escorpião da Areia",
            VIDA_BASE,
            FORCA,
            VELOCIDADE,
            TAMANHO,
            HITBOX,
            0,
            -12,
            textura("entitys/enemy/Skeleton/Idle.png"),
            textura("entitys/enemy/Skeleton/Walk.png"),
            textura("entitys/enemy/Skeleton/Death.png"),
            null,
            textura("entitys/enemy/Skeleton/Walk.png"),
            textura("entitys/enemy/Skeleton/Walk.png"),
            1f,
            null,
            null
        );
    }

    private Texture textura(String caminho) {
        return Assets.manager.isLoaded(caminho, Texture.class) ? Assets.get(caminho, Texture.class) : null;
    }

    @Override
    public void updateSpriteDefinitions() {
        if (animacao.getIdleSheet() == null) return;
        DefinirSprite idle = new DefinirSprite(animacao.getIdleSheet(), 4, 0.1f, false, olhandoEsquerda);
        DefinirSprite walk = new DefinirSprite(animacao.getWalkSheet(), 6, 0.1f, false, olhandoEsquerda);
        DefinirSprite dead = new DefinirSprite(animacao.getDeadSheet(), 8, 0.1f, false, olhandoEsquerda);
        animacao.setDefinitions(idle, walk, dead, walk, walk, walk);
    }

    @Override
    protected void definirFases(List<FaseBoss> fases) {
        Animation<TextureRegion> ataque = null;
        if (animacao.getWalkSheet() != null) {
            ataque = animacao.getMotorAnimacao().animar(
                new DefinirSprite(animacao.getWalkSheet(), 6, 0.08f, false, olhandoEsquerda));
            transicaoFase = animacao.getMotorAnimacao().criarAnimacao(
                new DefinirSprite(animacao.getDeadSheet(), 8, 0.06f, false, olhandoEsquerda), false);
        }
        fases.add(new FaseCacador(ataque));
        fases.add(new FasePredador(ataque));
        fases.add(new FaseFuriaDaAreia(ataque));
    }

    @Override
    public void update(float delta) {
        if (player != null && player.isPause()) return;
        if (!stats.isMorto()) sincronizarFaseComVida();
        super.update(delta);
        if (stats.isMorto()) {
            cancelarAtaque(Estado.MORTE);
            invocacoesPendentes.clear();
        }
    }

    @Override
    public void executarIA(float delta) {
        if (stats.isMorto() || player == null || player.isMorto()) {
            moving = false;
            return;
        }
        if (estado == Estado.TRANSICAO && !emTransicao) estado = Estado.APROXIMACAO;
        if (emTransicao || estado == Estado.TRANSICAO) return;

        cooldownAtaque = Math.max(0f, cooldownAtaque - delta);
        cooldownSalto = Math.max(0f, cooldownSalto - delta);
        cooldownInvocacao = Math.max(0f, cooldownInvocacao - delta);
        limparInvocacoesMortas();

        switch (estado) {
            case APROXIMACAO -> processarAproximacao(delta);
            case AVISO -> processarAviso(delta);
            case ATAQUE -> processarAtaque(delta);
            case RECUPERACAO -> processarRecuperacao(delta);
            case SALTO_AVISO -> processarAvisoSalto(delta);
            case SALTO -> aterrissar();
            case ENTERRADO -> processarEnterrado(delta);
            default -> moving = false;
        }
    }

    private void processarAproximacao(float delta) {
        executarAtaqueEspecial();
        if (estado != Estado.APROXIMACAO) return;

        if (calcularDistanciaAoPlayer() > ALCANCE_FERRAO) {
            mover.mover(this, delta);
        } else {
            moving = false;
            if (cooldownAtaque <= 0f) executarAtaqueNormal();
        }
    }

    private void iniciarAtaqueNormal(boolean duplo) {
        if (estado != Estado.APROXIMACAO) return;
        golpeAtual = duplo ? 0 : -1;
        timerEstado = 0f;
        danoAplicadoNaJanela = false;
        estado = Estado.AVISO;
        moving = false;
    }

    private void processarAviso(float delta) {
        timerEstado += delta;
        float antecipacao = golpeAtual < 0 ? ANTECIPACAO_FERRAO : ANTECIPACAO_DUPLO;
        if (timerEstado < antecipacao) return;
        estado = Estado.ATAQUE;
        timerEstado = 0f;
        danoAplicadoNaJanela = false;
        aplicarContato();
    }

    private void processarAtaque(float delta) {
        if (golpeAtual < 0) {
            iniciarRecuperacao();
            return;
        }
        timerEstado += delta;
        if (timerEstado >= INTERVALO_GOLPE_DUPLO) {
            golpeAtual++;
            timerEstado = 0f;
            danoAplicadoNaJanela = false;
            aplicarContato();
            if (golpeAtual >= 1) iniciarRecuperacao();
        }
    }

    private void aplicarContato() {
        if (danoAplicadoNaJanela || calcularDistanciaAoPlayer() > ALCANCE_FERRAO) return;
        int dano = stats.getForca();
        if (golpeAtual < 0) dano += DANO_VENENO_DIRETO;
        player.dano(dano);
        danoAplicadoNaJanela = true;
        atacando = true;
        animacao.resetStateTime();
    }

    private void iniciarSalto() {
        if (estado != Estado.APROXIMACAO || cooldownSalto > 0f) return;
        destinoSaltoX = player.getDx();
        destinoSaltoY = player.getDy();
        timerEstado = 0f;
        danoAplicadoNaJanela = false;
        estado = Estado.SALTO_AVISO;
        moving = false;
        cooldownSalto = COOLDOWN_SALTO;
    }

    private void processarAvisoSalto(float delta) {
        timerEstado += delta;
        if (timerEstado >= ANTECIPACAO_SALTO) estado = Estado.SALTO;
    }

    private void aterrissar() {
        setLocation(Math.round(destinoSaltoX), Math.round(destinoSaltoY));
        if (!danoAplicadoNaJanela && calcularDistanciaAoPlayer() <= ALCANCE_ATERRISSAGEM) {
            player.dano(stats.getForca() + 2);
            danoAplicadoNaJanela = true;
        }
        atacandoEspecial = true;
        iniciarRecuperacao();
    }

    private void iniciarInvocacao() {
        if (estado != Estado.APROXIMACAO || cooldownInvocacao > 0f || quantidadeInvocacoesVivas() >= MAX_INVOCACOES) return;
        estado = Estado.ENTERRADO;
        timerEstado = 0f;
        moving = false;
        cooldownInvocacao = COOLDOWN_INVOCACAO;
    }

    private void processarEnterrado(float delta) {
        timerEstado += delta;
        if (timerEstado < DURACAO_ENTERRADO) return;
        int faltantes = MAX_INVOCACOES - quantidadeInvocacoesVivas();
        for (int i = 0; i < faltantes; i++) {
            EnemyTemplate invocacao = criarInvocacao(i);
            invocacao.setInvocador(this);
            invocacao.setEnemiesList(enemiesList);
            invocacao.setParedesColisores(paredesColisores);
            invocacoesAtivas.add(invocacao);
            invocacoesPendentes.add(invocacao);
        }
        iniciarRecuperacao();
    }

    protected EnemyTemplate criarInvocacao(int indice) {
        int deslocamentoX = (indice - 1) * 70;
        return new Skeleton(Math.round(dx) + deslocamentoX, Math.round(dy) - 50, false, player);
    }

    private void processarRecuperacao(float delta) {
        timerEstado += delta;
        if (timerEstado < RECUPERACAO) return;
        estado = Estado.APROXIMACAO;
        atacando = false;
        atacandoEspecial = false;
        float cadencia = faseAtual >= 2 ? COOLDOWN_ATAQUE_FASE_3 : COOLDOWN_ATAQUE;
        cooldownAtaque = Math.max(COOLDOWN_MINIMO, cadencia);
    }

    private void iniciarRecuperacao() {
        estado = Estado.RECUPERACAO;
        timerEstado = 0f;
    }

    private void cancelarAtaque(Estado novoEstado) {
        estado = novoEstado;
        timerEstado = 0f;
        golpeAtual = 0;
        danoAplicadoNaJanela = false;
        atacando = false;
        atacandoEspecial = false;
        moving = false;
    }

    private void sincronizarFaseComVida() {
        while (faseAtual < fases.size() - 1 && faseVigente().deveTransicionar(stats.getVida(), stats.getVidaBase())) {
            mudarFase(faseAtual + 1);
        }
    }

    private void limparInvocacoesMortas() {
        Iterator<EnemyTemplate> iterator = invocacoesAtivas.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isMorto()) iterator.remove();
        }
    }

    private int quantidadeInvocacoesVivas() {
        limparInvocacoesMortas();
        return invocacoesAtivas.size();
    }

    @Override
    public void drenarInvocacoes(List<EnemyTemplate> destino) {
        destino.addAll(invocacoesPendentes);
        invocacoesPendentes.clear();
    }

    @Override
    public void executarAtaqueNormal() {
        FaseBoss fase = faseVigente();
        if (fase != null) fase.executarAtaqueNormal(this);
    }

    @Override
    public void executarAtaqueEspecial() {
        FaseBoss fase = faseVigente();
        if (fase != null) fase.executarAtaqueEspecial(this);
    }

    @Override
    public void mudarFase(int novaFase) {
        if (novaFase <= faseAtual || novaFase >= fases.size()) return;
        faseAtual = novaFase;
        cancelarAtaque(Estado.TRANSICAO);
        aplicarAnimacoesFase(fases.get(novaFase));
        iniciarTransicao();
        if (!emTransicao) estado = Estado.APROXIMACAO;
    }

    @Override
    public void atualizarEstado() {
        sincronizarFaseComVida();
    }

    @Override
    public void andarIA(float delta) {
        mover.mover(this, delta);
    }

    @Override
    public int getTamanho() {
        return TAMANHO;
    }

    @Override
    public EnemyTemplate cloneEnemy() {
        return new EscorpiaoAreia(Math.round(dx), Math.round(dy), forte, player);
    }

    @Override
    protected int moedasMorte() {
        return 75;
    }

    @Override
    public void drawSprite(SpriteBatch batch) {
        if (estado != Estado.ENTERRADO) {
            super.drawSprite(batch);
            return;
        }
        Color cor = batch.getColor();
        float r = cor.r, g = cor.g, b = cor.b, a = cor.a;
        batch.setColor(r, g, b, 0.55f);
        batch.draw(animaAtual(), dx, dy - TAMANHO * 0.28f, TAMANHO, TAMANHO);
        batch.setColor(r, g, b, a);
    }

    @Override
    public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (estado != Estado.SALTO_AVISO || shapeRenderer.isDrawing()) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(destinoSaltoX + HITBOX / 2f, destinoSaltoY + HITBOX / 2f, ALCANCE_ATERRISSAGEM);
        shapeRenderer.end();
    }

    public Estado getEstado() { return estado; }
    public int getQuantidadeInvocacoesAtivas() { return quantidadeInvocacoesVivas(); }
    public float getDestinoSaltoX() { return destinoSaltoX; }
    public float getDestinoSaltoY() { return destinoSaltoY; }

    private static class FaseCacador extends FaseBoss {
        FaseCacador(Animation<TextureRegion> ataque) {
            super("Caçador do Deserto", LIMIAR_FASE_2);
            animacaoAtaque = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((EscorpiaoAreia) boss).iniciarAtaqueNormal(false); }
        @Override public void executarAtaqueEspecial(BossTemplate boss) { }
    }

    private static class FasePredador extends FaseBoss {
        FasePredador(Animation<TextureRegion> ataque) {
            super("Predador Saltador", LIMIAR_FASE_3);
            animacaoAtaque = ataque;
            animacaoDefesa = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((EscorpiaoAreia) boss).iniciarAtaqueNormal(true); }
        @Override public void executarAtaqueEspecial(BossTemplate boss) { ((EscorpiaoAreia) boss).iniciarSalto(); }
    }

    private static class FaseFuriaDaAreia extends FaseBoss {
        FaseFuriaDaAreia(Animation<TextureRegion> ataque) {
            super("Fúria da Areia", 0f);
            animacaoAtaque = ataque;
            animacaoDefesa = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((EscorpiaoAreia) boss).iniciarAtaqueNormal(true); }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) {
            EscorpiaoAreia escorpiao = (EscorpiaoAreia) boss;
            if (escorpiao.cooldownInvocacao <= 0f && escorpiao.quantidadeInvocacoesVivas() < MAX_INVOCACOES) {
                escorpiao.iniciarInvocacao();
            } else {
                escorpiao.iniciarSalto();
            }
        }
    }
}
