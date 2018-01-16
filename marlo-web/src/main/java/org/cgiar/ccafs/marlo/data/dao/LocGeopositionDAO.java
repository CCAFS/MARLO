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

import org.cgiar.ccafs.marlo.data.model.LocGeoposition;

import java.util.List;


public interface LocGeopositionDAO {

  /**
   * This method removes a specific locGeoposition value from the database.
   * 
   * @param locGeopositionId is the locGeoposition identifier.
   * @return true if the locGeoposition was successfully deleted, false otherwise.
   */
  public void deleteLocGeoposition(long locGeopositionId);

  /**
   * This method validate if the locGeoposition identify with the given id exists in the system.
   * 
   * @param locGeopositionID is a locGeoposition identifier.
   * @return true if the locGeoposition exists, false otherwise.
   */
  public boolean existLocGeoposition(long locGeopositionID);

  /**
   * This method gets a locGeoposition object by a given locGeoposition identifier.
   * 
   * @param locGeopositionID is the locGeoposition identifier.
   * @return a LocGeoposition object.
   */
  public LocGeoposition find(long id);

  /**
   * This method gets a list of locGeoposition that are active
   * 
   * @return a list from LocGeoposition null if no exist records
   */
  public List<LocGeoposition> findAll();


  /**
   * This method saves the information of the given locGeoposition
   * 
   * @param locGeoposition - is the locGeoposition object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the locGeoposition was
   *         updated
   *         or -1 is some error occurred.
   */
  public LocGeoposition save(LocGeoposition locGeoposition);
}
