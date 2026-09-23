package com.pawfight.game.entity.component;

import com.badlogic.ashley.core.Component;
import com.pawfight.game.entity.Entidade;

/** Ponte temporária entre a entidade Ashley e o objeto de domínio legado. */
public class ReferenciaEntidadeComponent implements Component {
    private final Entidade entidade;

    public ReferenciaEntidadeComponent(Entidade entidade) {
        this.entidade = entidade;
    }

    public Entidade getEntidade() {
        return entidade;
    }
}
