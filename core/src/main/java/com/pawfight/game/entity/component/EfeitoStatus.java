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
        // UNICO_POR_FONTE compara a fonte por identidade (==): uma instância de origem precisa existir
        // para que "mesmo atacante" seja reconhecido — fonte nula trataria qualquer efeito como igual.
        if (politica == PoliticaAcumuloStatus.UNICO_POR_FONTE && fonte == null) {
            throw new IllegalArgumentException("A política UNICO_POR_FONTE exige uma fonte não nula.");
        }
        this.id = id;
        this.fonte = fonte;
        this.duracaoRestante = Math.max(0f, duracao);
        this.intensidade = intensidade;
        this.cargas = Math.max(1, cargas);
        this.intervaloTick = Math.max(0f, intervaloTick);
        this.politica = politica;
        this.persistente = persistente;
    }

    // Avança a duração e retorna os ticks devidos neste frame. Ticks são contados somente pelo
    // tempo em que o efeito esteve vivo — inclusive quando o mesmo frame também o faz expirar.
    int atualizar(float delta) {
        int ticks = 0;
        if (intervaloTick > 0f) {
            float tempoVivo = Math.min(delta, Math.max(0f, duracaoRestante));
            acumuladorTick += tempoVivo;
            ticks = (int) (acumuladorTick / intervaloTick);
            acumuladorTick -= ticks * intervaloTick;
        }
        duracaoRestante -= delta;
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
