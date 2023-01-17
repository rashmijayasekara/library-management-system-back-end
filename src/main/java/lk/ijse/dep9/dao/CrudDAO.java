package lk.ijse.dep9.dao;

import lk.ijse.dep9.dao.exception.ContraintViolationException;
import lk.ijse.dep9.entity.SuperEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface SuperDAO<T extends SuperEntity, ID> extends Serializable {
    long count();
    void deleteById(ID pk) throws ContraintViolationException;
    boolean existById(ID pk);
    List<T> findAll();
    Optional<T> findById(ID pk);
    Object save(T entity) throws ContraintViolationException;
    Object update(T entity) throws ContraintViolationException;
}
