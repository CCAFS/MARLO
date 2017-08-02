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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProjectType;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectTypeDAO extends AbstractMarloDAO implements ICenterProjectTypeDAO {


  @Inject
  public CenterProjectTypeDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectType(long projectTypeId) {
    CenterProjectType projectType = this.find(projectTypeId);
    projectType.setActive(false);
    return this.save(projectType) > 0;
  }

  @Override
  public boolean existProjectType(long projectTypeID) {
    CenterProjectType projectType = this.find(projectTypeID);
    if (projectType == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProjectType find(long id) {
    return super.find(CenterProjectType.class, id);

  }

  @Override
  public List<CenterProjectType> findAll() {
    String query = "from " + CenterProjectType.class.getName();
    List<CenterProjectType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProjectType> getProjectTypesByUserId(long userId) {
    String query = "from " + CenterProjectType.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public long save(CenterProjectType projectType) {
    if (projectType.getId() == null) {
      super.save(projectType);
    } else {
      super.update(projectType);
    }
    return projectType.getId();
  }

  @Override
  public long save(CenterProjectType projectType, String actionName, List<String> relationsName) {
    if (projectType.getId() == null) {
      super.save(projectType, actionName, relationsName);
    } else {
      super.update(projectType, actionName, relationsName);
    }
    return projectType.getId();
  }


}