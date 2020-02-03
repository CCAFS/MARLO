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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationSubIdoManagerImpl implements ProjectInnovationSubIdoManager {

    private ProjectInnovationSubIdoDAO projectInnovationSubIdoDAO;
    // Managers
    private PhaseDAO phaseDAO;

    @Inject
    public ProjectInnovationSubIdoManagerImpl(ProjectInnovationSubIdoDAO projectInnovationSubIdoDAO,
	    PhaseDAO phaseDAO) {
	this.projectInnovationSubIdoDAO = projectInnovationSubIdoDAO;
	this.phaseDAO = phaseDAO;

    }

    @Override
    public void deleteProjectInnovationSubIdo(long projectInnovationSubIdoId) {
	ProjectInnovationSubIdo ProjectInnovationSubIdo = this
		.getProjectInnovationSubIdoById(projectInnovationSubIdoId);

	// Conditions to Project Innovation Works In AR phase and Upkeep Phase
	if (ProjectInnovationSubIdo.getPhase().getDescription().equals(APConstants.PLANNING)
		&& ProjectInnovationSubIdo.getPhase().getNext() != null) {
	    this.deleteProjectInnovationSubIdoPhase(ProjectInnovationSubIdo.getPhase().getNext(),
		    ProjectInnovationSubIdo.getProjectInnovation().getId(), ProjectInnovationSubIdo);
	}

	if (ProjectInnovationSubIdo.getPhase().getDescription().equals(APConstants.REPORTING)) {
	    if (ProjectInnovationSubIdo.getPhase().getNext() != null
		    && ProjectInnovationSubIdo.getPhase().getNext().getNext() != null) {
		Phase upkeepPhase = ProjectInnovationSubIdo.getPhase().getNext().getNext();
		if (upkeepPhase != null) {
		    this.deleteProjectInnovationSubIdoPhase(upkeepPhase,
			    ProjectInnovationSubIdo.getProjectInnovation().getId(), ProjectInnovationSubIdo);
		}
	    }
	}

	projectInnovationSubIdoDAO.deleteProjectInnovationSubIdo(projectInnovationSubIdoId);
    }

    public void deleteProjectInnovationSubIdoPhase(Phase next, long innovationID,
	    ProjectInnovationSubIdo projectInnovationSubIdo) {
	Phase phase = phaseDAO.find(next.getId());

	List<ProjectInnovationSubIdo> projectInnovationSubIdos = projectInnovationSubIdoDAO.findAll().stream()
		.filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()
			&& c.getProjectInnovation().getId().longValue() == innovationID
			&& c.getSrfSubIdo().getId().equals(projectInnovationSubIdo.getSrfSubIdo().getId()))
		.collect(Collectors.toList());

	for (ProjectInnovationSubIdo projectInnovationSubIdoDB : projectInnovationSubIdos) {
	    projectInnovationSubIdoDAO.deleteProjectInnovationSubIdo(projectInnovationSubIdoDB.getId());
	}

	if (phase.getNext() != null) {
	    this.deleteProjectInnovationSubIdoPhase(phase.getNext(), innovationID, projectInnovationSubIdo);
	}
    }

    @Override
    public boolean existProjectInnovationSubIdo(long projectInnovationSubIdoID) {

	return projectInnovationSubIdoDAO.existProjectInnovationSubIdo(projectInnovationSubIdoID);
    }

    @Override
    public List<ProjectInnovationSubIdo> findAll() {

	return projectInnovationSubIdoDAO.findAll();

    }

    @Override
    public ProjectInnovationSubIdo getProjectInnovationSubIdoById(long projectInnovationSubIdoID) {

	return projectInnovationSubIdoDAO.find(projectInnovationSubIdoID);
    }

    public void saveInnovationMilestonePhase(Phase next, long innovationid,
	    ProjectInnovationSubIdo projectInnovationSubIdo) {

	Phase phase = phaseDAO.find(next.getId());

	List<ProjectInnovationSubIdo> projectInnovatioCenters = projectInnovationSubIdoDAO.findAll().stream()
		.filter(c -> c.getProjectInnovation().getId().longValue() == innovationid
			&& c.getPhase().getId().equals(phase.getId())
			&& c.getSrfSubIdo().getId().equals(projectInnovationSubIdo.getSrfSubIdo().getId()))
		.collect(Collectors.toList());

	if (projectInnovatioCenters.isEmpty()) {
	    ProjectInnovationSubIdo projectInnovationSubIdoAdd = new ProjectInnovationSubIdo();
	    projectInnovationSubIdoAdd.setProjectInnovation(projectInnovationSubIdo.getProjectInnovation());
	    projectInnovationSubIdoAdd.setPhase(phase);
	    projectInnovationSubIdoAdd.setSrfSubIdo(projectInnovationSubIdo.getSrfSubIdo());
	    projectInnovationSubIdoAdd.setPrimary(projectInnovationSubIdo.getPrimary());
	    projectInnovationSubIdoDAO.save(projectInnovationSubIdoAdd);
	}
	if (phase.getNext() != null) {
	    this.saveInnovationMilestonePhase(phase.getNext(), innovationid, projectInnovationSubIdo);
	}
    }

    @Override
    public ProjectInnovationSubIdo saveProjectInnovationSubIdo(ProjectInnovationSubIdo projectInnovationSubIdo) {
	ProjectInnovationSubIdo innovationMilestone = projectInnovationSubIdoDAO.save(projectInnovationSubIdo);
	Phase phase = phaseDAO.find(innovationMilestone.getPhase().getId());
	// Conditions to Project Innovation Works In AR phase and Upkeep Phase
	if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
	    this.saveInnovationMilestonePhase(innovationMilestone.getPhase().getNext(),
		    innovationMilestone.getProjectInnovation().getId(), projectInnovationSubIdo);
	}
	if (phase.getDescription().equals(APConstants.REPORTING)) {
	    if (phase.getNext() != null && phase.getNext().getNext() != null) {
		Phase upkeepPhase = phase.getNext().getNext();
		if (upkeepPhase != null) {
		    this.saveInnovationMilestonePhase(upkeepPhase, innovationMilestone.getProjectInnovation().getId(),
			    projectInnovationSubIdo);
		}
	    }
	}
	return innovationMilestone;
    }

}
