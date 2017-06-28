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

import org.cgiar.ccafs.marlo.data.dao.impl.InstitutionDAO;
import org.cgiar.ccafs.marlo.data.model.Institution;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(InstitutionDAO.class)
public interface IInstitutionDAO {

  /**
   * This method removes a specific institution value from the database.
   * 
   * @param institutionId is the institution identifier.
   * @return true if the institution was successfully deleted, false otherwise.
   */
  public boolean deleteInstitution(long institutionId);

  /**
   * This method validate if the institution identify with the given id exists in the system.
   * 
   * @param institutionID is a institution identifier.
   * @return true if the institution exists, false otherwise.
   */
  public boolean existInstitution(long institutionID);

  /**
   * This method gets a institution object by a given institution identifier.
   * 
   * @param institutionID is the institution identifier.
   * @return a Institution object.
   */
  public Institution find(long id);

  /**
   * This method gets a list of institution that are active
   * 
   * @return a list from Institution null if no exist records
   */
  public List<Institution> findAll();


  /**
   * This method gets a list of institutions belongs of the user
   * 
   * @param userId - the user id
   * @return List of Institutions or null if the user is invalid or not have roles.
   */
  public List<Institution> getInstitutionsByUserId(long userId);

  /**
   * This method saves the information of the given institution
   * 
   * @param institution - is the institution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the institution was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(Institution institution);
}
