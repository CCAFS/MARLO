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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectFundingSourceDAO extends AbstractMarloDAO<CenterProjectFundingSource, Long>
  implements ICenterProjectFundingSourceDAO {


  @Inject
  public CenterProjectFundingSourceDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectFundingSource(long projectFundingSourceId) {
    CenterProjectFundingSource projectFundingSource = this.find(projectFundingSourceId);
    projectFundingSource.setActive(false);
    return this.save(projectFundingSource) > 0;
  }

  @Override
  public boolean existProjectFundingSource(long projectFundingSourceID) {
    CenterProjectFundingSource projectFundingSource = this.find(projectFundingSourceID);
    if (projectFundingSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProjectFundingSource find(long id) {
    return super.find(CenterProjectFundingSource.class, id);

  }

  @Override
  public List<CenterProjectFundingSource> findAll() {
    String query = "from " + CenterProjectFundingSource.class.getName();
    List<CenterProjectFundingSource> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProjectFundingSource> getProjectFundingSourcesByUserId(long userId) {
    String query = "from " + CenterProjectFundingSource.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterProjectFundingSource projectFundingSource) {
    if (projectFundingSource.getId() == null) {
      super.saveEntity(projectFundingSource);
    } else {
      super.update(projectFundingSource);
    }
    return projectFundingSource.getId();
  }


}