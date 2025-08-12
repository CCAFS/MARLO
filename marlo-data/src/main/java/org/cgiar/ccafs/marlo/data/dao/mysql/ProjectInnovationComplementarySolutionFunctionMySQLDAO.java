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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionFunctionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolutionFunction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationComplementarySolutionFunctionMySQLDAO
  extends AbstractMarloDAO<ProjectInnovationComplementarySolutionFunction, Long>
  implements ProjectInnovationComplementarySolutionFunctionDAO {


  @Inject
  public ProjectInnovationComplementarySolutionFunctionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionId) {
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction =
      this.find(projectInnovationComplementarySolutionFunctionId);
    projectInnovationComplementarySolutionFunction.setActive(false);
    this.update(projectInnovationComplementarySolutionFunction);
  }

  @Override
  public boolean
    existProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionID) {
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction =
      this.find(projectInnovationComplementarySolutionFunctionID);
    if (projectInnovationComplementarySolutionFunction == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationComplementarySolutionFunction find(long id) {
    return super.find(ProjectInnovationComplementarySolutionFunction.class, id);

  }

  @Override
  public List<ProjectInnovationComplementarySolutionFunction> findAll() {
    String query = "from " + ProjectInnovationComplementarySolutionFunction.class.getName() + " where is_active=1";
    List<ProjectInnovationComplementarySolutionFunction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationComplementarySolutionFunction>
    getProjectInnovationComplementarySolutionFunctionByComplementarySolutionId(long complementarySolutionID) {
    String query = "from " + ProjectInnovationComplementarySolutionFunction.class.getName()
      + " where is_active=1 and complementary_solution_id = " + complementarySolutionID;
    List<ProjectInnovationComplementarySolutionFunction> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectInnovationComplementarySolutionFunction
    save(ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction) {
    if (projectInnovationComplementarySolutionFunction.getId() == null) {
      super.saveEntity(projectInnovationComplementarySolutionFunction);
    } else {
      projectInnovationComplementarySolutionFunction = super.update(projectInnovationComplementarySolutionFunction);
    }


    return projectInnovationComplementarySolutionFunction;
  }


}