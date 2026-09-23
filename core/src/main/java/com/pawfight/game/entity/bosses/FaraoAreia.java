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
    private final List<ProjetilFarao> projeteis = new ArrayList<>(QUANTIDADE_PROJETEIS * 2);
    private final List<EnemyTemplate> invocacoesAtivas = new ArrayList<>(MAX_INVOCACOES);
    private final List<EnemyTemplate> invocacoesPendentes = new ArrayList<>(MAX_INVOCACOES);

    private Estado estado = Estado.APROXIMACAO;
    private float timerEstado;
    private float cooldownSalva;
    private float cooldownTempestade;
    private float cooldownEscudo;
    private float cooldownInvocacao = COOLDOWN_INVOCACAO;
    private float cooldownAtaqueFinal;
    private float intervaloProximoProjetil;
    private int projeteisRestantes;
    private float direcaoSalvaX = 1f;
    private float direcaoSalvaY;
    private float zonaX;
    private float zonaY;
    private boolean danoFinalAplicado;

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
    public void update(float delta) {
        if (player != null && player.isPause()) return;
        if (!stats.isMorto()) sincronizarFaseComVida();
        super.update(delta);
        if (stats.isMorto()) encerrarCombate();
    }

    @Override
    public void executarIA(float delta) {
        if (stats.isMorto() || player == null || player.isMorto()) {
            moving = false;
            limparEfeitos();
            return;
        }
        atualizarProjeteis(delta);
        if (estado == Estado.TRANSICAO && !emTransicao) estado = Estado.APROXIMACAO;
        if (emTransicao || estado == Estado.TRANSICAO) return;

        cooldownSalva = Math.max(0f, cooldownSalva - delta);
        cooldownTempestade = Math.max(0f, cooldownTempestade - delta);
        cooldownEscudo = Math.max(0f, cooldownEscudo - delta);
        cooldownInvocacao = Math.max(0f, cooldownInvocacao - delta);
        cooldownAtaqueFinal = Math.max(0f, cooldownAtaqueFinal - delta);
        limparInvocacoesMortas();

        switch (estado) {
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
        if (estado != Estado.APROXIMACAO) return;
        if (cooldownSalva <= 0f) {
            executarAtaqueNormal();
            return;
        }
        mover.mover(this, delta);
    }

    private void iniciarSalva() {
        if (estado != Estado.APROXIMACAO || cooldownSalva > 0f) return;
        estado = Estado.SALVA_PROJETEIS;
        projeteisRestantes = QUANTIDADE_PROJETEIS;
        intervaloProximoProjetil = 0f;
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
        intervaloProximoProjetil -= delta;
        while (projeteisRestantes > 0 && intervaloProximoProjetil <= 0f) {
            projeteis.add(new ProjetilFarao(centroX(), centroY(), direcaoSalvaX, direcaoSalvaY));
            projeteisRestantes--;
            intervaloProximoProjetil += INTERVALO_PROJETEIS;
        }
        if (projeteisRestantes == 0) {
            cooldownSalva = COOLDOWN_SALVA;
            iniciarRecuperacao();
        }
    }

    private void iniciarTempestade() {
        if (estado != Estado.APROXIMACAO || cooldownTempestade > 0f) return;
        capturarZonaDoPlayer();
        estado = Estado.AVISO_TEMPESTADE;
        timerEstado = 0f;
        moving = false;
    }

    private void processarAvisoTempestade(float delta) {
        timerEstado += delta;
        if (timerEstado < ANTECIPACAO_TEMPESTADE) return;
        estado = Estado.TEMPESTADE;
        timerEstado = 0f;
        cooldownTempestade = COOLDOWN_TEMPESTADE;
        atacandoEspecial = true;
    }

    private void processarTempestade(float delta) {
        timerEstado += delta;
        if (playerNaZona(RAIO_TEMPESTADE)) {
            player.aplicarRestricaoMovimento(MULTIPLICADOR_TEMPESTADE, 0.2f);
        } else {
            player.removerRestricaoMovimento();
        }
        if (timerEstado < DURACAO_TEMPESTADE) return;
        player.removerRestricaoMovimento();
        iniciarRecuperacao();
    }

    private void iniciarEscudo() {
        if (estado != Estado.APROXIMACAO || cooldownEscudo > 0f) return;
        estado = Estado.ESCUDO;
        timerEstado = 0f;
        cooldownEscudo = COOLDOWN_ESCUDO;
        moving = false;
        atacandoEspecial = true;
    }

    private void processarEscudo(float delta) {
        timerEstado += delta;
        if (timerEstado >= DURACAO_ESCUDO) iniciarRecuperacao();
    }

    private void iniciarAtaqueFinal() {
        if (estado != Estado.APROXIMACAO || cooldownAtaqueFinal > 0f) return;
        capturarZonaDoPlayer();
        estado = Estado.AVISO_ATAQUE_FINAL;
        timerEstado = 0f;
        danoFinalAplicado = false;
        moving = false;
    }

    private void processarAvisoAtaqueFinal(float delta) {
        timerEstado += delta;
        if (timerEstado < ANTECIPACAO_ATAQUE_FINAL) return;
        estado = Estado.ATAQUE_FINAL;
        if (!danoFinalAplicado && playerNaZona(RAIO_ATAQUE_FINAL)) {
            player.dano(DANO_ATAQUE_FINAL);
            danoFinalAplicado = true;
        }
        cooldownAtaqueFinal = COOLDOWN_ATAQUE_FINAL;
        atacandoEspecial = true;
    }

    private void invocarGuardioes() {
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
        estado = Estado.INVOCACAO;
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
        zonaX = alvo.x + alvo.width / 2f;
        zonaY = alvo.y + alvo.height / 2f;
    }

    private boolean playerNaZona(float raio) {
        Rectangle alvo = player.getHitBox();
        float deltaX = alvo.x + alvo.width / 2f - zonaX;
        float deltaY = alvo.y + alvo.height / 2f - zonaY;
        return deltaX * deltaX + deltaY * deltaY <= raio * raio;
    }

    private float centroX() { return hitBox.x + hitBox.width / 2f; }
    private float centroY() { return hitBox.y + hitBox.height / 2f; }

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
        projeteisRestantes = 0;
        danoFinalAplicado = false;
        atacando = false;
        atacandoEspecial = false;
        moving = false;
    }

    private void sincronizarFaseComVida() {
        while (faseAtual < fases.size() - 1 && faseVigente().deveTransicionar(stats.getVida(), stats.getVidaBase())) {
            mudarFase(faseAtual + 1);
        }
    }

    private void limparEfeitos() {
        if (player != null) player.removerRestricaoMovimento();
    }

    private void encerrarCombate() {
        cancelarAtaque(Estado.MORTE);
        limparEfeitos();
        projeteis.clear();
        invocacoesPendentes.clear();
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
    public boolean receberDano(int forca) {
        if (estado == Estado.ESCUDO) return false;
        return super.receberDano(forca);
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
        limparEfeitos();
        projeteis.clear();
        cancelarAtaque(Estado.TRANSICAO);
        aplicarAnimacoesFase(fases.get(novaFase));
        iniciarTransicao();
        if (!emTransicao) estado = Estado.APROXIMACAO;
    }

    @Override public void atualizarEstado() { sincronizarFaseComVida(); }
    @Override public void andarIA(float delta) { mover.mover(this, delta); }
    @Override public int getTamanho() { return TAMANHO; }
    @Override public EnemyTemplate cloneEnemy() { return new FaraoAreia(Math.round(dx), Math.round(dy), forte, player); }
    @Override protected int moedasMorte() { return 100; }

    @Override
    public void extraDraw(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (shapeRenderer.isDrawing()) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (estado == Estado.AVISO_TEMPESTADE || estado == Estado.TEMPESTADE) {
            shapeRenderer.setColor(estado == Estado.TEMPESTADE ? Color.TAN : Color.ORANGE);
            shapeRenderer.circle(zonaX, zonaY, RAIO_TEMPESTADE);
        } else if (estado == Estado.AVISO_ATAQUE_FINAL || estado == Estado.ATAQUE_FINAL) {
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(zonaX, zonaY, RAIO_ATAQUE_FINAL);
        } else if (estado == Estado.ESCUDO) {
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

    public Estado getEstado() { return estado; }
    public int getQuantidadeProjeteisAtivos() { return projeteis.size(); }
    public int getQuantidadeInvocacoesAtivas() { return quantidadeInvocacoesVivas(); }
    public boolean isEscudoAtivo() { return estado == Estado.ESCUDO; }
    public float getZonaX() { return zonaX; }
    public float getZonaY() { return zonaY; }

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
            if (farao.cooldownTempestade <= 0f) farao.iniciarTempestade();
            if (farao.estado == Estado.APROXIMACAO && farao.cooldownEscudo <= 0f) farao.iniciarEscudo();
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
            if (farao.cooldownInvocacao <= 0f && farao.quantidadeInvocacoesVivas() < MAX_INVOCACOES) {
                farao.invocarGuardioes();
                return;
            }
            if (farao.cooldownAtaqueFinal <= 0f) farao.iniciarAtaqueFinal();
        }
    }
}
