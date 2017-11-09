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

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;
import org.cgiar.ccafs.marlo.data.model.ProjectHighligthsTypeEnum;
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
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectHighlightsExcelSummaryAction extends BaseAction implements Summary {


  private static final long serialVersionUID = 1L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectHighlightsExcelSummaryAction.class);
  // Managers
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private ProjectHighligthManager projectHighLightManager;
  // XLSX bytes
  private byte[] bytesXLSX;
  // Streams
  InputStream inputStream;
  // Parameters
  private int year;
  private long startTime;
  private GlobalUnit loggedCrp;

  @Inject
  public ProjectHighlightsExcelSummaryAction(APConfig config, GlobalUnitManager crpManager,
    ProjectHighligthManager projectHighLightManager) {
    super(config);
    this.crpManager = crpManager;
    this.projectHighLightManager = projectHighLightManager;
  }

  /**
   * Method to add i8n parameters to masterReport in Pentaho
   * 
   * @param masterReport
   * @return masterReport with i8n parameters added
   */
  private MasterReport addi8nParameters(MasterReport masterReport) {
    masterReport.getParameterValues().put("i8nMainTitle", this.getText("projectHighlights.title"));
    masterReport.getParameterValues().put("i8nProjectHighlightsId",
      this.getText("projectHighlights.projectHighlightsId"));
    masterReport.getParameterValues().put("i8nTitle", this.getText("highlight.title"));
    masterReport.getParameterValues().put("i8nAuthor", this.getText("highlight.author"));
    masterReport.getParameterValues().put("i8nSubject", this.getText("highlight.subject"));
    masterReport.getParameterValues().put("i8nPublisher", this.getText("highlight.publisher"));
    masterReport.getParameterValues().put("i8nYear", this.getText("highlight.year"));
    masterReport.getParameterValues().put("i8nTypes", this.getText("highlight.types"));
    masterReport.getParameterValues().put("i8nIsGlobal", this.getText("highlight.isGlobal.readText"));
    masterReport.getParameterValues().put("i8nStartDate", this.getText("highlight.startDate"));
    masterReport.getParameterValues().put("i8nEndDate", this.getText("highlight.endDate"));
    masterReport.getParameterValues().put("i8nImage", this.getText("highlight.image.readText"));
    masterReport.getParameterValues().put("i8nKeywords", this.getText("highlight.keywords"));
    masterReport.getParameterValues().put("i8nCountries", this.getText("highlight.countries"));
    masterReport.getParameterValues().put("i8nDescripition", this.getText("highlight.descripition.readText"));
    masterReport.getParameterValues().put("i8nObjectives", this.getText("highlight.objectives.readText"));
    masterReport.getParameterValues().put("i8nResults", this.getText("highlight.results.readText"));
    masterReport.getParameterValues().put("i8nPartners", this.getText("highlight.partners.readText"));
    masterReport.getParameterValues().put("i8nLinks", this.getText("highlight.links.readText"));
    masterReport.getParameterValues().put("i8nProjectId", this.getText("searchTerms.projectId"));

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
        manager.createDirectly(this.getClass().getResource("/pentaho/projectHighlightsExcel.prpt"), MasterReport.class);
      MasterReport masterReport = (MasterReport) reportResource.getResource();
      String center = loggedCrp.getAcronym();
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
      TypedTableModel model = this.getMasterTableModel(center, date, String.valueOf(year));
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
      ExcelReportUtil.createXLSX(masterReport, os);
      bytesXLSX = os.toByteArray();
      os.close();
    } catch (Exception e) {
      LOG.error("Error generating ProjectHighlightsExcel " + e.getMessage());
      throw e;
    }
    // Calculate time of generation
    long stopTime = System.currentTimeMillis();
    stopTime = stopTime - startTime;
    LOG.info(
      "Downloaded successfully: " + this.getFileName() + ". User: " + this.getCurrentUser().getComposedCompleteName()
        + ". CRP: " + this.loggedCrp.getAcronym() + ". Time to generate: " + stopTime + "ms.");
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
    fileName.append("ProjectHighlightsSummary-");
    fileName.append(this.year + "_");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".xlsx");
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
    TypedTableModel model = new TypedTableModel(new String[] {"center", "date", "year"},
      new Class[] {String.class, String.class, String.class});
    model.addRow(new Object[] {center, date, year});
    return model;
  }

  private TypedTableModel getProjectHighligthsTableModel() {


    TypedTableModel model = new TypedTableModel(
      new String[] {"id", "title", "author", "subject", "publisher", "year_reported", "highlights_types",
        "highlights_is_global", "start_date", "end_date", "keywords", "countries", "highlight_desc", "introduction",
        "results", "partners", "links", "project_id", "image", "imageurl"},
      new Class[] {Long.class, String.class, String.class, String.class, String.class, Long.class, String.class,
        String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class},
      0);
    SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");
    for (ProjectHighlight projectHighlight : projectHighLightManager.findAll().stream()
      .sorted((h1, h2) -> Long.compare(h1.getId(), h2.getId()))
      .filter(ph -> ph.isActive() && ph.getProject() != null && ph.getYear() == year
        && ph.getProject().getGlobalUnitProjects().stream()
          .filter(gup -> gup.isActive() && gup.getGlobalUnit().getId().equals(this.loggedCrp.getId()))
          .collect(Collectors.toList()).size() > 0
        && ph.getProject().isActive() && ph.getProject().getReporting() && ph.getProject().getId() == 2)
      .collect(Collectors.toList())) {
      String title = null, author = null, subject = null, publisher = null, highlightsTypes = "",
        highlightsIsGlobal = null, startDate = null, endDate = null, keywords = null, countries = "",
        highlightDesc = null, introduction = null, results = null, partners = null, links = null, projectId = null,
        image = null, imageurl = null;
      Long yearReported = null;
      if (projectHighlight.getTitle() != null && !projectHighlight.getTitle().isEmpty()) {
        title = projectHighlight.getTitle();
      }
      if (projectHighlight.getAuthor() != null && !projectHighlight.getAuthor().isEmpty()) {
        author = projectHighlight.getAuthor();
      }
      if (projectHighlight.getSubject() != null && !projectHighlight.getSubject().isEmpty()) {
        subject = projectHighlight.getSubject();
      }
      if (projectHighlight.getPublisher() != null && !projectHighlight.getPublisher().isEmpty()) {
        publisher = projectHighlight.getPublisher();
      }
      if (projectHighlight.getYear() != null) {
        yearReported = projectHighlight.getYear();
      }
      for (ProjectHighlightType projectHighlightType : projectHighlight.getProjectHighligthsTypes().stream()
        .filter(pht -> pht.isActive()).collect(Collectors.toList())) {
        if (ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "") != null) {
          highlightsTypes +=
            "\n● " + ProjectHighligthsTypeEnum.getEnum(projectHighlightType.getIdType() + "").getDescription();
        }
      }
      if (highlightsTypes.isEmpty()) {
        highlightsTypes = null;
      }
      if (projectHighlight.isGlobal() == true) {
        highlightsIsGlobal = "Yes";
      } else {
        highlightsIsGlobal = "No";
      }
      if (projectHighlight.getStartDate() != null) {
        startDate = formatter.format(projectHighlight.getStartDate());
      }
      if (projectHighlight.getEndDate() != null) {
        endDate = formatter.format(projectHighlight.getEndDate());
      }
      if (projectHighlight.getKeywords() != null && !projectHighlight.getKeywords().isEmpty()) {
        keywords = projectHighlight.getKeywords();
      }
      int countriesFlag = 0;
      for (ProjectHighlightCountry projectHighlightCountry : projectHighlight.getProjectHighligthCountries().stream()
        .filter(phc -> phc.isActive()).collect(Collectors.toList())) {

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
      if (projectHighlight.getDescription() != null && !projectHighlight.getDescription().isEmpty()) {
        highlightDesc = projectHighlight.getDescription();
      }
      if (projectHighlight.getObjectives() != null && !projectHighlight.getObjectives().isEmpty()) {
        introduction = projectHighlight.getObjectives();
      }
      if (projectHighlight.getResults() != null && !projectHighlight.getResults().isEmpty()) {
        results = projectHighlight.getResults();
      }
      if (projectHighlight.getPartners() != null && !projectHighlight.getPartners().isEmpty()) {
        partners = projectHighlight.getPartners();
      }
      if (projectHighlight.getLinks() != null && !projectHighlight.getLinks().isEmpty()) {
        links = projectHighlight.getLinks();
      }
      if (projectHighlight.getProject() != null) {
        projectId = projectHighlight.getProject().getId().toString();
      }
      if (projectHighlight.getFile() != null) {
        image = projectHighlight.getFile().getFileName();
        imageurl = this.getHighlightsImagesUrl(projectHighlight.getProject().getId().toString())
          + projectHighlight.getFile().getFileName();
        File url;
        try {
          url = new File(imageurl);
        } catch (Exception e) {
          LOG.warn("Failed to get image File. Url was set to null. Exception: " + e.getMessage());
          url = null;
          imageurl = null;
          image = null;
        }
        if (url != null && url.exists()) {

        } else {
          imageurl = null;
          image = null;
        }
      }
      model.addRow(new Object[] {projectHighlight.getId(), title, author, subject, publisher, yearReported,
        highlightsTypes, highlightsIsGlobal, startDate, endDate, keywords, countries, highlightDesc, introduction,
        results, partners, links, projectId, image, imageurl});
    }
    return model;
  }

  @Override
  public void prepare() throws Exception {
    // Get loggerCrp
    try {
      loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    } catch (Exception e) {
      LOG.error("Failed to get " + APConstants.SESSION_CRP + " parameter. Exception: " + e.getMessage());
    }
    // Get parameters from URL
    // Get year
    try {
      Map<String, Object> parameters = this.getParameters();
      year = Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0])));
    } catch (Exception e) {
      LOG.warn("Failed to get " + APConstants.YEAR_REQUEST
        + " parameter. Parameter will be set as CurrentCycleYear. Exception: " + e.getMessage());
      year = this.getCurrentCycleYear();
    }
    // Calculate time to generate report
    startTime = System.currentTimeMillis();
    LOG.info("Start report download: " + this.getFileName() + ". User: "
      + this.getCurrentUser().getComposedCompleteName() + ". CRP: " + this.loggedCrp.getAcronym());
  }

  public void setBytesXLSX(byte[] bytesXLSX) {
    this.bytesXLSX = bytesXLSX;
  }

  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }


}