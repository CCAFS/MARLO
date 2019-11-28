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

package org.cgiar.ccafs.marlo.validation.publications;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndTypeActivityManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicScope;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAssetTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class PublicationValidator extends BaseValidator {


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndTypeActivityManager repIndTypeActivityManager;

  @Inject
  public PublicationValidator(GlobalUnitManager crpManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndTypeActivityManager repIndTypeActivityManager) {
    super();
    this.crpManager = crpManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndTypeActivityManager = repIndTypeActivityManager;
  }

  private Path getAutoSaveFilePath(Deliverable deliverable, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.DELIVERABLE.getStatus().replace("/", "_");
    String autoSaveFile =
      deliverable.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction action, Deliverable deliverable, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    boolean validate = true;

    if (validate) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(deliverable, action.getCrpID());
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }
      this.validateDeliverableInfo(deliverable.getDeliverableInfo(), deliverable, action);

      // Validate Leaders
      if (deliverable.getLeaders() == null || deliverable.getLeaders().size() == 0) {
        action.addMessage(action.getText("deliverable.leaders"));
        action.getInvalidFields().put("list-deliverable.leaders",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Leaders"}));
      }

      // Validate Flagships
      if (deliverable.getPrograms() != null) {
        if (deliverable.getPrograms().size() == 0) {
          if (deliverable.getFlagshipValue() == null || deliverable.getFlagshipValue().length() == 0) {
            action.addMessage(action.getText("projectDescription.flagships"));
            action.getInvalidFields().put("input-deliverable.flagshipValue", InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      } else {
        if (deliverable.getFlagshipValue().length() == 0) {
          action.addMessage(action.getText("projectDescription.flagships"));
          action.getInvalidFields().put("input-deliverable.flagshipValue", InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      // Validate Funding sources
      if (deliverable.getFundingSources() == null || deliverable.getFundingSources().isEmpty()) {
        action.addMessage(action.getText("project.deliverable.generalInformation.fundingSources"));
        action.getInvalidFields().put("list-deliverable.fundingSources",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Funding Sources"}));
      }

      if (action.hasSpecificities(APConstants.CRP_HAS_DISEMINATION)) {

        // Validate Dissemination
        if (deliverable.getDissemination() != null) {
          this.validateDissemination(deliverable.getDissemination(), saving, action);
        } else {
          action.addMessage(action.getText("project.deliverable.dissemination.v.dissemination"));
          action.getInvalidFields().put("input-deliverable.dissemination.alreadyDisseminated",
            InvalidFieldsMessages.EMPTYFIELD);
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

        // Validate Publication Meta-data
        this.validatePublicationMetadata(deliverable.getPublication(), deliverable.getDeliverableInfo(), action);

      }
    }
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(deliverable, deliverable.getPhase().getDescription(), deliverable.getPhase().getYear(),
      deliverable.getPhase().getUpkeep(), ProjectSectionStatusEnum.DELIVERABLES.getStatus(), action);
  }

  private void validateDeliverableInfo(DeliverableInfo deliverableInfo, Deliverable deliverable, BaseAction action) {
    if (!(this.isValidString(deliverableInfo.getTitle()) && this.wordCount(deliverableInfo.getTitle()) <= 25)) {
      action.addMessage(action.getText("project.deliverable.generalInformation.title"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (!(this.isValidString(deliverableInfo.getDescription())
      && this.wordCount(deliverableInfo.getDescription()) <= 50)) {
      action.addMessage(action.getText("project.deliverable.generalInformation.description"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.description", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverableInfo.getDeliverableType() == null || deliverableInfo.getDeliverableType().getId() == null
      || deliverableInfo.getDeliverableType().getId() == -1) {
      action.addMessage(action.getText("project.deliverable.generalInformation.subType"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.deliverableType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (action.hasSpecificities(APConstants.CRP_HAS_REGIONS)) {
      if (deliverableInfo.getIsLocationGlobal() == null
        || deliverableInfo.getIsLocationGlobal().booleanValue() == false) {
        if (deliverable.getRegionsValue() == null || deliverable.getRegionsValue().length() == 0) {
          action.addMessage(action.getText("projectDescription.regions"));
          action.getInvalidFields().put("input-deliverable.regionsValue", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    if (deliverableInfo.getCrpClusterKeyOutput() == null || deliverableInfo.getCrpClusterKeyOutput().getId() == null
      || deliverableInfo.getCrpClusterKeyOutput().getId() == -1) {
      action.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
      action.getInvalidFields().put("input-deliverable.deliverableInfo.crpClusterKeyOutput.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (action.hasSpecificities(APConstants.CRP_HAS_DISEMINATION)) {
      // Deliverable Licenses
      if (deliverableInfo.getAdoptedLicense() != null) {
        this.validateLicense(deliverableInfo, action);
      } else {
        action.addMessage(action.getText("project.deliverable.v.ALicense"));
        action.getInvalidFields().put("input-deliverable.deliverableInfo.adoptedLicense",
          InvalidFieldsMessages.EMPTYFIELD);
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
        if (deliverableParticipant.getRepIndTypeActivity().getId()
          .equals(action.getReportingIndTypeActivityAcademicDegree())) {
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
      }
    } else {
      action.addMessage(action.getText("project.deliverable.dissemination.v.alreadyDisseminated"));
      action.getInvalidFields().put("input-deliverable.dissemination.alreadyDisseminated",
        InvalidFieldsMessages.EMPTYFIELD);

    }
  }

  private void validateIntellectualAsset(DeliverableIntellectualAsset intellectualAsset, BaseAction action) {
    if (intellectualAsset.getHasPatentPvp()) {
      if (!this.isValidString(intellectualAsset.getApplicant())) {
        action.addMessage(action.getText("intellectualAsset.applicants"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.applicant",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (intellectualAsset.getType() == null) {
        action.addMessage(action.getText("intellectualAsset.type"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.type", InvalidFieldsMessages.EMPTYFIELD);
      } else {
        if (DeliverableIntellectualAssetTypeEnum.getValue(intellectualAsset.getType())
          .equals(DeliverableIntellectualAssetTypeEnum.Patent)) {
          if (intellectualAsset.getFillingType() == null || intellectualAsset.getFillingType().getId() == -1) {
            action.addMessage(action.getText("intellectualAsset.fillingType"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.fillingType.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (intellectualAsset.getPatentStatus() == null || intellectualAsset.getPatentStatus().getId() == -1) {
            action.addMessage(action.getText("intellectualAsset.patentStatus"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.patentStatus.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (intellectualAsset.getPatentType() == null) {
            action.addMessage(action.getText("intellectualAsset.patentType"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.patentType",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        } else if (DeliverableIntellectualAssetTypeEnum.getValue(intellectualAsset.getType())
          .equals(DeliverableIntellectualAssetTypeEnum.PVP)) {
          if (!this.isValidString(intellectualAsset.getVarietyName())) {
            action.addMessage(action.getText("intellectualAsset.varietyName"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.varietyName",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (intellectualAsset.getStatus() == null || intellectualAsset.getStatus() == -1) {
            action.addMessage(action.getText("intellectualAsset.status"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.status",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (intellectualAsset.getCountry() == null || intellectualAsset.getCountry().getIsoAlpha2() == null
            || intellectualAsset.getCountry().getIsoAlpha2().equals("-1")) {
            action.addMessage(action.getText("intellectualAsset.country"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.country.isoAlpha2",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (intellectualAsset.getAppRegNumber() == null) {
            action.addMessage(action.getText("intellectualAsset.appRegNumber"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.appRegNumber",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          if (!this.isValidString(intellectualAsset.getBreederCrop())) {
            action.addMessage(action.getText("intellectualAsset.breederCrop"));
            action.getInvalidFields().put("input-deliverable.intellectualAsset.breederCrop",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
      if (!this.isValidString(intellectualAsset.getTitle())) {
        action.addMessage(action.getText("intellectualAsset.title.readText"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.title", InvalidFieldsMessages.EMPTYFIELD);
      }
      if (intellectualAsset.getDateFilling() == null) {
        action.addMessage(action.getText("intellectualAsset.dateFilling"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.dateFilling",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (intellectualAsset.getDateRegistration() == null) {
        action.addMessage(action.getText("intellectualAsset.dateRegistration"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.dateRegistration",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (intellectualAsset.getDateExpiry() == null) {
        action.addMessage(action.getText("intellectualAsset.dateExpiry"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.dateExpiry",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!this.isValidString(intellectualAsset.getAdditionalInformation())) {
        action.addMessage(action.getText("intellectualAsset.additionalInformation.readText"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.additionalInformation",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!this.isValidString(intellectualAsset.getLink())) {
        action.addMessage(action.getText("intellectualAsset.link.readText"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.link", InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!this.isValidString(intellectualAsset.getPublicCommunication())) {
        action.addMessage(action.getText("intellectualAsset.publicCommunication.readText"));
        action.getInvalidFields().put("input-deliverable.intellectualAsset.publicCommunication",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }
  }

  public void validateLicense(DeliverableInfo deliverableInfo, BaseAction action) {
    if (deliverableInfo.getAdoptedLicense().booleanValue()) {
      if (deliverableInfo.getLicense() != null) {
        if (this.isValidString(deliverableInfo.getLicense())) {
          if (deliverableInfo.getLicense().equals(LicensesTypeEnum.OTHER.getValue())) {
            if (deliverableInfo.getOtherLicense() != null) {
              if (!(this.isValidString(deliverableInfo.getOtherLicense())
                && this.wordCount(deliverableInfo.getOtherLicense()) <= 100)) {
                action.addMessage(action.getText("project.deliverable.license.v.other"));
                action.getInvalidFields().put("input-deliverable.deliverableInfo.otherLicense",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
              if (deliverableInfo.getAllowModifications() == null) {
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
      action.getInvalidFields().put("input-deliverable.metadataElements[7].elementValue",
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
