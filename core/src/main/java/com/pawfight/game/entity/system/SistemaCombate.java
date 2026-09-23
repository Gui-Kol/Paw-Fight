package com.pawfight.game.entity.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.pawfight.game.entity.Entidade;
import com.pawfight.game.entity.component.CombateComponent;
import com.pawfight.game.entity.component.ReferenciaEntidadeComponent;
import com.pawfight.game.entity.component.StatsComponent;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.TirosTemplate;

import java.util.Iterator;
import java.util.List;

public class SistemaCombate extends IteratingSystem {
    private static final ComponentMapper<CombateComponent> COMBATE = ComponentMapper.getFor(CombateComponent.class);
    private static final ComponentMapper<ReferenciaEntidadeComponent> REFERENCIA =
        ComponentMapper.getFor(ReferenciaEntidadeComponent.class);
    private static final ComponentMapper<StatsComponent> STATS = ComponentMapper.getFor(StatsComponent.class);

    public SistemaCombate() {
        super(Family.all(CombateComponent.class, ReferenciaEntidadeComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entidade, float deltaTime) {
        CombateComponent combate = COMBATE.get(entidade);
        Entidade referencia = REFERENCIA.get(entidade).getEntidade();

        if (referencia instanceof PlayerTemplate player) {
            StatsComponent stats = STATS.get(entidade);
            if ((stats == null || !stats.isMorto()) && combate.isPodeAtacar()
                && !combate.getTirosModelos().isEmpty() && possuiAlvoValido(combate.getFonteInimigos())) {
                combate.getAtirar().atira(combate.getTirosModelos(), player, deltaTime, combate.getFonteInimigos());
            }
        }

        Iterator<TirosTemplate> tiros = combate.getTiros().iterator();
        while (tiros.hasNext()) {
            TirosTemplate tiro = tiros.next();
            tiro.update(deltaTime);
            if (tiro.isExpirado()) {
                tiros.remove();
                tiro.liberar();
            }
        }
    }

    private boolean possuiAlvoValido(List<EnemyTemplate> inimigos) {
        if (inimigos == null || inimigos.isEmpty()) return false;
        for (EnemyTemplate inimigo : inimigos) {
            if (inimigo != null && !inimigo.isMorto()) return true;
        }
        return false;
    }
}
