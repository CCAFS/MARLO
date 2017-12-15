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

package org.cgiar.ccafs.marlo.rest.dto;

public class LocElementTypeDTO {

  private Long id;


  private String name;


  private boolean scope;


  private Boolean hasCoordinates;


  public Boolean getHasCoordinates() {
    return hasCoordinates;
  }


  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public boolean isScope() {
    return scope;
  }


  public void setHasCoordinates(Boolean hasCoordinates) {
    this.hasCoordinates = hasCoordinates;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setScope(boolean scope) {
    this.scope = scope;
  }
}
