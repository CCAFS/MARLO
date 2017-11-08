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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.RepositoryChannelDAO;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

/**
 * RepositoryChannelMySQLDAO
 * 
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 8:49:38 AM: MySQLDAO creation
 */
public class RepositoryChannelMySQLDAO extends AbstractMarloDAO<RepositoryChannel, Long>
  implements RepositoryChannelDAO {


  @Inject
  public RepositoryChannelMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteRepositoryChannel(long repositoryChannelId) {
    RepositoryChannel repositoryChannel = this.find(repositoryChannelId);
    repositoryChannel.setActive(false);
    this.save(repositoryChannel);
  }

  @Override
  public boolean existRepositoryChannel(long RepositoryChannelID) {
    RepositoryChannel repositoryChannel = this.find(RepositoryChannelID);
    if (repositoryChannel == null) {
      return false;
    }
    return true;

  }

  @Override
  public RepositoryChannel find(long id) {
    return super.find(RepositoryChannel.class, id);

  }

  @Override
  public List<RepositoryChannel> findAll() {
    String query = "from " + RepositoryChannel.class.getName() + " where is_active=1";
    List<RepositoryChannel> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public RepositoryChannel save(RepositoryChannel repositoryChannel) {
    if (repositoryChannel.getId() == null) {
      repositoryChannel = super.saveEntity(repositoryChannel);
    } else {
      repositoryChannel = super.update(repositoryChannel);
    }
    return repositoryChannel;
  }


}