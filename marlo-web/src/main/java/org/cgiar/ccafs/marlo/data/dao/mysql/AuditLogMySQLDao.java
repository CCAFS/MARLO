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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.dao.AuditLogDao;
import org.cgiar.ccafs.marlo.data.model.Auditlog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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
public class AuditLogMySQLDao implements AuditLogDao {

  private StandardDAO dao;

  public String baseModelPakcage = "org.cgiar.ccafs.marlo.data.model";

  @Inject
  public AuditLogMySQLDao(StandardDAO dao) {
    this.dao = dao;
  }

  public String capitalizeFirstLetter(String original) {
    if (original == null || original.length() == 0) {
      return original;
    }
    return original.substring(0, 1).toUpperCase() + original.substring(1);
  }

  @Override
  public IAuditLog getHistory(long transactionID) {

    List<Auditlog> auditLogs =
      dao.findAll("from " + Auditlog.class.getName() + " where transaction_id=" + transactionID + " and principal=1");

    if (!auditLogs.isEmpty()) {

      Auditlog log = auditLogs.get(0);
      Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
      try {
        Class classToCast = Class.forName(log.getEntityName().replace("class ", ""));
        IAuditLog iAuditLog = gson.fromJson(log.getEntityJson(), classToCast);
        Session session = dao.openSession();
        ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(iAuditLog.getClass());
        String[] propertyNames = classMetadata.getPropertyNames();
        for (String name : propertyNames) {
          Type propertyType = classMetadata.getPropertyType(name);
          if (propertyType instanceof OrderedSetType || propertyType instanceof SetType) {
            String className = this.capitalizeFirstLetter(name).substring(0, name.length() - 1);
            String classNameRelation = baseModelPakcage + "." + className;

            List<Auditlog> auditLogsRelations =
              dao.findAll("from " + Auditlog.class.getName() + " where transaction_id=" + transactionID
                + " and principal=3 and entityName='class " + classNameRelation + "'");

            Set<IAuditLog> relation = new HashSet<IAuditLog>();
            for (Auditlog auditlog : auditLogsRelations) {
              Class classToCastRelation = Class.forName(auditlog.getEntityName().replace("class ", ""));
              IAuditLog relationObject = gson.fromJson(auditlog.getEntityJson(), classToCastRelation);
              relation.add(relationObject);
            }
            classMetadata.setPropertyValue(iAuditLog, name, relation, EntityMode.POJO);
          }
        }

        return iAuditLog;
      } catch (JsonSyntaxException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {

        e.printStackTrace();
      }
    }
    return null;

  }
}
