package com.populstay.populife.util;

import java.util.List;

public class CollectionUtil {

    public static boolean isEmpty(List list) {
        return null == list || list.size() <= 0;
    }
}
