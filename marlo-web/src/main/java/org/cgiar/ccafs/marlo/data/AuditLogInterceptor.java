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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
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
  private Set<Object> inserts;
  private Set<Object> updates;
  private Set<Object> deletes;
  private StandardDAO dao;

  public AuditLogInterceptor() {
    this.dao = new StandardDAO();
    inserts = new HashSet<Object>();
    updates = new HashSet<Object>();
    deletes = new HashSet<Object>();
  }

  /**
   * delete an object, the object is not delete into database yet.
   */
  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

    System.out.println("onDelete");

    if (entity instanceof IAuditLog) {
      deletes.add(entity);
    }
  }

  /**
   * this method triggered when update an object, the object is not update into database yet.
   */
  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
    String[] propertyNames, Type[] types) throws CallbackException {

    if (entity instanceof IAuditLog) {
      if (!((IAuditLog) entity).isActive()) {
        deletes.add(entity);
      } else {
        updates.add(entity);
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

    if (entity instanceof IAuditLog) {
      inserts.add(entity);
    }
    return false;

  }

  /**
   * this method triggered after the saved, updated or deleted objects are committed to database.
   */
  @SuppressWarnings("rawtypes")
  @Override
  public void postFlush(Iterator iterator) {


    try {

      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      for (Iterator it = inserts.iterator(); it.hasNext();) {
        IAuditLog entity = (IAuditLog) it.next();
        System.out.println("postFlush - insert");


        String json = gson.toJson(entity);
        dao.logIt("Saved", entity, json, entity.getModifiedBy().getId());
      }

      for (Iterator it = updates.iterator(); it.hasNext();) {
        IAuditLog entity = (IAuditLog) it.next();
        System.out.println("postFlush - update");
        String json = gson.toJson(entity);
        dao.logIt("Updated", entity, json, entity.getModifiedBy().getId());
      }

      for (Iterator it = deletes.iterator(); it.hasNext();) {
        IAuditLog entity = (IAuditLog) it.next();
        System.out.println("postFlush - delete");
        String json = gson.toJson(entity);
        dao.logIt("Deleted", entity, json, entity.getModifiedBy().getId());
      }

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

  public void setSession(Session session) {
    this.session = session;
  }
}