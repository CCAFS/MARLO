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

package org.cgiar.ccafs.marlo.utils.dto;


/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class MeliasByTypeDTO {

  private String meliaCategory;
  private Long quantity;


  public MeliasByTypeDTO() {
  }

  public MeliasByTypeDTO(String meliaCategory, Long quantity) {
    super();
    this.meliaCategory = meliaCategory;
    this.quantity = quantity;
  }

  public String getMeliaCategory() {
    return meliaCategory;
  }

  public Long getQuantity() {
    return quantity;
  }

  public void setMeliaCategory(String meliaCategory) {
    this.meliaCategory = meliaCategory;
  }

  public void setQuantity(Long quantity) {
    this.quantity = quantity;
  }
}