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

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.rest.dto.OutcomeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.OutcomeMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Named
public class OutcomeItem<T> {

	private CrpProgramOutcomeManager crpProgramOutcomeManager;
	private CrpProgramManager crpProgramManager;
	private OutcomeMapper outcomeMapper;
	private PhaseManager phaseManager;

	@Inject
	public OutcomeItem(CrpProgramOutcomeManager crpProgramOutcomeManager, OutcomeMapper outcomeMapper,
			CrpProgramManager crpProgramManager, PhaseManager phaseManager) {
		super();
		this.crpProgramOutcomeManager = crpProgramOutcomeManager;
		this.outcomeMapper = outcomeMapper;
		this.crpProgramManager = crpProgramManager;
		this.phaseManager = phaseManager;
	}

	/**
	 * Find a Flagship or program requesting by smo Code
	 * 
	 * @param smo code
	 * @return a FlagshipProgramDTO with the flagship or program data.
	 */

	public ResponseEntity<OutcomeDTO> findOutcomeById(Long id, String CGIARentityAcronym) {

		CrpProgramOutcome crpProgramOutcome = this.crpProgramOutcomeManager.getCrpProgramOutcomeById(id);
		if (!crpProgramOutcome.getCrpProgram().getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)) {
			crpProgramOutcome = null;
		}
		return Optional.ofNullable(crpProgramOutcome).map(this.outcomeMapper::crpProgramOutcomeToOutcomeDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Find a Flagship or program requesting by id
	 * 
	 * @param id
	 * @return a FlagshipProgramDTO with the flagship or program data.
	 */
	public List<OutcomeDTO> getAllOutcomes(String crpProgramCode, String CGIARentityAcronym, Integer targetYear,
			Integer repoYear) {
		List<OutcomeDTO> outcomeDTOs = null;
		CrpProgram crpProgram = this.crpProgramManager.getCrpProgramBySmoCode(crpProgramCode);
		List<Phase> phases = this.phaseManager.findAll().stream()
				.filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym) && c.getYear() == repoYear
						&& c.getName().equalsIgnoreCase("AR"))
				.collect(Collectors.toList());

		if (crpProgram != null && phases != null && !phases.isEmpty()
				&& crpProgram.getCrp().equals(phases.get(0).getCrp())) {

			List<CrpProgramOutcome> crpProgramOutcomes = crpProgram.getCrpProgramOutcomes().stream()
					.filter(c -> c.isActive() && c.getPhase().equals(phases.get(0))).collect(Collectors.toList());
			if (targetYear != null) {
				outcomeDTOs = crpProgramOutcomes.stream().filter(c -> c.getYear().equals(targetYear))
						.map(outcomeEntity -> this.outcomeMapper.crpProgramOutcomeToOutcomeDTO(outcomeEntity))
						.collect(Collectors.toList());
			} else {
				outcomeDTOs = crpProgramOutcomes.stream()
						.map(outcomeEntity -> this.outcomeMapper.crpProgramOutcomeToOutcomeDTO(outcomeEntity))
						.collect(Collectors.toList());
			}

		}
		return outcomeDTOs;
//		Optional.ofNullable(outcomeDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

	/**
	 * Get All the Flagship or program Items *
	 * 
	 * @return a List of FlagshipProgramDTO with all the Flagship or program
	 * Items.
	 */
	/*
	 * public List<FlagshipProgramDTO> getAllCrpPrograms() { if
	 * (this.crpProgramManager.findAll() != null) { List<CrpProgram> crpPrograms
	 * = new ArrayList<>(this.crpProgramManager.findAll());
	 * List<FlagshipProgramDTO> flagshipProgramDTOs = crpPrograms.stream()
	 * .filter(c -> c.getProgramType() == 1 &&
	 * c.getCrp().getGlobalUnitType().getId() <= 3)
	 * .sorted(Comparator.comparing(CrpProgram::getSmoCode,
	 * Comparator.nullsLast(Comparator.naturalOrder()))) .map(crpProgramsEntity
	 * ->
	 * this.flagshipProgramMapper.crpProgramToCrpProgramDTO(crpProgramsEntity))
	 * .collect(Collectors.toList()); return flagshipProgramDTOs; } else {
	 * return null; } }
	 */

}
