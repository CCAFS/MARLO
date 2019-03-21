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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ImpactPathway;

import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.rest.dto.MilestoneDTO;
import org.cgiar.ccafs.marlo.rest.mappers.MilestoneMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Mralmanzar
 */

@Named
public class MilestoneItem<T> {

	private CrpMilestoneManager crpMilestoneManager;
	private CrpProgramManager crpProgramManager;
	private PhaseManager phaseManager;
	private MilestoneMapper milestoneMapper;

	@Inject
	public MilestoneItem(CrpMilestoneManager crpMilestoneManager, MilestoneMapper milestoneMapper,
			CrpProgramManager crpProgramManager, PhaseManager phaseManager) {
		super();
		this.crpMilestoneManager = crpMilestoneManager;
		this.milestoneMapper = milestoneMapper;
		this.crpProgramManager = crpProgramManager;
		this.phaseManager = phaseManager;
	}

	/**
	 * Find a milestone by smo id
	 * 
	 * @param id of milestone
	 * @param CGIARentityAcronym acronym of the CRP/PTF
	 * @return a OutcomeDTO with the milestone data.
	 */

	public ResponseEntity<MilestoneDTO> findMilestoneById(Long id, String CGIARentityAcronym) {

		CrpMilestone crpMilestone = this.crpMilestoneManager.getCrpMilestoneById(id);
		if (!crpMilestone.getCrpProgramOutcome().getCrpProgram().getCrp().getAcronym()
				.equalsIgnoreCase(CGIARentityAcronym)) {
			crpMilestone = null;
		}
		return Optional.ofNullable(crpMilestone).map(this.milestoneMapper::crpMilestoneToMilestoneDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Find all milestones of a flagship
	 * 
	 * @param crpProgramCode code of flagship
	 * @param CGIARentityAcronym acronym of the CRP/PTF
	 * @param targetYear target year of milestone
	 * @param repoYear year of the reporting
	 * @return a OutcomeDTO with the flagship or program data.
	 */
	public List<MilestoneDTO> getAllMilestones(String crpProgramCode, String CGIARentityAcronym, Integer repoYear) {
		List<MilestoneDTO> milestonesDTOs = null;
		List<CrpMilestone> milestoneList = new ArrayList<CrpMilestone>();
		CrpProgram crpProgram = this.crpProgramManager.getCrpProgramBySmoCode(crpProgramCode);

		List<Phase> phases = this.phaseManager.findAll().stream()
				.filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym) && c.getYear() == repoYear
						&& c.getName().equalsIgnoreCase("AR"))
				.collect(Collectors.toList());
		if (crpProgram != null && phases != null && !phases.isEmpty()
				&& crpProgram.getCrp().equals(phases.get(0).getCrp())) {
			List<CrpProgramOutcome> crpProgramOutcomes = crpProgram.getCrpProgramOutcomes().stream()
					.filter(c -> c.isActive() && c.getPhase().equals(phases.get(0))).collect(Collectors.toList());

			for (CrpProgramOutcome crpProgramOutcome : crpProgramOutcomes) {
				milestoneList.addAll(crpProgramOutcome.getCrpMilestones().stream()
						.filter(c -> c.getYear() == phases.get(0).getYear()).collect(Collectors.toList()));
			}

			milestonesDTOs = milestoneList.stream()
					.map(milestoneEntity -> this.milestoneMapper.crpMilestoneToMilestoneDTO(milestoneEntity))
					.collect(Collectors.toList());

		}
		return milestonesDTOs;

	}

}
