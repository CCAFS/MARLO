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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSdgTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSdgTargetManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSdgTarget;
import org.cgiar.ccafs.marlo.data.model.SdgTargets;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationSdgTargetManagerImpl implements ProjectInnovationSdgTargetManager {


  private ProjectInnovationSdgTargetDAO projectInnovationSdgTargetDAO;
  // Managers


  @Inject
  public ProjectInnovationSdgTargetManagerImpl(ProjectInnovationSdgTargetDAO projectInnovationSdgTargetDAO) {
    this.projectInnovationSdgTargetDAO = projectInnovationSdgTargetDAO;


  }

  @Override
  public void deleteProjectInnovationSdgTarget(long projectInnovationSdgTargetId) {

    projectInnovationSdgTargetDAO.deleteProjectInnovationSdgTarget(projectInnovationSdgTargetId);
  }

  @Override
  public boolean existProjectInnovationSdgTarget(long projectInnovationSdgTargetID) {

    return projectInnovationSdgTargetDAO.existProjectInnovationSdgTarget(projectInnovationSdgTargetID);
  }

  @Override
  public List<ProjectInnovationSdgTarget> findAll() {

    return projectInnovationSdgTargetDAO.findAll();

  }

  @Override
  public List<ProjectInnovationSdgTarget> getAllInnovationSdgTargetsByInnovation(Long innovationId) {
    return this.projectInnovationSdgTargetDAO.getAllInnovationSdgTargetsByInnovation(innovationId.longValue());
  }

  @Override
  public ProjectInnovationSdgTarget getInnovationSdgTargetByInnovationSdgTargetAndPhase(ProjectInnovation innovation,
    SdgTargets sdgTarget, Phase phase) {
    if (innovation != null && sdgTarget != null && phase != null) {
      return this.projectInnovationSdgTargetDAO.getInnovationSdgTargetByInnovationSdgTargetAndPhase(
        innovation.getId().longValue(), sdgTarget.getId().longValue(), phase.getId().longValue());
    }
    return null;
  }

  @Override
  public ProjectInnovationSdgTarget getProjectInnovationSdgTargetById(long projectInnovationSdgTargetID) {

    return projectInnovationSdgTargetDAO.find(projectInnovationSdgTargetID);
  }

  @Override
  public void replicate(ProjectInnovationSdgTarget originalProjectInnovationSdgTarget, Phase initialPhase) {
    Phase current = initialPhase;

    while (current != null && originalProjectInnovationSdgTarget != null
      && originalProjectInnovationSdgTarget.getProjectInnovation() != null
      && originalProjectInnovationSdgTarget.getSdgTarget() != null
      && originalProjectInnovationSdgTarget.getPhase() != null) {
      ProjectInnovationSdgTarget innovationSdgTarget = this.getInnovationSdgTargetByInnovationSdgTargetAndPhase(
        originalProjectInnovationSdgTarget.getProjectInnovation(), originalProjectInnovationSdgTarget.getSdgTarget(),
        current);
      if (innovationSdgTarget == null) {
        innovationSdgTarget = new ProjectInnovationSdgTarget();
      }

      innovationSdgTarget.copyFields(originalProjectInnovationSdgTarget);
      innovationSdgTarget.setPhase(current);

      innovationSdgTarget = this.projectInnovationSdgTargetDAO.save(innovationSdgTarget);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public ProjectInnovationSdgTarget
    saveProjectInnovationSdgTarget(ProjectInnovationSdgTarget projectInnovationSdgTarget) {

    return projectInnovationSdgTargetDAO.save(projectInnovationSdgTarget);
  }
}
