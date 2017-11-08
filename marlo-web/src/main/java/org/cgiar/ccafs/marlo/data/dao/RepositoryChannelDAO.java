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

import org.cgiar.ccafs.marlo.data.dao.mysql.RepositoryChannelMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * RepositoryChannelDAO
 * 
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 9:09:08 AM: DAO creation
 * @time 10:22:12 AM: added findByShortName
 */
@ImplementedBy(RepositoryChannelMySQLDAO.class)
public interface RepositoryChannelDAO {

  /**
   * This method removes a specific RepositoryChannel value from the database.
   * 
   * @param repositoryChannel is the RepositoryChannel identifier.
   */
  public void deleteRepositoryChannel(long repositoryChannel);

  /**
   * This method validate if the RepositoryChannel identify with the given id exists in the system.
   * 
   * @param repositoryChannelID is a RepositoryChannel identifier.
   * @return true if the RepositoryChannel exists, false otherwise.
   */
  public boolean existRepositoryChannel(long repositoryChannelID);

  /**
   * This method gets a RepositoryChannel object by a given RepositoryChannel identifier.
   * 
   * @param id is the RepositoryChannel identifier.
   * @return a RepositoryChannel object.
   */
  public RepositoryChannel find(long id);

  /**
   * This method gets a list of RepositoryChannel that are active
   * 
   * @return a list from RepositoryChannel null if no exist records
   */
  public List<RepositoryChannel> findAll();

  /**
   * This method gets a RepositoryChannel object by a given RepositoryChannel shortName.
   * 
   * @param shortName is the RepositoryChannel shortName.
   * @return a RepositoryChannel object.
   */
  public RepositoryChannel findbyShortName(String shortName);


  /**
   * This method saves the information of the given RepositoryChannel
   * 
   * @param repositoryChannel - is the RepositoryChannel object with the new information to be added/updated.
   * @return a RepositoryChannel object successfully saved
   */
  public RepositoryChannel save(RepositoryChannel repositoryChannel);
}
