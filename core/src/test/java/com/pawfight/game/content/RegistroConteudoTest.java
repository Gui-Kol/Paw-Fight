package com.pawfight.game.content;

import com.pawfight.game.content.player.RegistroPersonagens;
import com.pawfight.game.content.enemy.RegistroInimigos;
import com.pawfight.game.content.boss.RegistroBosses;
import com.pawfight.game.content.tiro.RegistroTiros;
import com.pawfight.game.content.world.RegistroMundos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Registros de conteúdo")
class RegistroConteudoTest {

    @Test
    @DisplayName("Rejeita IDs duplicados na inicialização")
    void rejeitaIdDuplicado() {
        RegistroConteudo<String> registro = new RegistroConteudo<>("teste");
        registro.registrar("mesmo_id", "primeiro");

        assertThrows(IllegalStateException.class, () -> registro.registrar("mesmo_id", "segundo"));
    }

    @Test
    @DisplayName("ID desconhecido produz erro claro")
    void idDesconhecido() {
        RegistroConteudo<String> registro = new RegistroConteudo<>("tiro");

        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
            () -> registro.obter("inexistente"));
        assertEquals("tiro desconhecido: inexistente", erro.getMessage());
    }

    @Test
    @DisplayName("Todos os personagens padrão possuem IDs únicos e aparecem no registro")
    void personagensPadraoRegistrados() {
        Set<String> ids = RegistroPersonagens.listar().stream()
            .map(definicao -> definicao.id())
            .collect(Collectors.toSet());

        assertEquals(4, RegistroPersonagens.listar().size());
        assertEquals(Set.of("black_cat", "orange_cat", "black_bird", "dove"), ids);
    }

    @Test
    @DisplayName("Catálogos padrão expõem todo conteúdo existente por ID estável")
    void catalogosPadraoCompletos() {
        assertEquals(1, RegistroInimigos.listar().size());
        assertEquals(4, RegistroBosses.listar().size());
        assertEquals(5, RegistroTiros.listar().size());
        assertEquals(1, RegistroMundos.listar().size());
        assertEquals("Mundo Areia", RegistroMundos.obter("mundo_areia").nomeExibido());
    }

    @Test
    @DisplayName("Definições de tiro possuem configuração válida")
    void tirosComConfiguracaoValida() {
        RegistroTiros.listar().forEach(tiro -> {
            org.junit.jupiter.api.Assertions.assertFalse(tiro.assets().isEmpty(), tiro.id());
            org.junit.jupiter.api.Assertions.assertTrue(tiro.frames() > 0, tiro.id());
            org.junit.jupiter.api.Assertions.assertTrue(tiro.framesPorSegundo() > 0, tiro.id());
            org.junit.jupiter.api.Assertions.assertTrue(tiro.quantidadePadrao() > 0, tiro.id());
        });
    }
}
