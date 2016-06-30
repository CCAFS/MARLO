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


package org.cgiar.ccafs.marlo.data;

import org.cgiar.ccafs.marlo.data.dao.mysql.StandardDAO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.OrderedSetType;
import org.hibernate.type.SetType;
import org.hibernate.type.Type;

/**
 * This is a EmptyInterceptor triggered when the data will be change ,
 * add and remove and save to audit log table
 * 
 * @author Christian Garcia
 */
@Singleton
public class AuditLogInterceptor extends EmptyInterceptor {

  private static final long serialVersionUID = -900829831186014812L;
  Session session;
  private Set<Map<String, Object>> inserts;
  private Set<Map<String, Object>> updates;
  private Set<Map<String, Object>> deletes;
  private StandardDAO dao;
  private final String PRINCIPAL = "PRINCIPAL";
  private final String ENTITY = "entity";
  private final String RELATION_NAME = "relationName";
  private long transactionId;

  public AuditLogInterceptor() {
    this.dao = new StandardDAO();
    inserts = new HashSet<Map<String, Object>>();
    updates = new HashSet<Map<String, Object>>();
    deletes = new HashSet<Map<String, Object>>();


  }


  public void loadRelations(IAuditLog entity, boolean loadUsers) {
    ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(entity.getClass());
    String[] propertyNames = classMetadata.getPropertyNames();
    for (String name : propertyNames) {
      Object propertyValue = classMetadata.getPropertyValue(entity, name, EntityMode.POJO);
      Type propertyType = classMetadata.getPropertyType(name);
      if (propertyValue != null && propertyType instanceof ManyToOneType) {

        if (loadUsers) {
          IAuditLog entityRelation = (IAuditLog) propertyValue;
          Object obj = dao.find(propertyType.getReturnedClass(), entityRelation.getId());
          this.loadRelations((IAuditLog) obj, false);
          classMetadata.setPropertyValue(entity, name, obj, EntityMode.POJO);
        } else {
          if (!(name.equals("createdBy") || name.equals("modifiedBy"))) {
            IAuditLog entityRelation = (IAuditLog) propertyValue;
            Object obj = dao.find(propertyType.getReturnedClass(), entityRelation.getId());
            this.loadRelations((IAuditLog) obj, false);
            classMetadata.setPropertyValue(entity, name, obj, EntityMode.POJO);
          }
        }


      }
    }
  }

  public void logSaveAndUpdate(String function, Set<Map<String, Object>> elements) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    for (Iterator<Map<String, Object>> it = elements.iterator(); it.hasNext();) {
      Map<String, Object> map = it.next();
      if (map.get(PRINCIPAL) == null || map.get(PRINCIPAL).toString().equals("1")) {
        IAuditLog entity = (IAuditLog) map.get(ENTITY);
        this.loadRelations(entity, true);
        String json = gson.toJson(entity);
        if (map.containsKey(PRINCIPAL)) {
          dao.logIt(function, entity, json, entity.getModifiedBy().getId(), this.transactionId,
            new Long(map.get(PRINCIPAL).toString()), null);
        }
      } else {
        Set<IAuditLog> set = (Set<IAuditLog>) map.get(ENTITY);
        for (IAuditLog iAuditLog : set) {
          String json = gson.toJson(iAuditLog);
          if (map.containsKey(PRINCIPAL)) {
            dao.logIt("Updated", iAuditLog, json, iAuditLog.getModifiedBy().getId(), this.transactionId,
              new Long(map.get(PRINCIPAL).toString()), map.get(RELATION_NAME).toString());
          }
        }
      }


    }
  }


  /**
   * delete an object, the object is not delete into database yet.
   */
  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

    System.out.println("onDelete");

    HashMap<String, Object> objects = new HashMap<>();

    if (entity instanceof IAuditLog) {
      objects.put(ENTITY, entity);
      objects.put("PRINCIPAL", new Long(1));

      deletes.add(objects);
    }
  }

  /**
   * this method triggered when update an object, the object is not update into database yet.
   */
  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
    String[] propertyNames, Type[] types) throws CallbackException {
    HashMap<String, Object> objects = new HashMap<>();
    if (entity instanceof IAuditLog) {
      if (!((IAuditLog) entity).isActive()) {
        objects.put(ENTITY, entity);
        objects.put("PRINCIPAL", new Long(1));

        deletes.add(objects);

      } else {
        objects.put(ENTITY, entity);
        objects.put(PRINCIPAL, new Long(1));
        updates.add(objects);

        updates.addAll(this.relations(currentState, types, propertyNames));
      }

    }
    return false;

  }


  /**
   * this method triggered when save an object, the object is not save into database yet.
   */
  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
    throws CallbackException {
    HashMap<String, Object> objects = new HashMap<>();
    if (entity instanceof IAuditLog) {
      objects.put(ENTITY, entity);
      objects.put(PRINCIPAL, new Long(1));
      inserts.add(objects);


      inserts.addAll(this.relations(state, types, propertyNames));
    }
    return false;

  }

  /**
   * this method triggered after the saved, updated or deleted objects are committed to database.
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void postFlush(Iterator iterator) {

    String transactionId = dao.findCustomQuery("select IFNULL(max(transaction_id),0) as 'transactionId' from auditlog")
      .get(0).get("transactionId").toString();
    this.transactionId = Long.parseLong(transactionId);
    this.transactionId++;

    try {

      this.logSaveAndUpdate("Saved", inserts);
      this.logSaveAndUpdate("Updated", updates);
      this.logSaveAndUpdate("Deleted", deletes);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      inserts.clear();
      updates.clear();
      deletes.clear();
    }
  }

  /**
   * this method triggered before the saved, updated or deleted objects are committed to database (usually before
   * postFlush).
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void preFlush(Iterator iterator) {

  }


  public Set<HashMap<String, Object>> relations(Object[] state, Type[] types, String[] propertyNames) {

    Set<HashMap<String, Object>> relations = new HashSet<>();
    int i = 0;
    for (Type type : types) {
      HashMap<String, Object> objects = new HashMap<>();

      if (type instanceof OrderedSetType || type instanceof SetType) {
        Set<IAuditLog> listRelation = new HashSet<>();
        Set<Object> set = (Set<Object>) state[i];
        if (set != null) {
          for (Object iAuditLog : set) {
            if (iAuditLog instanceof IAuditLog) {
              IAuditLog audit = (IAuditLog) iAuditLog;
              if (audit.isActive()) {
                try {
                  String name = audit.getClass().getName();
                  Class className = Class.forName(name);
                  Object obj = dao.find(className, audit.getId());
                  listRelation.add(audit);
                } catch (ClassNotFoundException e) {

                  e.printStackTrace();
                }
              }


            }


          }
          if (!listRelation.isEmpty()) {
            objects.put(ENTITY, listRelation);
            objects.put(PRINCIPAL, "3");
            objects.put(RELATION_NAME, type.getName());
            relations.add(objects);
          }

        }
      }
      i++;
    }
    return relations;
  }

  public void setSession(Session session) {
    this.session = session;
  }
}