package com.pawfight.game.content.world;

import com.pawfight.game.content.RegistroConteudo;
import com.pawfight.game.world.MundoAreia;

import java.util.List;

public final class RegistroMundos {
    private static final RegistroConteudo<DefinicaoMundo> REGISTRO = criar();
    private RegistroMundos() { }

    private static RegistroConteudo<DefinicaoMundo> criar() {
        RegistroConteudo<DefinicaoMundo> r = new RegistroConteudo<>("mundo");
        r.registrar("mundo_areia", new DefinicaoMundo("mundo_areia", "Mundo Areia", MundoAreia::new));
        return r;
    }

    public static DefinicaoMundo obter(String id) { return REGISTRO.obter(id); }
    public static List<DefinicaoMundo> listar() { return REGISTRO.listar(); }
}
