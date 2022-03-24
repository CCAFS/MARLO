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
import org.apache.commons.lang3.StringUtils;

public class AllianceLever extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  /**
   * 
   */
  private static final long serialVersionUID = 6187169023384050115L;

  @Expose
  private String name;

  @Expose
  private String indicator;

  public AllianceLever() {
  }

  public String getComposedName() {
    String composedName = "";
    if (this.getId() == null || this.getId() == -1) {
      return "<Not defined>";
    } else {
      if (StringUtils.isNotBlank(this.getIndicator())) {
        composedName += "Lever " + StringUtils.trimToEmpty(this.getIndicator());
      }
      if (StringUtils.isNotBlank(this.getName())) {
        composedName += " - " + StringUtils.trimToEmpty(this.getName());
      }
    }

    return composedName;
  }

  public String getIndicator() {
    return indicator;
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

  public String getName() {
    return this.name;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
  }

  public void setName(String name) {
    this.name = name;
  }

}
