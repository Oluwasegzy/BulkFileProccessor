package com.progressoft.idowu.fileprocessor.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Oluwasegun Idowu
 * @param <T>
 */
public abstract class AbstractDao<T extends Serializable> implements Dao<T> {

    private Class<T> entityClass;
    //private static EntityManagerFactory emf;
    protected boolean extended;
    protected org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractDao.class.getName());

    public AbstractDao(Class<T> domainClass) {
        this.entityClass = domainClass;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public abstract EntityManager getEntityManager();

    EntityManager createEntityManager() {
        return DBConfig.getEntityManagerFactory().createEntityManager();
    }

    private T transactDML(T object, int transaction) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            switch (transaction) {
                case 0: {
                    em.remove(object);
                    break;
                }
                case 1: {
                    // log.info("clazz:: " + object.getClass().getSimpleName());
                    object = em.merge(object);
                    break;
                }
                case 2: {
                    em.persist(object);
                    break;
                }
            }
            em.getTransaction().commit();
//            log.info("::::committed::: " + object.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("sql error ", e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
        return object;
    }
    
    private T transactDMLL(T object, int transaction) throws Exception{
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            switch (transaction) {
                case 0: {
                    em.remove(object);
                    break;
                }
                case 1: {
                    // log.info("clazz:: " + object.getClass().getSimpleName());
                    object = em.merge(object);
                    break;
                }
                case 2: {
                    em.persist(object);
                    break;
                }
            }
            em.getTransaction().commit();
//            log.info("::::committed::: " + object.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("sql error ", e);
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
        return object;
    }

    public List<T> namedQueryList(String queryName) {
        Query query = getEntityManager().createNamedQuery(queryName);
        return query.getResultList();
    }

    @Override
    public void delete(T object) {
        transactDML(object, 0);
    }

    @Override
    public T update(T object) {
        return transactDML(object, 1);
    }
    
      @Override
    public T updatee(T object) throws Exception{
        return transactDMLL(object, 1);
    }

    @Override
    public void save(T object) {
        transactDML(object, 2);
    }

//    @Override
//    public T initialize(T object) {
//        if (org.hibernate.Hibernate.isInitialized(object)) {
//            return object;
//        }
//        EntityManager em = getEntityManager(true);
//        boolean active = em.getTransaction().isActive();
//        try {
//            if (!active) {
//                em.getTransaction().begin();
//            }
//            org.hibernate.Hibernate.initialize(object);
//            if (!active) {
//                em.getTransaction().commit();
//            }
//        } catch (Exception e) {
//            if (em != null && !active) {
//                em.getTransaction().rollback();
//            }
//        } finally {
//            if (!extended && em != null) {
//                em.close();
//            }
//        }
//        return object;
//    }

    @Override
    public void update(String query) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Query q = em.createQuery(query);
            q.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public T getObject(long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } catch (Exception e) {
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public T getObject(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } catch (Exception e) {
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    Query getQuery(Map<String, Object> map, EntityManager em, String... prefix) {
        Set<String> parameters = null;
        int i = 0;
        Query q = null;

        String query = "select o from " + (prefix.length == 0 ? "" : prefix[0]) + entityClass.getSimpleName() + " o";
        List<String> qParams = new ArrayList<>();
        if (map != null && !map.isEmpty()) {
            parameters = map.keySet();
            Iterator<String> iter = parameters.iterator();

            while (iter.hasNext()) {
                String param = iter.next();
                Object value = map.get(param);
                boolean append = true;
                if (i == 0) {
                   
                    if (value != null) {
                         query = query + " where ";
                        qParams.add(param);
                    } else {
                        query = query + " where " + param;
                        append = false;
                    }
                } else if (i > 0 && value != null) {
                    query = query + " and ";
                    qParams.add(param);
                } else if (i > 0) {
                    query = query + " and " + param;
                    append = false;
                }
                if (append) {
                    query = query + "o." + param + "=:" + param;
                }
                i++;
          //      System.out.println("final::::: " + query);
            }
        }
     //   System.out.println("query::: " + query);
        q = em.createQuery(query);
//        if (parameters != null) {
//            Iterator<String> iter = parameters.iterator();
//            while (iter.hasNext()) {
//                String param = iter.next();
//                q.setParameter(param, map.get(param));
//            }
//        }

        if (!qParams.isEmpty()) {

            for (String p : qParams) {
             //   System.out.println("setting params: " + p);
                q.setParameter(p, map.get(p));
            }
        }
        return q;
    }

    @Override
    public T getObject(Map<String, Object> map) {
        EntityManager em = getEntityManager();
        try {
            Query query = getQuery(map, em);
            return query != null ? (T) query.getSingleResult() : null;
        } catch (Exception e) {
         //   e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public T getObject(Map<String, Object> map, String prefix) {
        EntityManager em = getEntityManager();
        try {
//            System.out.println("Query start " + System.currentTimeMillis());
            Query query = getQuery(map, em, prefix);
            T t = query != null ? (T) query.getSingleResult() : null;
//            System.out.println("Query end " + System.currentTimeMillis());
            return t;
        } catch (Exception e) {
          //  e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<T> getObjects(Map<String, Object> map) {
        EntityManager em = getEntityManager();
        try {
            Query query = getQuery(map, em);
            return query != null ? query.getResultList() : null;
        } catch (Exception e) {
 //           e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }
    
        @Override
    public List<T> getObjects(Map<String, Object> map,int min,int max) {
        EntityManager em = getEntityManager();
        try {
            Query query = getQuery(map, em).setFirstResult(min).setMaxResults(max);
            return query != null ? query.getResultList() : null;
        } catch (Exception e) {
        //    e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }
    
        @Override
    public List<T> getObjects(Map<String, Object> map,String prefix) {
        EntityManager em = getEntityManager();
        try {
//                     System.out.println("Query start " + System.currentTimeMillis());
            Query query = getQuery(map, em,prefix);
            List<T> t= query != null ? query.getResultList() : null;
//                     System.out.println("Query end " + System.currentTimeMillis());
                     return t;
        } catch (Exception e) {
         //   e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public T find(Object id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } catch (Exception e) {
       //     e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            Query query = getQuery(null, em);
            return query != null ? query.getResultList() : null;
        } catch (Exception e) {
        //    e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<T> findAll(int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query query = getQuery(null, em);
            if (query != null) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
                return query.getResultList();
            } else {
                return null;
            }
        } catch (Exception e) {
        //    e.printStackTrace();
            if (!extended && em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended && em != null) {
                em.close();
            }
        }
    }

    @Override
    public int countAll() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from " + entityClass.getName() + " as o");
            return ((Long) q.getSingleResult()).intValue();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return 0;
        } finally {
            if (!extended) {
                em.close();
            }
        }
    }

    public T singleQuery(String sql) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery(sql);
            return (T) q.getSingleResult();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended) {
                em.close();
            }
        }
    }

    public List<T> listQuery(String sql) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery(sql);
            return (List<T>) q.getResultList();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        } finally {
            if (!extended) {
                em.close();
            }
        }
    }
}
