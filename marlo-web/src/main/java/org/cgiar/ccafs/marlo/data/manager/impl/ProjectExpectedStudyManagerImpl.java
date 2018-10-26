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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectExpectedStudyManagerImpl implements ProjectExpectedStudyManager {


  private ProjectExpectedStudyDAO projectExpectedStudyDAO;


  @Inject
  public ProjectExpectedStudyManagerImpl(ProjectExpectedStudyDAO projectExpectedStudyDAO) {
    this.projectExpectedStudyDAO = projectExpectedStudyDAO;
  }

  @Override
  public void deleteProjectExpectedStudy(long projectExpectedStudyId) {
    projectExpectedStudyDAO.deleteProjectExpectedStudy(projectExpectedStudyId);

  }


  @Override
  public boolean existProjectExpectedStudy(long projectExpectedStudyID) {
    return projectExpectedStudyDAO.existProjectExpectedStudy(projectExpectedStudyID);
  }


  @Override
  public List<ProjectExpectedStudy> findAll() {
    return projectExpectedStudyDAO.findAll();
  }


  @Override
  public ProjectExpectedStudy getProjectExpectedStudyById(long projectExpectedStudyID) {
    return projectExpectedStudyDAO.find(projectExpectedStudyID);
  }


  @Override
  public List<ProjectExpectedStudy> getStudiesByOrganizationType(RepIndOrganizationType repIndOrganizationType,
    Phase phase) {
    return projectExpectedStudyDAO.getStudiesByOrganizationType(repIndOrganizationType, phase);
  }


  @Override
  public List<ProjectExpectedStudy> getStudiesByPhase(Phase phase) {
    return projectExpectedStudyDAO.getStudiesByPhase(phase);
  }

  @Override
  public List<ProjectExpectedStudy> getUserStudies(long userId, String crp) {

    List<ProjectExpectedStudy> projects = new ArrayList<>();

    List<Map<String, Object>> view = projectExpectedStudyDAO.getUserStudies(userId, crp);

    if (view != null) {
      for (Map<String, Object> map : view) {
        projects.add(this.getProjectExpectedStudyById((Long.parseLong(map.get("project_id").toString()))));
      }
    }


    return projects;
  }

  @Override
  public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy, String section,
    List<String> relationsName, Phase phase) {
    return projectExpectedStudyDAO.save(projectExpectedStudy, section, relationsName, phase);
  }

  @Override
  public ProjectExpectedStudy saveProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    return projectExpectedStudyDAO.save(projectExpectedStudy);
  }

}
