package by.yayauheny.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, V> {

    Optional<V> findById(T id);

    List<V> findAll();

    V save(V v);

    void update(V v);

    boolean delete(T id);
}
