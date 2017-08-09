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
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.Initializable;
import org.hibernate.event.PostCollectionRecreateEvent;
import org.hibernate.event.PostCollectionRecreateEventListener;
import org.hibernate.event.PostCollectionRemoveEvent;
import org.hibernate.event.PostCollectionRemoveEventListener;
import org.hibernate.event.PostCollectionUpdateEvent;
import org.hibernate.event.PostCollectionUpdateEventListener;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OrderedSetType;
import org.hibernate.type.SetType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This was previously an Hibernate Interceptor but has been refactored to use Hibernate
 * Event Listener framework. The Listeners need to be registered in the Hibernate
 * Configuration for the callback methods to work.
 * 
 * @author Christian Garcia, Grant Lay
 */
public class HibernateAuditLogListener
  implements PreDeleteEventListener, PreUpdateEventListener, PreInsertEventListener, PostDeleteEventListener,
  PostUpdateEventListener, PostInsertEventListener, FlushEventListener, PostCollectionUpdateEventListener,
  PostCollectionRemoveEventListener, PostCollectionRecreateEventListener, Initializable {

  public static Logger LOG = LoggerFactory.getLogger(HibernateAuditLogListener.class);
  private static final long serialVersionUID = 1L;

  private final String PRINCIPAL = "PRINCIPAL";
  private final String ENTITY = "entity";
  private final String RELATION_NAME = "relationName";

  private final String SAVED = "Saved";
  private final String UPDATED = "Updated";
  private final String DELETED = "Deleted";


  private List<Auditlog> createAllAuditLogsForCrudType(String function, String transactionId,
    Set<Map<String, Object>> records, String actionName, SessionFactory sessionFactory) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    List<Auditlog> auditLogs = new ArrayList<>();


    for (Map<String, Object> record : records) {
      /**
       * See comment below in regards to simplifying.
       */
      if (record.get(PRINCIPAL).toString().equals("1")) {
        IAuditLog entity = (IAuditLog) record.get(ENTITY);

        Auditlog auditLog = this.createAuditlog(SAVED, entity, gson.toJson(entity), entity.getModifiedBy().getId(),
          transactionId, new Long(record.get(PRINCIPAL).toString()), null, actionName);
        auditLogs.add(auditLog);
      } else {
        /**
         * This complex logic might be able to be simplified by using
         * a @PostCollectionUpdateEventListener, @PostCollectionRemoveEventListener or
         * a @PostCollectionRecreateEventListener to detect changes to child relations. For now just leave it as is
         * until I understand the code better.
         */
        Set<IAuditLog> collectionRecords = (Set<IAuditLog>) record.get(ENTITY);
        for (IAuditLog collectionRecord : collectionRecords) {
          this.loadRelations(collectionRecord, false, 2, sessionFactory);
          if (collectionRecord.isActive()) {
            String json = gson.toJson(collectionRecord);

            Auditlog auditlog = this.createAuditlog(UPDATED, collectionRecord, json,
              collectionRecord.getModifiedBy().getId(), transactionId, new Long(record.get(PRINCIPAL).toString()),
              record.get(RELATION_NAME).toString(), actionName);
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

    allAuditlogsForTransaction.addAll(this.createAllAuditLogsForCrudType(SAVED, transactionId,
      auditLogContext.getInserts(), auditLogContext.getActionName(), sessionFactory));
    allAuditlogsForTransaction.addAll(this.createAllAuditLogsForCrudType(UPDATED, transactionId,
      auditLogContext.getUpdates(), auditLogContext.getActionName(), sessionFactory));
    allAuditlogsForTransaction.addAll(this.createAllAuditLogsForCrudType(DELETED, transactionId,
      auditLogContext.getDeletes(), auditLogContext.getActionName(), sessionFactory));

    return allAuditlogsForTransaction;
  }


  // @Override
  // public void initialize(Configuration arg0) {
  // LOG.debug("initializing HibernateAuditLogListener");
  // }

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

  @Override
  public void initialize(Configuration arg0) {
    LOG.debug("Initializing HibernateAuditLogListener");

  }


  /**
   * This code should be simplified!!!
   * 
   * @param entity
   * @return
   */
  public Set<HashMap<String, Object>> loadList(IAuditLog entity, SessionFactory sessionFactory) {
    Set<HashMap<String, Object>> setRelations = new HashSet<>();

    ClassMetadata classMetadata = sessionFactory.getClassMetadata(entity.getClass());
    String[] propertyNames = classMetadata.getPropertyNames();
    for (String name : propertyNames) {

      Object propertyValue = classMetadata.getPropertyValue(entity, name, EntityMode.POJO);
      Type propertyType = classMetadata.getPropertyType(name);

      if (propertyValue != null && (propertyType instanceof OrderedSetType || propertyType instanceof SetType)) {
        HashMap<String, Object> objects = new HashMap<>();
        Set<IAuditLog> listRelation = new HashSet<>();

        Set<IAuditLog> entityRelation = (Set<IAuditLog>) propertyValue;
        try {
          for (IAuditLog iAuditLog : entityRelation) {

            if (iAuditLog.isActive()) {

              listRelation.add(iAuditLog);
            }
          }
        } catch (Exception e) {

        }

        objects.put(ENTITY, listRelation);
        objects.put(PRINCIPAL, "3");
        objects.put(RELATION_NAME, propertyType.getName() + ":" + entity.getId());
        setRelations.add(objects);
      }


    }

    return setRelations;
  }

  /**
   * This code should be simplified!!!
   * 
   * @param entity
   * @param loadUsers
   * @param level
   */
  public void loadRelations(IAuditLog entity, boolean loadUsers, int level, SessionFactory sessionFactory) {
    ClassMetadata classMetadata = sessionFactory.getClassMetadata(entity.getClass());


    String[] propertyNames = classMetadata.getPropertyNames();
    for (String name : propertyNames) {
      Object propertyValue = classMetadata.getPropertyValue(entity, name, EntityMode.POJO);

      if (propertyValue != null && propertyValue instanceof IAuditLog) {
        Type propertyType = classMetadata.getPropertyType(name);

        if (propertyValue != null && propertyType instanceof ManyToOneType) {

          if (loadUsers) {
            IAuditLog entityRelation = (IAuditLog) propertyValue;

            Object obj = sessionFactory.getCurrentSession().get(propertyType.getReturnedClass(),
              (Serializable) entityRelation.getId());

            this.loadRelations((IAuditLog) obj, false, 2, sessionFactory);
            classMetadata.setPropertyValue(entity, name, obj, EntityMode.POJO);
          } else {
            if (!(name.equals("createdBy") || name.equals("modifiedBy"))) {
              IAuditLog entityRelation = (IAuditLog) propertyValue;

              Object obj = sessionFactory.getCurrentSession().get(propertyType.getReturnedClass(),
                (Serializable) entityRelation.getId());
              if (level == 2) {
                this.loadRelations((IAuditLog) obj, false, 3, sessionFactory);
              }

              // this.loadRelations((IAuditLog) obj, false);
              classMetadata.setPropertyValue(entity, name, obj, EntityMode.POJO);
            }
          }


        }
      }

    }


  }

  @Override
  public void onFlush(FlushEvent flushEvent) throws HibernateException {
    LOG.debug("begin onFlush");

    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();
    /**
     * This is because our save(entity), update(entity) and delete(entity) methods on our DAOs should not
     * execute the listeners. There might be a better way to do this like conditionally active the listeners.
     */
    if (auditLogContext.getActionName() == null && CollectionUtils.isEmpty(auditLogContext.getRelationsNames())) {
      return;
    }

    SessionFactory sessionFactory = flushEvent.getSession().getSessionFactory();

    List<Auditlog> createAuditlogs = this.createAllAuditLogsForTransaction(sessionFactory);

    if (CollectionUtils.isEmpty(createAuditlogs)) {
      LOG.info("Audit logs are empty");
      return;
    }

    this.persistAuditLogs(sessionFactory, createAuditlogs);
  }

  // @Override
  // public void onFlushEntity(FlushEntityEvent flushEntityEvent) throws HibernateException {
  // LOG.info("onFlushEntity");
  //
  // }

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
      return;
    }

    HashMap<String, Object> deleteRecord = new HashMap<>();
    if (entity instanceof IAuditLog) {
      deleteRecord.put(ENTITY, entity);
      deleteRecord.put(PRINCIPAL, new Long(1));
      auditLogContext.getDeletes().add(deleteRecord);

      ClassMetadata classMetadata =
        postDeleteEvent.getSession().getSessionFactory().getClassMetadata(entity.getClass());
      Type[] types = classMetadata.getPropertyTypes();
      auditLogContext.getDeletes().addAll(this.relations(postDeleteEvent.getDeletedState(), types,
        ((IAuditLog) entity).getId(), true, postDeleteEvent.getSession().getSessionFactory()));
    }
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
      return;
    }

    HashMap<String, Object> insertRecord = new HashMap<>();
    if (entity instanceof IAuditLog) {
      insertRecord.put(ENTITY, entity);
      insertRecord.put(PRINCIPAL, new Long(1));
      auditLogContext.getInserts().add(insertRecord);
      ClassMetadata classMetadata =
        postInsertEvent.getSession().getSessionFactory().getClassMetadata(entity.getClass());
      Type[] types = classMetadata.getPropertyTypes();

      auditLogContext.getInserts().addAll(this.relations(postInsertEvent.getState(), types,
        ((IAuditLog) entity).getId(), true, postInsertEvent.getSession().getSessionFactory()));
    }

  }

  @Override
  public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
    LOG.info("onPostRecreateCollection for Parent Entity: " + event.getAffectedOwnerEntityName()
      + " , and collection : " + event.getCollection().getClass());

  }

  @Override
  public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
    LOG.info("onPostRemoveCollection for Parent Entity: " + event.getAffectedOwnerEntityName() + " , and collection :"
      + event.getCollection().getClass());
    LOG.info("onPostRemoveCollection");
  }


  @Override
  public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
    Object entity = postUpdateEvent.getEntity();
    LOG.debug("begin onPostUpdatefor Entity : " + entity);

    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

    /**
     * This is because our save(entity), update(entity) and delete(entity) methods on our DAOs should not
     * execute the listeners. There might be a better way to do this like conditionally active the listeners.
     */
    if (auditLogContext.getActionName() == null && CollectionUtils.isEmpty(auditLogContext.getRelationsNames())) {
      return;
    }

    HashMap<String, Object> updateRecord = new HashMap<>();
    if (entity instanceof IAuditLog) {
      updateRecord.put(ENTITY, entity);
      updateRecord.put(PRINCIPAL, new Long(1));
      auditLogContext.getUpdates().add(updateRecord);

      ClassMetadata classMetadata =
        postUpdateEvent.getSession().getSessionFactory().getClassMetadata(entity.getClass());
      Type[] types = classMetadata.getPropertyTypes();

      auditLogContext.getUpdates().addAll(this.relations(postUpdateEvent.getState(), types,
        ((IAuditLog) entity).getId(), true, postUpdateEvent.getSession().getSessionFactory()));
    }

  }

  @Override
  public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
    LOG.info("onPostUpdateCollection for Parent Entity: " + event.getAffectedOwnerEntityName() + " , and collection : "
      + event.getCollection().getClass());

  }

  @Override
  public boolean onPreDelete(PreDeleteEvent preDeleteEvent) {
    LOG.debug("onPreDelete for entity: " + preDeleteEvent.getEntity());
    return false;
  }

  @Override
  public boolean onPreInsert(PreInsertEvent preInsertEvent) {
    LOG.debug("onPreInsert for entity: " + preInsertEvent.getEntity());
    return false;
  }

  @Override
  public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
    LOG.debug("onPreUpdate for entity: " + preUpdateEvent.getEntity());
    return false;
  }

  /**
   * We should not use the same session we writing to the database from an Hibernate Event Listener,
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

  public Set<HashMap<String, Object>> relations(Object[] state, Type[] types, Object id, boolean firstRelations,
    SessionFactory sessionFactory) {

    Set<HashMap<String, Object>> relations = new HashSet<>();
    int i = 0;
    String parentId = "";
    try {
      parentId = id.toString();
    } catch (Exception e1) {
      parentId = "";
    }
    for (Type type : types) {
      HashMap<String, Object> objects = new HashMap<>();

      if (type instanceof OrderedSetType || type instanceof SetType) {

        if (AuditLogContextProvider.getAuditLogContext().getRelationsNames().contains(type.getName())) {
          Set<IAuditLog> listRelation = new HashSet<>();
          Set<Object> set = (Set<Object>) state[i];
          if (set != null) {
            for (Object iAuditLog : set) {
              if (iAuditLog instanceof IAuditLog) {
                IAuditLog audit = (IAuditLog) iAuditLog;
                if (audit.isActive()) {
                  try {
                    String name = audit.getClass().getName();
                    Class<?> className = Class.forName(name);

                    Object obj = sessionFactory.getCurrentSession().get(className, (Serializable) audit.getId());


                    listRelation.add((IAuditLog) obj);
                    Set<HashMap<String, Object>> loadList = this.loadList((IAuditLog) obj, sessionFactory);
                    for (HashMap<String, Object> hashMap : loadList) {
                      HashSet<IAuditLog> relationAudit = (HashSet<IAuditLog>) hashMap.get(ENTITY);
                      for (IAuditLog iAuditLog2 : relationAudit) {
                        Set<HashMap<String, Object>> loadListRelations = this.loadList(iAuditLog2, sessionFactory);

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
              objects.put(ENTITY, listRelation);
              objects.put(PRINCIPAL, "3");
              objects.put(RELATION_NAME, type.getName() + ":" + parentId);
              relations.add(objects);
            }

          }
        }

      }
      i++;
    }

    return relations;
  }


}