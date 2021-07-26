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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLeverManager;
import org.cgiar.ccafs.marlo.data.model.AllianceLever;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyLeverManagerImpl implements ProjectExpectedStudyLeverManager {


  private ProjectExpectedStudyLeverDAO projectExpectedStudyLeverDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyLeverManagerImpl(ProjectExpectedStudyLeverDAO projectExpectedStudyLeverDAO) {
    this.projectExpectedStudyLeverDAO = projectExpectedStudyLeverDAO;


  }

  @Override
  public void deleteProjectExpectedStudyLever(long projectExpectedStudyLeverId) {

    projectExpectedStudyLeverDAO.deleteProjectExpectedStudyLever(projectExpectedStudyLeverId);
  }

  @Override
  public boolean existProjectExpectedStudyLever(long projectExpectedStudyLeverID) {

    return projectExpectedStudyLeverDAO.existProjectExpectedStudyLever(projectExpectedStudyLeverID);
  }

  @Override
  public List<ProjectExpectedStudyLever> findAll() {

    return projectExpectedStudyLeverDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyLever> getAllStudyLeversByStudy(Long studyId) {
    return this.projectExpectedStudyLeverDAO.getAllStudyLeversByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyLever getProjectExpectedStudyLeverById(long projectExpectedStudyLeverID) {

    return projectExpectedStudyLeverDAO.find(projectExpectedStudyLeverID);
  }

  @Override
  public ProjectExpectedStudyLever getStudyLeverByStudyLeverAndPhase(ProjectExpectedStudy study, AllianceLever lever,
    Phase phase) {
    if (study != null && lever != null && phase != null) {
      return this.projectExpectedStudyLeverDAO.getStudyLeverByStudyLeverAndPhase(study.getId().longValue(),
        lever.getId().longValue(), phase.getId().longValue());
    }

    return null;
  }

  @Override
  public void replicate(ProjectExpectedStudyLever originalProjectExpectedStudyLever, Phase initialPhase) {
    Phase current = initialPhase;

    while (current != null && originalProjectExpectedStudyLever != null
      && originalProjectExpectedStudyLever.getProjectExpectedStudy() != null
      && originalProjectExpectedStudyLever.getAllianceLever() != null
      && originalProjectExpectedStudyLever.getPhase() != null) {
      ProjectExpectedStudyLever studyLever =
        this.getStudyLeverByStudyLeverAndPhase(originalProjectExpectedStudyLever.getProjectExpectedStudy(),
          originalProjectExpectedStudyLever.getAllianceLever(), originalProjectExpectedStudyLever.getPhase());
      if (studyLever == null) {
        studyLever = new ProjectExpectedStudyLever();
      }

      studyLever.copyFields(originalProjectExpectedStudyLever);
      studyLever.setPhase(current);

      studyLever = this.projectExpectedStudyLeverDAO.save(studyLever);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public ProjectExpectedStudyLever saveProjectExpectedStudyLever(ProjectExpectedStudyLever projectExpectedStudyLever) {

    return projectExpectedStudyLeverDAO.save(projectExpectedStudyLever);
  }
}
