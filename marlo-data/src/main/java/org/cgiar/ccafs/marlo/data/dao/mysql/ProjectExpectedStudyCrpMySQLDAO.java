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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCrpDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyCrpMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyCrp, Long>
  implements ProjectExpectedStudyCrpDAO {


  @Inject
  public ProjectExpectedStudyCrpMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyCrp(long projectExpectedStudyCrpId) {
    ProjectExpectedStudyCrp projectExpectedStudyCrp = this.find(projectExpectedStudyCrpId);
    this.delete(projectExpectedStudyCrp);
  }

  @Override
  public boolean existProjectExpectedStudyCrp(long projectExpectedStudyCrpID) {
    ProjectExpectedStudyCrp projectExpectedStudyCrp = this.find(projectExpectedStudyCrpID);
    if (projectExpectedStudyCrp == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyCrp find(long id) {
    return super.find(ProjectExpectedStudyCrp.class, id);

  }

  @Override
  public List<ProjectExpectedStudyCrp> findAll() {
    String query = "from " + ProjectExpectedStudyCrp.class.getName();
    List<ProjectExpectedStudyCrp> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyCrp save(ProjectExpectedStudyCrp projectExpectedStudyCrp) {
    if (projectExpectedStudyCrp.getId() == null) {
      super.saveEntity(projectExpectedStudyCrp);
    } else {
      projectExpectedStudyCrp = super.update(projectExpectedStudyCrp);
    }


    return projectExpectedStudyCrp;
  }


}