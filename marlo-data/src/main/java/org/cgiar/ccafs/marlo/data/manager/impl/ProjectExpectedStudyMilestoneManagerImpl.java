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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyMilestoneManagerImpl implements ProjectExpectedStudyMilestoneManager {

	private ProjectExpectedStudyMilestoneDAO projectExpectedStudyMilestoneDAO;
	// Managers
	private PhaseDAO phaseDAO;

	@Inject
	public ProjectExpectedStudyMilestoneManagerImpl(ProjectExpectedStudyMilestoneDAO projectExpectedStudyMilestoneDAO,
			PhaseDAO phaseDAO) {
		this.projectExpectedStudyMilestoneDAO = projectExpectedStudyMilestoneDAO;
		this.phaseDAO = phaseDAO;

	}

	@Override
	public void deleteProjectExpectedStudyMilestone(long projectExpectedStudyMilestoneId) {
		ProjectExpectedStudyMilestone ProjectExpectedStudyMilestone = this
				.getProjectExpectedStudyMilestoneById(projectExpectedStudyMilestoneId);

		// Conditions to Project Innovation Works In AR phase and Upkeep Phase
		if (ProjectExpectedStudyMilestone.getPhase().getDescription().equals(APConstants.PLANNING)
				&& ProjectExpectedStudyMilestone.getPhase().getNext() != null) {
			this.deleteProjectExpectedStudyMilestonePhase(ProjectExpectedStudyMilestone.getPhase().getNext(),
					ProjectExpectedStudyMilestone.getProjectExpectedStudy().getId(), ProjectExpectedStudyMilestone);
		}

		if (ProjectExpectedStudyMilestone.getPhase().getDescription().equals(APConstants.REPORTING)) {
			if (ProjectExpectedStudyMilestone.getPhase().getNext() != null
					&& ProjectExpectedStudyMilestone.getPhase().getNext().getNext() != null) {
				Phase upkeepPhase = ProjectExpectedStudyMilestone.getPhase().getNext().getNext();
				if (upkeepPhase != null) {
					this.deleteProjectExpectedStudyMilestonePhase(upkeepPhase,
							ProjectExpectedStudyMilestone.getProjectExpectedStudy().getId(), ProjectExpectedStudyMilestone);
				}
			}
		}

		projectExpectedStudyMilestoneDAO.deleteProjectExpectedStudyMilestone(projectExpectedStudyMilestoneId);
	}

	public void deleteProjectExpectedStudyMilestonePhase(Phase next, long innovationID,
			ProjectExpectedStudyMilestone projectExpectedStudyMilestone) {
		Phase phase = phaseDAO.find(next.getId());

		List<ProjectExpectedStudyMilestone> projectExpectedStudyMilestones = projectExpectedStudyMilestoneDAO.findAll().stream()
				.filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()
						&& c.getProjectExpectedStudy().getId().longValue() == innovationID
						&& c.getCrpMilestone().getId().equals(projectExpectedStudyMilestone.getCrpMilestone().getId()))
				.collect(Collectors.toList());

		for (ProjectExpectedStudyMilestone projectExpectedStudyMilestoneDB : projectExpectedStudyMilestones) {
			projectExpectedStudyMilestoneDAO.deleteProjectExpectedStudyMilestone(projectExpectedStudyMilestoneDB.getId());
		}

		if (phase.getNext() != null) {
			this.deleteProjectExpectedStudyMilestonePhase(phase.getNext(), innovationID, projectExpectedStudyMilestone);
		}
	}

	@Override
	public boolean existProjectExpectedStudyMilestone(long projectExpectedStudyMilestoneID) {

		return projectExpectedStudyMilestoneDAO.existProjectExpectedStudyMilestone(projectExpectedStudyMilestoneID);
	}

	@Override
	public List<ProjectExpectedStudyMilestone> findAll() {

		return projectExpectedStudyMilestoneDAO.findAll();

	}

	@Override
	public ProjectExpectedStudyMilestone getProjectExpectedStudyMilestoneById(long projectExpectedStudyMilestoneID) {

		return projectExpectedStudyMilestoneDAO.find(projectExpectedStudyMilestoneID);
	}

	public void saveInnovationMilestonePhase(Phase next, long innovationid,
			ProjectExpectedStudyMilestone projectExpectedStudyMilestone) {

		Phase phase = phaseDAO.find(next.getId());

		List<ProjectExpectedStudyMilestone> projectInnovatioCenters = projectExpectedStudyMilestoneDAO.findAll().stream()
				.filter(c -> c.getProjectExpectedStudy().getId().longValue() == innovationid
						&& c.getPhase().getId().equals(phase.getId())
						&& c.getCrpMilestone().getId().equals(projectExpectedStudyMilestone.getCrpMilestone().getId()))
				.collect(Collectors.toList());

		if (projectInnovatioCenters.isEmpty()) {
			ProjectExpectedStudyMilestone projectExpectedStudyMilestoneAdd = new ProjectExpectedStudyMilestone();
			projectExpectedStudyMilestoneAdd.setProjectExpectedStudy(projectExpectedStudyMilestone.getProjectExpectedStudy());
			projectExpectedStudyMilestoneAdd.setPhase(phase);
			projectExpectedStudyMilestoneAdd.setCrpMilestone(projectExpectedStudyMilestone.getCrpMilestone());
			projectExpectedStudyMilestoneDAO.save(projectExpectedStudyMilestoneAdd);
		}
		if (phase.getNext() != null) {
			this.saveInnovationMilestonePhase(phase.getNext(), innovationid, projectExpectedStudyMilestone);
		}
	}

	@Override
	public ProjectExpectedStudyMilestone saveProjectExpectedStudyMilestone(ProjectExpectedStudyMilestone projectExpectedStudyMilestone) {
		ProjectExpectedStudyMilestone innovationMilestone = projectExpectedStudyMilestoneDAO.save(projectExpectedStudyMilestone);
		Phase phase = phaseDAO.find(innovationMilestone.getPhase().getId());
		// Conditions to Project Innovation Works In AR phase and Upkeep Phase
		if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
			this.saveInnovationMilestonePhase(innovationMilestone.getPhase().getNext(), innovationMilestone.getProjectExpectedStudy().getId(),
					projectExpectedStudyMilestone);
		}
		if (phase.getDescription().equals(APConstants.REPORTING)) {
			if (phase.getNext() != null && phase.getNext().getNext() != null) {
				Phase upkeepPhase = phase.getNext().getNext();
				if (upkeepPhase != null) {
					this.saveInnovationMilestonePhase(upkeepPhase, innovationMilestone.getProjectExpectedStudy().getId(),
							projectExpectedStudyMilestone);
				}
			}
		}
		return innovationMilestone;
	}

}
