/** ***************************************************************
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
 ****************************************************************
 */
package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ImpactPathway;

import static com.mchange.v2.c3p0.impl.C3P0Defaults.user;
import java.util.Date;
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

import org.apache.commons.lang3.StringUtils;
import org.cgiar.ccafs.marlo.data.manager.RestApiAuditlogManager;
import org.cgiar.ccafs.marlo.data.model.RestApiAuditlog;
import org.cgiar.ccafs.marlo.data.model.User;
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
    private RestApiAuditlogManager restApiAuditlogManager;

    @Inject
    public OutcomeItem(CrpProgramOutcomeManager crpProgramOutcomeManager, OutcomeMapper outcomeMapper,
            CrpProgramManager crpProgramManager, PhaseManager phaseManager,
            RestApiAuditlogManager restApiAuditlogManager) {
        super();
        this.crpProgramOutcomeManager = crpProgramOutcomeManager;
        this.outcomeMapper = outcomeMapper;
        this.crpProgramManager = crpProgramManager;
        this.phaseManager = phaseManager;
        this.restApiAuditlogManager = restApiAuditlogManager;
    }

    /**
     *
     * @param id
     * @param CGIARentityAcronym
     * @param year
     * @param user
     * @return a FlagshipProgramDTO with the flagship or program data.
     */
    public ResponseEntity<OutcomeDTO> findOutcomeById(String id, String CGIARentityAcronym, Integer year, User user) {
        CrpProgramOutcome crpProgramOutcome = null;
        Phase phase = this.phaseManager.findAll().stream()
                .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), CGIARentityAcronym) && p.getYear() == year
                && StringUtils.equalsIgnoreCase(p.getName(), "AR"))
                .findFirst().orElse(null);

        if (phase != null) {
            crpProgramOutcome = this.crpProgramOutcomeManager.getCrpProgramOutcome(id, phase);
            if (!crpProgramOutcome.getCrpProgram().getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)) {
                crpProgramOutcome = null;
            } else {
                // Log Action
                RestApiAuditlog restApiAuditLog = new RestApiAuditlog("findOutcomeById", "Searched CGIAR Entity Acronym " + CGIARentityAcronym + " ID " + id + " Year:" + year + " Phase: " + phase.getId(), new Date(), Long.parseLong(id), "class org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome",
                        "N/A", user.getId(), null, "", phase.getId());
                restApiAuditlogManager.logApiCall(restApiAuditLog);
            }

        }

        return Optional.ofNullable(crpProgramOutcome).map(this.outcomeMapper::crpProgramOutcomeToOutcomeDTO)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Find a Flagship or program requesting by id
     *
     * @param crpProgramCode
     * @param CGIARentityAcronym
     * @param targetYear
     * @param repoYear
     * @param user
     * @return a FlagshipProgramDTO with the flagship or program data.
     */
    public List<OutcomeDTO> getAllOutcomes(String crpProgramCode, String CGIARentityAcronym, Integer targetYear,
            Integer repoYear, User user) {
        List<OutcomeDTO> outcomeDTOs = null;
        CrpProgram crpProgram = this.crpProgramManager.getCrpProgramBySmoCode(crpProgramCode);
        List<Phase> phases
                = this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
                && c.getYear() == repoYear && c.getName().equalsIgnoreCase("AR")).collect(Collectors.toList());

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
        
        //Log Action
        RestApiAuditlog restApiAuditLog = new RestApiAuditlog("List Outcomes", "LIST outcomes CGIAR Entity Acronym " + CGIARentityAcronym + " CRP Programe Code " + crpProgramCode + " Year:" + repoYear, new Date(), 0, "class org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome", "N/A", user.getId(), null, "", null);
        restApiAuditlogManager.logApiCall(restApiAuditLog);

        return outcomeDTOs;
        // Optional.ofNullable(outcomeDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new
        // ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

}
