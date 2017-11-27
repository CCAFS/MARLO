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

import org.cgiar.ccafs.marlo.data.dao.mysql.InstitutionLocationMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(InstitutionLocationMySQLDAO.class)
public interface InstitutionLocationDAO {

  /**
   * This method removes a specific institutionLocation value from the database.
   * 
   * @param institutionLocationId is the institutionLocation identifier.
   * @return true if the institutionLocation was successfully deleted, false otherwise.
   */
  public void deleteInstitutionLocation(long institutionLocationId);

  /**
   * This method validate if the institutionLocation identify with the given id exists in the system.
   * 
   * @param institutionLocationID is a institutionLocation identifier.
   * @return true if the institutionLocation exists, false otherwise.
   */
  public boolean existInstitutionLocation(long institutionLocationID);

  /**
   * This method gets a institutionLocation object by a given institutionLocation identifier.
   * 
   * @param institutionLocationID is the institutionLocation identifier.
   * @return a InstitutionLocation object.
   */
  public InstitutionLocation find(long id);

  /**
   * This method gets a list of institutionLocation that are active
   * 
   * @return a list from InstitutionLocation null if no exist records
   */
  public List<InstitutionLocation> findAll();

  public InstitutionLocation findByLocation(long locationID, long institutionID);


  /**
   * This method saves the information of the given institutionLocation
   * 
   * @param institutionLocation - is the institutionLocation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the institutionLocation was
   *         updated
   *         or -1 is some error occurred.
   */
  public InstitutionLocation save(InstitutionLocation institutionLocation);
}
