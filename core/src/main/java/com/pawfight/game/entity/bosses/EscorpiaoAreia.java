package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.bosses.infra.ControladorCooldown;
import com.pawfight.game.entity.bosses.infra.ControladorInvocacoes;
import com.pawfight.game.entity.bosses.infra.JanelaDeContato;
import com.pawfight.game.entity.bosses.infra.MaquinaEstadosBoss;
import com.pawfight.game.entity.bosses.infra.SequenciaAtaques;
import com.pawfight.game.entity.bosses.infra.ZonaTelegrafada;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.enemy.MoverDirecaoPlayer;
import com.pawfight.game.entity.enemy.Skeleton;
import com.pawfight.game.entity.player.PlayerTemplate;

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
    // O Player ainda não possui StatusComponent genérico; o veneno é representado por dano direto adicional.
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
    private final MaquinaEstadosBoss<Estado> maquina = new MaquinaEstadosBoss<>(Estado.APROXIMACAO);
    private final ControladorCooldown cooldownAtaque = new ControladorCooldown(COOLDOWN_ATAQUE, false);
    private final ControladorCooldown cooldownSalto = new ControladorCooldown(COOLDOWN_SALTO, true);
    private final ControladorCooldown cooldownInvocacao = new ControladorCooldown(COOLDOWN_INVOCACAO, true);
    private final ControladorInvocacoes invocacoes = new ControladorInvocacoes(MAX_INVOCACOES);
    private final JanelaDeContato janelaFerrao = new JanelaDeContato();
    private final JanelaDeContato janelaSalto = new JanelaDeContato();
    private final SequenciaAtaques sequenciaGolpes = new SequenciaAtaques(INTERVALO_GOLPE_DUPLO);
    private final ZonaTelegrafada zonaSalto = new ZonaTelegrafada();

    private boolean golpeDuplo;

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
    public void executarIA(float delta) {
        if (stats.isMorto() || player == null || player.isMorto()) {
            moving = false;
            return;
        }
        if (maquina.is(Estado.TRANSICAO) && !emTransicao) maquina.mudar(Estado.APROXIMACAO);
        if (emTransicao || maquina.is(Estado.TRANSICAO)) return;

        cooldownAtaque.atualizar(delta);
        cooldownSalto.atualizar(delta);
        cooldownInvocacao.atualizar(delta);
        invocacoes.removerMortas();

        switch (maquina.getEstado()) {
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
        if (!maquina.is(Estado.APROXIMACAO)) return;

        if (calcularDistanciaAoPlayer() > ALCANCE_FERRAO) {
            mover.mover(this, delta);
        } else {
            moving = false;
            if (cooldownAtaque.pronto()) executarAtaqueNormal();
        }
    }

    private void iniciarAtaqueNormal(boolean duplo) {
        if (!maquina.is(Estado.APROXIMACAO)) return;
        golpeDuplo = duplo;
        janelaFerrao.abrir();
        maquina.mudar(Estado.AVISO);
        moving = false;
    }

    private void processarAviso(float delta) {
        maquina.avancar(delta);
        float antecipacao = golpeDuplo ? ANTECIPACAO_DUPLO : ANTECIPACAO_FERRAO;
        if (maquina.getTimer() < antecipacao) return;
        // Fim do telegraph: abre a sequência de contatos; o primeiro sai no mesmo frame.
        maquina.mudar(Estado.ATAQUE);
        sequenciaGolpes.iniciar(golpeDuplo ? 2 : 1);
        sequenciaGolpes.atualizar(0f, this::aplicarContato);
    }

    private void processarAtaque(float delta) {
        if (sequenciaGolpes.concluida()) {
            iniciarRecuperacao();
            return;
        }
        sequenciaGolpes.atualizar(delta, this::aplicarContato);
        if (sequenciaGolpes.concluida()) iniciarRecuperacao();
    }

    // Contato do ferrão: um dano por janela aberta, somente dentro do alcance.
    private void aplicarContato() {
        janelaFerrao.abrir();
        janelaFerrao.aplicarUmaVez(() -> {
            if (calcularDistanciaAoPlayer() > ALCANCE_FERRAO) return;
            int dano = stats.getForca();
            if (!golpeDuplo) dano += DANO_VENENO_DIRETO;
            player.dano(dano);
            atacando = true;
            animacao.resetStateTime();
        });
    }

    private void iniciarSalto() {
        if (!maquina.is(Estado.APROXIMACAO) || !cooldownSalto.pronto()) return;
        zonaSalto.marcar(player.getDx(), player.getDy());
        janelaSalto.abrir();
        maquina.mudar(Estado.SALTO_AVISO);
        moving = false;
        cooldownSalto.disparar();
    }

    private void processarAvisoSalto(float delta) {
        maquina.avancar(delta);
        if (maquina.getTimer() >= ANTECIPACAO_SALTO) maquina.mudar(Estado.SALTO);
    }

    private void aterrissar() {
        setLocation(Math.round(zonaSalto.getX()), Math.round(zonaSalto.getY()));
        janelaSalto.aplicarUmaVez(() -> {
            if (calcularDistanciaAoPlayer() <= ALCANCE_ATERRISSAGEM) {
                player.dano(stats.getForca() + 2);
            }
        });
        atacandoEspecial = true;
        iniciarRecuperacao();
    }

    private void iniciarInvocacao() {
        if (!maquina.is(Estado.APROXIMACAO) || !cooldownInvocacao.pronto()
            || invocacoes.quantidadeVivas() >= MAX_INVOCACOES) return;
        maquina.mudar(Estado.ENTERRADO);
        moving = false;
        cooldownInvocacao.disparar();
    }

    private void processarEnterrado(float delta) {
        maquina.avancar(delta);
        if (maquina.getTimer() < DURACAO_ENTERRADO) return;
        int faltantes = MAX_INVOCACOES - invocacoes.quantidadeVivas();
        for (int i = 0; i < faltantes; i++) {
            invocacoes.registrar(this, criarInvocacao(i));
        }
        iniciarRecuperacao();
    }

    protected EnemyTemplate criarInvocacao(int indice) {
        int deslocamentoX = (indice - 1) * 70;
        return new Skeleton(Math.round(dx) + deslocamentoX, Math.round(dy) - 50, false, player);
    }

    private void processarRecuperacao(float delta) {
        maquina.avancar(delta);
        if (maquina.getTimer() < RECUPERACAO) return;
        maquina.mudar(Estado.APROXIMACAO);
        atacando = false;
        atacandoEspecial = false;
        float cadencia = faseAtual >= 2 ? COOLDOWN_ATAQUE_FASE_3 : COOLDOWN_ATAQUE;
        cooldownAtaque.disparar(Math.max(COOLDOWN_MINIMO, cadencia));
    }

    private void iniciarRecuperacao() {
        maquina.mudar(Estado.RECUPERACAO);
    }

    // Cancela o ataque em andamento: nenhuma janela aplica dano tardio após o cancelamento.
    private void cancelarAtaque(Estado novoEstado) {
        maquina.mudar(novoEstado);
        sequenciaGolpes.cancelar();
        janelaFerrao.fechar();
        janelaSalto.fechar();
        atacando = false;
        atacandoEspecial = false;
        moving = false;
    }

    @Override
    protected void aoTransicionarFase(int novaFase) {
        cancelarAtaque(Estado.TRANSICAO);
    }

    @Override
    protected void aposTransicionarFase(int novaFase) {
        if (!emTransicao) maquina.mudar(Estado.APROXIMACAO);
    }

    @Override
    protected void aoMorrer() {
        cancelarAtaque(Estado.MORTE);
        invocacoes.cancelarPendentes();
    }

    @Override
    public void drenarInvocacoes(List<EnemyTemplate> destino) {
        invocacoes.drenar(destino);
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
        if (!maquina.is(Estado.ENTERRADO)) {
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
        if (!maquina.is(Estado.SALTO_AVISO) || shapeRenderer.isDrawing()) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(zonaSalto.getX() + HITBOX / 2f, zonaSalto.getY() + HITBOX / 2f, ALCANCE_ATERRISSAGEM);
        shapeRenderer.end();
    }

    public Estado getEstado() { return maquina.getEstado(); }
    public int getQuantidadeInvocacoesAtivas() { return invocacoes.quantidadeVivas(); }
    public float getDestinoSaltoX() { return zonaSalto.getX(); }
    public float getDestinoSaltoY() { return zonaSalto.getY(); }

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
            if (escorpiao.cooldownInvocacao.pronto() && escorpiao.invocacoes.quantidadeVivas() < MAX_INVOCACOES) {
                escorpiao.iniciarInvocacao();
            } else {
                escorpiao.iniciarSalto();
            }
        }
    }
}
