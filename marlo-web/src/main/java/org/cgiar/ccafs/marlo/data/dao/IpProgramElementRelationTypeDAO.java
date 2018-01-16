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

import org.cgiar.ccafs.marlo.data.model.IpProgramElementRelationType;

import java.util.List;


public interface IpProgramElementRelationTypeDAO {

  /**
   * This method removes a specific ipProgramElementRelationType value from the database.
   * 
   * @param ipProgramElementRelationTypeId is the ipProgramElementRelationType identifier.
   * @return true if the ipProgramElementRelationType was successfully deleted, false otherwise.
   */
  public void deleteIpProgramElementRelationType(long ipProgramElementRelationTypeId);

  /**
   * This method validate if the ipProgramElementRelationType identify with the given id exists in the system.
   * 
   * @param ipProgramElementRelationTypeID is a ipProgramElementRelationType identifier.
   * @return true if the ipProgramElementRelationType exists, false otherwise.
   */
  public boolean existIpProgramElementRelationType(long ipProgramElementRelationTypeID);

  /**
   * This method gets a ipProgramElementRelationType object by a given ipProgramElementRelationType identifier.
   * 
   * @param ipProgramElementRelationTypeID is the ipProgramElementRelationType identifier.
   * @return a IpProgramElementRelationType object.
   */
  public IpProgramElementRelationType find(long id);

  /**
   * This method gets a list of ipProgramElementRelationType that are active
   * 
   * @return a list from IpProgramElementRelationType null if no exist records
   */
  public List<IpProgramElementRelationType> findAll();


  /**
   * This method saves the information of the given ipProgramElementRelationType
   * 
   * @param ipProgramElementRelationType - is the ipProgramElementRelationType object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         ipProgramElementRelationType was
   *         updated
   *         or -1 is some error occurred.
   */
  public IpProgramElementRelationType save(IpProgramElementRelationType ipProgramElementRelationType);
}
