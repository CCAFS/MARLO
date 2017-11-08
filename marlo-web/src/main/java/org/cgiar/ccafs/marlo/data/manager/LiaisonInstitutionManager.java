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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.LiaisonInstitutionManagerImpl;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(LiaisonInstitutionManagerImpl.class)
public interface LiaisonInstitutionManager {


  /**
   * This method removes a specific liaisonInstitution value from the database.
   * 
   * @param liaisonInstitutionId is the liaisonInstitution identifier.
   * @return true if the liaisonInstitution was successfully deleted, false otherwise.
   */
  public void deleteLiaisonInstitution(long liaisonInstitutionId);


  /**
   * This method validate if the liaisonInstitution identify with the given id exists in the system.
   * 
   * @param liaisonInstitutionID is a liaisonInstitution identifier.
   * @return true if the liaisonInstitution exists, false otherwise.
   */
  public boolean existLiaisonInstitution(long liaisonInstitutionID);


  /**
   * This method gets a list of liaisonInstitution that are active
   * 
   * @return a list from LiaisonInstitution null if no exist records
   */
  public List<LiaisonInstitution> findAll();


  /**
   * This method gets a liaisonInstitution object by a given liaisonInstitution acronym.
   * 
   * @param acronym is the liaisonInstitution acronym.
   * @return a LiaisonInstitution object.
   */
  public LiaisonInstitution findByAcronym(String acronym);

  /**
   * This method gets a liaisonInstitution object by a given liaisonInstitution identifier.
   * 
   * @param liaisonInstitutionID is the liaisonInstitution identifier.
   * @return a LiaisonInstitution object.
   */
  public LiaisonInstitution getLiaisonInstitutionById(long liaisonInstitutionID);


  /**
   * This method saves the information of the given liaisonInstitution
   * 
   * @param liaisonInstitution - is the liaisonInstitution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the liaisonInstitution was
   *         updated
   *         or -1 is some error occurred.
   */
  public LiaisonInstitution saveLiaisonInstitution(LiaisonInstitution liaisonInstitution);

}
