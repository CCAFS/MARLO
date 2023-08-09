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

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class ActivityTitle extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String title;

  private Set<Activity> activities = new HashSet<Activity>(0);
  @Expose
  private int startYear;
  @Expose
  private int endYear;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ActivityTitle other = (ActivityTitle) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public Set<Activity> getActivities() {
    return activities;
  }

  public int getEndYear() {
    return endYear;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  @Override
  public String getModificationJustification() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public User getModifiedBy() {
    // TODO Auto-generated method stub
    return null;
  }

  public int getStartYear() {
    return startYear;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  @Override
  public boolean isActive() {
    // TODO Auto-generated method stub
    return true;
  }

  public void setActivities(Set<Activity> activities) {
    this.activities = activities;
  }

  public void setEndYear(int endYear) {
    this.endYear = endYear;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub
  }

  public void setStartYear(int startYear) {
    this.startYear = startYear;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}

