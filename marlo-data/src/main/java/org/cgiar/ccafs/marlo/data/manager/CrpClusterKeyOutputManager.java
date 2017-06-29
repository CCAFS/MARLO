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

import org.cgiar.ccafs.marlo.data.manager.impl.CrpClusterKeyOutputManagerImpl;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CrpClusterKeyOutputManagerImpl.class)
public interface CrpClusterKeyOutputManager {


  /**
   * This method removes a specific crpClusterKeyOutput value from the database.
   * 
   * @param crpClusterKeyOutputId is the crpClusterKeyOutput identifier.
   * @return true if the crpClusterKeyOutput was successfully deleted, false otherwise.
   */
  public boolean deleteCrpClusterKeyOutput(long crpClusterKeyOutputId);


  /**
   * This method validate if the crpClusterKeyOutput identify with the given id exists in the system.
   * 
   * @param crpClusterKeyOutputID is a crpClusterKeyOutput identifier.
   * @return true if the crpClusterKeyOutput exists, false otherwise.
   */
  public boolean existCrpClusterKeyOutput(long crpClusterKeyOutputID);


  /**
   * This method gets a list of crpClusterKeyOutput that are active
   * 
   * @return a list from CrpClusterKeyOutput null if no exist records
   */
  public List<CrpClusterKeyOutput> findAll();


  /**
   * This method gets a crpClusterKeyOutput object by a given crpClusterKeyOutput identifier.
   * 
   * @param crpClusterKeyOutputID is the crpClusterKeyOutput identifier.
   * @return a CrpClusterKeyOutput object.
   */
  public CrpClusterKeyOutput getCrpClusterKeyOutputById(long crpClusterKeyOutputID);

  /**
   * This method saves the information of the given crpClusterKeyOutput
   * 
   * @param crpClusterKeyOutput - is the crpClusterKeyOutput object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the crpClusterKeyOutput was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveCrpClusterKeyOutput(CrpClusterKeyOutput crpClusterKeyOutput);


}
