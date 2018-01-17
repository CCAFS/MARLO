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

import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;

import java.util.List;

public interface GlobalUnitProjectDAO {

  /**
   * This method removes a specific globalUnitProject value from the database.
   * 
   * @param globalUnitProjectId is the globalUnitProject identifier.
   * @return true if the globalUnitProject was successfully deleted, false otherwise.
   */
  public void deleteGlobalUnitProject(long globalUnitProjectId);

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
   * This method find a globalUnitProject by the project and global unit
   * 
   * @param globalUnitProject - is the globalUnitProject object with the new information to be added/updated.
   * @return GlobalUnitProject object created or updated.
   */
  public GlobalUnitProject findByProjectAndGlobalUnitId(long projectId, long globalUnitId);

  /**
   * This method find a globalUnitProject by the project and global unit
   * 
   * @param globalUnitProject - is the globalUnitProject object with the new information to be added/updated.
   * @return GlobalUnitProject object created or updated.
   */
  public GlobalUnitProject findByProjectId(long projectId);

  /**
   * This method saves the information of the given globalUnitProject
   * 
   * @param globalUnitProject - is the globalUnitProject object with the new information to be added/updated.
   * @return GlobalUnitProject object created or updated.
   */
  public GlobalUnitProject save(GlobalUnitProject globalUnitProject);
}
