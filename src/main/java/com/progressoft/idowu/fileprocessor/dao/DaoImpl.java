/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package  com.progressoft.idowu.fileprocessor.dao;

import java.io.Serializable;
import javax.persistence.EntityManager;

/**
 *
 * @author Oluwasegun Idowu
 * @param <T>
 */
public class DaoImpl<T extends Serializable> extends AbstractDao<T> {

    protected  EntityManager em;

    public DaoImpl(Class claszz) {
        super(claszz);
    }

//    public DaoImpl(Class claszz, EntityManager em, boolean extended) {
//        super(claszz);
//        this.em = em;
//        this.extended = extended;
//    }

    @Override
    public EntityManager getEntityManager() {
//        log.info(" em " + em);
        if (em == null || !em.isOpen()) {
            EntityManager _em = super.createEntityManager();
//            log.info(" created em ");
            if (extended) {
                this.em = _em;
            }
            return em;
        }
        return em;
    }

    public EntityManager getEntityManager(boolean extended) {
        this.extended = extended;
        return getEntityManager();
    }

    public void close() {
        if (em != null && em.isOpen()) {
            if(em.getTransaction().isActive()) {
            em.getTransaction().commit();
            }
            em.close();
            log.info("closed em");
        }
    }

    public boolean isOpen() {
        return (em != null && em.isOpen());
    }

    public String inQuery(Object[] names) {
        if (names == null) {
            return "";
        }
        String sep = "'";
        StringBuilder sb = null;
        for (Object name : names) {
            if (name == null) {
                continue;
            }
            sb = sb == null ? new StringBuilder() : sb.append(",");
            if (name instanceof String) {
                sb.append(sep);
            }
            sb.append(name);
            if (name instanceof String) {
                sb.append(sep);
            }
        }
        return (sb != null ? sb.toString() : "");
    }

    @Override
    public T initialize(T object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
