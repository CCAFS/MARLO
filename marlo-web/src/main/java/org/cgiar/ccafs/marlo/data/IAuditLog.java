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


package org.cgiar.ccafs.marlo.data;

/**
 * Interface that have the methods for get the Model Class information to audit
 * 
 * @author Christian Garcia
 * @author Hermes Jimenez
 */
import org.cgiar.ccafs.marlo.data.model.User;

public interface IAuditLog {

  /**
   * This method get the Entity id that is the primary key
   * 
   * @return a Object model id
   */
  public Object getId();


  /**
   * This method get a entity information detail to identify in the audit.
   * 
   * @return a Model class log Detail
   */
  public String getLogDeatil();

  public String getModificationJustification();

  /**
   * This method gets the user that insert or update the information to the database.
   * 
   * @return a User object that has data changes
   */
  public User getModifiedBy();

  /**
   * This method gets if the Entity is active in the database.
   * 
   * @return true if the Entity is active or false if the Entity is not active.
   */
  public boolean isActive();
}
