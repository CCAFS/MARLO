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

import org.cgiar.ccafs.marlo.data.model.ImpactArea;

import java.util.List;


public interface ImpactAreaDAO {

  /**
   * This method removes a specific impactArea value from the database.
   * 
   * @param impactAreaId is the impactArea identifier.
   * @return true if the impactArea was successfully deleted, false otherwise.
   */
  public void deleteImpactArea(long impactAreaId);

  /**
   * This method validate if the impactArea identify with the given id exists in the system.
   * 
   * @param impactAreaID is a impactArea identifier.
   * @return true if the impactArea exists, false otherwise.
   */
  public boolean existImpactArea(long impactAreaID);

  /**
   * This method gets a impactArea object by a given impactArea identifier.
   * 
   * @param impactAreaID is the impactArea identifier.
   * @return a ImpactArea object.
   */
  public ImpactArea find(long id);

  /**
   * This method gets a list of impactArea that are active
   * 
   * @return a list from ImpactArea null if no exist records
   */
  public List<ImpactArea> findAll();


  List<ImpactArea> findAllCustom();

  /**
   * This method saves the information of the given impactArea
   * 
   * @param impactArea - is the impactArea object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the impactArea was
   *         updated
   *         or -1 is some error occurred.
   */
  public ImpactArea save(ImpactArea impactArea);
}
