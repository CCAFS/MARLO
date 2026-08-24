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

import com.google.gson.annotations.Expose;

public class HelldotsScreenshot extends MarloAuditableEntity implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  @Expose
  private String commentId;
  @Expose
  private String kind;
  @Expose
  private String fileName;
  @Expose
  private String relativePath;
  @Expose
  private String contentType;
  @Expose
  private Long byteSize;

  public HelldotsScreenshot() {
  }

  public Long getByteSize() {
    return byteSize;
  }

  public String getCommentId() {
    return commentId;
  }

  public String getContentType() {
    return contentType;
  }

  public String getFileName() {
    return fileName;
  }

  public String getKind() {
    return kind;
  }

  public String getRelativePath() {
    return relativePath;
  }

  public void setByteSize(Long byteSize) {
    this.byteSize = byteSize;
  }

  public void setCommentId(String commentId) {
    this.commentId = commentId;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }

}
