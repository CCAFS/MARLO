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

package org.cgiar.ccafs.marlo.action.summaries;

import org.cgiar.ccafs.marlo.data.manager.CrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingDimensions;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.dto.CrossCuttingDimensionTableDTO;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */

public class POWBSummaryAction extends BaseSummariesAction implements Summary {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(POWBSummaryAction.class);

  // Parameters
  private int year;
  private long startTime;
  private List<PowbSynthesis> powbSynthesisList;

  // Managers
  private CrossCuttingDimensionManager crossCuttingManager;

  // RTF bytes
  private byte[] bytesRTF;
  // Streams
  InputStream inputStream;

  @Inject
  public POWBSummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    CrossCuttingDimensionManager crossCuttingManager) {
    super(config, crpManager, phaseManager);
    this.crossCuttingManager = crossCuttingManager;
  }


  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    // masterReport.getParameterValues().put("i8nParameterName", "i8nText"));
    masterReport.getParameterValues().put("i8nPlansCRPFlagshipTitle",
      this.getText("summaries.powb.synthesis.flagshipPlans.title"));

    return masterReport;
  }

  @Override
  public String execute() throws Exception {
    ClassicEngineBoot.getInstance().start();
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    try {
      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/crp/POWBTemplate.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "Main_Query";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel();
      sdf.addTable(masterQueryName, model);
      masterReport.setDataFactory(cdf);
      // Set i8n for pentaho
      masterReport = this.addi8nParameters(masterReport);
      // Get details band
      ItemBand masteritemBand = masterReport.getItemBand();
      // Create new empty subreport hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the subreports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // TODO: Complete POWB subreports
      this.fillSubreport((SubReport) hm.get("ExpectedKeyResults"), "ExpectedKeyResults");
      // this.fillSubreport((SubReport) hm.get("EffectivenessandEfficiency"), "EffectivenessandEfficiency");
      // this.fillSubreport((SubReport) hm.get("CRPManagement"), "CRPManagement");
      // // Table A
      // this.fillSubreport((SubReport) hm.get("PlannedMilestones"), "PlannedMilestones");
      // this.fillSubreport((SubReport) hm.get("TableAContent"), "TableAContent");
      // // Table B
      // this.fillSubreport((SubReport) hm.get("PlannedStudies"), "PlannedStudies");
      // this.fillSubreport((SubReport) hm.get("TableBContent"), "TableBContent");
      // // Table C
      this.fillSubreport((SubReport) hm.get("Crosscutting"), "Crosscutting");
      this.fillSubreport((SubReport) hm.get("TableCContent"), "TableCContent");
      // // Table D
      // this.fillSubreport((SubReport) hm.get("CRPStaffing"), "CRPStaffing");
      // this.fillSubreport((SubReport) hm.get("TableDContent"), "TableDContent");
      // // Table E
      // this.fillSubreport((SubReport) hm.get("CRPPlannedBudget"), "CRPPlannedBudget");
      // this.fillSubreport((SubReport) hm.get("TableEContent"), "TableEContent");
      // // Table F
      // this.fillSubreport((SubReport) hm.get("MainAreas"), "MainAreas");
      // // Table G
      // this.fillSubreport((SubReport) hm.get("CGIARCollaborations"), "CGIARCollaborations");
      // this.fillSubreport((SubReport) hm.get("TableGContent"), "TableGContent");
      // // Table H
      // this.fillSubreport((SubReport) hm.get("PlannedMonitoring"), "PlannedMonitoring");
      // this.fillSubreport((SubReport) hm.get("TableHContent"), "TableHContent");

      RTFReportUtil.createRTF(masterReport, os);
      bytesRTF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating POWB Summary " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: "
      + this.getSelectedCycle() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }

  /**
   * @param subReport Subreport to fill
   * @param query Name of the query in Pentaho Report Data Set
   */
  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "ExpectedKeyResults":
        model = this.getExpectedKeyResultsTableModel();
        break;
      case "EffectivenessandEfficiency":
        model = this.getEffectivenessandEfficiencyTableModel();
        break;
      case "CRPManagement":
        model = this.getCRPManagementTableModel();
        break;
      // Table A
      case "PlannedMilestones":
        model = this.getPlannedMilestonesTableModel();
        break;
      case "TableAContent":
        model = this.getTableAContentTableModel();
        break;
      // Table B
      case "PlannedStudies":
        model = this.getPlannedStudiesTableModel();
        break;
      case "TableBContent":
        model = this.getTableBContentTableModel();
        break;
      // Table C
      case "Crosscutting":
        model = this.getCrossCuttingTableModel();
        break;
      case "TableCContent":
        model = this.getTableCContentTableModel();
        break;
      // Table D
      case "CRPStaffing":
        model = this.getCRPStaffingTableModel();
        break;
      case "TableDContent":
        model = this.getTableDContentTableModel();
        break;
      // Table E
      case "CRPPlannedBudget":
        model = this.getCRPPlannedBudgetTableModel();
        break;
      case "TableEContent":
        model = this.getTableEContentTableModel();
        break;
      // Table F
      case "MainAreas":
        model = this.getMainAreasTableModel();
        break;
      // Table G
      case "CGIARCollaborations":
        model = this.getCGIARCollaborationsTableModel();
        break;
      case "TableGContent":
        model = this.getTableGContentTableModel();
        break;
      // Table H
      case "PlannedMonitoring":
        model = this.getPlannedMonitoringTableModel();
        break;
      case "TableHContent":
        model = this.getTableHContentTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  private TypedTableModel getCGIARCollaborationsTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableGDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {"Text", "Text"});
    return model;
  }


  @Override
  public int getContentLength() {
    return bytesRTF.length;
  }


  @Override
  public String getContentType() {
    return "application/rtf";
  }


  private TypedTableModel getCrossCuttingTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableCDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {"Text"});
    return model;
  }


  private TypedTableModel getCRPManagementTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"managementRisksTitleDescription", "CRPManagementGovernanceDescription"},
        new Class[] {String.class, String.class}, 0);

    model.addRow(new Object[] {"Text", "Text"});
    return model;
  }


  private TypedTableModel getCRPPlannedBudgetTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableEDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {"Text"});
    return model;
  }


  private TypedTableModel getCRPStaffingTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableDDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {"Text"});
    return model;
  }


  private TypedTableModel getEffectivenessandEfficiencyTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"staffingDescription", "financialPlanDescription", "newKeyExternalPartnershipsDescription",
        "newContributionPlatformsDescription", "newCrossCRPInteractionsDescription",
        "expectedEffortsCountryCoordinationDescription", "monitoringEvaluationLearningDescription"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class},
      0);

    model.addRow(new Object[] {"Text", "Text", "Text", "Text", "Text", "Text", "Text"});
    return model;
  }


  private TypedTableModel getExpectedKeyResultsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"unitName", "leadCenter", "participantingCenters", "adjustmentsDescription",
        "expectedCrpDescription", "evidenceDescription", "plansCRPFlagshipDescription", "crossCuttingGenderDescription",
        "crossCuttingOpenDataDescription"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class},
      0);
    String unitName = "", leadCenter = "", participantingCenters = "", adjustmentsDescription = "",
      expectedCrpDescription = "", evidenceDescription = "", plansCRPFlagshipDescription = "",
      crossCuttingGenderDescription = "", crossCuttingOpenDataDescription = "";

    unitName = this.getLoggedCrp().getAcronym() != null && !this.getLoggedCrp().getAcronym().isEmpty()
      ? this.getLoggedCrp().getAcronym() : this.getLoggedCrp().getName();


    LiaisonInstitution PMU = null;

    // get the PMU institution
    for (LiaisonInstitution institution : this.getLoggedCrp().getLiaisonInstitutions()) {
      if (this.isPMU(institution)) {
        PMU = institution;
        break;
      }
    }

    if (powbSynthesisList != null && !powbSynthesisList.isEmpty()) {
      for (PowbSynthesis powbSynthesis : powbSynthesisList) {
        plansCRPFlagshipDescription =
          this.getPowbSynthesisFlagshipPlanSummary(powbSynthesis, plansCRPFlagshipDescription);


        // Cross Cutting Dimensions Info
        LiaisonInstitution institution = powbSynthesis.getLiaisonInstitution();
        Phase phase = powbSynthesis.getPhase();

        if (institution != null && phase != null) {
          if (phase.equals(this.getSelectedPhase())) {
            if (institution.getId() == PMU.getId()) {
              CrossCuttingDimensions crossCutting = powbSynthesis.getCrossCutting();
              crossCuttingGenderDescription = crossCutting.getSummarize();
              crossCuttingOpenDataDescription = crossCutting.getAssets();
            }
          }
        }


      }
    }
    model.addRow(new Object[] {unitName, leadCenter, participantingCenters, adjustmentsDescription,
      expectedCrpDescription, evidenceDescription, plansCRPFlagshipDescription, crossCuttingGenderDescription,
      crossCuttingOpenDataDescription});
    return model;
  }


  @SuppressWarnings("unused")
  private File getFile(String fileName) {
    // Get file from resources folder
    ClassLoader classLoader = this.getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());
    return file;
  }


  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("POWBSummary-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".rtf");
    return fileName.toString();
  }


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesRTF);
    }
    return inputStream;
  }


  private TypedTableModel getMainAreasTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"expenditureArea", "estimatedPercentajeFS", "commentsSpace"},
        new Class[] {String.class, Double.class, String.class}, 0);
    for (int i = 0; i < 5; i++) {
      model.addRow(new Object[] {"Text", 0, "Text"});
    }
    return model;
  }


  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"unit", "year"}, new Class[] {String.class, Integer.class});
    model.addRow(new Object[] {this.getLoggedCrp().getAcronym(), this.getSelectedYear()});
    return model;
  }


  private TypedTableModel getPlannedMilestonesTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableADescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {"Text"});
    return model;
  }


  private TypedTableModel getPlannedMonitoringTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableHDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {"Text"});
    return model;
  }


  private TypedTableModel getPlannedStudiesTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"tableBDescription"}, new Class[] {String.class}, 0);

    model.addRow(new Object[] {"Text"});
    return model;
  }


  private String getPowbSynthesisFlagshipPlanSummary(PowbSynthesis powbSynthesis, String plansCRPFlagshipDescription) {
    if (powbSynthesis.getPowbFlagshipPlans() != null) {
      if (powbSynthesis.getPowbFlagshipPlans().getPlanSummary() != null) {
        if (plansCRPFlagshipDescription.isEmpty()) {
          plansCRPFlagshipDescription = powbSynthesis.getLiaisonInstitution().getAcronym() + ": "
            + powbSynthesis.getPowbFlagshipPlans().getPlanSummary();
        } else {
          plansCRPFlagshipDescription += "<br>" + powbSynthesis.getLiaisonInstitution().getAcronym() + ": "
            + powbSynthesis.getPowbFlagshipPlans().getPlanSummary();
        }
      }
    }
    return plansCRPFlagshipDescription;
  }


  private TypedTableModel getTableAContentTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"FP", "subIDO", "outcomes", "milestone", "w1w2", "w3Bilateral", "assessment", "meansVerifications"},
      new Class[] {String.class, String.class, String.class, String.class, Double.class, Double.class, String.class,
        String.class},
      0);
    for (int i = 0; i < 5; i++) {
      model.addRow(new Object[] {"Text", "Text", "Text", "Text", 0, 0, "Text", "Text"});
    }
    return model;
  }


  private TypedTableModel getTableBContentTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"plannedStudy", "geographicScope", "revelantSubIDO", "comments"},
        new Class[] {String.class, String.class, String.class, String.class}, 0);
    for (int i = 0; i < 5; i++) {
      model.addRow(new Object[] {"Text", "Text", "Text", "Text"});
    }
    return model;
  }

  private TypedTableModel getTableCContentTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"Scored2Gender", "Scored2Youth", "Scored2CapDev", "scored1Gender", "scored1Youth", "scored1CapDev",
        "scored0Gender", "scored0Youth", "scored0CapDev", "totalOverallOutputs"},
      new Class[] {Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class,
        Double.class, Double.class, Double.class},
      0);

    LiaisonInstitution PMU = null;

    // get the PMU institution
    for (LiaisonInstitution institution : this.getLoggedCrp().getLiaisonInstitutions()) {
      if (this.isPMU(institution)) {
        PMU = institution;
        break;
      }
    }


    if (powbSynthesisList != null && !powbSynthesisList.isEmpty()) {
      for (PowbSynthesis powbSynthesis : powbSynthesisList) {
        LiaisonInstitution institution = powbSynthesis.getLiaisonInstitution();
        Phase phase = powbSynthesis.getPhase();

        if (institution != null && phase != null) {

          if (phase.equals(this.getSelectedPhase())) {

            if (institution.getId() == PMU.getId()) {
              CrossCuttingDimensionTableDTO tableC =
                this.crossCuttingManager.loadTableByLiaisonAndPhase(institution.getId(), phase.getId());

              if (tableC != null) {
                model.addRow(
                  new Object[] {tableC.getPercentageGenderPrincipal() / 100, tableC.getPercentageYouthPrincipal() / 100,
                    tableC.getPercentageCapDevPrincipal() / 100, tableC.getPercentageGenderSignificant() / 100,
                    tableC.getPercentageYouthSignificant() / 100, tableC.getPercentageCapDevSignificant() / 100,
                    tableC.getPercentageGenderNotScored() / 100, tableC.getPercentageYouthNotScored() / 100,
                    tableC.getPercentageCapDevNotScored() / 100, tableC.getTotal()});
              }

            }


          }


        }


      }
    }


    return model;
  }

  private TypedTableModel getTableDContentTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"category", "female", "totalFTE", "FemalePercentaje", "male"},
        new Class[] {String.class, Double.class, Double.class, Double.class, Double.class}, 0);
    for (int i = 0; i < 5; i++) {
      model.addRow(new Object[] {"Text", 0, 0, 0, 0});
    }
    return model;
  }

  private TypedTableModel getTableEContentTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"category", "w1w2", "w3Bilateral", "total", "comments"},
      new Class[] {String.class, Double.class, Double.class, Double.class, String.class}, 0);
    for (int i = 0; i < 5; i++) {
      model.addRow(new Object[] {"Text", 0, 0, 0, "Text"});
    }
    return model;
  }

  private TypedTableModel getTableGContentTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"crpPlatform", "descriptionCollaboration", "relevantFP"},
      new Class[] {String.class, String.class, String.class}, 0);
    for (int i = 0; i < 5; i++) {
      model.addRow(new Object[] {"Text", "Text", "Text"});
    }

    return model;
  }

  private TypedTableModel getTableHContentTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"plannedStudiesLearning", "comments"},
      new Class[] {String.class, String.class}, 0);
    for (int i = 0; i < 5; i++) {
      model.addRow(new Object[] {"Text", "Text"});
    }
    return model;
  }

  public int getYear() {
    return year;
  }

  public boolean isPMU(LiaisonInstitution institution) {
    if (institution.getAcronym().equals("PMU")) {
      return true;
    }

    return false;
  }

  @Override
  public void prepare() {
    this.setGeneralParameters();
    powbSynthesisList =
      this.getSelectedPhase().getPowbSynthesis().stream().filter(ps -> ps.isActive()).collect(Collectors.toList());
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

}