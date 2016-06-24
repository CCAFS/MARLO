/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.impactPathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpAssumptionManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpOutcomeSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.impactPathway.OutcomeValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class OutcomesAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private CrpMilestoneManager crpMilestoneManager;
  private long crpProgramID;
  private CrpProgramManager crpProgramManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private HashMap<Long, String> idoList;
  private Crp loggedCrp;
  private List<CrpProgramOutcome> outcomes;
  private List<CrpProgram> programs;
  private CrpProgram selectedProgram;
  private SrfIdoManager srfIdoManager;
  private CrpOutcomeSubIdoManager crpOutcomeSubIdoManager;
  private SrfTargetUnitManager srfTargetUnitManager;
  private HashMap<Long, String> targetUnitList;
  private OutcomeValidator validator;
  private CrpAssumptionManager crpAssumptionManager;
  private CrpManager crpManager;

  @Inject
  public OutcomesAction(APConfig config, SrfTargetUnitManager srfTargetUnitManager, SrfIdoManager srfIdoManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, CrpMilestoneManager crpMilestoneManager,
    CrpProgramManager crpProgramManager, OutcomeValidator validator, CrpOutcomeSubIdoManager crpOutcomeSubIdoManager,
    CrpAssumptionManager crpAssumptionManager, CrpManager crpManager) {
    super(config);
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.srfIdoManager = srfIdoManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.crpProgramManager = crpProgramManager;
    this.validator = validator;
    this.crpOutcomeSubIdoManager = crpOutcomeSubIdoManager;
    this.crpManager = crpManager;
    this.crpAssumptionManager = crpAssumptionManager;
  }

  public long getCrpProgramID() {
    return crpProgramID;
  }


  public HashMap<Long, String> getIdoList() {
    return idoList;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }


  public List<CrpProgram> getPrograms() {
    return programs;
  }

  public CrpProgram getSelectedProgram() {
    return selectedProgram;
  }


  public HashMap<Long, String> getTargetUnitList() {
    return targetUnitList;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    outcomes = new ArrayList<CrpProgramOutcome>();
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    List<CrpProgram> allPrograms = loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
      .collect(Collectors.toList());
    crpProgramID = -1;
    if (allPrograms != null) {

      this.programs = allPrograms;

      try {
        crpProgramID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
      } catch (Exception e) {
        if (!this.programs.isEmpty()) {
          crpProgramID = this.programs.get(0).getId();
        }
      }
    } else {
      programs = new ArrayList<>();
    }

    if (crpProgramID != -1) {
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);
      outcomes.addAll(
        selectedProgram.getCrpProgramOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

    }

    for (CrpProgramOutcome crpProgramOutcome : outcomes) {
      crpProgramOutcome.setMilestones(
        crpProgramOutcome.getCrpMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      crpProgramOutcome.setSubIdos(
        crpProgramOutcome.getCrpOutcomeSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


      for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getSubIdos()) {
        List<CrpAssumption> assumptions =
          crpOutcomeSubIdo.getCrpAssumptions().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        crpOutcomeSubIdo.setAssumptions(assumptions);
        HashMap<Long, String> mapSubidos = new HashMap<>();
        try {
          for (SrfSubIdo srfSubIdo : crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getSrfSubIdos().stream()
            .filter(c -> c.isActive()).collect(Collectors.toList())) {
            mapSubidos.put(srfSubIdo.getId(), srfSubIdo.getDescription());

          }
        } catch (Exception e) {

        }
        crpOutcomeSubIdo.setSubIdoList(mapSubidos);
      }

    }
    targetUnitList = new HashMap<>();
    if (srfTargetUnitManager.findAll() != null) {
      List<SrfTargetUnit> targetUnits =
        srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (SrfTargetUnit srfTargetUnit : targetUnits) {
        targetUnitList.put(srfTargetUnit.getId(), srfTargetUnit.getAcronym());
      }
    }

    idoList = new HashMap<>();
    for (SrfIdo srfIdo : srfIdoManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
      idoList.put(srfIdo.getId(), srfIdo.getDescription());
    }
    String params[] = {loggedCrp.getAcronym()};
    this.setBasePermission(this.getText(Permission.IMPACT_PATHWAY_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      outcomes.clear();
    }

  }


  @Override
  public String save() {
    if (this.hasPermission("*")) {
      /*
       * Removing outcomes
       */
      selectedProgram = crpProgramManager.getCrpProgramById(crpProgramID);

      /*
       * Save outcomes
       */
      for (CrpProgramOutcome crpProgramOutcome : outcomes) {

        if (crpProgramOutcome.getId() == null) {
          crpProgramOutcome.setActive(true);

          crpProgramOutcome.setCreatedBy(this.getCurrentUser());
          crpProgramOutcome.setModifiedBy(this.getCurrentUser());
          crpProgramOutcome.setModificationJustification("");
          crpProgramOutcome.setActiveSince(new Date());

        } else {
          CrpProgramOutcome db = crpProgramOutcomeManager.getCrpProgramOutcomeById(crpProgramOutcome.getId());
          crpProgramOutcome.setActive(true);
          crpProgramOutcome.setCreatedBy(db.getCreatedBy());
          crpProgramOutcome.setModifiedBy(this.getCurrentUser());
          crpProgramOutcome.setModificationJustification("");
          crpProgramOutcome.setActiveSince(db.getActiveSince());
        }
        crpProgramOutcome.setCrpProgram(selectedProgram);
        crpProgramOutcomeManager.saveCrpProgramOutcome(crpProgramOutcome);

        /*
         * Delete Milestones
         */
        CrpProgramOutcome crpProgramOutcomeBD =
          crpProgramOutcomeManager.getCrpProgramOutcomeById(crpProgramOutcome.getId());
        for (CrpMilestone crpMilestone : crpProgramOutcomeBD.getCrpMilestones().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          if (crpProgramOutcome.getMilestones() != null) {
            if (!crpProgramOutcome.getMilestones().contains(crpMilestone)) {
              crpMilestoneManager.deleteCrpMilestone(crpMilestone.getId());
            }
          } else {
            crpMilestoneManager.deleteCrpMilestone(crpMilestone.getId());
          }


        }
        /*
         * Save Milestones
         */

        if (crpProgramOutcome.getMilestones() != null) {
          for (CrpMilestone crpMilestone : crpProgramOutcome.getMilestones()) {
            if (crpMilestone.getId() == null) {
              crpMilestone.setActive(true);

              crpMilestone.setCreatedBy(this.getCurrentUser());
              crpMilestone.setModifiedBy(this.getCurrentUser());
              crpMilestone.setModificationJustification("");
              crpMilestone.setActiveSince(new Date());

            } else {
              CrpMilestone db = crpMilestoneManager.getCrpMilestoneById(crpMilestone.getId());
              crpMilestone.setActive(true);
              crpMilestone.setCreatedBy(db.getCreatedBy());
              crpMilestone.setModifiedBy(this.getCurrentUser());
              crpMilestone.setModificationJustification("");
              crpMilestone.setActiveSince(db.getActiveSince());
            }
            crpMilestone.setCrpProgramOutcome(crpProgramOutcome);
            crpMilestoneManager.saveCrpMilestone(crpMilestone);
          }
        }


        /*
         * Delete SubIDOS
         */

        for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcomeBD.getCrpOutcomeSubIdos().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          if (crpProgramOutcome.getSubIdos() != null) {
            if (!crpProgramOutcome.getSubIdos().contains(crpOutcomeSubIdo)) {

              crpOutcomeSubIdoManager.deleteCrpOutcomeSubIdo(crpOutcomeSubIdo.getId());
              for (CrpAssumption assumption : crpOutcomeSubIdo.getCrpAssumptions()) {
                crpAssumptionManager.deleteCrpAssumption(assumption.getId());
              }
            }
          } else {
            if (crpOutcomeSubIdo.getCrpAssumptions().isEmpty()) {
              crpOutcomeSubIdoManager.deleteCrpOutcomeSubIdo(crpOutcomeSubIdo.getId());
              for (CrpAssumption assumption : crpOutcomeSubIdo.getCrpAssumptions()) {
                crpAssumptionManager.deleteCrpAssumption(assumption.getId());
              }
            }
          }


        }

        /*
         * Save CrpOutcomeSubIdo
         */

        if (crpProgramOutcome.getSubIdos() != null) {
          for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getSubIdos()) {
            if (crpOutcomeSubIdo.getId() == null) {
              crpOutcomeSubIdo.setActive(true);

              crpOutcomeSubIdo.setCreatedBy(this.getCurrentUser());
              crpOutcomeSubIdo.setModifiedBy(this.getCurrentUser());
              crpOutcomeSubIdo.setModificationJustification("");
              crpOutcomeSubIdo.setActiveSince(new Date());

            } else {
              CrpOutcomeSubIdo db = crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(crpOutcomeSubIdo.getId());
              crpOutcomeSubIdo.setActive(true);
              crpOutcomeSubIdo.setCreatedBy(db.getCreatedBy());
              crpOutcomeSubIdo.setModifiedBy(this.getCurrentUser());
              crpOutcomeSubIdo.setModificationJustification("");
              crpOutcomeSubIdo.setActiveSince(db.getActiveSince());
            }
            crpOutcomeSubIdo.setCrpProgramOutcome(crpProgramOutcome);
            if (crpOutcomeSubIdo.getSrfSubIdo() == null || crpOutcomeSubIdo.getSrfSubIdo().getId() == null
              || crpOutcomeSubIdo.getSrfSubIdo().getId() == -1 || crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo() == null
              || crpOutcomeSubIdo.getSrfSubIdo().getSrfIdo().getId() == -1) {
              crpOutcomeSubIdo.setSrfSubIdo(null);
            }
            crpOutcomeSubIdoManager.saveCrpOutcomeSubIdo(crpOutcomeSubIdo);

            /*
             * Delete assupmtions
             */
            CrpOutcomeSubIdo crpOutcomeSubIdoBD =
              crpOutcomeSubIdoManager.getCrpOutcomeSubIdoById(crpOutcomeSubIdo.getId());
            for (CrpAssumption crpAssumption : crpOutcomeSubIdoBD.getCrpAssumptions().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
              if (crpOutcomeSubIdo.getAssumptions() != null) {
                if (!crpOutcomeSubIdo.getAssumptions().contains(crpAssumption)) {
                  crpAssumptionManager.deleteCrpAssumption(crpAssumption.getId());
                }
              } else {
                crpAssumptionManager.deleteCrpAssumption(crpAssumption.getId());
              }


            }


            if (crpOutcomeSubIdo.getAssumptions() != null) {
              for (CrpAssumption crpAssumption : crpOutcomeSubIdo.getAssumptions()) {
                if (crpAssumption.getId() == null) {
                  crpAssumption.setActive(true);

                  crpAssumption.setCreatedBy(this.getCurrentUser());
                  crpAssumption.setModifiedBy(this.getCurrentUser());
                  crpAssumption.setModificationJustification("");
                  crpAssumption.setActiveSince(new Date());

                } else {
                  CrpAssumption db = crpAssumptionManager.getCrpAssumptionById(crpAssumption.getId());
                  crpAssumption.setActive(true);
                  crpAssumption.setCreatedBy(db.getCreatedBy());
                  crpAssumption.setModifiedBy(this.getCurrentUser());
                  crpAssumption.setModificationJustification("");

                  crpAssumption.setActiveSince(db.getActiveSince());
                }
                crpAssumption.setCrpOutcomeSubIdo(crpOutcomeSubIdo);
                if (crpAssumption.getDescription() == null) {
                  crpAssumption.setDescription(new String());
                }
                crpAssumptionManager.saveCrpAssumption(crpAssumption);
              }
            }


          }
        }


      }
      for (CrpProgramOutcome crpProgramOutcome : selectedProgram.getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList())) {
        if (!outcomes.contains(crpProgramOutcome)) {
          // if (crpProgramOutcome.getCrpMilestones().isEmpty() && crpProgramOutcome.getCrpOutcomeSubIdos().isEmpty()) {
          crpProgramOutcomeManager.deleteCrpProgramOutcome(crpProgramOutcome.getId());

          for (CrpMilestone crpMilestone : crpProgramOutcome.getCrpMilestones()) {
            crpMilestoneManager.deleteCrpMilestone(crpMilestone.getId());

          }
          for (CrpOutcomeSubIdo crpOutcomeSubIdo : crpProgramOutcome.getCrpOutcomeSubIdos()) {
            crpOutcomeSubIdoManager.deleteCrpOutcomeSubIdo(crpOutcomeSubIdo.getId());
            for (CrpAssumption assumption : crpOutcomeSubIdo.getCrpAssumptions()) {
              crpAssumptionManager.deleteCrpAssumption(assumption.getId());
            }
          }
        }

      }
      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      messages = this.getActionMessages();
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }


  }


  public void setCrpProgramID(long crpProgramID) {
    this.crpProgramID = crpProgramID;
  }


  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
  }


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setOutcomes(List<CrpProgramOutcome> outcomes) {
    this.outcomes = outcomes;
  }

  public void setPrograms(List<CrpProgram> programs) {
    this.programs = programs;
  }

  public void setSelectedProgram(CrpProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }


  public void setTargetUnitList(HashMap<Long, String> targetUnitList) {
    this.targetUnitList = targetUnitList;
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, outcomes);
    }
  }

}