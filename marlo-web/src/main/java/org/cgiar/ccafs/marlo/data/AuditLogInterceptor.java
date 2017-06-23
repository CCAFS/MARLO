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

import org.cgiar.ccafs.marlo.data.dao.mysql.StandardDAO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a EmptyInterceptor triggered when the data will be change ,
 * add and remove and save to audit log table
 * 
 * @author Christian Garcia
 */
@Singleton
public class AuditLogInterceptor extends EmptyInterceptor {

  public static Logger LOG = LoggerFactory.getLogger(AuditLogInterceptor.class);
  private static final long serialVersionUID = -900829831186014812L;
  Session session;
  private Set<Map<String, Object>> inserts;
  private Set<Map<String, Object>> updates;
  private Set<Map<String, Object>> deletes;
  private StandardDAO dao;
  private final String PRINCIPAL = "PRINCIPAL";
  private final String ENTITY = "entity";
  private final String RELATION_NAME = "relationName";
  private String transactionId;
  private String actionName;
  private List<String> relationsName;


  public AuditLogInterceptor() {
    this.dao = new StandardDAO();
    inserts = new HashSet<Map<String, Object>>();
    updates = new HashSet<Map<String, Object>>();
    deletes = new HashSet<Map<String, Object>>();


  }


  public String getActionName() {
    return actionName;
  }


  public List<String> getRelationsName() {
    return relationsName;
  }


  public Set<HashMap<String, Object>> loadList(IAuditLog entity) {
    Set<HashMap<String, Object>> setRelations = new HashSet<>();

    ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(entity.getClass());
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


  public void loadRelations(IAuditLog entity, boolean loadUsers, int level) {
    ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(entity.getClass());


    String[] propertyNames = classMetadata.getPropertyNames();
    for (String name : propertyNames) {
      Object propertyValue = classMetadata.getPropertyValue(entity, name, EntityMode.POJO);

      if (propertyValue != null && propertyValue instanceof IAuditLog) {
        Type propertyType = classMetadata.getPropertyType(name);

        if (propertyValue != null && propertyType instanceof ManyToOneType) {

          if (loadUsers) {
            IAuditLog entityRelation = (IAuditLog) propertyValue;


            Object obj = dao.find(propertyType.getReturnedClass(), (Serializable) entityRelation.getId());

            this.loadRelations((IAuditLog) obj, false, 2);
            classMetadata.setPropertyValue(entity, name, obj, EntityMode.POJO);
          } else {
            if (!(name.equals("createdBy") || name.equals("modifiedBy"))) {
              IAuditLog entityRelation = (IAuditLog) propertyValue;

              Object obj = dao.find(propertyType.getReturnedClass(), (Serializable) entityRelation.getId());
              if (level == 2) {
                this.loadRelations((IAuditLog) obj, false, 3);
              }

              // this.loadRelations((IAuditLog) obj, false);
              classMetadata.setPropertyValue(entity, name, obj, EntityMode.POJO);
            }
          }


        }
      }

    }


  }


  public void logSaveAndUpdate(String function, Set<Map<String, Object>> elements) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();


    for (Iterator<Map<String, Object>> it = elements.iterator(); it.hasNext();) {
      Map<String, Object> map = it.next();
      if (map.get(PRINCIPAL).toString().equals("1")) {
        IAuditLog entity = (IAuditLog) map.get(ENTITY);
        this.loadRelations(entity, true, 1);
        String json = gson.toJson(entity);

        dao.logIt(function, entity, json, entity.getModifiedBy().getId(), this.transactionId,
          new Long(map.get(PRINCIPAL).toString()), null, actionName);

      } else {
        Set<IAuditLog> set = (Set<IAuditLog>) map.get(ENTITY);
        for (IAuditLog iAuditLog : set) {
          this.loadRelations(iAuditLog, false, 2);
          if (iAuditLog.isActive()) {
            String json = gson.toJson(iAuditLog);

            dao.logIt("Updated", iAuditLog, json, iAuditLog.getModifiedBy().getId(), this.transactionId,
              new Long(map.get(PRINCIPAL).toString()), map.get(RELATION_NAME).toString(), actionName);

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

    HashMap<String, Object> objects = new HashMap<>();

    if (entity instanceof IAuditLog) {
      objects.put(ENTITY, entity);
      objects.put("PRINCIPAL", new Long(1));

      // deletes.add(objects);
      // deletes.addAll(this.relations(state, types, propertyNames, ((IAuditLog) entity).getId(), true));
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
        deletes.addAll(this.relations(currentState, types, propertyNames, ((IAuditLog) entity).getId(), true));
      } else {
        objects.put(ENTITY, entity);
        objects.put(PRINCIPAL, new Long(1));
        updates.add(objects);

        updates.addAll(this.relations(currentState, types, propertyNames, ((IAuditLog) entity).getId(), true));
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


      inserts.addAll(this.relations(state, types, propertyNames, ((IAuditLog) entity).getId(), true));
    }
    return false;

  }

  /**
   * this method triggered after the saved, updated or deleted objects are committed to database.
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void postFlush(Iterator iterator) {

    transactionId = UUID.randomUUID().toString();
    try {

      this.logSaveAndUpdate("Saved", inserts);
      this.logSaveAndUpdate("Updated", updates);
      // this.logSaveAndUpdate("Deleted", deletes);
    } catch (Exception e) {
      e.printStackTrace();
      LOG.error(e.getLocalizedMessage());
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

  public Set<HashMap<String, Object>> relations(Object[] state, Type[] types, String[] propertyNames, Object id,
    boolean firstRelations) {

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

        if (relationsName.contains(type.getName())) {
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

                    Object obj = dao.find(className, (Serializable) audit.getId());


                    listRelation.add((IAuditLog) obj);
                    Set<HashMap<String, Object>> loadList = this.loadList((IAuditLog) obj);
                    for (HashMap<String, Object> hashMap : loadList) {
                      HashSet<IAuditLog> relationAudit = (HashSet<IAuditLog>) hashMap.get(ENTITY);
                      for (IAuditLog iAuditLog2 : relationAudit) {
                        Set<HashMap<String, Object>> loadListRelations = this.loadList(iAuditLog2);

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

  public void setActionName(String actionName) {
    this.actionName = actionName;
  }


  public void setRelationsName(List<String> relationName) {
    this.relationsName = relationName;
  }

  public void setSession(Session session) {
    this.session = session;
  }
}