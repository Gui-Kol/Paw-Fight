package com.pawfight.game.engine.input;

import java.util.Objects;

/**
 * Binding semântico de gamepad: um botão lógico (A, X, START, D-Pad...) ou o
 * extremo de um eixo lógico (ex.: analógico esquerdo para a esquerda).
 * A resolução para índices físicos é feita via {@code ControllerMapping} no
 * GerenciadorGamepad, então o mesmo binding funciona em qualquer controle.
 *
 * Serialização: "BOTAO:X", "EIXO+:ANALOGICO_ESQ_X", "EIXO-:ANALOGICO_ESQ_Y".
 * Entradas sem correspondência no mapping usam fallback bruto: "BOTAO_7", "EIXO_3".
 */
public final class EntradaGamepad {

    // Botões lógicos (resolvidos via ControllerMapping)
    public static final String BOTAO_A = "A";
    public static final String BOTAO_B = "B";
    public static final String BOTAO_X = "X";
    public static final String BOTAO_Y = "Y";
    public static final String BOTAO_L1 = "L1";
    public static final String BOTAO_R1 = "R1";
    public static final String BOTAO_L2 = "L2";
    public static final String BOTAO_R2 = "R2";
    public static final String BOTAO_SELECT = "SELECT";
    public static final String BOTAO_START = "START";
    public static final String BOTAO_DPAD_CIMA = "DPAD_CIMA";
    public static final String BOTAO_DPAD_BAIXO = "DPAD_BAIXO";
    public static final String BOTAO_DPAD_ESQUERDA = "DPAD_ESQUERDA";
    public static final String BOTAO_DPAD_DIREITA = "DPAD_DIREITA";
    public static final String BOTAO_ANALOGICO_ESQ = "ANALOGICO_ESQ";
    public static final String BOTAO_ANALOGICO_DIR = "ANALOGICO_DIR";

    // Eixos lógicos (resolvidos via ControllerMapping)
    public static final String EIXO_ANALOGICO_ESQ_X = "ANALOGICO_ESQ_X";
    public static final String EIXO_ANALOGICO_ESQ_Y = "ANALOGICO_ESQ_Y";
    public static final String EIXO_ANALOGICO_DIR_X = "ANALOGICO_DIR_X";
    public static final String EIXO_ANALOGICO_DIR_Y = "ANALOGICO_DIR_Y";

    // Prefixos de fallback para entradas sem equivalente lógico no mapping
    public static final String PREFIXO_BOTAO_BRUTO = "BOTAO_";
    public static final String PREFIXO_EIXO_BRUTO = "EIXO_";

    private static final String SEPARADOR = ":";
    private static final String SINAL_POSITIVO = "+";
    private static final String SINAL_NEGATIVO = "-";

    private final TipoEntradaGamepad tipo;
    private final String codigo;

    private EntradaGamepad(TipoEntradaGamepad tipo, String codigo) {
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.codigo = Objects.requireNonNull(codigo, "codigo");
    }

    public static EntradaGamepad botao(String codigoBotao) {
        return new EntradaGamepad(TipoEntradaGamepad.BOTAO, codigoBotao);
    }

    public static EntradaGamepad eixoPositivo(String codigoEixo) {
        return new EntradaGamepad(TipoEntradaGamepad.EIXO_POSITIVO, codigoEixo);
    }

    public static EntradaGamepad eixoNegativo(String codigoEixo) {
        return new EntradaGamepad(TipoEntradaGamepad.EIXO_NEGATIVO, codigoEixo);
    }

    public static EntradaGamepad botaoBruto(int indice) {
        return botao(PREFIXO_BOTAO_BRUTO + indice);
    }

    public static EntradaGamepad eixoBruto(int indice, boolean positivo) {
        String codigo = PREFIXO_EIXO_BRUTO + indice;
        return positivo ? eixoPositivo(codigo) : eixoNegativo(codigo);
    }

    public TipoEntradaGamepad getTipo() {
        return tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String serializar() {
        String sinal;
        switch (tipo) {
            case EIXO_POSITIVO:
                sinal = SINAL_POSITIVO;
                break;
            case EIXO_NEGATIVO:
                sinal = SINAL_NEGATIVO;
                break;
            default:
                return TipoEntradaGamepad.BOTAO.name() + SEPARADOR + codigo;
        }
        return "EIXO" + sinal + SEPARADOR + codigo;
    }

    /** Converte a forma serializada de volta; retorna null para texto vazio/nulo/inválido. */
    public static EntradaGamepad desserializar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        int separador = texto.indexOf(SEPARADOR);
        if (separador <= 0 || separador == texto.length() - 1) {
            return null;
        }
        String cabecalho = texto.substring(0, separador);
        String codigo = texto.substring(separador + 1);
        if (TipoEntradaGamepad.BOTAO.name().equals(cabecalho)) {
            return botao(codigo);
        }
        if (("EIXO" + SINAL_POSITIVO).equals(cabecalho)) {
            return eixoPositivo(codigo);
        }
        if (("EIXO" + SINAL_NEGATIVO).equals(cabecalho)) {
            return eixoNegativo(codigo);
        }
        return null;
    }

    /** Texto amigável para exibição na tela de configuração. */
    public String descricao() {
        if (tipo == TipoEntradaGamepad.BOTAO) {
            return descricaoBotao();
        }
        String base = codigo.startsWith(PREFIXO_EIXO_BRUTO)
            ? "Eixo " + codigo.substring(PREFIXO_EIXO_BRUTO.length())
            : descricaoEixo(codigo);
        return base + (tipo == TipoEntradaGamepad.EIXO_POSITIVO ? " +" : " -");
    }

    private String descricaoBotao() {
        if (codigo.startsWith(PREFIXO_BOTAO_BRUTO)) {
            return "Botão " + codigo.substring(PREFIXO_BOTAO_BRUTO.length());
        }
        switch (codigo) {
            case BOTAO_DPAD_CIMA: return "D-Pad Cima";
            case BOTAO_DPAD_BAIXO: return "D-Pad Baixo";
            case BOTAO_DPAD_ESQUERDA: return "D-Pad Esq.";
            case BOTAO_DPAD_DIREITA: return "D-Pad Dir.";
            case BOTAO_ANALOGICO_ESQ: return "An. Esq. Clique";
            case BOTAO_ANALOGICO_DIR: return "An. Dir. Clique";
            default: return codigo;
        }
    }

    private static String descricaoEixo(String eixo) {
        switch (eixo) {
            case EIXO_ANALOGICO_ESQ_X: return "An. Esq. X";
            case EIXO_ANALOGICO_ESQ_Y: return "An. Esq. Y";
            case EIXO_ANALOGICO_DIR_X: return "An. Dir. X";
            case EIXO_ANALOGICO_DIR_Y: return "An. Dir. Y";
            default: return eixo;
        }
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof EntradaGamepad)) return false;
        EntradaGamepad that = (EntradaGamepad) outro;
        return tipo == that.tipo && codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipo, codigo);
    }

    @Override
    public String toString() {
        return serializar();
    }
}
