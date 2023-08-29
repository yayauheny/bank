package by.yayauheny.dao;

import by.yayauheny.exception.DatabaseException;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {

    Optional<T> findById(K id) throws DatabaseException;

    boolean delete(K id);

    T save(T t);

    void update(T t);

    List<Optional<T>> findAll();
}
