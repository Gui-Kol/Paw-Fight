package com.pawfight.game.content.player;

import com.pawfight.game.content.RegistroConteudo;
import com.pawfight.game.entity.player.BlackBird;
import com.pawfight.game.entity.player.BlackCat;
import com.pawfight.game.entity.player.Dove;
import com.pawfight.game.entity.player.OrangeCat;

import java.util.List;

public final class RegistroPersonagens {
    private static final RegistroConteudo<DefinicaoPlayer> REGISTRO = criarRegistroPadrao();

    private RegistroPersonagens() { }

    private static RegistroConteudo<DefinicaoPlayer> criarRegistroPadrao() {
        RegistroConteudo<DefinicaoPlayer> registro = new RegistroConteudo<>("personagem");
        registro.registrar("black_cat", new DefinicaoPlayer("black_cat", "Black Cat",
            "entitys/player/selecao/black_cat.png", contexto -> new BlackCat(
                contexto.x(), contexto.y(), contexto.larguraTile(), contexto.quantidadeTilesX(),
                contexto.alturaTile(), contexto.quantidadeTilesY(), contexto.zoomCamera())));
        registro.registrar("orange_cat", new DefinicaoPlayer("orange_cat", "Orange Cat",
            "entitys/player/selecao/orange_cat.png", contexto -> new OrangeCat(
                contexto.x(), contexto.y(), contexto.larguraTile(), contexto.quantidadeTilesX(),
                contexto.alturaTile(), contexto.quantidadeTilesY(), contexto.zoomCamera())));
        registro.registrar("black_bird", new DefinicaoPlayer("black_bird", "Black Bird",
            "entitys/player/selecao/black_bird.png", contexto -> new BlackBird(
                contexto.x(), contexto.y(), contexto.larguraTile(), contexto.quantidadeTilesX(),
                contexto.alturaTile(), contexto.quantidadeTilesY(), contexto.zoomCamera())));
        registro.registrar("dove", new DefinicaoPlayer("dove", "Dove",
            "entitys/player/selecao/blue_bird.png", contexto -> new Dove(
                contexto.x(), contexto.y(), contexto.larguraTile(), contexto.quantidadeTilesX(),
                contexto.alturaTile(), contexto.quantidadeTilesY(), contexto.zoomCamera())));
        return registro;
    }

    public static List<DefinicaoPlayer> listar() { return REGISTRO.listar(); }
    public static DefinicaoPlayer obter(String id) { return REGISTRO.obter(id); }
}
