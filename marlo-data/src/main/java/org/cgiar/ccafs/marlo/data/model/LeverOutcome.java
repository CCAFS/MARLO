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

public class LeverOutcome extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 2591192504126616140L;

  @Expose
  private LeverOutcome leverOutcomeCategory;

  @Expose
  private String name;

  @Expose
  private String description;

  @Expose
  private String indicator;

  private Set<LeverOutcome> leverOutcomeTypes = new HashSet<>(0);

  public LeverOutcome() {
  }

  public String getDescription() {
    return this.description;
  }

  public String getIndicator() {
    return indicator;
  }

  public LeverOutcome getLeverOutcomeCategory() {
    return leverOutcomeCategory;
  }

  public Set<LeverOutcome> getLeverOutcomeTypes() {
    return leverOutcomeTypes;
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

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public void setLeverOutcomeCategory(LeverOutcome leverOutcomeCategory) {
    this.leverOutcomeCategory = leverOutcomeCategory;
  }

  public void setLeverOutcomeTypes(Set<LeverOutcome> leverOutcomeTypes) {
    this.leverOutcomeTypes = leverOutcomeTypes;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
  }

  public void setName(String name) {
    this.name = name;
  }
}
