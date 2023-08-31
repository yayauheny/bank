package by.yayauheny.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {

    Optional<T> findById(K id);

    List<T> findAll();

    T save(T t);

    void update(T t);

    boolean delete(K id);
}
