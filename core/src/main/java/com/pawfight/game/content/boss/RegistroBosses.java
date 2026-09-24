package com.pawfight.game.content.boss;

import com.pawfight.game.content.RegistroConteudo;
import com.pawfight.game.content.enemy.DefinicaoInimigo;
import com.pawfight.game.entity.bosses.EscorpiaoAreia;
import com.pawfight.game.entity.bosses.FaraoAreia;
import com.pawfight.game.entity.bosses.MumiaAreia;
import com.pawfight.game.entity.bosses.ReiEsqueleto;

import java.util.List;

public final class RegistroBosses {
    private static final RegistroConteudo<DefinicaoInimigo> REGISTRO = criar();

    private RegistroBosses() { }

    private static RegistroConteudo<DefinicaoInimigo> criar() {
        RegistroConteudo<DefinicaoInimigo> registro = new RegistroConteudo<>("boss");
        registro.registrar("escorpiao_areia", new DefinicaoInimigo("escorpiao_areia", "Escorpião da Areia", 128,
            c -> new EscorpiaoAreia(c.x(), c.y(), c.forte(), c.player())));
        registro.registrar("mumia_areia", new DefinicaoInimigo("mumia_areia", "Múmia da Areia", 128,
            c -> new MumiaAreia(c.x(), c.y(), c.forte(), c.player())));
        registro.registrar("farao_areia", new DefinicaoInimigo("farao_areia", "Faraó da Areia", 128,
            c -> new FaraoAreia(c.x(), c.y(), c.forte(), c.player())));
        registro.registrar("rei_esqueleto", new DefinicaoInimigo("rei_esqueleto", "Rei Esqueleto", 128,
            c -> new ReiEsqueleto(c.x(), c.y(), c.forte(), c.player())));
        return registro;
    }

    public static DefinicaoInimigo obter(String id) { return REGISTRO.obter(id); }
    public static List<DefinicaoInimigo> listar() { return REGISTRO.listar(); }
}
