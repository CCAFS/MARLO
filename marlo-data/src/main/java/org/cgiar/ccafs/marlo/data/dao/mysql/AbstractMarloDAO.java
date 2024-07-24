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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.utils.AuditLogContext;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
// import org.hibernate.Transaction;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian David García O. - CIAT/CCAFS
 * @author Héctor F. Tobón R. - CIAT/CCAFS
 * @author Hermes Jimenez - CIAT/CCAFS
 */
public abstract class AbstractMarloDAO<T, ID extends Serializable> {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractMarloDAO.class);
  private final SessionFactory sessionFactory;

  public AbstractMarloDAO(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }


  private void addAuditLogFieldsToThreadStorage(Object entity, String actionName, List<String> relationsNames) {
    LOG.debug("Adding auditing fields to AuditLogContext");
    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();
    auditLogContext.setEntityCanonicalName(entity.getClass().getCanonicalName());
    auditLogContext.setActionName(actionName);
    auditLogContext.setRelationsNames(relationsNames);

  }

  private void addAuditLogFieldsToThreadStorage(Object entity, String actionName, List<String> relationsNames,
    Phase phase) {
    LOG.debug("Adding auditing fields to AuditLogContext");
    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();
    auditLogContext.setEntityCanonicalName(entity.getClass().getCanonicalName());
    auditLogContext.setActionName(actionName);
    auditLogContext.setRelationsNames(relationsNames);
    auditLogContext.setPhase(phase);
  }


  /**
   * This method deletes a record from the database.
   * 
   * @param obj is a persistence instance from the database model.
   */
  protected void delete(Object obj) {
    System.out.println(" linea 78");
    this.sessionFactory.getCurrentSession().delete(obj);
  }

  /**
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an SQL query.
   */
  public List<Map<String, Object>> excuteStoreProcedure(String storeProcedure, String sqlQuery) {
    System.out.println(" linea 87 excuteStoreProcedure ");
    Session sesion = sessionFactory.openSession();
    System.out.println(" linea 89 excuteStoreProcedure ");
    System.out.println(" linea 90 excuteStoreProcedure " + storeProcedure);
    Transaction transaction = sesion.beginTransaction();
    NativeQuery<Map<String, Object>> queryProcd = sesion.createSQLQuery(storeProcedure);
    System.out.println(" linea 92 excuteStoreProcedure ");
    // queryProcd.setFlushMode(FlushMode.COMMIT);
    System.out.println(" linea 95 excuteStoreProcedure ");
    queryProcd.executeUpdate();
    NativeQuery<Map<String, Object>> query = sesion.createSQLQuery(sqlQuery);
    System.out.println(" linea 98 excuteStoreProcedure ");
    query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
    System.out.println(" linea 100 excuteStoreProcedure ");
    // query.setFlushMode(FlushMode.COMMIT);
    System.out.println(" linea 102 excuteStoreProcedure ");

    List<Map<String, Object>> result = query.list();
    System.out.println(" linea 105 excuteStoreProcedure ");
    transaction.commit();
    System.out.println(" linea 107 excuteStoreProcedure ");
    sesion.close();
    System.out.println(" linea 109 excuteStoreProcedure ");
    return result;

  }

  /**
   * This method make a query that returns a object result from the function.
   * 
   * @param function is a string representing an SQL query.
   */
  public Object executeFunction(String function) {
    return this.resultFunction(this.findCustomQuery(function));
  }

  /**
   * Pass String based hibernate query.
   * 
   * @param sqlQuery
   */
  public void executeUpdateQuery(String sqlQuery) {
    System.out.println(" linea 130");
    NativeQuery query = this.sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
    query.setFlushMode(FlushMode.COMMIT);
    query.executeUpdate();
  }


  //
  // /**
  // * Performs either a save or update depending on if there is an identifier or not.
  // *
  // * @param entity
  // * @return
  // */
  // public T save(T entity) {
  // if (this.getId(entity) != null) {
  // return this.update(entity);
  //
  // }
  // return this.saveEntity(entity);
  //
  // }


  /**
   * This method finds a specific record from the database and transform it to a database model object.
   * 
   * @param clazz represents the class of the database model object.
   * @param id is the record identifier.
   * @return the object populated.
   */
  public T find(Class<T> clazz, ID id) {
    System.out.println(" linea 162");
    Session sesion = sessionFactory.openSession();
    Transaction transaction = sesion.beginTransaction();
    System.out.println(" linea 165");
    T obj = sesion.get(clazz, id);

    // committing the transaction
    transaction.commit(); // Transaction is committed


    // closing the session
    // sesion.close();
    System.out.println(" linea 173");
    return obj;
  }

  protected List<T> findAll(Query<T> hibernateQuery) {
    System.out.println(" linea 177");
    hibernateQuery.setHibernateFlushMode(FlushMode.COMMIT);
    @SuppressWarnings("unchecked")
    List<T> list = hibernateQuery.list();
    return list;
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
  protected List<T> findAll(String hibernateQuery) {
    System.out.println(" linea 196");
    Session sesion = sessionFactory.openSession();
    Transaction transaction = sesion.beginTransaction();
    Query<T> query = sesion.createQuery(hibernateQuery);
    transaction.commit();
    return this.findAll(query);
  }

  /**
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an HQL query.
   */
  public List<Map<String, Object>> findCustomQuery(String sqlQuery) {
    System.out.println(" linea 207");
    Session sesion = sessionFactory.openSession();
    Transaction transaction = sesion.beginTransaction();
    NativeQuery<Map<String, Object>> query = sesion.createSQLQuery(sqlQuery);
    query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
    query.setFlushMode(FlushMode.COMMIT);
    List<Map<String, Object>> result = query.list();
    transaction.commit();
    System.out.println(" linea 218");
    return result;

  }

  protected List<T> findEveryone(Class<T> clazz) {
    System.out.println(" linea 224");
    Session sesion = sessionFactory.openSession();
    Transaction transaction = sesion.beginTransaction();
    System.out.println(" linea 227");
    Query<T> query = sesion.createQuery("from " + clazz.getName());
    System.out.println(" linea 229");
    query.setHibernateFlushMode(FlushMode.COMMIT);
    System.out.println(" linea 231");
    @SuppressWarnings("unchecked")
    List<T> list = query.list();
    System.out.println(" linea 234");
    transaction.commit();
    System.out.println(" linea 236");
    return list;

  }

  /**
   * Allows clients to create the HibernateQuery and set parameters on it.
   * 
   * @param clazz
   * @param hibernateQuery
   * @return
   */
  protected T findSingleResult(Class<T> clazz, Query<T> hibernateQuery) {
    System.out.println(" linea 251");
    hibernateQuery.setHibernateFlushMode(FlushMode.COMMIT);
    T object = clazz.cast(hibernateQuery.uniqueResult());
    return object;
  }

  /**
   * This method make a query that returns a single object result from the model.
   * This method was implemented in a generic way, so, the object to be returned will depend on how the method
   * is being called.
   * 
   * @param hibernateQuery is a string representing an HQL query.
   * @return a Object of <T>
   */
  protected T findSingleResult(Class<T> clazz, String hibernateQuery) {
    System.out.println(" linea 266");
    Query<T> query = sessionFactory.getCurrentSession().createQuery(hibernateQuery);
    query.setHibernateFlushMode(FlushMode.COMMIT);
    return this.findSingleResult(clazz, query);
  }


  public Session getCurrentSession() {
    System.out.println(" linea 274");
    Session session = sessionFactory.getCurrentSession();

    if (session == null || !session.isOpen()) {
      session = sessionFactory.openSession();
    }

    return session;
  }

  /**
   * Return the sessionFactory. DAOs are free to get this and use it to perform custom queries.
   * 
   * @return
   */
  SessionFactory getSessionFactory() {
    return this.sessionFactory;
  }


  /**
   * Get the user id that is in the temporally table (permissions)
   * 
   * @return the user id
   */
  public long getTemTableUserId() {
    long idT = -1;
    StringBuilder builder = new StringBuilder();
    builder.append("select DISTINCT id as idT from user_permission");
    try {
      List<Map<String, Object>> list = this.findCustomQuery(builder.toString());
      for (Map<String, Object> map : list) {
        idT = Long.parseLong(map.get("idT").toString());
      }
    } catch (Exception e) {
      return idT;
    }
    return idT;
  }


  // /**
  // * Return the ID for the entity or null
  // *
  // * @param entity
  // * @return
  // */
  // private ID getId(T entity) {
  // ClassMetadata metadata = this.sessionFactory.getClassMetadata(entity.getClass());
  // if (metadata.hasIdentifierProperty()) {
  // return (ID) metadata.getIdentifier(entity, (SessionImplementor) this.sessionFactory.getCurrentSession());
  // }
  // return null;
  // }

  public Session openSessionIfNeeded() {
    Session currentSession = sessionFactory.getCurrentSession();

    if (currentSession == null || !currentSession.getTransaction().isActive()) {
      return sessionFactory.openSession();
    }

    return currentSession;
  }

  /**
   * This method return a object result from the function.
   * 
   * @param result is a List<Map<String, Object>> representing the result the function
   */
  private Object resultFunction(List<Map<String, Object>> result) {
    for (Map<String, Object> list1 : result) {
      for (Map.Entry<String, Object> entry : list1.entrySet()) {
        return entry.getValue();
      }
    }
    return null;
  }


  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected T saveEntity(T entity) {
    System.out.println(" linea 335");
    sessionFactory.getCurrentSession().persist(entity);
    return entity;
  }

  /**
   * This method persists record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected T saveEntity(T entity, String actionName, List<String> relationsName) {
    System.out.println(" linea 348");
    this.addAuditLogFieldsToThreadStorage(entity, actionName, relationsName);
    sessionFactory.getCurrentSession().persist(entity);
    return entity;
  }

  /**
   * This method persists record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected T saveEntity(T entity, String actionName, List<String> relationsName, Phase phase) {
    System.out.println(" linea 354");
    this.addAuditLogFieldsToThreadStorage(entity, actionName, relationsName, phase);
    sessionFactory.getCurrentSession().persist(entity);
    return entity;
  }

  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected T update(T entity) {
    System.out.println(" linea 367");
    entity = (T) sessionFactory.getCurrentSession().merge(entity);
    return entity;
  }

  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected T update(T entity, String actionName, List<String> relationsName) {
    System.out.println(" linea 380");
    this.addAuditLogFieldsToThreadStorage(entity, actionName, relationsName);
    entity = (T) sessionFactory.getCurrentSession().merge(entity);
    return entity;
  }

  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected T update(T entity, String actionName, List<String> relationsName, Phase phase) {
    System.out.println(" linea 394");
    this.addAuditLogFieldsToThreadStorage(entity, actionName, relationsName, phase);
    entity = (T) sessionFactory.getCurrentSession().merge(entity);
    return entity;
  }


}