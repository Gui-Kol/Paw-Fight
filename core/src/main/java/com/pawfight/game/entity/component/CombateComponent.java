package com.pawfight.game.entity.component;

import com.badlogic.ashley.core.Component;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.tiro.Atirar;
import com.pawfight.game.entity.tiro.TirosTemplate;

import java.util.ArrayList;
import java.util.List;

public class CombateComponent implements Component {

    private final List<TirosTemplate> tiros = new ArrayList<>();
    private final List<TirosTemplate> tirosModelos = new ArrayList<>();
    private final Atirar atirar = new Atirar();
    private boolean podeAtacar = false;

    // Inimigos vivos da sala atual (injetados pelo mundo via PlayerTemplate).
    private List<EnemyTemplate> fonteInimigos;

    public void addModeloTiro(TirosTemplate modelo) {
        if (modelo != null) {
            tirosModelos.add(modelo);
        }
    }

    public void setFonteInimigos(List<EnemyTemplate> inimigos) {
        this.fonteInimigos = inimigos;
    }

    public void adicionarTiro(TirosTemplate tiro) {
        tiros.add(tiro);
    }

    public void removerTiro(TirosTemplate tiro) {
        tiros.remove(tiro);
    }

    public void clearTiros() {
        for (TirosTemplate tiro : tiros) {
            tiro.liberar();
        }
        tiros.clear();
    }

    public List<TirosTemplate> getTiros() { return tiros; }
    public List<TirosTemplate> getTirosModelos() { return tirosModelos; }
    public Atirar getAtirar() { return atirar; }
    public List<EnemyTemplate> getFonteInimigos() { return fonteInimigos; }
    public boolean isPodeAtacar() { return podeAtacar; }
    public void setPodeAtacar(boolean podeAtacar) { this.podeAtacar = podeAtacar; }
}
