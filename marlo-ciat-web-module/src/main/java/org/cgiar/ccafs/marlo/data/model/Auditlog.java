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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Modified by @author nmatovu last on Sep 29, 2016
 *
 */
public class Auditlog implements Serializable {
	// TODO: Implement IAuditlog
	private static final long serialVersionUID = -8597809497639887867L;
	private Long auditLogId;
	private String action;
	private String detail;
	private Date createdDate;
	private String entityId;
	private String entityName;
	private String entityJson;
	private Long userId;
	private String transactionId;
	private Long main;
	private String relationName;
	private User user;

	public Auditlog() {
	}

	public Auditlog(String action, String detail, Date createdDate,
			String entityId, String entityName, String entityJson, Long userId,
			String transactionId, Long principal, String relationName) {
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
	}

	public String getAction() {
		return this.action;
	}

	public Long getAuditLogId() {
		return this.auditLogId;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public String getDetail() {
		return this.detail;
	}

	public String getEntityId() {
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

	public void setAuditLogId(Long auditLogId) {
		this.auditLogId = auditLogId;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public void setEntityId(String entityId) {
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

}
