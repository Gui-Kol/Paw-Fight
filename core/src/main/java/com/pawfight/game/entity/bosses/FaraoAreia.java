package com.pawfight.game.entity.bosses;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.pawfight.game.engine.design.DefinirSprite;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.bosses.infra.AtaqueComAviso;
import com.pawfight.game.entity.bosses.infra.ControladorCooldown;
import com.pawfight.game.entity.bosses.infra.ControladorInvocacoes;
import com.pawfight.game.entity.bosses.infra.MaquinaEstadosBoss;
import com.pawfight.game.entity.bosses.infra.SequenciaAtaques;
import com.pawfight.game.entity.bosses.infra.ZonaTelegrafada;
import com.pawfight.game.entity.enemy.DadosInimigo;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.enemy.MoverDirecaoPlayer;
import com.pawfight.game.entity.enemy.Skeleton;
import com.pawfight.game.entity.player.PlayerTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FaraoAreia extends BossTemplate {

    public enum Estado {
        APROXIMACAO,
        SALVA_PROJETEIS,
        AVISO_TEMPESTADE,
        TEMPESTADE,
        ESCUDO,
        AVISO_ATAQUE_FINAL,
        ATAQUE_FINAL,
        INVOCACAO,
        RECUPERACAO,
        TRANSICAO,
        MORTE
    }

    private static final int VIDA_BASE = 560;
    private static final int FORCA = 6;
    private static final int VELOCIDADE = 90;
    private static final int TAMANHO = 128;
    private static final int HITBOX = 52;

    private static final float LIMIAR_FASE_2 = 0.65f;
    private static final float LIMIAR_FASE_3 = 0.30f;
    private static final float RECUPERACAO = 0.45f;

    private static final int QUANTIDADE_PROJETEIS = 3;
    private static final float INTERVALO_PROJETEIS = 0.2f;
    private static final float COOLDOWN_SALVA = 3f;
    private static final float VELOCIDADE_PROJETIL = 260f;
    private static final float DURACAO_PROJETIL = 3f;
    private static final float TAMANHO_PROJETIL = 18f;
    private static final int DANO_PROJETIL = 5;

    private static final float ANTECIPACAO_TEMPESTADE = 0.8f;
    private static final float DURACAO_TEMPESTADE = 4f;
    private static final float RAIO_TEMPESTADE = 150f;
    private static final float MULTIPLICADOR_TEMPESTADE = 0.6f;
    private static final float COOLDOWN_TEMPESTADE = 9f;

    private static final float DURACAO_ESCUDO = 3f;
    private static final float COOLDOWN_ESCUDO = 12f;

    private static final float COOLDOWN_INVOCACAO = 15f;
    private static final int MAX_INVOCACOES = 2;
    private static final float ANTECIPACAO_ATAQUE_FINAL = 1.2f;
    private static final float RAIO_ATAQUE_FINAL = 125f;
    private static final int DANO_ATAQUE_FINAL = 12;
    private static final float COOLDOWN_ATAQUE_FINAL = 10f;

    private final MoverDirecaoPlayer mover = new MoverDirecaoPlayer();
    private final MaquinaEstadosBoss<Estado> maquina = new MaquinaEstadosBoss<>(Estado.APROXIMACAO);
    private final ControladorCooldown cooldownSalva = new ControladorCooldown(COOLDOWN_SALVA, false);
    private final ControladorCooldown cooldownTempestade = new ControladorCooldown(COOLDOWN_TEMPESTADE, false);
    private final ControladorCooldown cooldownEscudo = new ControladorCooldown(COOLDOWN_ESCUDO, false);
    private final ControladorCooldown cooldownInvocacao = new ControladorCooldown(COOLDOWN_INVOCACAO, true);
    private final ControladorCooldown cooldownAtaqueFinal = new ControladorCooldown(COOLDOWN_ATAQUE_FINAL, false);
    private final ControladorInvocacoes invocacoes = new ControladorInvocacoes(MAX_INVOCACOES);
    private final SequenciaAtaques sequenciaSalva = new SequenciaAtaques(INTERVALO_PROJETEIS);
    private final AtaqueComAviso tempestade = new AtaqueComAviso(ANTECIPACAO_TEMPESTADE);
    private final AtaqueComAviso ataqueFinal = new AtaqueComAviso(ANTECIPACAO_ATAQUE_FINAL);
    private final ZonaTelegrafada zona = new ZonaTelegrafada();
    private final List<ProjetilFarao> projeteis = new ArrayList<>(QUANTIDADE_PROJETEIS * 2);

    private float direcaoSalvaX = 1f;
    private float direcaoSalvaY;

    public FaraoAreia(int dx, int dy, boolean forte, PlayerTemplate player) {
        super(dx, dy, forte, player);
    }

    @Override
    protected DadosInimigo dadosInimigo() {
        return new DadosInimigo(
            "Faraó da Areia",
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
        fases.add(new FaseSenhorDasAreias(ataque));
        fases.add(new FaseTempestadeReal(ataque));
        fases.add(new FaseJulgamentoDoFarao(ataque));
    }

    @Override
    public void executarIA(float delta) {
        if (stats.isMorto() || player == null || player.isMorto()) {
            moving = false;
            limparEfeitos();
            return;
        }
        atualizarProjeteis(delta);
        if (maquina.is(Estado.TRANSICAO) && !emTransicao) maquina.mudar(Estado.APROXIMACAO);
        if (emTransicao || maquina.is(Estado.TRANSICAO)) return;

        cooldownSalva.atualizar(delta);
        cooldownTempestade.atualizar(delta);
        cooldownEscudo.atualizar(delta);
        cooldownInvocacao.atualizar(delta);
        cooldownAtaqueFinal.atualizar(delta);
        invocacoes.removerMortas();

        switch (maquina.getEstado()) {
            case APROXIMACAO -> processarAproximacao(delta);
            case SALVA_PROJETEIS -> processarSalva(delta);
            case AVISO_TEMPESTADE -> processarAvisoTempestade(delta);
            case TEMPESTADE -> processarTempestade(delta);
            case ESCUDO -> processarEscudo(delta);
            case AVISO_ATAQUE_FINAL -> processarAvisoAtaqueFinal(delta);
            case ATAQUE_FINAL, INVOCACAO -> iniciarRecuperacao();
            case RECUPERACAO -> processarRecuperacao(delta);
            default -> moving = false;
        }
    }

    private void processarAproximacao(float delta) {
        executarAtaqueEspecial();
        if (!maquina.is(Estado.APROXIMACAO)) return;
        if (cooldownSalva.pronto()) {
            executarAtaqueNormal();
            return;
        }
        mover.mover(this, delta);
    }

    private void iniciarSalva() {
        if (!maquina.is(Estado.APROXIMACAO) || !cooldownSalva.pronto()) return;
        maquina.mudar(Estado.SALVA_PROJETEIS);
        sequenciaSalva.iniciar(QUANTIDADE_PROJETEIS);
        capturarDirecaoSalva();
        moving = false;
        atacando = true;
    }

    private void capturarDirecaoSalva() {
        float origemX = centroX();
        float origemY = centroY();
        Rectangle alvo = player.getHitBox();
        float deltaX = alvo.x + alvo.width / 2f - origemX;
        float deltaY = alvo.y + alvo.height / 2f - origemY;
        float modulo = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (modulo > 0f) {
            direcaoSalvaX = deltaX / modulo;
            direcaoSalvaY = deltaY / modulo;
        } else {
            direcaoSalvaX = olhandoEsquerda ? -1f : 1f;
            direcaoSalvaY = 0f;
        }
    }

    private void processarSalva(float delta) {
        sequenciaSalva.atualizar(delta, this::dispararProjetil);
        if (sequenciaSalva.concluida()) {
            cooldownSalva.disparar();
            iniciarRecuperacao();
        }
    }

    private void dispararProjetil() {
        projeteis.add(new ProjetilFarao(centroX(), centroY(), direcaoSalvaX, direcaoSalvaY));
    }

    private void iniciarTempestade() {
        if (!maquina.is(Estado.APROXIMACAO) || !cooldownTempestade.pronto()) return;
        capturarZonaDoPlayer();
        maquina.mudar(Estado.AVISO_TEMPESTADE);
        tempestade.iniciarAviso();
        moving = false;
    }

    private void processarAvisoTempestade(float delta) {
        maquina.avancar(delta);
        if (!tempestade.avisoConcluido(maquina.getTimer())) return;
        maquina.mudar(Estado.TEMPESTADE);
        cooldownTempestade.disparar();
        atacandoEspecial = true;
    }

    // A tempestade aplica a restrição continuamente enquanto o alvo permanece na zona telegrafada.
    private void processarTempestade(float delta) {
        maquina.avancar(delta);
        if (playerNaZona(RAIO_TEMPESTADE)) {
            player.aplicarRestricaoMovimento(MULTIPLICADOR_TEMPESTADE, 0.2f);
        } else {
            player.removerRestricaoMovimento();
        }
        if (maquina.getTimer() < DURACAO_TEMPESTADE) return;
        player.removerRestricaoMovimento();
        iniciarRecuperacao();
    }

    private void iniciarEscudo() {
        if (!maquina.is(Estado.APROXIMACAO) || !cooldownEscudo.pronto()) return;
        maquina.mudar(Estado.ESCUDO);
        cooldownEscudo.disparar();
        moving = false;
        atacandoEspecial = true;
    }

    private void processarEscudo(float delta) {
        maquina.avancar(delta);
        if (maquina.getTimer() >= DURACAO_ESCUDO) iniciarRecuperacao();
    }

    private void iniciarAtaqueFinal() {
        if (!maquina.is(Estado.APROXIMACAO) || !cooldownAtaqueFinal.pronto()) return;
        capturarZonaDoPlayer();
        maquina.mudar(Estado.AVISO_ATAQUE_FINAL);
        ataqueFinal.iniciarAviso();
        moving = false;
    }

    private void processarAvisoAtaqueFinal(float delta) {
        maquina.avancar(delta);
        if (!ataqueFinal.avisoConcluido(maquina.getTimer())) return;
        maquina.mudar(Estado.ATAQUE_FINAL);
        ataqueFinal.aplicarUmaVez(() -> {
            if (playerNaZona(RAIO_ATAQUE_FINAL)) {
                player.dano(DANO_ATAQUE_FINAL);
            }
        });
        cooldownAtaqueFinal.disparar();
        atacandoEspecial = true;
    }

    private void invocarGuardioes() {
        if (!maquina.is(Estado.APROXIMACAO) || !cooldownInvocacao.pronto()
            || invocacoes.quantidadeVivas() >= MAX_INVOCACOES) return;
        int faltantes = MAX_INVOCACOES - invocacoes.quantidadeVivas();
        for (int i = 0; i < faltantes; i++) {
            invocacoes.registrar(this, criarInvocacao(i));
        }
        cooldownInvocacao.disparar();
        maquina.mudar(Estado.INVOCACAO);
        moving = false;
        atacandoEspecial = true;
    }

    protected EnemyTemplate criarInvocacao(int indice) {
        int deslocamentoX = indice == 0 ? -70 : 70;
        return new Skeleton(Math.round(dx) + deslocamentoX, Math.round(dy) - 40, false, player);
    }

    private void atualizarProjeteis(float delta) {
        Iterator<ProjetilFarao> iterator = projeteis.iterator();
        Rectangle hitboxPlayer = player.getHitBox();
        while (iterator.hasNext()) {
            ProjetilFarao projetil = iterator.next();
            projetil.atualizar(delta);
            if (!projetil.atingiu && projetil.hitbox.overlaps(hitboxPlayer)) {
                player.dano(DANO_PROJETIL);
                projetil.atingiu = true;
            }
            if (projetil.atingiu || projetil.tempoVida >= DURACAO_PROJETIL) iterator.remove();
        }
    }

    private void capturarZonaDoPlayer() {
        Rectangle alvo = player.getHitBox();
        zona.marcar(alvo.x + alvo.width / 2f, alvo.y + alvo.height / 2f);
    }

    private boolean playerNaZona(float raio) {
        return zona.contemCentro(player.getHitBox(), raio);
    }

    private float centroX() { return hitBox.x + hitBox.width / 2f; }
    private float centroY() { return hitBox.y + hitBox.height / 2f; }

    private void processarRecuperacao(float delta) {
        maquina.avancar(delta);
        if (maquina.getTimer() < RECUPERACAO) return;
        maquina.mudar(Estado.APROXIMACAO);
        atacando = false;
        atacandoEspecial = false;
    }

    private void iniciarRecuperacao() {
        maquina.mudar(Estado.RECUPERACAO);
    }

    // Cancela o ataque em andamento: sequência e janelas fecham, sem dano tardio.
    private void cancelarAtaque(Estado novoEstado) {
        maquina.mudar(novoEstado);
        sequenciaSalva.cancelar();
        tempestade.cancelar();
        ataqueFinal.cancelar();
        atacando = false;
        atacandoEspecial = false;
        moving = false;
    }

    private void limparEfeitos() {
        if (player != null) player.removerRestricaoMovimento();
    }

    @Override
    protected void aoTransicionarFase(int novaFase) {
        limparEfeitos();
        projeteis.clear();
        cancelarAtaque(Estado.TRANSICAO);
    }

    @Override
    protected void aposTransicionarFase(int novaFase) {
        if (!emTransicao) maquina.mudar(Estado.APROXIMACAO);
    }

    @Override
    protected void aoMorrer() {
        cancelarAtaque(Estado.MORTE);
        limparEfeitos();
        projeteis.clear();
        invocacoes.cancelarPendentes();
    }

    @Override
    public boolean receberDano(int forca) {
        if (maquina.is(Estado.ESCUDO)) return false;
        return super.receberDano(forca);
    }

    @Override
    public void drenarInvocacoes(List<EnemyTemplate> destino) {
        invocacoes.drenar(destino);
    }

    @Override public void andarIA(float delta) { mover.mover(this, delta); }
    @Override public int getTamanho() { return TAMANHO; }
    @Override public EnemyTemplate cloneEnemy() { return new FaraoAreia(Math.round(dx), Math.round(dy), forte, player); }
    @Override protected int moedasMorte() { return 100; }

    @Override
    public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (shapeRenderer.isDrawing()) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (maquina.is(Estado.AVISO_TEMPESTADE) || maquina.is(Estado.TEMPESTADE)) {
            shapeRenderer.setColor(maquina.is(Estado.TEMPESTADE) ? Color.TAN : Color.ORANGE);
            shapeRenderer.circle(zona.getX(), zona.getY(), RAIO_TEMPESTADE);
        } else if (maquina.is(Estado.AVISO_ATAQUE_FINAL) || maquina.is(Estado.ATAQUE_FINAL)) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(zona.getX(), zona.getY(), RAIO_ATAQUE_FINAL);
        } else if (maquina.is(Estado.ESCUDO)) {
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.circle(centroX(), centroY(), HITBOX * 0.8f);
        }
        shapeRenderer.setColor(Color.GOLD);
        for (ProjetilFarao projetil : projeteis) {
            shapeRenderer.circle(projetil.hitbox.x + projetil.hitbox.width / 2f,
                projetil.hitbox.y + projetil.hitbox.height / 2f, TAMANHO_PROJETIL / 2f);
        }
        shapeRenderer.end();
    }

    public Estado getEstado() { return maquina.getEstado(); }
    public int getQuantidadeProjeteisAtivos() { return projeteis.size(); }
    public int getQuantidadeInvocacoesAtivas() { return invocacoes.quantidadeVivas(); }
    public boolean isEscudoAtivo() { return maquina.is(Estado.ESCUDO); }
    public float getZonaX() { return zona.getX(); }
    public float getZonaY() { return zona.getY(); }

    private static class ProjetilFarao {
        private final Rectangle hitbox;
        private final float direcaoX;
        private final float direcaoY;
        private float tempoVida;
        private boolean atingiu;

        ProjetilFarao(float centroX, float centroY, float direcaoX, float direcaoY) {
            this.hitbox = new Rectangle(centroX - TAMANHO_PROJETIL / 2f,
                centroY - TAMANHO_PROJETIL / 2f, TAMANHO_PROJETIL, TAMANHO_PROJETIL);
            this.direcaoX = direcaoX;
            this.direcaoY = direcaoY;
        }

        void atualizar(float delta) {
            tempoVida += delta;
            hitbox.x += direcaoX * VELOCIDADE_PROJETIL * delta;
            hitbox.y += direcaoY * VELOCIDADE_PROJETIL * delta;
        }
    }

    private static class FaseSenhorDasAreias extends FaseBoss {
        FaseSenhorDasAreias(Animation<TextureRegion> ataque) {
            super("Senhor das Areias", LIMIAR_FASE_2);
            animacaoAtaque = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((FaraoAreia) boss).iniciarSalva(); }
        @Override public void executarAtaqueEspecial(BossTemplate boss) { }
    }

    private static class FaseTempestadeReal extends FaseBoss {
        FaseTempestadeReal(Animation<TextureRegion> ataque) {
            super("Tempestade Real", LIMIAR_FASE_3);
            animacaoAtaque = ataque;
            animacaoDefesa = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((FaraoAreia) boss).iniciarSalva(); }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) {
            FaraoAreia farao = (FaraoAreia) boss;
            if (farao.cooldownTempestade.pronto()) farao.iniciarTempestade();
            if (farao.maquina.is(Estado.APROXIMACAO) && farao.cooldownEscudo.pronto()) farao.iniciarEscudo();
        }
    }

    private static class FaseJulgamentoDoFarao extends FaseBoss {
        FaseJulgamentoDoFarao(Animation<TextureRegion> ataque) {
            super("Julgamento do Faraó", 0f);
            animacaoAtaque = ataque;
            animacaoDefesa = ataque;
        }

        @Override public void executarAtaqueNormal(BossTemplate boss) { ((FaraoAreia) boss).iniciarSalva(); }

        @Override
        public void executarAtaqueEspecial(BossTemplate boss) {
            FaraoAreia farao = (FaraoAreia) boss;
            if (farao.cooldownInvocacao.pronto() && farao.invocacoes.quantidadeVivas() < MAX_INVOCACOES) {
                farao.invocarGuardioes();
                return;
            }
            if (farao.cooldownAtaqueFinal.pronto()) farao.iniciarAtaqueFinal();
        }
    }
}
