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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyManagerImpl implements ProjectPolicyManager {


  private ProjectPolicyDAO projectPolicyDAO;
  // Managers


  @Inject
  public ProjectPolicyManagerImpl(ProjectPolicyDAO projectPolicyDAO) {
    this.projectPolicyDAO = projectPolicyDAO;


  }

  @Override
  public void deleteProjectPolicy(long projectPolicyId) {

    projectPolicyDAO.deleteProjectPolicy(projectPolicyId);
  }

  @Override
  public boolean existProjectPolicy(long projectPolicyID) {

    return projectPolicyDAO.existProjectPolicy(projectPolicyID);
  }

  @Override
  public List<ProjectPolicy> findAll() {

    return projectPolicyDAO.findAll();

  }

  @Override
  public ProjectPolicy getProjectPolicyById(long projectPolicyID) {

    return projectPolicyDAO.find(projectPolicyID);
  }

  @Override
  public ProjectPolicy saveProjectPolicy(ProjectPolicy projectPolicy) {

    return projectPolicyDAO.save(projectPolicy);
  }

  @Override
  public ProjectPolicy saveProjectPolicy(ProjectPolicy projectPolicy, String section, List<String> relationsName,
    Phase phase) {
    return projectPolicyDAO.save(projectPolicy, section, relationsName, phase);
  }


}
