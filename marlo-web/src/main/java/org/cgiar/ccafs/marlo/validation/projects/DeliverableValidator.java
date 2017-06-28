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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableValidator extends BaseValidator {

  BaseAction action;

  @Inject
  private CrpManager crpManager;
  @Inject
  private ProjectManager projectManager;

  @Inject
  private InstitutionManager institutionManager;

  @Inject
  private ProjectPartnerPersonManager projectPartnerPersonManager;

  @Inject
  public DeliverableValidator() {
  }

  private Path getAutoSaveFilePath(Deliverable deliverable, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.DELIVERABLE.getStatus().replace("/", "_");
    String autoSaveFile =
      deliverable.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction action, Deliverable deliverable, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    this.action = action;

    boolean validate = false;
    if (action.isPlanningActive()) {
      validate = deliverable.getYear() >= action.getCurrentCycleYear();
    } else {
      validate = deliverable.isRequieriedReporting(action.getCurrentCycleYear());
    }
    if (validate) {
      Project project = projectManager.getProjectById(deliverable.getProject().getId());

      if (!saving) {
        Path path = this.getAutoSaveFilePath(deliverable, action.getCrpID());

        if (path.toFile().exists()) {
          this.addMissingField("draft");
        }
      }
      if (!(deliverable.getStatus() != null
        && deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId()))) {
        if (!(this.isValidString(deliverable.getTitle()) && this.wordCount(deliverable.getTitle()) <= 25)) {
          this.addMessage(action.getText("project.deliverable.generalInformation.title"));
          action.getInvalidFields().put("input-deliverable.title", InvalidFieldsMessages.EMPTYFIELD);
        }
        /*
         * if (!(this.isValidString(deliverable.getStatusDescription())
         * && this.wordCount(deliverable.getStatusDescription()) <= 15)) {
         * this.addMessage(action.getText("project.deliverable.generalInformation.description"));
         * action.getInvalidFields().put("input-deliverable.description", InvalidFieldsMessages.EMPTYFIELD);
         * }
         */
        if (deliverable.getDeliverableType() != null) {
          if (deliverable.getDeliverableType().getId() == null || deliverable.getDeliverableType().getId() == -1) {
            this.addMessage(action.getText("project.deliverable.generalInformation.subType"));
            action.getInvalidFields().put("input-deliverable.deliverableType.id", InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (deliverable.getDeliverableType().getDeliverableType() != null) {
              if (deliverable.getDeliverableType().getDeliverableType().getId() == -1) {
                this.addMessage(action.getText("project.deliverable.generalInformation.type"));
                action.getInvalidFields().put("input-deliverable.deliverableType.deliverableType.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            } else {
              this.addMessage(action.getText("project.deliverable.generalInformation.type"));
              action.getInvalidFields().put("input-deliverable.deliverableType.deliverableType.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        } else {
          this.addMessage(action.getText("project.deliverable.generalInformation.subType"));
          action.getInvalidFields().put("input-deliverable.deliverableType.id", InvalidFieldsMessages.EMPTYFIELD);
          action.getInvalidFields().put("input-deliverable.deliverableType.deliverableType.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        if (deliverable.getStatus() != null) {
          if (deliverable.getStatus() == -1) {
            this.addMessage(action.getText("project.deliverable.generalInformation.status"));
            action.getInvalidFields().put("input-deliverable.status", InvalidFieldsMessages.EMPTYFIELD);
          }
        } else {
          this.addMessage(action.getText("project.deliverable.generalInformation.status"));
          action.getInvalidFields().put("input-deliverable.status", InvalidFieldsMessages.EMPTYFIELD);
        }


        if (deliverable.getYear() == -1) {
          this.addMessage(action.getText("project.deliverable.generalInformation.year"));
          action.getInvalidFields().put("input-deliverable.year", InvalidFieldsMessages.EMPTYFIELD);
        }

        if (deliverable.getNewExpectedYear() != null && deliverable.getStatus() != null
          && deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
          && deliverable.getNewExpectedYear().intValue() == action.getCurrentCycleYear()) {
          this.addMessage(action.getText("project.deliverable.generalInformation.newewExpectedYear"));
          action.getInvalidFields().put("input-deliverable.newExpectedYear", InvalidFieldsMessages.EMPTYFIELD);
        }
        if (!action.isReportingActive()) {
          if (!(project.getProjecInfoPhase(action.getActualPhase()).getAdministrative() != null
            && project.getProjecInfoPhase(action.getActualPhase()).getAdministrative().booleanValue() == true)) {
            if (deliverable.getCrpClusterKeyOutput() != null) {
              if (deliverable.getCrpClusterKeyOutput().getId() == -1) {
                this.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
                action.getInvalidFields().put("input-deliverable.crpClusterKeyOutput.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            } else {
              this.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
              action.getInvalidFields().put("input-deliverable.crpClusterKeyOutput.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }


        if (deliverable.getResponsiblePartner() != null
          && deliverable.getResponsiblePartner().getProjectPartnerPerson() != null) {
          if (deliverable.getResponsiblePartner().getProjectPartnerPerson().getId() == null
            || deliverable.getResponsiblePartner().getProjectPartnerPerson().getId() == -1) {
            this.addMessage(action.getText("project.deliverable.generalInformation.partnerResponsible"));

            action.getInvalidFields().put("input-deliverable.responsiblePartner.projectPartnerPerson.id",
              InvalidFieldsMessages.EMPTYFIELD);

          } else {
            if (deliverable.getResponsiblePartner() != null) {


              if (projectPartnerPersonManager
                .getProjectPartnerPersonById(deliverable.getResponsiblePartner().getProjectPartnerPerson().getId())
                .getProjectPartner().getInstitution().getAcronym().equalsIgnoreCase("IFPRI")) {
                if (action.hasSpecificities(APConstants.CRP_DIVISION_FS)) {
                  if (deliverable.getResponsiblePartner().getPartnerDivision() == null) {
                    this.addMessage(action.getText("deliverable.division"));
                    action.getInvalidFields().put("input-deliverable.responsiblePartner.partnerDivision.id",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }
                  if (deliverable.getResponsiblePartner().getPartnerDivision() != null) {
                    if (deliverable.getResponsiblePartner().getPartnerDivision().getId() == null) {
                      this.addMessage(action.getText("deliverable.division"));
                      action.getInvalidFields().put("input-deliverable.responsiblePartner.partnerDivision.id",
                        InvalidFieldsMessages.EMPTYFIELD);
                    } else {
                      if (deliverable.getResponsiblePartner().getPartnerDivision().getId().longValue() == -1) {
                        this.addMessage(action.getText("deliverable.division"));
                        action.getInvalidFields().put("input-deliverable.responsiblePartner.partnerDivision.id",
                          InvalidFieldsMessages.EMPTYFIELD);
                      }
                    }

                  }

                }

              }
            }
          }
        } else {
          this.addMessage(action.getText("project.deliverable.generalInformation.partnerResponsible"));
          action.getInvalidFields().put("input-deliverable.responsiblePartner.projectPartnerPerson.id",
            InvalidFieldsMessages.EMPTYFIELD);

        }


        if (deliverable.getOtherPartners() != null) {
          int i = 0;
          for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners()) {
            if (deliverablePartnership != null && deliverablePartnership.getProjectPartnerPerson() != null
              && deliverablePartnership.getProjectPartnerPerson().getId() != null) {
              if (projectPartnerPersonManager
                .getProjectPartnerPersonById(deliverablePartnership.getProjectPartnerPerson().getId())
                .getProjectPartner().getInstitution().getAcronym().equalsIgnoreCase("IFPRI")) {
                if (action.hasSpecificities(APConstants.CRP_DIVISION_FS)) {
                  if (deliverablePartnership.getPartnerDivision() == null) {
                    this.addMessage(action.getText("deliverable.division"));
                    action.getInvalidFields().put("input-deliverable.otherPartners[" + i + "].partnerDivision.id",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }
                  if (deliverablePartnership.getPartnerDivision() != null) {
                    if (deliverablePartnership.getPartnerDivision().getId() == null) {
                      this.addMessage(action.getText("deliverable.division"));
                      action.getInvalidFields().put("input-deliverable.otherPartners[" + i + "].partnerDivision.id",
                        InvalidFieldsMessages.EMPTYFIELD);
                    } else {
                      if (deliverablePartnership.getPartnerDivision().getId().longValue() == -1) {
                        this.addMessage(action.getText("deliverable.division"));
                        action.getInvalidFields().put("input-deliverable.otherPartners[" + i + "].partnerDivision.id",
                          InvalidFieldsMessages.EMPTYFIELD);
                      }
                    }

                  }

                }

              }
            }

            i++;
          }
        }
        if (!action.isReportingActive()) {
          if (deliverable.getFundingSources() == null || deliverable.getFundingSources().isEmpty()) {
            this.addMessage(action.getText("project.deliverable.generalInformation.fundingSources"));
            action.getInvalidFields().put("list-deliverable.fundingSources",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Funding Sources"}));
          }
        }
        if (deliverable.getCrossCuttingGender() != null && deliverable.getCrossCuttingGender().booleanValue() == true) {

          if (deliverable.getGenderLevels() == null || deliverable.getGenderLevels().isEmpty()) {
            this.addMessage(action.getText("project.deliverable.generalInformation.genderLevels"));
            action.getInvalidFields().put("list-deliverable.genderLevels",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Gender Levels"}));
          }
        }
      }


      if (action.isReportingActive()) {


        if (deliverable.getStatus() != null
          && deliverable.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {

          this.addMessage(action.getText("project.deliverable.generalInformation.status"));
          action.getInvalidFields().put("input-deliverable.status", InvalidFieldsMessages.EMPTYFIELD);
        }


        // Deliverable Dissemination
        if (deliverable.getDissemination() != null) {
          this.validateDissemination(deliverable.getDissemination(), saving);
        } else {
          this.addMessage(action.getText("project.deliverable.dissemination.v.dissemination"));
          action.getInvalidFields().put("input-deliverable.dissemination.isOpenAccess",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Deliverable Meta-data Elements
        if (deliverable.getMetadataElements() != null) {
          // this.validateMetadata(deliverable.getMetadataElements());
        } else {
          this.addMessage(action.getText("project.deliverable.v.metadata"));
          action.getInvalidFields().put("input-deliverable.dissemination.isOpenAccess",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Deliverable Publication Meta-data
        if (deliverable.getDeliverableType() != null && deliverable.getDeliverableType().getDeliverableType() != null) {
          if (deliverable.getDeliverableType().getDeliverableType().getId() == 49) {
            this.validatePublicationMetadata(deliverable);
          }
        }

        // Deliverable Licenses
        if (deliverable.getAdoptedLicense() != null) {
          this.validateLicense(deliverable);
        } else {
          this.addMessage(action.getText("project.deliverable.v.ALicense"));
          action.getInvalidFields().put("input-deliverable.adoptedLicense", InvalidFieldsMessages.EMPTYFIELD);
        }

        // Deliverable Quality Check
        if (deliverable.getDeliverableType() != null && (deliverable.getDeliverableType().getId().intValue() == 51
          || deliverable.getDeliverableType().getId().intValue() == 74)) {
          if (deliverable.getQualityCheck() != null) {
            if (deliverable.getQualityCheck().getQualityAssurance() == null) {
              this.addMessage(action.getText("project.deliverable.v.qualityCheck.assurance"));
              action.getInvalidFields().put("input-deliverable.qualityCheck.qualityAssurance.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            if (deliverable.getQualityCheck().getDataDictionary() == null) {
              this.addMessage(action.getText("project.deliverable.v.qualityCheck.dictionary"));
              action.getInvalidFields().put("input-deliverable.qualityCheck.dataDictionary.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            if (deliverable.getQualityCheck().getDataTools() == null) {
              this.addMessage(action.getText("project.deliverable.v.qualityCheck.tool"));
              action.getInvalidFields().put("input-deliverable.qualityCheck.dataTools.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }

      }


    }


    if (!action.getFieldErrors().isEmpty()) {
      System.out.println(action.getFieldErrors());
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    if (action.isReportingActive()) {
      this.saveMissingFields(deliverable, APConstants.REPORTING, action.getReportingYear(),
        ProjectSectionStatusEnum.DELIVERABLES.getStatus());
    } else {
      this.saveMissingFields(deliverable, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.DELIVERABLES.getStatus());
    }
  }


  public void validateDissemination(DeliverableDissemination dissemination, boolean saving) {

    if (dissemination.getIsOpenAccess() != null) {

      if (!dissemination.getIsOpenAccess().booleanValue()) {


        if (saving) {
          if (dissemination.getType() == null) {


            this.addMessage(action.getText("project.deliverable.dissemination.v.openAccessRestriction"));
            action.getInvalidFields().put("input-deliverable.dissemination.type", InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (dissemination.getType().equals("restrictedUseAgreement")) {

              if (dissemination.getRestrictedAccessUntil() == null) {
                this.addMessage(action.getText("project.deliverable.dissemination.v.restrictedUseAgreement"));
                action.getInvalidFields().put("input-deliverable.dissemination.restrictedAccessUntil",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

            }

            if (dissemination.getType().equals("effectiveDateRestriction")) {

              if (dissemination.getRestrictedEmbargoed() == null) {
                this.addMessage(action.getText("project.deliverable.dissemination.v.restrictedEmbargoed"));
                action.getInvalidFields().put("input-deliverable.dissemination.restrictedEmbargoed",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

            }
          }
        } else {
          boolean hasOne = false;
          if (dissemination.getIntellectualProperty() != null
            && dissemination.getIntellectualProperty().booleanValue()) {
            hasOne = true;
          }
          if (dissemination.getLimitedExclusivity() != null && dissemination.getLimitedExclusivity().booleanValue()) {
            hasOne = true;
          }
          if (dissemination.getNotDisseminated() != null && dissemination.getNotDisseminated().booleanValue()) {
            hasOne = true;
          }
          if (dissemination.getRestrictedUseAgreement() != null
            && dissemination.getRestrictedUseAgreement().booleanValue()) {
            hasOne = true;
            if (dissemination.getRestrictedAccessUntil() == null) {
              this.addMessage(action.getText("project.deliverable.dissemination.v.restrictedUseAgreement"));
              action.getInvalidFields().put("input-deliverable.dissemination.restrictedAccessUntil",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
          if (dissemination.getEffectiveDateRestriction() != null
            && dissemination.getEffectiveDateRestriction().booleanValue()) {
            hasOne = true;
            if (dissemination.getRestrictedEmbargoed() == null) {
              this.addMessage(action.getText("project.deliverable.dissemination.v.restrictedEmbargoed"));
              action.getInvalidFields().put("input-deliverable.dissemination.restrictedEmbargoed",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }

          if (!hasOne) {
            this.addMessage(action.getText("project.deliverable.dissemination.v.openAccessRestriction"));
            action.getInvalidFields().put("input-deliverable.dissemination.type", InvalidFieldsMessages.EMPTYFIELD);
          }
        }


      }

    } else {
      this.addMessage(action.getText("project.deliverable.dissemination.v.isOpenAccess"));
      action.getInvalidFields().put("input-deliverable.dissemination.isOpenAccess", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (dissemination.getAlreadyDisseminated() != null) {
      if (dissemination.getAlreadyDisseminated().booleanValue()) {
        if (dissemination.getDisseminationChannel() != null) {
          if (dissemination.getDisseminationChannel().equals("-1")) {
            this.addMessage(action.getText("project.deliverable.dissemination.v.DisseminationChanel"));
            action.getInvalidFields().put("input-deliverable.dissemination.disseminationChannel",
              InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (!(this.isValidString(dissemination.getDisseminationUrl())
              && this.wordCount(dissemination.getDisseminationUrl()) <= 100)) {
              this.addMessage(action.getText("project.deliverable.dissemination.v.ChanelURL"));
              action.getInvalidFields().put("input-deliverable.dissemination.disseminationUrl",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        } else {
          this.addMessage(action.getText("project.deliverable.dissemination.v.DisseminationChanel"));
          action.getInvalidFields().put("input-deliverable.dissemination.disseminationChannel",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      this.addMessage(action.getText("project.deliverable.dissemination.v.alreadyDisseminated"));
      action.getInvalidFields().put("input-deliverable.dissemination.isOpenAccess", InvalidFieldsMessages.EMPTYFIELD);

    }
  }


  public void validateLicense(Deliverable deliverable) {
    if (deliverable.getAdoptedLicense().booleanValue()) {
      if (deliverable.getLicense() != null) {
        if (deliverable.getLicense().equals(LicensesTypeEnum.OTHER.getValue())) {
          if (deliverable.getOtherLicense() != null) {
            if (!(this.isValidString(deliverable.getOtherLicense())
              && this.wordCount(deliverable.getOtherLicense()) <= 100)) {
              this.addMessage(action.getText("project.deliverable.license.v.other"));
              action.getInvalidFields().put("input-deliverable.otherLicense", InvalidFieldsMessages.EMPTYFIELD);
            }

            if (deliverable.getAllowModifications() == null) {
              this.addMessage(action.getText("project.deliverable.license.v.allowModification"));
              action.getInvalidFields().put("input-deliverable.dissemination.allowModification",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        }
      } else {
        this.addMessage(action.getText("project.deliverable.v.license"));
        action.getInvalidFields().put("input-deliverable.license", InvalidFieldsMessages.EMPTYFIELD);
      }
    }
  }

  public void validateMetadata(List<DeliverableMetadataElement> elements) {

    boolean description = false;


    for (DeliverableMetadataElement deliverableMetadataElement : elements) {
      if (deliverableMetadataElement != null) {
        if (deliverableMetadataElement.getMetadataElement().getId() != null) {
          switch (deliverableMetadataElement.getMetadataElement().getId()) {
            case 8:
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
      this.addMessage(action.getText("project.deliverable.metadata.v.description"));
      action.getInvalidFields().put("input-deliverable.metadataElements[7].elementValue",
        InvalidFieldsMessages.EMPTYFIELD);
    }


  }

  public void validatePublicationMetadata(Deliverable deliverable) {

    if (deliverable.getPublication() != null && deliverable.getPublication().getId() != null
      && deliverable.getPublication().getId().intValue() != -1) {

      DeliverablePublicationMetadata metadata = deliverable.getPublication();

      if (!(this.isValidString(metadata.getVolume()) && this.wordCount(metadata.getVolume()) <= 100)) {
        // this.addMessage(action.getText("project.deliverable.publication.v.volume"));
        // action.getInvalidFields().put("input-deliverable.publication.volume", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!(this.isValidString(metadata.getJournal()) && this.wordCount(metadata.getJournal()) <= 100)) {
        this.addMessage(action.getText("project.deliverable.publication.v.journal"));
        action.getInvalidFields().put("input-deliverable.publication.journal", InvalidFieldsMessages.EMPTYFIELD);
      }

      boolean indicators = false;

      if (deliverable.getDeliverableType().getId() != 63 || deliverable.getDeliverableType().getId() != 79) {
        indicators = true;
      }

      if (metadata.getIsiPublication() != null) {
        if (metadata.getIsiPublication().booleanValue()) {
          indicators = true;
        }
      }

      if (metadata.getNasr() != null) {
        if (metadata.getNasr().booleanValue()) {
          indicators = true;
        }
      }

      if (metadata.getCoAuthor() != null) {
        if (metadata.getCoAuthor().booleanValue()) {
          indicators = true;
        }
      }

      if (!indicators) {
        this.addMessage(action.getText("project.deliverable.publication.v.indicators"));
        action.getInvalidFields().put("input-deliverable.publication.nasr", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (metadata.getPublicationAcknowledge() == null) {
        this.addMessage(action.getText("project.deliverable.publication.v.allowPublication"));
        action.getInvalidFields().put("input-deliverable.publication.publicationAcknowledge",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      // else {
      // this.addMessage(action.getText("project.deliverable.v.publication"));
      // action.getInvalidFields().put("input-deliverable.publication.nasr", InvalidFieldsMessages.EMPTYFIELD);
      // }
    }
  }
}

