package org.yserver.core.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

/**
 * Description: JpaBaseService.<br>
 * Date: 2016/9/7 23:43<br>
 * Author: ysj
 */
public interface JpaBaseService<T extends JpaBaseEntity, ID extends Serializable, DAO extends JpaBaseDao<T, ID>> extends Remote {
    DAO getDao();

    /**
     * save
     *
     * @param entity
     */
    T save(T entity);

    /**
     * batch save
     *
     * @param entities
     * @param <S>
     * @return
     */
    <S extends T> List<S> save(List<S> entities);

    /**
     * delete
     *
     * @param entity
     */
    void delete(T entity);

    /**
     * deleteList
     *
     * @param list
     */
    void deleteList(List<T> list);

    /**
     * deleteList
     */
    void deleteAll();

    /**
     * get
     *
     * @param id
     * @return
     */
    T find(ID id);

    /**
     * findList
     *
     * @param ids
     * @return
     */
    List<T> findList(List<ID> ids);

    /**
     * findAll
     *
     * @return
     */
    List<T> findAll();

    /**
     * findPage
     *
     * @param pageable
     * @return
     */
    Page<T> findPage(Pageable pageable);
}
