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

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;

/**
 * Administrator-entered content for the banner at the top of the homepage: one row per Global Unit. Deliberately not
 * phase-scoped — the banner is homepage chrome rather than reportable cycle data, so it takes part in no forward
 * replication (ENH-HOMEPAGE-BANNER-001, ADR-2).
 */
public class HomepageBanner extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 4479301724861529051L;

  @Expose
  private String title;
  @Expose
  private String description;
  @Expose
  private String imageFileName;
  @Expose
  private GlobalUnit globalUnit;

  public HomepageBanner() {
  }

  public String getDescription() {
    return description;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }

  public String getImageFileName() {
    return imageFileName;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder detail = new StringBuilder();
    detail.append("HomepageBanner - ");
    detail.append("ID").append(" : ").append(this.getId());
    return detail.toString();
  }

  public String getTitle() {
    return title;
  }

  /**
   * True when there is nothing to show. The homepage renders no banner at all in that case, which is how an
   * administrator hides it: clear the three fields.
   */
  public boolean isEmpty() {
    return StringUtils.isBlank(title) && StringUtils.isBlank(description) && StringUtils.isBlank(imageFileName);
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setImageFileName(String imageFileName) {
    this.imageFileName = imageFileName;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
