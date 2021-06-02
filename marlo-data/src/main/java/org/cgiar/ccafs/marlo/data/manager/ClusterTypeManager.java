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

import org.cgiar.ccafs.marlo.data.model.ClusterType;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ClusterTypeManager {


  /**
   * This method removes a specific clusterType value from the database.
   * 
   * @param clusterTypeId is the clusterType identifier.
   * @return true if the clusterType was successfully deleted, false otherwise.
   */
  public void deleteClusterType(long clusterTypeId);


  /**
   * This method validate if the clusterType identify with the given id exists in the system.
   * 
   * @param clusterTypeID is a clusterType identifier.
   * @return true if the clusterType exists, false otherwise.
   */
  public boolean existClusterType(long clusterTypeID);


  /**
   * This method gets a list of clusterType that are active
   * 
   * @return a list from ClusterType null if no exist records
   */
  public List<ClusterType> findAll();


  /**
   * This method gets a clusterType object by a given clusterType identifier.
   * 
   * @param clusterTypeID is the clusterType identifier.
   * @return a ClusterType object.
   */
  public ClusterType getClusterTypeById(long clusterTypeID);

  /**
   * This method saves the information of the given clusterType
   * 
   * @param clusterType - is the clusterType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the clusterType was
   *         updated
   *         or -1 is some error occurred.
   */
  public ClusterType saveClusterType(ClusterType clusterType);


}
