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
import org.hibernate.SessionFactory;
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
    this.sessionFactory.getCurrentSession().delete(obj);
  }

  /**
   * Validate the existence of a given table
   * 
   * @return validation result
   */
  public boolean doesTableExist(String tableName) {
    boolean exists = false;
    String sql = "";

    sql = "SELECT 1 FROM information_schema.tables WHERE table_name = :tableName LIMIT 1";

    try {
      NativeQuery<?> query = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
      query.setParameter("tableName", tableName);
      Object result = query.uniqueResult();
      exists = result != null;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return exists;
  }

  /**
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an SQL query.
   */

  public List<Map<String, Object>> excuteStoreProcedure(String storeProcedure, String sqlQuery) {
    try {
      NativeQuery<Map<String, Object>> queryProcd =
        this.sessionFactory.getCurrentSession().createSQLQuery(storeProcedure);
      queryProcd.setFlushMode(FlushMode.COMMIT);
      queryProcd.executeUpdate();
      NativeQuery<Map<String, Object>> query = this.sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
      query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
      query.setFlushMode(FlushMode.COMMIT);
      List<Map<String, Object>> result = query.list();
      return result;
    } catch (Exception e) {
      LOG.error(" error " + e.getMessage());
      return null;
    }

  }

  /**
   * This method make a query that returns a object result from the function.
   * 
   * @param function is a string representing an SQL query.
   */
  public Object executeFunction(String function) {
    return this.resultFunction(this.findCustomQuery(function));
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
   * Pass String based hibernate query.
   * 
   * @param sqlQuery
   */
  public void executeUpdateQuery(String sqlQuery) {

    NativeQuery query = this.sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
    query.setFlushMode(FlushMode.COMMIT);
    query.executeUpdate();
  }


  /**
   * This method finds a specific record from the database and transform it to a database model object.
   * 
   * @param clazz represents the class of the database model object.
   * @param id is the record identifier.
   * @return the object populated.
   */
  public T find(Class<T> clazz, ID id) {
    T obj = sessionFactory.getCurrentSession().get(clazz, id);

    return obj;
  }

  protected List<T> findAll(Query<T> hibernateQuery) {
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
    Query<T> query = sessionFactory.getCurrentSession().createQuery(hibernateQuery);
    return this.findAll(query);
  }

  /**
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an HQL query.
   */

  public List<Map<String, Object>> findCustomQuery(String sqlQuery) {

    NativeQuery<Map<String, Object>> query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
    query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
    query.setFlushMode(FlushMode.COMMIT);
    List<Map<String, Object>> result = query.list();

    return result;

  }


  protected List<T> findEveryone(Class<T> clazz) {
    Query<T> query = sessionFactory.getCurrentSession().createQuery("from " + clazz.getName());
    query.setHibernateFlushMode(FlushMode.COMMIT);

    @SuppressWarnings("unchecked")
    List<T> list = query.list();
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
    Query<T> query = sessionFactory.getCurrentSession().createQuery(hibernateQuery);
    query.setHibernateFlushMode(FlushMode.COMMIT);
    return this.findSingleResult(clazz, query);
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

    if (!this.doesTableExist("user_permission")) {
      LOG.info(" the table user_permission does not exist yet");
      return idT;
    }

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

  /**
   * Validates if the table presents an error in its integrity
   * 
   * @return validation result
   */
  public boolean isTableInGoodCondition(String tableName) {
    String sql = "CHECK TABLE " + tableName;

    try {
      NativeQuery<?> query = this.sessionFactory.getCurrentSession().createSQLQuery(sql);
      List<Object[]> results = (List<Object[]>) query.list();

      for (Object[] row : results) {
        String table = (String) row[0];
        String operation = (String) row[1];
        String messageType = (String) row[2];
        String messageText = (String) row[3];

        LOG.info("Table: " + table);
        LOG.info("Operation: " + operation);
        LOG.info("Message Type: " + messageType);
        LOG.info("Message Text: " + messageText);

        if ("status".equals(messageType) && "OK".equals(messageText)) {
          return true;
        } else if ("error".equals(messageType)) {
          LOG.error("Error with table: " + messageText);
          return false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return false;
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
    this.addAuditLogFieldsToThreadStorage(entity, actionName, relationsName, phase);
    entity = (T) sessionFactory.getCurrentSession().merge(entity);
    return entity;
  }

}