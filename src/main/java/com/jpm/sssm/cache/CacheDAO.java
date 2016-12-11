package com.jpm.sssm.cache;

import java.util.List;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public interface CacheDAO<T> {
    public void saveAll(List<T> data);

    public void save(T data);

    public T get(String id);
}
