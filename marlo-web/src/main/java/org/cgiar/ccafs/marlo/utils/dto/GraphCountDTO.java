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

public class GraphCountDTO {

  private String key;
  private Long count;

  public GraphCountDTO() {
  }

  public GraphCountDTO(String key, Long count) {
    super();
    this.key = key;
    this.count = count;
  }

  public String getKey() {
    return key;
  }

  public Long getCount() {
    return count;
  }

  public void setKey(String meliaCategory) {
    this.key = meliaCategory;
  }

  public void setCount(Long quantity) {
    this.count = quantity;
  }
}