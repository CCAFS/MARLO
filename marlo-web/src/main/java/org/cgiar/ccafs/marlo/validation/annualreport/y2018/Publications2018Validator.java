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
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

/**
 * @author Andrés Valencia - CIAT/CCAFS
 */
@Named
public class Publications2018Validator extends BaseValidator {

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
        boolean tableComplete = false;
        SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
          "Reporting", 2019, false, "policies");

        if (sectionStatus == null) {
          tableComplete = true;
          // sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        } else

        if (sectionStatus != null && sectionStatus.getId() != 0 && sectionStatus.getMissingFields() != null
          && sectionStatus.getMissingFields().length() != 0) {
          if (sectionStatus.getMissingFields().contains("synthesis.AR2019Table2")) {
            sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
            tableComplete = true;
          } else {
            tableComplete = false;
          }
        } else {
          if (sectionStatus != null && sectionStatus.getId() != 0) {
            tableComplete = true;
            sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
          }
        }

        int count = 0;
        List<Deliverable> deliverables = new ArrayList<>();
        deliverables =
          deliverableManager.getPublicationsList(reportSynthesis.getLiaisonInstitution(), action.getActualPhase());
        if (deliverables != null && !deliverables.isEmpty()) {
          for (Deliverable deliverable : deliverables) {
            if (deliverable != null && deliverable.getId() != null) {
              sectionStatus = new SectionStatus();
              sectionStatus = this.sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(), "Reporting",
                action.getActualPhase().getYear(), false, "publications");

              if (sectionStatus != null && sectionStatus.getMissingFields() != null
                && sectionStatus.getMissingFields().length() != 0) {
                count++;
              }
            }
          }

          if (count > 0) {
            action.addMissingField("synthesis.AR2019Table6");
          }

          // Save Synthesis Flagship
          if (reportSynthesis.getLiaisonInstitution() != null
            && reportSynthesis.getLiaisonInstitution().getAcronym() != null && !action.isPMU()) {

            String sSynthesisFlagships = action.getSynthesisFlagships().toString();


            if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("1")) {
              if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
                if (!sSynthesisFlagships.contains("1")) {
                  action.addSynthesisFlagship("F1");
                }
              } else {
                action.addSynthesisFlagship("F1");
              }
            }
            if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("2")) {
              if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
                if (!sSynthesisFlagships.contains("2")) {
                  action.addSynthesisFlagship("F2");
                }
              } else {
                action.addSynthesisFlagship("F2");
              }
            }
            if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("3")) {
              if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
                if (!sSynthesisFlagships.contains("3")) {
                  action.addSynthesisFlagship("F3");
                }
              } else {
                action.addSynthesisFlagship("F3");
              }
            }
            if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("4")) {
              if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
                if (!sSynthesisFlagships.contains("4")) {
                  action.addSynthesisFlagship("F4");
                }
              } else {
                action.addSynthesisFlagship("F4");
              }
            }
            if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("5")) {
              if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
                if (!sSynthesisFlagships.contains("5")) {
                  action.addSynthesisFlagship("F5");
                }
              } else {
                action.addSynthesisFlagship("F5");
              }
            }
            if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("6")) {
              if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
                if (!sSynthesisFlagships.contains("6")) {
                  action.addSynthesisFlagship("F6");
                }
              } else {
                action.addSynthesisFlagship("F6");
              }
            }
            if (reportSynthesis.getLiaisonInstitution().getAcronym().contains("PMU")) {
              if (action.getSynthesisFlagships() != null && action.getSynthesisFlagships().toString().length() > 0) {
                if (!sSynthesisFlagships.contains("PMU")) {
                  action.addSynthesisFlagship("PMU");
                }
              } else {
                action.addSynthesisFlagship("PMU");
              }
            }
          }

        }
      }

      this.saveMissingFields(reportSynthesis, action.getActualPhase().getDescription(),
        action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
        ReportSynthesis2018SectionStatusEnum.PUBLICATIONS.getStatus(), action);
    }

  }

}