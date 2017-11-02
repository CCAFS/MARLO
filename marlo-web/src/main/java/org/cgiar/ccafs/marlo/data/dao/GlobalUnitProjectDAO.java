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

import org.cgiar.ccafs.marlo.data.dao.mysql.GlobalUnitProjectMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(GlobalUnitProjectMySQLDAO.class)
public interface GlobalUnitProjectDAO {

  /**
   * This method removes a specific globalUnitProject value from the database.
   * 
   * @param globalUnitProjectId is the globalUnitProject identifier.
   * @return true if the globalUnitProject was successfully deleted, false otherwise.
   */
  public boolean deleteGlobalUnitProject(long globalUnitProjectId);

  /**
   * This method validate if the globalUnitProject identify with the given id exists in the system.
   * 
   * @param globalUnitProjectID is a globalUnitProject identifier.
   * @return true if the globalUnitProject exists, false otherwise.
   */
  public boolean existGlobalUnitProject(long globalUnitProjectID);

  /**
   * This method gets a globalUnitProject object by a given globalUnitProject identifier.
   * 
   * @param globalUnitProjectID is the globalUnitProject identifier.
   * @return a GlobalUnitProject object.
   */
  public GlobalUnitProject find(long id);

  /**
   * This method gets a list of globalUnitProject that are active
   * 
   * @return a list from GlobalUnitProject null if no exist records
   */
  public List<GlobalUnitProject> findAll();


  /**
   * This method saves the information of the given globalUnitProject
   * 
   * @param globalUnitProject - is the globalUnitProject object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the globalUnitProject was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(GlobalUnitProject globalUnitProject);
}
