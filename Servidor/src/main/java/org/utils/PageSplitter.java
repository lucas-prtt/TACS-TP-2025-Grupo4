
package org.utils;

import java.util.List;

/**
 * Utilidad para dividir listas en páginas según parámetros de paginación.
 */
public class PageSplitter {

    /**
     * Devuelve una sublista correspondiente a la página solicitada, según el límite indicado.
     * Si page o limit son null, retorna la lista completa.
     *
     * @param objectsList Lista de objetos a paginar.
     * @param page Número de página (comienza en 1).
     * @param limit Cantidad máxima de elementos por página.
     * @param <T> Tipo de los elementos de la lista.
     * @return Sublista correspondiente a la página solicitada.
     */
    public static <T> List<T> getPageList(List<T> objectsList, Integer page, Integer limit){
        if(page == null || limit == null)
            return objectsList;
        else {
            page = Math.max(page, 1);
            limit = Math.max(limit, 1);
            int offset = Math.min((page-1)*limit, objectsList.size());
            int until = Math.min(offset + limit, objectsList.size());
            return objectsList.subList(offset, until).stream().toList();
        }
    }
}
