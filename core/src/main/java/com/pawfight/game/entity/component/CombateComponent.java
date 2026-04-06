package com.pawfight.game.entity.component;

import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.Atirar;
import com.pawfight.game.entity.tiro.TirosTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Componente responsável pelo combate.
 * Gerencia tiros (projéteis), modelos de tiro e cadência de disparo.
 */
public class CombateComponent {

    private final List<TirosTemplate> tiros = new ArrayList<>();
    private final List<TirosTemplate> tirosModelos = new ArrayList<>();
    private final Atirar atirar = new Atirar();
    private boolean podeAtacar = false;

    // ── Modelos de tiro ────────────────────────────────────────

    public void addModeloTiro(TirosTemplate modelo) {
        if (modelo != null) {
            tirosModelos.add(modelo);
        }
    }

    // ── Update (chamado todo frame) ────────────────────────────

    public void updateTiros(float delta) {
        Iterator<TirosTemplate> it = tiros.iterator();
        while (it.hasNext()) {
            TirosTemplate tiro = it.next();
            tiro.update(delta);
            if (tiro.isExpirado()) {
                it.remove();
                tiro.dispose();
            }
        }
    }

    /**
     * Dispara tiros automáticos se podeAtacar e houver modelos.
     */
    public void processarTirosAutomaticos(PlayerTemplate player, float delta) {
        if (!podeAtacar || tirosModelos == null || tirosModelos.isEmpty()) return;
        atirar.atira(tirosModelos, player, delta);
    }

    // ── Gerenciamento de tiros ativos ──────────────────────────

    public void adicionarTiro(TirosTemplate tiro) {
        tiros.add(tiro);
    }

    public void removerTiro(TirosTemplate tiro) {
        tiros.remove(tiro);
    }

    public void clearTiros() {
        tiros.clear();
    }

    // ── Getters / Setters ──────────────────────────────────────

    public List<TirosTemplate> getTiros() { return tiros; }
    public List<TirosTemplate> getTirosModelos() { return tirosModelos; }
    public boolean isPodeAtacar() { return podeAtacar; }
    public void setPodeAtacar(boolean podeAtacar) { this.podeAtacar = podeAtacar; }
}

