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

import org.cgiar.ccafs.marlo.data.dao.mysql.IpLiaisonInstitutionMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;

import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;

@ImplementedBy(IpLiaisonInstitutionMySQLDAO.class)
public interface IpLiaisonInstitutionDAO {

  /**
   * This method removes a specific ipLiaisonInstitution value from the database.
   * 
   * @param ipLiaisonInstitutionId is the ipLiaisonInstitution identifier.
   * @return true if the ipLiaisonInstitution was successfully deleted, false otherwise.
   */
  public void deleteIpLiaisonInstitution(long ipLiaisonInstitutionId);

  /**
   * This method validate if the ipLiaisonInstitution identify with the given id exists in the system.
   * 
   * @param ipLiaisonInstitutionID is a ipLiaisonInstitution identifier.
   * @return true if the ipLiaisonInstitution exists, false otherwise.
   */
  public boolean existIpLiaisonInstitution(long ipLiaisonInstitutionID);

  /**
   * This method gets a ipLiaisonInstitution object by a given ipLiaisonInstitution identifier.
   * 
   * @param ipLiaisonInstitutionID is the ipLiaisonInstitution identifier.
   * @return a IpLiaisonInstitution object.
   */
  public IpLiaisonInstitution find(long id);

  /**
   * This method gets a list of ipLiaisonInstitution that are active
   * 
   * @return a list from IpLiaisonInstitution null if no exist records
   */
  public List<IpLiaisonInstitution> findAll();

  /**
   * This method gets a ipLiaisonInstitution object by a given ipprogram identifier.
   * 
   * @param ipProgramID is the ipprogram identifier.
   * @return a IpLiaisonInstitution object.
   */
  public IpLiaisonInstitution findByIpProgram(long ipProgramID);


  /**
   * This method return all the center for crps in the database.
   * 
   * @return a List of maps with the information.
   */
  public List<IpLiaisonInstitution> getLiaisonInstitutionsCrpsIndicator();

  /**
   * This method return all the center for crps in the database.
   * 
   * @return a List of maps with the information.
   */
  public List<Map<String, Object>> getLiaisonInstitutionSynthesisByMog();

  /**
   * This method saves the information of the given ipLiaisonInstitution
   * 
   * @param ipLiaisonInstitution - is the ipLiaisonInstitution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipLiaisonInstitution was
   *         updated
   *         or -1 is some error occurred.
   */
  public IpLiaisonInstitution save(IpLiaisonInstitution ipLiaisonInstitution);

  public IpLiaisonInstitution save(IpLiaisonInstitution ipLiaisonInstitution, String section, List<String> relationsName);
}
