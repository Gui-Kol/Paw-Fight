package com.pawfight.game.engine;

import java.util.List;

public class Validar {

    public boolean validarLista(List list){
        if (list == null || list.isEmpty()){
            return false;
        }
        return true;
    }

}
