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

package org.cgiar.ccafs.marlo.validation.annualreport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class Publications2018Validator extends BaseValidator {

  private static Logger LOG = LoggerFactory.getLogger(Publications2018Validator.class);

  private final GlobalUnitManager crpManager;
  private final ReportSynthesisManager reportSynthesisManager;
  private final SectionStatusManager sectionStatusManager;
  private final DeliverableManager deliverableManager;

  public Publications2018Validator(GlobalUnitManager crpManager, ReportSynthesisManager reportSynthesisManager,
    SectionStatusManager sectionStatusManager, DeliverableManager deliverableManager) {
    this.crpManager = crpManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.sectionStatusManager = sectionStatusManager;
    this.deliverableManager = deliverableManager;
  }


  private Path getAutoSaveFilePath(ReportSynthesis reportSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = reportSynthesis.getClass().getSimpleName();
    String actionFile = ReportSynthesis2018SectionStatusEnum.PUBLICATIONS.getStatus().replace("/", "_");
    String autoSaveFile =
      reportSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName() + "_"
        + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public LiaisonInstitution getLiaisonInstitution(BaseAction action, long synthesisID) {
    ReportSynthesis reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    LiaisonInstitution liaisonInstitution = reportSynthesis.getLiaisonInstitution();
    return liaisonInstitution;
  }

  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }


  public void validate(BaseAction action, ReportSynthesis reportSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    List<String> emptyFields = new ArrayList<>();
    if (reportSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(reportSynthesis, action.getCrpID(), action);
        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {

        List<Deliverable> deliverablesTable6 = null;
        if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
          // Get mark policies list

          deliverablesTable6 = new ArrayList<>(
            deliverableManager.getPublicationsList(reportSynthesis.getLiaisonInstitution(), action.getActualPhase()));

          if (reportSynthesis != null && reportSynthesis.getReportSynthesisFlagshipProgress() != null
            && reportSynthesis.getReportSynthesisFlagshipProgress()
              .getReportSynthesisFlagshipProgressDeliverables() != null
            && !reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables()
              .isEmpty()) {
            for (ReportSynthesisFlagshipProgressDeliverable flagshipProgressDeliverable : reportSynthesis
              .getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressDeliverables().stream()
              .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
              deliverablesTable6.remove(flagshipProgressDeliverable.getDeliverable());
            }
          }
        }

        boolean tableComplete = false;
        SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
          "Reporting", 2019, false, "publications");

        if (sectionStatus == null) {
          tableComplete = true;
          // sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        } else

        if (sectionStatus != null && sectionStatus.getId() != 0 && sectionStatus.getMissingFields() != null
          && sectionStatus.getMissingFields().length() != 0) {
          if (sectionStatus.getMissingFields().contains("synthesis.AR2019Table6") && sectionStatus.getId() != null
            && sectionStatus.getId() != 0) {
            // sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
            sectionStatus.setMissingFields("");
            tableComplete = true;
          } else {
            tableComplete = false;
          }
        } else {
          if (sectionStatus != null && sectionStatus.getId() != 0) {
            tableComplete = true;
            // sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
            sectionStatus.setMissingFields("");
          }
        }

        int count = 0;
        List<Deliverable> deliverables = new ArrayList<>();
        if (deliverablesTable6 != null) {
          deliverables = deliverablesTable6;
        } else {
          deliverables =
            deliverableManager.getPublicationsList(reportSynthesis.getLiaisonInstitution(), action.getActualPhase());
        }
        if (deliverables != null && !deliverables.isEmpty()) {
          for (Deliverable deliverable : deliverables) {
            if (deliverable != null && deliverable.getId() != null) {

              // Validate Specific fields
              deliverable = deliverableManager.getDeliverableById(deliverable.getId());
              int countB = 0;
              int count2 = 0;
              if (deliverable != null) {
                deliverable.setCrps(deliverable.getDeliverableCrps().stream()
                  .filter(c -> c.isActive() && c.getPhase().equals(action.getActualPhase()))
                  .collect(Collectors.toList()));
                if (deliverable.getCrps() == null || deliverable.getCrps().isEmpty()) {
                  emptyFields.add("CRP");
                  countB++;
                }

                if (deliverable.getPublication() != null) {

                  // Is publication
                  if (deliverable.getPublication().getIsiPublication() == null) {
                    emptyFields.add("Is publication");
                    countB++;
                  }

                  // Issue - Page - Volume
                  if (deliverable.getPublication().getVolume() == null
                    || deliverable.getPublication().getVolume().isEmpty()) {
                    count2++;
                  }
                  if (deliverable.getPublication().getPages() == null
                    || deliverable.getPublication().getPages().isEmpty()) {
                    count2++;
                  }
                  if (deliverable.getPublication().getIssue() == null
                    || deliverable.getPublication().getIssue().isEmpty()) {
                    count2++;
                  }
                }
                if (count2 == 3) {
                  emptyFields.add("Issue/Pages/Volume");
                  countB++;
                }

                // Authors
                if (deliverable.getUsers() == null || deliverable.getUsers().isEmpty()) {
                  emptyFields.add("Authors");
                  countB++;
                }

                if (deliverable.getMetadata() != null) {

                  // Unique identifier (DOI)
                  if (deliverable.getMetadataValue(36) == null || deliverable.getMetadataValue(36).isEmpty()) {
                    emptyFields.add("DOI");
                    countB++;
                  }
                  // Date of Publication
                  if (deliverable.getMetadataValue(17) == null || deliverable.getMetadataValue(36).isEmpty()) {
                    emptyFields.add("Date of Publication");
                    countB++;
                  }
                  // Article Title
                  if (deliverable.getMetadataValue(1) == null || deliverable.getMetadataValue(36).isEmpty()) {
                    emptyFields.add("Article Title");
                    countB++;
                  }
                }
              }

              if (countB > 0) {
                emptyFields.add("ID:" + deliverable.getId());
                count++;
              }

            }
          }

          if (count > 0) {
            if (action.getMissingFields() != null && action.getMissingFields().length() > 0
              && !action.getMissingFields().toString().isEmpty()) {
              if (!action.getMissingFields().toString().contains("synthesis.AR2019Table6")) {
                action.addMissingField("synthesis.AR2019Table6");
              }
            } else {
              action.addMissingField("synthesis.AR2019Table6");
            }

          }
        }
      }
      try {
        this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
          action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
          ReportSynthesis2018SectionStatusEnum.PUBLICATIONS.getStatus(), action);
      } catch (Exception e) {
        LOG.error("Error getting publications validator: " + e.getMessage());
      }
    }

  }

}