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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.RepositoryChannelDAO;
import org.cgiar.ccafs.marlo.data.manager.RepositoryChannelManager;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RepositoryChannelManagerImpl
 * 
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 9:09:33 AM: Manager Impl creation
 * @time 10:22:12 AM: added getRepositoryChannelByShortName
 */
@Named
public class RepositoryChannelManagerImpl implements RepositoryChannelManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(RepositoryChannelManagerImpl.class);

  private RepositoryChannelDAO repositoryChannelDAO;
  // Managers


  @Inject
  public RepositoryChannelManagerImpl(RepositoryChannelDAO repositoryChannelDAO) {
    this.repositoryChannelDAO = repositoryChannelDAO;
  }

  @Override
  public void deleteRepositoryChannel(long repositoryChannelId) {

    repositoryChannelDAO.deleteRepositoryChannel(repositoryChannelId);
  }

  @Override
  public boolean existRepositoryChannel(long repositoryChannelID) {
    return repositoryChannelDAO.existRepositoryChannel(repositoryChannelID);
  }

  @Override
  public List<RepositoryChannel> findAll() {
    return repositoryChannelDAO.findAll();
  }

  @Override
  public RepositoryChannel getRepositoryChannelById(long repositoryChannelID) {

    return repositoryChannelDAO.find(repositoryChannelID);
  }

  @Override
  public RepositoryChannel getRepositoryChannelByShortName(String shortName) {
    RepositoryChannel repositoryChannel = repositoryChannelDAO.findbyShortName(shortName);
    if (repositoryChannel != null) {
      return repositoryChannel;
    }
    LOG.warn("Information related to the repositoryChannel {} wasn't found.", shortName);
    return null;
  }

  @Override
  public RepositoryChannel saveRepositoryChannel(RepositoryChannel repositoryChannel) {

    return repositoryChannelDAO.save(repositoryChannel);
  }

}
