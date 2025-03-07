package com.cardone.dscatalog.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cardone.dscatalog.entities.Product;
import com.cardone.dscatalog.projection.ProductProjection;

public class Util {
    
    public static List<Product> replace(List<ProductProjection> ordered,List<Product> unordered){

        Map<Long, Product> map = new HashMap<>();
        //Inclue os produtos no map
        unordered.forEach(product -> map.put(product.getId(), product));

        //Monta uma lista ordenada com base na ordenaçao do resultado da busca inicial
        List<Product> result = new ArrayList<>();

        //Percorro a lista de projection e incluo no map de maneira ordenada de acordo com a ordenação da busca
        for(ProductProjection p : ordered){
            result.add(map.get(p.getId())); //Recupera o produto do map pelo Id e inclui na lista
        }
        return result;
    }
}
