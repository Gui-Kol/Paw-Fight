package com.pawfight.game.content.enemy;

import com.pawfight.game.content.RegistroConteudo;
import com.pawfight.game.entity.enemy.Skeleton;

import java.util.List;

public final class RegistroInimigos {
    private static final RegistroConteudo<DefinicaoInimigo> REGISTRO = criar();

    private RegistroInimigos() { }

    private static RegistroConteudo<DefinicaoInimigo> criar() {
        RegistroConteudo<DefinicaoInimigo> registro = new RegistroConteudo<>("inimigo");
        registro.registrar("skeleton", new DefinicaoInimigo("skeleton", "Esqueleto", 64,
            contexto -> new Skeleton(contexto.x(), contexto.y(), contexto.forte(), contexto.player())));
        return registro;
    }

    public static DefinicaoInimigo obter(String id) { return REGISTRO.obter(id); }
    public static List<DefinicaoInimigo> listar() { return REGISTRO.listar(); }
}
