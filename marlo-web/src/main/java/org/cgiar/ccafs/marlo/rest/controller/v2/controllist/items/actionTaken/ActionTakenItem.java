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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.actionTaken;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationActionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluationAction;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewRelevantEvaluationActionDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewRelevantEvaluationDTO;
import org.cgiar.ccafs.marlo.rest.dto.RelevantEvaluationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.RelevantEvaluationMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ActionTakenItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisMeliaManager reportSynthesisMeliaManager;
  private ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager;
  private ReportSynthesisMeliaEvaluationActionManager reportSynthesisMeliaEvaluationActionManager;
  private RelevantEvaluationMapper actionTakenMapper;

  @Inject
  public ActionTakenItem(PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisMeliaManager reportSynthesisMeliaManager,
    ReportSynthesisMeliaEvaluationManager reportSynthesisMeliaEvaluationManager,
    ReportSynthesisMeliaEvaluationActionManager reportSynthesisMeliaEvaluationActionManager,
    RelevantEvaluationMapper actionTakenMapper) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisMeliaManager = reportSynthesisMeliaManager;
    this.reportSynthesisMeliaEvaluationManager = reportSynthesisMeliaEvaluationManager;
    this.reportSynthesisMeliaEvaluationActionManager = reportSynthesisMeliaEvaluationActionManager;
    this.actionTakenMapper = actionTakenMapper;
  }

  public Long createActionTaken(NewRelevantEvaluationDTO relevantAction, String entityAcronym, User user) {
    Long relevantActionID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createActionTaken", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == relevantAction.getPhase().getYear()
        && c.getName().equalsIgnoreCase(relevantAction.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createActionTaken", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }

    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createActionTaken", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          reportSynthesis.setPhase(phase);
          reportSynthesis.setLiaisonInstitution(liaisonInstitution);
          reportSynthesis = this.reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }
        ReportSynthesisMelia reportSynthesisMelia =
          reportSynthesisMeliaManager.getReportSynthesisMeliaById(reportSynthesis.getId());
        if (reportSynthesisMelia == null) {
          reportSynthesisMelia = new ReportSynthesisMelia();
          reportSynthesisMelia.setReportSynthesis(reportSynthesis);
          reportSynthesisMelia.setSummary("");
          reportSynthesisMelia.setCreatedBy(user);
          reportSynthesisMelia = reportSynthesisMeliaManager.saveReportSynthesisMelia(reportSynthesisMelia);
        }

        ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation = new ReportSynthesisMeliaEvaluation();
        reportSynthesisMeliaEvaluation.setNameEvaluation(relevantAction.getNameEvaluation());
        reportSynthesisMeliaEvaluation.setCreatedBy(user);
        reportSynthesisMeliaEvaluation.setRecommendation(relevantAction.getRecomendation());
        reportSynthesisMeliaEvaluation.setManagementResponse(relevantAction.getResponse());
        reportSynthesisMeliaEvaluation.setStatus(relevantAction.getStatus());
        reportSynthesisMeliaEvaluation.setComments(relevantAction.getComments());
        reportSynthesisMeliaEvaluation.setReportSynthesisMelia(reportSynthesisMelia);
        reportSynthesisMeliaEvaluation =
          reportSynthesisMeliaEvaluationManager.saveReportSynthesisMeliaEvaluation(reportSynthesisMeliaEvaluation);

        if (reportSynthesisMeliaEvaluation != null) {
          relevantActionID = reportSynthesisMeliaEvaluation.getId();
          for (NewRelevantEvaluationActionDTO action : relevantAction.getActionList()) {
            ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationAction =
              new ReportSynthesisMeliaEvaluationAction();
            reportSynthesisMeliaEvaluationAction.setActions(action.getActionName());
            reportSynthesisMeliaEvaluationAction.setReportSynthesisMeliaEvaluation(reportSynthesisMeliaEvaluation);
            reportSynthesisMeliaEvaluationAction.setCreatedBy(user);
            reportSynthesisMeliaEvaluationAction.setTextWhen(action.getByWhen());
            reportSynthesisMeliaEvaluationAction.setTextWhom(action.getByWhom());
            reportSynthesisMeliaEvaluationActionManager
              .saveReportSynthesisMeliaEvaluationAction(reportSynthesisMeliaEvaluationAction);
          }
        }
      }
    }
    return relevantActionID;
  }

  public Long deleteActionTaken(Long id, String entityAcronym, int year, String stringPhase, User user) {
    Long relevantActionID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteActionTaken", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == year && c.getName().equalsIgnoreCase(stringPhase)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("deleteActionTaken", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("deleteActionTaken", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("deleteActionTaken", "ReportSynthesis", "can not find report synthesis"));
        } else {
          ReportSynthesisMelia reportSynthesisMelia =
            reportSynthesisMeliaManager.getReportSynthesisMeliaById(reportSynthesis.getId());
          if (reportSynthesisMelia == null) {
            fieldErrors.add(
              new FieldErrorDTO("deleteActionTaken", "ReportSynthesisMelia", "can not find report synthesis melia"));
          } else {
            ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation =
              reportSynthesisMeliaEvaluationManager.getReportSynthesisMeliaEvaluationById(id.longValue());
            if (reportSynthesisMeliaEvaluation == null) {
              fieldErrors
                .add(new FieldErrorDTO("deleteActionTaken", "ReportSynthesisMelia", "can not find Melia Evaluation"));
            } else {
              relevantActionID = reportSynthesisMeliaEvaluation.getId();
              // delete actions
              for (ReportSynthesisMeliaEvaluationAction evaluationAction : reportSynthesisMeliaEvaluation
                .getReportSynthesisMeliaEvaluationActions().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                reportSynthesisMeliaEvaluationActionManager
                  .deleteReportSynthesisMeliaEvaluationAction(evaluationAction.getId());
              }
              reportSynthesisMeliaEvaluationManager
                .deleteReportSynthesisMeliaEvaluation(reportSynthesisMeliaEvaluation.getId());
            }
          }
        }
      }
    }
    return relevantActionID;
  }

  public ResponseEntity<RelevantEvaluationDTO> getActionTaken(Long id, String entityAcronym, int year,
    String stringPhase, User user) {

    ResponseEntity<RelevantEvaluationDTO> relevantEvaluation = null;
    ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("updateActionTaken", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == year && c.getName().equalsIgnoreCase(stringPhase)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("updateActionTaken", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("deleteActionTaken", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("deleteActionTaken", "ReportSynthesis", "can not find report synthesis"));
        } else {
          ReportSynthesisMelia reportSynthesisMelia =
            reportSynthesisMeliaManager.getReportSynthesisMeliaById(reportSynthesis.getId());
          if (reportSynthesisMelia == null) {
            fieldErrors.add(
              new FieldErrorDTO("deleteActionTaken", "ReportSynthesisMelia", "can not find report synthesis melia"));
          } else {
            reportSynthesisMeliaEvaluation =
              reportSynthesisMeliaEvaluationManager.getReportSynthesisMeliaEvaluationById(id.longValue());
            if (reportSynthesisMeliaEvaluation == null) {
              fieldErrors
                .add(new FieldErrorDTO("deleteActionTaken", "ReportSynthesisMelia", "can not find Melia Evaluation"));
            } else {
              List<ReportSynthesisMeliaEvaluationAction> evaluationActionList =
                reportSynthesisMeliaEvaluation.getReportSynthesisMeliaEvaluationActions().stream()
                  .filter(c -> c.isActive()).collect(Collectors.toList());
              reportSynthesisMeliaEvaluation.setMeliaEvaluationActions(evaluationActionList);
            }
          }
        }
      }
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    relevantEvaluation = Optional.ofNullable(reportSynthesisMeliaEvaluation)
      .map(this.actionTakenMapper::reportSynthesisMeliaEvaluationToRelevantEvaluationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    return relevantEvaluation;
  }

  public Long updateActionTaken(Long id, NewRelevantEvaluationDTO relevantAction, String entityAcronym, User user) {
    Long relevantActionID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("updateActionTaken", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == relevantAction.getPhase().getYear()
        && c.getName().equalsIgnoreCase(relevantAction.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("updateActionTaken", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("updateActionTaken", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("updateActionTaken", "ReportSynthesis", "can not find report synthesis"));
        } else {
          ReportSynthesisMelia reportSynthesisMelia =
            reportSynthesisMeliaManager.getReportSynthesisMeliaById(reportSynthesis.getId());
          if (reportSynthesisMelia == null) {
            fieldErrors.add(
              new FieldErrorDTO("updateActionTaken", "ReportSynthesisMelia", "can not find report synthesis melia"));
          } else {
            ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation =
              reportSynthesisMeliaEvaluationManager.getReportSynthesisMeliaEvaluationById(id.longValue());
            if (reportSynthesisMeliaEvaluation == null) {
              fieldErrors
                .add(new FieldErrorDTO("updateActionTaken", "ReportSynthesisMelia", "can not find Melia Evaluation"));
            } else {
              relevantActionID = reportSynthesisMeliaEvaluation.getId();
              reportSynthesisMeliaEvaluation.setNameEvaluation(relevantAction.getNameEvaluation());
              reportSynthesisMeliaEvaluation.setRecommendation(relevantAction.getRecomendation());
              reportSynthesisMeliaEvaluation.setStatus(relevantAction.getStatus());
              reportSynthesisMeliaEvaluation.setManagementResponse(relevantAction.getResponse());
              reportSynthesisMeliaEvaluation.setModifiedBy(user);
              reportSynthesisMeliaEvaluation = reportSynthesisMeliaEvaluationManager
                .saveReportSynthesisMeliaEvaluation(reportSynthesisMeliaEvaluation);
              // update actions or create new ones
              for (NewRelevantEvaluationActionDTO actions : relevantAction.getActionList()) {
                boolean find = false;
                ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationActionTemp = null;
                for (ReportSynthesisMeliaEvaluationAction evaluationAction : reportSynthesisMeliaEvaluation
                  .getReportSynthesisMeliaEvaluationActions().stream().filter(c -> c.isActive())
                  .collect(Collectors.toList())) {
                  if (evaluationAction.getActions().toUpperCase().equals(actions.getActionName().toUpperCase())) {
                    find = true;
                    reportSynthesisMeliaEvaluationActionTemp = evaluationAction;
                    reportSynthesisMeliaEvaluationActionTemp.setModifiedBy(user);
                    reportSynthesisMeliaEvaluationActionTemp.setTextWhen(actions.getByWhen());
                    reportSynthesisMeliaEvaluationActionTemp.setTextWhom(actions.getByWhom());
                    reportSynthesisMeliaEvaluationActionManager
                      .saveReportSynthesisMeliaEvaluationAction(reportSynthesisMeliaEvaluationActionTemp);
                  }
                }
                if (!find) {
                  ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationAction =
                    new ReportSynthesisMeliaEvaluationAction();
                  reportSynthesisMeliaEvaluationAction.setActions(actions.getActionName());
                  reportSynthesisMeliaEvaluationAction.setCreatedBy(user);
                  reportSynthesisMeliaEvaluationAction
                    .setReportSynthesisMeliaEvaluation(reportSynthesisMeliaEvaluation);
                  reportSynthesisMeliaEvaluationAction.setTextWhen(actions.getByWhen());
                  reportSynthesisMeliaEvaluationAction.setTextWhom(actions.getByWhom());
                  reportSynthesisMeliaEvaluationActionManager
                    .saveReportSynthesisMeliaEvaluationAction(reportSynthesisMeliaEvaluationAction);
                }
              }
              // delete actions
              List<ReportSynthesisMeliaEvaluationAction> deleteEvaluationAction =
                new ArrayList<ReportSynthesisMeliaEvaluationAction>();
              for (ReportSynthesisMeliaEvaluationAction evaluationAction : reportSynthesisMeliaEvaluation
                .getReportSynthesisMeliaEvaluationActions().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                boolean find = false;
                for (NewRelevantEvaluationActionDTO actions : relevantAction.getActionList()) {
                  if (evaluationAction.getActions().toUpperCase().equals(actions.getActionName().toUpperCase())) {
                    find = true;
                  }
                }
                if (!find) {
                  deleteEvaluationAction.add(evaluationAction);
                }
              }
              for (ReportSynthesisMeliaEvaluationAction deleteAction : deleteEvaluationAction) {
                reportSynthesisMeliaEvaluationActionManager
                  .deleteReportSynthesisMeliaEvaluationAction(deleteAction.getId());
              }
            }
          }
        }
      }
    }
    return relevantActionID;
  }


}
