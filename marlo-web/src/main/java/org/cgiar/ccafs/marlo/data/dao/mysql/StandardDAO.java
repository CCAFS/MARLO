/*****************************************************************
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.config.HibernateListener;
import org.cgiar.ccafs.marlo.data.AuditLogInterceptor;
import org.cgiar.ccafs.marlo.data.CloseSession;
import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian David García O. - CIAT/CCAFS
 * @author Héctor F. Tobón R. - CIAT/CCAFS
 * @author Hermes Jimenez - CIAT/CCAFS
 */
@Singleton
public class StandardDAO {

  private static final Logger LOG = LoggerFactory.getLogger(StandardDAO.class);
  private SessionFactory sessionFactory;
  @Inject
  private AuditLogInterceptor interceptor;

  public StandardDAO() {

  }


  /**
   * This method commit the changes to hibernate table (in memory) but does not synchronize the changes to the database
   * engine.
   */
  private void commitTransaction(Transaction tx) {
    tx.commit();
  }

  /**
   * This method deletes a record from the database.
   * 
   * @param obj is a persistence instance from the database model.
   * @return true if the record was successfully deleted, false otherwhise.
   */
  protected boolean delete(Object obj) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession(interceptor);
      interceptor.setSession(session);
      Object newEntityRef = session.merge(obj);
      tx = this.initTransaction(session);
      session.clear();
      session.delete(newEntityRef);
      this.commitTransaction(tx);

      return true;
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
      return false;
    } finally {
      // Flushing the changes always.
    }
  }

  public boolean executeQuery(String sqlQuery) {
    Session session = null;
    Transaction tx = null;

    try {
      session = this.openSession();
      tx = this.initTransaction(session);

      session.flush();
      session.clear();
      Query query = session.createSQLQuery(sqlQuery);
      System.out.println(sqlQuery);
      query.executeUpdate();
      tx.commit();
      return true;
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
      return false;
    } finally {

    }
  }


  /**
   * This method finds a specific record from the database and transform it to a database model object.
   * 
   * @param clazz represents the class of the database model object.
   * @param id is the record identifier.
   * @return the object populated.
   */
  public <T> T find(Class<T> clazz, Object id) {
    T obj = null;
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();


      if (!session.isOpen() || !session.isConnected() || session.connection().isClosed()) {
        session = this.openSession();
      }
      tx = this.initTransaction(session);
      this.commitTransaction(tx);
      obj = (T) session.get(clazz, (Serializable) id);


    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
    } finally {
      // Flushing the changes always.
    }
    return obj;
  }


  /**
   * This method make a query that returns a list of objects from the model.
   * This method was implemented in a generic way, so, the list of objects to be returned will depend on how the method
   * is being called.
   * e.g:
   * List<SomeObject> list = this.findAll("some hibernate query");
   * or
   * this.<SomeObject>findAll("some hibernate query");
   * 
   * @param hibernateQuery is a string representing an HQL query.
   * @return a list of <T> objects.
   */
  protected <T> List<T> findAll(String hibernateQuery) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();
      tx = this.initTransaction(session);
      session.clear();
      session.flush();


      Query query = session.createQuery(hibernateQuery);

      @SuppressWarnings("unchecked")
      List<T> list = query.list();
      this.commitTransaction(tx);

      return list;
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
      return new ArrayList<T>();
    } finally {
      if (session.isOpen()) {
        // Flushing the changes always.
      }

    }
  }

  /**
   * This method make a query that returns a list of objects from the model.
   * This method was implemented in a generic way, so, the list of objects to be returned will depend on how the method
   * is being called.
   * e.g:
   * List<SomeObject> list = this.findAll("some hibernate query");
   * or
   * this.<SomeObject>findAll("some hibernate query");
   * 
   * @param hibernateQuery is a string representing an HQL query
   * @param params array of params to run query
   * @return a list of <T> objects.
   */
  protected <T> List<T> findAll(String hibernateQuery, Object... params) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();
      tx = this.initTransaction(session);
      session.clear();
      session.flush();


      Query query = session.createQuery(hibernateQuery);
      int i = 0;
      for (Object param : params) {
        query.setParameter(i, param);
        i++;
      }
      @SuppressWarnings("unchecked")
      List<T> list = query.list();
      this.commitTransaction(tx);

      return list;
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
      return new ArrayList<T>();
    } finally {
      if (session.isOpen()) {
        // Flushing the changes always.
      }

    }
  }

  /**
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an HQL query.
   */
  public List<Map<String, Object>> findCustomQuery(String sqlQuery) {
    Session session = null;
    Transaction tx = null;

    try {
      session = this.openSession();
      tx = this.initTransaction(session);

      session.flush();
      session.clear();
      Query query = session.createSQLQuery(sqlQuery);
      query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
      List<Map<String, Object>> result = query.list();
      this.commitTransaction(tx);

      return result;
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
      return null;
    } finally {
      if (session.isOpen()) {
        // Flushing the changes always.
      }
    }
  }

  protected <T> List<T> findEveryone(Class<T> clazz) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();
      tx = this.initTransaction(session);
      session.flush();
      session.clear();
      Query query = session.createQuery("from " + clazz.getName());
      @SuppressWarnings("unchecked")
      List<T> list = query.list();
      this.commitTransaction(tx);

      return list;
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
      return null;
    } finally {
      // Flushing the changes always.
    }
  }

  /**
   * This method make a query that returns a single object result from the model.
   * This method was implemented in a generic way, so, the object to be returned will depend on how the method
   * is being called.
   * 
   * @param hibernateQuery is a string representing an HQL query.
   * @return a Object of <T>
   */
  protected <T> Object findSingleResult(Class<T> clazz, String hibernateQuery) {
    Session session = null;
    Transaction tx = null;
    try {

      session = this.openSession();
      tx = this.initTransaction(session);
      session.flush();
      session.clear();

      Query query = session.createQuery(hibernateQuery);
      session.flush();
      Object object = clazz.cast(query.uniqueResult());
      this.commitTransaction(tx);

      return object;
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
      return new ArrayList<T>();
    } finally {
      if (session.isOpen()) {
        // Flushing the changes always.
      }
    }
  }

  /**
   * This method initializes a transaction.
   */
  private Transaction initTransaction(Session session) {
    Transaction tx = session.beginTransaction();
    return tx;
  }


  public void logIt(String action, IAuditLog entity, String json, long userId, String transactionId, Long principal,
    String relationName, String actionName, Phase phase) {


    String detail = "";
    if (actionName == null) {
      detail = entity.getLogDeatil();
    } else {
      detail = "Action: " + actionName + " " + entity.getLogDeatil();
    }

    Auditlog auditRecord =
      new Auditlog(action, detail, new Date(), entity.getId().toString(), entity.getClass().toString(), json, userId,
        transactionId, principal, relationName, entity.getModificationJustification());
    if (phase != null) {
      auditRecord.setPhase(phase.getId());
    }

    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();

      tx = this.initTransaction(session);
      session.save(auditRecord);
      this.commitTransaction(tx);


    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      session.clear();
      if (e instanceof org.hibernate.exception.ConstraintViolationException) {
        Transaction tx1 = session.beginTransaction();
        tx1.commit();
      }

    }


  }


  /**
   * This method opens a session to the database.
   * 
   * @return a Session object.
   */
  public Session openSession() {
    if (sessionFactory == null) {
      sessionFactory =
        (SessionFactory) ServletActionContext.getServletContext().getAttribute(HibernateListener.KEY_NAME);

    }

    Session session = sessionFactory.openSession();
    CloseSession closeSession = new CloseSession();
    closeSession.setSession(session);
    closeSession.start();
    return session;

  }


  /**
   * This method opens a session to the database.
   * 
   * @return a Session object.
   */
  private Session openSession(AuditLogInterceptor interceptor) {
    if (sessionFactory == null) {
      sessionFactory =
        (SessionFactory) ServletActionContext.getServletContext().getAttribute(HibernateListener.KEY_NAME);

    }

    Session session = sessionFactory.openSession(interceptor);
    CloseSession closeSession = new CloseSession();
    closeSession.setSession(session);
    closeSession.start();
    return session;


  }

  /**
   * This method tries to roll back the changes in case they were not flushed.
   */
  private void rollBackTransaction(Transaction tx) {
    try {
      tx.rollback();
    } catch (Exception e) {

    }
  }

  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean save(Object obj) {

    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();
      tx = this.initTransaction(session);
      session.save(obj);
      this.commitTransaction(tx);


      return true;
    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      session.clear();
      if (e instanceof org.hibernate.exception.ConstraintViolationException) {
        Transaction tx1 = session.beginTransaction();
        tx1.commit();
      }
      return false;
    }
  }


  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean save(Object obj, String actionName, List<String> relationsName) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession(interceptor);
      interceptor.setSession(session);
      interceptor.setActionName(actionName);
      interceptor.setRelationsName(relationsName);
      tx = this.initTransaction(session);
      session.save(obj);
      this.commitTransaction(tx);


      return true;
    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      session.clear();
      if (e instanceof org.hibernate.exception.ConstraintViolationException) {
        Transaction tx1 = session.beginTransaction();
        tx1.commit();
      }
      return false;
    }
  }

  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean save(Object obj, String actionName, List<String> relationsName, Phase phase) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession(interceptor);
      interceptor.setSession(session);
      interceptor.setActionName(actionName);
      interceptor.setRelationsName(relationsName);
      interceptor.setPhase(phase);
      tx = this.initTransaction(session);
      session.save(obj);
      this.commitTransaction(tx);


      return true;
    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      session.clear();
      if (e instanceof org.hibernate.exception.ConstraintViolationException) {
        Transaction tx1 = session.beginTransaction();
        tx1.commit();
      }
      return false;
    }
  }


  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean update(Object obj) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();
      // interceptor.setSession(session);

      tx = this.initTransaction(session);


      obj = session.merge(obj);
      session.saveOrUpdate(obj);
      this.commitTransaction(tx);


      return true;
    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      session.clear();
      if (e instanceof org.hibernate.exception.ConstraintViolationException) {
        Transaction tx1 = session.beginTransaction();
        tx1.commit();
      }
      return false;
    }
  }


  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean update(Object obj, String actionName, List<String> relationsName) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession(interceptor);
      interceptor.setSession(session);
      interceptor.setActionName(actionName);
      interceptor.setRelationsName(relationsName);
      tx = this.initTransaction(session);


      obj = session.merge(obj);
      session.saveOrUpdate(obj);

      this.commitTransaction(tx);


      return true;
    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      session.clear();
      if (e instanceof org.hibernate.exception.ConstraintViolationException) {
        Transaction tx1 = session.beginTransaction();
        tx1.commit();
      }
      return false;
    }
  }

  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean update(Object obj, String actionName, List<String> relationsName, Phase phase) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession(interceptor);
      interceptor.setSession(session);
      interceptor.setActionName(actionName);
      interceptor.setRelationsName(relationsName);
      interceptor.setPhase(phase);
      tx = this.initTransaction(session);


      obj = session.merge(obj);
      session.saveOrUpdate(obj);

      this.commitTransaction(tx);


      return true;
    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      session.clear();
      if (e instanceof org.hibernate.exception.ConstraintViolationException) {
        Transaction tx1 = session.beginTransaction();
        tx1.commit();
      }
      return false;
    }
  }
}