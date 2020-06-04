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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectImpactsManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.ReportProjectImpactsCovid19DTO;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
 * @author Luis Benavides. CCAFS
 */
public class ImpactCovid19SummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = -6493065557542133007L;
  private static Logger LOG = LoggerFactory.getLogger(ImpactCovid19SummaryAction.class);

  private final ResourceManager resourceManager;

  // Parameters
  private long startTime;
  private HashMap<Long, String> targetUnitList;

  // Managers
  private ProjectImpactsManager projectImpactsManager;

  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;

  @Inject
  public ImpactCovid19SummaryAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager, ResourceManager resourceManager, ProjectImpactsManager projectImpactsManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.resourceManager = resourceManager;
    this.projectImpactsManager = projectImpactsManager;
  }

  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nCovid19Title", this.getText("summaries.impacts.titleCovid19"));
    masterReport.getParameterValues().put("i8nProjectId", this.getText("searchTerms.projectId"));
    masterReport.getParameterValues().put("i8nTitle", this.getText("project.title.readText"));
    masterReport.getParameterValues().put("i8nProjectSummary", this.getText("project.summary"));
    masterReport.getParameterValues().put("i8nProjectLeader", this.getText("summaries.oaprojects.projectLeader"));
    masterReport.getParameterValues().put("i8nManagementLiasion",
      this.getText("summaries.oaprojects.managementLiasion"));
    masterReport.getParameterValues().put("i8nAnswer2020Covid19", this.getText("summaries.impacts.answer2020Covid19"));
    masterReport.getParameterValues().put("i8nAnswer2021Covid19", this.getText("summaries.impacts.answer2021Covid19"));
    masterReport.getParameterValues().put("i8nProjectLeaderEmail",
      this.getText("summaries.impacts.projectLeaderEmail"));
    masterReport.getParameterValues().put("i8nManagementLiasionAcronym",
      this.getText("summaries.impacts.managementLiasionAcronym"));

    return masterReport;
  }

  private void calculateTimeToGenerateReport() {
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Cycle: " + this.getSelectedCycle());
  }

  @Override
  public String execute() throws Exception {
    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/ImpactCovid19.prpt"), MasterReport.class);

      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();
      // Get datetime
      ZonedDateTime timezone = ZonedDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
      String zone = timezone.getOffset() + "";
      if (zone.equals("Z")) {
        zone = "+0";
      }
      String currentDate = timezone.format(format) + "(GMT" + zone + ")";
      // Set Main_Query
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      String masterQueryName = "main";
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel(center, currentDate);
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
      this.fillSubreport((SubReport) hm.get("impact_covid19"), "impact_covid19");
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ImpactCovid19 " + e.getMessage());
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
      case "impact_covid19":
        model = this.getImpactCovid19TableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  @Override
  public int getContentLength() {
    return bytesXLSX.length;
  }

  @Override
  public String getContentType() {
    return "application/xlsx";
  }

  @Override
  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("ImpactCovid19Summary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedCycle() + "_");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
    return fileName.toString();
  }

  private TypedTableModel getImpactCovid19TableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"projectId", "title", "projectSummary", "projectLeader", "managementLiasion", "answer2020Covid19",
        "answer2021Covid19", "projectUrl", "phaseId", "projectLeaderEmail", "managementLiasionAcronym"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, Long.class, String.class, String.class},
      0);

    List<ReportProjectImpactsCovid19DTO> reportProjectImpactsCovid19DTO =
      projectImpactsManager.getProjectImpactsByProjectAndYears(this.getSelectedPhase());

    for (ReportProjectImpactsCovid19DTO reportProjectImpactCovid19DTO : reportProjectImpactsCovid19DTO) {

      model.addRow(new Object[] {reportProjectImpactCovid19DTO.getProjectId(), reportProjectImpactCovid19DTO.getTitle(),
        reportProjectImpactCovid19DTO.getProjectSummary(), reportProjectImpactCovid19DTO.getProjectLeader(),
        reportProjectImpactCovid19DTO.getManagementLiasion(), reportProjectImpactCovid19DTO.getAnswer().get(2020),
        reportProjectImpactCovid19DTO.getAnswer().get(2021), reportProjectImpactCovid19DTO.getProjectUrl(),
        reportProjectImpactCovid19DTO.getPhaseId(), reportProjectImpactCovid19DTO.getProjectLeaderEmail(),
        reportProjectImpactCovid19DTO.getManagementLiasionAcronym()});
    }
    return model;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesXLSX);
    }
    return inputStream;
  }

  private TypedTableModel getMasterTableModel(String center, String date) {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"center", "date", "hasTargetUnit", "hasOutcomeIndicator", "cycle", "year"},
        new Class[] {String.class, String.class, Boolean.class, Boolean.class, String.class, Integer.class});
    Boolean hasTargetUnit = false;
    if (targetUnitList.size() > 0) {
      hasTargetUnit = true;
    }

    model.addRow(new Object[] {center, date, hasTargetUnit, this.hasSpecificities(APConstants.CRP_IP_OUTCOME_INDICATOR),
      this.getSelectedCycle(), this.getSelectedYear()});
    return model;
  }

  @Override
  public void prepare() {
    this.setGeneralParameters();
    targetUnitList = new HashMap<>();
    /* if need more parameters use same that StudiesSummaryAction.java */
    this.calculateTimeToGenerateReport();
  }
}
