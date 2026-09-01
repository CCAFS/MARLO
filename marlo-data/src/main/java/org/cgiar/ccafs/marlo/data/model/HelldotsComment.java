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

import java.util.Date;

import com.google.gson.annotations.Expose;

public class HelldotsComment extends MarloAuditableEntity implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  @Expose
  private String commentId;
  @Expose
  private String page;
  @Expose
  private String pageQuery;
  private User authorUser;
  @Expose
  private String authorName;
  @Expose
  private String status;
  @Expose
  private String type;
  @Expose
  private String priority;
  @Expose
  private Date createdAt;
  @Expose
  private Date editedAt;
  @Expose
  private Date resolvedAt;
  @Expose
  private Integer schemaVersion;
  private GlobalUnit globalUnit;
  @Expose
  private String payload;

  public HelldotsComment() {
  }

  public String getAuthorName() {
    return authorName;
  }

  public User getAuthorUser() {
    return authorUser;
  }

  public String getCommentId() {
    return commentId;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getEditedAt() {
    return editedAt;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }

  public String getPage() {
    return page;
  }

  public String getPageQuery() {
    return pageQuery;
  }

  public String getPayload() {
    return payload;
  }

  public String getPriority() {
    return priority;
  }

  public Date getResolvedAt() {
    return resolvedAt;
  }

  public Integer getSchemaVersion() {
    return schemaVersion;
  }

  public String getStatus() {
    return status;
  }

  public String getType() {
    return type;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public void setAuthorUser(User authorUser) {
    this.authorUser = authorUser;
  }

  public void setCommentId(String commentId) {
    this.commentId = commentId;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setEditedAt(Date editedAt) {
    this.editedAt = editedAt;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public void setPageQuery(String pageQuery) {
    this.pageQuery = pageQuery;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public void setResolvedAt(Date resolvedAt) {
    this.resolvedAt = resolvedAt;
  }

  public void setSchemaVersion(Integer schemaVersion) {
    this.schemaVersion = schemaVersion;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setType(String type) {
    this.type = type;
  }

}
