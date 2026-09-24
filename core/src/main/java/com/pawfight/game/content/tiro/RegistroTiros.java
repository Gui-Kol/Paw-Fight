package com.pawfight.game.content.tiro;

import com.pawfight.game.content.RegistroConteudo;
import com.pawfight.game.entity.tiro.blackbird.TiroGelo;
import com.pawfight.game.entity.tiro.blackcat.TiroFantasmagorico;
import com.pawfight.game.entity.tiro.dove.TiroCoco;
import com.pawfight.game.entity.tiro.dove.TiroSolar;
import com.pawfight.game.entity.tiro.orangecat.TiroSangue;

import java.util.List;

public final class RegistroTiros {
    private static final RegistroConteudo<DefinicaoTiro> REGISTRO = criar();
    private RegistroTiros() { }

    private static RegistroConteudo<DefinicaoTiro> criar() {
        RegistroConteudo<DefinicaoTiro> r = new RegistroConteudo<>("tiro");
        r.registrar("tiro_fantasmagorico", new DefinicaoTiro("tiro_fantasmagorico",
            List.of("entitys/player/ataques/arranhao.png"), 13, 12, 1, TiroFantasmagorico::new));
        r.registrar("tiro_sangue", new DefinicaoTiro("tiro_sangue",
            List.of("effects/sangue/sangue.png"), 1, 12, 1, TiroSangue::new));
        r.registrar("tiro_gelo", new DefinicaoTiro("tiro_gelo",
            List.of("entitys/player/ataques/TiroGelo.png", "entitys/player/ataques/TiroGeloExplodindo.png"), 6, 10, 2, TiroGelo::new));
        r.registrar("tiro_solar", new DefinicaoTiro("tiro_solar",
            List.of("entitys/player/ataques/TiroSolar.png"), 8, 25, 2, TiroSolar::new));
        r.registrar("tiro_coco", new DefinicaoTiro("tiro_coco",
            List.of("entitys/player/dove/coco/1.png", "entitys/player/dove/coco/2.png", "entitys/player/dove/coco/3.png"), 1, 12, 1, TiroCoco::new));
        return r;
    }

    public static DefinicaoTiro obter(String id) { return REGISTRO.obter(id); }
    public static List<DefinicaoTiro> listar() { return REGISTRO.listar(); }
}
