package com.cardone.dscatalog.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cardone.dscatalog.projection.IdProjection;

public class Util {
    
    public static <ID> List<? extends IdProjection<ID>> replace( List<? extends IdProjection<ID>> ordered, 
                                                          List<? extends IdProjection<ID>> unordered){
        //Monta uma lista ordenada com base na projection que estava ordenada de acordo com o Pageable
        //Necessário que a classe tenha implementado a interface IdProjection
        Map<ID, IdProjection<ID>> map = new HashMap<>();
        //Inclue os produtos no map
        for(IdProjection<ID> obj : unordered){
            map.put(obj.getId(), obj);
        }

        //Monta uma lista ordenada com base na ordenaçao do resultado da busca inicial
        List<IdProjection<ID>> result = new ArrayList<>();

        //Percorro a lista de projection e incluo no map de maneira ordenada de acordo com a ordenação da busca
        for(IdProjection<ID> obj : ordered){
            result.add(map.get(obj.getId())); //Recupera o produto do map pelo Id e inclui na lista
        }
        return result;
    }
}
