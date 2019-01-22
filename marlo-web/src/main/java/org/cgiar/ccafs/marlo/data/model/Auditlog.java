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

package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 8, 2016 11:23:28 AM by Hibernate Tools 4.3.1.Final


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Auditlog generated by hbm2java
 */
public class Auditlog extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = -5812698307148791654L;


  private String action;
  private String detail;
  private Date createdDate;
  private Long entityId;
  private String entityName;
  private String entityJson;
  private Long userId;
  private String transactionId;
  private Long main;
  private String relationName;
  private User user;
  private String modificationJustification;
  private Long phase;

  public Auditlog() {
  }


  public Auditlog(String action, String detail, Date createdDate, Long entityId, String entityName, String entityJson,
    Long userId, String transactionId, Long principal, String relationName, String modificationJustification,
    Long phase) {
    this.action = action;
    this.detail = detail;
    this.createdDate = createdDate;
    this.entityId = entityId;
    this.entityName = entityName;
    this.entityJson = entityJson;
    this.userId = userId;
    this.transactionId = transactionId;
    this.main = principal;
    this.relationName = relationName;
    this.modificationJustification = modificationJustification;
    this.phase = phase;
  }


  public String getAction() {
    return this.action;
  }


  public boolean getAvailable() {
    String inputString = "10-01-2018";
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    try {
      Date inputDate = dateFormat.parse(inputString);
      return this.createdDate.after(inputDate);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      return false;
    }

  }


  public Date getCreatedDate() {
    return this.createdDate;
  }


  public String getDetail() {
    return this.detail;
  }


  public Long getEntityId() {
    return this.entityId;
  }


  public String getEntityJson() {
    return this.entityJson;
  }

  public String getEntityName() {
    return this.entityName;
  }


  public Long getMain() {
    return main;
  }


  public String getModificationJustification() {
    return modificationJustification;
  }


  public Long getPhase() {
    return phase;
  }


  public String getRelationName() {
    return relationName;
  }

  public String getTransactionId() {
    return transactionId;
  }


  public User getUser() {
    return user;
  }


  public Long getUserId() {
    return userId;
  }

  public void setAction(String action) {
    this.action = action;
  }


  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public void setEntityJson(String entityJson) {
    this.entityJson = entityJson;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public void setMain(Long main) {
    this.main = main;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setPhase(Long phase) {
    this.phase = phase;
  }

  public void setRelationName(String relationName) {
    this.relationName = relationName;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "Auditlog [auditLogId=" + this.getId() + ", entityId=" + entityId + ", entityName=" + entityName
      + ", transactionId=" + transactionId + "]";
  }


}

