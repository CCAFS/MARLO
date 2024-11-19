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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.GlobalTarget;

import java.util.List;


public interface GlobalTargetDAO {

  /**
   * This method removes a specific globalTarget value from the database.
   * 
   * @param globalTargetId is the globalTarget identifier.
   * @return true if the globalTarget was successfully deleted, false otherwise.
   */
  public void deleteGlobalTarget(long globalTargetId);

  /**
   * This method validate if the globalTarget identify with the given id exists in the system.
   * 
   * @param globalTargetID is a globalTarget identifier.
   * @return true if the globalTarget exists, false otherwise.
   */
  public boolean existGlobalTarget(long globalTargetID);

  /**
   * This method gets a globalTarget object by a given globalTarget identifier.
   * 
   * @param globalTargetID is the globalTarget identifier.
   * @return a GlobalTarget object.
   */
  public GlobalTarget find(long id);

  /**
   * This method gets a list of globalTarget that are active
   * 
   * @return a list from GlobalTarget null if no exist records
   */
  public List<GlobalTarget> findAll();


  List<GlobalTarget> findAllByImpactArea(long impactAreaId);

  /**
   * This method saves the information of the given globalTarget
   * 
   * @param globalTarget - is the globalTarget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the globalTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public GlobalTarget save(GlobalTarget globalTarget);
}
