package org.yserver.core.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Transactional
public abstract class JpaBaseServiceImpl<T extends JpaBaseEntity, ID extends Serializable, DAO extends JpaBaseDao<T, ID>> implements
        JpaBaseService<T, ID, DAO> {

    public T save(T entity) {
        return getDao().save(entity);
    }

    public <S extends T> List<S> save(List<S> entities) {
        return getDao().save(entities);
    }

    public void delete(T entity) {
        getDao().delete(entity);
    }

    public void deleteList(List<T> list) {
        getDao().delete(list);
    }

    public void deleteAll() {
        getDao().deleteAllInBatch();
    }

    public T find(ID id) {
        return getDao().findOne(id);
    }

    public List<T> findList(List<ID> ids) {
        return getDao().findAll(ids);
    }

    public List<T> findAll() {
        return getDao().findAll();
    }

    @Override
    public Page<T> findPage(Pageable pageable) {
        return getDao().findAll(pageable);
    }
}
