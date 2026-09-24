package com.pawfight.game.entity.component;

import com.badlogic.ashley.core.Component;
import com.pawfight.game.entity.Entidade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatusComponent implements Component {
    public static final float TICK_QUEIMADURA = 0.5f;
    private static final Object FONTE_LEGADA = new Object();
    private final List<EfeitoStatus> efeitos = new ArrayList<>();
    private Entidade alvo;

    public void setAlvo(Entidade alvo) { this.alvo = alvo; }

    public boolean aplicar(EfeitoStatus novo) {
        EfeitoStatus atual = localizar(novo);
        if (atual == null) { efeitos.add(novo); return true; }
        switch (novo.getPolitica()) {
            case IMUNIDADE -> { return false; }
            case RENOVAR_DURACAO, UNICO_POR_FONTE -> atual.renovar(novo.getDuracaoRestante());
            case MANTER_MAIS_FORTE -> {
                if (novo.getIntensidade() < atual.getIntensidade()) atual.substituirPor(novo);
                else atual.renovar(novo.getDuracaoRestante());
            }
            case ACUMULAR_INTENSIDADE -> atual.acumularIntensidade(novo);
            case ACUMULAR_CARGAS -> atual.acumularCargas(novo);
            case SUBSTITUIR -> atual.substituirPor(novo);
        }
        return true;
    }

    private EfeitoStatus localizar(EfeitoStatus novo) {
        for (EfeitoStatus efeito : efeitos) {
            if (!efeito.getId().equals(novo.getId())) continue;
            if (novo.getPolitica() != PoliticaAcumuloStatus.UNICO_POR_FONTE || efeito.getFonte() == novo.getFonte()) return efeito;
        }
        return null;
    }

    public boolean aplicarQueimadura(int danoPorTick, float duracao, float chance) {
        if (chance <= 0f || (chance < 1f && Math.random() > chance)) return false;
        return aplicar(new EfeitoStatus("queimadura", FONTE_LEGADA, duracao, Math.max(0, danoPorTick), 1,
            TICK_QUEIMADURA, PoliticaAcumuloStatus.SUBSTITUIR, false));
    }

    public boolean aplicarLentidao(float multiplicador, float duracao, float chance) {
        if (chance <= 0f || (chance < 1f && Math.random() > chance)) return false;
        float valor = Math.max(0.1f, Math.min(1f, multiplicador));
        return aplicar(new EfeitoStatus("lentidao", FONTE_LEGADA, duracao, valor, 1, 0f,
            PoliticaAcumuloStatus.MANTER_MAIS_FORTE, false));
    }

    public int update(float delta) {
        int dano = 0;
        Iterator<EfeitoStatus> iterator = efeitos.iterator();
        while (iterator.hasNext()) {
            EfeitoStatus efeito = iterator.next();
            int ticks = efeito.atualizar(delta);
            if ("queimadura".equals(efeito.getId())) dano += Math.round(efeito.getIntensidade()) * efeito.getCargas() * ticks;
            if (efeito.isExpirado()) iterator.remove();
        }
        return dano;
    }

    public Entidade getAlvo() { return alvo; }
    public float getMultiplicadorVelocidade() {
        float multiplicador = 1f;
        for (EfeitoStatus efeito : efeitos) if ("lentidao".equals(efeito.getId())) multiplicador = Math.min(multiplicador, efeito.getIntensidade());
        return multiplicador;
    }
    public boolean isQueimando() { return possui("queimadura"); }
    public boolean isLento() { return possui("lentidao"); }
    public float getMultiplicadorLentidao() { return getMultiplicadorVelocidade(); }
    public boolean possui(String id) { return efeitos.stream().anyMatch(e -> e.getId().equals(id)); }
    public List<EfeitoStatus> getEfeitos() { return List.copyOf(efeitos); }
    public void remover(String id) { efeitos.removeIf(e -> e.getId().equals(id)); }
    public void limparNaoPersistentes() { efeitos.removeIf(e -> !e.isPersistente()); }
    public void limpar() { efeitos.clear(); }
}
