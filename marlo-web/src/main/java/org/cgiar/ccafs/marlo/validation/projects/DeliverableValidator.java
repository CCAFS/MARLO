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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndTypeActivityManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicScope;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeActivity;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class DeliverableValidator extends BaseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(DeliverableValidator.class);


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private ProjectManager projectManager;
  private ProjectPartnerPersonManager projectPartnerPersonManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndTypeActivityManager repIndTypeActivityManager;

  @Inject
  public DeliverableValidator(GlobalUnitManager crpManager, ProjectManager projectManager,
    ProjectPartnerPersonManager projectPartnerPersonManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndTypeActivityManager repIndTypeActivityManager) {
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndTypeActivityManager = repIndTypeActivityManager;
  }

  private Path getAutoSaveFilePath(Deliverable deliverable, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.DELIVERABLE.getStatus().replace("/", "_");
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Deliverable deliverable, boolean saving) {
    action.setInvalidFields(new HashMap<>());

    boolean validate = false;


    if (action.isPlanningActive()) {
      if (deliverable.getDeliverableInfo().getStatus() != null
        && deliverable.getDeliverableInfo().getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
        validate = deliverable.getDeliverableInfo(action.getActualPhase()).getYear() >= action.getCurrentCycleYear();
      }
      if (deliverable.getDeliverableInfo().getStatus() != null
        && deliverable.getDeliverableInfo().getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
        if (deliverable.getDeliverableInfo().getNewExpectedYear() != null) {
          if (deliverable.getDeliverableInfo().getNewExpectedYear() >= action.getActualPhase().getYear()) {
            validate = true;
          }
        }

      }

    } else {
      validate = deliverable.getDeliverableInfo().isRequiredToComplete()
        || deliverable.getDeliverableInfo().isStatusCompleteInNextPhases();
    }
    if (validate) {
      Project project = projectManager.getProjectById(deliverable.getProject().getId());

      if (!saving) {
        Path path = this.getAutoSaveFilePath(deliverable, action.getCrpID(), action);

        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }
      if (!(deliverable.getDeliverableInfo(action.getActualPhase()).getStatus() != null
        && deliverable.getDeliverableInfo(action.getActualPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Cancelled.getStatusId()))) {

        this.validateDeliverableInfo(deliverable.getDeliverableInfo(), deliverable, project, action);

        Boolean isManagingPartnerPersonRequerid =
          action.hasSpecificities(APConstants.CRP_MANAGING_PARTNERS_CONTACT_PERSONS);


        // Deliverable Others
        if (deliverable.getOtherPartnerships() != null) {

          for (int i = 0; i < deliverable.getOtherPartnerships().size(); i++) {
            if (deliverable.getOtherPartnerships().get(i).getInstitution() == null
              || deliverable.getOtherPartnerships().get(i).getInstitution().getId() == null) {

              action.addMessage("Other Partnership Institution");
              action.getInvalidFields().put("input-deliverable.otherPartnerships[" + i + "].institution.id",
                InvalidFieldsMessages.EMPTYFIELD);

            } else {
              if (isManagingPartnerPersonRequerid) {
                if (deliverable.getOtherPartnerships().get(i).getPartnershipPersons() == null) {
                  action.addMessage("Other Partnership Persons");
                  action.getInvalidFields().put("input-deliverable.otherPartnerships[" + i + "].partnershipPersons",
                    InvalidFieldsMessages.EMPTYFIELD);
                } else {
                  boolean haveUser = false;
                  for (int j = 0; j < deliverable.getOtherPartnerships().get(i).getPartnershipPersons().size(); j++) {
                    if (deliverable.getOtherPartnerships().get(i).getPartnershipPersons().get(j).getUser() != null
                      && deliverable.getOtherPartnerships().get(i).getPartnershipPersons().get(j).getUser()
                        .getId() != null) {
                      haveUser = true;
                      break;
                    }
                  }
                  if (!haveUser) {
                    action.addMessage("Other Partnership Persons");
                    action.getInvalidFields().put("input-deliverable.otherPartnerships[" + i + "].partnershipPersons",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }
                }
              }
            }
          }

        }

        // Deliverable responsible
        if (deliverable.getResponsiblePartnership() == null) {
          action.addMessage(action.getText("deliverable others"));
          action.getInvalidFields().put("list-deliverable.otherPartnerships",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"deliverable others"}));
        } else {

          for (int i = 0; i < deliverable.getResponsiblePartnership().size(); i++) {
            if (deliverable.getResponsiblePartnership().get(i).getInstitution() == null
              || deliverable.getResponsiblePartnership().get(i).getInstitution().getId() == null) {

              action.addMessage("Other Partnership Institution");
              action.getInvalidFields().put("input-deliverable.otherPartnerships[" + i + "].institution.id",
                InvalidFieldsMessages.EMPTYFIELD);

            } else {
              if (isManagingPartnerPersonRequerid) {
                if (deliverable.getResponsiblePartnership().get(i).getPartnershipPersons() == null) {
                  action.addMessage("Other Partnership Persons");
                  action.getInvalidFields().put("input-deliverable.otherPartnerships[" + i + "].partnershipPersons",
                    InvalidFieldsMessages.EMPTYFIELD);
                } else {
                  boolean haveUser = false;
                  for (int j = 0; j < deliverable.getResponsiblePartnership().get(i).getPartnershipPersons()
                    .size(); j++) {
                    if (deliverable.getResponsiblePartnership().get(i).getPartnershipPersons().get(j).getUser() != null
                      && deliverable.getResponsiblePartnership().get(i).getPartnershipPersons().get(j).getUser()
                        .getId() != null) {
                      haveUser = true;
                      break;
                    }
                  }
                  if (!haveUser) {
                    action.addMessage("Other Partnership Persons");
                    action.getInvalidFields().put("input-deliverable.otherPartnerships[" + i + "].partnershipPersons",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }
                }
              }
            }
          }

        }


        if (deliverable.getFundingSources() == null || deliverable.getFundingSources().isEmpty()) {
          action.addMessage(action.getText("project.deliverable.fundingSource.readText"));
          action.getInvalidFields().put("list-deliverable.fundingSources",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Funding Sources"}));
        }
      }


      if ((action.isReportingActive())) {

        if (action.isReportingActive() && deliverable.getDeliverableInfo(action.getActualPhase()).getStatus() != null
          && deliverable.getDeliverableInfo(action.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
          action.addMessage(action.getText("project.deliverable.generalInformation.status"));
          action.getInvalidFields().put("input-deliverable.deliverableInfo.status", InvalidFieldsMessages.EMPTYFIELD);
        }

        if (action.hasSpecificities(APConstants.CRP_HAS_DISEMINATION)) {

          // Deliverable Dissemination
          if (deliverable.getDissemination() != null) {
            this.validateDissemination(deliverable.getDissemination(), saving, action);
          } else {
            action.addMessage(action.getText("project.deliverable.dissemination.v.dissemination"));
            action.getInvalidFields().put("input-deliverable.deliverableInfo.dissemination.isOpenAccess",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Deliverable Meta-data Elements
          if (deliverable.getMetadataElements() != null) {
            // this.validateMetadata(deliverable.getMetadataElements());
          } else {
            action.addMessage(action.getText("project.deliverable.v.metadata"));
            action.getInvalidFields().put("input-deliverable.deliverableInfo.dissemination.isOpenAccess",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Deliverable Publication Meta-data
          // Validate Publication Meta-data
          this.validatePublicationMetadata(deliverable.getPublication(), deliverable.getDeliverableInfo(), action);

          // Deliverable Licenses
          if (deliverable.getDeliverableInfo(action.getActualPhase()).getAdoptedLicense() != null) {
            this.validateLicense(deliverable, action);
          } else {
            action.addMessage(action.getText("project.deliverable.v.ALicense"));
            action.getInvalidFields().put("input-deliverable.deliverableInfo.adoptedLicense",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          // Deliverable Quality Check
          if (deliverable.getDeliverableInfo(action.getActualPhase()).getDeliverableType() != null && (deliverable
            .getDeliverableInfo(action.getActualPhase()).getDeliverableType().getId().intValue() == 51
            || deliverable.getDeliverableInfo(action.getActualPhase()).getDeliverableType().getId().intValue() == 74)) {
            if (deliverable.getQualityCheck() != null) {
              if (deliverable.getQualityCheck().getQualityAssurance() == null) {
                action.addMessage(action.getText("project.deliverable.v.qualityCheck.assurance"));
                action.getInvalidFields().put("input-deliverable.qualityCheck.qualityAssurance.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

              if (deliverable.getQualityCheck().getDataDictionary() == null) {
                action.addMessage(action.getText("project.deliverable.v.qualityCheck.dictionary"));
                action.getInvalidFields().put("input-deliverable.qualityCheck.dataDictionary.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

              if (deliverable.getQualityCheck().getDataTools() == null) {
                action.addMessage(action.getText("project.deliverable.v.qualityCheck.tool"));
                action.getInvalidFields().put("input-deliverable.qualityCheck.dataTools.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            }
          }

          // Validate Deliverable Participant
          if (deliverable.getDeliverableParticipant() != null
            && deliverable.getDeliverableParticipant().getHasParticipants() != null) {
            this.validateDeliverableParticipant(deliverable.getDeliverableParticipant(), action);
          } else {
            action.addMessage("hasParticipants");
            action.getInvalidFields().put("input-deliverable.deliverableParticipant.hasParticipants",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }


    }

    // Validate Status if the extended year is greater than the current phase year
    DeliverableInfo dInfo = deliverable.getDeliverableInfo(action.getActualPhase());
    if (dInfo != null) {
      Integer status = dInfo.getStatus();
      Integer newExpectedYear = dInfo.getNewExpectedYear();
      Integer statusExtendedId = Integer.parseInt(ProjectStatusEnum.Extended.getStatusId());
      if (status != null && newExpectedYear != null && (status.intValue() != statusExtendedId)
        && (newExpectedYear > action.getCurrentCycleYear())) {
        action.addMessage(action.getText("deliverable.status.validation1"));
        action.getInvalidFields().put("input-deliverable.deliverableInfo.status",
          action.getText("deliverable.status.validation1"));
      }
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(deliverable, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      action.getActualPhase().getUpkeep(), ProjectSectionStatusEnum.DELIVERABLES.getStatus(), action);

  }

  private void validateDeliverableInfo(DeliverableInfo deliverableInfo, Deliverable deliverable, Project project,
    BaseAction action) {
    if (!(this.isValidString(deliverable.getDeliverableInfo(action.getActualPhase()).getTitle())
      && this.wordCount(deliverable.getDeliverableInfo(action.getActualPhase()).getTitle()) <= 25)) {
      action.addMessage(action.getText("project.deliverable.generalInformation.title"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }
    // Add description validator
    if (!(this.isValidString(deliverable.getDeliverableInfo(action.getActualPhase()).getDescription())
      && this.wordCount(deliverable.getDeliverableInfo(action.getActualPhase()).getDescription()) <= 50)) {
      action.addMessage(action.getText("project.deliverable.generalInformation.description"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.description", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (deliverable.getDeliverableInfo(action.getActualPhase()).getDeliverableType() != null) {
      if (deliverable.getDeliverableInfo(action.getActualPhase()).getDeliverableType().getId() == null
        || deliverable.getDeliverableInfo(action.getActualPhase()).getDeliverableType().getId() == -1) {
        action.addMessage(action.getText("project.deliverable.generalInformation.subType"));
        action.getInvalidFields().put("input-deliverable.deliverableInfo.deliverableType.id",
          InvalidFieldsMessages.EMPTYFIELD);
        deliverable.getDeliverableInfo(action.getActualPhase()).setDeliverableType(null);

      } else {
        if (deliverable.getDeliverableInfo(action.getActualPhase()).getDeliverableType()
          .getDeliverableCategory() != null) {
          if (deliverable.getDeliverableInfo(action.getActualPhase()).getDeliverableType().getDeliverableCategory()
            .getId() == -1) {
            action.addMessage(action.getText("project.deliverable.generalInformation.type"));
            action.getInvalidFields().put("input-deliverable.deliverableInfo.deliverableType.deliverableType.id",
              InvalidFieldsMessages.EMPTYFIELD);
            deliverable.getDeliverableInfo(action.getActualPhase()).setDeliverableType(null);

          }
        } else {
          action.addMessage(action.getText("project.deliverable.generalInformation.type"));
          action.getInvalidFields().put("input-deliverable.deliverableInfo.deliverableType.deliverableType.id",
            InvalidFieldsMessages.EMPTYFIELD);
          deliverable.getDeliverableInfo(action.getActualPhase()).setDeliverableType(null);
        }
      }
    } else {
      action.addMessage(action.getText("project.deliverable.generalInformation.subType"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.deliverableType.id",
        InvalidFieldsMessages.EMPTYFIELD);
      action.getInvalidFields().put("input-deliverable.deliverableInfo.deliverableType.deliverableType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getDeliverableInfo(action.getActualPhase()).getStatus() != null) {
      if (deliverable.getDeliverableInfo(action.getActualPhase()).getStatus() == -1) {
        action.addMessage(action.getText("project.deliverable.generalInformation.status"));
        action.getInvalidFields().put("input-deliverable.deliverableInfo.status", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("project.deliverable.generalInformation.status"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.status", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (deliverable.getDeliverableInfo(action.getActualPhase()).getYear() == -1) {
      action.addMessage(action.getText("project.deliverable.generalInformation.year"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.year", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getDeliverableInfo(action.getActualPhase()).getStatus() != null
      && deliverable.getDeliverableInfo(action.getActualPhase()).getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Extended.getStatusId())
      && deliverable.getDeliverableInfo(action.getActualPhase()).getNewExpectedYear() == null) {
      action.addMessage(action.getText("project.deliverable.generalInformation.newewExpectedYear"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.newExpectedYear",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (!action.isCenterGlobalUnit()) {
      if (!(project.getProjecInfoPhase(action.getActualPhase()).getAdministrative() != null
        && project.getProjecInfoPhase(action.getActualPhase()).getAdministrative().booleanValue() == true)) {
        if (deliverable.getDeliverableInfo(action.getActualPhase()).getCrpClusterKeyOutput() != null
          && deliverable.getDeliverableInfo(action.getActualPhase()).getCrpClusterKeyOutput().getId() != null) {
          if (deliverable.getDeliverableInfo(action.getActualPhase()).getCrpClusterKeyOutput().getId() == -1) {
            action.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
            action.getInvalidFields().put("input-deliverable.deliverableInfo.crpClusterKeyOutput.id",
              InvalidFieldsMessages.EMPTYFIELD);

            deliverable.getDeliverableInfo(action.getActualPhase()).setCrpClusterKeyOutput(null);

          }
        } else {
          action.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
          action.getInvalidFields().put("input-deliverable.deliverableInfo.crpClusterKeyOutput.id",
            InvalidFieldsMessages.EMPTYFIELD);
          deliverable.getDeliverableInfo(action.getActualPhase()).setCrpClusterKeyOutput(null);
        }
      }
    }


    // Validate Geographic Scope

    boolean haveRegions = false;
    boolean haveCountries = false;

    if (deliverable.getGeographicScopes() == null || deliverable.getGeographicScopes().isEmpty()) {
      action.addMessage(action.getText("geographicScopes"));
      action.addMissingField("deliverable.geographicScope");
      action.getInvalidFields().put("list-deliverable.geographicScopes",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
    } else {
      for (DeliverableGeographicScope deliverableGeographicScope : deliverable.getGeographicScopes()) {
        if (deliverableGeographicScope.getRepIndGeographicScope().getId() == 2) {
          haveRegions = true;
        }
        if (deliverableGeographicScope.getRepIndGeographicScope().getId() != 1
          && deliverableGeographicScope.getRepIndGeographicScope().getId() != 2) {
          haveCountries = true;
        }
      }
    }


    if (haveRegions) {
      // Validate Regions
      if (deliverable.getDeliverableRegions() == null || deliverable.getDeliverableRegions().isEmpty()) {
        action.addMessage(action.getText("regions"));
        action.addMissingField("deliverable.deliverableRegions");
        action.getInvalidFields().put("list-deliverable.deliverableRegions",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
      }
    }

    if (haveCountries) {
      // Validate Countries
      if (deliverable.getCountriesIds() == null || deliverable.getCountriesIds().isEmpty()) {
        action.addMessage(action.getText("countries"));
        action.addMissingField("deliverable.countries");
        action.getInvalidFields().put("input-deliverable.countriesIds",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
      }
    }

    // // Deliverable Locations
    // if (deliverableInfo.getGeographicScope() == null || deliverableInfo.getGeographicScope().getId() == null
    // || deliverableInfo.getGeographicScope().getId() == -1) {
    // action.addMessage(action.getText("deliverable.geographicScope"));
    // action.getInvalidFields().put("input-deliverable.deliverableInfo.geographicScope.id",
    // InvalidFieldsMessages.EMPTYFIELD);
    // } else {
    // if (deliverableInfo.getGeographicScope().getId().equals(action.getReportingIndGeographicScopeRegional())) {
    // if (deliverable.getDeliverableRegions() == null) {
    // action.addMessage(action.getText("deliverable.region"));
    // action.getInvalidFields().put("input-deliverable.deliverableRegions", InvalidFieldsMessages.EMPTYFIELD);
    // }
    // }
    // if (deliverableInfo.getGeographicScope().getId().equals(action.getReportingIndGeographicScopeMultiNational())
    // || deliverableInfo.getGeographicScope().getId().equals(action.getReportingIndGeographicScopeNational())
    // || deliverableInfo.getGeographicScope().getId().equals(action.getReportingIndGeographicScopeSubNational())) {
    // if (deliverable.getCountriesIds() == null || deliverable.getCountriesIds().isEmpty()) {
    // action.addMessage(action.getText("deliverable.countries"));
    // action.getInvalidFields().put("input-deliverable.countriesIds", InvalidFieldsMessages.EMPTYFIELD);
    // }
    // }
    // }
    // Cross Cutting Markers
    List<CgiarCrossCuttingMarker> cgiarCrossCuttingMarkers = cgiarCrossCuttingMarkerManager.findAll();

    // Deliverable Cross Cutting Markers
    if (deliverable.getCrossCuttingMarkers() != null) {
      int i = 0;
      for (CgiarCrossCuttingMarker cgiarCrossCuttingMarker : cgiarCrossCuttingMarkers) {
        List<DeliverableCrossCuttingMarker> deliverableCrossCuttingMarkers =
          deliverable.getCrossCuttingMarkers().stream()
            .filter(
              cc -> cc.isActive() && cc.getCgiarCrossCuttingMarker().getId().equals(cgiarCrossCuttingMarker.getId()))
            .collect(Collectors.toList());
        if (deliverableCrossCuttingMarkers != null && !deliverableCrossCuttingMarkers.isEmpty()) {
          DeliverableCrossCuttingMarker deliverableCrossCuttingMarker = deliverableCrossCuttingMarkers.get(0);
          if (deliverableCrossCuttingMarker.getRepIndGenderYouthFocusLevel() == null
            || deliverableCrossCuttingMarker.getRepIndGenderYouthFocusLevel().getId() == -1) {
            action.addMessage(cgiarCrossCuttingMarker.getName());
            action.getInvalidFields().put(
              "input-deliverable.crossCuttingMarkers[" + i + "].repIndGenderYouthFocusLevel.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        } else {
          action.addMessage(cgiarCrossCuttingMarker.getName());
          action.getInvalidFields().put(
            "input-deliverable.crossCuttingMarkers[" + i + "].repIndGenderYouthFocusLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        i++;
      }

    } else {
      int i = 0;
      for (CgiarCrossCuttingMarker cgiarCrossCuttingMarker : cgiarCrossCuttingMarkers) {
        action.addMessage(cgiarCrossCuttingMarker.getName());
        action.getInvalidFields().put("input-deliverable.crossCuttingMarkers[" + i + "].repIndGenderYouthFocusLevel.id",
          InvalidFieldsMessages.EMPTYFIELD);
        i++;
      }
    }

  }

  private void validateDeliverableParticipant(DeliverableParticipant deliverableParticipant, BaseAction action) {
    if (deliverableParticipant.getHasParticipants()) {
      if (!this.isValidString(deliverableParticipant.getEventActivityName())) {
        action.addMessage(action.getText("involveParticipants.title"));
        action.getInvalidFields().put("input-deliverable.deliverableParticipant.eventActivityName",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (deliverableParticipant.getRepIndTypeActivity() == null
        || deliverableParticipant.getRepIndTypeActivity().getId() == -1) {
        action.addMessage(action.getText("involveParticipants.typeActivity"));
        action.getInvalidFields().put("input-deliverable.deliverableParticipant.repIndTypeActivity.id",
          InvalidFieldsMessages.EMPTYFIELD);
      } else {
        RepIndTypeActivity repIndTypeActivity =
          repIndTypeActivityManager.getRepIndTypeActivityById(deliverableParticipant.getRepIndTypeActivity().getId());

        if (repIndTypeActivity.getId().equals(action.getReportingIndTypeActivityAcademicDegree())) {
          if (!this.isValidString(deliverableParticipant.getAcademicDegree())) {
            action.addMessage(action.getText("involveParticipants.academicDegree"));
            action.getInvalidFields().put("input-deliverable.deliverableParticipant.academicDegree",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
        if (repIndTypeActivity.getIsFormal()) {
          if (deliverableParticipant.getRepIndTrainingTerm() == null
            || deliverableParticipant.getRepIndTrainingTerm().getId() == -1) {
            action.addMessage(action.getText("involveParticipants.trainingPeriod"));
            action.getInvalidFields().put("input-deliverable.deliverableParticipant.repIndTrainingTerm.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
      if (deliverableParticipant.getParticipants() == null
        || !this.isValidNumber(deliverableParticipant.getParticipants().toString())
        || deliverableParticipant.getParticipants() < 0) {
        action.addMessage(action.getText("involveParticipants.participants"));
        action.getInvalidFields().put("input-deliverable.deliverableParticipant.participants",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (deliverableParticipant.getDontKnowFemale() == null || !deliverableParticipant.getDontKnowFemale()) {
        if (deliverableParticipant.getFemales() == null
          || !this.isValidNumber(deliverableParticipant.getFemales().toString())
          || deliverableParticipant.getFemales() < 0) {
          action.addMessage(action.getText("involveParticipants.females"));
          action.getInvalidFields().put("input-deliverable.deliverableParticipant.females",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
      if (deliverableParticipant.getRepIndTypeParticipant() == null
        || deliverableParticipant.getRepIndTypeParticipant().getId() == -1) {
        action.addMessage(action.getText("involveParticipants.participantsType"));
        action.getInvalidFields().put("input-deliverable.deliverableParticipant.repIndTypeParticipant.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }
  }


  public void validateDissemination(DeliverableDissemination dissemination, boolean saving, BaseAction action) {

    if (dissemination.getIsOpenAccess() != null) {
      if (!dissemination.getIsOpenAccess().booleanValue()) {

        Boolean hasIntellectualProperty =
          (dissemination.getIntellectualProperty() != null && dissemination.getIntellectualProperty().booleanValue())
            || (dissemination.getType() != null && dissemination.getType().equals("intellectualProperty"));
        Boolean hasLimitedExclusivity =
          (dissemination.getLimitedExclusivity() != null && dissemination.getLimitedExclusivity().booleanValue())
            || (dissemination.getType() != null && dissemination.getType().equals("limitedExclusivity"));
        Boolean hasRestrictedUse = (dissemination.getRestrictedUseAgreement() != null
          && dissemination.getRestrictedUseAgreement().booleanValue())
          || (dissemination.getType() != null && dissemination.getType().equals("restrictedUseAgreement"));
        Boolean hasEffectiveDate = (dissemination.getEffectiveDateRestriction() != null
          && dissemination.getEffectiveDateRestriction().booleanValue())
          || (dissemination.getType() != null && dissemination.getType().equals("effectiveDateRestriction"));
        Boolean hasNotDisseminated =
          (dissemination.getNotDisseminated() != null && dissemination.getNotDisseminated().booleanValue())
            || (dissemination.getType() != null && dissemination.getType().equals("notDisseminated"));

        if (hasIntellectualProperty || hasLimitedExclusivity || hasRestrictedUse || hasEffectiveDate
          || hasNotDisseminated) {
          if (hasRestrictedUse && dissemination.getRestrictedAccessUntil() == null) {
            action.addMessage(action.getText("project.deliverable.dissemination.v.restrictedUseAgreement"));
            action.getInvalidFields().put("input-deliverable.dissemination.restrictedAccessUntil",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (hasEffectiveDate && dissemination.getRestrictedEmbargoed() == null) {
            action.addMessage(action.getText("project.deliverable.dissemination.v.restrictedEmbargoed"));
            action.getInvalidFields().put("input-deliverable.dissemination.restrictedEmbargoed",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        } else {
          action.addMessage(action.getText("project.deliverable.dissemination.v.openAccessRestriction"));
          action.getInvalidFields().put("input-deliverable.dissemination.type", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      action.addMessage(action.getText("project.deliverable.dissemination.v.isOpenAccess"));
      action.getInvalidFields().put("input-deliverable.dissemination.isOpenAccess", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (dissemination.getAlreadyDisseminated() != null) {
      if (dissemination.getAlreadyDisseminated().booleanValue()) {
        if (dissemination.getDisseminationChannel() != null) {
          if (dissemination.getDisseminationChannel().equals("-1")) {
            action.addMessage(action.getText("project.deliverable.dissemination.v.DisseminationChanel"));
            action.getInvalidFields().put("input-deliverable.dissemination.disseminationChannel",
              InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (!(this.isValidString(dissemination.getDisseminationUrl())
              && this.wordCount(dissemination.getDisseminationUrl()) <= 100)) {
              action.addMessage(action.getText("project.deliverable.dissemination.v.ChanelURL"));
              action.getInvalidFields().put("input-deliverable.dissemination.disseminationUrl",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        } else {
          action.addMessage(action.getText("project.deliverable.dissemination.v.DisseminationChanel"));
          action.getInvalidFields().put("input-deliverable.dissemination.disseminationChannel",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        // Validate confidential
        if (dissemination.getConfidential() != null) {
          if (dissemination.getConfidential()) {
            if (dissemination.getConfidentialUrl() == null || dissemination.getConfidentialUrl().trim().isEmpty()) {
              action.addMessage(action.getText("project.deliverable.dissemination.confidentialUrl"));
              action.getInvalidFields().put("input-deliverable.dissemination.confidentialUrl",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        } else {
          action.addMessage(action.getText("project.deliverable.dissemination.confidential"));
          action.getInvalidFields().put("input-deliverable.dissemination.confidential",
            InvalidFieldsMessages.EMPTYFIELD);
        }

      }
    } else {
      action.addMessage(action.getText("project.deliverable.dissemination.v.alreadyDisseminated"));
      action.getInvalidFields().put("input-deliverable.dissemination.alreadyDisseminated",
        InvalidFieldsMessages.EMPTYFIELD);

    }
  }

  public void validateLicense(Deliverable deliverable, BaseAction action) {
    if (deliverable.getDeliverableInfo(action.getActualPhase()).getAdoptedLicense().booleanValue()) {
      if (deliverable.getDeliverableInfo(action.getActualPhase()).getLicense() != null) {
        if (deliverable.getDeliverableInfo(action.getActualPhase()).getLicense()
          .equals(LicensesTypeEnum.OTHER.getValue())) {
          if (deliverable.getDeliverableInfo(action.getActualPhase()).getOtherLicense() != null) {
            if (!(this.isValidString(deliverable.getDeliverableInfo(action.getActualPhase()).getOtherLicense())
              && this.wordCount(deliverable.getDeliverableInfo(action.getActualPhase()).getOtherLicense()) <= 100)) {
              action.addMessage(action.getText("project.deliverable.license.v.other"));
              action.getInvalidFields().put("input-deliverable.deliverableInfo.otherLicense",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            if (deliverable.getDeliverableInfo(action.getActualPhase()).getAllowModifications() == null) {
              action.addMessage(action.getText("project.deliverable.license.v.allowModification"));
              action.getInvalidFields().put("input-deliverable.deliverableInfo.dissemination.allowModification",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }
      } else {
        action.addMessage(action.getText("project.deliverable.v.license"));
        action.getInvalidFields().put("input-deliverable.deliverableInfo.license", InvalidFieldsMessages.EMPTYFIELD);
      }
    }
  }

  public void validateMetadata(List<DeliverableMetadataElement> elements, BaseAction action) {

    boolean description = false;


    for (DeliverableMetadataElement deliverableMetadataElement : elements) {
      if (deliverableMetadataElement != null) {
        if (deliverableMetadataElement.getMetadataElement().getId() != null) {
          if (8L == deliverableMetadataElement.getMetadataElement().getId().longValue()) {
            if ((this.isValidString(deliverableMetadataElement.getElementValue())
              && this.wordCount(deliverableMetadataElement.getElementValue()) <= 100)) {
              description = true;
            }
            break;

          }
        }
      }
    }
    if (!description) {
      action.addMessage(action.getText("project.deliverable.metadata.v.description"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.metadataElements[7].elementValue",
        InvalidFieldsMessages.EMPTYFIELD);
    }


  }


  public void validatePublicationMetadata(DeliverablePublicationMetadata deliverablePublicationMetadata,
    DeliverableInfo deliverableInfo, BaseAction action) {
    if (action.hasDeliverableRule(deliverableInfo, APConstants.DELIVERABLE_RULE_PUBLICATION_METADATA)) {
      if (deliverablePublicationMetadata != null && deliverablePublicationMetadata.getId() != null
        && deliverablePublicationMetadata.getId().intValue() != -1) {

        if (action.hasDeliverableRule(deliverableInfo, APConstants.DELIVERABLE_RULE_JORNAL_ARTICLES)) {
          // Validation of Volume or Issue or Pages
          if (!this.isValidString(deliverablePublicationMetadata.getVolume())
            && !this.isValidString(deliverablePublicationMetadata.getIssue())
            && !this.isValidString(deliverablePublicationMetadata.getPages())) {
            action.addMessage(action.getText("project.deliverable.publication.v.volume"));
            action.getInvalidFields().put("input-deliverable.publication.volume", InvalidFieldsMessages.EMPTYFIELD);
            action.addMessage(action.getText("project.deliverable.publication.v.issue"));
            action.getInvalidFields().put("input-deliverable.publication.issue", InvalidFieldsMessages.EMPTYFIELD);
            action.addMessage(action.getText("project.deliverable.publication.v.pages"));
            action.getInvalidFields().put("input-deliverable.publication.pages", InvalidFieldsMessages.EMPTYFIELD);
          }
        }

        // Validation of Journal Article Name
        if (!(this.isValidString(deliverablePublicationMetadata.getJournal())
          && this.wordCount(deliverablePublicationMetadata.getJournal()) <= 100)) {
          action.addMessage(action.getText("project.deliverable.publication.v.journal"));
          action.getInvalidFields().put("input-deliverable.publication.journal", InvalidFieldsMessages.EMPTYFIELD);
        }

        // Validation of ISI Question
        if (deliverablePublicationMetadata.getIsiPublication() == null) {
          action.addMessage(action.getText("deliverable.isiPublication"));
          action.getInvalidFields().put("input-deliverable.publication.isiPublication",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }
  }
}

