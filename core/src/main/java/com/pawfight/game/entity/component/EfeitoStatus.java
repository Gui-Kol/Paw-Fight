package com.pawfight.game.entity.component;

public final class EfeitoStatus {
    private final String id;
    private final Object fonte;
    private final PoliticaAcumuloStatus politica;
    private final float intervaloTick;
    private final boolean persistente;
    private float duracaoRestante;
    private float intensidade;
    private int cargas;
    private float acumuladorTick;

    public EfeitoStatus(String id, Object fonte, float duracao, float intensidade, int cargas,
                        float intervaloTick, PoliticaAcumuloStatus politica, boolean persistente) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID do status não pode ser vazio.");
        this.id = id;
        this.fonte = fonte;
        this.duracaoRestante = Math.max(0f, duracao);
        this.intensidade = intensidade;
        this.cargas = Math.max(1, cargas);
        this.intervaloTick = Math.max(0f, intervaloTick);
        this.politica = politica;
        this.persistente = persistente;
    }

    int atualizar(float delta) {
        duracaoRestante -= delta;
        if (intervaloTick <= 0f || duracaoRestante <= 0f) return 0;
        acumuladorTick += delta;
        int ticks = (int) (acumuladorTick / intervaloTick);
        acumuladorTick -= ticks * intervaloTick;
        return ticks;
    }
    void renovar(float duracao) { duracaoRestante = Math.max(duracaoRestante, duracao); acumuladorTick = 0f; }
    void substituirPor(EfeitoStatus outro) { duracaoRestante = outro.duracaoRestante; intensidade = outro.intensidade; cargas = outro.cargas; acumuladorTick = 0f; }
    void acumularIntensidade(EfeitoStatus outro) { intensidade += outro.intensidade; renovar(outro.duracaoRestante); }
    void acumularCargas(EfeitoStatus outro) { cargas += outro.cargas; renovar(outro.duracaoRestante); }

    public String getId() { return id; }
    public Object getFonte() { return fonte; }
    public PoliticaAcumuloStatus getPolitica() { return politica; }
    public float getDuracaoRestante() { return duracaoRestante; }
    public float getIntensidade() { return intensidade; }
    public int getCargas() { return cargas; }
    public boolean isPersistente() { return persistente; }
    public boolean isExpirado() { return duracaoRestante <= 0f; }
}
