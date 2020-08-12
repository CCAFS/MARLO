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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Expenditures;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseExpendituryAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseSummaryManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewW1W2ExpenditureDTO;
import org.cgiar.ccafs.marlo.rest.dto.W1W2ExpenditureDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ExpenditureMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import com.opensymphony.xwork2.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ExpendituresItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFundingUseSummaryManager reportSynthesisFundingUseSummaryManager;
  private ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private ExpenditureMapper expenditureMapper;

  @Inject
  public ExpendituresItem(PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFundingUseSummaryManager reportSynthesisFundingUseSummaryManager,
    PowbExpenditureAreasManager powbExpenditureAreasManager,
    ReportSynthesisFundingUseExpendituryAreaManager reportSynthesisFundingUseExpendituryAreaManager,
    ExpenditureMapper expenditureMapper) {

    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFundingUseSummaryManager = reportSynthesisFundingUseSummaryManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.reportSynthesisFundingUseExpendituryAreaManager = reportSynthesisFundingUseExpendituryAreaManager;
    this.expenditureMapper = expenditureMapper;
  }

  public Long createExpenditure(NewW1W2ExpenditureDTO expenditure, String entityAcronym, User user) {
    Long expenditureExampleID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createExpenditure", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream()
        .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
          && c.getYear() == expenditure.getPhase().getYear()
          && c.getName().equalsIgnoreCase(expenditure.getPhase().getName()))
        .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createExpenditure", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createExpenditure", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          reportSynthesis.setPhase(phase);
          reportSynthesis.setLiaisonInstitution(liaisonInstitution);
          reportSynthesis = this.reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }
        ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary =
          reportSynthesisFundingUseSummaryManager.getReportSynthesisFundingUseSummaryById(reportSynthesis.getId());
        if (reportSynthesisFundingUseSummary == null) {
          reportSynthesisFundingUseSummary = new ReportSynthesisFundingUseSummary();
          reportSynthesisFundingUseSummary.setReportSynthesis(reportSynthesis);
          reportSynthesisFundingUseSummary.setCreatedBy(user);
          reportSynthesisFundingUseSummary.setInterestingPoints("");
          reportSynthesisFundingUseSummaryManager
            .saveReportSynthesisFundingUseSummary(reportSynthesisFundingUseSummary);
        }

        ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea =
          new ReportSynthesisFundingUseExpendituryArea();
        reportSynthesisFundingUseExpendituryArea.setExampleExpenditure(expenditure.getExampleExpenditure());
        reportSynthesisFundingUseExpendituryArea.setCreatedBy(user);
        PowbExpenditureAreas area =
          powbExpenditureAreasManager.getPowbExpenditureAreasById(expenditure.getExpenditureAreaID());
        if (area == null) {
          fieldErrors.add(new FieldErrorDTO("createExpenditure", "ExpenditureArea", "invalid Area identifier"));
        } else {
          reportSynthesisFundingUseExpendituryArea.setExpenditureArea(area);
        }
        if (fieldErrors.isEmpty()) {
          reportSynthesisFundingUseExpendituryArea = reportSynthesisFundingUseExpendituryAreaManager
            .saveReportSynthesisFundingUseExpendituryArea(reportSynthesisFundingUseExpendituryArea);
          if (reportSynthesisFundingUseExpendituryArea != null) {
            expenditureExampleID = reportSynthesisFundingUseExpendituryArea.getId();
          }
        }
      }
    }
    return expenditureExampleID;
  }

  public Long deleteExpenditure(Long id, String entityAcronym, int year, String stringPhase, User user) {
    Long expenditureExampleID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createExpenditure", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == year && c.getName().equalsIgnoreCase(stringPhase)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createExpenditure", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createExpenditure", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("createExpenditure", "ReportSynthesis", "can not find report synthesis"));
        } else {
          ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary =
            reportSynthesisFundingUseSummaryManager.getReportSynthesisFundingUseSummaryById(reportSynthesis.getId());
          if (reportSynthesisFundingUseSummary == null) {
            fieldErrors.add(new FieldErrorDTO("createExpenditure", "ReportSynthesisFundingUsage",
              "can not find report synthesis funding usage"));
          } else {
            if (reportSynthesisFundingUseSummary.getReportSynthesisFundingUseExpendituryAreas().stream()
              .filter(c -> c.isActive() && c.getId().longValue() == id.longValue())
              .collect(Collectors.toList()) != null) {
              ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea =
                reportSynthesisFundingUseExpendituryAreaManager.getReportSynthesisFundingUseExpendituryAreaById(id);
              if (reportSynthesisFundingUseExpendituryArea != null) {
                expenditureExampleID = reportSynthesisFundingUseExpendituryArea.getId();
                reportSynthesisFundingUseExpendituryAreaManager
                  .deleteReportSynthesisFundingUseExpendituryArea(reportSynthesisFundingUseExpendituryArea.getId());
              }
            }
          }
        }
      }
    }
    return expenditureExampleID;
  }

  public ResponseEntity<W1W2ExpenditureDTO> getExpenditure(Long id, String entityAcronym, int year, String stringPhase,
    User user) {
    ResponseEntity<W1W2ExpenditureDTO> expenditure = null;
    ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createExpenditure", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == year && c.getName().equalsIgnoreCase(stringPhase)).findFirst().orElse(null);

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createExpenditure", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createExpenditure", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("createExpenditure", "ReportSynthesis", "can not find report synthesis"));
        } else {
          ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary =
            reportSynthesisFundingUseSummaryManager.getReportSynthesisFundingUseSummaryById(reportSynthesis.getId());
          if (reportSynthesisFundingUseSummary == null) {
            fieldErrors.add(new FieldErrorDTO("createExpenditure", "ReportSynthesisFundingUsage",
              "can not find report synthesis funding usage"));
          } else {
            reportSynthesisFundingUseExpendituryArea =
              reportSynthesisFundingUseExpendituryAreaManager.getReportSynthesisFundingUseExpendituryAreaById(id);
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
    expenditure = Optional.ofNullable(reportSynthesisFundingUseExpendituryArea)
      .map(this.expenditureMapper::reportSynthesisFundingUseExpendituryAreaToW1W2ExpenditureDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    return expenditure;
  }

  public Long updateExpenditure(Long id, NewW1W2ExpenditureDTO expenditure, String entityAcronym, User user) {
    Long expenditureExampleID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("updateExpenditure", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream()
        .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
          && c.getYear() == expenditure.getPhase().getYear()
          && c.getName().equalsIgnoreCase(expenditure.getPhase().getName()))
        .findFirst().orElse(null);

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("updateExpenditure", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("updateExpenditure", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("createExpenditure", "ReportSynthesis", "can not find report synthesis"));
        } else {
          ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary =
            reportSynthesisFundingUseSummaryManager.getReportSynthesisFundingUseSummaryById(reportSynthesis.getId());
          if (reportSynthesisFundingUseSummary == null) {
            fieldErrors.add(new FieldErrorDTO("updateExpenditure", "ReportSynthesisFundingUsage",
              "can not find report synthesis funding usage"));
          } else {
            if (reportSynthesisFundingUseSummary.getReportSynthesisFundingUseExpendituryAreas().stream()
              .filter(c -> c.isActive() && c.getId().longValue() == id.longValue())
              .collect(Collectors.toList()) != null) {
              PowbExpenditureAreas area =
                powbExpenditureAreasManager.getPowbExpenditureAreasById(expenditure.getExpenditureAreaID());
              if (area == null) {
                fieldErrors.add(new FieldErrorDTO("updateExpenditure", "ExpenditureArea", "invalid Area identifier"));
              } else {
                ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea =
                  reportSynthesisFundingUseExpendituryAreaManager.getReportSynthesisFundingUseExpendituryAreaById(id);
                if (reportSynthesisFundingUseExpendituryArea != null) {
                  expenditureExampleID = reportSynthesisFundingUseExpendituryArea.getId();
                  reportSynthesisFundingUseExpendituryArea.setModifiedBy(user);
                  reportSynthesisFundingUseExpendituryArea.setExampleExpenditure(expenditure.getExampleExpenditure());
                  reportSynthesisFundingUseExpendituryArea.setExpenditureArea(area);
                  reportSynthesisFundingUseExpendituryAreaManager
                    .saveReportSynthesisFundingUseExpendituryArea(reportSynthesisFundingUseExpendituryArea);
                }
              }
            }
          }
        }
      }
    }
    return expenditureExampleID;
  }

}
