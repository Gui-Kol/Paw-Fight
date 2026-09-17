package com.pawfight.game.entity.component;

import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.entity.player.DadosPlayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StatsComponent")
class StatsComponentTest {

    private StatsComponent stats;

    @BeforeAll
    static void initGdx() {
        HeadlessGdx.ensureGdx(); // xpUp() chama Gdx.app.log()
    }

    @BeforeEach
    void setUp() {
        DadosPlayer dados = new DadosPlayer(
            2,      // forca
            0.3f,   // cadenciaTiro
            1.0f,   // duracaoTiro
            10,     // vidaBase
            350,    // velocidade
            64,     // tamanho
            16,     // tamanhoTiro
            25,     // hitboxSize
            0,      // hitboxOffsetY
            -5,     // hitboxOffsetX
            null, null, null, null, // texturas (não usadas nos testes)
            null    // audio
        );
        stats = new StatsComponent(dados);
    }

    // ── Construção ─────────────────────────────────────────────

    @Test
    @DisplayName("Inicializa com stats base, vida cheia, level 1 e XP zero")
    void inicializacaoCorreta() {
        assertEquals(10, stats.getVidaBase());
        assertEquals(10, stats.getVida());
        assertEquals(2, stats.getForca());
        assertEquals(350, stats.getVelocidade());
        assertEquals(1, stats.getLevel());
        assertEquals(0, stats.getXp());
        assertEquals(50, stats.getXpNecessario());
        assertEquals(0, stats.getMoedas());
        assertEquals(0, stats.getPontosDisponiveis());
        assertFalse(stats.isMorto());
        assertFalse(stats.isHurt());
    }

    @Test
    @DisplayName("Construtor simples (inimigos) inicializa com vida cheia")
    void construtorSimples() {
        StatsComponent inimigo = new StatsComponent(20, 3, 200);
        assertEquals(20, inimigo.getVida());
        assertEquals(20, inimigo.getVidaBase());
        assertEquals(3, inimigo.getForca());
        assertFalse(inimigo.isMorto());
    }

    // ── Dano ───────────────────────────────────────────────────

    @Test
    @DisplayName("Dano reduz vida e ativa estado hurt")
    void danoReduzVida() {
        assertTrue(stats.aplicarDano(3));
        assertEquals(7, stats.getVida());
        assertTrue(stats.isHurt());
        assertFalse(stats.isMorto());
    }

    @Test
    @DisplayName("Dano letal marca como morto")
    void danoLetal() {
        stats.aplicarDano(10);
        assertEquals(0, stats.getVida());
        assertTrue(stats.isMorto());
    }

    @Test
    @DisplayName("Dano maior que a vida também mata")
    void danoExcedente() {
        stats.aplicarDano(999);
        assertTrue(stats.isMorto());
        assertTrue(stats.getVida() <= 0);
    }

    @Test
    @DisplayName("Cooldown impede dano consecutivo imediato")
    void cooldownBloqueiaDano() {
        assertTrue(stats.aplicarDano(2));   // primeiro acerta
        assertFalse(stats.aplicarDano(2));  // segundo é bloqueado (cooldown)
        assertEquals(8, stats.getVida());
    }

    @Test
    @DisplayName("Cooldown de dano expira após 0.5s")
    void cooldownExpira() {
        stats.aplicarDano(2);
        stats.updateTimers(0.5f);
        assertTrue(stats.aplicarDano(2), "Deveria tomar dano após o cooldown expirar");
        assertEquals(6, stats.getVida());
    }

    @Test
    @DisplayName("Estado hurt expira após 0.5s")
    void hurtExpira() {
        stats.aplicarDano(1);
        assertTrue(stats.isHurt());
        stats.updateTimers(0.5f);
        assertFalse(stats.isHurt());
    }

    @Test
    @DisplayName("Timers não avançam quando não há hurt nem cooldown ativo")
    void timersInertes() {
        stats.updateTimers(10f);
        assertFalse(stats.isHurt());
        assertEquals(0f, stats.getHurtTime());
    }

    // ── Dano direto (DoT / efeitos de status) ──────────────────

    @Test
    @DisplayName("Dano direto ignora o cooldown e pode ser aplicado em sequência")
    void danoDiretoIgnoraCooldown() {
        assertTrue(stats.aplicarDanoDireto(2));
        assertTrue(stats.aplicarDanoDireto(2));
        assertEquals(6, stats.getVida());
    }

    @Test
    @DisplayName("Dano direto não ativa a animação de hurt")
    void danoDiretoSemHurt() {
        stats.aplicarDanoDireto(2);
        assertFalse(stats.isHurt());
    }

    @Test
    @DisplayName("Dano direto letal marca como morto")
    void danoDiretoLetal() {
        assertTrue(stats.aplicarDanoDireto(999));
        assertTrue(stats.isMorto());
    }

    @Test
    @DisplayName("Dano direto não é aplicado em quem já está morto")
    void danoDiretoEmMorto() {
        stats.aplicarDano(999);
        assertFalse(stats.aplicarDanoDireto(1));
    }

    // ── XP / Level ─────────────────────────────────────────────

    @Test
    @DisplayName("Ganhar XP abaixo do necessário não sobe de nível")
    void xpSemLevelUp() {
        assertEquals(0, stats.xpUp(30));
        assertEquals(1, stats.getLevel());
        assertEquals(30, stats.getXp());
    }

    @Test
    @DisplayName("XP suficiente sobe um nível e concede 1 ponto")
    void levelUpSimples() {
        assertEquals(1, stats.xpUp(50));
        assertEquals(2, stats.getLevel());
        assertEquals(1, stats.getPontosDisponiveis());
        assertEquals(0, stats.getXp(), "XP excedente deveria ser 0");
    }

    @Test
    @DisplayName("XP excedente carrega para o próximo nível")
    void xpExcedente() {
        stats.xpUp(70); // 50 para o level 2, sobram 20
        assertEquals(2, stats.getLevel());
        assertEquals(20, stats.getXp());
    }

    @Test
    @DisplayName("XP suficiente para múltiplos níveis sobe todos de uma vez")
    void multiplosLevelUps() {
        int niveis = stats.xpUp(10000);
        assertTrue(niveis >= 3, "10.000 XP deveria render vários níveis");
        assertEquals(1 + niveis, stats.getLevel());
        assertEquals(niveis, stats.getPontosDisponiveis());
    }

    @Test
    @DisplayName("XP necessário aumenta a cada nível")
    void xpNecessarioEscala() {
        int antes = stats.getXpNecessario();
        stats.xpUp(50);
        assertTrue(stats.getXpNecessario() >= antes,
            "XP necessário não deveria diminuir ao subir de nível");
    }

    // ── Moedas ─────────────────────────────────────────────────

    @Test
    @DisplayName("moedaUp acumula moedas")
    void moedasAcumulam() {
        stats.moedaUp(5);
        stats.moedaUp(3);
        assertEquals(8, stats.getMoedas());
    }

    // ── Upgrades com pontos ────────────────────────────────────

    @Test
    @DisplayName("vidaBaseUp aumenta vida base e restaura vida, gastando pontos")
    void upgradeVida() {
        stats.xpUp(50); // 1 ponto
        stats.setVida(3);
        stats.vidaBaseUp(1);
        assertEquals(11, stats.getVidaBase());
        assertEquals(11, stats.getVida(), "Vida deveria ser restaurada ao novo máximo");
        assertEquals(0, stats.getPontosDisponiveis());
    }

    @Test
    @DisplayName("forcaUp e velocidadeUp aplicam incrementos corretos")
    void upgradeForcaVelocidade() {
        stats.setPontosDisponiveis(2);
        stats.forcaUp(1);
        stats.velocidadeUp(1);
        assertEquals(3, stats.getForca());
        assertEquals(370, stats.getVelocidade());
        assertEquals(0, stats.getPontosDisponiveis());
    }
}
