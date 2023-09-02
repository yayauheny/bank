package by.yayauheny.service;

import java.util.List;
import java.util.Optional;

public interface Service<T, V> {
    Optional<V> findById(T id);

    List<V> findAll();

    V save(V v);

    void update(V v);

    boolean delete(T id);
}
