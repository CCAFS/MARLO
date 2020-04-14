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
public class BiParameters extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 4184202552072120327L;

  @Expose
  private String parameterName;

  @Expose
  private String parameterValue;


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


  public String getParameterName() {
    return parameterName;
  }

  public String getParameterValue() {
    return parameterValue;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub

  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public void setParameterValue(String parameterValue) {
    this.parameterValue = parameterValue;
  }


}
