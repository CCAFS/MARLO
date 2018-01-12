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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
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
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an SQL query.
   */
  public List<Map<String, Object>> excuteStoreProcedure(String storeProcedure, String sqlQuery) {
    this.sessionFactory.getCurrentSession().createSQLQuery(storeProcedure).executeUpdate();
    Query query = this.sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
    query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
    List<Map<String, Object>> result = query.list();
    return result;

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

    Query query = this.sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
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
    T obj = (T) sessionFactory.getCurrentSession().get(clazz, id);
    if (obj != null) {
      this.getSessionFactory().getCurrentSession().refresh(obj);

    }
    return obj;
  }

  protected List<T> findAll(Query hibernateQuery) {
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
    Query query = sessionFactory.getCurrentSession().createQuery(hibernateQuery);
    return this.findAll(query);
  }

  /**
   * This method make a query that returns a not mapped object result from the model.
   * 
   * @param sqlQuery is a string representing an HQL query.
   */
  public List<Map<String, Object>> findCustomQuery(String sqlQuery) {
    Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);
    query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
    List<Map<String, Object>> result = query.list();

    return result;

  }

  protected List<T> findEveryone(Class<T> clazz) {
    Query query = sessionFactory.getCurrentSession().createQuery("from " + clazz.getName());
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
  protected T findSingleResult(Class<T> clazz, Query hibernateQuery) {
    T object = clazz.cast(hibernateQuery.uniqueResult());
    return object;
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
   * This method make a query that returns a single object result from the model.
   * This method was implemented in a generic way, so, the object to be returned will depend on how the method
   * is being called.
   * 
   * @param hibernateQuery is a string representing an HQL query.
   * @return a Object of <T>
   */
  protected T findSingleResult(Class<T> clazz, String hibernateQuery) {
    Query query = sessionFactory.getCurrentSession().createQuery(hibernateQuery);
    return this.findSingleResult(clazz, query);
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
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @return
   */
  protected T refreshEntity(T entity) {
    sessionFactory.getCurrentSession().refresh(entity);
    return entity;
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