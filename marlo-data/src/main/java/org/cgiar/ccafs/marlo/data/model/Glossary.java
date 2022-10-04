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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.model;

import java.util.Date;

public class Glossary extends MarloBaseEntity implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -4418504193664414295L;

  private String applicationName;
  private String title;
  private String definition;
  private Date activeSince;
  private boolean active = true;
  private String createdBy;
  private Date modificationDate;
  private String modifiedBy;
  private String modificationJustification;

  public Date getActiveSince() {
    return this.activeSince;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getDefinition() {
    return definition;
  }

  public Date getModificationDate() {
    return modificationDate;
  }

  public String getModificationJustification() {
    return modificationJustification;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getTitle() {
    return title;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public void setModificationDate(Date modificationDate) {
    this.modificationDate = modificationDate;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setTitle(String title) {
    this.title = title;
  }


}
