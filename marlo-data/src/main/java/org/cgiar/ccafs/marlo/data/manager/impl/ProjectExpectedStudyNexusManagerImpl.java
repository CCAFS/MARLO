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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyNexusDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyNexusManager;
import org.cgiar.ccafs.marlo.data.model.Nexus;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyNexus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyNexusManagerImpl implements ProjectExpectedStudyNexusManager {


  private ProjectExpectedStudyNexusDAO projectExpectedStudyNexusDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyNexusManagerImpl(ProjectExpectedStudyNexusDAO projectExpectedStudyNexusDAO) {
    this.projectExpectedStudyNexusDAO = projectExpectedStudyNexusDAO;


  }

  @Override
  public void deleteProjectExpectedStudyNexus(long projectExpectedStudyNexusId) {

    projectExpectedStudyNexusDAO.deleteProjectExpectedStudyNexus(projectExpectedStudyNexusId);
  }

  @Override
  public boolean existProjectExpectedStudyNexus(long projectExpectedStudyNexusID) {

    return projectExpectedStudyNexusDAO.existProjectExpectedStudyNexus(projectExpectedStudyNexusID);
  }

  @Override
  public List<ProjectExpectedStudyNexus> findAll() {

    return projectExpectedStudyNexusDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyNexus> getAllStudyNexussByStudy(Long studyId) {
    return this.projectExpectedStudyNexusDAO.getAllStudyNexussByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyNexus getProjectExpectedStudyNexusById(long projectExpectedStudyNexusID) {

    return projectExpectedStudyNexusDAO.find(projectExpectedStudyNexusID);
  }

  @Override
  public ProjectExpectedStudyNexus getStudyNexusByStudyNexusAndPhase(ProjectExpectedStudy study, Nexus nexus,
    Phase phase) {
    if (study != null && nexus != null && phase != null) {
      return this.projectExpectedStudyNexusDAO.getStudyNexusByStudyNexusAndPhase(study.getId().longValue(),
        nexus.getId().longValue(), phase.getId().longValue());
    }

    return null;
  }

  @Override
  public void replicate(ProjectExpectedStudyNexus originalProjectExpectedStudyNexus, Phase initialPhase) {
    Phase current = initialPhase;

    while (current != null && originalProjectExpectedStudyNexus != null
      && originalProjectExpectedStudyNexus.getProjectExpectedStudy() != null
      && originalProjectExpectedStudyNexus.getNexus() != null && originalProjectExpectedStudyNexus.getPhase() != null) {
      ProjectExpectedStudyNexus studyLever =
        this.getStudyNexusByStudyNexusAndPhase(originalProjectExpectedStudyNexus.getProjectExpectedStudy(),
          originalProjectExpectedStudyNexus.getNexus(), originalProjectExpectedStudyNexus.getPhase());
      if (studyLever == null) {
        studyLever = new ProjectExpectedStudyNexus();
      }

      studyLever.copyFields(originalProjectExpectedStudyNexus);
      studyLever.setPhase(current);

      studyLever = this.projectExpectedStudyNexusDAO.save(studyLever);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public ProjectExpectedStudyNexus saveProjectExpectedStudyNexus(ProjectExpectedStudyNexus projectExpectedStudyNexus) {

    return projectExpectedStudyNexusDAO.save(projectExpectedStudyNexus);
  }
}
