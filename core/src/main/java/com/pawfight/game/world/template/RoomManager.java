package com.pawfight.game.world.template;

import com.pawfight.game.engine.hud.DesenharMiniMapa;
import com.pawfight.game.engine.procedural.CarregarPortas;
import com.pawfight.game.engine.procedural.sala.GeradorSalas;
import com.pawfight.game.engine.procedural.sala.CoordenadaSala;
import com.pawfight.game.engine.procedural.sala.Sala;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoomManager {

    private final GeradorSalas geradorSalas;
    private final CarregarPortas carregarPortas;
    private final DesenharMiniMapa desenharMiniMapa;
    private List<Sala> rooms;
    private Sala currentRoom;
    private boolean podeEntrarPorta;
    private final Set<CoordenadaSala> salasVisitadas;

    public RoomManager() {
        this.geradorSalas = new GeradorSalas();
        this.carregarPortas = new CarregarPortas();
        this.desenharMiniMapa = new DesenharMiniMapa();
        this.salasVisitadas = new HashSet<>();
        this.podeEntrarPorta = true;
    }

    public boolean currentRoomFoiVisitada() {
        if (currentRoom == null) return false;
        return salasVisitadas.contains(CoordenadaSala.de(currentRoom));
    }

    public GeradorSalas getRoomGenerator() {
        return geradorSalas;
    }

    public CarregarPortas getCarregarPortas() {
        return carregarPortas;
    }

    public DesenharMiniMapa getDesenharMiniMapa() {
        return desenharMiniMapa;
    }

    public List<Sala> getRooms() {
        return rooms;
    }

    public void setRooms(List<Sala> rooms) {
        this.rooms = rooms;
    }

    public Sala getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Sala currentRoom) {
        this.currentRoom = currentRoom;
    }

    public boolean isPodeEntrarPorta() {
        return podeEntrarPorta;
    }

    public void setPodeEntrarPorta(boolean podeEntrarPorta) {
        this.podeEntrarPorta = podeEntrarPorta;
    }

    public Set<CoordenadaSala> getSalasVisitadas() {
        return salasVisitadas;
    }

    public void addSalaVisitada(CoordenadaSala coordenada) {
        salasVisitadas.add(coordenada);
    }

    public Sala buscarSala(int x, int y) {
        return geradorSalas.buscarSala(x, y);
    }

    public void dispose() {
        if (desenharMiniMapa != null) {
            desenharMiniMapa.dispose();
        }
    }
}
