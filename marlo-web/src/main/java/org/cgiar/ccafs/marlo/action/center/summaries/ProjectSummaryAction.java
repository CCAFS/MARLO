/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.center.summaries;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.config.PentahoListener;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;
import org.cgiar.ccafs.marlo.data.model.CenterProjectLocation;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProjectPartner;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrés Felipe Valencia Rivera. CCAFS
 */
public class ProjectSummaryAction extends BaseAction implements Summary {

  private static final long serialVersionUID = 5009625767712161204L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectSummaryAction.class);

  // Streams
  InputStream inputStream;
  // PDF bytes
  private byte[] bytesPDF;
  // Parameters
  private long startTime;
  private Center loggedCenter;
  private CenterProject project;
  // Front-end
  private long projectID;
  // Services
  private ICenterManager centerService;
  private ICenterProjectManager projectService;

  @Inject
  public ProjectSummaryAction(APConfig config, ICenterManager centerService, ICenterProjectManager projectService) {
    super(config);
    this.centerService = centerService;
    this.projectService = projectService;
  }

  @Override
  public String execute() throws Exception {


    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ResourceManager manager =
      (ResourceManager) ServletActionContext.getServletContext().getAttribute(PentahoListener.KEY_NAME);
    // manager.registerDefaults();
    try {

      Resource reportResource =
        manager.createDirectly(this.getClass().getResource("/pentaho/projectSummary.prpt"), MasterReport.class);

      // Get main report
      MasterReport masterReport = (MasterReport) reportResource.getResource();

      // Get program from DB
      // project = projectManager.getProjectById(projectID);


      // Get details band
      ItemBand masteritemBand = masterReport.getItemBand();
      // Create new empty subreport hash map
      HashMap<String, Element> hm = new HashMap<String, Element>();
      // method to get all the subreports in the prpt and store in the HashMap
      this.getAllSubreports(hm, masteritemBand);
      // Uncomment to see which Subreports are detecting the method getAllSubreports
      System.out.println("Pentaho SubReports: " + hm);

      // Set Main_Query
      String masterQueryName = "main";
      CompoundDataFactory cdf = CompoundDataFactory.normalize(masterReport.getDataFactory());
      TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(masterQueryName);
      TypedTableModel model = this.getMasterTableModel();
      sdf.addTable(masterQueryName, model);
      masterReport.setDataFactory(cdf);

      // Start Setting Subreports

      // Subreport Description
      this.fillSubreport((SubReport) hm.get("description"), "description");
      this.fillSubreport((SubReport) hm.get("partners"), "partners");
      this.fillSubreport((SubReport) hm.get("deliverables"), "deliverables");
      this.fillSubreport((SubReport) hm.get("descriptionFS"), "descriptionFS");

      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating PDF " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;

  }


  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "description":
        model = this.getDescriptionTableModel();
        break;
      case "partners":
        model = this.getPartnersTableModel();
        break;
      case "deliverables":
        model = this.getDeliverablesTableModel();
        break;
      case "descriptionFS":
        model = this.getFundingSourcesTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
  }


  /**
   * Get all subreports and store then in a hash map.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm List to populate with subreports found
   * @param itemBand details section in pentaho
   */
  private void getAllSubreports(HashMap<String, Element> hm, ItemBand itemBand) {
    int elementCount = itemBand.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = itemBand.getElement(i);
      // verify if the item is a SubReport
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());
          // If report footer is not null check for subreports
          if (((SubReport) e).getReportFooter().getElementCount() != 0) {
            this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
          }
        }
      }
      // If is a band, find the subreport if exist
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  /**
   * Get all subreports in the band.
   * If it encounters a band, search subreports in the band
   * 
   * @param hm
   * @param band
   */
  private void getBandSubreports(HashMap<String, Element> hm, Band band) {
    int elementCount = band.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = band.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        // If report footer is not null check for subreports
        if (((SubReport) e).getReportFooter().getElementCount() != 0) {
          this.getFooterSubreports(hm, ((SubReport) e).getReportFooter());
        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  public byte[] getBytesPDF() {
    return bytesPDF;
  }

  @Override
  public int getContentLength() {
    return bytesPDF.length;
  }

  @Override
  public String getContentType() {
    return "application/pdf";
  }

  private TypedTableModel getDeliverablesTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "deliverableTitle", "type", "subType", "startDate", "endDate", "crossCutting",
        "deliverableOutputs", "supportingDocuments"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class});

    for (CenterDeliverable deliverable : project.getDeliverables().stream().filter(d -> d.isActive())
      .collect(Collectors.toList())) {
      Long id = deliverable.getId();
      String deliverableTitle = null;
      if (deliverable.getName() != null && !deliverable.getName().trim().isEmpty()) {
        deliverableTitle = deliverable.getName();
      }
      String type = null;
      String subType = null;
      if (deliverable.getDeliverableType() != null && deliverable.getDeliverableType().getDeliverableType() != null) {
        subType = deliverable.getDeliverableType().getName();
        type = deliverable.getDeliverableType().getDeliverableType().getName();
      }

      SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
      String startDate = null;

      if (deliverable.getStartDate() != null) {
        startDate = formatter.format(deliverable.getStartDate());
      }
      String endDate = null;
      if (deliverable.getEndDate() != null) {
        endDate = formatter.format(deliverable.getEndDate());
      }

      String crossCutting = "";
      if (deliverable.getDeliverableCrosscutingTheme() != null) {
        if (deliverable.getDeliverableCrosscutingTheme().getClimateChange() != null
          && deliverable.getDeliverableCrosscutingTheme().getClimateChange()) {
          crossCutting += "&#9679;  Climate Change <br>";
        }
        if (deliverable.getDeliverableCrosscutingTheme().getGender() != null
          && deliverable.getDeliverableCrosscutingTheme().getGender()) {
          crossCutting += "&#9679;  Gender <br>";
        }
        if (deliverable.getDeliverableCrosscutingTheme().getYouth() != null
          && deliverable.getDeliverableCrosscutingTheme().getYouth()) {
          crossCutting += "&#9679;  Youth <br>";
        }
        if (deliverable.getDeliverableCrosscutingTheme().getNa() != null
          && deliverable.getDeliverableCrosscutingTheme().getNa()) {
          crossCutting += "&#9679;  N/A <br>";
        }
        if (deliverable.getDeliverableCrosscutingTheme().getCapacityDevelopment() != null
          && deliverable.getDeliverableCrosscutingTheme().getCapacityDevelopment()) {
          crossCutting += "&#9679;  Capacity Development <br>";
        }
        if (deliverable.getDeliverableCrosscutingTheme().getBigData() != null
          && deliverable.getDeliverableCrosscutingTheme().getBigData()) {
          crossCutting += "&#9679;  Big Data <br>";
        }
        if (deliverable.getDeliverableCrosscutingTheme().getImpactAssessment() != null
          && deliverable.getDeliverableCrosscutingTheme().getImpactAssessment()) {
          crossCutting += "&#9679;  Impact Assessment <br>";
        }
        if (deliverable.getDeliverableCrosscutingTheme().getPoliciesInstitutions() != null
          && deliverable.getDeliverableCrosscutingTheme().getPoliciesInstitutions()) {
          crossCutting += "&#9679;  Policies and Institutions <br>";
        }
      }
      if (crossCutting.isEmpty()) {
        crossCutting = null;
      }
      String deliverableOutputs = "";
      for (CenterDeliverableOutput deliverableOutput : deliverable.getDeliverableOutputs().stream()
        .filter(deo -> deo.isActive()).collect(Collectors.toList())) {
        deliverableOutputs += "&#9679;  " + deliverableOutput.getResearchOutput().getTitle() + "<br>";
      }
      if (deliverableOutputs.isEmpty()) {
        deliverableOutputs = null;
      }
      String supportingDocuments = "";
      for (CenterDeliverableDocument deliverableDocument : deliverable.getDeliverableDocuments().stream()
        .filter(dd -> dd.isActive()).collect(Collectors.toList())) {
        if (deliverableDocument.getLink() != null && !deliverableDocument.getLink().trim().isEmpty()) {
          supportingDocuments += "&#9679;  " + deliverableDocument.getLink() + "<br>";
        }
      }
      if (supportingDocuments.isEmpty()) {
        supportingDocuments = null;
      }

      model.addRow(new Object[] {id, deliverableTitle, type, subType, startDate, endDate, crossCutting,
        deliverableOutputs, supportingDocuments});
    }
    return model;
  }

  private TypedTableModel getDescriptionTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"title", "startDate", "endDate", "extensionDate", "principalInvestigator", "projectContact",
        "ocsCode", "type", "suggestedTitle", "descriptionObjectives", "originalDonor", "customer", "totalAmount",
        "globalDimension", "crossCutting", "projectOutputs", "regionalDimension"},
      new Class[] {String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class});

    String title = null;
    if (project.getName() != null && !project.getName().isEmpty()) {
      title = project.getName();
    }

    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    String startDate = null;

    if (project.getStartDate() != null) {
      startDate = formatter.format(project.getStartDate());
    }
    String endDate = null;
    if (project.getEndDate() != null) {
      endDate = formatter.format(project.getEndDate());
    }
    String extensionDate = null;
    // if (project.getExtensionDate() != null) {
    // extensionDate = formatter.format(project.getExtensionDate());
    // }
    String principalInvestigator = null;

    if (project.getResearchProgram().getResearchLeaders() != null) {
      principalInvestigator = project.getResearchProgram().getResearchLeaders().stream().collect(Collectors.toList())
        .get(0).getUser().getComposedName();
    }

    String projectContact = null;
    if (project.getProjectLeader() != null) {
      projectContact = project.getProjectLeader().getComposedName();
    }

    String ocsCode = null;
    // if (project.getOcsCode() != null && !project.getOcsCode().trim().isEmpty()) {
    // ocsCode = project.getOcsCode();
    // }

    String type = null;

    String suggestedTitle = null;
    if (project.getSuggestedName() != null && !project.getSuggestedName().trim().isEmpty()) {
      suggestedTitle = project.getSuggestedName();
    }

    String descriptionObjectives = null;
    if (project.getDescription() != null && !project.getDescription().trim().isEmpty()) {
      descriptionObjectives = project.getDescription();
    }

    String originalDonor = null;
    // if (project.getOriginalDonor() != null && !project.getOriginalDonor().trim().isEmpty()) {
    // originalDonor = project.getOriginalDonor();
    // }

    String customer = null;
    // if (project.getDirectDonor() != null && !project.getDirectDonor().trim().isEmpty()) {
    // customer = project.getDirectDonor();
    // }

    Double totalAmount = null;
    // if (project.getTotalAmount() != null) {
    // totalAmount = project.getTotalAmount();
    // }
    String globalDimension = null;
    if (project.getGlobal() != null && project.getGlobal()) {
      globalDimension = "&#9679 Yes";
    } else if (project.getGlobal() != null && !project.getGlobal()) {
      globalDimension = "";
      for (CenterProjectLocation projectLocation : project.getProjectLocations().stream()
        .filter(pl -> pl.isActive() && pl.getLocElement().getLocElementType().getId() == 2)
        .collect(Collectors.toList())) {
        if (globalDimension.isEmpty()) {
          globalDimension += "&#9679 " + projectLocation.getLocElement().getName();
        } else {
          globalDimension += "<br>&#9679 " + projectLocation.getLocElement().getName();
        }
      }
    }
    String regionalDimension = null;
    if (project.getRegion() != null && project.getRegion()) {
      regionalDimension = "";
      for (CenterProjectLocation projectLocation : project.getProjectLocations().stream()
        .filter(pl -> pl.isActive() && pl.getLocElement().getLocElementType().getId() == 1)
        .collect(Collectors.toList())) {
        if (regionalDimension.isEmpty()) {
          regionalDimension += "&#9679 " + projectLocation.getLocElement().getName();
        } else {
          regionalDimension += "<br>&#9679 " + projectLocation.getLocElement().getName();
        }
      }
    } else if (project.getRegion() != null && !project.getRegion()) {
      regionalDimension = "&#9679 No";
    }

    String crossCutting = "";
    if (project.getProjectCrosscutingTheme() != null) {
      if (project.getProjectCrosscutingTheme().getClimateChange() != null
        && project.getProjectCrosscutingTheme().getClimateChange()) {
        crossCutting += "&#9679 Climate Change";
      }
      if (project.getProjectCrosscutingTheme().getGender() != null
        && project.getProjectCrosscutingTheme().getGender()) {
        if (crossCutting.isEmpty()) {
          crossCutting += "&#9679 Gender";
        } else {
          crossCutting += "<br>&#9679 Gender";
        }
      }
      if (project.getProjectCrosscutingTheme().getYouth() != null && project.getProjectCrosscutingTheme().getYouth()) {
        if (crossCutting.isEmpty()) {
          crossCutting += "&#9679 Youth";
        } else {
          crossCutting += "<br>&#9679 Youth";
        }
      }
      if (project.getProjectCrosscutingTheme().getPoliciesInstitutions() != null
        && project.getProjectCrosscutingTheme().getPoliciesInstitutions()) {
        if (crossCutting.isEmpty()) {
          crossCutting += "&#9679 Policies and Institutions";
        } else {
          crossCutting += "<br>&#9679 Policies and Institutions";
        }
      }
      if (project.getProjectCrosscutingTheme().getCapacityDevelopment() != null
        && project.getProjectCrosscutingTheme().getCapacityDevelopment()) {
        if (crossCutting.isEmpty()) {
          crossCutting += "&#9679 Capacity Development";
        } else {
          crossCutting += "<br>&#9679 Capacity Development";
        }
      }
      if (project.getProjectCrosscutingTheme().getBigData() != null
        && project.getProjectCrosscutingTheme().getBigData()) {
        if (crossCutting.isEmpty()) {
          crossCutting += "&#9679 Big Data";
        } else {
          crossCutting += "<br>&#9679 Big Data";
        }
      }
      if (project.getProjectCrosscutingTheme().getImpactAssessment() != null
        && project.getProjectCrosscutingTheme().getImpactAssessment()) {
        if (crossCutting.isEmpty()) {
          crossCutting += "&#9679 Impact Assessment";
        } else {
          crossCutting += "<br>&#9679 Impact Assessment";
        }
      }
      if (project.getProjectCrosscutingTheme().getNa() != null && project.getProjectCrosscutingTheme().getNa()) {
        if (crossCutting.isEmpty()) {
          crossCutting += "&#9679 N/A";
        } else {
          crossCutting += "<br>&#9679 N/A";
        }
      }
    }
    if (crossCutting != null && crossCutting.trim().isEmpty()) {
      crossCutting = null;
    }

    String projectOutputs = "";
    for (CenterProjectOutput projectOutput : project.getProjectOutputs().stream().filter(po -> po.isActive())
      .collect(Collectors.toList())) {
      if (projectOutputs.isEmpty()) {
        projectOutputs = "&#9679 " + projectOutput.getResearchOutput().getShortName();
      } else {
        projectOutputs += "<br>&#9679 " + projectOutput.getResearchOutput().getShortName();
      }
    }
    if (projectOutputs.trim().isEmpty()) {
      projectOutputs = null;
    }

    model.addRow(new Object[] {title, startDate, endDate, extensionDate, principalInvestigator, projectContact, ocsCode,
      type, suggestedTitle, descriptionObjectives, originalDonor, customer, totalAmount, globalDimension, crossCutting,
      projectOutputs, regionalDimension});
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
    fileName.append("FullProjectReportSummary-CIAT" + "-");
    fileName.append("P" + projectID + "-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();
  }

  private void getFooterSubreports(HashMap<String, Element> hm, ReportFooter reportFooter) {

    int elementCount = reportFooter.getElementCount();
    for (int i = 0; i < elementCount; i++) {
      Element e = reportFooter.getElement(i);
      if (e instanceof SubReport) {
        hm.put(e.getName(), e);
        if (((SubReport) e).getElementCount() != 0) {
          this.getAllSubreports(hm, ((SubReport) e).getItemBand());

        }
      }
      if (e instanceof Band) {
        this.getBandSubreports(hm, (Band) e);
      }
    }
  }

  private TypedTableModel getFundingSourcesTableModel() {
    TypedTableModel model = new TypedTableModel(new String[] {"crp", "fundingSource", "projectTitle"},
      new Class[] {String.class, String.class, String.class});
    for (CenterProjectFundingSource projectFundingSource : project.getProjectFundingSources().stream()
      .filter(fs -> fs.isActive()).collect(Collectors.toList())) {
      String crp = null;

      if (projectFundingSource.getCrp() != null) {
        crp = projectFundingSource.getCrp().getName();
      }
      String fundingSource = null;
      // if (projectFundingSource.getFundingSourceType() != null) {
      // fundingSource = projectFundingSource.getFundingSourceType().getName();
      // }
      String projectTitle = null;
      if (projectFundingSource.getTitle() != null && !projectFundingSource.getTitle().trim().isEmpty()) {
        projectTitle = projectFundingSource.getTitle();
      }

      model.addRow(new Object[] {crp, fundingSource, projectTitle});
    }
    return model;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new ByteArrayInputStream(bytesPDF);
    }
    return inputStream;
  }

  /**
   * Get the main information of the report
   * 
   * @return
   */
  private TypedTableModel getMasterTableModel() {
    // Initialization of Model
    TypedTableModel model =
      new TypedTableModel(new String[] {"shortTitle", "currentDate", "projectSubmission", "imageUrl"},
        new Class[] {String.class, String.class, String.class, String.class});
    // Set short title
    String shortTitle = "";
    if (project.getName() != null && !project.getName().isEmpty()) {
      shortTitle += project.getName() + " - ";
    }
    if (loggedCenter.getAcronym() != null && !loggedCenter.getAcronym().isEmpty()) {
      shortTitle += loggedCenter.getAcronym() + " - ";
    }
    shortTitle += "P" + Long.toString(projectID);

    // Set currentDate
    String currentDate = "";
    // Get datetime
    ZonedDateTime timezone = ZonedDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-d 'at' HH:mm ");
    currentDate = timezone.format(format) + this.getTimeZone();

    // Set projectSubmission
    String projectSubmission = "";

    // set CIAT imgage URL from repo
    String imageUrl = this.getBaseUrl() + "/global/images/centers/CIAT.png";

    model.addRow(new Object[] {shortTitle, currentDate, projectSubmission, imageUrl});
    return model;
  }

  private TypedTableModel getPartnersTableModel() {
    TypedTableModel model =
      new TypedTableModel(new String[] {"partnerName", "partnerType", "institution_id", "project_id"},
        new Class[] {String.class, String.class, Long.class, Long.class});

    for (CenterProjectPartner projectPartner : project.getProjectPartners().stream().filter(pp -> pp.isActive())
      .collect(Collectors.toList())) {
      String partnerName = projectPartner.getInstitution().getComposedName();
      Long institution_id = projectPartner.getInstitution().getId();
      String partnerType = null;
      // if (projectPartner.isInternal()) {
      // partnerType = "Internal";
      // }
      // if (!projectPartner.isInternal()) {
      // partnerType = "External";
      // }
      model.addRow(new Object[] {partnerName, partnerType, institution_id, project.getId()});
    }
    return model;
  }

  @Override
  public void prepare() {
    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (Exception e) {
      projectID = -1;
      LOG.error("Failed to get parameter" + APConstants.PROJECT_ID + ". Exception: " + e.getMessage());
      throw e;
    }
    try {
      project = projectService.getCenterProjectById(projectID);
    } catch (Exception e) {
      LOG.error("Failed to get project from Database. Exception: " + e.getMessage());
      throw e;
    }
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info(
      "Start report download: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName());
  }

  public void setBytesPDF(byte[] bytesPDF) {
    this.bytesPDF = bytesPDF;
  }

}