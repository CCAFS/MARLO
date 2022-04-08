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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class StudySummaryAction extends BaseStudySummaryData implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(StudySummaryAction.class);

  // Managers
  private final ProjectExpectedStudyManager projectExpectedStudyManager;
  private final ResourceManager resourceManager;
  private final HTMLParser htmlParser;;
  private List<ProjectExpectedStudyInfo> projectExpectedStudyInfos = new ArrayList<>();
  private GlobalUnitManager crpManager;
  private String crp;

  // PDF bytes
  private byte[] bytesPDF;

  // Streams
  InputStream inputStream;


  // Parameters
  private long startTime;
  private Long projectExpectedStudyID;
  private ProjectExpectedStudyInfo projectExpectedStudyInfo;
  private String studyProjects = null;

  @Inject
  public StudySummaryAction(APConfig config, CaseStudyManager caseStudyManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager, ResourceManager resourceManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    HTMLParser htmlParser, ProjectManager projectManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager) {
    super(config, crpManager, phaseManager, projectManager, htmlParser, projectExpectedStudyCountryManager);
    this.resourceManager = resourceManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.htmlParser = htmlParser;
    this.crpManager = crpManager;
  }

  @Override
  public String execute() throws Exception {

    if (this.projectExpectedStudyID == -1) {
      return NOT_FOUND;
    }

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }

    List<String> allianceArgList = new ArrayList<>();

    if (projectExpectedStudyID == null
      || projectExpectedStudyManager.getProjectExpectedStudyById(projectExpectedStudyID) == null
      || projectExpectedStudyManager.getProjectExpectedStudyById(projectExpectedStudyID)
        .getProjectExpectedStudyInfo(this.getSelectedPhase()) == null) {
      LOG.error("ProjectExpectedStudy " + projectExpectedStudyID + " Not found");
      return NOT_FOUND;
    } else {
      projectExpectedStudyInfo = projectExpectedStudyManager.getProjectExpectedStudyById(projectExpectedStudyID)
        .getProjectExpectedStudyInfo(this.getSelectedPhase());
      if (projectExpectedStudyInfo != null) {
        boolean isOicr =
          projectExpectedStudyInfo.getStudyType() != null && projectExpectedStudyInfo.getStudyType().getId() != null
            && projectExpectedStudyInfo.getStudyType().getId().longValue() == 1L;
        allianceArgList.add(isOicr ? "OICR" : "MELIA");
      }
    }
    projectExpectedStudyInfos.add(projectExpectedStudyInfo);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      crp = this.getLoggedCrp().getAcronym();
      if (crp == null || crp.isEmpty()) {
        String[] actionMap = ActionContext.getContext().getName().split("/");
        if (actionMap.length > 1) {
          String enteredCrp = actionMap[0];
          crp = crpManager.findGlobalUnitByAcronym(enteredCrp).getAcronym();
        }
      }

      String center = crp;
      boolean isAlliance = "Alliance".equalsIgnoreCase(center);

      Resource reportResource = isAlliance
        ? resourceManager.createDirectly(this.getClass().getResource("/pentaho/crp/StudiesPDFAlliance.prpt"),
          MasterReport.class)
        : resourceManager.createDirectly(this.getClass().getResource("/pentaho/crp/StudiesPDF.prpt"),
          MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();


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
      TypedTableModel model = this.getMasterTableModel(crp, date, String.valueOf(this.getSelectedYear()));
      sdf.addTable(masterQueryName, model);
      masterReport.setDataFactory(cdf);
      // Set i8n for pentaho
      masterReport = this.addi8nParameters(masterReport, isAlliance, allianceArgList);
      // Get details band
      ItemBand masteritemBand = masterReport.getItemBand();
      // Create new empty subreport hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the subreports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      // System.out.println("Pentaho SubReports: " + hm);

      this.fillSubreport((SubReport) hm.get("case_studies"), "case_studies", isAlliance);

      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating Study Summary: " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: " + this.getDownloadByUser() + ". CRP: "
      + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
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

  public String getCaseStudyUrl(String project) {
    return config.getDownloadURL() + "/" + this.getCaseStudyUrlPath(project).replace('\\', '/');
  }


  public String getCaseStudyUrlPath(String project) {

    return config.getProjectsBaseFolder(crp) + File.separator + project + File.separator + "caseStudy" + File.separator;
  }

  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
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
    fileName.append("OutcomesCaseStudySummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    try {
      if (studyProjects != null && !studyProjects.isEmpty()) {
        fileName.append(studyProjects.replaceAll("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● ", "") + "-");
      } else if (projectExpectedStudyInfo.getProjectExpectedStudy().getProject() != null) {
        fileName.append(projectExpectedStudyInfo.getProjectExpectedStudy().getProject()
          .getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER) + "-");
      }
    } catch (Exception e) {
      LOG.info("Error getting project(s) for study: " + projectExpectedStudyID);
    }
    fileName.append("OICS" + projectExpectedStudyID + "-");
    fileName.append(this.getSelectedCycle() + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }

  private TypedTableModel getMasterTableModel(String center, String date, String year) {
    // Initialization of Model
    TypedTableModel model = new TypedTableModel(
      new String[] {"center", "date", "year", "isReporting", "id", "type", "cycle", "phase_id"}, new Class[] {
        String.class, String.class, String.class, Boolean.class, Long.class, String.class, String.class, Long.class});
    String type = null;
    // Type
    if (projectExpectedStudyInfo.getStudyType() != null) {
      type = projectExpectedStudyInfo.getStudyType().getName();
    }
    model.addRow(new Object[] {center, date, year, this.getSelectedPhase().isReporting(),
      projectExpectedStudyInfo.getProjectExpectedStudy().getId(), type, this.getSelectedCycle(),
      this.getSelectedPhase().getId()});
    return model;
  }

  public String getPath() {
    return config.getDownloadURL() + "/" + this.getStudiesSourceFolder().replace('\\', '/');
  }

  private String getStudiesSourceFolder() {
    return APConstants.STUDIES_FOLDER.concat(File.separator).concat(crp).concat(File.separator).concat(File.separator)
      .concat(crp + "_").concat(ProjectSectionStatusEnum.EXPECTEDSTUDY.getStatus()).concat(File.separator);
  }

  @Override
  public boolean isPublicRoute() {
    return true;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    this.setPublicAccessParameters();
    try {
      projectExpectedStudyID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.STUDY_REQUEST_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      LOG.info("Error getting project: expected study " + projectExpectedStudyID);

      if (projectExpectedStudyID == null) {
        projectExpectedStudyID = (long) -1;
      }
    }


    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: " + this.getDownloadByUser() + ". CRP: "
      + this.getLoggedCrp().getAcronym());

  }

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }


  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

}
