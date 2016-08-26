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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class Deliverable implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 1867818669925473872L;


  @Expose
  private Long id;


  @Expose
  private Project project;


  @Expose
  private DeliverableType deliverableType;


  @Expose
  private String title;


  @Expose
  private String typeOther;


  @Expose
  private int year;


  @Expose
  private Integer status;

  @Expose
  private String statusDescription;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private User createdBy;

  @Expose
  private User modifiedBy;

  @Expose
  private String modificationJustification;

  @Expose
  private CrpClusterOfActivity crpClusterOfActivity;

  @Expose
  private CrpClusterKeyOutput crpClusterKeyOutput;
  @Expose
  private CrpProgramOutcome crpProgramOutcome;

  @Expose
  private CrpProgram crpProgram;

  public Deliverable() {
  }

  public Deliverable(Project project, DeliverableType deliverableType, String title, String typeOther, int year,
    Integer status, String statusDescription, boolean active, Date activeSince, User createdBy, User modifiedBy,
    String modificationJustification, CrpClusterOfActivity crpClusterOfActivity,
    CrpClusterKeyOutput crpClusterKeyOutput, CrpProgramOutcome crpProgramOutcome, CrpProgram crpProgram) {
    this.project = project;
    this.deliverableType = deliverableType;
    this.title = title;
    this.typeOther = typeOther;
    this.year = year;
    this.status = status;
    this.statusDescription = statusDescription;
    this.active = active;
    this.activeSince = activeSince;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.modificationJustification = modificationJustification;
    this.crpClusterOfActivity = crpClusterOfActivity;
    this.crpProgramOutcome = crpProgramOutcome;
    this.crpProgram = crpProgram;
    this.crpClusterKeyOutput = crpClusterKeyOutput;
  }

  public Deliverable(Project project, int year, boolean active, Date activeSince, User createdBy, User modifiedBy,
    String modificationJustification) {
    this.project = project;
    this.year = year;
    this.active = active;
    this.activeSince = activeSince;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.modificationJustification = modificationJustification;
  }

  public Date getActiveSince() {
    return this.activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public CrpClusterKeyOutput getCrpClusterKeyOutput() {
    return crpClusterKeyOutput;
  }

  public CrpClusterOfActivity getCrpClusterOfActivity() {
    return crpClusterOfActivity;
  }

  public CrpProgram getCrpProgram() {
    return crpProgram;
  }

  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }

  public DeliverableType getDeliverableType() {
    return this.deliverableType;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getModificationJustification() {
    return this.modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Project getProject() {
    return project;
  }

  public Integer getStatus() {
    return this.status;
  }

  public String getStatusDescription() {
    return this.statusDescription;
  }

  public String getStatusName() {
    String statusName = "";
    List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum projectStatusEnum : list) {
      if (Integer.parseInt(projectStatusEnum.getStatusId()) == this.status) {
        statusName = projectStatusEnum.getStatus();
      }
    }
    return statusName;
  }

  public String getTitle() {
    return this.title;
  }

  public String getTypeOther() {
    return this.typeOther;
  }

  public int getYear() {
    return this.year;
  }

  @Override
  public boolean isActive() {
    return active;
  }


  public void setActive(boolean active) {
    this.active = active;
  }


  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCrpClusterKeyOutput(CrpClusterKeyOutput crpClusterKeyOutput) {
    this.crpClusterKeyOutput = crpClusterKeyOutput;
  }

  public void setCrpClusterOfActivity(CrpClusterOfActivity crpClusterOfActivity) {
    this.crpClusterOfActivity = crpClusterOfActivity;
  }

  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
  }


  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }


  public void setDeliverableType(DeliverableType deliverableType) {
    this.deliverableType = deliverableType;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public void setStatusDescription(String statusDescription) {
    this.statusDescription = statusDescription;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTypeOther(String typeOther) {
    this.typeOther = typeOther;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
