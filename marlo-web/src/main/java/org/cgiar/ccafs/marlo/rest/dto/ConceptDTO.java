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

package org.cgiar.ccafs.marlo.rest.dto;


public class ConceptDTO {

  private String name;

  private Long action_area_id;

  private String action_area_description;


  public String getAction_area_description() {
    return action_area_description;
  }


  public Long getAction_area_id() {
    return action_area_id;
  }


  public String getName() {
    return name;
  }


  public void setAction_area_description(String action_area_description) {
    this.action_area_description = action_area_description;
  }


  public void setAction_area_id(Long action_area_id) {
    this.action_area_id = action_area_id;
  }


  public void setName(String name) {
    this.name = name;
  }
}
