package org.utils;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class PageSplitter {

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
