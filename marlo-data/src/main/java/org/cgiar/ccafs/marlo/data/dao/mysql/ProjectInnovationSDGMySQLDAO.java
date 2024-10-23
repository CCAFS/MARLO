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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSDGDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSDG;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationSDGMySQLDAO extends AbstractMarloDAO<ProjectInnovationSDG, Long> implements ProjectInnovationSDGDAO {


  @Inject
  public ProjectInnovationSDGMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationSDG(long projectInnovationSDGId) {
    ProjectInnovationSDG projectInnovationSDG = this.find(projectInnovationSDGId);
    projectInnovationSDG.setActive(false);
    this.update(projectInnovationSDG);
  }

  @Override
  public boolean existProjectInnovationSDG(long projectInnovationSDGID) {
    ProjectInnovationSDG projectInnovationSDG = this.find(projectInnovationSDGID);
    if (projectInnovationSDG == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationSDG find(long id) {
    return super.find(ProjectInnovationSDG.class, id);

  }

  @Override
  public List<ProjectInnovationSDG> findAll() {
    String query = "from " + ProjectInnovationSDG.class.getName() + " where is_active=1";
    List<ProjectInnovationSDG> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationSDG save(ProjectInnovationSDG projectInnovationSDG) {
    if (projectInnovationSDG.getId() == null) {
      super.saveEntity(projectInnovationSDG);
    } else {
      projectInnovationSDG = super.update(projectInnovationSDG);
    }


    return projectInnovationSDG;
  }


}