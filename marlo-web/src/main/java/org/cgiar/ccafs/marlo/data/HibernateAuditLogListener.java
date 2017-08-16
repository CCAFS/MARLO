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

import org.cgiar.ccafs.marlo.utils.AuditLogContext;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.util.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;
import org.hibernate.event.spi.PostCollectionRecreateEvent;
import org.hibernate.event.spi.PostCollectionRecreateEventListener;
import org.hibernate.event.spi.PostCollectionRemoveEvent;
import org.hibernate.event.spi.PostCollectionRemoveEventListener;
import org.hibernate.event.spi.PostCollectionUpdateEvent;
import org.hibernate.event.spi.PostCollectionUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
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
public class HibernateAuditLogListener implements PreDeleteEventListener, PreUpdateEventListener,
  PreInsertEventListener, PostDeleteEventListener, PostUpdateEventListener, PostInsertEventListener, FlushEventListener,
  PostCollectionUpdateEventListener, PostCollectionRemoveEventListener, PostCollectionRecreateEventListener {

  public static Logger LOG = LoggerFactory.getLogger(HibernateAuditLogListener.class);
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
        HashMap<String, Object> objects = new HashMap<>();
        Set<IAuditLog> listRelation = new HashSet<>();

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

  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    // TODO Auto-generated method stub
    return false;
  }


}