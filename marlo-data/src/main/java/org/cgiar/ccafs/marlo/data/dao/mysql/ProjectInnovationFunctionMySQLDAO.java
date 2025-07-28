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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationFunctionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationFunction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationFunctionMySQLDAO extends AbstractMarloDAO<ProjectInnovationFunction, Long> implements ProjectInnovationFunctionDAO {


  @Inject
  public ProjectInnovationFunctionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationFunction(long projectInnovationFunctionId) {
    ProjectInnovationFunction projectInnovationFunction = this.find(projectInnovationFunctionId);
    projectInnovationFunction.setActive(false);
    this.update(projectInnovationFunction);
  }

  @Override
  public boolean existProjectInnovationFunction(long projectInnovationFunctionID) {
    ProjectInnovationFunction projectInnovationFunction = this.find(projectInnovationFunctionID);
    if (projectInnovationFunction == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationFunction find(long id) {
    return super.find(ProjectInnovationFunction.class, id);

  }

  @Override
  public List<ProjectInnovationFunction> findAll() {
    String query = "from " + ProjectInnovationFunction.class.getName() + " where is_active=1";
    List<ProjectInnovationFunction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationFunction save(ProjectInnovationFunction projectInnovationFunction) {
    if (projectInnovationFunction.getId() == null) {
      super.saveEntity(projectInnovationFunction);
    } else {
      projectInnovationFunction = super.update(projectInnovationFunction);
    }


    return projectInnovationFunction;
  }


}