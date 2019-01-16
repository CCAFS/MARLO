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
 * A base class that all Marlo Entities that require soft delete functionality need to extend from. Currently DAOs set
 * the active field to false when the entity is deleted, but this may change in the future as we leverage hibernate's
 * soft delete support.
 * This entity prevents the need to re-define the active field.
 * 
 * @author GrantL
 */
@MappedSuperclass
public class MarloSoftDeleteableEntity extends MarloBaseEntity {

  // Default to true for all new entities. This gets overriden by hibernate which will call the setActive method.
  @Expose
  private boolean active = true;


  public boolean isActive() {
    return this.active;
  }


  public void setActive(boolean active) {
    this.active = active;
  }
}
