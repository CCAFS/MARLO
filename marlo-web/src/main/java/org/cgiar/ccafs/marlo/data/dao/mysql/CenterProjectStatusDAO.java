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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectStatusDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectStatus;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectStatusDAO extends AbstractMarloDAO implements ICenterProjectStatusDAO {


  @Inject
  public CenterProjectStatusDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectStatus(long projectStatusId) {
    CenterProjectStatus projectStatus = this.find(projectStatusId);
    projectStatus.setActive(false);
    return this.save(projectStatus) > 0;
  }

  @Override
  public boolean existProjectStatus(long projectStatusID) {
    CenterProjectStatus projectStatus = this.find(projectStatusID);
    if (projectStatus == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProjectStatus find(long id) {
    return super.find(CenterProjectStatus.class, id);

  }

  @Override
  public List<CenterProjectStatus> findAll() {
    String query = "from " + CenterProjectStatus.class.getName();
    List<CenterProjectStatus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProjectStatus> getProjectStatussByUserId(long userId) {
    String query = "from " + CenterProjectStatus.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterProjectStatus projectStatus) {
    if (projectStatus.getId() == null) {
      super.save(projectStatus);
    } else {
      super.update(projectStatus);
    }
    return projectStatus.getId();
  }


}