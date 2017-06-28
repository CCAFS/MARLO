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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.dao.IAuditLogDao;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.utils.BigDecimalTypeAdapter;
import org.cgiar.ccafs.marlo.utils.DoubleTypeAdapter;
import org.cgiar.ccafs.marlo.utils.FloatTypeAdapter;
import org.cgiar.ccafs.marlo.utils.IntegerTypeAdapter;
import org.cgiar.ccafs.marlo.utils.LongTypeAdapter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.inject.Inject;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.OrderedSetType;
import org.hibernate.type.SetType;
import org.hibernate.type.Type;

/**
 * @author Christian Garcia
 */
public class AuditLogDao implements IAuditLogDao {

  private StandardDAO dao;
  private UserDAO userDao;
  public String baseModelPakcage = "org.cgiar.ccafs.marlo.data.model";

  @Inject
  public AuditLogDao(StandardDAO dao, UserDAO userDao) {
    this.dao = dao;
    this.userDao = userDao;
  }


  @Override
  public IAuditLog getHistory(String transactionID) {

    List<Auditlog> auditLogs =
      dao.findAll("from " + Auditlog.class.getName() + " where transaction_id='" + transactionID + "' and main=1");

    if (!auditLogs.isEmpty()) {

      Auditlog log = auditLogs.get(0);
      IAuditLog iAuditLog = this.loadFromAuditLog(log);
      this.loadRelationsForIAuditLog(iAuditLog, transactionID);
      return iAuditLog;

    }
    return null;

  }

  @Override
  public List<Auditlog> listLogs(Class classAudit, long id, String actionName) {

    List<Auditlog> auditLogs =
      dao.findAll("from " + Auditlog.class.getName() + " where ENTITY_NAME='class " + classAudit.getName()
        + "' and ENTITY_ID=" + id + " and main=1 and DETAIL like 'Action: " + actionName
        + "%' order by CREATED_DATE desc LIMIT 11");
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
      Gson gson =
        new GsonBuilder().registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
        .registerTypeAdapter(Long.class, new LongTypeAdapter())
        .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
        .registerTypeAdapter(Float.class, new FloatTypeAdapter())
        .registerTypeAdapter(BigDecimal.class, new BigDecimalTypeAdapter())
        .registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
      Class classToCast = Class.forName(auditlog.getEntityName().replace("class ", ""));
      IAuditLog iAuditLog = (IAuditLog) gson.fromJson(auditlog.getEntityJson(), classToCast);

      return iAuditLog;
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {

      e.printStackTrace();
    }

    return null;
  }

  public void loadRelationsForIAuditLog(IAuditLog iAuditLog, String transactionID) {
    try {

      Session session = dao.openSession();
      ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(iAuditLog.getClass());
      String[] propertyNames = classMetadata.getPropertyNames();
      for (String name : propertyNames) {
        Type propertyType = classMetadata.getPropertyType(name);
        if (propertyType instanceof OrderedSetType || propertyType instanceof SetType) {

          String classNameRelation = propertyType.getName();
          String sql =
            "from " + Auditlog.class.getName() + " where transaction_id='" + transactionID
            + "' and main=3 and relation_name='" + classNameRelation + ":" + iAuditLog.getId()
            + "'order by ABS(ENTITY_ID) asc";
          List<Auditlog> auditLogsRelations = dao.findAll(sql);

          Set<IAuditLog> relation = new HashSet<IAuditLog>();
          for (Auditlog auditlog : auditLogsRelations) {
            IAuditLog relationObject = this.loadFromAuditLog(auditlog);
            this.loadRelationsForIAuditLog(relationObject, transactionID);
            relation.add(relationObject);
          }
          classMetadata.setPropertyValue(iAuditLog, name, relation, EntityMode.POJO);
        }
      }
      session.close();
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
    }
  }
}
