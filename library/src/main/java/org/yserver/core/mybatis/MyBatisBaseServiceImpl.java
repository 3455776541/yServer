package org.yserver.core.mybatis;

import org.yserver.core.model.BaseEntity;
import org.yserver.core.model.Pagination;
import org.yserver.utils.Log;
import org.yserver.utils.RandomUtil;
import org.yserver.utils.StringUtils;

import java.util.*;

/**
 * ClassName: MyBatisBaseServiceImpl <br>
 * Reason: MyBatisBaseServiceImpl. <br>
 * date: 2016年7月26日 下午1:40:44 <br>
 *
 * @author ysj
 * @version 1.0
 * @since JDK 1.7
 */
public abstract class MyBatisBaseServiceImpl<T extends BaseEntity, DAO extends MyBatisBaseDao> implements MyBatisBaseService<T, DAO>
{
    private final static Log logger = Log.getLogger(MyBatisBaseServiceImpl.class);

    public T save(T entity)
    {
        if (StringUtils.isNotBlank(entity.getCode()))
        {
            entity.setUpdatedTime(new Date());
            getDao().update(entity);
        }
        else
        {
            //生成主键
            entity.setCode(RandomUtil.uuid());
            entity.setCreatedTime(new Date());
            getDao().insert(entity);
        }
        return entity;
    }

    public List<T> save(List<T> entities)
    {
        Iterator<T> iterator = entities.iterator();
        while (iterator.hasNext())
        {
            T entity = iterator.next();
            this.save(entity);
        }
        return entities;
    }

    public void delete(T entity)
    {
        getDao().delete(entity);
    }

    public void delete(List<T> list)
    {
        getDao().deleteList(list);
    }

    public void deleteAll()
    {
        getDao().deleteAll();
    }

    /**
     * find:(获取). <br>
     *
     * @param entity
     * @return
     * @author ysj
     * @since JDK 1.7
     * date: 2016年8月12日 下午5:50:41 <br>
     */
    public T find(T entity)
    {
        return (T) getDao().findOne(entity);
    }

    /**
     * list:(列表). <br>
     *
     * @param entity
     * @return
     * @author ysj
     * @since JDK 1.7
     * date: 2016年8月12日 下午5:50:14 <br>
     */
    public List<T> findAll(T entity)
    {
        return (List<T>) getDao().findAll(entity);
    }

    /**
     * listAll:(列表(全部)). <br>
     *
     * @return
     * @throws Exception
     * @author ysj
     * @since JDK 1.7
     * date: 2016年8月12日 下午5:50:27 <br>
     */
    public List<T> findAll()
    {
        return (List<T>) getDao().findAll();
    }

    @Override
    public Map<String, Object> findPage(Pagination<T> pagination)
    {
        Map<String, Object> map = null != pagination.getParams() ? pagination.getParams() : new HashMap<>();
        map.put("page", pagination.getPage());
        map.put("size", pagination.getSize());
        map.put("index", pagination.getIndex());
        List<T> list = getDao().findPage(map);
        map = new HashMap<>();
        if (null != list && list.size() > 0)
        {
            pagination.setContent(list);
            map.put("content", list);
        }
        map.put("totalElements", getDao().count(pagination.getParams()));
        return map;
    }
}
