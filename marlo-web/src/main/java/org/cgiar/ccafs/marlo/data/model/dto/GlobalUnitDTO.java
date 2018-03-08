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


package org.cgiar.ccafs.marlo.data.model.dto;

import java.io.Serializable;


public class GlobalUnitDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String name;

  private String acronym;

  private boolean active;

  public GlobalUnitDTO() {

  }


  public String getAcronym() {
    return acronym;
  }


  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public boolean isActive() {
    return active;
  }


  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setActive(boolean active) {
    this.active = active;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setName(String name) {
    this.name = name;
  }


}
