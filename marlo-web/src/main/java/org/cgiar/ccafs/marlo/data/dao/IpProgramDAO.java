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

import org.cgiar.ccafs.marlo.data.dao.mysql.IpProgramMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.IpProgram;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(IpProgramMySQLDAO.class)
public interface IpProgramDAO {

  /**
   * This method removes a specific ipProgram value from the database.
   * 
   * @param ipProgramId is the ipProgram identifier.
   * @return true if the ipProgram was successfully deleted, false otherwise.
   */
  public boolean deleteIpProgram(long ipProgramId);

  /**
   * This method validate if the ipProgram identify with the given id exists in the system.
   * 
   * @param ipProgramID is a ipProgram identifier.
   * @return true if the ipProgram exists, false otherwise.
   */
  public boolean existIpProgram(long ipProgramID);

  /**
   * This method gets a ipProgram object by a given ipProgram identifier.
   * 
   * @param ipProgramID is the ipProgram identifier.
   * @return a IpProgram object.
   */
  public IpProgram find(long id);

  /**
   * This method gets a list of ipProgram that are active
   * 
   * @return a list from IpProgram null if no exist records
   */
  public List<IpProgram> findAll();


  /**
   * This method saves the information of the given ipProgram
   * 
   * @param ipProgram - is the ipProgram object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ipProgram was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(IpProgram ipProgram);

  /**
   * This method saves the information of the given ip program and save the history in the auditlog
   * 
   * @param ipProgram - is the ip program object with the new information to be added/updated.
   * @param section - the action name of the section that execute the save method
   * @param relationsName - the model class relations of deliverables that save in the auditlog.
   * @return
   */
  public long save(IpProgram ipProgram, String section, List<String> relationsName);
}
