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

import org.cgiar.ccafs.marlo.action.report.MicroserviceReportAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CaseStudyManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportConfigurationManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HTMLParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InnovationSummaryAction extends BaseStudySummaryData implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(InnovationSummaryAction.class);

  // Managers
  private final ProjectInnovationManager projectInnovationManager;
  private final ResourceManager resourceManager;
  private final HTMLParser htmlParser;;
  private final InstitutionManager institutionManager;
  private final MicroserviceReportAction microserviceReportAction;
  private final ReportConfigurationManager reportConfigurationManager;
  private List<ProjectInnovation> projectInnovations = new ArrayList<>();
  private GlobalUnitManager crpManager;
  private LocElementManager locElementManager;
  private RepIndStageInnovationManager repIndStageInnovationManager;
  private String crp;

  // PDF bytes
  private byte[] bytesPDF;

  // Streams
  InputStream inputStream;


  // Parameters
  private long startTime;
  private Long projectInnovationID;
  private ProjectInnovation projectInnovation;
  private String studyProjects = null;

  @Inject
  public InnovationSummaryAction(APConfig config, CaseStudyManager caseStudyManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager, ResourceManager resourceManager, ProjectInnovationManager projectInnovationManager,
    HTMLParser htmlParser, ProjectManager projectManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager, InstitutionManager institutionManager,
    MicroserviceReportAction microserviceReportAction, ReportConfigurationManager reportConfigurationManager,
    LocElementManager locElementManager, RepIndStageInnovationManager repIndStageInnovationManager) {
    super(config, crpManager, phaseManager, projectManager, htmlParser, projectExpectedStudyCountryManager,
      institutionManager, microserviceReportAction, reportConfigurationManager, locElementManager,
      repIndStageInnovationManager);
    this.resourceManager = resourceManager;
    this.projectInnovationManager = projectInnovationManager;
    this.htmlParser = htmlParser;
    this.crpManager = crpManager;
    this.institutionManager = institutionManager;
    this.microserviceReportAction = microserviceReportAction;
    this.reportConfigurationManager = reportConfigurationManager;
    this.locElementManager = locElementManager;
    this.repIndStageInnovationManager = repIndStageInnovationManager;
  }


  @Override
  public String execute() throws Exception {

    if (this.projectInnovationID == -1) {
      return NOT_FOUND;
    }

    if (this.getSelectedPhase() == null) {
      return NOT_FOUND;
    }


    if (projectInnovationID == null || projectInnovationManager.getProjectInnovationById(projectInnovationID) == null
      || projectInnovationManager.getProjectInnovationById(projectInnovationID)
        .getProjectInnovationInfo(this.getSelectedPhase()) == null) {
      System.out.println("ProjectExpectedStudy " + projectInnovationID + " Not found");
      return NOT_FOUND;
    } else {
      projectInnovation = projectInnovationManager.getProjectInnovationById(projectInnovationID);
    }
    projectInnovations.add(projectInnovation);
    ByteArrayOutputStream os = new ByteArrayOutputStream();


    try {
      this.generateAndSendJsonForInnovations(projectInnovations);
      bytesPDF = os.toByteArray();
    } catch (Exception e) {
      if (e.getClass().getName().contains("ClientAbortException")) {
        System.out.println("Client aborted the connection: " + e.getMessage());
      } else {
        System.out.println("Exception while generating JSON: " + e.getMessage());
        throw e;
      }
    } finally {
      try {
        os.close();
      } catch (Exception e) {
        System.out.println("Error closing output stream: " + e.getMessage());
      }
    }


    return SUCCESS;
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
    fileName.append("OICRsMELIAsSummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    try {
      if (studyProjects != null && !studyProjects.isEmpty()) {
        fileName.append(studyProjects.replaceAll("<br>&nbsp;&nbsp;&nbsp;&nbsp; ● ", "") + "-");
      } else if (projectInnovation != null && projectInnovation.getProject() != null) {
        fileName.append(projectInnovation.getProject().getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER) + "-");
      }
    } catch (Exception e) {
      System.out.println("Error getting project(s) for study: " + projectInnovationID);
    }
    fileName.append("OICR" + projectInnovationID + "-");
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

  public String getPath() {
    return config.getDownloadURL() + "/" + this.getStudiesSourceFolder().replace('\\', '/');
  }

  private String getStudiesSourceFolder() {
    return APConstants.STUDIES_FOLDER.concat(File.separator).concat(crp).concat(File.separator).concat(File.separator)
      .concat(crp + "_").concat(ProjectSectionStatusEnum.EXPECTEDSTUDY.getStatus()).concat(File.separator);
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    this.setPublicAccessParameters();
    try {
      projectInnovationID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.INNOVATION_REQUEST_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      System.out.println("Error getting project: expected study " + projectInnovationID);

      if (projectInnovationID == null) {
        projectInnovationID = (long) -1;
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
