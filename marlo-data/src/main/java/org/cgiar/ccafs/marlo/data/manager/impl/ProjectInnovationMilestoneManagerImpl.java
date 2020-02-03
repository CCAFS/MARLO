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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationMilestoneManagerImpl implements ProjectInnovationMilestoneManager {

    private ProjectInnovationMilestoneDAO projectInnovationMilestoneDAO;
    // Managers
    private PhaseDAO phaseDAO;

    @Inject
    public ProjectInnovationMilestoneManagerImpl(ProjectInnovationMilestoneDAO projectInnovationMilestoneDAO,
	    PhaseDAO phaseDAO) {
	this.projectInnovationMilestoneDAO = projectInnovationMilestoneDAO;
	this.phaseDAO = phaseDAO;

    }

    @Override
    public void deleteProjectInnovationMilestone(long projectInnovationMilestoneId) {
	ProjectInnovationMilestone ProjectInnovationMilestone = this
		.getProjectInnovationMilestoneById(projectInnovationMilestoneId);

	// Conditions to Project Innovation Works In AR phase and Upkeep Phase
	if (ProjectInnovationMilestone.getPhase().getDescription().equals(APConstants.PLANNING)
		&& ProjectInnovationMilestone.getPhase().getNext() != null) {
	    this.deleteProjectInnovationMilestonePhase(ProjectInnovationMilestone.getPhase().getNext(),
		    ProjectInnovationMilestone.getProjectInnovation().getId(), ProjectInnovationMilestone);
	}

	if (ProjectInnovationMilestone.getPhase().getDescription().equals(APConstants.REPORTING)) {
	    if (ProjectInnovationMilestone.getPhase().getNext() != null
		    && ProjectInnovationMilestone.getPhase().getNext().getNext() != null) {
		Phase upkeepPhase = ProjectInnovationMilestone.getPhase().getNext().getNext();
		if (upkeepPhase != null) {
		    this.deleteProjectInnovationMilestonePhase(upkeepPhase,
			    ProjectInnovationMilestone.getProjectInnovation().getId(), ProjectInnovationMilestone);
		}
	    }
	}

	projectInnovationMilestoneDAO.deleteProjectInnovationMilestone(projectInnovationMilestoneId);
    }

    public void deleteProjectInnovationMilestonePhase(Phase next, long innovationID,
	    ProjectInnovationMilestone projectInnovationMilestone) {
	Phase phase = phaseDAO.find(next.getId());

	List<ProjectInnovationMilestone> projectInnovationMilestones = projectInnovationMilestoneDAO.findAll().stream()
		.filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()
			&& c.getProjectInnovation().getId().longValue() == innovationID
			&& c.getCrpMilestone().getId().equals(projectInnovationMilestone.getCrpMilestone().getId()))
		.collect(Collectors.toList());

	for (ProjectInnovationMilestone projectInnovationMilestoneDB : projectInnovationMilestones) {
	    projectInnovationMilestoneDAO.deleteProjectInnovationMilestone(projectInnovationMilestoneDB.getId());
	}

	if (phase.getNext() != null) {
	    this.deleteProjectInnovationMilestonePhase(phase.getNext(), innovationID, projectInnovationMilestone);
	}
    }

    @Override
    public boolean existProjectInnovationMilestone(long projectInnovationMilestoneID) {

	return projectInnovationMilestoneDAO.existProjectInnovationMilestone(projectInnovationMilestoneID);
    }

    @Override
    public List<ProjectInnovationMilestone> findAll() {

	return projectInnovationMilestoneDAO.findAll();

    }

    @Override
    public ProjectInnovationMilestone getProjectInnovationMilestoneById(long projectInnovationMilestoneID) {

	return projectInnovationMilestoneDAO.find(projectInnovationMilestoneID);
    }

    public void saveInnovationMilestonePhase(Phase next, long innovationid,
	    ProjectInnovationMilestone projectInnovationMilestone) {

	Phase phase = phaseDAO.find(next.getId());

	List<ProjectInnovationMilestone> projectInnovatioCenters = projectInnovationMilestoneDAO.findAll().stream()
		.filter(c -> c.getProjectInnovation().getId().longValue() == innovationid
			&& c.getPhase().getId().equals(phase.getId())
			&& c.getCrpMilestone().getId().equals(projectInnovationMilestone.getCrpMilestone().getId()))
		.collect(Collectors.toList());

	if (projectInnovatioCenters.isEmpty()) {
	    ProjectInnovationMilestone projectInnovationMilestoneAdd = new ProjectInnovationMilestone();
	    projectInnovationMilestoneAdd.setProjectInnovation(projectInnovationMilestone.getProjectInnovation());
	    projectInnovationMilestoneAdd.setPhase(phase);
	    projectInnovationMilestoneAdd.setCrpMilestone(projectInnovationMilestone.getCrpMilestone());
	    projectInnovationMilestoneAdd.setPrimary(projectInnovationMilestone.getPrimary());
	    projectInnovationMilestoneDAO.save(projectInnovationMilestoneAdd);
	}
	if (phase.getNext() != null) {
	    this.saveInnovationMilestonePhase(phase.getNext(), innovationid, projectInnovationMilestone);
	}
    }

    @Override
    public ProjectInnovationMilestone saveProjectInnovationMilestone(
	    ProjectInnovationMilestone projectInnovationMilestone) {
	ProjectInnovationMilestone innovationMilestone = projectInnovationMilestoneDAO.save(projectInnovationMilestone);
	Phase phase = phaseDAO.find(innovationMilestone.getPhase().getId());
	// Conditions to Project Innovation Works In AR phase and Upkeep Phase
	if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
	    this.saveInnovationMilestonePhase(innovationMilestone.getPhase().getNext(),
		    innovationMilestone.getProjectInnovation().getId(), projectInnovationMilestone);
	}
	if (phase.getDescription().equals(APConstants.REPORTING)) {
	    if (phase.getNext() != null && phase.getNext().getNext() != null) {
		Phase upkeepPhase = phase.getNext().getNext();
		if (upkeepPhase != null) {
		    this.saveInnovationMilestonePhase(upkeepPhase, innovationMilestone.getProjectInnovation().getId(),
			    projectInnovationMilestone);
		}
	    }
	}
	return innovationMilestone;
    }

}
