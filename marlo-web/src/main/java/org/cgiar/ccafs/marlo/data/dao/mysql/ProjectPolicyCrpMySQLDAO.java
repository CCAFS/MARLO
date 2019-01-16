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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCrpDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyCrpMySQLDAO extends AbstractMarloDAO<ProjectPolicyCrp, Long> implements ProjectPolicyCrpDAO {


  @Inject
  public ProjectPolicyCrpMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyCrp(long projectPolicyCrpId) {
    ProjectPolicyCrp projectPolicyCrp = this.find(projectPolicyCrpId);
    this.delete(projectPolicyCrpId);
  }

  @Override
  public boolean existProjectPolicyCrp(long projectPolicyCrpID) {
    ProjectPolicyCrp projectPolicyCrp = this.find(projectPolicyCrpID);
    if (projectPolicyCrp == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyCrp find(long id) {
    return super.find(ProjectPolicyCrp.class, id);

  }

  @Override
  public List<ProjectPolicyCrp> findAll() {
    String query = "from " + ProjectPolicyCrp.class.getName();
    List<ProjectPolicyCrp> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicyCrp save(ProjectPolicyCrp projectPolicyCrp) {
    if (projectPolicyCrp.getId() == null) {
      super.saveEntity(projectPolicyCrp);
    } else {
      projectPolicyCrp = super.update(projectPolicyCrp);
    }


    return projectPolicyCrp;
  }


}