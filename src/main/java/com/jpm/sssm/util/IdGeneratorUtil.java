package com.jpm.sssm.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Generates unique id used to generate trade id
 *
 * Created by adnan_saqib on 11/12/2016.
 */
public class IdGeneratorUtil {
    private static AtomicLong id = new AtomicLong(1);

    public static Long generateUniqueId(){
        return id.getAndIncrement();
    }
}
