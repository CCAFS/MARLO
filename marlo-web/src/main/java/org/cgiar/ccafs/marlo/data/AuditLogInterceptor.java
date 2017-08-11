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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.shiro.util.CollectionUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ideally we wouldn't need an interceptor and the event listeners, but I can't seem to find a way to
 * listen for postFlush events. The FlushEventListener seems to be called before the pre and post save/upate/delete
 * methods, therefore have to revert to using the Interceptor for the flushing.
 * 
 * @author GrantL
 */
public class AuditLogInterceptor extends EmptyInterceptor {

  private static final long serialVersionUID = 1L;

  public static Logger LOG = LoggerFactory.getLogger(AuditLogInterceptor.class);

  /**
   * I don't like the sessionFactory being held by the Interceptor. However Gavin King the author of
   * Hibernate doesn't seem to have an issue with it: https://hibernate.atlassian.net/browse/HB-860
   */
  private SessionFactory sessionFactory;

  public AuditLogInterceptor() {
  }

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

        Auditlog auditLog =
          this.createAuditlog(IAuditLog.SAVED, entity, gson.toJson(entity), entity.getModifiedBy().getId(),
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
          this.loadRelations(collectionRecord, false, 2, sessionFactory);
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
   * This code should be simplified!!!
   * 
   * @param entity
   * @param loadUsers
   * @param level
   */
  public void loadRelations(IAuditLog entity, boolean loadUsers, int level, SessionFactory sessionFactory) {
    ClassMetadata classMetadata =
      sessionFactory.getClassMetadata(HibernateProxyHelper.getClassWithoutInitializingProxy(entity));


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


  @Override
  public void postFlush(Iterator iterator) {
    LOG.debug("begin onFlush");

    AuditLogContext auditLogContext = AuditLogContextProvider.getAuditLogContext();

    /**
     * This is because our save(entity), update(entity) and delete(entity) methods on our DAOs should not
     * execute the listeners. There might be a better way to do this like conditionally active the listeners.
     */
    if (auditLogContext.getActionName() == null) {
      LOG.debug("No ActionName in auditLogContext so ignoring flushEvent.");
      return;
    }

    if (sessionFactory.getCurrentSession().isDirty()) {
      LOG.debug("Hibernate session is clean, ignoring flushEvent.");
      return;
    }

    List<Auditlog> createAuditlogs = this.createAllAuditLogsForTransaction(sessionFactory);

    if (CollectionUtils.isEmpty(createAuditlogs)) {
      LOG.info("Audit logs are empty");
      return;
    }

    this.persistAuditLogs(sessionFactory, createAuditlogs);
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
