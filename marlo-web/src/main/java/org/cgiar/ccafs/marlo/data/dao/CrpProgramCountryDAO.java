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

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpProgramCountryMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgramCountry;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpProgramCountryMySQLDAO.class)
public interface CrpProgramCountryDAO {

  /**
   * This method removes a specific crpProgramCountry value from the database.
   * 
   * @param crpProgramCountryId is the crpProgramCountry identifier.
   * @return true if the crpProgramCountry was successfully deleted, false otherwise.
   */
  public void deleteCrpProgramCountry(long crpProgramCountryId);

  /**
   * This method validate if the crpProgramCountry identify with the given id exists in the system.
   * 
   * @param crpProgramCountryID is a crpProgramCountry identifier.
   * @return true if the crpProgramCountry exists, false otherwise.
   */
  public boolean existCrpProgramCountry(long crpProgramCountryID);

  /**
   * This method gets a crpProgramCountry object by a given crpProgramCountry identifier.
   * 
   * @param crpProgramCountryID is the crpProgramCountry identifier.
   * @return a CrpProgramCountry object.
   */
  public CrpProgramCountry find(long id);

  /**
   * This method gets a list of crpProgramCountry that are active
   * 
   * @return a list from CrpProgramCountry null if no exist records
   */
  public List<CrpProgramCountry> findAll();


  /**
   * This method saves the information of the given crpProgramCountry
   * 
   * @param crpProgramCountry - is the crpProgramCountry object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpProgramCountry was
   *         updated
   *         or -1 is some error occurred.
   */
  public CrpProgramCountry save(CrpProgramCountry crpProgramCountry);
}
