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
// Generated May 26, 2016 9:42:28 AM by Hibernate Tools 4.3.1.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * SrfSlo generated by hbm2java
 */
public class SrfSlo implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 8111135950998862150L;

  @Expose
  private Long id;

  @Expose
  private String title;

  @Expose
  private String description;


  private Set<SrfSloIndicator> srfSloIndicators = new HashSet<SrfSloIndicator>(0);


  private Set<SrfSloIdo> srfSloIdos = new HashSet<SrfSloIdo>(0);


  private boolean active;


  private User createdBy;


  private Date activeSince;

  private User modifiedBy;
  private String modificationJustification;

  public SrfSlo() {
  }

  public SrfSlo(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public SrfSlo(String title, String description, Set<SrfSloIndicator> srfSloIndicators, Set<SrfSloIdo> srfSloIdos) {
    this.title = title;
    this.description = description;
    this.srfSloIndicators = srfSloIndicators;
    this.srfSloIdos = srfSloIdos;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Set<SrfSloIdo> getSrfSloIdos() {
    return this.srfSloIdos;
  }

  public Set<SrfSloIndicator> getSrfSloIndicators() {
    return this.srfSloIndicators;
  }


  public String getTitle() {
    return this.title;
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

  public void setDescription(String description) {
    this.description = description;
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

  public void setSrfSloIdos(Set<SrfSloIdo> srfSloIdos) {
    this.srfSloIdos = srfSloIdos;
  }

  public void setSrfSloIndicators(Set<SrfSloIndicator> srfSloIndicators) {
    this.srfSloIndicators = srfSloIndicators;
  }

  public void setTitle(String title) {
    this.title = title;
  }


}

