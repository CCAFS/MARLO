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

import org.cgiar.ccafs.marlo.data.dao.GlobalUnitProjectDAO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class GlobalUnitProjectMySQLDAO extends AbstractMarloDAO<GlobalUnitProject, Long>
  implements GlobalUnitProjectDAO {


  @Inject
  public GlobalUnitProjectMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteGlobalUnitProject(long globalUnitProjectId) {
    GlobalUnitProject globalUnitProject = this.find(globalUnitProjectId);
    globalUnitProject.setActive(false);
    this.save(globalUnitProject);
  }

  @Override
  public boolean existGlobalUnitProject(long globalUnitProjectID) {
    GlobalUnitProject globalUnitProject = this.find(globalUnitProjectID);
    if (globalUnitProject == null) {
      return false;
    }
    return true;

  }

  @Override
  public GlobalUnitProject find(long id) {
    return super.find(GlobalUnitProject.class, id);

  }

  @Override
  public List<GlobalUnitProject> findAll() {
    String query = "from " + GlobalUnitProject.class.getName() + " where is_active=1";
    List<GlobalUnitProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public GlobalUnitProject findByProjectAndGlobalUnitId(long projectId, long globalUnitId) {
    String query = "from " + GlobalUnitProject.class.getName() + " where project_id=" + projectId
      + " and global_unit_id=" + globalUnitId + " and is_active=1 and origin = 1";
    List<GlobalUnitProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public GlobalUnitProject findByProjectId(long projectId) {
    String query = "from " + GlobalUnitProject.class.getName() + " where project_id=" + projectId
      + " and is_active=1 and origin = 1";
    List<GlobalUnitProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }


  @Override
  public GlobalUnitProject save(GlobalUnitProject globalUnitProject) {
    if (globalUnitProject.getId() == null) {
      super.saveEntity(globalUnitProject);
    } else {
      super.update(globalUnitProject);
    }
    return globalUnitProject;
  }


}