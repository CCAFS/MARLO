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

import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;

import java.util.List;


/**
 * RepositoryChannelManager
 * 
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 9:09:19 AM: Manager Creation
 * @time 10:22:12 AM: added getRepositoryChannelByShortName
 */
public interface RepositoryChannelManager {


  /**
   * This method removes a specific repositoryChannel value from the database.
   * 
   * @param repositoryChannelId is the repositoryChannel identifier.
   */
  public void deleteRepositoryChannel(long repositoryChannelId);


  /**
   * This method validate if the repositoryChannel identify with the given id exists in the system.
   * 
   * @param repositoryChannelID is a repositoryChannel identifier.
   * @return true if the repositoryChannel exists, false otherwise.
   */
  public boolean existRepositoryChannel(long repositoryChannelID);


  /**
   * This method gets a list of repositoryChannel that are active
   * 
   * @return a list from RepositoryChannel null if no exist records
   */
  public List<RepositoryChannel> findAll();


  /**
   * This method gets a repositoryChannel object by a given repositoryChannel identifier.
   * 
   * @param repositoryChannelID is the repositoryChannel identifier.
   * @return a RepositoryChannel object.
   */
  public RepositoryChannel getRepositoryChannelById(long repositoryChannelID);

  /**
   * Get the RepositoryChannel identified by the specified shortName parameter.
   * 
   * @param shortName of the RepositoryChannel.
   * @return RepositoryChannel object representing the RepositoryChannel identified by the shortName provided or Null if
   *         the RepositoryChannel doesn't exist in the
   *         database.
   */
  public RepositoryChannel getRepositoryChannelByShortName(String shortName);

  /**
   * This method saves the information of the given repositoryChannels
   * 
   * @param repositoryChannel - is the repositoryChannel object with the new information to be added/updated.
   * @return a RepositoryChannel object successfully saved
   */
  public RepositoryChannel saveRepositoryChannel(RepositoryChannel repositoryChannel);
}
