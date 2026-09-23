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

public class MumiaAreia extends BossTemplate {

    public enum Estado {
        APROXIMACAO,
        AVISO_GOLPE,
        ATAQUE,
        AVISO_FAIXA,
        FAIXA,
        AVISO_MALDICAO,
        MALDICAO,
        RECUPERACAO,
        TRANSICAO,
        MORTE
    }

    private static final int VIDA_BASE = 480;
    private static final int FORCA = 5;
    private static final int VELOCIDADE = 105;
    private static final int TAMANHO = 128;
    private static final int HITBOX = 50;

    private static final float LIMIAR_FASE_2 = 0.65f;
    private static final float LIMIAR_FASE_3 = 0.30f;
    private static final float ALCANCE_GOLPE = 68f;
    private static final float ANTECIPACAO_GOLPE = 0.6f;
    private static final float COOLDOWN_GOLPE = 2f;
    private static final float RECUPERACAO = 0.4f;

    private static final float ALCANCE_FAIXA = 220f;
    private static final float ANTECIPACAO_FAIXA = 0.55f;
    private static final float DURACAO_FAIXA = 2f;
    private static final float MULTIPLICADOR_FAIXA = 0.35f;
    private static final float COOLDOWN_FAIXA = 8f;

    private static final float RAIO_MALDICAO = 180f;
    private static final float ANTECIPACAO_MALDICAO = 0.8f;
    private static final float COOLDOWN_MALDICAO = 6f;
    private static final int DANO_MALDICAO = 4;

    private static final float PERCENTUAL_REGENERACAO = 0.10f;
    private static final float COOLDOWN_INVOCACAO = 14f;
    private static final int MAX_INVOCACOES = 2;

    private final MoverDirecaoPlayer mover = new MoverDirecaoPlayer();
    private final List<EnemyTemplate> invocacoesAtivas = new ArrayList<>(MAX_INVOCACOES);
    private final List<EnemyTemplate> invocacoesPendentes = new ArrayList<>(MAX_INVOCACOES);

    private Estado estado = Estado.APROXIMACAO;
    private float timerEstado;
    private float cooldownGolpe;
    private float cooldownFaixa;
    private float cooldownMaldicao;
    private float cooldownInvocacao = COOLDOWN_INVOCACAO;
    private boolean danoAplicadoNaJanela;
    private boolean faixaAplicadaNaExecucao;
    private boolean regeneracaoUsada;

    public MumiaAreia(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
    }

    @Override
    protected DadosInimigo dadosInimigo() {
        return new DadosInimigo(
            "Múmia da Areia",
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
        DefinirSprite idle = new DefinirSprite(animacao.getIdleSheet(), 4, 0.14f, false, olhandoEsquerda);
        DefinirSprite walk = new DefinirSprite(animacao.getWalkSheet(), 6, 0.14f, false, olhandoEsquerda);
        DefinirSprite dead = new DefinirSprite(animacao.getDeadSheet(), 8, 0.1f, false, olhandoEsquerda);
        animacao.setDefinitions(idle, walk, dead, walk, walk, walk);
    }

    @Override
    protected void definirFases(List<FaseBoss> fases) {
        Animation<TextureRegion> ataque = null;
        if (animacao.getWalkSheet() != null) {
            ataque = animacao.getMotorAnimacao().animar(
                new DefinirSprite(animacao.getWalkSheet(), 6, 0.11f, false, olhandoEsquerda));
            transicaoFase = animacao.getMotorAnimacao().criarAnimacao(
                new DefinirSprite(animacao.getDeadSheet(), 8, 0.07f, false, olhandoEsquerda), false);
        }
        fases.add(new FaseGuardiaDoTumulo(ataque));
        fases.add(new FaseMaldita(ataque));
        fases.add(new FaseRessurgida(ataque));
    }

    @Override
    public void update(float delta) {
        if (player != null && player.isPause()) return;
        if (!stats.isMorto()) sincronizarFaseComVida();
        super.update(delta);
        if (stats.isMorto()) {
            cancelarAtaque(Estado.MORTE);
            invocacoesPendentes.clear();
            liberarFaixa();
        }
    }

    @Override
    public void executarIA(float delta) {
        if (stats.isMorto() || player == null || player.isMorto()) {
            moving = false;
            liberarFaixa();
            return;
        }
        if (estado == Estado.TRANSICAO && !emTransicao) estado = Estado.APROXIMACAO;
        if (emTransicao || estado == Estado.TRANSICAO) return;

        cooldownGolpe = Math.max(0f, cooldownGolpe - delta);
        cooldownFaixa = Math.max(0f, cooldownFaixa - delta);
        cooldownMaldicao = Math.max(0f, cooldownMaldicao - delta);
        cooldownInvocacao = Math.max(0f, cooldownInvocacao - delta);
        limparInvocacoesMortas();

        switch (estado) {
            case APROXIMACAO -> processarAproximacao(delta);
            case AVISO_GOLPE -> processarAvisoGolpe(delta);
            case ATAQUE -> iniciarRecuperacao();
            case AVISO_FAIXA -> processarAvisoFaixa(delta);
            case FAIXA -> iniciarRecuperacao();
            case AVISO_MALDICAO -> processarAvisoMaldicao(delta);
            case MALDICAO -> iniciarRecuperacao();
            case RECUPERACAO -> processarRecuperacao(delta);
            default -> moving = false;
        }
    }

    private void processarAproximacao(float delta) {
        executarAtaqueEspecial();
        if (estado != Estado.APROXIMACAO) return;

        if (calcularDistanciaAoPlayer() > ALCANCE_GOLPE) {
            mover.mover(this, delta);
        } else {
            moving = false;
            if (cooldownGolpe <= 0f) executarAtaqueNormal();
        }
    }

    private void iniciarGolpe() {
        if (estado != Estado.APROXIMACAO || cooldownGolpe > 0f) return;
        estado = Estado.AVISO_GOLPE;
        timerEstado = 0f;
        danoAplicadoNaJanela = false;
        moving = false;
    }

    private void processarAvisoGolpe(float delta) {
        timerEstado += delta;
        if (timerEstado < ANTECIPACAO_GOLPE) return;
        estado = Estado.ATAQUE;
        if (!danoAplicadoNaJanela && calcularDistanciaAoPlayer() <= ALCANCE_GOLPE) {
            player.dano(stats.getForca());
            danoAplicadoNaJanela = true;
        }
        atacando = true;
        cooldownGolpe = COOLDOWN_GOLPE;
        animacao.resetStateTime();
    }

    private void iniciarFaixa() {
        if (estado != Estado.APROXIMACAO || cooldownFaixa > 0f
            || calcularDistanciaAoPlayer() > ALCANCE_FAIXA) return;
        estado = Estado.AVISO_FAIXA;
        timerEstado = 0f;
        faixaAplicadaNaExecucao = false;
        moving = false;
    }

    private void processarAvisoFaixa(float delta) {
        timerEstado += delta;
        if (timerEstado < ANTECIPACAO_FAIXA) return;
        estado = Estado.FAIXA;
        if (!faixaAplicadaNaExecucao && calcularDistanciaAoPlayer() <= ALCANCE_FAIXA) {
            player.aplicarRestricaoMovimento(MULTIPLICADOR_FAIXA, DURACAO_FAIXA);
            faixaAplicadaNaExecucao = true;
        }
        cooldownFaixa = COOLDOWN_FAIXA;
        atacandoEspecial = true;
    }

    private void iniciarMaldicao() {
        if (estado != Estado.APROXIMACAO || cooldownMaldicao > 0f
            || calcularDistanciaAoPlayer() > RAIO_MALDICAO) return;
        estado = Estado.AVISO_MALDICAO;
        timerEstado = 0f;
        danoAplicadoNaJanela = false;
        moving = false;
    }

    private void processarAvisoMaldicao(float delta) {
        timerEstado += delta;
        if (timerEstado < ANTECIPACAO_MALDICAO) return;
        estado = Estado.MALDICAO;
        if (!danoAplicadoNaJanela && calcularDistanciaAoPlayer() <= RAIO_MALDICAO) {
            player.dano(DANO_MALDICAO);
            danoAplicadoNaJanela = true;
        }
        cooldownMaldicao = COOLDOWN_MALDICAO;
        atacandoEspecial = true;
    }

    private void invocarServos() {
        if (estado != Estado.APROXIMACAO || cooldownInvocacao > 0f
            || quantidadeInvocacoesVivas() >= MAX_INVOCACOES) return;
        int faltantes = MAX_INVOCACOES - quantidadeInvocacoesVivas();
        for (int i = 0; i < faltantes; i++) {
            EnemyTemplate invocacao = criarInvocacao(i);
            invocacao.setInvocador(this);
            invocacao.setEnemiesList(enemiesList);
            invocacao.setParedesColisores(paredesColisores);
            invocacoesAtivas.add(invocacao);
            invocacoesPendentes.add(invocacao);
        }
        cooldownInvocacao = COOLDOWN_INVOCACAO;
        atacandoEspecial = true;
        iniciarRecuperacao();
    }

    protected EnemyTemplate criarInvocacao(int indice) {
        int deslocamentoX = indice == 0 ? -65 : 65;
        return new Skeleton(Math.round(dx) + deslocamentoX, Math.round(dy) - 40, false, player);
    }

    private void processarRecuperacao(float delta) {
        timerEstado += delta;
        if (timerEstado < RECUPERACAO) return;
        estado = Estado.APROXIMACAO;
        atacando = false;
        atacandoEspecial = false;
    }

    private void iniciarRecuperacao() {
        estado = Estado.RECUPERACAO;
        timerEstado = 0f;
    }

    private void cancelarAtaque(Estado novoEstado) {
        estado = novoEstado;
        timerEstado = 0f;
        danoAplicadoNaJanela = false;
        faixaAplicadaNaExecucao = false;
        atacando = false;
        atacandoEspecial = false;
        moving = false;
    }

    private void sincronizarFaseComVida() {
        while (faseAtual < fases.size() - 1 && faseVigente().deveTransicionar(stats.getVida(), stats.getVidaBase())) {
            mudarFase(faseAtual + 1);
        }
    }

    private void regenerarUmaVez() {
        if (regeneracaoUsada) return;
        int cura = Math.round(stats.getVidaBase() * PERCENTUAL_REGENERACAO);
        stats.curar(cura);
        regeneracaoUsada = true;
    }

    private void liberarFaixa() {
        if (player != null) player.removerRestricaoMovimento();
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
        liberarFaixa();
        cancelarAtaque(Estado.TRANSICAO);
        aplicarAnimacoesFase(fases.get(novaFase));
        iniciarTransicao();
        if (faseAtual == 2) regenerarUmaVez();
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

    @Override public int getTamanho() { return TAMANHO; }

    @Override
    public EnemyTemplate cloneEnemy() {
        return new MumiaAreia(Math.round(dx), Math.round(dy), forte, player);
    }

    @Override protected int moedasMorte() { return 80; }

    @Override
    public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (shapeRenderer.isDrawing()) return;
        if (estado == Estado.AVISO_FAIXA && player != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.line(getDx() + HITBOX / 2f, getDy() + HITBOX / 2f,
                player.getDx() + player.getHitboxSize() / 2f, player.getDy() + player.getHitboxSize() / 2f);
            shapeRenderer.end();
        } else if (estado == Estado.AVISO_MALDICAO) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.PURPLE);
            shapeRenderer.circle(getDx() + HITBOX / 2f, getDy() + HITBOX / 2f, RAIO_MALDICAO);
            shapeRenderer.end();
        }
    }

    public Estado getEstado() { return estado; }
    public int getQuantidadeInvocacoesAtivas() { return quantidadeInvocacoesVivas(); }
    public boolean isRegeneracaoUsada() { return regeneracaoUsada; }

    private static class FaseGuardiaDoTumulo extends FaseBoss {
        FaseGuardiaDoTumulo(Animation<TextureRegion> ataque) {
            super("Guardiã do Túmulo", LIMIAR_FASE_2);
            animacaoAtaque = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((MumiaAreia) boss).iniciarGolpe(); }
        @Override public void executarAtaqueEspecial(BossTemplate boss) { }
    }

    private static class FaseMaldita extends FaseBoss {
        FaseMaldita(Animation<TextureRegion> ataque) {
            super("Tecelã de Maldições", LIMIAR_FASE_3);
            animacaoAtaque = ataque;
            animacaoDefesa = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((MumiaAreia) boss).iniciarGolpe(); }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) {
            MumiaAreia mumia = (MumiaAreia) boss;
            if (mumia.cooldownFaixa <= 0f) mumia.iniciarFaixa();
            if (mumia.estado == Estado.APROXIMACAO && mumia.cooldownMaldicao <= 0f) mumia.iniciarMaldicao();
        }
    }

    private static class FaseRessurgida extends FaseBoss {
        FaseRessurgida(Animation<TextureRegion> ataque) {
            super("Ressurgida", 0f);
            animacaoAtaque = ataque;
            animacaoDefesa = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((MumiaAreia) boss).iniciarGolpe(); }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) {
            MumiaAreia mumia = (MumiaAreia) boss;
            if (mumia.cooldownInvocacao <= 0f && mumia.quantidadeInvocacoesVivas() < MAX_INVOCACOES) {
                mumia.invocarServos();
                return;
            }
            if (mumia.cooldownFaixa <= 0f) mumia.iniciarFaixa();
            if (mumia.estado == Estado.APROXIMACAO && mumia.cooldownMaldicao <= 0f) mumia.iniciarMaldicao();
        }
    }
}
