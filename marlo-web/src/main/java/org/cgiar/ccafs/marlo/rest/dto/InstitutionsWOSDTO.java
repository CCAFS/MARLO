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


public class InstitutionsWOSDTO {

  private Long clarisa_id;


  private String fullName;

  public InstitutionsWOSDTO(Long clarisa_id, String fullName) {
    super();
    this.clarisa_id = clarisa_id;
    this.fullName = fullName;
  }


  public Long getClarisa_id() {
    return clarisa_id;
  }


  public String getFullName() {
    return fullName;
  }


  public void setClarisa_id(Long clarisa_id) {
    this.clarisa_id = clarisa_id;
  }


  public void setFullName(String fullName) {
    this.fullName = fullName;
  }


}
