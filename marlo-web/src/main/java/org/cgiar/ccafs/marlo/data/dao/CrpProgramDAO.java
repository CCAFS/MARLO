/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.dao.mysql.CrpProgramMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(CrpProgramMySQLDAO.class)
public interface CrpProgramDAO {

  /**
   * This method removes a specific crpProgram value from the database.
   * 
   * @param crpProgramId is the crpProgram identifier.
   * @return true if the crpProgram was successfully deleted, false otherwise.
   */
  public boolean deleteCrpProgram(long crpProgramId);

  /**
   * This method validate if the crpProgram identify with the given id exists in the system.
   * 
   * @param crpProgramID is a crpProgram identifier.
   * @return true if the crpProgram exists, false otherwise.
   */
  public boolean existCrpProgram(long crpProgramID);

  /**
   * This method gets a crpProgram object by a given crpProgram identifier.
   * 
   * @param crpProgramID is the crpProgram identifier.
   * @return a CrpProgram object.
   */
  public CrpProgram find(long id);

  /**
   * This method gets a list of crpProgram that are active
   * 
   * @return a list from CrpProgram null if no exist records
   */
  public List<CrpProgram> findAll();

  /**
   * This method gets a list of crpProgram of a specific program type
   * 
   * @return a CrpProgram list
   */
  public List<CrpProgram> findCrpProgramsByType(long id, int programType);


  /**
   * This method saves the information of the given crpProgram
   * 
   * @param crpProgram - is the crpProgram object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpProgram was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(CrpProgram crpProgram);
}
