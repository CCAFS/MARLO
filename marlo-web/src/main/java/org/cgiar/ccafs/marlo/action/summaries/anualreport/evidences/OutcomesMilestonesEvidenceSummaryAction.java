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

package org.cgiar.ccafs.marlo.action.summaries.anualreport.evidences;

import org.cgiar.ccafs.marlo.action.summaries.BaseSummariesAction;
import org.cgiar.ccafs.marlo.action.summaries.Summary;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.anualreport.evidences.AROutcomeMilestoneEvidence;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action prepare and create the Project Policies evidences to fill the part C of the annual report document.
 * == NOTE : this report works only for annual report 2018 and later phases ==
 * 
 * @author Hermes Jimenez - CIAT/CCAFS
 */
public class OutcomesMilestonesEvidenceSummaryAction extends BaseSummariesAction implements Summary {


  private static final long serialVersionUID = -2543743013961311798L;
  private static Logger LOG = LoggerFactory.getLogger(OutcomesMilestonesEvidenceSummaryAction.class);
  // Managers
  private final ResourceManager resourceManager;

  private final ReportSynthesisManager reportSynthesisManager;
  private final ProjectPolicyManager projectPolicyManager;


  // Parameters
  private long startTime;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public OutcomesMilestonesEvidenceSummaryAction(APConfig config, GlobalUnitManager crpManager,
    PhaseManager phaseManager, ResourceManager resourceManager, ProjectManager projectManager,
    ReportSynthesisManager reportSynthesisManager, ProjectPolicyManager projectPolicyManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.projectPolicyManager = projectPolicyManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * the order of the parameters is the same order for the getOutcomeMilestoneReportingTableModel() method
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {

    masterReport.getParameterValues().put("i8nColumnA", this.getText("outcomesMilestones.table.id"));
    masterReport.getParameterValues().put("i8nColumnB", this.getText("outcomesMilestones.table.fp"));
    masterReport.getParameterValues().put("i8nColumnC", this.getText("outcomesMilestones.table.outcome"));
    masterReport.getParameterValues().put("i8nColumnD", this.getText("outcomesMilestones.table.outcomeProgress"));
    masterReport.getParameterValues().put("i8nColumnE", this.getText("outcomesMilestones.table.milestone"));
    masterReport.getParameterValues().put("i8nColumnF", this.getText("outcomesMilestones.table.status"));
    masterReport.getParameterValues().put("i8nColumnG", this.getText("outcomesMilestones.table.statusPredominant"));
    masterReport.getParameterValues().put("i8nColumnH", this.getText("outcomesMilestones.table.milestoneEvidence"));
    masterReport.getParameterValues().put("i8nColumnI", this.getText("outcomesMilestones.table.gender"));
    masterReport.getParameterValues().put("i8nColumnJ", this.getText("outcomesMilestones.table.genderJustification"));
    masterReport.getParameterValues().put("i8nColumnK", this.getText("outcomesMilestones.table.youth"));
    masterReport.getParameterValues().put("i8nColumnL", this.getText("outcomesMilestones.table.youthJutification"));
    masterReport.getParameterValues().put("i8nColumnM", this.getText("outcomesMilestones.table.capdev"));
    masterReport.getParameterValues().put("i8nColumnN", this.getText("outcomesMilestones.table.capdevJustification"));
    masterReport.getParameterValues().put("i8nColumnO", this.getText("outcomesMilestones.table.climateChange"));
    masterReport.getParameterValues().put("i8nColumnP",
      this.getText("outcomesMilestones.table.climateChangeJustification"));
    masterReport.getParameterValues().put("i8nHeader", this.getText("outcomesMilestones.table.header"));

    return masterReport;
  }


  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager.createDirectly(
        this.getClass().getResource("/pentaho/crp/AR-Evidences/OutcomesMilestonesAR2018.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String date = timezone.format(format) + "(GMT" + zone + ")";
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(this.getSelectedYear()));
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
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      // System.out.println("Pentaho SubReports: " + hm);

      this.fillSubreport((SubReport) hm.get("details"), "details");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating PoliciesEvidenceReporting " + e.getMessage());
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


  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "details":
        model = this.getOutcomeMilestoneReportingTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  public byte[] getBytesXLSX() {
    return bytesXLSX;
  }

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
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
    fileName.append("OutcomesMilestones-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedPhase().getName());
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
    return fileName.toString();
  }

  public String getHighlightsImagesUrl(String projectId) {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath(projectId).replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath(String projectId) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + projectId + File.separator
      + "hightlightsImage" + File.separator;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }


  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "baseUrl"},
      new Class[] {String.class, String.class, String.class, String.class});
    model.addRow(new Object[] {center, date, year, this.getBaseUrl()});
    return model;
  }

  private TypedTableModel getOutcomeMilestoneReportingTableModel() {

    /*
     * Parameters variables to send to the file
     * paramA - Id
     * paramB - FP
     * paramC - Outcome
     * paramD - Outcome Progress
     * paramE - Milestone
     * paramF - Status
     * paramG - Status predominant reason
     * paramH - Milestone Evidence
     * paramI - Gender
     * paramJ - Gender Justification
     * paramK - Youth
     * paramL - Youth Justification
     * paramM - CapDev
     * paramN - CapDev Justification
     * paramO - Climate Change
     * paramP - Climate Change Justification
     * NOTE : does not mater the order into the implementation (ex: the paramV will be setup first that the paramA)
     */
    TypedTableModel model = new TypedTableModel(
      new String[] {"paramA", "paramB", "paramC", "paramD", "paramE", "paramF", "paramG", "paramH", "paramI", "paramJ",
        "paramK", "paramL", "paramM", "paramN", "paramO", "paramP"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class},
      0);

    // Load the information
    List<AROutcomeMilestoneEvidence> outcomeMilestones = this.getOutcomeMilestonesInfo();

    for (AROutcomeMilestoneEvidence outcomeMilestone : outcomeMilestones) {

      String paramA = "", paramB = "", paramC = "", paramD = "", paramE = "", paramF = "", paramG = "", paramH = "",
        paramI = "", paramJ = "", paramK = "", paramL = "", paramM = "", paramN = "", paramO = "", paramP = "";

      // Id
      paramA = outcomeMilestone.getCrpMilestone().getId().toString();
      // FP
      paramB = outcomeMilestone.getCrpProgramOutcome().getPAcronym();
      // Outcome
      paramC = outcomeMilestone.getCrpProgramOutcome().getComposedName();
      // Outcome Progress
      paramD = this.removeHtmlTags(outcomeMilestone.getOutcomeProgress());
      paramD = this.removeHrefTags(paramD);
      // Milestone
      paramE = outcomeMilestone.getCrpMilestone().getComposedName();
      // Milestone Status
      paramF = outcomeMilestone.getStatusName();
      if (outcomeMilestone.getStatusName() == null && outcomeMilestone.getStatusName().isEmpty()) {
        paramF = "<Not Defined>";
      }
      // Reason
      if (outcomeMilestone.getMilestonesStatus() != null && outcomeMilestone.getMilestonesStatus() != 1L) {
        if (outcomeMilestone.getRepIndMilestoneReason() != null) {
          if (outcomeMilestone.getRepIndMilestoneReason().getId().equals(7L)) {
            paramG = outcomeMilestone.getOtherReason();
          } else {
            paramG = outcomeMilestone.getRepIndMilestoneReason().getName();
          }
        } else {
          paramG = "<Not Defined>";
        }
      } else {
        paramG = "<Not Applicable>";
      }
      // milestone evidence
      paramH = this.removeHtmlTags(outcomeMilestone.getEvidence());
      paramH = this.removeHrefTags(paramH);

      if (paramH == null || paramH.isEmpty()) {
        paramH = "<Not Defined>";
      }

      // CGIAR Cross-cutting Markers
      if (outcomeMilestone.getCrossCuttingMarkers() != null) {

        List<ReportSynthesisFlagshipProgressCrossCuttingMarker> markers = new ArrayList<>(

          outcomeMilestone.getCrossCuttingMarkers().stream().filter(o -> o.isActive()).collect(Collectors.toList()));

        if (markers != null && !markers.isEmpty()) {
          for (ReportSynthesisFlagshipProgressCrossCuttingMarker marker : markers) {
            // Gender
            if (marker.getMarker().getId() == 1) {
              if (marker.getFocus() != null) {
                paramI = marker.getFocus().getPowbName();
                paramJ = marker.getJust();
              } else {
                paramI = "<Not Defined>";
                paramJ = "<Not Defined>";
              }
            }
            // Youth
            if (marker.getMarker().getId() == 2) {
              if (marker.getFocus() != null) {
                paramK = marker.getFocus().getPowbName();
                paramL = marker.getJust();
              } else {
                paramK = "<Not Defined>";
                paramL = "<Not Defined>";
              }
            }
            // CapDev
            if (marker.getMarker().getId() == 3) {
              if (marker.getFocus() != null) {
                paramM = marker.getFocus().getPowbName();
                paramN = marker.getJust();
              } else {
                paramM = "<Not Defined>";
                paramN = "<Not Defined>";
              }
            }
            // Climate Change
            if (marker.getMarker().getId() == 4) {
              if (marker.getFocus() != null) {
                paramO = marker.getFocus().getPowbName();
                paramP = marker.getJust();
              } else {
                paramO = "<Not Defined>";
                paramP = "<Not Defined>";
              }
            }
          }
        }

      } else {
        paramI = "<Not Defined>";
        paramJ = "<Not Defined>";
        paramK = "<Not Defined>";
        paramL = "<Not Defined>";
        paramM = "<Not Defined>";
        paramN = "<Not Defined>";
        paramO = "<Not Defined>";
        paramP = "<Not Defined>";
      }

      model.addRow(new Object[] {paramA, paramB, paramC, paramD, paramE, paramF, paramG, paramH, paramI, paramJ, paramK,
        paramL, paramM, paramN, paramO, paramP});
    }
    return model;
  }


  /**
   * Get the information of all Outcomes and milestones synthetized in the annual report
   * 
   * @return a list of all Outcomes and milestones synthetized in the annual report
   */
  public List<AROutcomeMilestoneEvidence> getOutcomeMilestonesInfo() {

    List<AROutcomeMilestoneEvidence> arOutcomeMilestoneEvidences = new ArrayList<AROutcomeMilestoneEvidence>();

    List<LiaisonInstitution> liaisonInstitutions = this.getLoggedCrp().getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null && c.isActive() && c.getCrpProgram().isActive()
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList());
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

    for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {

      ReportSynthesis reportSynthesisFG =
        reportSynthesisManager.findSynthesis(this.getSelectedPhase().getId(), liaisonInstitution.getId());

      if (reportSynthesisFG.getReportSynthesisFlagshipProgress() != null) {

        if (reportSynthesisFG.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes() != null
          && !reportSynthesisFG.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes()
            .isEmpty()
          && reportSynthesisFG.isActive() && reportSynthesisFG.getReportSynthesisFlagshipProgress().isActive()) {

          List<ReportSynthesisFlagshipProgressOutcome> progressOutcomes = new ArrayList<>(
            reportSynthesisFG.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressOutcomes().stream()
              .filter(o -> o.isActive() && o.getCrpProgramOutcome().isActive()).collect(Collectors.toList()));

          for (ReportSynthesisFlagshipProgressOutcome progressOutcome : progressOutcomes) {

            if (progressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones() != null
              && !progressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().isEmpty()) {
              List<ReportSynthesisFlagshipProgressOutcomeMilestone> outcomeMilestones =
                new ArrayList<>(progressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
                  .filter(o -> o.isActive() && o.getCrpMilestone().isActive()).collect(Collectors.toList()));
              for (ReportSynthesisFlagshipProgressOutcomeMilestone outcomeMilestone : outcomeMilestones) {

                AROutcomeMilestoneEvidence milestoneEvidence = new AROutcomeMilestoneEvidence();
                milestoneEvidence.setCrpProgramOutcome(progressOutcome.getCrpProgramOutcome());
                milestoneEvidence.setOutcomeProgress(progressOutcome.getSummary());
                milestoneEvidence.setCrpMilestone(outcomeMilestone.getCrpMilestone());
                milestoneEvidence.setMilestonesStatus(outcomeMilestone.getMilestonesStatus().getId());
                milestoneEvidence.setRepIndMilestoneReason(outcomeMilestone.getReason());
                milestoneEvidence.setOtherReason(outcomeMilestone.getOtherReason());
                milestoneEvidence.setEvidence(outcomeMilestone.getEvidence());

                milestoneEvidence.setCrossCuttingMarkers(new ArrayList<>());
                if (outcomeMilestone.getReportSynthesisFlagshipProgressCrossCuttingMarkers() != null
                  && !outcomeMilestone.getReportSynthesisFlagshipProgressCrossCuttingMarkers().isEmpty()) {

                  milestoneEvidence.getCrossCuttingMarkers()
                    .addAll(outcomeMilestone.getReportSynthesisFlagshipProgressCrossCuttingMarkers().stream()
                      .filter(o -> o.isActive()).collect(Collectors.toList()));
                }

                arOutcomeMilestoneEvidences.add(milestoneEvidence);

              }
            }

          }
        }
      }

    }


    return arOutcomeMilestoneEvidences;
  }


  @Override
  public void prepare() throws Exception {
    this.setGeneralParameters();
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  private String removeHrefTags(String text) {

    text = text.replaceAll("<a href=", "");
    text = text.replaceAll("</a>", "");
    text = text.replaceAll("amp;", "");
    text = text.replaceAll("&nbsp;", "");

    return text;

  }

  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

}
