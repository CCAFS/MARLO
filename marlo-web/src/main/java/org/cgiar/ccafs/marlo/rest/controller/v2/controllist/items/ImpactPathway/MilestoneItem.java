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
 * ***************************************************************
 */
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
import java.util.Date;
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
 * @author Mralmanzar
 */
@Named
public class MilestoneItem<T> {

    private CrpMilestoneManager crpMilestoneManager;
    private CrpProgramManager crpProgramManager;
    private PhaseManager phaseManager;
    private MilestoneMapper milestoneMapper;
    private RestApiAuditlogManager restApiAuditlogManager;

    @Inject
    public MilestoneItem(CrpMilestoneManager crpMilestoneManager, MilestoneMapper milestoneMapper,
            CrpProgramManager crpProgramManager, PhaseManager phaseManager,
            RestApiAuditlogManager restApiAuditlogManager) {
        super();
        this.crpMilestoneManager = crpMilestoneManager;
        this.milestoneMapper = milestoneMapper;
        this.crpProgramManager = crpProgramManager;
        this.phaseManager = phaseManager;
        this.restApiAuditlogManager = restApiAuditlogManager;
    }

    /**
     * Find a milestone by smo id and year
     *
     * @param id of milestone
     * @param CGIARentityAcronym acronym of the CRP/PTF
     * @param year
     * @param user
     * @return a OutcomeDTO with the milestone data.
     */
    public ResponseEntity<MilestoneDTO> findMilestoneById(String id, String CGIARentityAcronym, Integer year, User user) {
        CrpMilestone crpMilestone = null;
        Phase phase = this.phaseManager.findAll().stream()
                .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), CGIARentityAcronym) && p.getYear() == year
                && StringUtils.equalsIgnoreCase(p.getName(), "AR"))
                .findFirst().orElse(null);

        if (phase != null) {
            crpMilestone = this.crpMilestoneManager.getCrpMilestoneByPhase(id, phase.getId());
            if (!crpMilestone.getCrpProgramOutcome().getCrpProgram().getCrp().getAcronym()
                    .equalsIgnoreCase(CGIARentityAcronym)) {
                crpMilestone = null;
            } else {
                // Log Action
                RestApiAuditlog restApiAuditLog = new RestApiAuditlog("findMilestoneById", "Searched CGIAR Entity Acronym " + CGIARentityAcronym + " ID " + id + " Year:" + year, new Date(), Long.parseLong(id), "class org.cgiar.ccafs.marlo.data.model.CrpMilestone",
                        "N/A", user.getId(), null, "", phase.getId());
                restApiAuditlogManager.logApiCall(restApiAuditLog);
            }

        }

        return Optional.ofNullable(crpMilestone).map(this.milestoneMapper::crpMilestoneToMilestoneDTO)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Find all milestones of a flagship
     *
     * @param crpProgramCode code of flagship
     * @param CGIARentityAcronym acronym of the CRP/PTF
     * @param user
     * @param repoYear year of the reporting
     * @return a OutcomeDTO with the flagship or program data.
     */
    public List<MilestoneDTO> getAllMilestones(String crpProgramCode, String CGIARentityAcronym, Integer repoYear, User user) {
        List<MilestoneDTO> milestonesDTOs = null;
        List<CrpMilestone> milestoneList = new ArrayList<CrpMilestone>();
        CrpProgram crpProgram = this.crpProgramManager.getCrpProgramBySmoCode(crpProgramCode);

        List<Phase> phases
                = this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
                && c.getYear() == repoYear && c.getName().equalsIgnoreCase("AR")).collect(Collectors.toList());
        if (crpProgram != null && phases != null && !phases.isEmpty()
                && crpProgram.getCrp().equals(phases.get(0).getCrp())) {
            List<CrpProgramOutcome> crpProgramOutcomes = crpProgram.getCrpProgramOutcomes().stream()
                    .filter(c -> c.isActive() && c.getPhase().equals(phases.get(0))).collect(Collectors.toList());

            for (CrpProgramOutcome crpProgramOutcome : crpProgramOutcomes) {
                milestoneList.addAll(crpProgramOutcome.getCrpMilestones().stream()
                        .filter(c -> c.getYear() == phases.get(0).getYear()).collect(Collectors.toList()));
            }

            milestonesDTOs
                    = milestoneList.stream().map(milestoneEntity -> this.milestoneMapper.crpMilestoneToMilestoneDTO(milestoneEntity))
                            .collect(Collectors.toList());

        }

        //Log Action
        RestApiAuditlog restApiAuditLog = new RestApiAuditlog("List milestones", "LIST milestones CGIAR Entity Acronym " + CGIARentityAcronym + " CRP Programe Code " + crpProgramCode + " Year:" + repoYear, new Date(), 0, "class org.cgiar.ccafs.marlo.data.model.CrpMilestone", "N/A", user.getId(), null, "", null);
        restApiAuditlogManager.logApiCall(restApiAuditLog);
        return milestonesDTOs;

    }

}
