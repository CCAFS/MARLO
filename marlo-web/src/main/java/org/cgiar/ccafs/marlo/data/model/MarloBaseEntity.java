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

import javax.persistence.MappedSuperclass;

import com.google.gson.annotations.Expose;

/**
 * A base class that all Marlo Entities need to extend from. Prevents the need to re-define the id field and the active
 * field
 * 
 * @author GrantL
 */
@MappedSuperclass
public abstract class MarloBaseEntity {

  @Expose
  private Long id;

  /**
   * Entities loaded from the database will override the value by calling the setActive method using reflection.
   */
  public Long getId() {
    return this.id;
  }


  public void setId(Long id) {
    this.id = id;
  }

}
