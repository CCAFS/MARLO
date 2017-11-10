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

import org.cgiar.ccafs.marlo.data.model.CenterProgram;

import java.util.List;


public interface ICenterProgramDAO {

  /**
   * This method removes a specific CenterProgram value from the database.
   * 
   * @param programId is the CenterProgram identifier.
   * @return true if the CenterProgram was successfully deleted, false otherwise.
   */
  public void deleteProgram(long programId);

  /**
   * This method validate if the CenterProgram identify with the given id exists in the system.
   * 
   * @param programID is a CenterProgram identifier.
   * @return true if the CenterProgram exists, false otherwise.
   */
  public boolean existProgram(long programID);

  /**
   * This method gets a CenterProgram object by a given program identifier.
   * 
   * @param programID is the CenterProgram identifier.
   * @return a CenterProgram object.
   */
  public CenterProgram find(long id);

  /**
   * This method gets a list of CenterProgram that are active
   * 
   * @return a list from CenterProgram null if no exist records
   */
  public List<CenterProgram> findAll();

  /**
   * Lists all the CenterProgram for the research area with the given id
   * 
   * @param researchAreaId the research area id or primary key
   * @return List of research programs for the given research area id or else an empty list will be returned.
   */
  public List<CenterProgram> findProgramsByResearchArea(Long researchAreaId);


  /**
   * This method gets a list of CenterProgram of a specific program type
   * 
   * @return a CenterProgram list
   */
  public List<CenterProgram> findProgramsByType(long id, int programType);

  /**
   * This method saves the information of the given CenterProgram
   * 
   * @param researchProgram - is the CenterProgram object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the CenterProgram was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProgram save(CenterProgram researchProgram);

  /**
   * This method saves the information of the given CenterProgram
   * 
   * @param researchProgram - is the CenterProgram object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the CenterProgram was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProgram save(CenterProgram researchProgram, String actionName, List<String> relationsName);
}
