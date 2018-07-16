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
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.FileManager;
import org.cgiar.ccafs.marlo.utils.HTMLParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
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
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class ProjectHighlightSummaryAction extends BaseSummariesAction implements Summary {

  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectHighlightSummaryAction.class);
  // Managers
  private final ProjectHighligthManager projectHighLightManager;
  private final ResourceManager resourceManager;
  private final HTMLParser HTMLParser;
  // Parameters
  private long startTime;
  private Long projectHighlightID;
  private ProjectHighlightInfo projectHighlightInfo;
  // XLSX bytes
  private byte[] bytesPDF;
  // Streams
  InputStream inputStream;

  @Inject
  public ProjectHighlightSummaryAction(APConfig config, GlobalUnitManager crpManager,
    ProjectHighligthManager projectHighLightManager, PhaseManager phaseManager, ResourceManager resourceManager,
    HTMLParser HTMLParser, ProjectManager projectManager) {
    super(config, crpManager, phaseManager, projectManager);
    this.projectHighLightManager = projectHighLightManager;
    this.resourceManager = resourceManager;
    this.HTMLParser = HTMLParser;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nMainTitle", this.getText("projectHighlight.singular"));
    masterReport.getParameterValues().put("i8nNoData", this.getText("projectHighlight.noData"));
    masterReport.getParameterValues().put("i8nSingular", this.getText("projectHighlight.singular"));
    masterReport.getParameterValues().put("i8nTitle", this.getText("highlight.title"));
    masterReport.getParameterValues().put("i8nAuthor", this.getText("highlight.author"));
    masterReport.getParameterValues().put("i8nSubject", this.getText("highlight.subject"));
    masterReport.getParameterValues().put("i8nPublisher", this.getText("highlight.publisher"));
    masterReport.getParameterValues().put("i8nYear", this.getText("highlight.year"));
    masterReport.getParameterValues().put("i8nTypes", this.getText("highlight.types"));
    masterReport.getParameterValues().put("i8nIsGlobal", this.getText("highlight.isGlobal.readText"));
    masterReport.getParameterValues().put("i8nStartDate", this.getText("highlight.startDate"));
    masterReport.getParameterValues().put("i8nEndDate", this.getText("highlight.endDate"));
    masterReport.getParameterValues().put("i8nProjectId", this.getText("summaries.oaprojects.projectTitle"));
    masterReport.getParameterValues().put("i8nCountries", this.getText("highlight.countries"));
    masterReport.getParameterValues().put("i8nKeywords", this.getText("highlight.keywords"));
    masterReport.getParameterValues().put("i8nDescripition", this.getText("highlight.descripition.readText"));
    masterReport.getParameterValues().put("i8nObjectives", this.getText("highlight.objectives.readText"));
    masterReport.getParameterValues().put("i8nResults", this.getText("highlight.results.readText"));
    masterReport.getParameterValues().put("i8nPartners", this.getText("highlight.partners.readText"));
    masterReport.getParameterValues().put("i8nLinks", this.getText("highlight.links.readText"));
    masterReport.getParameterValues().put("i8nProjectHighlightsId",
      this.getText("projectHighlights.projectHighlightsId"));
    masterReport.getParameterValues().put("i8nImage", this.getText("highlight.image.readText"));
    return masterReport;
  }


  @Override
  public String execute() throws Exception {
    if (projectHighlightID == null || projectHighLightManager.getProjectHighligthById(projectHighlightID) == null
      || projectHighLightManager.getProjectHighligthById(projectHighlightID)
        .getProjectHighlightInfo(this.getSelectedPhase()) == null) {
      LOG.error("Project Highlight " + projectHighlightID + " Not found");
      return NOT_FOUND;
    } else {
      projectHighlightInfo = projectHighLightManager.getProjectHighligthById(projectHighlightID)
        .getProjectHighlightInfo(this.getSelectedPhase());
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      Resource reportResource = resourceManager
        .createDirectly(this.getClass().getResource("/pentaho/crp/ProjectHighlightPDF.prpt"), MasterReport.class);
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
      this.fillSubreport((SubReport) hm.get("project_highlight"), "project_highlight");
      PdfReportUtil.createPDF(masterReport, os);
      bytesPDF = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ProjectHighlights Summary: " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info("Downloaded successfully: " + this.getFileName() + ". User: " + this.getDownloadByUser() + ". CRP: "
      + this.getLoggedCrp().getAcronym() + ". Time to generate: " + stopTime + "ms.");
    return SUCCESS;
  }

  private void fillSubreport(SubReport subReport, String query) {
    CompoundDataFactory cdf = CompoundDataFactory.normalize(subReport.getDataFactory());
    TableDataFactory sdf = (TableDataFactory) cdf.getDataFactoryForQuery(query);
    TypedTableModel model = null;
    switch (query) {
      case "project_highlight":
        model = this.getProjectHighligthsTableModel();
        break;
    }
    sdf.addTable(query, model);
    subReport.setDataFactory(cdf);
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
    fileName.append("ProjectHighlightSummary-");
    fileName.append(this.getLoggedCrp().getAcronym() + "-");
    try {
      if (projectHighlightInfo != null && projectHighlightInfo.getProjectHighlight().getProject() != null) {
        fileName.append(projectHighlightInfo.getProjectHighlight().getProject()
          .getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER) + "-");
      }
    } catch (Exception e) {
      LOG.info("Error getting project for highlight: " + projectHighlightID);
    }

    fileName.append("H" + projectHighlightID + "-");
    fileName.append(this.getSelectedYear() + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();
  }

  public String getHighlightsImagesUrl(String projectId) {
    return config.getDownloadURL() + "/" + this.getHighlightsImagesUrlPath(projectId).replace('\\', '/');
  }

  public String getHighlightsImagesUrlPath(long projectID) {
    return config.getProjectsBaseFolder(this.getLoggedCrp().getAcronym()) + File.separator + projectID + File.separator
      + "hightlightsImage" + File.separator;
  }

  public String getHighlightsImagesUrlPath(String projectId) {
    return config.getProjectsBaseFolder(this.getLoggedCrp().getAcronym()) + File.separator + projectId + File.separator
      + "hightlightsImage" + File.separator;
  }

  private String getHightlightImagePath(long projectID) {
    return config.getUploadsBaseFolder() + File.separator + this.getHighlightsImagesUrlPath(projectID) + File.separator;
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
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year", "id", "cycle"},
      new Class[] {String.class, String.class, String.class, Long.class, String.class});
    model.addRow(new Object[] {center, date, year, projectHighlightID, this.getSelectedCycle()});
    return model;
  }

  private TypedTableModel getProjectHighligthsTableModel() {
    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "author", "subject", "publisher", "year_reported", "highlights_types",
        "highlights_is_global", "start_date", "end_date", "keywords", "countries", "image", "highlight_desc",
        "introduction", "results", "partners", "links", "width", "heigth", "project_id", "imageurl", "imageName",
        "phaseID"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, Integer.class, Integer.class, String.class, String.class,
        String.class, Long.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    String title = null, author = null, subject = null, publisher = null, highlightsTypes = "",
      highlightsIsGlobal = null, startDate = null, endDate = null, keywords = null, countries = "",
      highlightDesc = null, introduction = null, results = null, partners = null, links = null, project = null,
      image = "", imageurl = null, imageName = null;
    Long yearReported = null;
    int width = 244;
    int heigth = 163;
    if (projectHighlightInfo.getTitle() != null && !projectHighlightInfo.getTitle().isEmpty()) {
      title = projectHighlightInfo.getTitle();
    }
    if (projectHighlightInfo.getAuthor() != null && !projectHighlightInfo.getAuthor().isEmpty()) {
      author = projectHighlightInfo.getAuthor();
    }
    if (projectHighlightInfo.getSubject() != null && !projectHighlightInfo.getSubject().isEmpty()) {
      subject = projectHighlightInfo.getSubject();
    }
    if (projectHighlightInfo.getPublisher() != null && !projectHighlightInfo.getPublisher().isEmpty()) {
      publisher = projectHighlightInfo.getPublisher();
    }
    if (projectHighlightInfo.getYear() != null) {
      yearReported = projectHighlightInfo.getYear();
    }
    for (ProjectHighlightType projectHighlightType : projectHighlightInfo.getProjectHighlight()
      .getProjectHighligthsTypes().stream().filter(pht -> pht.isActive()).collect(Collectors.toList())) {
      if (ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "") != null) {
        highlightsTypes +=
          "<br>● " + ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "").getDescription();
      }
    }
    if (highlightsTypes.isEmpty()) {
      highlightsTypes = null;
    }
    if (projectHighlightInfo.isGlobal() == true) {
      highlightsIsGlobal = "Yes";
    } else {
      highlightsIsGlobal = "No";
    }
    if (projectHighlightInfo.getStartDate() != null) {
      startDate = formatter.format(projectHighlightInfo.getStartDate());
    }
    if (projectHighlightInfo.getEndDate() != null) {
      endDate = formatter.format(projectHighlightInfo.getEndDate());
    }
    if (projectHighlightInfo.getKeywords() != null && !projectHighlightInfo.getKeywords().isEmpty()) {
      keywords = projectHighlightInfo.getKeywords();
    }
    int countriesFlag = 0;
    for (ProjectHighlightCountry projectHighlightCountry : projectHighlightInfo.getProjectHighlight()
      .getProjectHighlightCountries().stream().filter(phc -> phc.isActive()).collect(Collectors.toList())) {

      if (projectHighlightCountry.getLocElement() != null) {
        if (countriesFlag == 0) {
          countries += projectHighlightCountry.getLocElement().getName();
          countriesFlag++;
        } else {
          countries += ", " + projectHighlightCountry.getLocElement().getName();
          countriesFlag++;
        }
      }
    }
    if (countries.isEmpty()) {
      countries = null;
    }
    if (projectHighlightInfo.getDescription() != null && !projectHighlightInfo.getDescription().isEmpty()) {
      highlightDesc = HTMLParser.plainTextToHtml(projectHighlightInfo.getDescription());
    }
    if (projectHighlightInfo.getObjectives() != null && !projectHighlightInfo.getObjectives().isEmpty()) {
      introduction = HTMLParser.plainTextToHtml(projectHighlightInfo.getObjectives());
    }
    if (projectHighlightInfo.getResults() != null && !projectHighlightInfo.getResults().isEmpty()) {
      results = HTMLParser.plainTextToHtml(projectHighlightInfo.getResults());
    }
    if (projectHighlightInfo.getPartners() != null && !projectHighlightInfo.getPartners().isEmpty()) {
      partners = HTMLParser.plainTextToHtml(projectHighlightInfo.getPartners());
    }
    if (projectHighlightInfo.getLinks() != null && !projectHighlightInfo.getLinks().isEmpty()) {
      links = HTMLParser.plainTextToHtml(projectHighlightInfo.getLinks());
    }
    if (projectHighlightInfo.getProjectHighlight().getProject() != null
      && projectHighlightInfo.getProjectHighlight().getProject().getProjecInfoPhase(this.getSelectedPhase()) != null) {
      project = projectHighlightInfo.getProjectHighlight().getProject().getComposedName();
    }
    if (projectHighlightInfo.getFile() != null) {
      double pageWidth = 612 * 0.4;
      double pageHeigth = 792 * 0.4;
      double imageWidth = 244;
      double imageHeigth = 163;
      image = this.getHighlightsImagesUrl(projectHighlightInfo.getProjectHighlight().getProject().getId().toString())
        + projectHighlightInfo.getFile().getFileName();
      imageurl = this.getHighlightsImagesUrl(projectHighlightInfo.getProjectHighlight().getProject().getId().toString())
        + projectHighlightInfo.getFile().getFileName();
      imageName = projectHighlightInfo.getFile().getFileName();
      Image imageFile = null;
      LOG.info("image.getURL.replace " + image);
      File url;
      try {
        url = new File(image);
      } catch (Exception e) {
        LOG.warn("Failed to get image File. Url was set to null. Exception: " + e.getMessage());
        url = null;
        image = "";
        imageurl = null;
        imageName = null;
      }
      if (url != null && url.exists()) {
        try {
          imageFile = Image.getInstance(FileManager.readURL(url));
          // System.out.println("W: " + imageFile.getWidth() + " \nH: " + imageFile.getHeight());
          if (imageFile.getWidth() >= imageFile.getHeight()) {
            imageWidth = pageWidth;
            imageHeigth = imageFile.getHeight() * (((pageWidth * 100) / imageFile.getWidth()) / 100);
          } else {
            imageHeigth = pageHeigth;
            imageWidth = imageFile.getWidth() * (((pageHeigth * 100) / imageFile.getHeight()) / 100);
          }
          // System.out.println("New W: " + imageWidth + " \nH: " + imageHeigth);
          width = (int) imageWidth;
          heigth = (int) imageHeigth;
          // If successful, process the message
        } catch (BadElementException e) {
          LOG.warn("BadElementException getting image: " + e.getMessage());
          image = "";
          imageurl = null;
          imageName = null;
        } catch (MalformedURLException e) {
          LOG.warn("MalformedURLException getting image: " + e.getMessage());
          image = "";
          imageurl = null;
          imageName = null;
        } catch (IOException e) {
          LOG.warn("IOException getting image: " + e.getMessage());
          image = "";
          imageurl = null;
          imageName = null;
        }
      } else {
        image = "";
        imageurl = null;
        imageName = null;
      }
    }
    Long phaseID = this.getSelectedPhase().getId();
    model.addRow(new Object[] {projectHighlightID, title, author, subject, publisher, yearReported, highlightsTypes,
      highlightsIsGlobal, startDate, endDate, keywords, countries, image, highlightDesc, introduction, results,
      partners, links, width, heigth, project, imageurl, imageName, phaseID});
    return model;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    this.setGeneralParameters();
    projectHighlightID =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.HIGHLIGHT_REQUEST_ID).getMultipleValues()[0]));
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