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

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class BiReports extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 2402669967253699462L;

  @Expose
  private String reportName;

  @Expose
  private String reportId;

  @Expose
  private String datasetId;

  @Expose
  private String embedUrl;

  @Expose
  private Boolean isActive;

  @Expose
  private Boolean hasFilters;

  @Expose
  private GlobalUnit crp;

  public GlobalUnit getCrp() {
    return crp;
  }


  public String getDatasetId() {
    return datasetId;
  }


  public String getEmbedUrl() {
    return embedUrl;
  }


  public Boolean getHasFilters() {
    return hasFilters;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
  }

  @Override
  public String getModificationJustification() {

    return "";
  }


  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }


  public String getReportId() {
    return reportId;
  }


  public String getReportName() {
    return reportName;
  }


  @Override
  public boolean isActive() {
    return true;
  }


  public void setCrp(GlobalUnit crp) {
    this.crp = crp;
  }


  public void setDatasetId(String datasetId) {
    this.datasetId = datasetId;
  }


  public void setEmbedUrl(String embedUrl) {
    this.embedUrl = embedUrl;
  }


  public void setHasFilters(Boolean hasFilters) {
    this.hasFilters = hasFilters;
  }


  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }


  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub

  }


  public void setReportId(String reportId) {
    this.reportId = reportId;
  }


  public void setReportName(String reportName) {
    this.reportName = reportName;
  }


}
