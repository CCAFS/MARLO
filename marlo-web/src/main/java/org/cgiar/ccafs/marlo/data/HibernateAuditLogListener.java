/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
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


package org.cgiar.ccafs.marlo.data;

import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.utils.AuditLogContext;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.shiro.util.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OrderedSetType;
import org.hibernate.type.SetType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Most of the code here was previously in a Hibernate Interceptor but has been refactored to use Hibernate
 * Event Listener framework. The Listeners need to be registered in the Hibernate
 * Configuration for the callback methods to work.
 * 
 * @author Christian Garcia, Grant Lay
 */
public class HibernateAuditLogListener
  implements PostDeleteEventListener, PostUpdateEventListener, PostInsertEventListener, FlushEventListener {

  /**
   * This private class handles the insertion into the AuditLog Table.
   * This happens postFlush but not before commit to the database.
   * 
   * @author GrantL
   */
  private class MARLOAuditBeforeTransactionCompletionProcess implements BeforeTransactionCompletionProcess {


    private List<Auditlog> createAllAuditLogsForCrudType(String function, String transactionId,
      Set<Map<String, Object>> records, String actionName, SessionFactory sessionFactory) {

      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      List<Auditlog> auditLogs = new ArrayList<>();


      for (Map<String, Object> record : records) {
        /**
         * See comment below in regards to simplifying.
         */
        if (record.get(IAuditLog.PRINCIPAL).toString().equals("1")) {
          IAuditLog entity = (IAuditLog) record.get(IAuditLog.ENTITY);
          entity = this.loadRelations(entity, true, 1, sessionFactory);
          Auditlog auditLog = this.createAuditlog(function, entity, gson.toJson(entity), entity.getModifiedBy().getId(),
            transactionId, new Long(record.get(IAuditLog.PRINCIPAL).toString()), null, actionName);
          auditLogs.add(auditLog);
        } else {
          /**
           * This complex logic might be able to be simplified by using
           * a @PostCollectionUpdateEventListener, @PostCollectionRemoveEventListener or
           * a @PostCollectionRecreateEventListener to detect changes to child relations. For now just leave it as is
           * until I understand the code better.
           */
          Set<IAuditLog> collectionRecords = (Set<IAuditLog>) record.get(IAuditLog.ENTITY);
          for (IAuditLog collectionRecord : collectionRecords) {
            collectionRecord = this.loadRelations(collectionRecord, false, 2, sessionFactory);
            if (collectionRecord.isActive()) {
              String json = gson.toJson(collectionRecord);

              Auditlog auditlog =
                this.createAuditlog(IAuditLog.UPDATED, collectionRecord, json, collectionRecord.getModifiedBy().getId(),
                  transactionId, new Long(record.get(IAuditLog.PRINCIPAL).toString()),
                  record.get(IAuditLog.RELATION_NAME).toString(), actionName);
              auditLogs.add(auditlog);
            }
          }

        }

      }

      return auditLogs;
    }

    private List<Auditlog> createAllAuditLogsForTransaction(SessionFactory sessionFactory) {


      String transactionId = UUID.randomUUID().toString();

      // Use the AuditLogContextHelper to retrieve the updates, inserts and deletes and other context information
      AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

      List<Auditlog> allAuditlogsForTransaction = new ArrayList<>();

      allAuditlogsForTransaction.addAll(this.createAllAuditLogsForCrudType(IAuditLog.SAVED, transactionId,
        auditLogContext.getInserts(), auditLogContext.getActionName(), sessionFactory));
      allAuditlogsForTransaction.addAll(this.createAllAuditLogsForCrudType(IAuditLog.UPDATED, transactionId,
        auditLogContext.getUpdates(), auditLogContext.getActionName(), sessionFactory));
      allAuditlogsForTransaction.addAll(this.createAllAuditLogsForCrudType(IAuditLog.DELETED, transactionId,
        auditLogContext.getDeletes(), auditLogContext.getActionName(), sessionFactory));

      return allAuditlogsForTransaction;
    }

    private Auditlog createAuditlog(String action, IAuditLog entity, String json, long userId, String transactionId,
      Long principal, String relationName, String actionName) {
      StringBuilder detailBuilder = new StringBuilder();
      if (actionName == null) {
        detailBuilder.append(entity.getLogDeatil());
      } else {
        detailBuilder.append("Action: ").append(actionName).append(" ").append(entity.getLogDeatil());
      }
      Auditlog auditRecord = new Auditlog(action, detailBuilder.toString(), new Date(), entity.getId().toString(),
        entity.getClass().toString(), json, userId, transactionId, principal, relationName,
        entity.getModificationJustification());
      return auditRecord;
    }

    /**
     * Entry point for our BeforeTransactionCompletionProcess.
     */
    @Override
    public void doBeforeTransactionCompletion(SessionImplementor sessionImplementor) {
      LOG.debug("begin doAfterTransactionCompletion");

      AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

      SessionFactory sessionFactory = sessionImplementor.getFactory().getCurrentSession().getSessionFactory();

      /**
       * This check may no longer be required.
       */
      if (auditLogContext.getActionName() == null) {
        LOG.debug("No ActionName in auditLogContext so ignoring.");
        return;
      }

      List<Auditlog> createAuditlogs = this.createAllAuditLogsForTransaction(sessionFactory);

      if (CollectionUtils.isEmpty(createAuditlogs)) {
        LOG.info("Audit logs are empty");
        return;
      }

      this.persistAuditLogs(sessionFactory, createAuditlogs);

    }

    /**
     * This code should be simplified!!!
     * 
     * @param entity
     * @param loadUsers
     * @param level
     */
    public IAuditLog loadRelations(IAuditLog entity, boolean loadUsers, int level, SessionFactory sessionFactory) {
      ClassMetadata classMetadata =
        sessionFactory.getClassMetadata(HibernateProxyHelper.getClassWithoutInitializingProxy(entity));

      String[] propertyNames = classMetadata.getPropertyNames();
      for (String name : propertyNames) {
        Object propertyValue = classMetadata.getPropertyValue(entity, name);

        if (propertyValue != null && propertyValue instanceof IAuditLog) {
          Type propertyType = classMetadata.getPropertyType(name);

          if (propertyValue != null && propertyType instanceof ManyToOneType) {

            if (loadUsers) {
              IAuditLog entityRelation = this.unProxyIAuditLogObject(propertyValue, sessionFactory);
              entityRelation = this.loadRelations(entityRelation, false, 2, sessionFactory);
              classMetadata.setPropertyValue(entity, name, entityRelation);
              LOG.debug("set property: " + name + ", on entity: " + entity + ", with value: " + entityRelation);
            } else {
              if (!(name.equals("createdBy") || name.equals("modifiedBy"))) {
                IAuditLog entityRelation = this.unProxyIAuditLogObject(propertyValue, sessionFactory);
                if (level == 2) {
                  entityRelation = this.loadRelations(entityRelation, false, 3, sessionFactory);
                }

                classMetadata.setPropertyValue(entity, name, entityRelation);
                LOG.debug("set property: " + name + ", on entity: " + entity + ", with value: " + entityRelation);
              }
            }


          }
        }

      }

      return entity;
    }


    /**
     * We should not use the same session when writing to the database from an Hibernate Event Listener,
     * therefore we use a openStatelessSession.
     * 
     * @param sessionFactory
     * @param auditLogs
     */
    private void persistAuditLogs(SessionFactory sessionFactory, List<Auditlog> auditLogs) {
      StatelessSession openStatelessSession = null;
      try {
        openStatelessSession = sessionFactory.openStatelessSession();

        openStatelessSession.beginTransaction();

        for (Auditlog auditLog : auditLogs) {
          openStatelessSession.insert(auditLog);
        }
        openStatelessSession.getTransaction().commit();

      } catch (HibernateException e) {
        LOG.error("Unable to insert Auditlog entity");
      } finally {

        if (openStatelessSession != null) {
          openStatelessSession.close();
        }
      }

    }

    private IAuditLog unProxyIAuditLogObject(Object propertyValue, SessionFactory sessionFactory) {
      PersistenceContext persistenceContext =
        ((SessionImplementor) sessionFactory.getCurrentSession()).getPersistenceContext();

      if (!Hibernate.isInitialized(propertyValue)) {
        Hibernate.initialize(propertyValue);
      }
      IAuditLog iAuditLog = (IAuditLog) persistenceContext.unproxy(propertyValue);

      return iAuditLog;

    }


  }

  public static final Logger LOG = LoggerFactory.getLogger(HibernateAuditLogListener.class);


  private static final long serialVersionUID = 1L;

  /**
   * This code should be simplified!!!
   * 
   * @param entity
   * @return
   */
  public Set<HashMap<String, Object>> loadListOfRelations(IAuditLog entity, SessionFactory sessionFactory) {
    Set<HashMap<String, Object>> setRelations = new HashSet<>();

    ClassMetadata classMetadata = sessionFactory.getClassMetadata(entity.getClass());
    String[] propertyNames = classMetadata.getPropertyNames();

    for (String name : propertyNames) {

      Object propertyValue = classMetadata.getPropertyValue(entity, name);
      Type propertyType = classMetadata.getPropertyType(name);


      if (propertyValue != null && (propertyType instanceof OrderedSetType || propertyType instanceof SetType)) {

        /** Add instanceof check to ensure no classCast exceptions. **/
        Set<?> checkElementCollectionTypes = (Set<?>) propertyValue;
        /** Java type erasure means we have to do it this way **/
        if (checkElementCollectionTypes.size() == 0
          || !(checkElementCollectionTypes.iterator().next() instanceof IAuditLog)) {
          /** We don't want to record entities that are not instances of IAuditLog such as SectionStatus **/
          continue;
        }

        HashMap<String, Object> objects = new HashMap<>();
        Set<IAuditLog> listRelation = new HashSet<>();

        /** Our instanceof check in the if statement above ensures that we will only be dealing with IAuditLog **/
        @SuppressWarnings(value = {"unchecked"})
        Set<IAuditLog> entityRelation = (Set<IAuditLog>) propertyValue;
        for (IAuditLog iAuditLog : entityRelation) {

          if (iAuditLog.isActive()) {

            listRelation.add(iAuditLog);
          }
        }


        objects.put(IAuditLog.ENTITY, listRelation);
        objects.put(IAuditLog.PRINCIPAL, "3");
        objects.put(IAuditLog.RELATION_NAME, propertyType.getName() + ":" + entity.getId());
        setRelations.add(objects);
      }


    }

    return setRelations;
  }


  /**
   * Unfortunately this is not the equivalent of the postFlush method in an Hibernate interceptor,
   * as it gets called before the pre and post update/save/delete methods.
   */
  @Override
  public void onFlush(FlushEvent flushEvent) throws HibernateException {
    /** Sometimes good to know when a Flush event happens **/
    LOG.debug("begin onFlushEvent");
  }


  @Override
  public void onPostDelete(PostDeleteEvent postDeleteEvent) {

    Object entity = postDeleteEvent.getEntity();
    LOG.debug("begin onPostDelete for Entity : " + entity);

    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

    /**
     * This is because our save(entity), update(entity) and delete(entity) methods on our DAOs should not
     * execute the listeners. There might be a better way to do this like conditionally active the listeners.
     */
    if (auditLogContext.getActionName() == null && CollectionUtils.isEmpty(auditLogContext.getRelationsNames())) {
      LOG.debug("auditLogContext has not been initiliazed for this request, so not audit logging");
      return;
    }

    /**
     * We might delete many entities on a single request, but we only want to do the audit logging on one.
     */
    if (!entity.getClass().getCanonicalName().equals(auditLogContext.getEntityCanonicalName())) {
      LOG.debug("Entity : " + entity + " , is not the entity we want to audit log");
      return;
    }

    HashMap<String, Object> deleteRecord = new HashMap<>();
    if (entity instanceof IAuditLog) {
      deleteRecord.put(IAuditLog.ENTITY, entity);
      deleteRecord.put(IAuditLog.PRINCIPAL, new Long(1));
      auditLogContext.getDeletes().add(deleteRecord);

      ClassMetadata classMetadata =
        postDeleteEvent.getSession().getSessionFactory().getClassMetadata(entity.getClass());
      Type[] types = classMetadata.getPropertyTypes();
      auditLogContext.getDeletes().addAll(this.relations(postDeleteEvent.getDeletedState(), types,
        ((IAuditLog) entity).getId(), true, postDeleteEvent.getSession().getSessionFactory()));
    }

    postDeleteEvent.getSession().getActionQueue().registerProcess(new MARLOAuditBeforeTransactionCompletionProcess());
  }

  @Override
  public void onPostInsert(PostInsertEvent postInsertEvent) {

    Object entity = postInsertEvent.getEntity();
    LOG.debug("begin onPostInsert for Entity : " + entity);

    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

    /**
     * This is because our save(entity), update(entity) and delete(entity) methods on our DAOs should not
     * execute the listeners. There might be a better way to do this like conditionally active the listeners.
     */
    if (auditLogContext.getActionName() == null && CollectionUtils.isEmpty(auditLogContext.getRelationsNames())) {
      LOG.debug("No audit log context setup for insert on entity: " + entity);
      return;
    }

    /**
     * We might save many entities on a single request, but we only want to do the audit logging on one.
     */
    if (!entity.getClass().getCanonicalName().equals(auditLogContext.getEntityCanonicalName())) {
      LOG.debug("Entity : " + entity + " , is not the entity we want to audit log");
      return;
    }


    HashMap<String, Object> insertRecord = new HashMap<>();
    if (entity instanceof IAuditLog) {
      insertRecord.put(IAuditLog.ENTITY, entity);
      insertRecord.put(IAuditLog.PRINCIPAL, new Long(1));
      auditLogContext.getInserts().add(insertRecord);
      ClassMetadata classMetadata =
        postInsertEvent.getSession().getSessionFactory().getClassMetadata(entity.getClass());
      Type[] types = classMetadata.getPropertyTypes();

      auditLogContext.getInserts().addAll(this.relations(postInsertEvent.getState(), types,
        ((IAuditLog) entity).getId(), true, postInsertEvent.getSession().getSessionFactory()));
    }
    postInsertEvent.getSession().getActionQueue().registerProcess(new MARLOAuditBeforeTransactionCompletionProcess());

  }


  @Override
  public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
    Object entity = postUpdateEvent.getEntity();
    LOG.debug("begin onPostUpdatefor Entity : " + entity);

    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

    /**
     * This is because our save(entity), update(entity) and delete(entity) methods on our DAOs should not
     * execute the listeners. There might be a better way to do this like conditionally activate the listeners.
     */
    if (auditLogContext.getActionName() == null && CollectionUtils.isEmpty(auditLogContext.getRelationsNames())) {
      LOG.debug("No audit log context setup for update on entity: " + entity);
      return;
    }

    /**
     * We might save many entities on a single request, but we only want to do the audit logging on one.
     */
    if (!entity.getClass().getCanonicalName().equals(auditLogContext.getEntityCanonicalName())) {
      LOG.debug("Entity : " + entity + " , is not the entity we want to audit log");
      return;
    }

    HashMap<String, Object> updateRecord = new HashMap<>();
    if (entity instanceof IAuditLog) {
      updateRecord.put(IAuditLog.ENTITY, entity);
      updateRecord.put(IAuditLog.PRINCIPAL, new Long(1));
      auditLogContext.getUpdates().add(updateRecord);

      ClassMetadata classMetadata =
        postUpdateEvent.getSession().getSessionFactory().getClassMetadata(entity.getClass());
      Type[] types = classMetadata.getPropertyTypes();

      auditLogContext.getUpdates().addAll(this.relations(postUpdateEvent.getState(), types,
        ((IAuditLog) entity).getId(), true, postUpdateEvent.getSession().getSessionFactory()));
    }

    // LOG.debug("COMPARE LOGS WITH STAGING BRANCH: " + auditLogContext.getUpdates().toString());
    postUpdateEvent.getSession().getActionQueue().registerProcess(new MARLOAuditBeforeTransactionCompletionProcess());

  }


  public Set<HashMap<String, Object>> relations(Object[] state, Type[] types, Object id, boolean firstRelations,
    SessionFactory sessionFactory) {

    Set<HashMap<String, Object>> relations = new HashSet<>();
    int i = 0;
    String parentId = "";
    try {
      parentId = id.toString();
    } catch (Exception e1) {
      e1.printStackTrace();
      parentId = "";
    }
    /**
     * We load and refresh the object to get the relations updated.
     * Christian Garcia
     */


    for (Type type : types) {
      HashMap<String, Object> objects = new HashMap<>();

      if (type instanceof OrderedSetType || type instanceof SetType) {

        if (AuditLogContextProvider.getAuditLogContext().getRelationsNames().contains(type.getName())) {
          Set<IAuditLog> listRelation = new HashSet<>();


          Set<Object> set = null;
          try {
            set = (Set<Object>) state[i];
          } catch (Exception e1) {
            LOG.info("Could not load relations");
          }

          if (set != null && !set.isEmpty()) {
            Object reObject = sessionFactory.getCurrentSession()
              .get(AuditLogContextProvider.getAuditLogContext().getEntityCanonicalName(), (Serializable) id);
            sessionFactory.getCurrentSession().refresh(reObject);
            ClassMetadata metadata = sessionFactory.getClassMetadata(reObject.getClass());
            Object[] values = metadata.getPropertyValues(reObject);
            set = (Set<Object>) values[i];
            for (Object iAuditLog : set) {
              if (iAuditLog instanceof IAuditLog) {
                IAuditLog audit = (IAuditLog) iAuditLog;
                if (audit.isActive()) {
                  try {
                    String name = audit.getClass().getName();
                    Class<?> className = Class.forName(name);

                    /**
                     * Ensure that lazy loaded collections are at least proxy instances (if not fetched).
                     * If you are not getting all collections auditLogged it is because the Action classes are
                     * persisting the detached entity and not copying across the managed collections (e.g. the ones
                     * in our entities that are generally using Set). To overcome refactor the Action classes to copy
                     * the List collections, which are full of detached entities, see OutcomeAction save method for an
                     * example.
                     * When issue #1124 is solved, this won't be a problem.
                     */
                    /**
                     * We load the object to get the id assigned
                     * Christian Garcia
                     */

                    Object obj = sessionFactory.getCurrentSession().get(className, (Serializable) audit.getId());
                    if (obj == null) {


                      /**
                       * This is likely to be that the entity has just been hard deleted (MARLO has a mixture of
                       * entities
                       * where some are soft deleted and others are hard deleted).
                       */
                      LOG.info("IAuditLog obj with className: " + className + ", and id: " + audit.getId()
                        + " can not be found");
                      // Add the audit from the state object array
                      listRelation.add(audit);
                      continue;
                    }


                    listRelation.add((IAuditLog) obj);
                    Set<HashMap<String, Object>> loadList = this.loadListOfRelations((IAuditLog) obj, sessionFactory);
                    for (HashMap<String, Object> hashMap : loadList) {
                      HashSet<IAuditLog> relationAudit = (HashSet<IAuditLog>) hashMap.get(IAuditLog.ENTITY);
                      for (IAuditLog iAuditLog2 : relationAudit) {
                        Set<HashMap<String, Object>> loadListRelations =
                          this.loadListOfRelations(iAuditLog2, sessionFactory);

                        relations.addAll(loadListRelations);
                      }
                    }


                    relations.addAll(loadList);
                  } catch (ClassNotFoundException e) {

                    LOG.error(e.getLocalizedMessage());
                  }
                }


              }


            }
            if (!listRelation.isEmpty()) {
              objects.put(IAuditLog.ENTITY, listRelation);
              objects.put(IAuditLog.PRINCIPAL, "3");
              objects.put(IAuditLog.RELATION_NAME, type.getName() + ":" + parentId);
              relations.add(objects);
            }

          }
        }

      }
      i++;
    }

    return relations;
  }

  /**
   * Setting this to true ensures our BeforeTransactionCompletionProcess get kicked off.
   */
  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return true;
  }


}