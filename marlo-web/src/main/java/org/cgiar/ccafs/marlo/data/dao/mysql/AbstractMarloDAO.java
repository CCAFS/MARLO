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
public abstract class AbstractMarloDAO {

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

  /**
   * This method deletes a record from the database.
   * 
   * @param obj is a persistence instance from the database model.
   * @return true if the record was successfully deleted, false otherwhise.
   */
  protected boolean delete(Object obj) {
    this.sessionFactory.getCurrentSession().delete(obj);
    // Better to not use booleans.
    return true;
  }

  public void executeQuery(String sqlQuery) {

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
  public <T> T find(Class<T> clazz, Object id) {
    T obj = null;
    obj = (T) sessionFactory.getCurrentSession().get(clazz, (Serializable) id);

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
    Query query = sessionFactory.getCurrentSession().createQuery(hibernateQuery);

    @SuppressWarnings("unchecked")
    List<T> list = query.list();
    return list;
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

  protected <T> List<T> findEveryone(Class<T> clazz) {
    Query query = sessionFactory.getCurrentSession().createQuery("from " + clazz.getName());
    @SuppressWarnings("unchecked")
    List<T> list = query.list();
    return list;

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
    Query query = sessionFactory.getCurrentSession().createQuery(hibernateQuery);
    Object object = clazz.cast(query.uniqueResult());
    return object;
  }


  SessionFactory getSessionFactory() {
    return this.sessionFactory;
  }


  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean save(Object obj) {
    sessionFactory.getCurrentSession().save(obj);
    return true;
  }


  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean save(Object obj, String actionName, List<String> relationsName) {
    this.addAuditLogFieldsToThreadStorage(obj, actionName, relationsName);

    sessionFactory.getCurrentSession().save(obj);


    return true;
  }


  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean update(Object obj) {

    obj = sessionFactory.getCurrentSession().merge(obj);
    sessionFactory.getCurrentSession().saveOrUpdate(obj);
    return true;
  }

  /**
   * This method saves or update a record into the database.
   * 
   * @param obj is the Object to be saved/updated.
   * @param actionName the action that called the save
   * @return true if the the save/updated was successfully made, false otherwhise.
   */
  protected boolean update(Object obj, String actionName, List<String> relationsName) {
    /**
     * Need to find a way to ensure the actionName and relationsName are handed to the interceptor.
     */
    this.addAuditLogFieldsToThreadStorage(obj, actionName, relationsName);

    obj = sessionFactory.getCurrentSession().merge(obj);
    sessionFactory.getCurrentSession().saveOrUpdate(obj);

    return true;

  }
}