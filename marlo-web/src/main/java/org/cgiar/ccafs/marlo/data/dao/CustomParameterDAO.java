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

import org.cgiar.ccafs.marlo.data.dao.mysql.CustomParameterMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CustomParameterMySQLDAO.class)
public interface CustomParameterDAO {

  /**
   * This method removes a specific customParameter value from the database.
   * 
   * @param customParameterId is the customParameter identifier.
   * @return true if the customParameter was successfully deleted, false otherwise.
   */
  public void deleteCustomParameter(long customParameterId);

  /**
   * This method validate if the customParameter identify with the given id exists in the system.
   * 
   * @param customParameterID is a customParameter identifier.
   * @return true if the customParameter exists, false otherwise.
   */
  public boolean existCustomParameter(long customParameterID);

  /**
   * This method gets a customParameter object by a given customParameter identifier.
   * 
   * @param customParameterID is the customParameter identifier.
   * @return a CustomParameter object.
   */
  public CustomParameter find(long id);

  /**
   * This method gets a list of customParameter that are active
   * 
   * @return a list from CustomParameter null if no exist records
   */
  public List<CustomParameter> findAll();


  /**
   * This method saves the information of the given customParameter
   * 
   * @param customParameter - is the customParameter object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the customParameter was
   *         updated
   *         or -1 is some error occurred.
   */
  public CustomParameter save(CustomParameter customParameter);
}
