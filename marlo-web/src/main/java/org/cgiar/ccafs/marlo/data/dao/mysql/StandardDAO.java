/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.config.HibernateListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;
import org.apache.struts2.ServletActionContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.AliasToEntityMapResultTransformer;

/**
 * @author Christian David García O. - CIAT/CCAFS
 * @author Héctor F. Tobón R. - CIAT/CCAFS
 * @author Hermes Jimenez - CIAT/CCAFS
 */
@Singleton
public class StandardDAO {

  private SessionFactory sessionFactory;

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
      session = this.openSession();
      Object newEntityRef = session.merge(obj);
      tx = this.initTransaction(session);
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
      session.flush(); // Flushing the changes always.
    }
  }

  /**
   * This method finds a specific record from the database and transform it to a database model object.
   * 
   * @param clazz represents the class of the database model object.
   * @param id is the record identifier.
   * @return the object populated.
   */
  protected <T> T find(Class<T> clazz, Object id) {
    T obj = null;
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();

      tx = this.initTransaction(session);
      obj = (T) session.get(clazz, (Serializable) id);
      this.commitTransaction(tx);
    } catch (Exception e) {
      if (tx != null) {
        this.rollBackTransaction(tx);
      }
      e.printStackTrace();
    } finally {
      session.flush(); // Flushing the changes always.
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
        session.flush(); // Flushing the changes always.
      }

    }
  }

  /**
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an HQL query.
   */
  protected List<Map<String, Object>> findCustomQuery(String sqlQuery) {
    Session session = null;
    Transaction tx = null;

    try {
      session = this.openSession();
      tx = this.initTransaction(session);
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
        session.flush(); // Flushing the changes always.
      }
    }
  }

  protected <T> List<T> findEveryone(Class<T> clazz) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();
      tx = this.initTransaction(session);

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
      session.flush(); // Flushing the changes always.
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
      Query query = session.createQuery(hibernateQuery);
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
        session.flush(); // Flushing the changes always.
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


  /**
   * This method opens a session to the database.
   * 
   * @return a Session object.
   */
  private Session openSession() {
    if (sessionFactory == null) {
      this.sessionFactory =
        (SessionFactory) ServletActionContext.getServletContext().getAttribute(HibernateListener.KEY_NAME);
    }

    return sessionFactory.openSession();
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
  protected boolean saveOrUpdate(Object obj) {
    Session session = null;
    Transaction tx = null;
    try {
      session = this.openSession();

      tx = this.initTransaction(session);
      session.merge(obj);
      session.save(obj);
      this.commitTransaction(tx);
      session.flush();
      return true;
    } catch (Exception e) {
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