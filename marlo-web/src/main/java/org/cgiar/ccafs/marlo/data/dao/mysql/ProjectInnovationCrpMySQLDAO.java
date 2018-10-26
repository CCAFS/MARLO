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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCrpDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationCrpMySQLDAO extends AbstractMarloDAO<ProjectInnovationCrp, Long>
  implements ProjectInnovationCrpDAO {


  @Inject
  public ProjectInnovationCrpMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationCrp(long projectInnovationCrpId) {
    ProjectInnovationCrp projectInnovationCrp = this.find(projectInnovationCrpId);
    this.delete(projectInnovationCrp);
  }

  @Override
  public boolean existProjectInnovationCrp(long projectInnovationCrpID) {
    ProjectInnovationCrp projectInnovationCrp = this.find(projectInnovationCrpID);
    if (projectInnovationCrp == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationCrp find(long id) {
    return super.find(ProjectInnovationCrp.class, id);

  }

  @Override
  public List<ProjectInnovationCrp> findAll() {
    String query = "from " + ProjectInnovationCrp.class.getName();
    List<ProjectInnovationCrp> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationCrp save(ProjectInnovationCrp projectInnovationCrp) {
    if (projectInnovationCrp.getId() == null) {
      super.saveEntity(projectInnovationCrp);
    } else {
      projectInnovationCrp = super.update(projectInnovationCrp);
    }


    return projectInnovationCrp;
  }


}