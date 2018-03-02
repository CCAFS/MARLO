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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.dao.AuditLogDao;
import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.utils.BigDecimalTypeAdapter;
import org.cgiar.ccafs.marlo.utils.DateTypeAdapter;
import org.cgiar.ccafs.marlo.utils.DoubleTypeAdapter;
import org.cgiar.ccafs.marlo.utils.FloatTypeAdapter;
import org.cgiar.ccafs.marlo.utils.IntegerTypeAdapter;
import org.cgiar.ccafs.marlo.utils.LongTypeAdapter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.OrderedSetType;
import org.hibernate.type.SetType;
import org.hibernate.type.Type;

/**
 * @author Christian Garcia
 */
@Named
public class AuditLogMySQLDao extends AbstractMarloDAO<Auditlog, Long> implements AuditLogDao {

  public String baseModelPakcage = "org.cgiar.ccafs.marlo.data.model";
  public UserDAO userDao;

  @Inject
  public AuditLogMySQLDao(SessionFactory sessionFactory, UserDAO userDao) {
    super(sessionFactory);
    this.userDao = userDao;
  }

  @Override
  public List<Auditlog> findAllWithClassNameAndIdAndActionName(Class<?> classAudit, long id, String actionName) {
    String queryString =
      "from " + Auditlog.class.getName() + " where ENTITY_NAME='class " + classAudit.getName() + "' and ENTITY_ID=" + id
        + " and main=1 and DETAIL like 'Action: " + actionName + "%' order by CREATED_DATE desc";
    Query query = this.getSessionFactory().getCurrentSession().createQuery(queryString);
    query.setMaxResults(11);
    List<Auditlog> auditLogs = super.findAll(query);
    return auditLogs;
  }

  @Override
  public Auditlog getAuditlog(String transactionID) {

    List<Auditlog> auditLogs =
      super.findAll("from " + Auditlog.class.getName() + " where transaction_id='" + transactionID + "' and main=1");

    if (!auditLogs.isEmpty()) {

      Auditlog log = auditLogs.get(0);

      return log;

    }
    return null;

  }


  @Override
  public Auditlog getAuditlog(String transactionID, IAuditLog auditLog) {


    List<Auditlog> auditLogs =
      super.findAll("from " + Auditlog.class.getName() + " where transaction_id='" + transactionID + "' and ENTITY_ID="
        + auditLog.getId() + " and ENTITY_NAME='" + auditLog.getClass().toString() + "'");

    if (!auditLogs.isEmpty()) {

      Auditlog log = auditLogs.get(0);

      return log;

    }
    return null;

  }

  @Override
  public List<Auditlog> getCompleteHistory(String transactionID) {
    List<Auditlog> auditLogs =
      super.findAll("from " + Auditlog.class.getName() + " where transaction_id='" + transactionID + "'");
    return auditLogs;

  }


  @Override
  public IAuditLog getHistory(String transactionID) {

    List<Auditlog> auditLogs =
      super.findAll("from " + Auditlog.class.getName() + " where transaction_id='" + transactionID + "' and main=1");

    if (!auditLogs.isEmpty()) {

      Auditlog log = auditLogs.get(0);
      IAuditLog iAuditLog = this.loadFromAuditLog(log);
      try {
        this.loadRelationsForIAuditLog(iAuditLog, transactionID);
      } catch (NoSuchFieldException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return iAuditLog;

    }
    return null;

  }

  @Override
  public List<Auditlog> getHistoryBefore(String transactionID) {

    List<Auditlog> logs = new ArrayList<Auditlog>();
    Auditlog principal = this.getAuditlog(transactionID);
    String sql = " select  transaction_id from auditlog where transaction_id !='" + transactionID + "' and ENTITY_ID='"
      + principal.getEntityId() + "'" + " and ENTITY_NAME='" + principal.getEntityName() + "' and DETAIL = '"
      + principal.getDetail() + "'  and CREATED_DATE< '" + principal.getCreatedDate() + "'  ORDER BY CREATED_DATE desc";
    List<Map<String, Object>> auditLogs = super.findCustomQuery(sql);

    if (!auditLogs.isEmpty()) {
      logs.addAll(this.getCompleteHistory(auditLogs.get(0).get("transaction_id").toString()));
    }

    return logs;

  }

  @Override
  public List<Auditlog> getHistoryBeforeList(String transactionID, String className, String entityID) {

    List<Auditlog> logs = new ArrayList<Auditlog>();
    Auditlog principal = this.getAuditlog(transactionID);
    String sql = " select  transaction_id from auditlog where transaction_id !='" + transactionID + "' and ENTITY_ID='"
      + entityID + "'" + " and ENTITY_NAME='" + className + "'  and CREATED_DATE< '" + principal.getCreatedDate()
      + "'  ORDER BY CREATED_DATE desc";
    List<Map<String, Object>> auditLogs = super.findCustomQuery(sql);

    if (!auditLogs.isEmpty()) {
      logs.addAll(this.getCompleteHistory(auditLogs.get(0).get("transaction_id").toString()));
    }

    return logs;

  }


  @Override
  public List<Auditlog> listLogs(Class<?> classAudit, long id, String actionName, Long phaseId) {

    List<Auditlog> auditLogs = super.findAll("from " + Auditlog.class.getName() + " where ENTITY_NAME='class "
      + classAudit.getName() + "' and ENTITY_ID=" + id + " and main=1 and id_phase=" + phaseId
      + " and DETAIL like 'Action: " + actionName + "%' order by CREATED_DATE desc ");
    // " and principal=1 order by CREATED_DATE desc LIMIT 10");
    for (Auditlog auditlog : auditLogs) {
      auditlog.setUser(userDao.getUser(auditlog.getUserId()));
    }

    if (auditLogs.size() > 11) {
      return auditLogs.subList(0, 11);
    }
    return auditLogs;

  }


  public IAuditLog loadFromAuditLog(Auditlog auditlog) {
    try {
      Gson gson = new GsonBuilder().registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
        .registerTypeAdapter(Long.class, new LongTypeAdapter())
        .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
        .registerTypeAdapter(Float.class, new FloatTypeAdapter())
        .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
        .registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
      Class<?> classToCast = Class.forName(auditlog.getEntityName().replace("class ", ""));
      IAuditLog iAuditLog = (IAuditLog) gson.fromJson(auditlog.getEntityJson(), classToCast);

      return iAuditLog;
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {

      e.printStackTrace();
    }

    return null;
  }

  public void loadRelationsForIAuditLog(IAuditLog iAuditLog, String transactionID)
    throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    try {

      Session session = super.getSessionFactory().getCurrentSession();
      ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(iAuditLog.getClass());
      String[] propertyNames = classMetadata.getPropertyNames();
      for (String name : propertyNames) {
        try {
          Type propertyType = classMetadata.getPropertyType(name);
          Field privateField = iAuditLog.getClass().getDeclaredField(name);
          privateField.setAccessible(true);
          if (propertyType instanceof OneToOneType) {
            Object obj = privateField.get(iAuditLog);
            if (obj != null && obj instanceof IAuditLog) {
              this.loadRelationsForIAuditLog((IAuditLog) obj, transactionID);
            }
          }
          if (propertyType instanceof OrderedSetType || propertyType instanceof SetType) {

            String classNameRelation = propertyType.getName();
            String sql = "from " + Auditlog.class.getName() + " where transaction_id='" + transactionID
              + "' and main=3 and relation_name='" + classNameRelation + ":" + iAuditLog.getId()
              + "'order by ABS(ENTITY_ID) asc";
            List<Auditlog> auditLogsRelations = super.findAll(sql);

            Set<IAuditLog> relation = new HashSet<IAuditLog>();
            for (Auditlog auditlog : auditLogsRelations) {
              IAuditLog relationObject = this.loadFromAuditLog(auditlog);
              this.loadRelationsForIAuditLog(relationObject, transactionID);
              relation.add(relationObject);
            }
            classMetadata.setPropertyValue(iAuditLog, name, relation);
          }
        } catch (Exception e) {

        }
      }
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    }

  }
}
