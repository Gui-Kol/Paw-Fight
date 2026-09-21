package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.entity.enemy.EnemyTemplate;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.tiro.Atirar;
import com.pawfight.game.entity.tiro.TirosTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CombateComponent {

    private static final String TAG = "CombateComponent";

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

    public void updateTiros(float delta) {
        Iterator<TirosTemplate> it = tiros.iterator();
        while (it.hasNext()) {
            TirosTemplate tiro = it.next();
            tiro.update(delta);
            if (tiro.isExpirado()) {
                it.remove();
                tiro.liberar();
                Gdx.app.debug(TAG, "Tiro expirado e removido: " + tiro.getClass().getSimpleName() + " — restam " + tiros.size());
            }
        }
    }


    public void processarTirosAutomaticos(PlayerTemplate player, float delta) {
        if (!podeAtacar || tirosModelos == null || tirosModelos.isEmpty()) return;

        // Guarda global: nenhum tiro é disparado se não houver inimigos na sala
        if (fonteInimigos == null || fonteInimigos.isEmpty()) return;

        atirar.atira(tirosModelos, player, delta, fonteInimigos);
    }

    public void setFonteInimigos(List<EnemyTemplate> inimigos) {
        this.fonteInimigos = inimigos;
    }

    public void adicionarTiro(TirosTemplate tiro) {
        tiros.add(tiro);
        Gdx.app.debug(TAG, "Tiro disparado: " + tiro.getClass().getSimpleName() + " — ativos: " + tiros.size());
    }

    public void removerTiro(TirosTemplate tiro) {
        if (tiros.remove(tiro)) {
            Gdx.app.debug(TAG, "Tiro removido: " + tiro.getClass().getSimpleName() + " — restam " + tiros.size());
        }
    }

    public void clearTiros() {
        for (TirosTemplate tiro : tiros) {
            tiro.liberar();
        }
        tiros.clear();
    }

    public List<TirosTemplate> getTiros() { return tiros; }
    public List<TirosTemplate> getTirosModelos() { return tirosModelos; }
    public boolean isPodeAtacar() { return podeAtacar; }
    public void setPodeAtacar(boolean podeAtacar) { this.podeAtacar = podeAtacar; }
}

