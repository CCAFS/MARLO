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

public class SoilIndicator extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String indicatorName;
  @Expose
  private long indicatorId;
  @Expose
  private Phase phase;

  public long getIndicatorId() {
    return indicatorId;
  }

  public String getIndicatorName() {
    return indicatorName;
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
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

  public Phase getPhase() {
    return phase;
  }

  @Override
  public boolean isActive() {
    // TODO Auto-generated method stub
    return false;
  }

  public void setIndicatorId(long indicatorId) {
    this.indicatorId = indicatorId;
  }

  public void setIndicatorName(String indicatorName) {
    this.indicatorName = indicatorName;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }
}

