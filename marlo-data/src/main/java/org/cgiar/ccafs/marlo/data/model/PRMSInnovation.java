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

public class PRMSInnovation extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  @Expose
  private String title;
  @Expose
  private int prmsResultId;
  @Expose
  private String description;
  @Expose
  private int typeId;
  @Expose
  private String typeName;
  @Expose
  private int year;
  @Expose
  private String pdfLink;
  @Expose
  private int readinessLevelId;
  @Expose
  private String readinessLevelName;

  public String getDescription() {
    return description;
  }

  public String getPdfLink() {
    return pdfLink;
  }

  public int getPrmsResultId() {
    return prmsResultId;
  }

  public int getReadinessLevelId() {
    return readinessLevelId;
  }

  public String getReadinessLevelName() {
    return readinessLevelName;
  }

  public String getTitle() {
    return title;
  }

  public int getTypeId() {
    return typeId;
  }

  public String getTypeName() {
    return typeName;
  }

  public int getYear() {
    return year;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public void setPdfLink(String pdfLink) {
    this.pdfLink = pdfLink;
  }

  public void setPrmsResultId(int prmsResultId) {
    this.prmsResultId = prmsResultId;
  }

  public void setReadinessLevelId(int readinessLevelId) {
    this.readinessLevelId = readinessLevelId;
  }

  public void setReadinessLevelName(String readinessLevelName) {
    this.readinessLevelName = readinessLevelName;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTypeId(int typeId) {
    this.typeId = typeId;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public void setYear(int year) {
    this.year = year;
  }
}