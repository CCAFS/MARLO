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

package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.Sdg;

import java.util.List;

public interface SdgManager {

  /**
   * This method removes a specific sdg value from the database.
   * 
   * @param sdgId is the sdg identifier.
   * @return true if the sdg was successfully deleted, false otherwise.
   */
  public void deleteSdg(Long sdgId);

  /**
   * This method validate if the sdg identify with the given id exists in the system.
   * 
   * @param sdgID is a sdg identifier.
   * @return true if the sdg exists, false otherwise.
   */
  public boolean existSdg(Long sdgID);

  /**
   * This method gets a list of sdg that are active
   * 
   * @return a list from Sdg; null if no records exists
   */
  public List<Sdg> getAll();

  /**
   * This method gets a Sdg object by a given financialCode identifier.
   * 
   * @param financialCode is the sdg financialCode identifier.
   * @return a Sdg object.
   */
  public Sdg getSdgByFinancialCode(String financialCode);

  /**
   * This method gets a Sdg object by a given identifier.
   * 
   * @param id is the sdg identifier.
   * @return a Sdg object.
   */
  public Sdg getSdgById(Long id);

  /**
   * This method gets a Sdg object by a given smoCode identifier.
   * 
   * @param smoCode is the sdg SMO Code identifier.
   * @return a Sdg object.
   */
  public Sdg getSdgBySmoCode(String smoCode);

  /**
   * This method saves the information of the given sdg
   * 
   * @param sdg - is the sdg object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the sdg was
   *         updated
   *         or -1 is some error occurred.
   */
  public Sdg save(Sdg sdg);

}
