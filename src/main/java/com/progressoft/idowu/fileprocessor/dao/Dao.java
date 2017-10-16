
package com.progressoft.idowu.fileprocessor.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Oluwasegun Idowu
 * @param <T>
 */
public interface Dao<T extends Serializable> {

    void delete(T o);

    T getObject(long id);
    
    T getObject(String id);

    T getObject(Map<String, Object> map);
    
    T getObject(Map<String, Object> map,String entityPrefix);

    List<T> getObjects(Map<String, Object> map);
    
     public List<T> getObjects(Map<String, Object> map,String prefix);
     
      public List<T> getObjects(Map<String, Object> map,int min,int max);

    T update(T o);
    
    T updatee(T object) throws Exception;

    void save(T o);

    void update(String query);

    T find(Object id);

    List<T> findAll();

    List<T> findAll(int maxResults, int firstResult);

    int countAll();

    T initialize(T object);
}
