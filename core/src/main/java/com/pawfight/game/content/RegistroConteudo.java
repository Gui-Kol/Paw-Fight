package com.pawfight.game.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RegistroConteudo<T> {
    private final String tipo;
    private final Map<String, T> itens = new LinkedHashMap<>();

    public RegistroConteudo(String tipo) {
        this.tipo = tipo;
    }

    public void registrar(String id, T item) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID de " + tipo + " não pode ser vazio.");
        }
        if (item == null) {
            throw new IllegalArgumentException(tipo + " '" + id + "' não pode ser nulo.");
        }
        if (itens.putIfAbsent(id, item) != null) {
            throw new IllegalStateException("ID duplicado de " + tipo + ": " + id);
        }
    }

    public T obter(String id) {
        T item = itens.get(id);
        if (item == null) {
            throw new IllegalArgumentException(tipo + " desconhecido: " + id);
        }
        return item;
    }

    public List<T> listar() {
        return Collections.unmodifiableList(new ArrayList<>(itens.values()));
    }

    public int tamanho() {
        return itens.size();
    }
}
