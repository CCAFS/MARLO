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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySdgTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySdgTargetManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgTarget;
import org.cgiar.ccafs.marlo.data.model.SdgTargets;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudySdgTargetManagerImpl implements ProjectExpectedStudySdgTargetManager {


  private ProjectExpectedStudySdgTargetDAO projectExpectedStudySdgTargetDAO;
  // Managers


  @Inject
  public ProjectExpectedStudySdgTargetManagerImpl(ProjectExpectedStudySdgTargetDAO projectExpectedStudySdgTargetDAO) {
    this.projectExpectedStudySdgTargetDAO = projectExpectedStudySdgTargetDAO;


  }

  @Override
  public void deleteProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetId) {

    projectExpectedStudySdgTargetDAO.deleteProjectExpectedStudySdgTarget(projectExpectedStudySdgTargetId);
  }

  @Override
  public boolean existProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetID) {

    return projectExpectedStudySdgTargetDAO.existProjectExpectedStudySdgTarget(projectExpectedStudySdgTargetID);
  }

  @Override
  public List<ProjectExpectedStudySdgTarget> findAll() {

    return projectExpectedStudySdgTargetDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudySdgTarget> getAllStudySdgTargetsByStudy(Long studyId) {
    return this.projectExpectedStudySdgTargetDAO.getAllStudySdgTargetsByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudySdgTarget getProjectExpectedStudySdgTargetById(long projectExpectedStudySdgTargetID) {

    return projectExpectedStudySdgTargetDAO.find(projectExpectedStudySdgTargetID);
  }

  @Override
  public ProjectExpectedStudySdgTarget getStudySdgTargetByStudySdgTargetAndPhase(ProjectExpectedStudy study,
    SdgTargets sdgTarget, Phase phase) {
    if (study != null && sdgTarget != null && phase != null) {
      return this.projectExpectedStudySdgTargetDAO.getStudySdgTargetByStudySdgTargetAndPhase(study.getId().longValue(),
        sdgTarget.getId().longValue(), phase.getId().longValue());
    }
    return null;
  }

  @Override
  public void replicate(ProjectExpectedStudySdgTarget originalProjectExpectedStudySdgTarget, Phase initialPhase) {
    Phase current = initialPhase;

    while (current != null && originalProjectExpectedStudySdgTarget != null
      && originalProjectExpectedStudySdgTarget.getProjectExpectedStudy() != null
      && originalProjectExpectedStudySdgTarget.getSdgTarget() != null
      && originalProjectExpectedStudySdgTarget.getPhase() != null) {
      ProjectExpectedStudySdgTarget studySdgTarget =
        this.getStudySdgTargetByStudySdgTargetAndPhase(originalProjectExpectedStudySdgTarget.getProjectExpectedStudy(),
          originalProjectExpectedStudySdgTarget.getSdgTarget(), originalProjectExpectedStudySdgTarget.getPhase());
      if (studySdgTarget == null) {
        studySdgTarget = new ProjectExpectedStudySdgTarget();
      }

      studySdgTarget.copyFields(originalProjectExpectedStudySdgTarget);
      studySdgTarget.setPhase(current);

      studySdgTarget = this.projectExpectedStudySdgTargetDAO.save(studySdgTarget);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public ProjectExpectedStudySdgTarget
    saveProjectExpectedStudySdgTarget(ProjectExpectedStudySdgTarget projectExpectedStudySdgTarget) {

    return projectExpectedStudySdgTargetDAO.save(projectExpectedStudySdgTarget);
  }
}
