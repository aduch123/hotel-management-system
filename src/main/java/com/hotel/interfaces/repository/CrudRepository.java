package com.hotel.interfaces.repository;

import java.util.List;

public interface CrudRepository<T, ID> {
    T save(T entity);
    T findById(ID id);
    List<T> findAll();
    void delete(ID id);
    boolean existsById(ID id);
}