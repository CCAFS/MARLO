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
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HTMLParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class StudiesSummaryAction extends BaseStudySummaryData implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(StudiesSummaryAction.class);

  // Managers
  private final ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private final ResourceManager resourceManager;
  private final HTMLParser htmlParser;
  private List<ProjectExpectedStudyInfo> projectExpectedStudyInfos = new ArrayList<>();
  private final ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;

  // PDF bytes
  private byte[] bytesPDF;
  // XLSX bytes
  private byte[] bytesXLSX;

  // Streams
  InputStream inputStream;


  // Parameters
  private long startTime;
  private String studyType;
  private String selectedFormat;

  @Inject
  public StudiesSummaryAction(APConfig config, CaseStudyManager caseStudyManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager, ResourceManager resourceManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager, HTMLParser htmlParser,
    ProjectManager projectManager, ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager) {
    super(config, crpManager, phaseManager, projectManager, htmlParser, projectExpectedStudyCountryManager);
    this.resourceManager = resourceManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.htmlParser = htmlParser;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
  }

  @Override
  public String execute() throws Exception {

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource;
      if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
        reportResource = resourceManager
          .createDirectly(this.getClass().getResource("/pentaho/crp/CaseStudiesExcel.prpt"), MasterReport.class);
      } else {
        reportResource = resourceManager.createDirectly(this.getClass().getResource("/pentaho/crp/StudiesPDF.prpt"),
          MasterReport.class);
      }
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = this.getLoggedCrp().getAcronym();
      boolean isAlliance = "Alliance".equalsIgnoreCase(center);

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
      masterReport = this.addi8nParameters(masterReport, isAlliance, Collections.singletonList("study"));
      // Get details band
      ItemBand masteritemBand = masterReport.getItemBand();
      // Create new empty subreport hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the subreports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      // System.out.println("Pentaho SubReports: " + hm);
      this.loadProjectExpectedStudyInfos();
      this.fillSubreport((SubReport) hm.get("case_studies"), "case_studies", isAlliance);

      if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
        ExcelReportUtil.createXLSX(masterReport, os);
        bytesXLSX = os.toByteArray();
      } else {
        PdfReportUtil.createPDF(masterReport, os);
        bytesPDF = os.toByteArray();
      }
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating CaseStudy" + this.getSelectedFormat() + ": " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }


  private void fillSubreport(SubReport subReport, String query, boolean isAlliance) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "case_studies":
        model = this.getCaseStudiesTableModel(projectExpectedStudyInfos, isAlliance);
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }

  public byte[] getBytesPDF() {
    return bytesPDF;
  }

  public byte[] getBytesXLSX() {
    return bytesXLSX;
  }

  @Override
  public int getContentLength() {
    if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
      return bytesXLSX.length;
    } else {
      return bytesPDF.length;
    }
  }

  @Override
  public String getContentType() {
    if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
      return "application/xlsx";
    } else {
      return "application/pdf";
    }

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
    fileName.append("OutcomesCaseStudiesSummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    fileName.append(this.getSelectedCycle() + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
      fileName.append(".xlsx");
    } else {
      fileName.append(".pdf");
    }
    return fileName.toString();
  }


  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      if (this.getSelectedFormat().equals(APConstants.SUMMARY_FORMAT_EXCEL)) {
        inputStream = new ByteArrayInputStream(bytesXLSX);
      } else {
        inputStream = new ByteArrayInputStream(bytesPDF);
      }
    }
    return inputStream;
  }

  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"center", "date", "year", "isReporting", "cycle", "phase_id"},
        new Class[] {String.class, String.class, String.class, Boolean.class, String.class, Long.class});
    model.addRow(new Object[] {center, date, year, this.getSelectedPhase().isReporting(), this.getSelectedCycle(),
      this.getSelectedPhase().getId()});
    return model;
  }


  public String getSelectedFormat() {
    return selectedFormat;
  }


  private void loadProjectExpectedStudyInfos() {
    if (studyType.equals("all")) {
      projectExpectedStudyInfos =
        projectExpectedStudyInfoManager.getProjectExpectedStudyInfoByPhase(this.getSelectedPhase()).stream()
          .filter(
            si -> si.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null
              && si.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear()
                .intValue() == this.getSelectedYear())
          .collect(Collectors.toList());
    }
    if (studyType.equals("outcome_case_study")) {
      projectExpectedStudyInfos =
        projectExpectedStudyInfoManager.getProjectExpectedStudyInfoByPhase(this.getSelectedPhase()).stream()
          .filter(
            si -> si.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null
              && si.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear()
                .intValue() == this.getSelectedYear()
              && si.getStudyType() != null && si.getStudyType().getId() == 1)
          .collect(Collectors.toList());
    }
    if (studyType.equals("others")) {
      projectExpectedStudyInfos =
        projectExpectedStudyInfoManager.getProjectExpectedStudyInfoByPhase(this.getSelectedPhase()).stream()
          .filter(
            si -> si.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear() != null
              && si.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getSelectedPhase()).getYear()
                .intValue() == this.getSelectedYear()
              && si.getStudyType() != null && si.getStudyType().getId() != 1)
          .collect(Collectors.toList());
    }
  }

  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    Map<String, Parameter> parameters = this.getParameters();
    this.setGeneralParameters();
    this.setSelectedFormat((StringUtils.trim(parameters.get(APConstants.SUMMARY_FORMAT).getMultipleValues()[0])));
    // Study type
    try {
      studyType = StringUtils.trim(parameters.get(APConstants.SUMMARY_STUDY_TYPE).getMultipleValues()[0]);
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.SUMMARY_STUDY_TYPE
        + " parameter. Parameter will be set as All. Exception: " + e.getMessage());
      studyType = "All";
    }
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.getLoggedCrp().getAcronym());
  }

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }


  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  public void setSelectedFormat(String selectedFormat) {
    this.selectedFormat = selectedFormat;
  }

}
